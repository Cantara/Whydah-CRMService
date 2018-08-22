package net.whydah.crmservice.verification;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import net.whydah.crmservice.configuration.HazelcastConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.service.Service;
import ratpack.service.StartEvent;
import ratpack.service.StopEvent;

import java.io.FileNotFoundException;
import java.util.Map;

public class ActiveVerificationCache implements Service {

    private static final Logger log = LoggerFactory.getLogger(ActiveVerificationCache.class);

    static Map<String, String> userpinmap;
    static Map<String, String> emailTokenMap;


    public void onStart(StartEvent event) {
        HazelcastConfig hazelcastConfig = event.getRegistry().get(HazelcastConfig.class);
        log.info("Executing onStart of ActiveVerificationCache");
        ActiveVerificationCache.init(hazelcastConfig.getFilename(), hazelcastConfig.getGridprefix());
    }

    public void onStop(StopEvent event) {
    }

    public static void init(String hazelcastConfigFilename, String gridPrefix) {


        log.info("Loading hazelcast configuration from :" + hazelcastConfigFilename);
		Config hazelcastConfig = new Config();
		if (hazelcastConfigFilename != null && hazelcastConfigFilename.length() > 10) {
			try {
				hazelcastConfig = new XmlConfigBuilder(hazelcastConfigFilename).build();
				log.info("Loading hazelcast configuration from :" + hazelcastConfigFilename);
			} catch (FileNotFoundException notFound) {
				log.error("Error - not able to load hazelcast.xml configuration.  Using embedded configuration as fallback");
			}
		}
		
		hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");
		
		HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);		
		
        userpinmap = hazelcastInstance.getMap(gridPrefix+"CRM_userpinmap");
        log.info("Connecting to map {}", gridPrefix+"CRM_userpinmap");

        emailTokenMap = hazelcastInstance.getMap(gridPrefix+"CRM_emailtokenmap");
        log.info("Connecting to map {}", gridPrefix + "CRM_emailtokenmap");
        
    }

    public static String getMapInfo() {
    	
    	StringBuilder sb = new StringBuilder();
        for(String key: emailTokenMap.keySet()) {
        	sb.append("email: " + key + ", token:" + emailTokenMap.get(key) + System.lineSeparator());
        }
        for(String key: userpinmap.keySet()) {
        	sb.append("phone: " + key + ", pin:" + emailTokenMap.get(key) + System.lineSeparator());
        }
        return sb.toString();
    }


    public static String addToken(String email, String token) {
        return emailTokenMap.put(email.toLowerCase(), token);
    }

    public static boolean tokenExists(String email) {
        return emailTokenMap.containsKey(email.toLowerCase());
    }

    public static String useToken(String email) {
        return emailTokenMap.remove(email.toLowerCase());
    }

    public static String addPin(String phoneNo, String generatedPin) {
        return userpinmap.put(phoneNo, generatedPin);
    }

    public static boolean pinExists(String phoneNo) {
        return userpinmap.containsKey(phoneNo);
    }

    public static String usePin(String phoneNo) {
        return userpinmap.remove(phoneNo);
    }
}
