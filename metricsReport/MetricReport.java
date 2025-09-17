package metricsReport;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static listeners.ElasticMetricsListener.testPlanEndTime;
import static listeners.ElasticMetricsListener.testPlanStartTime;

/**
 * Singleton Class to hold the test plan's metrics
 * @author sela.zvika
 * @since  02.01.2023
 */
public class MetricReport {


    private ArrayList<TestMetric> tests;


        private static MetricReport instance =null;

        private MetricReport(){
              tests = new ArrayList<>();
        }

        public static synchronized  MetricReport getReport()  {
                if(instance ==null)
                        instance = new MetricReport();
                return instance;
        }

    /**
     * get all tests in report
     * @return  list of all test metrics
     */
    public ArrayList<TestMetric> getTests() {
        return tests;
    }

    /**
     * Add new test metric to the report.
     * Due to repetitions bug if a test method already exist we remove it before adding the new one.
     * @param test the test metric to add to the report
     * @author tzvika.sela
     */
    public synchronized void addTest(TestMetric test){

            String methodName = test.getMethodName();
            String className = test.getClassName();
            long count = getTests().stream().filter(t->t.getMethodName().equals(methodName) && t.getClassName().equals(className)).count();
            //if duplicate found remove all except the last appearance
            if (count==1 && test.getAttempt()>1) {
                TestMetric testMetricToRemove = getTests().stream().filter(t -> t.getMethodName().equals(methodName) && t.getClassName().equals(className)).findFirst().get();
                removeTest(testMetricToRemove);
            }

             tests.add(test);
        }

    /**
     * get test metric object by id
     * @param id - the contextExtension unique Id
     */
    public synchronized TestMetric getTest(String id){
        List<TestMetric> testMetricList = new ArrayList<>();
        if (tests!=null)
            testMetricList = tests.stream().filter(t->t.getTestId().equals(id)).collect(Collectors.toList());
        if (testMetricList.stream().findFirst().isPresent())
            return testMetricList.stream().findFirst().get();

        return  null;
    }

    /**
     * remove test metric from ArrayList
     * @param testMetric - the testMetric to remove
     */
    public synchronized void removeTest(TestMetric testMetric){
        tests.remove(testMetric);
    }

    /**
     * does test methodname already exist with SUCCESS or FAIL status
     */

    public synchronized boolean doesTestHaveFiniteStatus(String methodName){
        List<TestMetric> testMetricList = new ArrayList<>();
        if (tests!=null)
            testMetricList = tests.stream().filter(t->t.getMethodName().equals(methodName)).collect(Collectors.toList());

        for (TestMetric test : testMetricList){
            if (test.getTestResult().equalsIgnoreCase("SUCCESSFUL") || test.getTestResult().equalsIgnoreCase("FAILED"))
                return true;
        }

        return  false;
    }

    /**
     * Add start of run time to all tests. (format: yyyy-MM-ddTHH:mm:ss)
     * @since 27.04.23
     * @author tzvika.sela
     */
    public void updateRunStartTimeForAllTest(){
        for (TestMetric t:MetricReport.getReport().getTests()){
            t.setRunStartTime(testPlanStartTime);
        }
    }


    /**
     * Add end of run time to all tests (format: yyyy-MM-ddTHH:mm:ss)
     * @since 27.04.23
     * @author tzvika.sela
     */
    public void updateRunEndTimeForAllTest(){
        for (TestMetric t:MetricReport.getReport().getTests()){
            t.setRunEndTime(testPlanEndTime);
        }
    }





}
