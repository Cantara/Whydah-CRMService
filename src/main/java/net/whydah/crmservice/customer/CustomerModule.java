package net.whydah.crmservice.customer;

import com.google.inject.AbstractModule;

public class CustomerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CreateCustomerHandler.class);
        bind(GetCustomerHandler.class);
        bind(UpdateCustomerHandler.class);
        bind(DeleteCustomerHandler.class);
        bind(CustomerHealthCheck.class);
    }
}
