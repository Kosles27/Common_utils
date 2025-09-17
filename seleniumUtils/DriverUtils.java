package seleniumUtils;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@SuppressWarnings("unused")
public class DriverUtils
{
    private static final Logger logger = LoggerFactory.getLogger(DriverUtils.class);

    /**
     * Switching between all open windows and closing all except for the one that is sent to the method
     * @param driver WebDriver instance
     * @param windowToRemainOpenHandle The handle of the window to remain open
     */public static void closeAllWindowsExceptOne(WebDriver driver, String windowToRemainOpenHandle)
    {
        Set<String> windows = driver.getWindowHandles();

        for (String handle: windows)
        {
            driver.switchTo().window(handle);
            logger.info("Switched to window: " + handle);
            if(!windowToRemainOpenHandle.contentEquals(handle))
            {
                driver.close();
                logger.info("Closed window: " + handle);
            }
        }

        driver.switchTo().window(windowToRemainOpenHandle);
        logger.info("Switched to window: " + windowToRemainOpenHandle);
    }
}
