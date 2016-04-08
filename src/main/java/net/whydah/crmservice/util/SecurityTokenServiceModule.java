package net.whydah.crmservice.util;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.net.URISyntaxException;

public class SecurityTokenServiceModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    TokenServiceClient tokenServiceClient(@Named("securitytokenserviceurl") String securitytokenserviceurl,
                                          @Named("applicationid") String activeApplicationId,
                                          @Named("applicationname") String applicationname,
                                          @Named("applicationsecret") String applicationsecret) throws URISyntaxException {
        return new TokenServiceClient(securitytokenserviceurl,
                activeApplicationId, applicationname, applicationsecret);
    }
}
