package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.sql.SQLIntegrityConstraintViolationException;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class CreateUserHandler implements Handler {

    private final UserRepository userRepository;

    @Inject
    public CreateUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");

        ctx.parse(fromJson(User.class)).then(user -> {
            Blocking.op(() -> {
                try {
                    userRepository.createUser(userId, user);
                } catch (SQLIntegrityConstraintViolationException e) {
                    ctx.clientError(400); //Bad request
                }
            }).then(() -> {
                ctx.redirect(201, userId); //Created
            });
        });
    }
}
