package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.security.Authentication;
import net.whydah.crmservice.util.CRMSessionObservedActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.agent.MonitorReporter;
import org.valuereporter.agent.activity.ObservedActivity;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class DeleteCustomerHandler implements Handler {


    private static final Logger log = LoggerFactory.getLogger(DeleteCustomerHandler.class);
    private final CustomerRepository customerRepository;

    @Inject
    public DeleteCustomerHandler(CustomerRepository customerRepository) {
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

        log.trace("Deleting customer with ref={}", customerRef);

        Blocking.get(() -> customerRepository.deleteCustomer(customerRef)).then(affectedRows -> {
            if (affectedRows == 1) {
                ObservedActivity observedActivity = new CRMSessionObservedActivity(customerRef, "crmUserDeleted", Authentication.getAuthenticatedUser().getUid());
                MonitorReporter.reportActivity(observedActivity);

                ctx.redirect(204, customerRef); //No content
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
