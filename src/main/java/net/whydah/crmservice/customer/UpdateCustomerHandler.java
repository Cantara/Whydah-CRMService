package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.security.Authentication;
import net.whydah.sso.extensions.crmcustomer.types.Customer;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class UpdateCustomerHandler implements Handler {

    private final CustomerRepository customerRepository;

    @Inject
    public UpdateCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
            ctx.clientError(401);
            return;
        }

        ctx.parse(fromJson(Customer.class)).then(customer -> {
            Blocking.get(() -> customerRepository.updateCustomer(customerRef, customer)).then(affectedRows -> {
                if (affectedRows == 1) {
                    ctx.redirect(202, customerRef); //Accepted
                } else {
                    ctx.clientError(404); //Not found
                }
            });
        });
    }
}
