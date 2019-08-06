package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.security.Authentication;
import net.whydah.crmservice.util.CRMSessionObservedActivity;
import net.whydah.sso.extensions.crmcustomer.types.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.client.MonitorReporter;
import org.valuereporter.activity.ObservedActivity;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class UpdateCustomerHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(UpdateCustomerHandler.class);
    private final CustomerRepository customerRepository;

    @Inject
    public UpdateCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        if (Authentication.isAdminUser()) {
        } else if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
            log.debug("User {} with personRef {} not authorized to update data for personRef {}", Authentication.getAuthenticatedUser().getUid(), Authentication.getAuthenticatedUser().getPersonRef(), customerRef);
            ctx.clientError(401);
            return;
        }
        log.trace("Updating customer with ref={}", customerRef);

        ctx.parse(fromJson(Customer.class)).then(customer -> {
            Blocking.get(() -> customerRepository.updateCustomer(customerRef, customer)).then(affectedRows -> {
                if (affectedRows == 1) {
                    ObservedActivity observedActivity = new CRMSessionObservedActivity(customerRef, "crmUserUpdated", Authentication.getAuthenticatedUser().getUid());
                    MonitorReporter.reportActivity(observedActivity);

                    ctx.redirect(202, customerRef); //Accepted
                } else {
                    ctx.clientError(404); //Not found
                }
            });
        });
    }
}
