package net.whydah.crmservice.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.util.TokenServiceClient;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;


@Singleton
public class SecurityHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SecurityHandler.class);

    private final TokenServiceClient tokenServiceClient;


    @Inject
    public SecurityHandler(TokenServiceClient tokenServiceClient) {
        this.tokenServiceClient = tokenServiceClient;
    }

    @Override
    public void handle(Context context) throws Exception {

        String applicationTokenId = context.getPathTokens().get("apptokenId");
        String userTokenId = context.getPathTokens().get("userTokenId");

        log.warn("SSL disabled for development - should be removed.");
        SSLTool.disableCertificateValidation();
        String userTokenXml = tokenServiceClient.getUserTokenXml(userTokenId);
        if (userTokenXml == null || userTokenXml.isEmpty()) {
            log.debug("Usertoken [{}] has been rejected.", userTokenId);
            log.error("Authentication has been disabled!");
            //context.clientError(401);
            //return;
        }

        log.debug("User validated - applicationTokenId=" + applicationTokenId + ", userTokenId=" + userTokenId);

        UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);
        Authentication.setAuthenticatedUser(userToken);

        context.next();
    }


}
