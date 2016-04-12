package net.whydah.crmservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailClient {
    private static final Logger log = LoggerFactory.getLogger(MailClient.class);
    public static  String FROM_ADDRESS = "notworking@whydah.net";


    private static final boolean SMTP_AUTH = true;
    private static final boolean SMTP_STARTTTLS_ENABLE = true;
    private String smtpHost;
    private String smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private String subject;
    private String bodyTemplate;


    public MailClient(String smtpHost, String smtpPort, String smtpUsername, String smtpPassword, String subject, String bodyTemplate) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.subject = subject;
        this.bodyTemplate = bodyTemplate;
    }

    public void sendVerificationEmail(String recipients, String verificationLink) {

        String body = String.format(bodyTemplate, verificationLink);

        log.debug("Sending email to recipients={}, subject={}, body={}", recipients, subject, body);
        FROM_ADDRESS=smtpUsername;

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
            message.setFrom(new InternetAddress(FROM_ADDRESS));
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
}
