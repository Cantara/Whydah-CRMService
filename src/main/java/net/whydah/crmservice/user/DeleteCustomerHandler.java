package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class DeleteCustomerHandler implements Handler {


    private final CustomerRepository userRepository;

    @Inject
    public DeleteCustomerHandler(CustomerRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");

        Blocking.get(() -> userRepository.deleteUser(userId)).then(affectedRows -> {
            if (affectedRows == 1) {
                ctx.redirect(204, userId); //No content
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
