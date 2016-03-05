package net.whydah.crmservice.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private String id;
    private String firstname;
    private String lastname;
    private Map<String, EmailAddress> emailaddresses;
    private Map<String, PhoneNumber> phonenumbers;
    private String defaultAddressLabel;
    private Map<String, DeliveryAddress> deliveryaddresses;

    public Customer(@JsonProperty("id") String id,
                    @JsonProperty("firstname") String firstname,
                    @JsonProperty("lastname") String lastname,
                    @JsonProperty("emailaddresses") Map<String, EmailAddress> email,
                    @JsonProperty("phonenumbers") Map<String, PhoneNumber> phonenumber,
                    @JsonProperty("defaultAddressLabel") String defaultAddressLabel,
                    @JsonProperty("deliveryaddresses") Map<String, DeliveryAddress> addresses) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailaddresses = email;
        this.phonenumbers = phonenumber;
        this.defaultAddressLabel = defaultAddressLabel;
        this.deliveryaddresses = addresses;
    }

    public Customer() {

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

    public Map<String, EmailAddress> getEmailaddresses() {
        return emailaddresses;
    }

    public void setEmailaddresses(Map<String, EmailAddress> emailaddresses) {
        this.emailaddresses = emailaddresses;
    }

    public Map<String, PhoneNumber> getPhonenumbers() {
        return phonenumbers;
    }

    public void setPhonenumbers(Map<String, PhoneNumber> phonenumbers) {
        this.phonenumbers = phonenumbers;
    }

    public Map<String, DeliveryAddress> getDeliveryaddresses() {
        return deliveryaddresses;
    }

    public void setDeliveryaddresses(Map<String, DeliveryAddress> deliveryaddresses) {
        this.deliveryaddresses = deliveryaddresses;
    }

    public String getDefaultAddressLabel() {
        return defaultAddressLabel;
    }

    public void setDefaultAddressLabel(String defaultAddressLabel) {
        this.defaultAddressLabel = defaultAddressLabel;
    }
}
