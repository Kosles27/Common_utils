package reportUtils;

import Managers.ExtentReportInstanceManager;
import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import constantsUtils.CommonConstants;

import static dateTimeUtils.DateUtils.getTodayDateInFormat;

public class ExtentReportUtils {

    private static final String ReportFilePath = CommonConstants.EnvironmentParams.EXTENT_REPORT_FOLDER;



    /**
     * initiate a new extentReport and put it into the map with its class's display name
     * @param classDisplayName class annotation display name
     *
     * @author - Tzvika Sela
     * @since - 6.6.2021
     *
     */
    public static synchronized void initExtentReports(String classDisplayName) {
        String currentDateAndTime = getTodayDateInFormat(CommonConstants.DatePatterns.DDMMYYYY_HHMMSS);
        String reportName = ReportFilePath + classDisplayName + "_" + currentDateAndTime + ".html";
        ExtentReports extent = new ExtentReports(reportName, true);
        ExtentReportInstanceManager.addCurrentExtentReport(classDisplayName,extent,classDisplayName + "_" + currentDateAndTime + ".html");


    }

    public  static void extentLogger(LogStatus logStatus, String details) {
        ReportInstanceManager.getCurrentTestReport().log(logStatus, details);
    }


    public  static void extentLogger(LogStatus logStatus, String stepName, String details) {
        ReportInstanceManager.getCurrentTestReport().log(logStatus, stepName, details);
    }

    public  static void attachScreenshotToExtentReport(String screenShotFilePath) {
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, ReportInstanceManager.getCurrentTestReport().addScreenCapture(screenShotFilePath));
    }

    public static void startTestReport(String testName, String testDescription,ExtentReports extentReport) {
        ExtentTest test = extentReport.startTest(testName, testDescription);
        ReportInstanceManager.addCurrentTestReport(test);
    }

    public static void endTestReport(ExtentReports extentReport) {
        extentReport.endTest(ReportInstanceManager.getCurrentTestReport());
    }

    public static void finalizeExtentReport(ExtentReports extentReport) {
        extentReport.flush();
    }

}
