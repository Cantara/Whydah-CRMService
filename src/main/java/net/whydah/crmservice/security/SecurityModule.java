package net.whydah.crmservice.security;

import com.google.inject.AbstractModule;

public class SecurityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SecurityHandler.class);
    }
}
