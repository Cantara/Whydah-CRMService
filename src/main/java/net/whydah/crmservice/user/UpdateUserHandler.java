package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.whydah.crmservice.user.model.User;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class UpdateUserHandler implements Handler {

    private final UserRepository userRepository;

    @Inject
    public UpdateUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");

        ctx.parse(fromJson(User.class)).then(user -> {
            Blocking.op(() -> {
                userRepository.updateUser(userId, user);
            }).then(() -> {
                ctx.getResponse().status(202).send(); //Accepted
            });
        });
    }
}
