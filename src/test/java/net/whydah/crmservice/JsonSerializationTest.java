package net.whydah.crmservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.crmservice.customer.model.Customer;
import net.whydah.crmservice.customer.model.DeliveryAddress;
import net.whydah.crmservice.customer.model.EmailAddress;
import net.whydah.crmservice.customer.model.PhoneNumber;
import org.junit.Test;


import java.util.HashMap;
import java.util.Map;


public class JsonSerializationTest {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void createExampleCustomerAndCheckJson() throws Exception {
        Customer customer = new Customer();
        customer.setId("12345");
        customer.setFirstname("First");
        customer.setLastname("Lastname");

        Map<String, DeliveryAddress> addresses = new HashMap<>();
        DeliveryAddress addr1 = new DeliveryAddress();
        addr1.setAddressLine1("Karl Johansgate 6");
        addr1.setPostalcode("0160");
        addr1.setPostalcity("Oslo");
        addresses.put("work", addr1);

        DeliveryAddress addr2 = new DeliveryAddress();
        addr2.setAddressLine1("Møllefaret 30E");
        addr2.setPostalcode("0750");
        addr2.setPostalcity("Oslo");
        addresses.put("home", addr2);
        customer.setDeliveryaddresses(addresses);

        Map<String, EmailAddress> emailAddressMap = new HashMap<>();
        EmailAddress email1 = new EmailAddress("totto@tott.org", "hjemme, privat, OID");
        emailAddressMap.put("hjemme", email1);
        EmailAddress email2 = new EmailAddress("totto@cantara.no", "opensource, privat, OID");
        emailAddressMap.put("community", email2);
        EmailAddress email3 = new EmailAddress("totto@capraconsulting.no", "jobb, Capra, OID");
        emailAddressMap.put("jobb", email3);
        EmailAddress email4 = new EmailAddress("thor.henning.hetland@nmd.no", "jobb, kunde, OID");
        emailAddressMap.put("kobb-kunde", email4);
        customer.setEmailaddresses(emailAddressMap);


        Map<String, PhoneNumber> phoneNumberMap = new HashMap<>();
        PhoneNumber p1 = new PhoneNumber("jobb", "91905054");
        phoneNumberMap.put("tja", p1);
        PhoneNumber p2 = new PhoneNumber("privat", "96909999");
        phoneNumberMap.put("tja", p2);

        customer.setPhonenumbers(phoneNumberMap);

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(customer));

    }

}
