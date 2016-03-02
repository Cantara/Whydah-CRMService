package net.whydah.crmservice.user;

import com.google.inject.Singleton;
import net.whydah.crmservice.user.model.Address;
import net.whydah.crmservice.user.model.User;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.jackson.Jackson.json;

@Singleton
public class DeleteUserHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        User user = new User();
        user.setId(ctx.getPathTokens().get("id"));
        user.setName("Navn navnesen");
        user.setPhonenumber("99887766");
        user.setAddresses(new Address[2]);


        ctx.render(json(user));
    }
}
