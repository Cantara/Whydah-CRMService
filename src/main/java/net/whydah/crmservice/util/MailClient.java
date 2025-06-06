package net.whydah.crmservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.commands.extras.CommandSendScheduledMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class MailClient {
    private static final Logger log = LoggerFactory.getLogger(MailClient.class);

    private static final boolean SMTP_AUTH = true;
    private static final boolean SMTP_STARTTTLS_ENABLE = true;
    private String smtpHost;
    private String smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private String subject;
    private String bodyTemplate;
    private String fromAddress;
    private String uasUrl;
    private SecurityTokenServiceClient serviceClient;


    public MailClient(String uasUrl, String smtpHost, String smtpPort, String smtpUsername, String smtpPassword, String subject, String bodyTemplate, String fromAddress) {
        this.uasUrl = uasUrl;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.subject = subject;
        this.bodyTemplate = bodyTemplate;
    }

    public void sendVerificationEmailLocally(String recipients, String verificationLink) {

        String body = bodyTemplate.formatted(verificationLink);

        log.debug("Sending email to recipients={}, subject={}, body={}", recipients, subject, body);

        //Gmail properties
        Properties smtpProperties = new Properties();

        smtpProperties.put("mail.smtp.host", smtpHost);
        smtpProperties.put("mail.smtp.socketFactory.port", smtpPort);
        smtpProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.port", "465");

        Session session = Session.getInstance(smtpProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUsername, smtpPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            message.setSubject(subject);

            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);
            log.info("Sent email to " + recipients);
        } catch (MessagingException e) {
            String smtpInfo = "Error sending email. SMTP_HOST=" + smtpHost + ", SMTP_PORT=" + smtpPort + ", smtpUsername=" + smtpUsername + ", subject=" + subject;
            if (e.getCause() instanceof AuthenticationFailedException) {
                log.warn("Failed to send mail due to missconfiguration? Reason {}", e.getCause().getMessage());
            }
            throw new RuntimeException(smtpInfo, e);
        }
    }

    public void sendVerificationEmailViaWhydah(SecurityTokenServiceClient tokenServiceClient, String recipients, String name, String verificationLink) {
        this.serviceClient = tokenServiceClient;
        try {
        	//String body = EmailBodyGenerator.generateVerificationLink(verificationLink, name);
        	HashMap<String, String> model = new HashMap<>();
        	model.put("name", name);
        	model.put("url", verificationLink);
        	ObjectMapper mapper = new ObjectMapper();
        	String params = mapper.writeValueAsString(model);
        	String templateName = "EmailVerification.ftl";
            long timestamp = new Date().getTime() + 8 * 1000;   // send mail after 8 seconds
            log.debug("Sending email to recipients={}", recipients);

            new CommandSendScheduledMail(URI.create(uasUrl), serviceClient.getMyAppTokenID(), Long.toString(timestamp), recipients, "", templateName, params, 0).queue();
            log.info("Sent email to " + recipients);
        } catch (Exception e) {
            log.warn("Failed to send mail due to missconfiguration? Reason {}", e.getCause().getMessage());
        }
    }

}
