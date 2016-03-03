package net.whydah.crmservice.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String name;
    private String email;
    private String phonenumber;
    private Address[] addresses;

    public User(@JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("phonenumber") String phonenumber,
                @JsonProperty("addresses") Address[] addresses) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.addresses = addresses;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Address[] getAddresses() {
        return addresses;
    }

    public void setAddresses(Address[] addresses) {
        this.addresses = addresses;
    }
}
