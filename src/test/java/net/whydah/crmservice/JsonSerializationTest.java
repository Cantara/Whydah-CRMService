package net.whydah.crmservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.extensions.crmcustomer.types.Customer;
import net.whydah.sso.extensions.crmcustomer.types.DeliveryAddress;
import net.whydah.sso.extensions.crmcustomer.types.EmailAddress;
import net.whydah.sso.extensions.crmcustomer.types.PhoneNumber;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JsonSerializationTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() {
    	HashMap<String, String> model = new HashMap<>();
		model.put("name", "abc");
		model.put("url", "http://google.com");
        ObjectMapper mapper = new ObjectMapper();
        try {
			System.out.println(mapper.writeValueAsString(model));
		} catch (JsonProcessingException e1) {
			
		}
    }
    
    @Test
    public void createExampleCustomerAndCheckJson() throws Exception {
        Customer customer = new Customer();
        customer.setId("12345");
        customer.setFirstname("First");
        customer.setMiddlename("Middle");
        customer.setLastname("Lastname");

        customer.setSex("M");
        customer.setBirthdate(new Date());

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
        customer.setDefaultAddressLabel("work");

        Map<String, EmailAddress> emailAddressMap = new HashMap<>();
        EmailAddress email1 = new EmailAddress("totto@tott.org", "hjemme, privat, OID", true);
        emailAddressMap.put("hjemme", email1);
        EmailAddress email2 = new EmailAddress("totto@cantara.no", "opensource, privat, OID", true);
        emailAddressMap.put("community", email2);
        EmailAddress email3 = new EmailAddress("totto@capraconsulting.no", "jobb, Capra, OID", true);
        emailAddressMap.put("jobb", email3);
        EmailAddress email4 = new EmailAddress("thor.henning.hetland@nmd.no", "jobb, kunde, OID", true);
        emailAddressMap.put("kobb-kunde", email4);
        customer.setEmailaddresses(emailAddressMap);
        customer.setDefaultEmailLabel("hjemme");


        Map<String, PhoneNumber> phoneNumberMap = new HashMap<>();
        PhoneNumber p1 = new PhoneNumber("91905054", "jobb", false);
        phoneNumberMap.put("tja", p1);
        PhoneNumber p2 = new PhoneNumber("96909999", "privat", true);
        phoneNumberMap.put("tja", p2);
        customer.setDefaultPhoneLabel("jobb");

        customer.setPhonenumbers(phoneNumberMap);

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(customer));

    }

}
