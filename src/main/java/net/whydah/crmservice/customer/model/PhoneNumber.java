package net.whydah.crmservice.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhoneNumber {


    private String phonenumber;
    private String tags;


    public PhoneNumber(@JsonProperty("phonenumber") String phonenumber,
                       @JsonProperty("tags") String tags) {
        this.phonenumber = phonenumber;
        this.tags = tags;
    }


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
