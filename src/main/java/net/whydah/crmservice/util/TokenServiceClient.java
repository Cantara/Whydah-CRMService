package net.whydah.crmservice.util;


import com.google.inject.Singleton;
import net.whydah.sso.session.baseclasses.BaseWhydahServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

@Singleton
public class TokenServiceClient extends BaseWhydahServiceClient {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceClient.class);


    public TokenServiceClient(String securitytokenserviceurl,
                              String useradminserviceurl,
                              String activeApplicationId,
                              String applicationname,
                              String applicationsecret) throws URISyntaxException {

        super(securitytokenserviceurl, useradminserviceurl, activeApplicationId, applicationname, applicationsecret);
        this.getWAS().setDisableUpdateAppLink(true);
    }


}
