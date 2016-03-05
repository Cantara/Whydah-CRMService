package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class DeleteCustomerHandler implements Handler {


    private final CustomerRepository customerRepository;

    @Inject
    public DeleteCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        Blocking.get(() -> customerRepository.deleteCustomer(customerRef)).then(affectedRows -> {
            if (affectedRows == 1) {
                ctx.redirect(204, customerRef); //No content
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
