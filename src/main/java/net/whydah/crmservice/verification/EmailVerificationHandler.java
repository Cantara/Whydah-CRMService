package net.whydah.crmservice.verification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import net.whydah.crmservice.customer.CustomerRepository;
import net.whydah.crmservice.security.Authentication;
import net.whydah.crmservice.util.MailClient;
import net.whydah.crmservice.util.SecurityTokenServiceClient;
import net.whydah.sso.extensions.crmcustomer.types.DeliveryAddress;
import net.whydah.sso.extensions.crmcustomer.types.EmailAddress;
import net.whydah.sso.user.helpers.TagsParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.util.MultiValueMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EmailVerificationHandler implements Handler {

	private static final Logger log = LoggerFactory.getLogger(EmailVerificationHandler.class);

	private final SecurityTokenServiceClient tokenServiceClient;
	private final CustomerRepository customerRepository;
	private final MailClient mailClient;
	//private final ActiveVerificationCache verificationMap;

	@Inject
	public EmailVerificationHandler(SecurityTokenServiceClient tokenServiceClient, CustomerRepository customerRepository, MailClient mailClient, @Named("hazelcast.config")String hazelcastConfig, @Named("gridprefix") String gridPrefix) {
		this.tokenServiceClient = tokenServiceClient;
		this.customerRepository = customerRepository;
		this.mailClient = mailClient;
		ActiveVerificationCache.init(hazelcastConfig, gridPrefix);
	}

	@Override
	public void handle(Context ctx) throws Exception {

		final String customerRef = ctx.getPathTokens().get("customerRef");
		log.info("Map found " + System.lineSeparator() + ActiveVerificationCache.getMapInfo());
		
		
		if ("useradmin".equalsIgnoreCase(Authentication.getAuthenticatedUser().getUid().toString())) {
		} else if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
			log.debug("User {} with personRef {} not authorized to get data for personRef {}", Authentication.getAuthenticatedUser().getUid(), Authentication.getAuthenticatedUser().getPersonRef(), customerRef);
			ctx.clientError(401);
			return;
		}

		MultiValueMap<String, String> queryParams = ctx.getRequest().getQueryParams();
		if (queryParams == null || queryParams.get("email") == null) {
			ctx.clientError(400); //Bad request
			return;
		}

		final String email = queryParams.get("email");
		final String token = queryParams.get("emailverificationtoken");
		final String linkurl = queryParams.get("linkurl");
		final String userticket = queryParams.get("userticket");
		
		

		if (token == null) {
			//Send email verification token
			String generatedToken = UUID.randomUUID().toString();

			StringBuilder builder = new StringBuilder(linkurl).
					append("?token=").append(generatedToken).
					append("&email=").append(email).
					append("&userticket=").append(userticket);

			String verificationLink = builder.toString();

			log.debug("added verificationlink: " + verificationLink);

			ActiveVerificationCache.addToken(email, generatedToken);
			
			log.debug("Ready to send email verificationmail. email:{}, token:{}, linkurl:{} ", email, token, linkurl);
			
			mailClient.sendVerificationEmailViaWhydah(tokenServiceClient, email, verificationLink);


			//set pending status to true
			Blocking.get(() -> customerRepository.getCustomer(customerRef)).then(customer -> {
				Map<String, EmailAddress> emailaddresses = customer.getEmailaddresses();
				for (EmailAddress emailAddress : emailaddresses.values()) {
					if (email.equalsIgnoreCase(emailAddress.getEmailaddress())) {
						emailAddress.setTags(TagsParser.addTag(emailAddress.getTags(), "pending", true));
						emailAddress.setTags(TagsParser.addTag(emailAddress.getTags(), "registrationTime", System.currentTimeMillis()));
					}
				}
				customerRepository.updateCustomer(customerRef, customer);
			});
			
			ctx.redirect(200, customerRef);
			
		} else {
			String expectedToken = ActiveVerificationCache.useToken(email);
			log.debug("Found verification token {} for {}", expectedToken, email);
			final boolean verified = (expectedToken != null && expectedToken.equals(token));
			if (verified) {
				log.debug("token {} matched", expectedToken);
				Blocking.get(() -> customerRepository.getCustomer(customerRef)).then(customer -> {
					Map<String, EmailAddress> emailaddresses = customer.getEmailaddresses();
					Map<String, EmailAddress> emailList = new HashMap<>();
					boolean foundMatch = false;
					for (EmailAddress emailAddress : emailaddresses.values()) {
						if (email.equalsIgnoreCase(emailAddress.getEmailaddress())) {
							emailAddress.setVerified(true);
							emailAddress.setTags(TagsParser.addTag(emailAddress.getTags(), "pending", false));
							emailAddress.setTags(TagsParser.addTag(emailAddress.getTags(), "verificationTime", System.currentTimeMillis()));
							foundMatch = true;
						}
						emailList.put(emailAddress.getEmailaddress(), emailAddress);
					}
					if (foundMatch) {
						//update verified emails
						for(DeliveryAddress da : customer.getDeliveryaddresses().values()) {
							da.setAddressLine1(updateEmailVerificationStatus(da.getAddressLine1(), emailList));
							da.setAddressLine2(updateEmailVerificationStatus(da.getAddressLine2(), emailList));
						}
						customerRepository.updateCustomer(customerRef, customer);
						log.debug("Email {} flagged as verified.", email);
					}
					ctx.redirect(200, customerRef);
				});

			} else {
				ctx.clientError(406); //Not acceptable
			}
		}
	}

	public static String updateEmailVerificationStatus(String addressLine, Map<String, EmailAddress> emails){
		try {
			if(addressLine!=null && !addressLine.equals("") && !addressLine.equals("null")) {
				JSONObject obj = new JSONObject(addressLine);
				JSONObject address = obj.getJSONObject("deliveryaddress");
				if(address!=null){

					if(address.has("contact")){
						JSONObject contact = address.getJSONObject("contact");
						if(contact.has("email")){
							String email = contact.getString("email");
							if(emails.containsKey(email.toLowerCase())){
								contact.put("emailConfirmed", emails.get(email).isVerified());
							}						
						}
					}

					if(address.has("addressLine1")) {
						address.put("addressLine1", updateEmailVerificationStatus(address.getString("addressLine1"), emails));
					}

					if(address.has("addressLine2")) {
						address.put("addressLine2", updateEmailVerificationStatus(address.getString("addressLine2"), emails));
					}

					return obj.toString();

				}
			}

			return addressLine;

		} catch (JSONException e) {
			return addressLine;
		}
	}

}
