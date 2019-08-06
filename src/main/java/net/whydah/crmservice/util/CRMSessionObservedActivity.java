package net.whydah.crmservice.util;

import org.valuereporter.activity.ObservedActivity;

public class CRMSessionObservedActivity extends ObservedActivity {
    public static final String USER_SESSION_ACTIVITY = "crmSession";
    private static final String USER_SESSION_ACTIVITY_DB_KEY = "crmid";

    public CRMSessionObservedActivity(String userid, String userSessionActivity, String applicationtokenid) {
        super(USER_SESSION_ACTIVITY, System.currentTimeMillis());
        put("crmid", userid);
        put("crmsessionfunction", userSessionActivity);
        put("applicationtokenid", applicationtokenid);
    }
}