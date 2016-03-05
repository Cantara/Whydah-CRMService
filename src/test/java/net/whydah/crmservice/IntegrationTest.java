package net.whydah.crmservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.crmservice.user.model.DeliveryAddress;
import net.whydah.crmservice.user.model.Customer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ratpack.func.Action;
import ratpack.http.MediaType;
import ratpack.http.client.ReceivedResponse;
import ratpack.http.client.RequestSpec;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.http.TestHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static MainClassApplicationUnderTest crmservice;
    private final String customerRef = "1234";
    private TestHttpClient client;

    @BeforeClass
    public static void beforeClass() throws IOException {
        crmservice = new MainClassApplicationUnderTest(Main.class);
    }

    @Before
    public void before() {
        client = TestHttpClient.testHttpClient(crmservice);
    }

    @Test
    public void testCRUDCustomer() throws Exception {
        Customer customer = createDummyCustomer(customerRef);

        String path = Main.CONTEXT_ROOT + "/customer/" + customer.getId();

        String originalFirstname = customer.getFirstname();
        String originalLastname = customer.getLastname();

        //Create userdata
        ReceivedResponse response =
                client.requestSpec(jsonRequestBody(customer)).post(path);
        assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus().getCode());
        assertTrue(response.getHeaders().get("Location").endsWith(path));


        //Update userdata
        customer.setFirstname("Integration");
        customer.setLastname("Test");

        response = client.requestSpec(jsonRequestBody(customer)).put(path);
        assertEquals(HttpURLConnection.HTTP_ACCEPTED, response.getStatus().getCode());
        assertTrue(response.getHeaders().get("Location").endsWith(path));

        //Read userdata
        response = client.get(path);
        Customer customer1 = parseJson(response.getBody().getText());

        assertEquals(customer.getId(), customer1.getId());
        assertEquals(customer.getFirstname(), customer1.getFirstname());
        assertEquals(customer.getLastname(), customer1.getLastname());

        //Verify updated values
        assertFalse(originalFirstname.equals(customer.getFirstname()));
        assertFalse(originalLastname.equals(customer.getLastname()));

        //Delete userdata
        response = client.delete(path);
        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.getStatus().getCode());

        //Verify deletion
        response = client.get(path);
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.getStatus().getCode());

    }

    private Customer createDummyCustomer(String customerRef) {
        Customer customer = new Customer();
        customer.setId(customerRef);
        customer.setFirstname("First");
        customer.setLastname("Lastname");

        Map<String, DeliveryAddress> addresses = new HashMap<>();
        DeliveryAddress addr1 = new DeliveryAddress();
        addr1.setAddressLine1("Karl Johansgate 6");
            addr1.setPostalcode("0160");
            addr1.setPostalcity("Oslo");
        addresses.put("work", addr1);

        customer.setDeliveryaddresses(addresses);

        return customer;
    }

    private Action<RequestSpec> jsonRequestBody(Customer customer) throws JsonProcessingException {
        return requestSpec -> requestSpec.getBody()
                .type(MediaType.APPLICATION_JSON)
                .text(mapper.writeValueAsString(customer));
    }

    private Customer parseJson(String customerJson) throws IOException {
        return mapper.readValue(customerJson, Customer.class);
    }

}
