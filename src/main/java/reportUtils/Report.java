package reportUtils;

import Managers.ReportInstanceManager;
import Managers.WebDriverInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import enumerations.BugSeverityEnum;
import enumerations.MessageLevel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static imageUtils.ScreenCaptureUtils.takeElementScreenShot;
import static imageUtils.ScreenCaptureUtils.takeScreenShot;
import static reportUtils.ReportStyle.removeHtmlTagsForLog;

@SuppressWarnings("unused")
public class Report {

    private static final Logger logger = LoggerFactory.getLogger(Report.class);

    /**
     * Write the current URL to the report including a link to this page
     * @param pageName optional name of the page, if you wish to add the name to the log
     * @return the current URL
     * @author genosar.dafna
     * @since 18.10.2023
     */
    public static String reportCurrentUrl(@Nullable String pageName){

        pageName = pageName == null? "" : pageName;
        String url = WebDriverInstanceManager.getDriverFromMap().getCurrentUrl();
        reportUtils.Report.reportAndLog(String.format("Opened page:%s<br>" +
                "<a target='_blank' href='%s'</a>%s", pageName, url, url), MessageLevel.INFO);

        return url;
    }

    /**
     * Get a String to add to the report that holds a link's text and href
     * @param link the link
     * @return a String to add to the report that holds a link's text and href
     * @author genosar.dafna
     * @since 07.11.2024
     */
    private static String getLinkToReport(String link){

        return String.format("<a target='_blank' href='%s'</a>%s", link, link);
    }

    /**
     * This method prints a message (of certain cardinality) to both report and log
     *
     * @param msg  message to print both to log and report
     * @param ml the message level from the MessageLevel Enumerator
     * @author zvika.sela
     * @since 26.04.2021
     * @author Dafna Genosar
     * @since 15.05.2023
     */
    public static void reportAndLog(String msg, MessageLevel ml){

        String reportMsg = msg.replace("/n", "<br/>").replace("\n", "<br/>");
        String logMsg = removeHtmlTagsForLog(msg);

        switch (ml) {

            case INFO:
                logger.info(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null)
                    ExtentReportUtils.extentLogger(LogStatus.INFO, reportMsg);
                break;

            case WARN:
                logger.warn(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null) {
                    ExtentReportUtils.extentLogger(LogStatus.WARNING, reportMsg);
                }
                break;

            case ERROR:
                logger.error(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null) {
                    ExtentReportUtils.extentLogger(LogStatus.ERROR, reportMsg);
                }
                break;

            case FAIL:
                logger.error(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null)
                    ExtentReportUtils.extentLogger(LogStatus.FAIL, reportMsg);
                break;
            case DEBUG:
                logger.debug(logMsg);
                if (ReportInstanceManager.getCurrentTestReport() != null)
                    ExtentReportUtils.extentLogger(LogStatus.INFO, reportMsg);
                break;
        }

    }

    /**
     * Take a screenshot and add it to the log if needed (regardless to failures)
     * @param ml the message level from the MessageLevel Enumerator
     * @author Dafna Genosar
     * @since 31.01.2024
     */
    public static void reportScreenshot(MessageLevel ml){
        String path = takeScreenShot(WebDriverInstanceManager.getDriverFromMap());
        reportAndLog(ReportInstanceManager.getCurrentTestReport().addScreenCapture(path), ml);
    }

    /**
     * Take a screenshot and add it to the log if needed (regardless to failures)
     * @param driver the driver to take screenshot of (in case of multiple drivers)
     * @param ml the message level from the MessageLevel Enumerator
     * @author Dafna Genosar
     * @since 08.04.2024
     */
    public static void reportScreenshot(WebDriver driver, MessageLevel ml){
        String path = takeScreenShot(driver);
        reportAndLog(ReportInstanceManager.getCurrentTestReport().addScreenCapture(path), ml);
    }

    /**
     * Take a screenshot of the given element and add it to the log if needed (regardless to failures)
     * @param ml the message level from the MessageLevel Enumerator
     * @author Dafna Genosar
     * @since 10.11.2024
     */
    public static void reportElementScreenshot(WebElement element, @Nullable String elementName, MessageLevel ml){
        String path = takeElementScreenShot(WebDriverInstanceManager.getDriverFromMap(), element, elementName);
        reportAndLog(ReportInstanceManager.getCurrentTestReport().addScreenCapture(path), ml);
    }

    /**
     * Report a highlighted bug line
     * @param details the text line to report
     * @author genosar.dafna
     * @since 07.08.2022
     */
    public static void reportBug(String details)
    {
        details = "<mark>" + details + "</mark>";
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.WARNING, details);
    }

    /**
     * Report a highlighted bug line with severity and date of creation
     * @param details the text line to report
     * @param severity the bug severity
     * @param bugDateCreation the date of the bug creation
     * @author ghawi.rami
     * @since 01.01.2024
     * @author genosar.dafna
     * @since 08.12.2024
     */
    public static void reportBug(String details, @Nullable BugSeverityEnum severity, @Nullable DateTime bugDateCreation)
    {
        if(details.toLowerCase().contains("[qa automation bug]"))
            details = details.replaceAll("\\[.*?\\]", "[QA Automation bug]");

        if (severity != null && bugDateCreation != null) {
            details = String.format("<mark>" + details + "| Severity: %s " + "| Date of Bug Creation: %s" + "</mark>", severity.getName(), bugDateCreation.toShortDateString(false));
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.WARNING, details);
        } else {
            details = "<mark>" + details + "| Severity: doesn't exist " + "| Date of Bug Creation: doesn't exist" + "</mark>";
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.WARNING, details);
        }
    }

    /**
     * Report an optional message with a URL and create an actual link to this url
     * @param prefixMessage optional additional message that will display before the URL
     * @param url the URL to report
     * @param ml the message level from the MessageLevel Enumerator
     * @author Dafna Genosar
     * @since 07.11.2024
     */
    public static void reportUrl(@Nullable String prefixMessage, String url, MessageLevel ml){
        String linkString = getLinkToReport(url);

        StringBuilder msg = new StringBuilder();

        if(prefixMessage != null)
            msg.append(prefixMessage).append("<br>");

        msg.append(linkString);

        reportAndLog(msg.toString(), ml);
    }
}
