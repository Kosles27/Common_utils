package AssertionsWithReport;

import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import enumerations.BugSeverityEnum;
import enumerations.MessageLevel;
import org.junit.jupiter.api.Assertions;
import reportUtils.ExtentReportUtils;
import reportUtils.ReportStyle;

import javax.annotation.Nullable;

import static reportUtils.Report.reportAndLog;
import static reportUtils.Report.reportBug;

/**
 * This class contain various type of assert and send a message to report accordingly
 */
public class AssertsWithReport {
    
    private static final String failMessage = "Test failed on assert";

    public static void assertFail(String errorMessage)
    {
        reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
        Assertions.fail(ReportStyle.getFailureMessage(failMessage));
    }

    public static void assertFail(String bugText, BugSeverityEnum severityEnum, DateTime creationDate)
    {
        reportBug(bugText, severityEnum, creationDate);
        Assertions.fail();
    }

    /**
     * Commit assert true and write to the report success/error accordingly
     * @param condition - true of false
     * @param successMessage - Message to log In case of success
     * @param errorMessage - Message to log in case of failure
     * @author dafna genosar
     * @since 28.11.2022
     * @since 14.04.2025
     */
    public static void assertTrue(boolean condition, @Nullable String successMessage, @Nullable String errorMessage)
    {
        if (!condition) {
            if(errorMessage != null)
                reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
        else
        {
            if(successMessage != null && !successMessage.contains(">null<"))
                ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
            Assertions.assertTrue(true);
        }
    }

    /**
     * Commit assert false and write to the report success/error accordingly
     * @param condition - true of false
     * @param successMessage - Message to log In case of success
     * @param errorMessage - Message to log in case of failure
     * @author dafna genosar
     * @since 28.11.2022
     * @since 14.04.2025
     */
    public static void assertFalse(boolean condition, @Nullable String successMessage, @Nullable String errorMessage)
    {
        if (condition) {
            if(errorMessage != null)
                reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
        else
        {
            if(successMessage != null && !successMessage.contains(">null<"))
                ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
            Assertions.assertTrue(true);
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report success/error accordingly
     * @param expected Object
     * @param actual Object
     * @param successMessage String
     * @param errorMessage String
     * @author genosar.dafna
     * @since 04.12.2024
     */
    public static void assertEquals(Object expected, Object actual, @Nullable String successMessage, @Nullable String errorMessage)
    {
        try {
            Assertions.assertEquals(expected, actual, errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            if(errorMessage != null)
                reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
    }

    /**
     * Commit assert not equals between 2 objects and write to the report success/error accordingly
     * @param unexpected Object
     * @param actual Object
     * @param successMessage String
     * @param errorMessage String
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 04.12.2024
     */
    public static void assertNotEquals(Object unexpected, Object actual, @Nullable String successMessage, @Nullable String errorMessage)
    {
        try {
            Assertions.assertNotEquals(unexpected, actual, errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            if(errorMessage != null)
                reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
    }

    /**
     * Commit assert object is Null and write to the report success/error accordingly
     * @param actual Object
     * @param successMessage String
     * @param errorMessage String
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 04.12.2024
     */
    public static void assertNull(Object actual, @Nullable String successMessage, String errorMessage)
    {
        try {
            Assertions.assertNull(actual,errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            if(errorMessage != null)
                reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
    }

    /**
     * Commit assert object is Not Null and write to the report success/error accordingly
     * @param actual Object
     * @param successMessage String
     * @param errorMessage String
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 04.12.2024
     */
    public static void assertNotNull(Object actual, @Nullable String successMessage, String errorMessage)
    {
        try {
            Assertions.assertNotNull(actual,errorMessage);
            if(successMessage != null && !successMessage.contains(">null<")) ExtentReportUtils.extentLogger(LogStatus.PASS,successMessage);
        }
        catch (Throwable e){
            if(errorMessage != null)
                reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report only when fails
     * @param expected Object
     * @param Actual Object
     * @param errorMessage String
     */
    public static void assertEqualsWriteOnlyFailuresToReport(Object expected, Object Actual, String errorMessage)
    {
        try {
            Assertions.assertEquals(expected, Actual,errorMessage);
        }
        catch (Throwable e){
            reportAndLog(ReportStyle.getFailureMessage(errorMessage), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
    }

    /**
     * Commit assert true and write to the report success/error accordingly
     * @param condition - condition to be asserted
     * @param message - Message to log example:message should be display
     * @author Yael Rozenfeld
     * @since 17.11.2021
     * @author dafna genosar
     * @since 04.12.2024
     */
    public static void assertTrue(boolean condition, String message)
    {
        if (!condition) {
            reportAndLog(ReportStyle.getFailureMessage(message), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
        else
        {
            ExtentReportUtils.extentLogger(LogStatus.PASS,message);
            Assertions.assertTrue(true);
        }
    }


    /**
     * Commit assert false and write to the report success/error accordingly
     * @param condition - true of false
     * @param message - Message to log. example:message shouldn't be display
     * @author Yael Rozenfeld
     * @since 17.11.2021
     * @author dafna genosar
     * @since 04.12.2024
     */
    public static void assertFalse(boolean condition,String message)
    {
        if (condition) {
            reportAndLog(ReportStyle.getFailureMessage(message), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
        else
        {
            Assertions.assertTrue(true);
        }
    }

    /**
     * Commit assert equals between 2 objects and write to the report success/error accordingly
     * @param expected Object
     * @param Actual Object
     * @param message String message will be written in the report<br/>,
     *               example: for field xxx expected value is '' and current is ''
     * @author Yael Rozenfeld
     * @since 17.11.2021
     * @author genosar.dafna
     * @since 04.12.2024
     */
    public static void assertEquals(Object expected, Object Actual, String message)
    {
        try {
            Assertions.assertEquals(expected, Actual,message);
            ExtentReportUtils.extentLogger(LogStatus.PASS,message);
        }
        catch (Throwable e){
            reportAndLog(ReportStyle.getFailureMessage(message), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
    }

    /**
     * Commit assert not equals between 2 objects and write to the report success/error accordingly
     * @param unexpected Object
     * @param actual Object
     * @param message String message will be written in the report<br/>,
     * example: for field xxx expected value is '' and current is ''
     * @author genosar.dafna
     * @since 21.11.2022
     * @since 04.12.2024
     */
    public static void assertNotEquals(Object unexpected, Object actual, String message)
    {
        try {
            Assertions.assertNotEquals(unexpected, actual, message);
            ExtentReportUtils.extentLogger(LogStatus.PASS,message);
        }
        catch (Throwable e){
            reportAndLog(ReportStyle.getFailureMessage(message), MessageLevel.FAIL);
            Assertions.fail(ReportStyle.getFailureMessage(failMessage));
        }
    }
}
