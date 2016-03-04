package net.whydah.crmservice.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String defaultAddressLabel;
    private Map<String, Address> addresses;

    public User(@JsonProperty("id") String id,
                @JsonProperty("firstname") String firstname,
                @JsonProperty("lastname") String lastname,
                @JsonProperty("email") String email,
                @JsonProperty("phonenumber") String phonenumber,
                @JsonProperty("defaultAddressLabel") String defaultAddressLabel,
                @JsonProperty("addresses") Map<String, Address> addresses) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phonenumber = phonenumber;
        this.defaultAddressLabel = defaultAddressLabel;
        this.addresses = addresses;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public Map<String, Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Map<String, Address> addresses) {
        this.addresses = addresses;
    }

    public String getDefaultAddressLabel() {
        return defaultAddressLabel;
    }

    public void setDefaultAddressLabel(String defaultAddressLabel) {
        this.defaultAddressLabel = defaultAddressLabel;
    }
}
