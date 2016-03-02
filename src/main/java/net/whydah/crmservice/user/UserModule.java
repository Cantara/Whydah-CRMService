package net.whydah.crmservice.user;

import com.google.inject.AbstractModule;
import net.whydah.crmservice.user.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserModule extends AbstractModule {

    public static Map<String, User> userMap = new HashMap<>();

    @Override
    protected void configure() {
        bind(GetUserHandler.class);
        bind(UserHealthCheck.class);
    }
}
