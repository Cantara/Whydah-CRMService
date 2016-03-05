package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.Customer;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class UpdateCustomerHandler implements Handler {

    private final CustomerRepository userRepository;

    @Inject
    public UpdateCustomerHandler(CustomerRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");

        ctx.parse(fromJson(Customer.class)).then(user -> {
            Blocking.get(() -> userRepository.updateCustomer(userId, user)).then(affectedRows -> {
                if (affectedRows == 1) {
                    ctx.redirect(202, userId); //Accepted
                } else {
                    ctx.clientError(404); //Not found
                }
            });
        });
    }
}
