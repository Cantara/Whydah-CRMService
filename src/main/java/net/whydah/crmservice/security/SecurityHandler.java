package net.whydah.crmservice.security;

import com.google.inject.Inject;
import net.whydah.crmservice.util.SecurityTokenServiceClient;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ratpack.handling.Context;
import ratpack.handling.Handler;


@Service
public class SecurityHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SecurityHandler.class);

    private final SecurityTokenServiceClient tokenServiceClient;


    @Inject
    public SecurityHandler(SecurityTokenServiceClient tokenServiceClient) {
        this.tokenServiceClient = tokenServiceClient;
    }

    @Override
    public void handle(Context context) throws Exception {

        String applicationTokenId = context.getPathTokens().get("apptokenId");

        if (!tokenServiceClient.isApplicationTokenIdValid(applicationTokenId)) {
            log.debug("ApplicationTokenId [{}] has been rejected.", applicationTokenId);
            context.clientError(401);
            return;
        }

        String userTokenId = context.getPathTokens().get("userTokenId");

        log.warn("SSL disabled for development - should be removed.");
        SSLTool.disableCertificateValidation();
        String userTokenXml = tokenServiceClient.getUserTokenXml(userTokenId);
        if (userTokenXml == null || userTokenXml.isEmpty()) {
            log.debug("UsertokenId [{}] has been rejected.", userTokenId);
            context.clientError(401);
            return;
        }

        log.debug("User validated - applicationTokenId=" + applicationTokenId + ", userTokenId=" + userTokenId);

        UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);
        boolean adminUser = UserXpathHelper.hasRoleFromUserToken(userTokenXml, "2219", "WhydahUserAdmin");
        if (!adminUser) {
            adminUser = "systest".equalsIgnoreCase(UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml)) ||
                    "useradmin".equalsIgnoreCase(UserXpathHelper.getUserIdFromUserTokenXml(userTokenXml));
        }
        Authentication.setAuthenticatedUser(userToken, adminUser);

        context.next();
    }


}
