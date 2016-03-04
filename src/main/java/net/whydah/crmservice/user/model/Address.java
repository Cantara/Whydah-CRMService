package net.whydah.crmservice.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lasse on 02/03/16.
 */
public class Address {
    private String id;
    private String address1;
    private String address2;
    private boolean isDefault;

    public Address(@JsonProperty("id") String id,
                   @JsonProperty("address1") String address1,
                   @JsonProperty("address2") String address2,
                   @JsonProperty("isDefault") boolean isDefault) {
        this.id = id;
        this.address1 = address1;
        this.address2 = address2;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
