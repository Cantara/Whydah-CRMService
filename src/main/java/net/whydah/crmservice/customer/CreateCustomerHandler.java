package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.customer.model.Customer;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.sql.SQLIntegrityConstraintViolationException;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class CreateCustomerHandler implements Handler {

    private final CustomerRepository customerRepository;

    @Inject
    public CreateCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        ctx.parse(fromJson(Customer.class)).then(customer -> {
            Blocking.op(() -> {
                try {
                    customerRepository.createCustomer(customerRef, customer);
                } catch (SQLIntegrityConstraintViolationException e) {
                    ctx.clientError(400); //Bad request
                }
            }).then(() -> {
                ctx.redirect(201, customerRef); //Created
            });
        });
    }
}
