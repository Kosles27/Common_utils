package emailUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static propertyUtils.PropertyUtils.getGlobalProperty;


public class GmailOldSchool {
    Store store;
    Folder inbox, used;
    String email;
    String password;
    String subject;
    Date receivedDate;
    String content;
    Session session;

    private RetrievedEmail retrievedEmail = new RetrievedEmail();
    private static Logger logger = LoggerFactory.getLogger(GmailOldSchool.class);
    public GmailOldSchool() {
    }

    public GmailOldSchool(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public RetrievedEmail getRectrievedEmail() {
        return retrievedEmail;
    }

    public void setRetrievedEmail(RetrievedEmail retrievedEmail) {
        this.retrievedEmail = retrievedEmail;
    }

    public Session gmailConnect() {

        Session session = null;
        try {
            Properties prop = System.getProperties();
            prop.setProperty("mail.store.protocol", "imaps");
            prop.put("mail.smtp.host", "smtp.gmail.com");
            // TODO validate following line
            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");


            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS

            session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email, password);
                        }
                    });

            store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, password);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setSession(session);
        return session;
    }

    public void gmailConnect2() {
        Session session = null;
        try {
            Properties prop = new Properties();

            prop.put("mail.smtp.host", "smtp.gmail.com");
            // TODO validate following line
            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS

            // TODO validate following line
            // prop.put("mail.transport.protocol", "smtp");

            // TODO validate following line
            // prop.put("mail.smtp.starttls.required","true");

            session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email, password);
                        }
                    });
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
    }

    public void gmailCloseConnection() {
        try {
            if (store != null && store.isConnected()) {
                inbox.close(true);
                store.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Message[] gmailGetMessages(String folderName) {
        try {
            inbox = store.getFolder(folderName);
            inbox.open(Folder.READ_WRITE);
            return inbox.getMessages();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void gmailSendEmailTLS(
            String fromMail, String fromPassword, String[] toMails, String subject, String content) {
        try {
            Properties prop = new Properties();

            prop.put("mail.smtp.host", "smtp.gmail.com");
            // TODO validate following line
            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");


            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS

            // TODO validate following line
            // prop.put("mail.transport.protocol", "smtp");

            // TODO validate following line
            // prop.put("mail.smtp.starttls.required","true");

            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(fromMail, fromPassword);
                        }
                    });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromMail));
                String to = String.join(",", toMails);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setFrom(new InternetAddress("iqshipmail@gmail.com"));
                message.setSubject(subject);
                message.setText(content);

                Transport.send(message);

                System.out.println("Done");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void sendEmail(String fromMail, String toMail, String subject, String content) {
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(fromMail));
            //String to = String.join(",", toMails);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
            message.setFrom(new InternetAddress(fromMail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            System.out.println("Done");
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
    }

    public void gmailSendEmailSsl(
            String fromMail, String fromPassword, String[] toMails, String subject, String content) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587"); //465
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "587");  //465
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromMail, fromPassword);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromMail));
            String to = String.join(",", toMails);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);

            System.out.println("Done");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void ReplyToSender(Message message) {
        try {
            System.out.println("Reply-to: " + InternetAddress.toString(message.getReplyTo()));
            message.getReplyTo();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public String gmailGetMessageSubject(Message message) {
        try {
            return message.getSubject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Date gmailGetMessageReceivedDate(Message message) {
        try {
            return message.getReceivedDate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Address[] gmailGetMessageSender(Message message) {
        try {
            return message.getFrom();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for mail attachment then saves it to the given path
     *
     * @param message  eMail contains the attachment
     * @param filePath desired location which we want to save the attachment to
     * @return the complete absolute path and the file name
     */
    public String getAttachments(Message message, String filePath) {
        Multipart multiPart;
        String destFilePath = null;
        try {
            multiPart = (Multipart) message.getContent();
            for (int i = 0; i < multiPart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // save an attachment from a MimeBodyPart to a file
                    destFilePath = filePath + part.getFileName();
                    FileOutputStream output = new FileOutputStream(destFilePath);

                    InputStream input = part.getInputStream();

                    byte[] buffer = new byte[4096];

                    int byteRead;

                    while ((byteRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, byteRead);
                    }
                    output.close();
                }
            }
        } catch (IOException | MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return destFilePath;
    }

    public String getAttachments(Message message) {
        return getAttachments(message, "C:\\Attachment\\");
    }

    public String gmailGetMessageContent(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof Multipart) {
                StringBuilder messageContent = new StringBuilder();
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    Part part = multipart.getBodyPart(i);
                    if (part.isMimeType("text/plain")) {
                        System.out.println("part 1");
                        messageContent.append(part.getContent().toString());
                    } else if (part.isMimeType("text/html")) {
                        System.out.println("part 2");
                        String s = part.getContent().toString();
                        if (s != null) {
                            messageContent.append(part.getContent().toString());
                        }

                    } else if (part.isMimeType("message/rfc822")) {
                        System.out.println("///////////////////////  part 3");
                        String s = part.getContent().toString();
                        if (s != null) {
                            messageContent.append(part.getContent().toString());
                        }

                    }
                    // check if the content has attachment
                    else if (part.isMimeType("multipart/*")) {
                        System.out.println("/////////////////////////////This is a Multipart");
                        System.out.println("---------------------------");
                        Multipart mp = (Multipart) part.getContent();
                        int count = mp.getCount();
                        for (int t = 0; t < count; t++) {
                            String s = mp.getBodyPart(t).toString();
                            if (s != null) {
                                messageContent.append(mp.getBodyPart(t).getContent().toString());
                            }
                        }
                    } else {
                        System.out.println(" /////////////////////////////  part 4");
                        messageContent.append(part.getContent().toString());
                    }

                }
                return messageContent.toString();
            }
            return content.toString();

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return null;
        }
    }

    public Message gmailGetMessageBySubject(String subjectContains) {
        try {
            Message[] messages = gmailGetMessages("Inbox");

            // From new to old
            int messagesCount = messages.length;
            for (int i = messagesCount - 1; i > 0; i--) {
                String subject = gmailGetMessageSubject(messages[i]);
                if (subject.contains(subjectContains))
                    return messages[i];
            }
            return null;

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return null;
        }
    }

    public Date changeDateFormat(Date emailDate) {
        Date date = null;
        try {
            SimpleDateFormat DateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy ");
            SimpleDateFormat NewDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String stringDate = DateFormat.format(emailDate);
            System.out.println(NewDateFormat.format(DateFormat.parse(stringDate)));
            //NewDateFormat.format(DateFormat.parse(stringDate));
            date = NewDateFormat.parse(NewDateFormat.format(DateFormat.parse(stringDate)));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return date;
    }

    /**
     * method retrieves email by subject (complete or just its beginning), sender account email address,
     * and starting date
     *
     * @param subject                   subject (complete or just its beginning - cf. parameter isSubjectComplete)
     * @param senderAccountEmailAddress sender account email address
     * @param startingDate              starting date
     * @param isSubjectComplete         true when subject is complete, false when only the subject's beginning is provided
     * @return list of RetrievedEmail instances
     * @modifier Dafna Genosar
     * @modified 22.11.2021
     */
    public List<RetrievedEmail> retrieveEmail(String subject, String senderAccountEmailAddress, Date startingDate, boolean isSubjectComplete) {

        try {
            List<RetrievedEmail> retrievedEmailList = new ArrayList<>();
            Message[] messages = gmailGetMessages("Inbox");

            // From new to old
            int messagesCount = messages.length;
            for (int i = messagesCount - 1; i > 0; i--) {
                this.subject = gmailGetMessageSubject(messages[i]);
                receivedDate = gmailGetMessageReceivedDate(messages[i]);
                Address[] address = gmailGetMessageSender(messages[i]);
                //System.out.println(startingDate);
                //System.out.println(gmailOldSchool.changeDateFormat(receivedDate));
                //System.out.println(startingDate.toString().equals(gmailOldSchool.changeDateFormat(receivedDate).toString()));

                if (this.subject != null) {
                    if (startingDate.compareTo(receivedDate) <= 0) {
                        if (((isSubjectComplete && (this.subject.equals(subject))) ||
                                (!isSubjectComplete && (this.subject.contains(subject)))) &&
                                isSender(address, senderAccountEmailAddress))
                        {
                            String content = gmailGetMessageContent(messages[i]);

                            // String from = InternetAddress.toString(messages[i].getFrom());
                            // String to = InternetAddress.toString(messages[i].getReplyTo());

                            setSubject(this.subject);
                            setReceivedDate(receivedDate);
                            setContent(content);
                            for (Address a : address) {
                                System.out.println("Address : " + a);

                            }
                            // setReceivedemailsnotifications(receivedemailsnotifications);

                            // receivedemailsnotifications.setTital(getTital());
                            // receivedemailsnotifications.setSubject(getSubject());
                            // receivedemailsnotifications.setContent(getContent());
                            // receivedemailsnotifications.setReceivedDate(getReceivedDate());
                            setRetrievedEmail(new RetrievedEmail(getSubject(), getContent(), getReceivedDate(), messages[i]));


                            retrievedEmailList.add(getRectrievedEmail());
                        }
                    } else {
                        break;
                    }
                }
            }
            if(retrievedEmailList.size() == 0)
                System.out.println("No emails were retrieved");

            return retrievedEmailList;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * method reply email by subject (complete or just its beginning), sender account email address,
     * and starting date
     *
     * @param user              the email sender address
     * @param password          - password sender
     * @param theMessageToReply the message to reply
     * @return list of RetrievedEmail instances
     */
    public List<RetrievedEmail> replyTo(Message theMessage, String user, String password, String theMessageToReply) {
        try {
            List<RetrievedEmail> retrievedEmailList = new ArrayList<>();
            String content = gmailGetMessageContent(theMessage);


            String replyTo = InternetAddress.toString(theMessage
                    .getReplyTo());
            if (replyTo != null) {
                System.out.println("Reply-to: " + replyTo);
            }

            Message replyMessage = new MimeMessage(session);
            replyMessage = (MimeMessage) theMessage.reply(false);
            replyMessage.setFrom(new InternetAddress(replyTo));
            replyMessage.setText(theMessageToReply);
            replyMessage.setReplyTo(theMessage.getReplyTo());

            // Send the message by authenticating the SMTP server
            // Create a Transport instance and call the sendMessage
            Transport t = session.getTransport("smtp");
            try {
                //connect to the smpt server using transport instance
                //change the user and password accordingly
                t.connect(user, password);
                t.sendMessage(replyMessage,
                        replyMessage.getAllRecipients());
            } finally {
                t.close();
            }
            System.out.println("message replied successfully ....");

            return retrievedEmailList;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    /**
     * method reply email by subject (complete or just its beginning), sender account email address,
     * and starting date
     *
     * @param subject                   subject (complete or just its beginning - cf. parameter isSubjectComplete)
     * @param senderAccountEmailAddress sender account email address
     * @param startingDate              starting date
     * @param isSubjectComplete         true when subject is complete, false when only the subject's beginning is provided
     * @param theMessageToForward       the message to reply
     * @return list of RetrievedEmail instances
     */
    public List<RetrievedEmail> forward(Message theMessage, String subject, String senderAccountEmailAddress, Date startingDate,
                                        boolean isSubjectComplete, String theMessageToForward) {
        try {
            List<RetrievedEmail> retrievedEmailList = new ArrayList<>();
            String content = gmailGetMessageContent(theMessage);


            // Get all the information from the message
            String from = InternetAddress.toString(theMessage.getFrom());
            if (from != null) {
                System.out.println("From: " + from);
            }

            // compose the message to forward
            Message message2 = new MimeMessage(session);
            message2.setSubject("Fwd: " + theMessage.getSubject());
            message2.setFrom(new InternetAddress(from));
            message2.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(senderAccountEmailAddress));


            // Create your new message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(theMessageToForward);


            // Create a multi-part to combine the parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Create and fill part for the forwarded content
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(theMessage.getDataHandler());

            // Add part to multi part
            multipart.addBodyPart(messageBodyPart);

            // Associate multi-part with message
            message2.setContent(multipart);

            // Send message
            Transport.send(message2);

            System.out.println("message forwarded ....");


            return retrievedEmailList;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public List<RetrievedEmail> retrieveEmailByCompleteSubject(String subject, String emailAddress, Date startingDate, boolean sendToArchive) {
        return retrieveEmail(subject, emailAddress, startingDate, true);
    }

    public List<RetrievedEmail> retrieveEmailThatContainsSubject(String subject, String emailAddress, Date startingDate) {
        return retrieveEmail(subject, emailAddress, startingDate, false);
    }


    public void downloadAttachment(String subject, Date startingDate, String emailAddress) {
        Message[] messages = gmailGetMessages("Inbox");
        // From new to old
        int messagesCount = messages.length;
        for (int i = messagesCount - 1; i > 0; i--) {
            this.subject = gmailGetMessageSubject(messages[i]);
            receivedDate = gmailGetMessageReceivedDate(messages[i]);
            Address[] address = gmailGetMessageSender(messages[i]);
            //System.out.println(startingDate);
            //System.out.println(gmailOldSchool.changeDateFormat(receivedDate));
            //System.out.println(startingDate.toString().equals(gmailOldSchool.changeDateFormat(receivedDate).toString()));
            if (subject.equals(this.subject) && startingDate.equals(changeDateFormat(receivedDate)) && isSender(address, emailAddress)) {
                getAttachments(messages[i]);
            }
        }
    }

    /**
     * @param subject                   subject
     * @param senderAccountEmailAddress sender account email address
     * @param startingDate              starting date
     * @param fileName                  file name
     * @return true if file name is included in attachments to recentest email to be found according to subject,
     * sender account email address, and starting date
     */
    public boolean isFileNameIncludedInAttachmentsToRecentestEmail(String subject, String senderAccountEmailAddress,
                                                                   Date startingDate, String fileName) {
        boolean isFileNameIncludedInAttachmentsToLastEmail = false;
        Message[] messages = gmailGetMessages("Inbox");
        // From new to old
        int messagesCount = messages.length;
        for (int i = messagesCount - 1; i > 0; i--) {
            String receivedSubject = gmailGetMessageSubject(messages[i]);
            Date receivedDate = gmailGetMessageReceivedDate(messages[i]);
            Address[] address = gmailGetMessageSender(messages[i]);
            if (subject.equals(receivedSubject) &&
                    (startingDate.compareTo(changeDateFormat(receivedDate)) <= 0) &&
                    isSender(address, senderAccountEmailAddress)) {
                Multipart multiPart;
                try {
                    multiPart = (Multipart) messages[i].getContent();
                    for (int j = 0; j < multiPart.getCount(); j++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(j);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            if (fileName.equals(part.getFileName())) {
                                isFileNameIncludedInAttachmentsToLastEmail = true;
                                break;
                            }
                        }
                    }
                } catch (IOException | MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return isFileNameIncludedInAttachmentsToLastEmail;
    }

    public boolean isSender(Address[] address, String emailAddress) {
        for (Address a : address) {
            if (emailAddress == null) {
                return true;
            }
            String sender = a.toString();
            if (sender.contains("<")) {
                sender = sender.substring(sender.toString().indexOf("<") + 1, sender.toString().indexOf(">"));
            }
            if (sender.toUpperCase().contains(emailAddress.toUpperCase())) {
                return true;
            }
            System.out.println("Address : " + a);
        }
        return false;
    }

    public void archiveMessage(Message message) throws MessagingException {
        Message[] messages = new Message[]{message};
        used = store.getFolder("Used");
        if (!used.exists()) {
            used.create(Folder.HOLDS_MESSAGES);
        }
        inbox.copyMessages(messages, used);

        message.setFlag(Flag.DELETED, true);

    }

    public Element getNodeElementFromBody(String beginningOfSubject, String emailAddress, Date startingTime, String elementCssValue) {
        RetrievedEmail mail = retrieveEmailThatContainsSubject(beginningOfSubject, emailAddress, startingTime).get(0);
        Document doc = Jsoup.parse(mail.getContent());
        return doc.select(elementCssValue).get(0);
    }

    public String getNodeElementTextFromBody(String beginningOfSubject, String emailAddress, Date startingTime, String elementCssValue) {
        return getNodeElementFromBody(beginningOfSubject, emailAddress, startingTime, elementCssValue).text();
    }

    public String getNodeNextElementSiblingTextFromBody(String beginningOfSubject, String emailAddress, Date startingTime, String elementCssValue) {
        return getNodeElementFromBody(beginningOfSubject, emailAddress, startingTime, elementCssValue).nextElementSibling().text();
    }

    /**download attach files by subject and sender and save it in /target/downloads/ directory
     *
     * @param emailAddress - the email address of the sender
     * @param subject - the subject of the mail
     * @author - Lior Umflat
     * @since - 2.6.2021*/
    public void downloadAttachment(String subject,String emailAddress) {
        Message[] messages = gmailGetMessages("Inbox");

        String downloadPath = getGlobalProperty("download_path");
        String currentBase = System.getProperty("user.dir");

        // From new to old
        int messagesCount = messages.length;
        for (int i = messagesCount - 1; i > 0; i--) {
            this.subject = gmailGetMessageSubject(messages[i]);
            Address[] address = gmailGetMessageSender(messages[i]);

            if (subject.equals(this.subject) && isSender(address, emailAddress)) {

                try {
                    //if dir doesn't exist create it
                    File theDir = new File(currentBase + downloadPath);
                    if (!theDir.exists()) {
                        theDir.mkdirs();
                    }
                }
                catch(Exception e)
                {
                    logger.error("failed to create directory /target/downloads/ " + e.toString());
                }
                //save the attached file in /target/downloads
                getAttachments(messages[i],currentBase+downloadPath);
            }
        }
    }


    /**download attach files from mail
     *
     * @param email - the email where we search for the files
     * @author - Tzvika Sela
     * @since - 11.8.2021 */
    public void downloadAttachmentFromMail(RetrievedEmail email){

        String downloadPath = getGlobalProperty("download_path");
        String currentBase = System.getProperty("user.dir");

        try {
            //if dir doesn't exist create it
            File theDir = new File(currentBase + downloadPath);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
        }
        catch(Exception e)
        {
            logger.error("failed to create directory /target/downloads/ " + e.toString());
        }


        getAttachments(email.getMessage(),currentBase+downloadPath);
    }

    /**
     * get last X messages from sent file in gmail
     * @param lastXmessages -   the range to retrieve the last X messages
     * @return - list of last X messages subject
     * @since 14.12.2023
     * @author abo_saleh.rawand
     */
    public  List<String> getSentMails(int lastXmessages) {
        // Set up properties for the mail session
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");

        List<String> messagesSubject = null;
        try {
            // Create a session with authentication
            Session session = Session.getDefaultInstance(properties);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, password);

            // Get the Sent folder
            Folder sentFolder = store.getFolder("[Gmail]/Sent Mail");
            sentFolder.open(Folder.READ_ONLY);

            // Get the total number of sent emails
            int totalMessages = sentFolder.getMessageCount();

            // Calculate the range to retrieve the last X messages
            int start = Math.max(1, totalMessages - (lastXmessages-1)); // Start from the index-1 message from the end
            int end = totalMessages;

            // Retrieve the last X sent emails
            Message[] messages = sentFolder.getMessages(start, end);
            messagesSubject = new ArrayList<>();
            // add the messages subject to list
            for (Message message : messages) {
                messagesSubject.add(message.getSubject());
            }
            // Close the folders and store
            sentFolder.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messagesSubject;
    }

    /** Send gmail mail with attachment
     *
     * @param gmailOldSchool instance of GmailOldSchool who is already connected and has a session.
     *                       connection example -
     *                       emailUtils.GmailOldSchool gmail = new GmailOldSchool(csrTestUser.get("email"),csrTestUser.get("emailPassword"));
     *                       gmail.setSession(gmail.gmailConnect());
     *
     * @param fromMail from email address
     * @param toMail to email address
     * @param subject subject of the mail
     * @param content content of the mail
     * @param filePath path of the file
     * @param fileName name of the file
     * @author umflat.lior
     * @since 3.1.2024
     */
    public static void sendEmailWithAttachment(GmailOldSchool gmailOldSchool,String fromMail, String toMail, String subject, String content,String filePath,String fileName) {
        try {
            Message message = new MimeMessage(gmailOldSchool.getSession());
            message.setFrom(new InternetAddress(fromMail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
            message.setFrom(new InternetAddress(fromMail));
            message.setSubject(subject);
            message.setText(content);
            DataSource source = new FileDataSource(filePath);
            message.setDataHandler(new DataHandler(source));
            message.setFileName(fileName);

            Transport.send(message);

            logger.info("Email with attachment was sent");

        } catch (Exception e) {

            throw new Error("Email with attachment wasn't sent. Exception: " +e.getMessage());
        }
    }

}
