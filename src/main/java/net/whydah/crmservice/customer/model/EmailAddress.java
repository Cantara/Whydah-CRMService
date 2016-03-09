package net.whydah.crmservice.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailAddress {

    private String emailaddress;
    private String tags;
    private boolean verified;


    public EmailAddress(@JsonProperty("emailaddress") String emailaddress,
                        @JsonProperty("tags") String tags,
                        @JsonProperty("verified") boolean verified) {
        this.emailaddress = emailaddress;
        this.tags = tags;
        this.verified = verified;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public void setEmailaddress(String enmailaddress) {
        this.emailaddress = enmailaddress;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
