package miscellaneousUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waitUtils.WaitWrapper;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class Loading {

    private static final Logger logger = LoggerFactory.getLogger(Loading.class);

    /**
     * Wait until the Progress bar indicator disappear (an element with @role = 'progressbar')
     * @author genosar.dafna
     * @since 10.10.2024
     */
    public static void waitUntilProgressBarDisappears(WebDriver driver)
    {
        int timeToWaitUntilProgressBarDisplays = 5;
        int timeToWaitUntilTheProgressBarDisappears = 60;

        waitUntilProgressBarDisappears(driver, timeToWaitUntilProgressBarDisplays, timeToWaitUntilTheProgressBarDisappears);
    }

    /**
     * Wait until the Progress bar indicator disappear (an element with @role = 'progressbar')
     * @param timeToWaitUntilProgressBarDisplays time to wait until the loading indicator displays (if at all) - In Seconds
     * @param timeToWaitUntilTheProgressBarDisappears time to wait until the loading indicator disappears - In Seconds
     * @author genosar.dafna
     * @since 10.10.2024
     * @since 10.11.2024
     */
    public static void waitUntilProgressBarDisappears(WebDriver driver, int timeToWaitUntilProgressBarDisplays, int timeToWaitUntilTheProgressBarDisappears)
    {
        List<WebElement> loadingProgressBars = new LinkedList<>();

        try {
            logger.info(String.format("Wait up to %d seconds for a loading indicator to display", timeToWaitUntilProgressBarDisplays));
            loadingProgressBars = WaitWrapper.waitForVisibilityOfAllElementsLocatedBy(driver, By.xpath("//*[@role='progressbar']"), "Loading indicator", timeToWaitUntilProgressBarDisplays);
        }
        catch (Throwable t){
            logger.info("Loading indicators did not display. No need to wait.");
        }

        if (loadingProgressBars.size() > 0){
            logger.info("Loading indicator(s) display(s)");
            logger.info("Wait until the Loading indicator(s) disappear(s)");

            try {
                WaitWrapper.waitForAllElementsToDisappear(driver, loadingProgressBars, "Loading indicator", timeToWaitUntilTheProgressBarDisappears);
            }
            catch (Throwable ex) {
                throw new Error(String.format("Loading indicator(s) did not disappear after %s seconds",timeToWaitUntilTheProgressBarDisappears));
            }
        }
    }
}
