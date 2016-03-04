package net.whydah.crmservice.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private String address1;
    private String address2;
    private String postalcode;
    private String postalcity;

    public Address(@JsonProperty("address1") String address1,
                   @JsonProperty("address2") String address2,
                   @JsonProperty("postalcode") String postalcode,
                   @JsonProperty("postalcity") String postalcity) {
        this.address1 = address1;
        this.address2 = address2;
        this.postalcode = postalcode;
        this.postalcity = postalcity;
    }

    public Address() {}

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getPostalcity() {
        return postalcity;
    }

    public void setPostalcity(String postalcity) {
        this.postalcity = postalcity;
    }
}
