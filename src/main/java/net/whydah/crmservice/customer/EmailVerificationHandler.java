package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.security.Authentication;
import net.whydah.crmservice.util.MailClient;
import net.whydah.crmservice.util.TokenServiceClient;
import net.whydah.sso.extensions.crmcustomer.types.EmailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EmailVerificationHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationHandler.class);

    private final CustomerRepository customerRepository;
    private final MailClient mailClient;
    private static HashMap<String, String> emailTokenMap;

    @Inject
    public EmailVerificationHandler(CustomerRepository customerRepository, MailClient mailClient) {
        this.customerRepository = customerRepository;
        this.mailClient = mailClient;
        emailTokenMap = new HashMap<>();
    }

    @Override
    public void handle(Context ctx) throws Exception {

        final String customerRef = ctx.getPathTokens().get("customerRef");

        if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
            log.debug("User {} with personRef {} not authorized to get data for personRef {}", Authentication.getAuthenticatedUser().getUid(), Authentication.getAuthenticatedUser().getPersonRef(), customerRef);
            ctx.clientError(401);
            return;
        }

        MultiValueMap<String, String> queryParams = ctx.getRequest().getQueryParams();
        if (queryParams == null) {
            ctx.clientError(400); //Bad request
            return;
        }

        final String email = queryParams.get("email");
        final String token = queryParams.get("token");
        final String linkurl = queryParams.get("linkurl");

        if (token == null) {
            //Send email verification token
            String generatedToken = UUID.randomUUID().toString();

            StringBuilder builder = new StringBuilder(linkurl).
                            append("?token=").append(generatedToken).
                            append("&email=").append(email);

            String verificationLink = builder.toString();

            log.debug("Verificationlink: " + verificationLink);

            mailClient.sendVerificationEmail(email, verificationLink);

            emailTokenMap.put(email, generatedToken);

            ctx.redirect(200, customerRef);
        } else {
            String expectedToken = emailTokenMap.get(email);

            final boolean verified = (expectedToken != null && expectedToken.equals(token));
            if (verified) {
                emailTokenMap.remove(email);

                Blocking.get(() -> customerRepository.getCustomer(customerRef)).then(customer -> {
                    Map<String, EmailAddress> emailaddresses = customer.getEmailaddresses();

                    boolean foundMatch = false;
                    for (EmailAddress emailAddress : emailaddresses.values()) {
                        if (email.equalsIgnoreCase(emailAddress.getEmailaddress())) {
                            emailAddress.setVerified(true);
                            foundMatch = true;
                        }
                    }
                    if (foundMatch) {
                        customerRepository.updateCustomer(customerRef, customer);
                        ctx.redirect(200, customerRef);
                        log.debug("Email {} flagged as verified.", email);
                    } else {
                        ctx.clientError(412); //Precondition failed
                        log.debug("Email {} NOT found for customerRef={}.", email, customerRef);
                        return;
                    }
                });

            } else {
                ctx.clientError(406); //Not acceptable
            }
        }
    }
}
