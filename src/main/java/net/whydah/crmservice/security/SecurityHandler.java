package net.whydah.crmservice.security;

import com.google.inject.Singleton;
import ratpack.handling.Context;
import ratpack.handling.Handler;


@Singleton
public class SecurityHandler implements Handler {

    @Override
    public void handle(Context context) throws Exception {
        System.out.println("TODO: Check app security");

        String apptokenId = context.getPathTokens().get("apptokenId");
        String adminuserTokenId = context.getPathTokens().get("adminuserTokenId");


        context.next();
    }
}
