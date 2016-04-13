package net.whydah.crmservice.util;


import com.google.inject.Singleton;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUsertokenId;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public class TokenServiceClient {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceClient.class);

    private final WhydahApplicationSession applicationSession;

    public TokenServiceClient(String securitytokenserviceurl,
                              String activeApplicationId,
                              String applicationname,
                              String applicationsecret) throws URISyntaxException {

        log.warn("SSL disabled for development - should be removed.");
        SSLTool.disableCertificateValidation();
        applicationSession = new WhydahApplicationSession(securitytokenserviceurl, activeApplicationId, applicationname, applicationsecret);
    }

    public Boolean isApplicationTokenIdValid(String applicationTokenId) {
        return new CommandValidateApplicationTokenId(applicationSession.getSTS(), applicationTokenId).execute();
    }

    public String getUserTokenXml(String userTokenId) throws URISyntaxException {
        log.warn("SSL disabled for development - should be removed.");
        SSLTool.disableCertificateValidation();
        return new CommandGetUsertokenByUsertokenId(new URI(applicationSession.getSTS()),
                                                    applicationSession.getActiveApplicationTokenId(),
                                                    applicationSession.getActiveApplicationTokenXML(),
                                                    userTokenId).
                                                    execute();
    }

}
