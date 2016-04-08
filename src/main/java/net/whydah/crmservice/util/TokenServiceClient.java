package net.whydah.crmservice.util;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUsertokenId;
import net.whydah.sso.commands.extensions.crmapi.CommandVerifyPhoneByPin;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public class TokenServiceClient {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceClient.class);


    private final URI securitytokenserviceurl;
    private final String applicationId;
    private final String applicationname;
    private final String applicationsecret;
    private static String myAppTokenId;
    private static String myAppTokenXml;

    @Inject
    public TokenServiceClient(String securitytokenserviceurl,
                              String activeApplicationId,
                              String applicationname,
                              String applicationsecret) throws URISyntaxException {
        this.securitytokenserviceurl = new URI(securitytokenserviceurl);
        this.applicationId = activeApplicationId;
        this.applicationname = applicationname;
        this.applicationsecret = applicationsecret;
    }

    public static void setMyAppTokenId(String myAppTokenId) {
        TokenServiceClient.myAppTokenId = myAppTokenId;
    }

    public String getMyAppTokenId() {
        return myAppTokenId;
    }

    public void logonApplication() {
        ApplicationCredential appCredential = new ApplicationCredential(applicationId, applicationname, applicationsecret);

        try {
            log.warn("SSL disabled for development - should be removed.");
            SSLTool.disableCertificateValidation();
            String appTokenXml = new CommandLogonApplication(securitytokenserviceurl, appCredential).execute();

            myAppTokenXml = appTokenXml;
            myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);

            log.debug("Applogon ok: apptokenxml: {}", myAppTokenXml);
            log.debug("myAppTokenId: {}", myAppTokenId);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserTokenXml(String userTokenId) throws URISyntaxException {
        return new CommandGetUsertokenByUsertokenId(securitytokenserviceurl, myAppTokenId, myAppTokenXml, userTokenId).execute();
    }

    public boolean verifyPhonePin(String userTokenId, String personRef, String phoneNo, String pin) {
        Boolean result = new CommandVerifyPhoneByPin(securitytokenserviceurl, myAppTokenXml, userTokenId, personRef, phoneNo, pin).execute();
        return result;
    }

    public boolean verifyEmailAddressToken(String userTokenId, String personRef, String emailaddress, String token) {
        log.warn("Email verification is NOT yet implemented - returning false.");
        return false;
    }
}
