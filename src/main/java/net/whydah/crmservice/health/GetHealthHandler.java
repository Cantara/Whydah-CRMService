package net.whydah.crmservice.health;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.util.TokenServiceClient;
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
    private final TokenServiceClient tokenServiceClient;


    @Inject
    public GetHealthHandler(TokenServiceClient tokenServiceClient) {
        this.tokenServiceClient = tokenServiceClient;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        ctx.render(getHealthTextJson());
    }

    public String getHealthTextJson() {
        String DEFCON = "";
        boolean hasApplicationToken = false;
        boolean hasValidApplicationToken = false;
        boolean hasApplicationsMetadata = false;
        try {
            hasApplicationToken = (tokenServiceClient.getWAS().getActiveApplicationTokenId() != null);
            hasValidApplicationToken = tokenServiceClient.getWAS().checkActiveSession();
            hasApplicationsMetadata = tokenServiceClient.getWAS().hasApplicationMetaData();
            DEFCON = tokenServiceClient.getWAS().getDefcon().toString();

        } catch (Exception e) {

        }
        return "{\n" +
                "  \"Status\": \"OK\",\n" +
                "  \"Version\": \"" + getVersion() + "\",\n" +
                "  \"DEFCON\": \"" + DEFCON + "\"\n" +
                "  \"hasApplicationToken\": \"" + Boolean.toString(hasApplicationToken) + "\"\n" +
                "  \"hasValidApplicationToken\": \"" + Boolean.toString(hasValidApplicationToken) + "\"\n" +
                "  \"hasApplicationsMetadata\": \"" + Boolean.toString(hasApplicationsMetadata) + "\"\n" +


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
