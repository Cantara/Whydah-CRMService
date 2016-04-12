package net.whydah.crmservice.util;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.net.URISyntaxException;

public class SmsModule extends AbstractModule {
    @Override
    protected void configure() {

    }


    @Provides
    SmsGatewayClient smsGatewayClient(@Named("smsgw.serviceurl") String serviceURL,
                                          @Named("smsgw.serviceaccount") String serviceAccount,
                                          @Named("smsgw.username") String username,
                                          @Named("smsgw.password") String password,
                                          @Named("smsgw.queryparams")String queryParam) throws URISyntaxException {

        return new SmsGatewayClient(serviceURL, serviceAccount, username, password, queryParam);
    }
}
