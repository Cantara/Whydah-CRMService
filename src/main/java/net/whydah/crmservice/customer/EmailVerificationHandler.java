package net.whydah.crmservice.customer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.security.Authentication;
import net.whydah.crmservice.util.TokenServiceClient;
import net.whydah.sso.extensions.crmcustomer.types.Customer;
import net.whydah.sso.extensions.crmcustomer.types.EmailAddress;
import net.whydah.sso.extensions.crmcustomer.types.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.form.Form;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.util.Map;

import static ratpack.jackson.Jackson.json;

@Singleton
public class EmailVerificationHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationHandler.class);

    private final CustomerRepository customerRepository;
    private final TokenServiceClient tokenServiceClient;

    @Inject
    public EmailVerificationHandler(CustomerRepository customerRepository, TokenServiceClient tokenServiceClient) {
        this.customerRepository = customerRepository;
        this.tokenServiceClient = tokenServiceClient;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
            log.debug("User {} with personRef {} not authorized to get data for personRef {}", Authentication.getAuthenticatedUser().getUid(), Authentication.getAuthenticatedUser().getPersonRef(), customerRef);
            ctx.clientError(401);
            return;
        }

        ctx.parse(new TypeToken<Form>() {
        }).then(form -> {

            String emailaddress = form.get("emailaddress");
            String token = form.get("token");
            Blocking.get(() -> tokenServiceClient.verifyEmailAddressToken(emailaddress, token)).then(verified -> {
                if (verified) {
                    //Update data
                    Customer customer = customerRepository.getCustomer(customerRef);
                    Map<String, EmailAddress> emailaddresses = customer.getEmailaddresses();

                    boolean foundMatch = false;
                    for (EmailAddress email : emailaddresses.values()) {
                        if (emailaddress.equalsIgnoreCase(email.getEmailaddress())) {
                            email.setVerified(true);
                            foundMatch = true;
                        }
                    }
                    if (foundMatch) {
                        customerRepository.updateCustomer(customerRef, customer);
                        ctx.redirect(200, customerRef);
                    } else {
                        ctx.clientError(400); //Bad request
                    }
                } else {
                    ctx.clientError(401); //Unauthorized
                }
            });
        });
    }
}
