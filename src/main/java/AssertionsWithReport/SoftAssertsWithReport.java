package AssertionsWithReport;

import Managers.ReportInstanceManager;
import Managers.WebDriverInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import enumerations.BugSeverityEnum;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.ExtentReportUtils;
import reportUtils.ReportStyle;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static imageUtils.ScreenCaptureUtils.takeScreenShot;

/**
 * SoftAssertsWithReport - assertion will not stop the test.<br/>
 * in the end of using SoftAssertsWithReport user assertAll to fail the test
 * example <code>
 * SoftAssertsWithReport softAssert = new SoftAssertsWithReport();
 * softAssert.assertFalse(false,"test success assert false" ,"test Fail assert false");
 * softAssert.assertTrue(false,"test success assert true" ,"test Fail assert true");
 * softAssert.assertEquals("a","b","test success assert equals","test fail assert equals");
 * softAssert.assertEquals("a","a","test success assert equals","test fail assert equals");
 * softAssert.assertAll();
 * </code>
 *
 * @author Rozenfeld.Yael
 * @since 04/2021
 */
@SuppressWarnings("unused")
public class SoftAssertsWithReport {

    private static final Logger logger = LoggerFactory.getLogger(SoftAssertsWithReport.class);
    private final List<Throwable> assertions;

    /**
     * Constructor. Initializes global variable assertions as empty list of Throwables
     */
    public SoftAssertsWithReport() {
        assertions = new ArrayList<>();
    }

    /**
     * assertFail - Fail the report.
     * @param errorMessage Error Message to the report
     * @author genosar.dafna
     * @since 22.01.2024
     */
    public void assertFail(String errorMessage) {
        try {
            AssertsWithReport.assertFail(errorMessage);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertFail - Fail the report.
     * @param errorMessage Error Message to the report
     * @author genosar.dafna
     * @since 22.01.2024
     * @since 19.11.2024
     */
    public void assertFail(String errorMessage, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertFail(errorMessage);
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertFail - Fail the report.
     * @param bugText Bug Message to the report
     * @param severityEnum bug priority
     * @param creationDate bug creation date
     * @author genosar.dafna
     * @since 22.01.2024
     */
    public void assertFail(String bugText, BugSeverityEnum severityEnum, DateTime creationDate) {
        try {
            AssertsWithReport.assertFail(bugText, severityEnum, creationDate);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertTrue - test will continue running anyway.
     *
     * @param condition boolean condition
     * @param successMessage Message to be written if condition is true
     * @param errorMessage Message to be written if condition is false
     * @author Rozenfeld.Yael
     * @since 04/2021
     * @author genosar.dafna
     * @since 04.14.2025
     */
    public void assertTrue(boolean condition, @Nullable String successMessage, @Nullable String errorMessage) {
        try {
            errorMessage = (errorMessage == null)? errorMessage : ReportStyle.getFailureMessage(errorMessage);
            successMessage = (successMessage == null)? successMessage : ReportStyle.getSuccessMessage(successMessage);

            AssertsWithReport.assertTrue(condition, successMessage, errorMessage);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertTrue - test will continue running anyway.
     * @param condition boolean condition
     * @param successMessage Message to be written if condition is true
     * @param errorMessage Message to be written if condition is false
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public void assertTrue(boolean condition, String successMessage, String errorMessage, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertTrue(condition, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertFalse - test will  continue running anyway
     *
     * @param condition boolean condition
     * @param successMessage Message to be written if condition is false
     * @param errorMessage Message to be written if condition is true
     * @author Rozenfeld.Yael
     * @since 04/2021
     */
    public void assertFalse(boolean condition, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertFalse(condition, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertFalse - test will  continue running anyway
     * @param condition boolean condition
     * @param successMessage Message to be written if condition is false
     * @param errorMessage Message to be written if condition is true
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public void assertFalse(boolean condition, String successMessage, String errorMessage, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertFalse(condition, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     * @param expected Expected result
     * @param Actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author Rozenfeld.Yael
     * @since 04/2021
     */
    public void assertEquals(Object expected, Object Actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertEquals(expected, Actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     * @param expected Expected result
     * @param Actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public void assertEquals(Object expected, Object Actual, String successMessage, String errorMessage, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertEquals(expected, Actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertNotEquals - test will  continue running anyway
     * @param unexpected unexpected result
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNotEquals(Object unexpected, Object actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertNotEquals(unexpected, actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertNotEquals - test will  continue running anyway
     * @param unexpected unexpected result
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 19.11.2024
     */
    public void assertNotEquals(Object unexpected, Object actual, String successMessage, String errorMessage, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertNotEquals(unexpected, actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertNull - test will  continue running anyway
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNull(Object actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertNull(actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertNull - test will  continue running anyway
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 19.11.2024
     */
    public void assertNull(Object actual, String successMessage, String errorMessage, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertNull(actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertNotNull - test will  continue running anyway
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNotNull(Object actual, String successMessage, String errorMessage) {
        try {
            AssertsWithReport.assertNotNull(actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertNotNull - test will  continue running anyway
     * @param actual Actual result
     * @param successMessage Message to be written if actual equals expected
     * @param errorMessage Message to be written if actual doesn't equal expected
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 19.11.2024
     */
    public void assertNotNull(Object actual, String successMessage, String errorMessage, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertNotNull(actual, ReportStyle.getSuccessMessage(successMessage), ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertAll - in the end of using SoftAssertsWithReport <br/>
     * call this function to fail the test in case soft assert failed
     * @author Rozenfeld.Yael
     * @since 04/2021
     * @author genosar.dafna
     * @since 04.12.2024
     */
    public void assertAll() {

        boolean condition = this.assertions.isEmpty();

        if (!condition) {
            Assertions.fail(ReportStyle.getFailureMessage(ReportStyle.getFailureMessage("Test failed on soft assert")));
        }
        else
        {
            ExtentReportUtils.extentLogger(LogStatus.PASS,ReportStyle.getSuccessMessage("All soft asserts passed"));
            Assertions.assertTrue(true);
        }
    }

    /**
     * onSoftAssertFail  - catch the  throwable on soft assert<br/>
     * and write it to report and attach Screenshot
     * @param throwable - the throwable was caught
     * @author Rozenfeld.Yael
     * @since 04/2021
     * @author genosar.dafna
     * @since 19.11.2024     *
     */
    private void onSoftAssertFail(Throwable throwable) {
        onSoftAssertFail(throwable, true);
    }

    /**
     * onSoftAssertFail  - catch the throwable on soft assert and write it to report
     * @param throwable - the throwable was caught
     * @param takeScreenshot true if to attach Screenshot / false otherwise
     * @author genosar.dafna
     * @since 19.11.2024
     * @since 15.04.2025
     */
    private void onSoftAssertFail(Throwable throwable, boolean takeScreenshot) {
        assertions.add(throwable);
        logger.debug(Arrays.toString(throwable.getStackTrace()));

        WebDriver driver = WebDriverInstanceManager.getDriverFromMap();

        //Don't take screenshots if there is no web driver
        if (driver != null && takeScreenshot)
        {
            String path = takeScreenShot(driver);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL, ReportInstanceManager.getCurrentTestReport().addScreenCapture(path));
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report only when fails
     *
     * @param expected     Object
     * @param actual       Object
     * @param errorMessage String
     */
    public void assertEqualsWriteOnlyFailuresToReport(Object expected, Object actual, String errorMessage) {
        try {
            AssertsWithReport.assertEqualsWriteOnlyFailuresToReport(expected, actual, ReportStyle.getFailureMessage(errorMessage));
        } catch (Throwable e) {
            onSoftAssertFail(e);
        }
    }

    /**
     * assertTrue - test will continue running anyway.
     * @param condition boolean condition
     * @param message Message to log. example:message should be display
     * @author Rozenfeld.Yael
     * @since 11/2021
     */
    public void assertTrue(boolean condition, String message) {
        try {
            AssertsWithReport.assertTrue(condition, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertTrue - test will continue running anyway.
     * @param condition boolean condition
     * @param message Message to log. example:message should be display
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public void assertTrue(boolean condition, String message, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertTrue(condition, message);
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertFalse - test will  continue running anyway
     * @param condition boolean condition
     * @param message Message to log. example:message shouldn't be display
     * @author Rozenfeld.Yael
     * @since 11/2021
     */
    public void assertFalse(boolean condition, String message) {
        try {
            AssertsWithReport.assertFalse(condition, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertFalse - test will  continue running anyway
     * @param condition boolean condition
     * @param message Message to log. example:message shouldn't be display
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public void assertFalse(boolean condition, String message, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertFalse(condition, message);
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     *
     * @param expected Expected result
     * @param Actual Actual result
     * @param message Message to be written in the report<br/>,
     *      *         example: for field xxx expected value is '' and current is ''
     * @author Rozenfeld.Yael
     * @since 11/2021
     */
    public void assertEquals(Object expected, Object Actual, String message) {
        try {
            AssertsWithReport.assertEquals(expected, Actual, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     *     * @param expected Expected result
     * @param Actual Actual result
     * @param message Message to be written in the report<br/>,
     *                example: for field xxx expected value is '' and current is ''
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public void assertEquals(Object expected, Object Actual, String message, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertEquals(expected, Actual, message);
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     * @param unexpected unexpected result
     * @param actual Actual result
     * @param message Message to be written in the report<br/>,
     *      *         example: for field xxx expected value is '' and current is ''
     * @author genosar.dafna
     * @since 21.11.2022
     */
    public void assertNotEquals(Object unexpected, Object actual, String message) {
        try {
            AssertsWithReport.assertNotEquals(unexpected, actual, message);
        } catch (Throwable t) {
            onSoftAssertFail(t);
        }
    }

    /**
     * assertEquals - test will  continue running anyway
     * @param unexpected unexpected result
     * @param actual Actual result
     * @param message Message to be written in the report<br/>,
     *                example: for field xxx expected value is '' and current is ''
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 19.11.2024
     */
    public void assertNotEquals(Object unexpected, Object actual, String message, boolean takeScreenshot) {
        try {
            AssertsWithReport.assertNotEquals(unexpected, actual, message);
        } catch (Throwable t) {
            onSoftAssertFail(t, takeScreenshot);
        }
    }
}
