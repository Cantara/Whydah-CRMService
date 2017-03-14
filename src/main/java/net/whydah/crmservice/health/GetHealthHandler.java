package net.whydah.crmservice.health;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Singleton
public class GetHealthHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(GetHealthHandler.class);


    @Override
    public void handle(Context ctx) throws Exception {

        ctx.render(getHealthTextJson());
    }

    public String getHealthTextJson() {
        return "{\n" +
                "  \"Status\": \"OK\",\n" +
                "  \"Version\": \"" + getVersion() + "\",\n" +
                "  \"DEFCON\": \"" + "DEFCON5" + "\"\n" +
//                "  \"hasApplicationToken\": \"" + Boolean.toString((credentialStore.getWas().getActiveApplicationTokenId() != null)) + "\"\n" +
//                "  \"hasValidApplicationToken\": \"" + Boolean.toString(credentialStore.getWas().checkActiveSession()) + "\"\n" +
//                "  \"hasApplicationsMetadata\": \"" + Boolean.toString(credentialStore.getWas().getApplicationList().size() > 2) + "\"\n" +


                "}\n";
    }

    private static String getVersion() {
        Properties mavenProperties = new Properties();
        String resourcePath = "/META-INF/maven/net.whydah.service/Whydah-CRMService/pom.properties";
        URL mavenVersionResource = GetHealthHandler.class.getResource(resourcePath);
        if (mavenVersionResource != null) {
            try {
                mavenProperties.load(mavenVersionResource.openStream());
                return mavenProperties.getProperty("version", "missing version info in " + resourcePath);
            } catch (IOException e) {
                log.warn("Problem reading version resource from classpath: ", e);
            }
        }
        return "(DEV VERSION)";
    }

}
