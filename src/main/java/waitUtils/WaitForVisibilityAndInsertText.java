package waitUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seleniumUtils.ElementWrapper;

import java.util.function.Function;

import static systemUtils.SystemCommonUtils.sleep;


public class WaitForVisibilityAndInsertText implements Function<WebElement, Boolean>
{
    private Logger logger = LoggerFactory.getLogger(WaitForVisibilityAndClick.class);
    private WebDriver driver;
    private String text;

    public WaitForVisibilityAndInsertText(WebDriver driver, String text)
    {
        this.driver = driver;
        this.text = text;
    }

    /**
     * Overriding apply method to click on insert text to WebElement
     * @param elem WebElement to insert text to
     * @return true if click succeeded. false otherwise
     */
    @Override
    public Boolean apply(WebElement elem)
    {
        try
        {
            ElementWrapper.waitForVisibilityAndInsertText(driver, elem, text, 10);
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
