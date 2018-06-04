package net.whydah.crmservice.verification;

import com.google.inject.Singleton;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Map;

@Singleton
public class ActiveVerificationCache {

    private static final Logger log = LoggerFactory.getLogger(ActiveVerificationCache.class);

    static Map<String, String> userpinmap;
    static Map<String, String> emailTokenMap;
    //private static String gridPrefix;
    //private static String hazelcastConfigFilename;

//    public ActiveVerificationCache(String hazelcastConfigFilename, String gridPrefix){
//        this.gridPrefix = gridPrefix;
//        this.hazelcastConfigFilename = hazelcastConfigFilename;
//        if (userpinmap == null || emailTokenMap == null) {
//            init();
//        }
//    }

    public static void init(String hazelcastConfigFilename, String gridPrefix) {
    	
    	
    	String xmlFileName = hazelcastConfigFilename;
		log.info("Loading hazelcast configuration from :" + xmlFileName);
		Config hazelcastConfig = new Config();
		if (xmlFileName != null && xmlFileName.length() > 10) {
			try {
				hazelcastConfig = new XmlConfigBuilder(xmlFileName).build();
				log.info("Loading hazelcast configuration from :" + xmlFileName);
			} catch (FileNotFoundException notFound) {
				log.error("Error - not able to load hazelcast.xml configuration.  Using embedded configuration as fallback");
			}
		}
		
		//hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");
		hazelcastConfig.setGroupConfig(new GroupConfig("CRM_SERVICE"));
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
