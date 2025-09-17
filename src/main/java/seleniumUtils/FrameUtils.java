package seleniumUtils;

import com.sun.istack.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Class to handle work with frames.
 * @author plot.ofek
 * @since 21-Nov-2021
 */
public class FrameUtils {

    private static Logger logger = LoggerFactory.getLogger(FrameUtils.class);

    /**
     * Switch to a certain Frame by its ID
     * If you need to wait for the frame you can use: WaitWrapper.waitForFrameToBeAvailableAndSwitchToIt()
     * @param driver the driver
     * @param frameId the frame ID
     * @return the driver after the switch
     * @author Dafna Genosar
     * @since 27.01.2022
     */
    public static WebDriver switchToFrame(WebDriver driver, String frameId)
    {
        return driver.switchTo().frame(frameId);
    }

    /**
     * Switch to default content
     * @param driver the driver
     * @return the default content driver
     * @author Dafna Genosar
     * @since 27.01.2022
     */
    public static WebDriver switchToDefaultContent(WebDriver driver)
    {
        return driver.switchTo().defaultContent();
    }

    /**
     * Method to find frame in which element is and move to it
     * @deprecated
     * This method is no longer acceptable to search for elements in frames.
     * Use FrameUtils#isElementFoundInAnyFrame() instead.
     *
     * @param driver Instance of WebDriver
     * @param originalFrame Frame to begin with (optional)
     * @param elementDescription Description of sought element
     * @param xpathString xpath string to find the element
     * @author jan.naor
     * @modifiedBy plot.ofek
     * @since 14-Nov-2021
     */
    @Deprecated
    public static void moveToFrameOfElement(WebDriver driver, @Nullable String originalFrame, String elementDescription, String xpathString) throws Exception {
        boolean elementFound=false;

        //Always start from top frame
        driver.switchTo().defaultContent();

        //originalFrame would typically be an iframe that has additional frames within it.
        //If no originalFrame is supplied, the method will search all frames in page.
        if (originalFrame != null)
        {
            driver.switchTo().frame(originalFrame);
        }

        //Get all frames in current page or frame
        List<WebElement> frames=driver.findElements(By.tagName("frame"));

        //Loop on all frames, searching for the element.
        //If found, remain in that frame, otherwise go back to parent frame
        for(int i=0; i< frames.size(); i++)
        {
            driver.switchTo().frame(i);
            elementFound= driver.findElements(By.xpath(xpathString)).size() > 0;
            if(elementFound) {
                logger.info("focus on  " + frames.get(i).toString() + " " + elementDescription);
                break;
            }
            driver.switchTo().parentFrame();
        }

        //Throw exception if element was not found in any frame
        if(!elementFound) {
            logger.info("Can not find in any frame element: " + elementDescription + ": " + xpathString);
            throw new Exception("Element was not found in any frame");
        }

    }

    /**
     * Search element in every frame of the page and move to frame of it if found
     * @param driver Instance of WebDriver
     * @param insideFrame true if driver is already at a specific frame to check,
     *                    otherwise false and then check will start from defaultContent
     * @param elementDescription Description of element
     * @param locator Instance of By to locate web element
     * @return true if element found, false otherwise
     * @author plot.ofek
     * @since 03-JAN-2022
     */
    public static boolean isElementFoundInAnyFrame(WebDriver driver, boolean insideFrame, String elementDescription, By locator) {
        boolean elementFound;

        if (!insideFrame)
        {
            driver.switchTo().defaultContent();
        }
        //Get all frames in current page or frame
        List<WebElement> frames=driver.findElements(By.tagName("frame"));
        frames.addAll(driver.findElements(By.tagName("iframe")));

        //Loop on all frames, searching for the element.
        //If found, remain in that frame, otherwise go back to parent frame
        for(int i=0; i< frames.size(); i++)
        {
            driver.switchTo().frame(i);
            elementFound= driver.findElements(locator).size() > 0;
            if(elementFound) {
                logger.info("focus on  " + frames.get(i).toString() + " " + elementDescription);
                return true;
            }
            //Recursive call to drill into frames within this frame
            if(isElementFoundInAnyFrame(driver, true, elementDescription, locator))
            {
                return true;
            }

            //If element was not found in this frame,
            // go back to parent frame and check the rest of the frames underneath it.
            driver.switchTo().parentFrame();
        }

        return false;
    }
}
