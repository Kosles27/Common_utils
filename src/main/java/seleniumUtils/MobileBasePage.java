package seleniumUtils;

import drivers.TesnetMobileDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a base page to all mobile pages. It extends BasePage
 */
public class MobileBasePage extends BasePage {

    private Logger logger = LoggerFactory.getLogger(MobileBasePage.class);
    protected TesnetMobileDriver mobileDriver;

    protected MobileBasePage(WebDriver driver) {
        super(driver);
        mobileDriver = (TesnetMobileDriver) driver;
    }

    public MobileBasePage(TesnetMobileDriver<?> driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        mobileDriver = driver;
        logger.debug("Created new page object instance: " + this.getClass());
    }

}
