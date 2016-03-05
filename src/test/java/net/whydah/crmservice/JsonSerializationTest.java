package net.whydah.crmservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.crmservice.user.model.Customer;
import net.whydah.crmservice.user.model.DeliveryAddress;
import org.junit.Test;
import ratpack.func.Action;
import ratpack.http.MediaType;
import ratpack.http.client.RequestSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonSerializationTest {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void createDummyCustomer() throws Exception {
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

        customer.setDeliveryaddresses(addresses);

        System.out.println(mapper.writeValueAsString(customer));
    }

}
