//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.whydah.crmservice.security;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandGroupKey.Factory;

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.whydah.sso.util.ExceptionUtil;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandGetUsertokenByUsertokenId extends HystrixCommand<String> {
    private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUsertokenId.class);
    private URI tokenServiceUri;
    private String myAppTokenId;
    private String usertokenId;
    private String myAppTokenXml;

    public CommandGetUsertokenByUsertokenId(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String usertokenId) {
        super(Setter.withGroupKey(Factory.asKey("SSOAUserAuthGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(60000)));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.usertokenId = usertokenId;
        this.myAppTokenXml = myAppTokenXml;
        if (tokenServiceUri == null || myAppTokenId == null || myAppTokenXml == null || usertokenId == null) {
            log.error("CommandGetUsertokenByUsertokenId initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, usertokenId:{}", new Object[]{tokenServiceUri.toString(), myAppTokenId, usertokenId});
        }

    }

    protected String run() {
        String responseXML = null;
        log.trace("CommandGetUsertokenByUsertokenId - uri={} myAppTokenId={}, usertokenId:{}", new Object[]{this.tokenServiceUri.toString(), this.myAppTokenId, this.usertokenId});
        Client tokenServiceClient = ClientBuilder.newClient();

        SSLTool.disableCertificateValidation();
        WebTarget userTokenResource = tokenServiceClient.target(this.tokenServiceUri).path("user/" + this.myAppTokenId + "/get_usertoken_by_usertokenid");
        log.trace("CommandGetUsertokenByUsertokenId  - usertokenid: {} apptoken: {}", this.usertokenId, this.myAppTokenXml);
        Form formData = new Form();
        formData.param("apptoken", this.myAppTokenXml);
        formData.param("usertokenid", this.usertokenId);
        SSLTool.disableCertificateValidation();
        Response response = (Response) userTokenResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        if (response.getStatus() == Status.FORBIDDEN.getStatusCode()) {
            log.debug("CommandGetUsertokenByUsertokenId - Response Code from STS: {}", Integer.valueOf(response.getStatus()));
            throw new IllegalArgumentException("CommandGetUsertokenByUsertokenId failed.");
        } else {
            if (response.getStatus() != Status.OK.getStatusCode()) {
                log.debug("CommandGetUsertokenByUsertokenId - Response Code from STS: {}", Integer.valueOf(response.getStatus()));
            }

            responseXML = (String) response.readEntity(String.class);
            log.debug("CommandGetUsertokenByUsertokenId - Response OK with XML: {}", responseXML);
            if (responseXML == null) {
                String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", userTokenResource, response);
                log.warn(authenticationFailedMessage);
                throw new RuntimeException(authenticationFailedMessage);
            } else {
                return responseXML;
            }
        }
    }

    protected String getFallback() {
        log.warn("CommandGetUsertokenByUsertokenId - fallback - uri={} - usertokenId:{} - myAppTokenId: {}", new Object[]{this.tokenServiceUri.toString(), this.usertokenId, this.myAppTokenId});
        return null;
    }
}
