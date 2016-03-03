package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.whydah.crmservice.user.model.Address;
import net.whydah.crmservice.user.model.User;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class DeleteUserHandler implements Handler {


    private final UserRepository userRepository;

    @Inject
    public DeleteUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");

        Blocking.op(() -> {
            userRepository.deleteUser(userId);
        }).then(() -> {
            ctx.getResponse().status(202).send(); //Accepted
        });
    }
}
