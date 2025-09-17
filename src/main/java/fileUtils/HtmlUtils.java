package fileUtils;

import Managers.WebDriverInstanceManager;
import dateTimeUtils.DateUtils;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static propertyUtils.PropertyUtils.getGlobalProperty;

public class HtmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    /**
     * Create the page source html file from the driver.
     * @param driver WebDriver instance
     * @since 09.05.2021
     * @author sela.zvika
     * @return HTMLpath string new location of file
     */
    public static String savePageSource(WebDriver driver)
    {
        //get the page source relative location from global props
        String htmlPath = getGlobalProperty("page_source_path");
        String currentBase = System.getProperty("user.dir");

        try {
            //if dir doesn't exist create it
            File theDir = new File(currentBase + htmlPath);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
        }
        catch(Exception e)
        {
            logger.error("failed to create directory /target/pagesource " + e);
        }

        htmlPath = htmlPath + File.separator + DateUtils.getUniqueTimestamp() + ".html";

        File htmlFile;
        FileWriter writer;
        try {

            //create the file
            htmlFile = new File(currentBase+htmlPath);
            htmlFile.createNewFile();

            //Write Content
            writer = new FileWriter(htmlFile);
            writer.write(driver.getPageSource());
            writer.close();
        }

        //if invalid session id exception exists - take driver from map
        catch (NoSuchSessionException exc)
        {

            //create the file
            htmlFile = new File(currentBase+htmlPath);
            try {
                htmlFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Write Content
            try {
                writer = new FileWriter(htmlFile);
                writer.write(WebDriverInstanceManager.getDriverFromMap().getPageSource());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        catch(Exception e)
        {
            logger.error("failed to create page source file" + e);
        }

        //return the relative path of file
        return htmlPath;
    }
}
