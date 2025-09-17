package extensions;


import Managers.ExtentReportInstanceManager;
import Managers.ReportInstanceManager;
import Store.StoreManager;
import Store.StoreType;
import com.relevantcodes.extentreports.model.Log;
import constantsUtils.CommonConstants;
import customAnnotations.Performance;
import dateTimeUtils.DateTime;
import dateTimeUtils.DateUtils;
import enumerations.TestFailureReasonEnum;
import io.github.artsok.RepeatedIfExceptionsTest;
import metricsReport.MetricReport;
import metricsReport.TestMetric;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.relevantcodes.extentreports.LogStatus.WARNING;
import static constantsUtils.CommonConstants.EMPTY_STRING;
import static enumerations.TestMetricsEnum.*;
import static propertyUtils.PropertyUtils.getGlobalProperty;


/**
 * Extension to gather test statistics for the Elastic Report.
 * @author sela.zvika
 * @since 09.05.2021
 */
public class ElasticMetricsReportExtension implements TestWatcher,AfterTestExecutionCallback, BeforeEachCallback, AfterEachCallback {
    private static final Logger logger= LoggerFactory.getLogger(ElasticMetricsReportExtension.class);
    private final HashMap<String,Integer> testRepeatCounter = new HashMap<>();

    /**
     * Enum representing a junit test result
     * @author sela.zvika
     * @since 09.05.2021
     */
    private enum TestResultStatus {
        //Invoked after a test has finished with no Throwables
        SUCCESSFUL,
        //Invoked after a test has been aborted or a Throwable with repeats remaining happened
        SKIPPED,
        //Invoked after a test has finished with Throwables
        FAILED,
        //Invoked after a test has been marked as @Disabled
        DISABLED
    }

    /**
     * Once a test is disabled mark it as such
     * @author sela.zvika
     * @since 09.05.2021
     */
    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        MetricReport metricReport = MetricReport.getReport();

        try {
            //due to repeat bug if we are repeating the test, although we got a finite status we do not add this test to the report
            if (repeatTagExists(context) ) {
                if (metricReport.doesTestHaveFiniteStatus(context.getRequiredTestMethod().getName())) {
                    return;
                }
            }
            TestMetric testMetric = populateMetrics(context);
            testMetric.setTestResult(TestResultStatus.DISABLED.toString());
            metricReport.addTest(testMetric);

        } catch (Exception e) {
            logger.info("Failed to add Disabled Test to ELK report");
        }
    }

    /**
     * mark the test as successful
     * @author sela.zvika
     * @since 09.05.2021
     */
    @Override
    public void testSuccessful(ExtensionContext context) {
        MetricReport metricReport = MetricReport.getReport();
        TestMetric testMetric = metricReport.getTest(context.getUniqueId());
        testMetric.setTestResult(TestResultStatus.SUCCESSFUL.toString());
    }

    /**
     * mark the test as skipped
     * For repeated tests only regard if the last one was skipped
     * @author sela.zvika
     * @since 09.05.2021
     */
    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        MetricReport metricReport = MetricReport.getReport();
        TestMetric testMetric = metricReport.getTest(context.getUniqueId());
        testMetric.setTestResult(TestResultStatus.SKIPPED.toString());
        if (repeatTagExists(context) ){
            //if repeated test and not last attempt ignore test
            int currAttempts = testRepeatCounter.get(context.getRequiredTestMethod().getName());
            RepeatedIfExceptionsTest n = context.getTestMethod().get().getAnnotation(RepeatedIfExceptionsTest.class);
            if ((currAttempts-1)==n.repeats()) {
                return;
            }
            metricReport.removeTest(testMetric);

        }



    }

    /**
     * mark the test as failed
     * @author sela.zvika
     * @since 09.05.2022
     */
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {

        MetricReport metricReport = MetricReport.getReport();
        // if test metric doesn't exist in this point it mean we failed in beforeTest
        TestMetric testMetric = metricReport.getTest(context.getUniqueId());
        if (testMetric == null){
            try {
                testMetric = populateMetrics(context);
                metricReport.addTest(testMetric);
            } catch (Exception e) {
                logger.info("Failed to add Disabled Test to ELK report");
            }
        }
        testMetric.setTestResult(TestResultStatus.FAILED.toString());
        setFailureCategorization(testMetric);
    }

    /**
     * add failure classification type and failure explanation fields to test.
     * @author sela.zvika
     * @since 25.07.2023
     * @param testMetric our potential ELK test entity.
     */
    private void setFailureCategorization(TestMetric testMetric) {
        try {
            //set failure type
            if (testMetric.getTestResult().equalsIgnoreCase(TestResultStatus.FAILED.toString())){
                if (testMetric.isWarningExists()) {
                    testMetric.setFailureType(TestFailureReasonEnum.PRODUCT_BUG.getName());
                } else if (testMetric.getPerformanceFailure()) {
                    testMetric.setFailureType(TestFailureReasonEnum.PERFORMANCE.getName());
                } else if (testMetric.isEmptyQueryResults()) {
                    testMetric.setFailureType(TestFailureReasonEnum.MISSING_DATA.getName());
                }
            }

        }catch(Throwable t){
            logger.info("Failed to set failure type");
        }
    }


    /**
     * Start timing the test
     * in case of repeated test also increase the attempts counter
     *
     * @author - Tzvika Sela
     * @since - 31.01.2023
     */
    @Override
    public void beforeEach(ExtensionContext extensionContext)  {
        //test start time
        StoreManager.getStore(StoreType.LOCAL_THREAD).putValueInStore(TEST_START_TIME,System.currentTimeMillis());

        //if repeated test then increment by 1 num of attempts
        if (repeatTagExists(extensionContext) && testRepeatCounter.get(extensionContext.getRequiredTestMethod().getName()) != null) {
            int currAttempts = testRepeatCounter.get(extensionContext.getRequiredTestMethod().getName()) + 1;
            testRepeatCounter.put(extensionContext.getRequiredTestMethod().getName(), currAttempts);
            return;

        }

        testRepeatCounter.put(extensionContext.getRequiredTestMethod().getName(), 1);
    }

    /**
     * register test's end time and report status
     *
     * @author - Tzvika Sela
     * @since - 31.01.2023
     */
    @Override
    public void afterTestExecution(ExtensionContext context) {
        StoreManager.getStore(StoreType.LOCAL_THREAD).putValueInStore(TEST_END_TIME,System.currentTimeMillis());
    }


    /**
     * Save all the metrics and add to report
     *
     * @author - Tzvika Sela
     * @since - 31.01.2023
     */
    @Override
    public void afterEach(ExtensionContext extensionContext) throws URISyntaxException, MalformedURLException {
        TestMetric testMetric = populateMetrics(extensionContext);
        //add to report singleton
        MetricReport metricReport = MetricReport.getReport();
        metricReport.addTest(testMetric);

    }

    /**
     * Save all the metrics to the TestMetric object
     *
     * @param extensionContext test's extension context
     * @return TestMetric object after updating the relevant report data
     * @author - Tzvika Sela
     * @since - 31.01.2023
     */
    private TestMetric populateMetrics(ExtensionContext extensionContext) throws URISyntaxException, MalformedURLException {
        //TBD: break each part here to a different method
        TestMetric testMetric = new TestMetric();
        testMetric.setTestId(extensionContext.getUniqueId());
        String packageName = extensionContext.getRequiredTestClass().getPackageName();
        testMetric.setPackageName(packageName.substring(packageName.lastIndexOf(".")+1));
        testMetric.setMethodName(extensionContext.getRequiredTestMethod().getName());
        testMetric.setClassName(extensionContext.getRequiredTestClass().getName());
        //set test display
        testMetric.setDisplayName(extensionContext.getDisplayName());
        //set class display
        DisplayName dn = extensionContext.getRequiredTestClass().getAnnotation(DisplayName.class);
        testMetric.setClassDisplayName(dn.value());

        //if the test is Disabled or failed in beforeTest we don't have this data
        if (StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_END_TIME) != null) {
            //duration in millisec
            long lDuration = (long) StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_END_TIME) - (long) StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_START_TIME);
            //duration in seconds
            testMetric.setTestDuration(lDuration/1000);

            //test start and end time currently not in use
            String startTime = DateUtils.convertDateToStringInFormatWithTimezone("yyyy-MM-dd###HH:mm:ss",
                    new Date((long) StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_START_TIME)), "Asia/Jerusalem").replace("###", "T");
            String endTime = DateUtils.convertDateToStringInFormatWithTimezone("yyyy-MM-dd###HH:mm:ss",
                    new Date((long) StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_END_TIME)), "Asia/Jerusalem").replace("###", "T");
            //start time in human-readable format yyyy-MM-dd###HH:mm:ss
            testMetric.setTestStartTime(startTime);
            //start time as Unix Epoch timestamp https://www.unixtimestamp.com/
            testMetric.setTestStartTimeNumber(StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_START_TIME));
            //end time in human-readable format yyyy-MM-dd###HH:mm:ss
            testMetric.setTestEndTime(endTime);
            //end time as Unix Epoch timestamp https://www.unixtimestamp.com/
            testMetric.setTestEndTimeNumber(StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_END_TIME));
            //attempts
            if (testRepeatCounter != null && testRepeatCounter.get(extensionContext.getRequiredTestMethod().getName()) != null)
                testMetric.setAttempt(testRepeatCounter.get(extensionContext.getRequiredTestMethod().getName()));
        }

        //add extent report link to our report
        String reportLinkScheme = getGlobalProperty("maestro_reports_scheme");
        String reportLinkHost = getGlobalProperty("maestro_reports_host");

        URI uri = new URI(
                reportLinkScheme,
                reportLinkHost + System.getProperty("environment") +"/seperated_reports",
                "/"+ExtentReportInstanceManager.getCurrentExtentReportName(dn.value()),
                null);
        String reportLink = uri.toURL().toString();
        testMetric.setReportLink(reportLink);



        LocalDateTime localDateTime = DateTime.getCurrentUtcDateTime().getLocalDateTimeObject();
        String tz = new DateTime(localDateTime).getTimeZone().toZoneId().getRules().getOffset(localDateTime).getId();
        testMetric.setTimestamp(localDateTime+tz);
        testMetric.setBrowser(System.getProperty("browser"));
        testMetric.setEnvironment(System.getProperty("environment"));
        testMetric.setJenkins_build_number(System.getProperty("BuildID"));

//TODO Remove Comment After The Indexes Are Mapped

//        testMetric.setTags(Arrays.asList(System.getProperty("groups")));
//        testMetric.setExcludeTags(Arrays.asList(System.getProperty("excludeGroups")));
        testMetric.setProjectDisplayName(System.getProperty("devops.displayName"));

        testMetric.setPlatform(System.getProperty("platform"));
        testMetric.setJenkins_job_name(System.getProperty("jobName"));
        testMetric.setMobile_capability(System.getProperty("caps.name"));

        testMetric.setSelenium_grid_version(System.getProperty("seleniumGridVersion"));




        //get the project name from path, for example: D:\Workspace\customer_area_new
        String projectPath = System.getProperty("user.dir");
        File file = new File(projectPath);
        String projectName = file.getName();
        testMetric.setProject(projectName);
        testMetric.setReportStatus(StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(REPORT_STATUS));
        testMetric.setTestReportException(StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore(TEST_REPORT_EXCEPTION));
        //set download and logs links
        String downloadLink = reportLinkScheme + "://" + reportLinkHost + testMetric.getProject() + "/" +
                System.getProperty("environment") + "/DOWNLOADS/" +
                testMetric.getProject() + "/" + testMetric.getJenkins_build_number() + "/downloads/";

        String logsLink = reportLinkScheme + "://" +reportLinkHost + System.getProperty("environment") + "/LOGS/"
                + testMetric.getProject() + "/" + testMetric.getJenkins_build_number()+"/";

        testMetric.setDownloadsLink(downloadLink);
        testMetric.setLogsLink(logsLink);

        //classify tests from report messages
        if (ReportInstanceManager.getCurrentTestReport()!=null){
            boolean warningMessageExists = ReportInstanceManager.getCurrentTestReport().getTest().getLogList().stream().anyMatch(t->t.getLogStatus() == WARNING);
            List<Log> warningMessage = new ArrayList<>();
            if (warningMessageExists)
                warningMessage = ReportInstanceManager.getCurrentTestReport().getTest().getLogList().stream().filter(t->t.getLogStatus() == WARNING).toList();


            //check the report for empty data from query message
            boolean emptyDataQuery = ReportInstanceManager.getCurrentTestReport().getTest().getLogList().stream().anyMatch(t->t.getDetails().contains(CommonConstants.FailureClassificationErrors.MISSING_DATA));
            if (!warningMessageExists)
                testMetric.setEmptyQueryResults(emptyDataQuery);
            //check the report for performance failure
            Annotation performanceAnnotation = extensionContext.getRequiredTestClass().getAnnotation(Performance.class);
            if (performanceAnnotation != null){
                boolean performanceFailure = ReportInstanceManager.getCurrentTestReport().getTest().getLogList().stream().anyMatch(t -> t.getDetails().contains(CommonConstants.FailureClassificationErrors.PERFORMANCE_BASELINE_EXCEEDED));
                if (!warningMessageExists)
                    testMetric.setPerformanceFailure(performanceFailure);
            }



            testMetric.setWarningExists(warningMessageExists);
            //set to warning message bug details
            if (!warningMessage.isEmpty() && warningMessage.size()==1) {
                testMetric.setWarningMessage(warningMessage.get(0).getDetails());
                //set specific bug parameters
                setSpecificBugParameters(testMetric, warningMessage.get(0).getDetails());
                //set empty strings because we have only one bug in the test
                testMetric.setBugGeneralDetailsForSomeBugs(EMPTY_STRING);
                testMetric.setBugSeverityForSomeBugs(EMPTY_STRING);
                testMetric.setBugDateCreationForSomeBugs(EMPTY_STRING);
            }

            //set to warning message bugs amount, because we have more than one bug in the test
            else if (!warningMessage.isEmpty()) {
                //set critical bug parameters or first bug parameters, if we haven't critical
                for (int i=0; i<warningMessage.size(); i++) {
                    String warningDetails = warningMessage.get(i).getDetails();
                    //set specific bug parameters about critical bug
                    if (warningDetails.contains("Severity: Critical") || i == 0)
                        setSpecificBugParameters(testMetric, warningDetails);
                }
                //set specific bugs parameters for fail with some bugs
                setSpecificBugParametersForFailWithSomeBugs(testMetric, warningMessage);
                //set additional values for elk report for fail with some bugs
                String warningMessageStr = generateFinalWarningMessage(warningMessage);
                testMetric.setWarningMessage(warningMessageStr);
            }

            //set empty strings because we haven't bug/s
            else {
                testMetric.setWarningMessage(EMPTY_STRING);
                testMetric.setBugGeneralDetails(EMPTY_STRING);
                testMetric.setBugSeverity(EMPTY_STRING);
                testMetric.setBugDateCreation(EMPTY_STRING);
                testMetric.setBugGeneralDetailsForSomeBugs(EMPTY_STRING);
                testMetric.setBugSeverityForSomeBugs(EMPTY_STRING);
                testMetric.setBugDateCreationForSomeBugs(EMPTY_STRING);
                testMetric.setOpenBugDays(EMPTY_STRING);
            }
        }


        //add dashboard link with filters
        String linkToDashboard = String.format("https://kibanaqa-zim365.msappproxy.net/s/qait/app/dashboards#/view/665d3c02-e022-56f9-92e5-d192454d5e67?_g=(filters:!(('$state':(store:globalState),meta:(alias:!n,disabled:!f,key:jenkins_build_number,negate:!f,params:(query:%s),type:phrase),query:(match_phrase:(jenkins_build_number:%s))),('$state':(store:globalState),meta:(alias:!n,disabled:!f,key:environment,negate:!f,params:(query:%s),type:phrase),query:(match_phrase:(environment:%s))),('$state':(store:globalState),meta:(alias:!n,disabled:!f,key:project,negate:!f,params:(query:%s),type:phrase),query:(match_phrase:(project:%s))))",
                testMetric.getJenkins_build_number(),testMetric.getJenkins_build_number(), testMetric.getEnvironment(),testMetric.getEnvironment(),testMetric.getProject(),testMetric.getProject())+",refreshInterval:(pause:!t,value:0),time:(from:now-7d%2Fd,to:now))";

        testMetric.setLinkToELKDashboard(linkToDashboard);


        return testMetric;
    }

    /**
     * checks whether the test is tagged with annotation - @RepeatedIfExceptionsTest
     * @param extensionContext - the extensionContext object
     * @return true if tag RepeatedIfExceptionsTest exists, false otherwise
     */
    private boolean repeatTagExists(ExtensionContext extensionContext) {
        if (extensionContext.getTestMethod().isPresent()) {
            RepeatedIfExceptionsTest n = extensionContext.getTestMethod().get().getAnnotation(RepeatedIfExceptionsTest.class);
            return n != null;
        }

        return false;
    }

    /** create final warning message with bug/s number
     *
     * @param warningMessages log list of test warning message/s
     * @return final string to print in dashboard
     */
    private String generateFinalWarningMessage(List<Log> warningMessages) {
        List<String > bugNumbers = new ArrayList<>();
        //save project name
        String projectPath = System.getProperty("user.dir");
        File file = new File(projectPath);
        String projectName = file.getName();

        //save bug number/s in the list
        for (Log warningMessage : warningMessages) {
            switch (projectName) {
                case "scheduling":
                    bugNumbers.add(warningMessage.getDetails().trim().substring(24, 28));
                    break;
                case "l2a_uiux_new", "l2a_uiux_performance", "customer_area_new", "apim":
                    bugNumbers.add(warningMessage.getDetails().trim().substring(0, 8));
                    break;
                case "crm_dynamics":
                    bugNumbers.add(warningMessage.getDetails().trim().substring(4, 8));
                    break;
                case "allocation":
                    if (warningMessage.getDetails().trim().contains("RQT")) {
                        List<String> temp = List.of(warningMessage.getDetails().trim().split("RQT"));
                        for (int i=1; i<temp.size(); i++)
                            bugNumbers.add("RQT" + temp.get(i).substring(0, 5));
                    } else
                        bugNumbers.add(warningMessage.getDetails().trim().substring(11, 15));
                    break;
                case "balancing":
                    bugNumbers.add(warningMessage.getDetails().trim().split(">")[1].substring(0, 8));
                    break;
                case "hcm":
                    bugNumbers.add(warningMessage.getDetails().trim().substring(6, 11));
                    break;
                case "route_catalog", "iqship_db-proactive_monitor", "helloworld", "data_preparation":
                    bugNumbers.add(warningMessage.getDetails().trim());
                    break;
            }
        }

        //create final warning message
        String warningMessageStr = Arrays.stream(bugNumbers.toString().trim().substring(1, bugNumbers.toString().trim().length()-1).split(","))
                .map(bugNumber -> "Bug: " + bugNumber.trim() + ". ")
                .reduce("", String::concat) + "Total: " + warningMessages.size() + " bug/s.";

        return warningMessageStr;
    }

    /**
     * Set specific bug parameters
     *
     * @param testMetric testMetric instance
     * @param warningDetails warning message details
     * @author reed.dakota
     * @since 18.01.2024
     */
    public void setSpecificBugParameters(TestMetric testMetric, String warningDetails) {
        List<String> specificBugParameters;
        String bugGeneralDetails = EMPTY_STRING;
        String bugSeverity = EMPTY_STRING;
        String bugDateCreation = EMPTY_STRING;
        String openBugDays = EMPTY_STRING;

        if (warningDetails.contains("Severity: ")) {
            specificBugParameters = List.of(warningDetails.split("\\|"));
            bugGeneralDetails = specificBugParameters.get(0).trim().substring(6);
            bugSeverity = List.of(specificBugParameters.get(1).split(":")).get(1).trim();
            if (bugSeverity.contains("Critical"))
                bugSeverity = List.of(bugSeverity.split(" ")).get(1);
        }

        if (warningDetails.contains("Date of Bug Creation: ")) {
            specificBugParameters = List.of(warningDetails.split("\\|"));
            bugDateCreation  = List.of(List.of(specificBugParameters.get(2).split(":")).get(1).trim().split("<")).get(0);
            // Parse the date string into a LocalDate object
            LocalDate dateBugCreation = LocalDate.parse(bugDateCreation, DateTimeFormatter.ofPattern("d/M/yyyy"));
            // Calculate the number of days between today and the bug creation date
            openBugDays = String.valueOf(ChronoUnit.DAYS.between(dateBugCreation, LocalDate.now()));
        }

        //set values for elk report
        testMetric.setBugGeneralDetails(bugGeneralDetails);
        testMetric.setBugSeverity(bugSeverity);
        testMetric.setBugDateCreation(bugDateCreation);
        testMetric.setOpenBugDays(openBugDays);
    }

    /**
     * Set specific bug parameters for fail with some bugs
     *
     * @param testMetric testMetric instance
     * @param warningMessages warning messages list
     * @author reed.dakota
     * @since 19.01.2024
     */
    public void setSpecificBugParametersForFailWithSomeBugs(TestMetric testMetric, List<Log> warningMessages) {
        List<String> specificBugParameters;
        String bugSeverityForSomeBugs = EMPTY_STRING;
        String bugDateCreationForSomeBugs = EMPTY_STRING;
        String warningDetails;

        for (int i=0; i<warningMessages.size(); i++) {
            warningDetails = warningMessages.get(i).getDetails();

            //set specific bug parameters about all bugs
            if (warningDetails.contains("Severity: ")) {
                specificBugParameters = List.of(warningDetails.split("\\|"));
                bugSeverityForSomeBugs += List.of(specificBugParameters.get(1).split(":")).get(1).trim() + " + " ;
                //delete last ' + ' from messages
                bugSeverityForSomeBugs = bugSeverityForSomeBugs.substring(0, (bugSeverityForSomeBugs.length() - 3));
            }
            if (warningDetails.contains("Date of Bug Creation: ")) {
                specificBugParameters = List.of(warningDetails.split("\\|"));
                bugDateCreationForSomeBugs  += List.of(List.of(specificBugParameters.get(2).split(":")).get(1).trim().split("<")).get(0) + " + " ;
                //delete last ' + ' from messages
                bugDateCreationForSomeBugs = bugDateCreationForSomeBugs.substring(0, (bugDateCreationForSomeBugs.length() - 3));
            }
        }


        //set values for elk report
        testMetric.setBugGeneralDetailsForSomeBugs(generateFinalWarningMessage(warningMessages));
        testMetric.setBugSeverityForSomeBugs(bugSeverityForSomeBugs);
        testMetric.setBugDateCreationForSomeBugs(bugDateCreationForSomeBugs);
    }

}

