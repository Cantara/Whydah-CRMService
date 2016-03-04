package net.whydah.crmservice.user;

import com.google.inject.AbstractModule;
import net.whydah.crmservice.user.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CreateUserHandler.class);
        bind(GetUserHandler.class);
        bind(UpdateUserHandler.class);
        bind(DeleteUserHandler.class);
        bind(UserHealthCheck.class);
    }
}
