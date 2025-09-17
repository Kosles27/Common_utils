package Managers;

import com.relevantcodes.extentreports.ExtentTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds all the instances of ExtentTest tests objects
 * This class holds a map of all the instances of ExtentTest tests created during the tests.
 * It puts the test object along with its thread id, so the class will work properly even for
 * multi thread environment
 *
 * @author Lior Umflat
 * @since April 21
 */
public class ReportInstanceManager {

    private static Map<Integer, ExtentTest> testMap = new ConcurrentHashMap<Integer, ExtentTest>();
    private ReportInstanceManager(){}

    /**add current ExtentTest instance to the testMap
     */
    public static void addCurrentTestReport(ExtentTest test)
    {
        Integer threadId = (int) (Thread.currentThread().getId());
        testMap.put(threadId,test);
    }

    /**get current ExtentTest instance*/
    public static ExtentTest getCurrentTestReport()
    {
        Integer threadId = (int) (Thread.currentThread().getId());
        return testMap.get(threadId);
    }
}
