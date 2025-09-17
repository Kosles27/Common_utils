package seleniumUtils;

import enumerations.MessageLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waitUtils.WaitWrapper;

import static reportUtils.Report.reportAndLog;

/**
 * Class that supports ZIM login page.
 * This page it the first page that opens when going to the app url and enables login to the app
 * This page url starts with <a href="https://login.microsoftonline.com/">...</a>
 * Many ZIM apps use this common login page, like Scheduling, Allocation, CRM.
 * Customer Are app does not use this login page for example
 * @author genosar.dafna
 * @since 23.04.2023
 */
public class ZimLoginPage extends BasePage {

    private final static Logger logger = LoggerFactory.getLogger(ZimLoginPage.class);

    public ZimLoginPage(WebDriver driver) {
        super(driver);
    }

    private WebElement txt_user;

    private WebElement txt_password;

    @FindBy(how = How.ID, using = "idSIButton9")
    private WebElement btn_signIn;

    /**
     * Login - the name of the method is line this on purpose, in case in your project you would like to create a method login() that calls this method and then returns your desired page
     * @param user username
     * @param password password
     * @author genosar.dafna
     * @since 23.04.2023
     * @since 29.02.2024
     */
    public void logIn(String user, String password)
    {
        reportAndLog(String.format("Login: <br> User: %s", user), MessageLevel.INFO);

        try {
            txt_user = WaitWrapper.waitForVisibilityOfElementLocated(driver, By.name("loginfmt"), "Username field", timeout);
        }
        catch(Exception e){
            throw new Error(e.getMessage());
        }

        try {
            logger.info(String.format("Enter username: '%s'", user));
            txt_user.sendKeys(user);
        }
        catch(Exception e){
            throw new Error(String.format("Failed to set username field to '%s'", user));
        }

        try {
            logger.info("Click 'Sign in button'");
            btn_signIn.click();
        }
        catch(Exception e3){
            throw new Error("'Next' button could not be found on login page");
        }

        try {
            txt_password = WaitWrapper.waitForVisibilityOfElementLocated(driver, By.name("passwd"), "Password field", timeout);
        }
        catch(Exception e){
            throw new Error(e.getMessage());
        }

        try {
            logger.info(String.format("Enter password", password));
            txt_password.sendKeys(password);
        }
        catch(Exception e2){
            throw new Error(String.format("Failed to set password field to '%s'", password));
        }

        try {
            logger.info("Click 'Sign in button'");
            btn_signIn.click();
        }
        catch(Exception e3){
            throw new Error("Sign In button could not be found on login page");
        }
    }
}
