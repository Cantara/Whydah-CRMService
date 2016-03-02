package net.whydah.crmservice.user;

import com.google.inject.Singleton;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class NameHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
       // ctx.getRequest()

       // ctx.get("foo", ctx -> ctx.render("ok")
    }
}
