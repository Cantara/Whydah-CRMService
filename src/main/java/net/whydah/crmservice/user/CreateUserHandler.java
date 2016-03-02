package net.whydah.crmservice.user;

import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.User;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class CreateUserHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {

        String userId = ctx.getPathTokens().get("id");

        ctx.render(ctx.parse(fromJson(User.class)).map(p -> p.getName()));

    }
}
