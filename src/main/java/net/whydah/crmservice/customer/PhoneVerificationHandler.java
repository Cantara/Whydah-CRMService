package net.whydah.crmservice.customer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.security.Authentication;
import net.whydah.crmservice.util.SmsGatewayClient;
import net.whydah.crmservice.util.TokenServiceClient;
import net.whydah.sso.commands.adminapi.user.CommandSendSMSToUser;
import net.whydah.sso.extensions.crmcustomer.types.Customer;
import net.whydah.sso.extensions.crmcustomer.types.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.form.Form;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.util.MultiValueMap;

import java.util.Map;
import java.util.WeakHashMap;

@Singleton
public class PhoneVerificationHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(PhoneVerificationHandler.class);

    private final CustomerRepository customerRepository;
    private final TokenServiceClient tokenServiceClient;
    private final SmsGatewayClient smsClient;
    private static java.util.Random generator = new java.util.Random();

    private static WeakHashMap<String, String> userpinmap;

    @Inject
    public PhoneVerificationHandler(CustomerRepository customerRepository, TokenServiceClient tokenServiceClient, SmsGatewayClient smsClient) {
        this.customerRepository = customerRepository;
        this.tokenServiceClient = tokenServiceClient;
        this.smsClient = smsClient;
        userpinmap = new WeakHashMap<>();
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

        final String phoneNo = queryParams.get("phoneNo");
        final String pin = queryParams.get("pin");

        if (pin == null) {
            //Send phone verification pin

            String cellNo = phoneNo;
            String generatedPin = generatePin();

            String response = new CommandSendSMSToUser(smsClient.getServiceUrl(), smsClient.getServiceAccount(),
                    smsClient.getUsername(), smsClient.getPassword(), smsClient.getQueryParam(), cellNo, generatedPin).execute();
            log.debug("Answer from smsgw: " + response);

            userpinmap.put(phoneNo, generatedPin);

            ctx.redirect(200, customerRef);

        } else {
            //Verify pin against expected data

            String expectedPin = userpinmap.get(phoneNo);

            final boolean verified = (expectedPin != null && expectedPin.equals(pin));

            if (verified) {
                userpinmap.remove(phoneNo);

                Blocking.get(() -> customerRepository.getCustomer(customerRef)).then(customer -> {

                    Map<String, PhoneNumber> phonenumbers = customer.getPhonenumbers();

                    boolean foundMatch = false;
                    for (PhoneNumber phoneNumber : phonenumbers.values()) {
                        if (phoneNo.equals(phoneNumber.getPhonenumber())) {
                            phoneNumber.setVerified(true);
                            foundMatch = true;
                        }
                    }
                    if (foundMatch) {
                        customerRepository.updateCustomer(customerRef, customer);
                        ctx.redirect(200, customerRef);
                        log.debug("Phone {} flagged as verified.", phoneNo);
                    } else {
                        ctx.clientError(412); //Precondition failed
                        log.debug("Phone {} NOT found for customerRef={}.", phoneNo, customerRef);
                        return;
                    }
                });

            } else {
                ctx.clientError(406); //Not acceptable
            }
        }
    }

    private String generatePin() {
        generator.setSeed(System.currentTimeMillis());
        int i = generator.nextInt(10000) % 10000;

        java.text.DecimalFormat f = new java.text.DecimalFormat("0000");
        return f.format(i);

    }

}
