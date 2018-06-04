package net.whydah.crmservice;

import net.whydah.crmservice.verification.ActiveVerificationCache;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class ActiveVerificationCacheTest {
    //private static ActiveVerificationCache cache;

    @BeforeClass
    public static void init() {
        ActiveVerificationCache.init("hazelcast.xml", "LA8PV");
    }

    @Test
    public void testEmailToken() {
        String email = "foo@@bar.com";
        String secretToken = "secret_token";

        ActiveVerificationCache.addToken(email, secretToken);

        // Verify upper- and lowercase
        assertTrue(ActiveVerificationCache.tokenExists(email));
        assertTrue(ActiveVerificationCache.tokenExists(email.toUpperCase()));
        assertTrue(ActiveVerificationCache.tokenExists(email.toLowerCase()));
        assertFalse(ActiveVerificationCache.tokenExists(email + email));

        // Use token - should also remove token
        String token = ActiveVerificationCache.useToken(email);
        assertEquals(secretToken, token);

        // Verify that token is removed
        String nullToken = ActiveVerificationCache.useToken(email);
        assertNull(nullToken);
    }

    @Test
    public void testPhonePin() {
        String phoneNo = "99887766";
        String generetedPin = "4321";

        String previousPin = ActiveVerificationCache.addPin(phoneNo, generetedPin);
        assertNull(previousPin);

        //Verify that pin exists
        assertTrue(ActiveVerificationCache.pinExists(phoneNo));

        // Use pin - should also remove pin
        String pin = ActiveVerificationCache.usePin(phoneNo);
        assertEquals(generetedPin, pin);

        // Verify that pin is removed
        String nullPin = ActiveVerificationCache.usePin(phoneNo);
        assertNull(nullPin);
    }
}
