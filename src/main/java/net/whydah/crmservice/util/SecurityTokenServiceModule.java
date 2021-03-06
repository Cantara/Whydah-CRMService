package net.whydah.crmservice.util;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.net.URISyntaxException;

public class SecurityTokenServiceModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    static SecurityTokenServiceClient client;
    @Provides
    SecurityTokenServiceClient tokenServiceClient(@Named("securitytokenserviceurl") String securitytokenserviceurl,
                                                  @Named("useradminserviceurl") String useradminserviceurl,
                                                  @Named("applicationid") String activeApplicationId,
                                                  @Named("applicationname") String applicationname,
                                                  @Named("applicationsecret") String applicationsecret) throws URISyntaxException {
    	if(client==null){
            client = new SecurityTokenServiceClient(securitytokenserviceurl, useradminserviceurl,
    				activeApplicationId, applicationname, applicationsecret);
    	} 
    	return client;
    }
}
