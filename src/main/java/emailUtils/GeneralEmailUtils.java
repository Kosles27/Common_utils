package emailUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * class for general email functions
 * @author Yael Rozenfeld
 * @Since 31.05.2021
 */
public class GeneralEmailUtils {

    /**
     * Verify that email address is valid
     * @param emailAddress - Email to validate
     * @return true if email address is valid otherwise false
     * @author Yael Rozenfeld
     * @Since 31.05.2021
     */
    public static boolean isValidMail(String emailAddress){
        try {
            InternetAddress email = new InternetAddress(emailAddress);
            email.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
}
