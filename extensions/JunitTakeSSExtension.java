package extensions;

import Managers.ReportInstanceManager;
import Managers.WebDriverInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import jdk.jfr.Category;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static imageUtils.ScreenCaptureUtils.takeScreenShot;

/**
 * Listener to Junit that takes the screenshot upon failure on test execution
 * @author sela.zvika
 * @since 09.05.2021
 */
public class JunitTakeSSExtension implements
        AfterTestExecutionCallback {


    /**
     * This method runs after test is finished.
     * It saves the page screen shot and adds it to the report in cases of exceptions in the test execution
     * @param context Instance of ExtensionContext
     * @author dafna.genosar
     * @since 15.11.2022
     * @since 04.12.2024
     */
    @Override
    public void afterTestExecution(ExtensionContext context) {

        //in case of exception we save the screenshot
        if (context.getExecutionException().isPresent()) {

            //Do not take a screenshot if it's a soft assert as it already took a screenshot when error was reported
            boolean takeScreenShot = !context.getExecutionException().toString().contains("Test failed on soft assert");

            Category category = context.getRequiredTestClass().getAnnotation(Category.class);
            if (category != null) {
                List<String> categories = Arrays.stream(category.value()).map(s -> s.toLowerCase()).collect(Collectors.toList()) ;
                if(categories.contains("non ui"))
                    takeScreenShot = false;
            }
            Tags testTags = context.getRequiredTestMethod().getAnnotation(Tags.class);
            if (testTags != null) {
                List<String> tags = Arrays.stream(testTags.value()).map(x -> x.value()).toList();
                for (String tag : tags){
                    if (tag.contains("non_ui")) {
                        takeScreenShot = false;
                    }
                }
            }
            if(takeScreenShot) {
                String path = takeScreenShot(WebDriverInstanceManager.getDriverFromMap());
                ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL, ReportInstanceManager.getCurrentTestReport().addScreenCapture(path));
            }
        }
    }
}
