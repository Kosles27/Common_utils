package emailUtils;

import enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import propertyUtils.PropertyUtils;
import reportUtils.Report;

import javax.annotation.Nullable;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static propertyUtils.PropertyUtils.getGlobalProperty;
import static reportUtils.Report.reportAndLog;

@SuppressWarnings("unused")
public class ZimSMTP {

    private final Logger logger = LoggerFactory.getLogger(ZimSMTP.class);

    private static Integer amountOfSentEmails = 0;

    public ZimSMTP() {
    }

    /**
     * Sending email(HTML content) by SMTP without authentication
     * @param fromEmail from which email send
     * @param toEmail to which email send - TO
     * @param ccEmail to which email send copy - CC
     * @param subject subject of email
     * @param htmlContent content of email
     * @author reed.dakota
     * @since 07.05.2023
     */
    public void sendBySMTPNoAuthentication(String fromEmail, List<String> toEmail, List<String> ccEmail, String subject, String htmlContent) throws Exception {
        if (amountOfSentEmails < 500) {
        Properties properties = System.getProperties();
        String jobName = System.getProperty("jobName");
        String buildId = System.getProperty("BuildID");

            //in case of Kubernetes MSO cluster we use main smtp
        if (jobName!=null && buildId!=null && jobName.equalsIgnoreCase("mvn-test"))
            properties.setProperty("mail.smtp.host", getGlobalProperty("smtpHost"));
        else
            properties.setProperty("mail.smtp.host", getGlobalProperty("smtpdHost"));

        try {
            Session session = Session.getDefaultInstance(properties);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            for (String email : toEmail)
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            for (String email : ccEmail) {
                if (email.contains("@"))
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
            }
            message.setSubject(subject);
            // create the HTML part of the email
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html");
            // create the multipart message and add the HTML part to it
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            // add the multipart message to the email message
            message.setContent(multipart);
            // send the message
            Transport.send(message);
            reportAndLog("Email sent to: " + toEmail + " and to: " + ccEmail, MessageLevel.INFO);
                amountOfSentEmails += 1;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            reportAndLog("Failed to send email: " + mex.getMessage(),MessageLevel.ERROR);
            throw new Exception("Failed to send email: " + mex.getMessage());
        }
        }
        else {
            reportAndLog("Send limit(500) for a single run reached",MessageLevel.ERROR);
            throw new Exception("Send limit of 500 reached");
        }
    }

    /**
     * Sending email(HTML content) WITH images - by SMTP without authentication
     * @param fromEmail from which email send
     * @param toEmail to which email send - TO
     * @param ccEmail to which email send copy - CC
     * @param subject subject of email
     * @param htmlContent content of email
     * @param screenShotAttachedData screenshots data
     * @author reed.dakota
     */
    public static void sendBySMTPNoAuthenticationWithImage(String fromEmail, List<String> toEmail, @Nullable List<String> ccEmail, String subject, String htmlContent, HashMap<String, String> screenShotAttachedData) throws Exception {

        Properties properties = System.getProperties();
        String jobName = System.getProperty("jobName");
        String buildId = System.getProperty("BuildID");
        if (jobName != null && buildId != null && jobName.equalsIgnoreCase("mvn-test")) {
            properties.setProperty("mail.smtp.host", PropertyUtils.getGlobalProperty("smtpHost"));
        } else {
            properties.setProperty("mail.smtp.host", PropertyUtils.getGlobalProperty("smtpdHost"));
        }

        try {
            Session session = Session.getDefaultInstance(properties);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));

            for (String email : toEmail) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }

            if(ccEmail != null) {
                for (String email : ccEmail) {
                    if (email.contains("@")) {
                        message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
                    }
                }
            }

            message.setSubject(subject);

            // Main multipart/related container
            MimeMultipart relatedMultipart = new MimeMultipart("related");

            // HTML part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            relatedMultipart.addBodyPart(htmlPart);

            // Image parts
            for (Map.Entry<String, String> entry : screenShotAttachedData.entrySet()) {
                String contentId = entry.getKey(); // Used in HTML as <img src='cid:contentId'>
                String imagePath = entry.getValue().replace('\\', '/');

                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.attachFile(imagePath);
                imagePart.setDisposition(MimeBodyPart.INLINE); // key: mark it as inline
                imagePart.setHeader("Content-ID", "<" + contentId + ">");
                imagePart.setHeader("Content-Type", "image/png"); // or image/jpeg, etc.
                relatedMultipart.addBodyPart(imagePart);
            }

            message.setContent(relatedMultipart);
            Transport.send(message);
            Report.reportAndLog("Email sent to: " + toEmail + " and to: " + ccEmail, MessageLevel.INFO);

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            Report.reportAndLog("Failed to send email: " + e.getMessage(), MessageLevel.ERROR);
            throw new Exception("Failed to send email: " + e.getMessage());
        }
    }
}
