package extensions;

import Managers.WebDriverInstanceManager;
import drivers.WebDriverFactory;
import enumerations.MessageLevel;
import jdk.jfr.Category;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import reportUtils.Report;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * this Junit listener starts the driver beforeEach and quits the driver afterEach
 *
 * @author - Lior Umflat
 * @since - 24.5.2021
 */
@SuppressWarnings("unused")
public class JunitWebDriverExtension implements AfterEachCallback, BeforeEachCallback {

    /**
     * create the driver BeforeEach
     *
     * @author - Lior Umflat
     * @since - 24.5.2021
     * @author dafna.genosar
     * @since 15.11.2022
     * @since 29.06.2022
     */
    @Override
    public void beforeEach(ExtensionContext extensionContext) {

        boolean initDriver = true;

        // Retrieve the test name from the test method's display name
        String testName = extensionContext.getDisplayName();

        // Set the test name as a system property
        System.setProperty("testName", testName);

        // Log the test name
        Report.reportAndLog("Test name set to: " + testName, MessageLevel.INFO);

        Category category = extensionContext.getRequiredTestClass().getAnnotation(Category.class);
        if (category != null) {
            List<String> categories = Arrays.stream(category.value()).map(s -> s.toLowerCase()).toList();
            if(categories.contains("non ui")) {
                initDriver = false;
                WebDriverInstanceManager.deleteDriverFromMap();
            }
        }
        Tags testTags = extensionContext.getRequiredTestMethod().getAnnotation(Tags.class);
        if (testTags != null) {
            List<String> tags = Arrays.stream(testTags.value()).map(x -> x.value()).toList();
            for (String tag : tags){
                if (tag.contains("non_ui")) {
                    initDriver = false;
                    WebDriverInstanceManager.deleteDriverFromMap();
                }
            }
        }

        if(initDriver) {
            WebDriverFactory driverFactory = new WebDriverFactory();
            driverFactory.initDriver();
        }
    }

    /**
     * quit the driver AfterEach
     *
     * @author - Lior Umflat
     * @author dafna.genosar
     * @since - 24.5.2021
     * @since 15.11.2022
     */
    @Override
    public void afterEach(ExtensionContext extensionContext) {
        boolean quitDriver = true;
        WebDriver driver = WebDriverInstanceManager.getDriverFromMap();
        try {
            Category category = extensionContext.getRequiredTestClass().getAnnotation(Category.class);
            if (category != null) {
                List<String> categories = Arrays.stream(category.value()).map(s -> s.toLowerCase()).collect(Collectors.toList());
                if (categories.contains("non ui"))
                    quitDriver = false;
            }
            Tags testTags = extensionContext.getRequiredTestMethod().getAnnotation(Tags.class);
            if (testTags != null) {
                List<String> tags = Arrays.stream(testTags.value()).map(x -> x.value()).toList();
                for (String tag : tags) {
                    if (tag.contains("non_ui")) {
                        quitDriver = false;
                    }
                }
            }
        }finally {
            driver = WebDriverInstanceManager.getDriverFromMap();
            if (driver != null)
                driver.close();
            driver = WebDriverInstanceManager.getDriverFromMap();
            if (driver != null)
                driver.quit();
        }
    }
}
