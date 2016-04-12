package net.whydah.crmservice.util;

public class SmsGatewayClient {
    private final String queryParam;
    private final String serviceUrl;
    private final String serviceAccount;
    private final String username;
    private final String password;

    public SmsGatewayClient(String serviceURL, String serviceAccount, String username, String password, String queryParam) {
        this.serviceUrl = serviceURL;
        this.serviceAccount= serviceAccount;
        this.username = username;
        this.password = password;
        this.queryParam = queryParam;
    }

    public String getQueryParam() {
        return queryParam;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getServiceAccount() {
        return serviceAccount;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
