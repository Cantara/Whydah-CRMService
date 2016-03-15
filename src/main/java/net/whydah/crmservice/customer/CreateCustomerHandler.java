package net.whydah.crmservice.customer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.sso.customer.types.Customer;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.internal.UuidBasedRequestIdGenerator;

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

        String customerRef;
        if (ctx.getPathTokens().get("customerRef") != null) {
            customerRef = ctx.getPathTokens().get("customerRef");
        } else {
            customerRef = UuidBasedRequestIdGenerator.INSTANCE.generate(ctx.getRequest()).toString();
        }


        ctx.parse(fromJson(Customer.class)).then(customer -> {
            Blocking.op(() -> {
                customerRepository.createCustomer(customerRef, customer);
            }).onError(throwable -> {
                if (throwable instanceof SQLIntegrityConstraintViolationException) {
                    ctx.clientError(400); //Bad request
                }
            }).then(() -> {
                ctx.redirect(201, customerRef); //Created
            });
        });
    }
}
