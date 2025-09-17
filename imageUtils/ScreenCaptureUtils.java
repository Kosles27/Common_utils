package imageUtils;

import Managers.WebDriverInstanceManager;
import dateTimeUtils.DateUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class ScreenCaptureUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScreenCaptureUtils.class);

    /**
     * This method capture the screen, not the browser, meaning it take a screenshot of the focused window
     * The capture image is in .png format
     * @param filePath the desired file path
     * @param fileName the desired file name
     * @return the captured screenshot absolute file path as String
     */
    public static String getCapturedScreenImageFilePath(String filePath, String fileName) {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture;
        String completeFileNameAndPath = filePath + fileName + ".png";
        try {
            capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File(completeFileNameAndPath));
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
        return completeFileNameAndPath;
    }

    /**
     * take screenshot for reporting
     * @param driver WebDriver instance
     * @return SSpath string new location of file
     */
    public static String takeScreenShot(WebDriver driver)
    {
        String SSpath = null;

        try
        {
            SSpath= "images" + File.separator +  DateUtils.getUniqueTimestamp() +".png" ;
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File("report" + File.separator + SSpath));
            logger.info(SSpath);
        }

        //if invalid session id exception exists - take driver from map
        catch (NoSuchSessionException exc) {
            try {
                SSpath= "images" + File.separator +  DateUtils.getUniqueTimestamp() +".png" ;
                File scrFile = ((TakesScreenshot) WebDriverInstanceManager.getDriverFromMap()).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(scrFile, new File("report" + File.separator + SSpath));
                logger.info(SSpath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        catch(Exception e)
        {
            logger.error(e.toString());
        }

        return SSpath;
    }

    /**
     * take screenshot of the given element for reporting
     * @param driver WebDriver instance
     * @param element the element
     * @return screenShotPath string new location of file
     * @author genosar.dafna
     * @since 10.11.2024
     */
    public static String takeElementScreenShot(WebDriver driver, WebElement element, @Nullable String elementName)
    {
        String screenShotPath;

        try
        {
            screenShotPath = "images" + File.separator +  DateUtils.getUniqueTimestamp() +".png" ;

            //Capture the element screenshot
            File screenshot = element.getScreenshotAs(OutputType.FILE);

            FileUtils.copyFile(screenshot, new File("report" + File.separator + screenShotPath));
            logger.info(screenShotPath);
        }

        //if invalid session id exception exists - take driver from map
        catch (Exception exc) {
            if(elementName == null)
                throw new Error(String.format("Error taking the element's screenshot<br>Error: %s", exc.getMessage()));
            else
                throw new Error(String.format("Error taking a screenshot or element '%s'<br>Error: %s", elementName, exc.getMessage()));
        }

        return screenShotPath;
    }
}
