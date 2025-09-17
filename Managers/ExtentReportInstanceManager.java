package Managers;

import com.relevantcodes.extentreports.ExtentReports;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the instances of ExtentReport objects
 * This class holds a map of all the instances of ExtentReport reports which is created during the class initiation (Before All listener).
 * It puts the ExtentReport object along with its display name as key, so the class will work properly even for
 * multi threaded environment
 *
 * @author Tzvika Sela
 * @since 03.06.2021
 */
public class ExtentReportInstanceManager {

    private static Map<String, ExtentReports> mapReport = new HashMap<String, ExtentReports>();
    private static Map<String, String> mapReportNames = new HashMap<String, String>();


    private ExtentReportInstanceManager(){}

    /**
     * add Extent Report class instance to mapReports and its name to mapReportNames map
     * @param classDisplayName - class display name
     * @param extentReport
     * @param reportName
     * @author tzvika.sela
     */

    public static void addCurrentExtentReport(String classDisplayName, ExtentReports extentReport, String reportName)
    {
        mapReport.put(classDisplayName,extentReport);
        mapReportNames.put(classDisplayName,reportName);
    }

    /**
     * get the class's ExtentReport
     * @param classDisplayName the class display name
     * @return report instance
     */
    public static ExtentReports getCurrentExtentReport(String classDisplayName)
    {
        return mapReport.get(classDisplayName);
    }

    /**
     * get the class display name and returns the report name for that class
     * @author tzvika.sela
     * @param classDisplayName the class display name
     * @return the report's name
     */
    public static String getCurrentExtentReportName(String classDisplayName)
    {
        return mapReportNames.get(classDisplayName);
    }
}
