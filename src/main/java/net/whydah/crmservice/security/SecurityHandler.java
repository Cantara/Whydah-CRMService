package net.whydah.crmservice.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUsertokenId;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.net.URI;


@Singleton
public class SecurityHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SecurityHandler.class);

    private final String securitytokenserviceurl;
    private final String applicationId;
    private final String applicationname;
    private final String applicationsecret;
    private static String myAppTokenId;
    private static String myAppTokenXml;

    @Inject
    public SecurityHandler(@Named("securitytokenserviceurl")String securitytokenserviceurl,
                           @Named("applicationid") String activeApplicationId,
                           @Named("applicationname") String applicationname,
                           @Named("applicationsecret") String applicationsecret) {
        this.securitytokenserviceurl = securitytokenserviceurl;
        this.applicationId = activeApplicationId;
        this.applicationname = applicationname;
        this.applicationsecret = applicationsecret;
    }

    @Override
    public void handle(Context context) throws Exception {

        String applicationTokenId = context.getPathTokens().get("apptokenId");
        String userTokenId = context.getPathTokens().get("userTokenId");

        if (myAppTokenId == null || myAppTokenId.isEmpty()) {
            logonApplication();
        }

        if (myAppTokenId == null || myAppTokenId.isEmpty()) {
            context.error(new ExceptionInInitializerError("Application authentication failed"));
            return;
        }

        log.warn("SSL disabled for development - should be removed.");
        SSLTool.disableCertificateValidation();
        String userTokenXml =  new CommandGetUsertokenByUsertokenId(new URI(securitytokenserviceurl), myAppTokenId, applicationTokenId, userTokenId).execute();
        if (userTokenXml == null) {
            context.clientError(401);
            return;
        }

        log.debug("User validated - applicationTokenId=" + applicationTokenId + ", userTokenId=" + userTokenId);

        UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);
        Authentication.setAuthenticatedUser(userToken);

        context.next();
    }

    private void logonApplication() {
        ApplicationCredential appCredential = new ApplicationCredential(applicationId,applicationname,applicationsecret);

        try {
            log.warn("SSL disabled for development - should be removed.");
            SSLTool.disableCertificateValidation();
            String appTokenXml = new CommandLogonApplication(new URI(securitytokenserviceurl), appCredential).execute();

            myAppTokenXml = appTokenXml;
            myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);

            log.debug("Applogon ok: apptokenxml: {}", myAppTokenXml);
            log.debug("myAppTokenId: {}", myAppTokenId);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
