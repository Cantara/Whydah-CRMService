package net.whydah.crmservice.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.Map;

// TODO  align with requirement spefication

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private String id;
    private String firstname;
    private String middlename;
    private String lastname;
    private String sex;
    private Date birthdate;
    private String defaultEmailLabel;
    private Map<String, EmailAddress> emailaddresses;
    private String defaultPhoneLabel;
    private Map<String, PhoneNumber> phonenumbers;
    private String defaultAddressLabel;
    private Map<String, DeliveryAddress> deliveryaddresses;

    public Customer(@JsonProperty("id") String id,
                    @JsonProperty("firstname") String firstname,
                    @JsonProperty("middlename") String middlename,
                    @JsonProperty("lastname") String lastname,
                    @JsonProperty("sex") String sex,
                    @JsonProperty("birthdate") Date birthdate,
                    @JsonProperty("defaultEmailLabel") String defaultEmailLabel,
                    @JsonProperty("emailaddresses") Map<String, EmailAddress> email,
                    @JsonProperty("defaultPhoneLabel") String defaultPhoneLabel,
                    @JsonProperty("phonenumbers") Map<String, PhoneNumber> phonenumber,
                    @JsonProperty("defaultAddressLabel") String defaultAddressLabel,
                    @JsonProperty("deliveryaddresses") Map<String, DeliveryAddress> addresses) {
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.sex = sex;
        this.birthdate = birthdate;
        this.defaultEmailLabel = defaultEmailLabel;
        this.emailaddresses = email;
        this.defaultPhoneLabel = defaultPhoneLabel;
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

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateDeserializer.class)
    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getDefaultEmailLabel() {
        return defaultEmailLabel;
    }

    public void setDefaultEmailLabel(String defaultEmailLabel) {
        this.defaultEmailLabel = defaultEmailLabel;
    }

    public Map<String, EmailAddress> getEmailaddresses() {
        return emailaddresses;
    }

    public void setEmailaddresses(Map<String, EmailAddress> emailaddresses) {
        this.emailaddresses = emailaddresses;
    }

    public String getDefaultPhoneLabel() {
        return defaultPhoneLabel;
    }

    public void setDefaultPhoneLabel(String defaultPhoneLabel) {
        this.defaultPhoneLabel = defaultPhoneLabel;
    }

    public Map<String, PhoneNumber> getPhonenumbers() {
        return phonenumbers;
    }

    public void setPhonenumbers(Map<String, PhoneNumber> phonenumbers) {
        this.phonenumbers = phonenumbers;
    }

    public String getDefaultAddressLabel() {
        return defaultAddressLabel;
    }

    public void setDefaultAddressLabel(String defaultAddressLabel) {
        this.defaultAddressLabel = defaultAddressLabel;
    }

    public Map<String, DeliveryAddress> getDeliveryaddresses() {
        return deliveryaddresses;
    }

    public void setDeliveryaddresses(Map<String, DeliveryAddress> deliveryaddresses) {
        this.deliveryaddresses = deliveryaddresses;
    }
}
