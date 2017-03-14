package net.whydah.crmservice.health;

import com.google.inject.AbstractModule;

public class HealthModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GetHealthHandler.class);
    }
}
