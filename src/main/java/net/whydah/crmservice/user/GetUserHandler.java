package net.whydah.crmservice.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class GetUserHandler implements Handler {

    private final UserRepository userRepository;

    @Inject
    public GetUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");

        Blocking.get(() -> userRepository.getUser(userId)).then(user -> {
            if (user != null) {
                ctx.render(json(user));
            } else {
                ctx.clientError(404); //Not found
            }
        });
    }
}
