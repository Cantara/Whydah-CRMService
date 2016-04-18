package net.whydah.crmservice;

import net.whydah.crmservice.verification.ActiveVerificationCache;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class ActiveVerificationCacheTest {
    private static ActiveVerificationCache cache;

    @BeforeClass
    public static void init() {
        cache = new ActiveVerificationCache("hazelcast.xml", "LA8PV");
    }

    @Test
    public void testEmailToken() {
        String email = "foo@@bar.com";
        String secretToken = "secret_token";

        cache.addToken(email, secretToken);

        // Verify upper- and lowercase
        assertTrue(cache.tokenExists(email));
        assertTrue(cache.tokenExists(email.toUpperCase()));
        assertTrue(cache.tokenExists(email.toLowerCase()));
        assertFalse(cache.tokenExists(email + email));

        // Use token - should also remove token
        String token = cache.useToken(email);
        assertEquals(secretToken, token);

        // Verify that token is removed
        String nullToken = cache.useToken(email);
        assertNull(nullToken);
    }

    @Test
    public void testPhonePin() {
        String phoneNo = "99887766";
        String generetedPin = "4321";

        String previousPin = cache.addPin(phoneNo, generetedPin);
        assertNull(previousPin);

        //Verify that pin exists
        assertTrue(cache.pinExists(phoneNo));

        // Use pin - should also remove pin
        String pin = cache.usePin(phoneNo);
        assertEquals(generetedPin, pin);

        // Verify that pin is removed
        String nullPin = cache.usePin(phoneNo);
        assertNull(nullPin);
    }
}
