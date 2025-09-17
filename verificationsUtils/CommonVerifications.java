package verificationsUtils;

import AssertionsWithReport.AssertsWithReport;
import AssertionsWithReport.SoftAssertsWithReport;
import collectionUtils.ListUtils;
import collectionUtils.MapUtils;
import com.google.common.base.Stopwatch;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import dateTimeUtils.Time;
import enumerations.BugSeverityEnum;
import enumerations.MessageLevel;
import objectsUtils.ObjectsUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.ExtentReportUtils;
import reportUtils.Report;
import reportUtils.ReportStyle;
import seleniumUtils.ElementWrapper;
import seleniumUtils.customeElements.CheckBox;
import seleniumUtils.customeElements.Table;
import seleniumUtils.customeElements.TableRow;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static collectionUtils.MapUtils.getMapRecord;
import static reportUtils.ExtentReportUtils.extentLogger;
import static reportUtils.Report.reportAndLog;
import static reportUtils.ReportStyle.getFailureMessage;
import static reportUtils.ReportStyle.getSuccessMessage;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class CommonVerifications {

    private static final Logger logger = LoggerFactory.getLogger(CommonVerifications.class);

    /**
     * Verify the status code received from the browser's matches the received status code
     * @param networkData hashmap of network data received from the browser (implemented in class devToolsUtils.NetworkUtils -> method getNetworkData())
     * @param expectedStatusCode the expected status code
     * @param softAssertsWithReport a SoftAssertsWithReport
     * @author genosar.dafna
     * @since 15.04.2024
     */
    public static void verifyNetworkApiStatusCodeMatches(Map<String, Object> networkData, int expectedStatusCode, SoftAssertsWithReport softAssertsWithReport)
    {
        int receivedStatusCode = ((AtomicReference<Integer>)networkData.get("status code")).get();

        verifyValuesMatch(receivedStatusCode, expectedStatusCode, "Received API status code", "Expected API status code", softAssertsWithReport);

        if(receivedStatusCode != expectedStatusCode){
            String errorMessage = ReportStyle.getFailureMessage(String.format("<b>Status code:</b> %d<br><b>Error:</b> %s<br><b>URL:</b> %s<br><b>Request ID:</b> %s", receivedStatusCode, networkData.get("message").toString(), networkData.get("url").toString(), networkData.get("request id").toString()));

            Report.reportAndLog(errorMessage, MessageLevel.INFO);
        }
    }

    /**
     * Verify the status code received from the browser's does not match the received status code
     * @param networkData hashmap of network data received from the browser (implemented in class devToolsUtils.NetworkUtils -> method getNetworkData())
     * @param expectedNotToMatchStatusCode the status code we do not want - for example, if we expect the browser API call to fail, then the 'expectedNotToMatchStatusCode' should be 200
     * @param softAssertsWithReport a SoftAssertsWithReport
     * @author genosar.dafna
     * @since 15.04.2024
     */
    public static void verifyNetworkApiStatusCodeDoesNotMatch(Map<String, Object> networkData, int expectedNotToMatchStatusCode, SoftAssertsWithReport softAssertsWithReport)
    {
        int receivedStatusCode = ((AtomicReference<Integer>)networkData.get("status code")).get();

        verifyValuesDoNotMatch(receivedStatusCode, expectedNotToMatchStatusCode, "Received API status code", String.valueOf(expectedNotToMatchStatusCode), softAssertsWithReport);

        if(receivedStatusCode == expectedNotToMatchStatusCode){
            String errorMessage = ReportStyle.getFailureMessage(String.format("<b>Status code:</b> %d<br><b>Error:</b> %s<br><b>URL:</b> %s<br><b>Request ID:</b> %s", receivedStatusCode, networkData.get("message").toString(), networkData.get("url").toString(), networkData.get("request id").toString()));

            Report.reportAndLog(errorMessage, MessageLevel.INFO);
        }
    }

    /**
     * Verify a WebElement exists and Displays
     * @param element the WebElement
     * @param elementName the element name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 27.06.2024
     * @since 12.08.2024
     */
    public static boolean verifyElementExists(WebElement element, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage(String.format("%s displays, as expected", elementName));
        String errorMessage = getFailureMessage(String.format("%s does not display, even though it should", elementName));

        return verifyElementExists(element, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify a WebElement exists and Displays
     * @param element the WebElement
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 12.08.2024
     */
    public static boolean verifyElementExists(WebElement element, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean existsAndDisplays = ElementWrapper.elementExistsAndDisplayed(element);

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(existsAndDisplays, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(existsAndDisplays, successMessage, errorMessage);

        return existsAndDisplays;
    }

    /**
     * Verify a WebElement exists and Displays
     * @param driver driver
     * @param by by statement to search for the WebElement
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return the element or null
     * @author Genosar.dafna
     * @since 22.04.2025
     */
    public static WebElement verifyElementExists(WebDriver driver, By by, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean existsAndDisplays;
        WebElement element = null;
        try{
            element = driver.findElement(by);
            existsAndDisplays = ElementWrapper.elementExistsAndDisplayed(element);
        }
        catch (Exception e){
            existsAndDisplays = false;
        }

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(existsAndDisplays, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(existsAndDisplays, successMessage, errorMessage);

        return element;
    }

    /**
     * Verify a WebElement exists and Displays
     * @param parentElement parent element
     * @param by by statement to search for the WebElement
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return the element or null
     * @author Genosar.dafna
     * @since 22.04.2025
     */
    public static WebElement verifyElementExists(WebElement parentElement, By by, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean existsAndDisplays;
        WebElement element = null;
        try{
            element = parentElement.findElement(by);
            existsAndDisplays = ElementWrapper.elementExistsAndDisplayed(element);
        }
        catch (Exception e){
            existsAndDisplays = false;
        }

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(existsAndDisplays, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(existsAndDisplays, successMessage, errorMessage);

        return element;
    }

    /**
     * Verify a WebElement does not exist and Display
     * @param element the WebElement
     * @param elementName the element name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 27.06.2024
     * @since 12.08.2024
     */
    public static boolean verifyElementDoesNotExist(WebElement element, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage(String.format("%s does not display, as expected", elementName));
        String errorMessage = getFailureMessage(String.format("%s displays, even though it should not", elementName));

        return verifyElementDoesNotExist(element, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify a WebElement does not exist and Display
     * @param element the WebElement
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 12.08.2024
     */
    public static boolean verifyElementDoesNotExist(WebElement element, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean existsAndDisplays = ElementWrapper.elementExistsAndDisplayed(element);

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(existsAndDisplays, successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(existsAndDisplays, successMessage, errorMessage);

        return !existsAndDisplays;
    }

    /**
     * *********** PLEASE USE verifyEquals() INSTEAD *********************
     * Verify values match
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if matches / false otherwise
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 03.02.2025 - deprecated
     */
    @Deprecated //Please use verifyEquals() instead
    public static <T extends Comparable<T>> boolean verifyValuesMatch(T obj1, T obj2, String obj1_name, String obj2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        return verifyEquals(obj1, obj2, obj1_name, obj2_name, softAssertsWithReport);
    }

    /**
     * *********** PLEASE USE verifyEquals() INSTEAD *********************
     * Verify values match
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param takeScreenshot true if to attach Screenshot / false otherwise
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if matches / false otherwise
     * @author genosar.dafna
     * @since 19.11.2024
     * @since 03.02.2025 - deprecated
     */
    @Deprecated //Please use verifyEquals() instead
    public static <T extends Comparable<T>> boolean verifyValuesMatch(T obj1, T obj2, String obj1_name, String obj2_name, boolean takeScreenshot, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        return verifyEquals(obj1, obj2, obj1_name, obj2_name, takeScreenshot, softAssertsWithReport);
    }

    /**
     * Verify values are equal
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if matches / false otherwise
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 03.02.2025
     */
    public static <T extends Comparable<T>> boolean verifyEquals(T obj1, T obj2, String obj1_name, String obj2_name, @Nullable SoftAssertsWithReport softAssertsWithReport){
        return verifyEquals(obj1, obj2, obj1_name, obj2_name, true, softAssertsWithReport);
    }

    /**
     * Verify values are equal
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param takeScreenshot true if to attach Screenshot / false otherwise
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if matches / false otherwise
     * @author genosar.dafna
     * @since 19.11.2024
     * @since 17.02.2025
     */
    public static <T extends Comparable<T>> boolean verifyEquals(T obj1, T obj2, String obj1_name, String obj2_name, boolean takeScreenshot, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean match = ObjectsUtils.areEqual(obj1, obj2);

        String obj1String;
        String obj2String;

        if(obj1 == null || obj2 == null){
            obj1String = (obj1 == null)? "null" : (obj1 instanceof DateTime)? ((DateTime) obj1).toLongTimeString() : obj1.toString();
            obj2String = obj2 == null? "null" : (obj2 instanceof DateTime)? ((DateTime) obj2).toLongTimeString() : obj2.toString();
        }
        else if(obj1 instanceof DateTime) {
            obj1String = ((DateTime) obj1).toLongTimeString();
            obj2String = ((DateTime) obj2).toLongTimeString();
        }
        else {
            obj1String = obj1.toString();
            obj2String = obj2.toString();
        }

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s matches %s - %s", obj1_name, obj2_name, obj1String));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s does not match %s<br>%s: %s<br>%s: %s", obj1_name, obj2_name, obj1_name, obj1String, obj2_name, obj2String));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage, takeScreenshot);

        return match;
    }
//    public static <T extends Comparable<T>> boolean verifyEquals(T obj1, T obj2, String obj1_name, String obj2_name, boolean takeScreenshot, @Nullable SoftAssertsWithReport softAssertsWithReport)
//    {
//        boolean match;
//
//        String obj1String;
//        String obj2String;
//
//        if(obj1 == null || obj2 == null){
//            match = Objects.equals(obj1, obj2);
//            obj1String = (obj1 == null)? "null" : (obj1 instanceof DateTime)? ((DateTime) obj1).toLongTimeString() : obj1.toString();
//            obj2String = obj2 == null? "null" : (obj2 instanceof DateTime)? ((DateTime) obj2).toLongTimeString() : obj2.toString();
//        }
//        else if(obj1 instanceof DateTime) {
//            obj1String = ((DateTime) obj1).toLongTimeString();
//            obj2String = ((DateTime) obj2).toLongTimeString();
//
//            match = ((DateTime)obj1).compareTo((DateTime)obj2) == 0;
//        }
//        else {
//            obj1String = obj1.toString();
//            obj2String = obj2.toString();
//
//            match = obj1.compareTo(obj2) == 0;
//        }
//
//        String successMessage = ReportStyle.getSuccessMessage(String.format("%s matches %s - %s", obj1_name, obj2_name, obj1String));
//        String errorMessage = ReportStyle.getFailureMessage(String.format("%s does not match %s<br>%s: %s<br>%s: %s", obj1_name, obj2_name, obj1_name, obj1String, obj2_name, obj2String));
//
//        if(softAssertsWithReport == null)
//            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
//        else
//            softAssertsWithReport.assertTrue(match, successMessage, errorMessage, takeScreenshot);
//
//        return match;
//    }

    /**
     * *********** PLEASE USE verifyNotEquals() INSTEAD *********************
     * Verify values do not match
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the values do not match / false otherwise
     * @author genosar.dafna
     * @since 11.05.2023
     * @since 03.02.2025 - Deprecated
     */
    @Deprecated //Please use verifyNotEquals() instead
    public static <T extends Comparable<T>> boolean verifyValuesDoNotMatch(T obj1, T obj2, String obj1_name, String obj2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        return verifyNotEquals(obj1, obj2, obj1_name, obj2_name, true, softAssertsWithReport);
    }

    /**
     * *********** PLEASE USE verifyNotEquals() INSTEAD *********************
     * Verify values do not match
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param takeScreenshot true if to attach Screenshot / false otherwise
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the values do not match / false otherwise
     * @author genosar.dafna
     * @since 19.11.2024
     * @since 03.02.2025 - Deprecated
     */
    @Deprecated //Please use verifyNotEquals() instead
    public static <T extends Comparable<T>> boolean verifyValuesDoNotMatch(T obj1, T obj2, String obj1_name, String obj2_name, boolean takeScreenshot, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        return verifyNotEquals(obj1, obj2, obj1_name, obj2_name, takeScreenshot, softAssertsWithReport);
    }

    /**
     * Verify values do not match
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the values do not match / false otherwise
     * @author genosar.dafna
     * @since 19.11.2024
     * @since 03.02.2025
     */
    public static <T extends Comparable<T>> boolean verifyNotEquals(T obj1, T obj2, String obj1_name, String obj2_name, @Nullable SoftAssertsWithReport softAssertsWithReport){
        return verifyNotEquals(obj1, obj2, obj1_name, obj2_name, true, softAssertsWithReport);
    }

    /**
     * Verify values are not equal
     * @param obj1 first obj to compare
     * @param obj2 second obj to compare
     * @param obj1_name obj1 name
     * @param obj2_name obj2 name
     * @param takeScreenshot true if to attach Screenshot / false otherwise
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the values do not match / false otherwise
     * @author genosar.dafna
     * @since 19.11.2024
     * @since 17.02.2025
     */
    public static <T extends Comparable<T>> boolean verifyNotEquals(T obj1, T obj2, String obj1_name, String obj2_name, boolean takeScreenshot, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean match = ObjectsUtils.areEqual(obj1, obj2);

        String obj1String;
        String obj2String;

        if(obj1 == null || obj2 == null){
            obj1String = (obj1 == null)? "null" : (obj1 instanceof DateTime)? ((DateTime) obj1).toLongTimeString() : obj1.toString();
            obj2String = obj2 == null? "null" : (obj2 instanceof DateTime)? ((DateTime) obj2).toLongTimeString() : obj2.toString();
        }
        else if(obj1 instanceof DateTime) {
            obj1String = ((DateTime) obj1).toLongTimeString();
            obj2String = ((DateTime) obj2).toLongTimeString();
        }
        else {
            obj1String = obj1.toString();
            obj2String = obj2.toString();
        }

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s does not match %s, as expected<br>%s: %s<br>%s: %s", obj1_name, obj2_name, obj1_name, obj1String, obj2_name, obj2String));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s matches %s, even though it should not<br>%s: %s<br>%s: %s", obj1_name, obj2_name, obj1_name, obj1String, obj2_name, obj2String));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(match, successMessage, errorMessage, takeScreenshot);

        return !match;
    }


    /**
     * Verify the given parameter is true
     * @param conditionName condition name/title to verify
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 11.07.2024
     * @since 12.08.2024
     */
    public static boolean verifyTrue(boolean condition, String conditionName, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage(String.format("%s, as expected", conditionName));
        String errorMessage = getFailureMessage(String.format("%s is false, even though it should be true", conditionName));

        return verifyTrue(condition, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the given parameter is true
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 12.08.2024
     */
    public static boolean verifyTrue(boolean condition, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(condition, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(condition, successMessage, errorMessage);

        return condition;
    }

    /**
     * Verify the given parameter is false
     * @param conditionName condition name/title to verify
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 27.06.2024
     * @since 12.08.2024
     */
    public static boolean verifyFalse(boolean condition, String conditionName, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage(String.format("%s is false, as expected", conditionName));
        String errorMessage = getFailureMessage(String.format("%s is true, even though it should be false", conditionName));

        return verifyFalse(condition, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the given parameter is false
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the verification passed / false otherwise
     * @author Genosar.dafna
     * @since 12.08.2024
     */
    public static boolean verifyFalse(boolean condition, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(condition, successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(condition, successMessage, errorMessage);

        return !condition;
    }

    /**
     * Verify the given parameter is null
     * @param itemToCheck item to verify is null
     * @param itemName the item name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the item is null / false otherwise
     * @author Genosar.dafna
     * @since 02.01.2024
     * @since 12.08.2024
     */
    public static <N> boolean verifyNull(N itemToCheck, String itemName, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage(String.format("%s is null, as expected", itemName));
        String errorMessage = getFailureMessage(String.format("%s is not null, even though it should be", itemName));

        return verifyNull(itemToCheck, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the given parameter is null
     * @param itemToCheck item to verify is null
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the item is null / false otherwise
     * @author Genosar.dafna
     * @since 12.08.2024
     */
    public static <N> boolean verifyNull(N itemToCheck, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        if(softAssertsWithReport == null)
            AssertsWithReport.assertNull(itemToCheck, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNull(itemToCheck, successMessage, errorMessage);

        return itemToCheck == null;
    }

    /**
     * Verify the given parameter is NOT null
     * @param itemToCheck item to verify is NOT null
     * @param itemName the item name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the item is NOT null / false otherwise
     * @author Genosar.dafna
     * @since 02.01.2024
     * @since 12.08.2024
     */
    public static <N> boolean verifyNotNull(N itemToCheck, String itemName, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage(String.format("%s is not null, as expected", itemName));
        String errorMessage = getFailureMessage(String.format("%s is null, even though it should not be", itemName));

        return verifyNotNull(itemToCheck, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the given parameter is NOT null
     * @param itemToCheck item to verify is NOT null
     * @param successMessage optional success message or null
     * @param errorMessage optional error message or null
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the item is NOT null / false otherwise
     * @author Genosar.dafna
     * @since 12.08.2024
     */
    public static <N> boolean verifyNotNull(N itemToCheck, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        if(softAssertsWithReport == null)
            AssertsWithReport.assertNotNull(itemToCheck, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNotNull(itemToCheck, successMessage, errorMessage);

        return itemToCheck != null;
    }

    /**
     * Verify each item in the list starts with the given value
     * @param list the list
     * @param valueToStartWith the value to start with in each list's item
     * @param elementName the name of the value to display in the report
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if all items in the list start with the value / false otherwise
     * @author ghawi.rami
     * @since 01.01.2024
     */
    public static boolean verifyEachListElementStartsWith(List<String> list, String valueToStartWith, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean allStartWith = true;
        List<String> wrongItems = new ArrayList<>();
        List<String> correctItems = new ArrayList<>(list);

        for(String listItem : list){
            if(!listItem.startsWith(valueToStartWith)) {
                allStartWith = false;
                wrongItems.add(listItem);
                correctItems.remove(listItem);
            }
        }

        String successMessage = ReportStyle.getSuccessMessage(String.format("All the list items of %s start with the value : %s. List items: %s",elementName, valueToStartWith, correctItems));
        String errorMessage = ReportStyle.getFailureMessage(String.format("At least one of the list items of %s does not start with : %s. The correct items: %s <br>The wrong items: %s",elementName,valueToStartWith,correctItems, wrongItems));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(allStartWith, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(allStartWith, successMessage, errorMessage);

        return allStartWith;
    }

    /**
     * Verify 2 lists match
     * @param list1 list 1
     * @param list2 list 2
     * @param list1_name list 1 name
     * @param list2_name list 2 name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the lists match / false otherwise
     * @author genosar.dafna
     * @since 04.12.2023
     */
    public static <T extends Comparable<? super T>> boolean verifyListsMatch(List<T> list1, List<T> list2, String list1_name, String list2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' and '%s' lists match: <br>%s", list1_name, list2_name, list1));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' and '%s' lists do not match<br>%s: %s<br>%s: %s", list1_name, list2_name, list1_name, list1, list2_name, list2));

        boolean match = ListUtils.sortAndCompareLists(list1, list2, list1_name, list2_name, MessageLevel.ERROR);

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return match;
    }

    /**
     * Verify the list contains the given value
     * @param list the list
     * @param value the value to search in the list
     * @param elementName the name of the value to display in the report
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list contains the value / false otherwise
     * @author genosar.dafna
     * @since 03.12.2023
     */
    public static <T> boolean verifyListContains(List<T> list, T value, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = ReportStyle.getSuccessMessage(String.format("The list contain value: %s", value));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The list does not contain value: %s<br>List: %s", value, list));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(list.contains(value), successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(list.contains(value), successMessage, errorMessage);

        return list.contains(value);
    }

    /**
     * Verify the list does not contain the given value
     * @param list the list
     * @param value the value to search in the list
     * @param elementName the name of the value to display in the report
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list does not contain the value / false otherwise
     * @author genosar.dafna
     * @since 03.12.2023
     */
    public static <T> boolean verifyListDoesNotContain(List<T> list, T value, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = ReportStyle.getSuccessMessage(String.format("The %s list does not contain value: %s, as expected", elementName, value));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The %s list contains value: %s, even though it should not", elementName, value));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(list.contains(value), successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(list.contains(value), successMessage, errorMessage);

        return !list.contains(value);
    }

    /**
     * Verify each item in the list contains the given value
     * @param list the list
     * @param valueToContain the value to search in each list's item
     * @param elementName the name of the value to display in the report
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if all items in the list contain the value / false otherwise
     * @author genosar.dafna
     * @since 03.12.2023
     */
    public static boolean verifyEachListElementContains(List<String> list, String valueToContain, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean allContain = true;

        for(String listItem : list){
            if(!verifyStringContains(listItem, valueToContain, elementName, softAssertsWithReport))
                allContain = false;
        }

        return allContain;
    }

    /**
     * Verify each item in the list does not contain the given value
     * @param list the list
     * @param valueNotToContain the value to search in each list's item
     * @param elementName the name of the value to display in the report
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if all items in the list do not contain the value / false otherwise
     * @author genosar.dafna
     * @since 03.12.2023
     */
    public static boolean verifyEachListElementDoesNotContain(List<String> list, String valueNotToContain, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean allDoNotContain = true;

        for(String listItem : list){
            if(!verifyStringDoesNotContain(listItem, valueNotToContain, elementName, softAssertsWithReport))
                allDoNotContain = false;
        }

        return allDoNotContain;
    }

    /**
     * Verify a key exists in the given map
     * @param map the map
     * @param key the key to search
     * @param mapName optional map name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if a key exists in the given map/ false otherwise
     * @author genosar.dafna
     * @since 23.06.2024
     */
    public static <K, V, T extends Map<K, V>> boolean verifyMapContainsKey(T map, @Nullable K key, @Nullable String mapName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String mapNameString = mapName == null? "" : mapName + " ";
        String successMessage = ReportStyle.getSuccessMessage(String.format("The %smap contains key: '%s'", mapNameString, key));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The %smap does not contain key: '%s', even though it should", mapNameString, key));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(map.containsKey(key), successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(map.containsKey(key), successMessage, errorMessage);

        return map.containsKey(key);
    }

    /**
     * Verify a key does not exist in the given map
     * @param map the map
     * @param key the key to search
     * @param mapName optional map name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if a key does not exist in the given map/ false otherwise
     * @author genosar.dafna
     * @since 23.06.2024
     */
    public static <K, V, T extends Map<K, V>> boolean verifyMapDoesNotContainKey(T map, @Nullable K key, @Nullable String mapName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String mapNameString = mapName == null? "" : mapName + " ";
        String successMessage = ReportStyle.getSuccessMessage(String.format("The %smap does not contain key '%s', as expected", mapNameString, key));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The %smap contains key '%s', even though it should not", mapNameString, key));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(map.containsKey(key), successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(map.containsKey(key), successMessage, errorMessage);

        return map.containsKey(key);
    }

    /**
     * Verify a date is within the given dates range
     * @param date the date to check
     * @param startDate start date of date range
     * @param endDate end date of date range
     * @param date_name the checked date's name/ title to put in the report
     * @param ignoreTime true if to ignore the time of the DateTime (Deduct the hours, minutes and seconds), so the comparison will be on the day, month, year parts
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the date is within the given dates range / false otherwise
     * @author genosar.dafna
     * @since 28.11.2023
     */
    public static <T extends Number> boolean verifyDatesWithinRange(DateTime date, DateTime startDate, DateTime endDate, String date_name, boolean ignoreTime, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        //Deduct the hours, minutes and seconds
        if(ignoreTime){
            date = date.addHours(-date.getHour());
            date = date.addMinutes(-date.getMinute());
            date = date.addSeconds(-date.getSecond());

            startDate = startDate.addHours(-startDate.getHour());
            startDate = startDate.addMinutes(-startDate.getMinute());
            startDate = startDate.addSeconds(-startDate.getSecond());

            endDate = endDate.addHours(-endDate.getHour());
            endDate = endDate.addMinutes(-endDate.getMinute());
            endDate = endDate.addSeconds(-endDate.getSecond());
        }
        //Deduct the milliseconds in all cases
        date = date.addMilliSeconds(-date.getMilliSecond());
        startDate = startDate.addMilliSeconds(-startDate.getMilliSecond());
        endDate = endDate.addMilliSeconds(-endDate.getMilliSecond());

        boolean isWithinDeviation = (date.compareTo(startDate) >= 0) && (date.compareTo(endDate) <=0);
        //boolean isWithinDeviation = (date.compareTo(startDate) >= 0) && (date.compareTo(endDate) <=0);

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s date %s is within the date range of %s and %s", date_name, date.toStringFormat("dd.MM.yyyy"), startDate.toStringFormat("dd.MM.yyyy"), endDate.toStringFormat("dd.MM.yyyy")));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s date %s is not within the date range of %s and %s", date_name, date.toStringFormat("dd.MM.yyyy"), startDate.toStringFormat("dd.MM.yyyy"), endDate.toStringFormat("dd.MM.yyyy")));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);

        return isWithinDeviation;
    }

    /**
     * Verify a string is not empty
     * @param string the string
     * @param stringTitle the name of the string
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the string is not empty / false otherwise
     * @author genosar.dafna
     * @since 04.12.2023
     */
    public static <T extends Number> boolean verifyStringIsNotEmpty(String string, String stringTitle, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        boolean isEmpty = (string == null) || string.isEmpty() || string.trim().equals("");

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' is not empty: %s", stringTitle, string));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' is empty", stringTitle));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(isEmpty, successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(isEmpty, successMessage, errorMessage);

        return !isEmpty;
    }

    /**
     * Verify the expected page title matches the displayed
     * @param expectedTitle expected title
     * @param displayedTitle displayed title
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if title match / false otherwise
     * @author genosar.dafna
     * @since 17.08.2023
     * @since 21.08.2023
     */
    public static boolean verifyPageTitleMatches(String expectedTitle, String displayedTitle, boolean ignoreCase, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = ReportStyle.getSuccessMessage(String.format("The page title is as expected: '%s'", expectedTitle));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The page title is incorrect.<br>Displayed: '%s'<br>Expected: '%s'", displayedTitle, expectedTitle));

        boolean match = ignoreCase? expectedTitle.equalsIgnoreCase(displayedTitle) : expectedTitle.equals(displayedTitle);

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return match;
    }

    /**
     * Verify the button is enabled
     * @param button the button web element
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the button is enabled / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyButtonIsEnabled(WebElement button, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isEnabled = button.isEnabled();

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' button is enabled",button.getText()));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' button is disabled",button.getText()));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isEnabled, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isEnabled, successMessage, errorMessage);

        return isEnabled;
    }

    /**
     * Verify the button is disabled
     * @param button the button web element
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the button is disabled / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyButtonIsDisabled(WebElement button, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isEnabled = button.isEnabled();

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' button is disabled",button.getText()));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' button is enabled",button.getText()));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(isEnabled, successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(isEnabled, successMessage, errorMessage);

        return !isEnabled;
    }

    /**
     * Verify List of maps is not empty (like DB query results)
     * @param listOfMaps the List of maps
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list has values (not empty) / false otherwise
     * @author genosar.dafna
     * @since 06.11.2022
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyListOfMapsIsNotEmpty(L listOfMaps, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list of maps does not have data");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(listOfMaps.size()>0, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(listOfMaps.size()>0, null, errorMessage);

        return listOfMaps.size() > 0;
    }

    /**
     * Verify List of maps is empty (like DB query results)
     * @param listOfMaps the List of maps
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list is empty / false otherwise
     * @author ghawi.rami
     * @since 09.01.2023
     * @author genosar.dafna
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyListOfMapsIsEmpty(L listOfMaps, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list of maps is not empty");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(listOfMaps.size()==0, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(listOfMaps.size()==0, null, errorMessage);

        return listOfMaps.size()==0;
    }


    /**
     * Verify the List is not empty
     * @param list the List
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list has values (not empty) / false otherwise
     * @author genosar.dafna
     * @since 14.09.2023
     * @since 19.11.2024
     */
    public static <T, L extends List<T>> boolean verifyListIsNotEmpty(L list, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        return verifyListIsNotEmpty(list, successMessage,errorMessage, true, softAssertsWithReport);
    }

    /**
     * Verify the List is not empty
     * @param list the List
     * @param takeScreenshot true if to attach Screenshot / false otherwise
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list has values (not empty) / false otherwise
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public static <T, L extends List<T>> boolean verifyListIsNotEmpty(L list, @Nullable String successMessage, @Nullable String errorMessage, boolean takeScreenshot, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        successMessage = successMessage != null? ReportStyle.getSuccessMessage(successMessage) : null;
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list is empty");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(list.size()>0, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(list.size()>0, successMessage, errorMessage, takeScreenshot);

        return list.size() > 0;
    }

    /**
     * Verify the List is empty
     * @param list the List
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the list is empty / false otherwise
     * @author genosar.dafna
     * @since 14.09.2023
     */
    public static <T, L extends List<T>> boolean verifyListIsEmpty(L list, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        successMessage = successMessage != null? ReportStyle.getSuccessMessage(successMessage) : null;
        errorMessage = errorMessage != null? errorMessage : ReportStyle.getFailureMessage("The list is not empty");

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(list.size()==0, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(list.size()==0, successMessage, errorMessage);

        return list.size() == 0;
    }

    /**
     * Verify query results are not empty
     * @param resultsData the query results
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the results are not empty / false otherwise
     * @author genosar.dafna
     * @since 06.11.2022
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyQueryResultsAreNotEmpty(L resultsData, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String errorMessage = "The query did not return data";
        return verifyListOfMapsIsNotEmpty(resultsData, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify query results are empty
     * @param resultsData the query results
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the results are empty / false otherwise
     * @author ghawi.rami
     * @since 09.01.2023
     * @author genosar.dafna
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyQueryResultsAreEmpty(L resultsData, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String errorMessage = "The query returned data, even though it should be empty";
        return verifyListOfMapsIsEmpty(resultsData, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify number of entry sets in the list of maps (like db query records)
     * @param listOfMaps the List of maps
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Number of Entry Sets matches the expected / false otherwise
     * @author genosar.dafna
     * @since 08.02.2023
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyNumberOfEntrySets(L listOfMaps, int expected, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        successMessage = (successMessage != null)? successMessage : getSuccessMessage(String.format("The number of maps sets in the list is correct: %d", expected));
        errorMessage = (errorMessage != null)? errorMessage : getFailureMessage(String.format("The number of maps sets in the list is incorrect <br>In Db: %d<br>Expected: %d", listOfMaps.size(), expected));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertEquals(expected, listOfMaps.size(), successMessage, errorMessage);
        else
            softAssertsWithReport.assertEquals(expected, listOfMaps.size(), successMessage, errorMessage);

        return expected == listOfMaps.size();
    }

        /**
     * Verify number of query records
     * @param resultsData the query results
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Number of Query Records matches the expected / false otherwise
     * @author genosar.dafna
     * @since 08.02.2023
     * @since 10.09.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> boolean verifyNumberOfQueryRecords(L resultsData, int expected, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {

        String successMessage = String.format("The number of query records is correct: %d", expected);
        String errorMessage = String.format("The number of query records is incorrect <br>In Db: %d<br>Expected: %d", resultsData.size(), expected);

        return verifyNumberOfEntrySets(resultsData, expected, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the list of maps contain an entry with the given data
     * @param listOfMaps list of maps
     * @param mapData map of data to search
     * @param successMessage optional success message. if null will report the default
     * @param errorMessage optional error message. if null will report the default
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 31.01.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyMapEntrySetExists(L listOfMaps, T mapData, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport){

        reportAndLog(String.format("<b>Map verification - Verify map entry set exists: %s</b>", mapData.toString()), MessageLevel.INFO);
        T entrySet = getMapRecord(listOfMaps, mapData);

        successMessage = (successMessage != null)? getSuccessMessage(successMessage) : getSuccessMessage(String.format("The list of maps contain entry: <br> %s", mapData));
        errorMessage = (errorMessage != null)? getFailureMessage (errorMessage) : getFailureMessage(String.format("The list of maps does not contain entry: <br> %s", mapData));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertNotNull(entrySet, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNotNull(entrySet, successMessage, errorMessage);

        return entrySet;
    }

    /**
     * Verify list of maps does not contain an entry with the given data
     * @param listOfMaps list of maps
     * @param mapData map of data to search
     * @param successMessage optional success message. if null will report the default
     * @param errorMessage optional error message. if null will report the default
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 07.02.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyMapEntrySetDoesNotExist(L listOfMaps, T mapData, @Nullable String successMessage, @Nullable String errorMessage, @Nullable SoftAssertsWithReport softAssertsWithReport){

        reportAndLog(String.format("<b>Map verification - Verify map entry set does not exist: %s</b>", mapData.toString()), MessageLevel.INFO);

        T entrySet = getMapRecord(listOfMaps, mapData);

        successMessage = (successMessage != null)? getSuccessMessage(successMessage) : getSuccessMessage(String.format("The list of maps does not contain the expected entry, as expected: <br> %s", mapData));
        errorMessage = (errorMessage != null)? getFailureMessage (errorMessage) : getFailureMessage(String.format("The list of maps contains the entry, even though it should not: <br> %s", mapData));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertNull(entrySet, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNull(entrySet, successMessage, errorMessage);

        return entrySet;
    }

    /**
     * Verify a certain row was found in the table
     * @param table The table
     * @param expectedRowData the expected row data
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return the row
     * @author genosar.dafna
     * @since 11.12.2023
     */
    public static <K, V, M extends Map<K, V>, T extends Table, TR extends TableRow> TR verifyTableRowExists(T table, M expectedRowData, @com.sun.istack.Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        //Find the expected row
        TableRow row;
        try {
            row = table.getRow(expectedRowData);
        }
        catch(Throwable t){
            row = null;
        }
        String expectedRowDataString = expectedRowData.toString().replace("{", "").replace("}", "");

        String successMessage = getSuccessMessage(String.format("The row was found in the table, as expected.<br>Row: %s", expectedRowDataString));
        String errorMessage = getFailureMessage(String.format("The row could not be found in the table even though it should be.<br> Row: %s", expectedRowDataString));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertNotNull(row, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNotNull(row, successMessage, errorMessage);

        return (TR)row;
    }

    /**
     * Verify a certain row is not in the table
     * @param table The table
     * @param expectedRowData the expected row data
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the row was not found / false otherwise
     * @author genosar.dafna
     * @since 11.12.2023
     */
    public static <K, V, M extends Map<K, V>, T extends Table, TR extends TableRow> boolean verifyTableRowDoesNotExist(T table, M expectedRowData, @com.sun.istack.Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        //Try to find the expected row
        TableRow row;
        try {
            row = table.getRow(expectedRowData);
        }
        catch(Throwable t){
            row = null;
        }
        String expectedRowDataString = expectedRowData.toString().replace("{", "").replace("}", "");

        String successMessage = getSuccessMessage(String.format("The row was not found in the table, as expected.<br>Row: %s", expectedRowDataString));
        String errorMessage = getFailureMessage(String.format("The row was found in the table even though it should not be.<br> Row: %s", expectedRowDataString));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertNull(row, successMessage, errorMessage);
        else
            softAssertsWithReport.assertNull(row, successMessage, errorMessage);

        return row == null;
    }

    /**
     * Verify the DB results contain an entry with the given data
     * @param dbResults db results
     * @param rowData hash of data to search
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 31.01.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyDbRowExists(L dbResults, T rowData, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = getSuccessMessage(String.format("The db results contain entry: <br> %s", rowData));
        String errorMessage = getFailureMessage(String.format("The db results do not contain entry: <br> %s", rowData));

        return verifyMapEntrySetExists(dbResults, rowData, successMessage, errorMessage, softAssertsWithReport);

    }

    /**
     * Verify the DB results does not contain an entry with the given data
     * @param dbResults db results
     * @param rowData hash of data to search
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 07.02.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> T verifyDbRowDoesNotExist(L dbResults, T rowData, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = getSuccessMessage(String.format("The db results does not contain entry, as expected: <br> %s", rowData));
        String errorMessage = getFailureMessage(String.format("The db results contains entry, even though it should not: <br> %s", rowData));

        return verifyMapEntrySetDoesNotExist(dbResults, rowData, successMessage, errorMessage, softAssertsWithReport);
    }

    /**
     * Verify the map entry value match the expected
     * @param entry row entry
     * @param expected the expected value
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 31.01.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T verifyEntryValueMatches(T entry, K key, V expected, @Nullable SoftAssertsWithReport softAssertsWithReport){

        V value = entry.get(key);

        boolean containsKey = entry.containsKey(key);

        String errorMessage = getFailureMessage(String.format("The Map does not contain key '%s'", key));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(containsKey, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(containsKey, null, errorMessage);

        if(!containsKey)
            return null;

        String successMessage = getSuccessMessage(String.format("The value of '%s' in map entry set is correct: %s", key, value.toString()));
        errorMessage = getFailureMessage(String.format("The value of '%s' in map entry set is incorrect: %s. Expected: %s", key, value, expected));

        boolean match = Objects.equals(value, expected);

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return entry;
    }

    /**
     * Verify the map entry values match the expected values
     * @param entry row entry
     * @param expected the expected values
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return the entry (row) that matched the expected data, or else null if not found
     * @author genosar.dafna
     * @since 09.02.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T verifyEntryValuesMatch(T entry, T expected, @Nullable SoftAssertsWithReport softAssertsWithReport){

        T row = null;
        for (Map.Entry<K, V> expectedData: expected.entrySet()) {
            K expectedKey = expectedData.getKey();
            V expectedValue = expectedData.getValue();
            row = verifyEntryValueMatches(entry, expectedKey, expectedValue, softAssertsWithReport);
            if(row == null)
                return null;
        }
        return row;
    }

    /**
     * Verify actual map of data matches the expected data
     * @param actualData the actual data
     * @param expectedData the expected data
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return rue if maps match, false otherwise
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 26.10.2023
     */
    public static <K, V, T extends Map<K, V>> boolean verifyComparisonOfMaps(T actualData, T expectedData, String data1_name, String data2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        boolean match = MapUtils.compareMaps(actualData, expectedData, data1_name, data2_name, MessageLevel.ERROR);

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s matches %s", data1_name, data2_name));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s does not match %s", data1_name, data2_name));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(match, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(match, successMessage, errorMessage);

        return match;
    }

    /**
     * Verify actual list of maps matches the expected data. Will not compare the content if the lists are not the same size
     * @param list1 the actual data
     * @param list2 the expected data
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 29.06.2025
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> void verifyComparisonOfHashMapsLists(L list1, L list2, String data1_name, String data2_name, @Nullable String columnOfUniqueValueToDisplayInReport, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        reportAndLog(String.format("Compare Map lists '%s' and '%s'", data1_name, data2_name), MessageLevel.INFO);

        boolean sameSize = list1.size() == list2.size();

        String successMessage = ReportStyle.getSuccessMessage("The size of Actual data list and Expected data list is the same");
        String errorMessage = ReportStyle.getFailureMessage(String.format("The size of Actual data list is not the same size as the expected data list<br>" +
                "Actual data size: %d<br>" +
                "Expected data size: %s", list1.size(), list2.size()));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(sameSize, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(sameSize, successMessage, errorMessage);

        if (sameSize) {
            for(int i = 0; i< list1.size() ; i++) {

                String uniqueValue1 = "";
                String uniqueValue2 = "";
                String data1_name_forReport = data1_name;
                String data2_name_forReport = data2_name;

                if(columnOfUniqueValueToDisplayInReport != null){
                    uniqueValue1 = list1.get(i).get(columnOfUniqueValueToDisplayInReport).toString();
                    uniqueValue2 = list2.get(i).get(columnOfUniqueValueToDisplayInReport).toString();
                    data1_name_forReport = "%s<br>%s: %s".formatted(data1_name, columnOfUniqueValueToDisplayInReport, uniqueValue1);
                    data2_name_forReport = "%s<br>%s: %s".formatted(data2_name, columnOfUniqueValueToDisplayInReport, uniqueValue2);
                }

                verifyComparisonOfMaps(list1.get(i), list2.get(i), data1_name_forReport, data2_name_forReport, softAssertsWithReport);
            }
        }
        else{
            logger.info("Create sets");
            Set<T> set1 = new HashSet<>(list1);
            Set<T> set2 = new HashSet<>(list2);

            logger.info("Items in list1 but not in list2");
            // Items in list1 but not in list2
            Set<T> onlyInList1 = new HashSet<>(set1);
            onlyInList1.removeAll(set2);

            logger.info("Items in list2 but not in list1");
            // Items in list2 but not in list1
            Set<T> onlyInList2 = new HashSet<>(set2);
            onlyInList2.removeAll(set1);

        }
    }

    /**
     * Verify actual list of maps matches the expected data. Will not compare the content if the lists are not the same size
     * @param actualData the actual data
     * @param expectedData the expected data
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 04.10.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> void verifyComparisonOfHashMapsLists(L actualData, L expectedData, String data1_name, String data2_name, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        reportAndLog(String.format("Compare Map lists '%s' and '%s'", data1_name, data2_name), MessageLevel.INFO);

        boolean sameSize = actualData.size() == expectedData.size();

        String successMessage = ReportStyle.getSuccessMessage("The size of Actual data list and Expected data list is the same");
        String errorMessage = ReportStyle.getFailureMessage(String.format("The size of Actual data list is not the same size as the expected data list<br>" +
                "Actual data size: %d<br>" +
                "Expected data size: %s", actualData.size(), expectedData.size()));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(sameSize, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(sameSize, successMessage, errorMessage);

        if (sameSize) {
            for(int i=0; i<actualData.size() ; i++) {
                verifyComparisonOfMaps(actualData.get(i), expectedData.get(i), data1_name, data2_name, softAssertsWithReport);
            }
        }
    }

    /**
     * Verify the given String contains the given value
     * @param stringToCheck string to check
     * @param valueToContain value to contains
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the String contains the value / false otherwise
     * @author genosar.dafna
     * @since 03.12.2023
     */
    public static boolean verifyStringContains(String stringToCheck, String valueToContain, String stringName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s '%s' contains '%s'", stringName, stringToCheck, valueToContain));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s '%s' does not contain '%s', even though it should", stringName, stringToCheck, valueToContain));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(stringToCheck.contains(valueToContain), successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(stringToCheck.contains(valueToContain), successMessage, errorMessage);

        return stringToCheck.contains(valueToContain);
    }

    /**
     * Verify the given String does not contain the given value
     * @param stringToCheck string to check
     * @param valueNotToContain value not to contain
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the String does not contain the value / false otherwise
     * @author genosar.dafna
     * @since 03.12.2023
     */
    public static boolean verifyStringDoesNotContain(String stringToCheck, String valueNotToContain, String stringName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s '%s' does not contain '%s', as expected", stringName, stringToCheck, valueNotToContain));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s '%s' contains '%s', even though it should not", stringName, stringToCheck, valueNotToContain));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertFalse(stringToCheck.contains(valueNotToContain), successMessage, errorMessage);
        else
            softAssertsWithReport.assertFalse(stringToCheck.contains(valueNotToContain), successMessage, errorMessage);

        return !stringToCheck.contains(valueNotToContain);
    }

    /**
     * Verify 2 numbers are within an allowed deviation.
     * For example:
     * El-Al allows a suitcase weight of 30KG
     * The deviation EL AL allows is 2 KG, which means it will not charge extra if the suitcase is over 30KG, but up to 32KG
     * The passenger brought a 32KG suitcase - no extra charge
     * The passenger brought a 33KG suitcase - there will be an extra charge
     * @param num1 first number, for example: the allowed suitcase weight written on EL_AL site is 30KG
     * @param num2 second number, for example: the actual suitcase weight the passenger brought is 32KG
     * @param allowedDeviation the allowed deviation, for example: El-Al will not charge if the actual weight is withing the deviation of 2KG
     * @param num1_name name of first value, like "EL AL Allowed weight"
     * @param num2_name name of first value, like "Actual weight"
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the numbers are within the allowed deviation / false otherwise
     * @param <T> generic number type, like int, double, float
     * @author genosar.dafna
     * @since 18.10.2023
     */
    public static <T extends Number> boolean verifyValuesWithinDeviation(T num1, T num2, T allowedDeviation, String num1_name, String num2_name, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        double value1 = num1.doubleValue();
        double value2 = num2.doubleValue();
        double result = value1 - value2;

        boolean isWithinDeviation = (Math.abs(result) <= allowedDeviation.doubleValue());

        String successMessage = ReportStyle.getSuccessMessage(String.format("%s and %s are within the allowed deviation of %s", num1_name, num2_name, allowedDeviation));
        String errorMessage = ReportStyle.getFailureMessage(String.format("%s and %s are not within the allowed deviation of %s<br>%s: %s<br>%s: %s", num1_name, num2_name, allowedDeviation, num1_name, num1, num2_name, num2));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isWithinDeviation, successMessage, errorMessage);

        return isWithinDeviation;
    }

    /**
     * Verify the Excel download time was within the expected threshold
     * @param actualDownloadSeconds actual download time
     * @param expectedThresholdSeconds threshold
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Excel Download Time was Within Threshold / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyExcelDownloadTimeIsWithinThreshold(Double actualDownloadSeconds, Double expectedThresholdSeconds, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        String successMessage = getSuccessMessage(String.format("Excel download time was within the reasonable time of %s seconds", expectedThresholdSeconds));
        String errorMessage = getFailureMessage(String.format("Excel download time was not within the reasonable time of %s seconds. <br>Download time was %s", expectedThresholdSeconds, actualDownloadSeconds));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(actualDownloadSeconds <= expectedThresholdSeconds, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(actualDownloadSeconds <= expectedThresholdSeconds, successMessage, errorMessage);

        return actualDownloadSeconds <= expectedThresholdSeconds;
    }

    /**
     * Verify the Excel download time was within the exoected timeout
     * @param actualDownloadSeconds actual download time
     * @param expectedTimeoutSeconds timeout limit
     * @param elementName element name
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Element Loading Time was Within Threshold / false otherwise
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 10.09.2023
     */
    public static boolean verifyElementLoadingTimeIsWithinThreshold(Double actualDownloadSeconds, Double expectedTimeoutSeconds, String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        String successMessage = getSuccessMessage(String.format("%s loading time was within the reasonable time of %s seconds: %s", elementName, expectedTimeoutSeconds, actualDownloadSeconds));
        String errorMessage = getFailureMessage(String.format("%s loading time was not within the reasonable time of %s seconds. <br>Download time was %s", elementName, expectedTimeoutSeconds, actualDownloadSeconds));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(actualDownloadSeconds <= expectedTimeoutSeconds, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(actualDownloadSeconds <= expectedTimeoutSeconds, successMessage, errorMessage);

        return actualDownloadSeconds <= expectedTimeoutSeconds;
    }

    /**
     * Verify the file was downloaded successfully
     * @param fileDownloaded true/false if a new file was downloaded to DOWNLOADS folder
     * @param filePath the file path
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the File was Downloaded / false otherwise
     * @author genosar.dafna
     * @since 15.06.2023
     * @since 10.09.2023
     */
    public static boolean verifyFileDownloaded(boolean fileDownloaded, String filePath, @Nullable SoftAssertsWithReport softAssertsWithReport)
    {
        String successMessage = getSuccessMessage("The file was downloaded successfully");
        String errorMessage = getFailureMessage(String.format("The file was not downloaded successfully to %s", filePath));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(fileDownloaded, null, errorMessage);
        else
            softAssertsWithReport.assertTrue(fileDownloaded, null, errorMessage);

        boolean fileExists = false;

        if(fileDownloaded){
            fileExists = new File(filePath).exists();
            if(softAssertsWithReport == null)
                AssertsWithReport.assertTrue(fileExists, successMessage, errorMessage);
            else
                softAssertsWithReport.assertTrue(fileExists, successMessage, errorMessage);
        }

        return fileDownloaded && fileExists;
    }

    /**
     * Method compares two objects and writes results to log
     * @param actual object for test
     * @param expected how the tested object should be like
     * @param description description of the object for test
     * @return true if the objects are equal, false otherwise
     */

    public static boolean verifyActualIsAsExpected(Object actual, Object expected, String description)
    {
        boolean verified = true;
        try
        {
            logger.info("Verifying that actual " + description + " is " + expected);
            if (expected.equals(actual))
            {
                logger.info("PASS. Expected " + description + " -" + expected + "- verified");
                extentLogger(LogStatus.PASS, "Expected " + description + " -" + expected + "- verified");
            }

            else
            {
                verified = false;
                logger.info("FAIL. Expected " + description + " -" + expected + "- not verified");
                extentLogger(LogStatus.FAIL, "Expected " + description + " -" + expected + "- not verified");
            }
        }
        catch(Exception e)
        {
            verified = false;
            logger.info("Caught exception: " + e);
        }

        return verified;
    }

    public static boolean verifyActualIsAsExpected(Object actual, Object expected, String description, String screenshotFilePath)
    {
        boolean isVerified = verifyActualIsAsExpected(actual, expected, description);
        if (!isVerified)
        {
            ExtentReportUtils.attachScreenshotToExtentReport(screenshotFilePath);
        }

        return isVerified;
    }

    /**
     * Verify the element is displayed
     * @param element the web element
     * @param elementName the name of the element
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the Element is Displayed / false otherwise
     * @author ghawi.rami
     * @since 28.08.2023
     * @author genosar.dafna
     * @since 10.09.2023
     */
    public static boolean verifyElementIsDisplayed(WebElement element,String elementName, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isDisplayed = element.isDisplayed();

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' element is displayed",elementName));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' element is not displayed",elementName));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertTrue(isDisplayed, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(isDisplayed, successMessage, errorMessage);

        return isDisplayed;
    }

    /**
     * Verify the status of the checkbox is as expected
     * @param checkBox the checkBox
     * @param checkboxName the checkbox name
     * @param shouldBeChecked true if the checkbox should be checked / false otherwise
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @return true if the checkbox status is as expected / false otherwise
     * @author genosar.dafna
     * @since 05.09.2023
     * @since 10.09.2023
     */
    public static boolean verifyCheckboxStatus(CheckBox checkBox, String checkboxName, boolean shouldBeChecked, @Nullable SoftAssertsWithReport softAssertsWithReport){

        boolean isChecked = checkBox.isSelected();
        String expectedStatus = shouldBeChecked? "checked" : "unchecked";
        String displayedStatus = isChecked? "checked" : "unchecked";

        String successMessage = ReportStyle.getSuccessMessage(String.format("The '%s' checkbox's status is as expected: %s", checkboxName, expectedStatus));
        String errorMessage = ReportStyle.getFailureMessage(String.format("The '%s' checkbox's status is incorrect <br>Current status: %s<br>Expected status: %s", checkboxName, expectedStatus, displayedStatus));

        if(softAssertsWithReport == null)
            AssertsWithReport.assertEquals(shouldBeChecked, isChecked, successMessage, errorMessage);
        else
            softAssertsWithReport.assertEquals(shouldBeChecked, isChecked, successMessage, errorMessage);

        return shouldBeChecked == isChecked;
    }

    /**
     * Verify the performance time is lower or equals the threshold
     * @param performanceTime       The current performance time
     * @param threshold             the threshold time
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @author genosar.dafna
     * @since 30.11.2023
     */
    public static void verifyPerformanceTime(double performanceTime, double threshold, @Nullable SoftAssertsWithReport softAssertsWithReport) {

        String roundedSecString = String.format("%.2f", performanceTime);

        String successMessage = getSuccessMessage(String.format("Loading time (%s) was blow the threshold of %s", roundedSecString, threshold));
        String errorMessage = getFailureMessage(String.format("Loading time was higher than the threshold<br>Loading time: %s<br>Threshold: %s", roundedSecString, threshold));

        if (softAssertsWithReport == null)
            AssertsWithReport.assertTrue(performanceTime <= threshold, successMessage, errorMessage);
        else
            softAssertsWithReport.assertTrue(performanceTime <= threshold, successMessage, errorMessage);

    }

    public static void verifyPerformanceTime(Stopwatch stopwatch, double threshold, @Nullable SoftAssertsWithReport softAssertsWithReport) {
        Time time = new Time(stopwatch);
        verifyPerformanceTime(time.doubleSeconds, threshold, softAssertsWithReport);
    }

    /**
     * Verify the performance time is lower or equals the threshold and report a generic bug
     * @param performanceTime       The current performance time
     * @param threshold             the threshold time
     * @param softAssertsWithReport a SoftAssertsWithReport or null if you wish to use AssertsWithReport
     * @param bugDescription name of the bug in Jira
     * @param bugSeverityEnum significance of the bug
     * @param dateTime bug date creation
     * @author khazov.alex
     * @since 24.08.2025
     */

    public static void verifyPerformanceTimeAndReportBug(double performanceTime, double threshold, @Nullable SoftAssertsWithReport softAssertsWithReport, String bugDescription, BugSeverityEnum bugSeverityEnum, DateTime dateTime) {
        verifyPerformanceTime(performanceTime, threshold, softAssertsWithReport);
        if (performanceTime >= threshold) {
            Report.reportBug(bugDescription, bugSeverityEnum, dateTime);
        }
    }


}
