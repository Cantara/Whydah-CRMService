package net.whydah.crmservice.user;

import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.json;

@Singleton
public class GetUserHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {

        User user = UserModule.userMap.get(ctx.getPathTokens().get("id"));

        ctx.render(json(user));

    }
}
