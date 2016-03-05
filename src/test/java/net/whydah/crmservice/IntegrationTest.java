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
    private final String userId = "1234";
    private TestHttpClient client;

    @BeforeClass
    public static void beforeClass() throws IOException {
        crmservice = new MainClassApplicationUnderTest(Main.class);
    }

    @Before
    public void before() {
        client = TestHttpClient.testHttpClient(crmservice);
    }

    @Ignore
    @Test
   public void testCRUDUser() throws Exception {
        Customer user = createDummyUser(userId);

        String path = Main.CONTEXT_ROOT + "/user/"+user.getId();

        String originalFirstname = user.getFirstname();
        String originalLastname = user.getLastname();

        //Create userdata
        ReceivedResponse response =
                client.requestSpec(jsonRequestBody(user)).post(path);
        assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus().getCode());
        assertTrue(response.getHeaders().get("Location").endsWith(path));


        //Update userdata
        user.setFirstname("Integration");
        user.setLastname("Test");

        response = client.requestSpec(jsonRequestBody(user)).put(path);
        assertEquals(HttpURLConnection.HTTP_ACCEPTED, response.getStatus().getCode());
        assertTrue(response.getHeaders().get("Location").endsWith(path));

        //Read userdata
        response = client.get(path);
        Customer user1 = parseJson(response.getBody().getText());

        assertEquals(user.getId(), user1.getId());
        assertEquals(user.getFirstname(), user1.getFirstname());
        assertEquals(user.getLastname(), user1.getLastname());

        //Verify updated values
        assertFalse(originalFirstname.equals(user.getFirstname()));
        assertFalse(originalLastname.equals(user.getLastname()));

        //Delete userdata
        response = client.delete(path);
        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.getStatus().getCode());

        //Verify deletion
        response = client.get(path);
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.getStatus().getCode());

    }

    private Customer createDummyUser(String userId) {
        Customer user = new Customer();
        user.setId(userId);
        user.setFirstname("First");
        user.setLastname("Lastname");

        Map<String, DeliveryAddress> addresses = new HashMap<>();
        DeliveryAddress addr1 = new DeliveryAddress();
        addr1.setAddressLine1("Karl Johansgate 6");
            addr1.setPostalcode("0160");
            addr1.setPostalcity("Oslo");
        addresses.put("work", addr1);

        user.setDeliveryaddresses(addresses);

        return user;
    }

    private Action<RequestSpec> jsonRequestBody(Customer user) throws JsonProcessingException {
        return requestSpec -> requestSpec.getBody()
                .type(MediaType.APPLICATION_JSON)
                .text(mapper.writeValueAsString(user));
    }

    private Customer parseJson(String userJson) throws IOException {
        return mapper.readValue(userJson, Customer.class);
    }

}
