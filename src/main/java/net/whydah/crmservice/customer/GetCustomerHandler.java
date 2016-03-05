package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class GetCustomerHandler implements Handler {

    private final CustomerRepository customerRepository;

    @Inject
    public GetCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        Blocking.get(() -> customerRepository.getCustomer(customerRef)).then(customer -> {
            if (customer != null) {
                ctx.render(json(customer));
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
