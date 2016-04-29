package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.security.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class GetCustomerHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(GetCustomerHandler.class);

    private final CustomerRepository customerRepository;

    @Inject
    public GetCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");
        // TODO  fix this to verify against a sensible UserRole
        if ("useradmin".equalsIgnoreCase(Authentication.getAuthenticatedUser().getUid().toString())) {
        } else if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
            log.debug("User {} with personRef {} not authorized to get data for personRef {}", Authentication.getAuthenticatedUser().getUid(), Authentication.getAuthenticatedUser().getPersonRef(), customerRef);
            ctx.clientError(401);
            return;
        }

        log.trace("Getting customer with ref={}", customerRef);

        Blocking.get(() -> customerRepository.getCustomer(customerRef)).then(customer -> {
            log.trace("Found customer-data: {}", customer);
            if (customer != null) {
                ctx.render(json(customer));
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
