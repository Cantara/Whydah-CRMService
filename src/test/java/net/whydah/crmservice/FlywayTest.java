package net.whydah.crmservice;

import static org.junit.Assert.assertEquals;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.extensions.crmcustomer.types.Customer;
import net.whydah.sso.extensions.crmcustomer.types.DeliveryAddress;
import net.whydah.sso.extensions.crmcustomer.types.EmailAddress;
import net.whydah.sso.extensions.crmcustomer.types.PhoneNumber;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

public class FlywayTest {

	 public Customer createExampleCustomern(String id) throws Exception {
	        Customer customer = new Customer();
	        customer.setId(id);
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
	        return customer;
	    }
	 
	ObjectMapper jsonMapper = new ObjectMapper();
	EmbeddedPostgres postgres;
	
	@Before
	public void setup() {
		postgres = new EmbeddedPostgres(Version.V9_6_8);	
	}
	
	@After
	public void close() {
		postgres.stop();
		
	} 
	
	@Test
	@Ignore //uncomment this to test locally
	public void testFlyway() throws Exception {
		// starting Postgres
		//final EmbeddedPostgres postgres = new EmbeddedPostgres(Version.V9_6_8);
		final String url = postgres.start("localhost", 5433, "crmservice", "crmadmin", "secret");
		
		new Main().migrateDb("localhost", 5433, "crmservice", "crmadmin", "secret");
		
		// connecting to a running Postgres and feeding up the database
		final Connection conn = DriverManager.getConnection(url);
		//INSERT INTO customers (customer_id, data) values(?, ?)
		
		PreparedStatement insert_statement = conn.prepareCall("INSERT INTO customers (customer_id, data) values(?, ?)");
		String id = UUID.randomUUID().toString();
        insert_statement.setString(1, id);
        insert_statement.setObject(2, jsonMapper.writeValueAsString(createExampleCustomern(id)));
        insert_statement.execute();
        
        Statement select_statement = conn.createStatement();
        assertEquals(select_statement.execute("SELECT * FROM customers;"), true);
        ResultSet set = select_statement.getResultSet();
        assertEquals(set.next(), true);
        assertEquals(set.getString("data").trim(), jsonMapper.writeValueAsString(createExampleCustomern(id)));
       

		conn.close();
		
	}
}
