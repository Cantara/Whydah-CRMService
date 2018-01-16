package net.whydah.crmservice.verification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.customer.CustomerRepository;
import net.whydah.crmservice.security.Authentication;
import net.whydah.crmservice.util.MailClient;
import net.whydah.crmservice.util.SecurityTokenServiceClient;
import net.whydah.sso.extensions.crmcustomer.types.EmailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.util.MultiValueMap;

import java.util.Map;
import java.util.UUID;

@Singleton
public class EmailVerificationHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationHandler.class);

    private final SecurityTokenServiceClient tokenServiceClient;
    private final CustomerRepository customerRepository;
    private final MailClient mailClient;
    private final ActiveVerificationCache emailTokenMap;

    @Inject
    public EmailVerificationHandler(SecurityTokenServiceClient tokenServiceClient, CustomerRepository customerRepository, MailClient mailClient, ActiveVerificationCache emailTokenMap) {
        this.tokenServiceClient = tokenServiceClient;
        this.customerRepository = customerRepository;
        this.mailClient = mailClient;
        this.emailTokenMap = emailTokenMap;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        final String customerRef = ctx.getPathTokens().get("customerRef");

        if ("useradmin".equalsIgnoreCase(Authentication.getAuthenticatedUser().getUid().toString())) {
        } else if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
            log.debug("User {} with personRef {} not authorized to get data for personRef {}", Authentication.getAuthenticatedUser().getUid(), Authentication.getAuthenticatedUser().getPersonRef(), customerRef);
            ctx.clientError(401);
            return;
        }

        MultiValueMap<String, String> queryParams = ctx.getRequest().getQueryParams();
        if (queryParams == null || queryParams.get("email") == null) {
            ctx.clientError(400); //Bad request
            return;
        }

        final String email = queryParams.get("email");
        final String token = queryParams.get("token");
        final String linkurl = queryParams.get("linkurl");
        log.debug("Ready to send email verificationmail. email:{}, token:{}, linkurl:{} ", email, token, linkurl);

        if (token == null) {
            //Send email verification token
            String generatedToken = UUID.randomUUID().toString();

            StringBuilder builder = new StringBuilder(linkurl).
                            append("?token=").append(generatedToken).
                            append("&email=").append(email);

            String verificationLink = builder.toString();

            log.debug("Verificationlink: " + verificationLink);

            mailClient.sendVerificationEmailViaWhydah(tokenServiceClient, email, verificationLink);

            emailTokenMap.addToken(email, generatedToken);

            ctx.redirect(200, customerRef);
        } else {
            String expectedToken = emailTokenMap.useToken(email);

            final boolean verified = (expectedToken != null && expectedToken.equals(token));
            if (verified) {

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
