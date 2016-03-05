package net.whydah.crmservice.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryAddress {
    private String addressLine1;
    private String addressLine2;
    private String postalcode;
    private String postalcity;

    public DeliveryAddress(@JsonProperty("addressLine1") String address1,
                           @JsonProperty("addressLine2") String address2,
                           @JsonProperty("postalcode") String postalcode,
                           @JsonProperty("postalcity") String postalcity) {
        this.addressLine1 = address1;
        this.addressLine2 = address2;
        this.postalcode = postalcode;
        this.postalcity = postalcity;
    }

    public DeliveryAddress() {
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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
