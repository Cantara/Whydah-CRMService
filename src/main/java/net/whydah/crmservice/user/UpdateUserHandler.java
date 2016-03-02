package net.whydah.crmservice.user;

import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class UpdateUserHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");
        User user = UserModule.userMap.get(userId);

        ctx.render(json(user));
    }
}
