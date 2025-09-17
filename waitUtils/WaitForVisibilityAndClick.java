package waitUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seleniumUtils.ElementWrapper;

import java.util.function.Function;

import static systemUtils.SystemCommonUtils.sleep;


public class WaitForVisibilityAndClick implements Function<WebElement, Boolean>
{
    private final Logger logger = LoggerFactory.getLogger(WaitForVisibilityAndClick.class);
    private final WebDriver driver;

    public WaitForVisibilityAndClick(WebDriver driver)
    {
        this.driver = driver;
    }

    /**
     * Overriding apply method to click on element
     * @param elem WebElement to click
     * @return true if click succeeded. false otherwise
     */
    @Override
    public Boolean apply(WebElement elem)
    {

        try
        {

            ElementWrapper.waitForVisibilityAndClick(driver, elem, 10);
            return true;
        }
        catch (Exception e)
        {
            logger.info("Visibility of " + elem + " not found in this try");
            sleep(5000);
            return false;
        }


    }


}
