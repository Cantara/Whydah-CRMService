package net.whydah.crmservice.security;


import net.whydah.sso.user.types.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Execution;

/**
 * Holds current authenticated user in a ratpack "threadlocal-equivalent".
 */
public final class Authentication {
    private static final Logger log = LoggerFactory.getLogger(Authentication.class);

    public static void setAuthenticatedUser(UserToken userToken, boolean isAdminUser) {
        log.debug("setAuthenticatedUser with userToken: {} isAdminUser:{}", userToken, isAdminUser);
        Execution.current().add(UserToken.class, userToken);
        Execution.current().add(Boolean.class, isAdminUser);
    }

    public static UserToken getAuthenticatedUser() {
        return Execution.current().get(UserToken.class);
    }

    public static boolean isAdminUser() {
        return Execution.current().get(Boolean.class);
    }


    public static void clearAuthentication() {
        Execution.current().remove(UserToken.class);
        Execution.current().remove(Boolean.class);
    }

    private Authentication(){
    }
}
