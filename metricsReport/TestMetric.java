package metricsReport;


import com.fasterxml.jackson.annotation.JsonProperty;
import enumerations.TestFailureReasonEnum;

import java.util.List;

/**
 * Holds relevant metrics on a test instance/invocation
 * @author sela.zvika
 * @since  02.01.2023
 */
public class TestMetric {
    private int attempt = 1;
    private String testId;
    private String packageName;
    private String className;
    private String classDisplayName;
    private String methodName;
    private String displayName;
    private String testResult;
    private long testDuration = 0;
    private String runStartTime;
    private String runEndTime;
    private String linkToELKDashboard;

    //start time in human-readable format yyyy-MM-dd###HH:mm:ss
    private String testStartTime;
    //end time in human-readable format yyyy-MM-dd###HH:mm:ss
    private String testEndTime;
    private String reportLink;
    private String timestamp;
    private String browser;
    private String environment;
    private String jenkins_build_number;
    private String jenkins_ci = "";
    private String platform;
    private String jenkins_job_name;
    private String mobile_capability;
    private String project;
    private String reportStatus = "unknown";
    private String selenium_grid_version;
    private String testReportException;
    private boolean warningExists = false;
    private String warningMessage = "";

    private List<String> tags;
    private List<String> excludeTags;
    private String projectDisplayName;

    //start time as Unix Epoch timestamp https://www.unixtimestamp.com/
    private long testStartTimeNumber;
    //end time as Unix Epoch timestamp https://www.unixtimestamp.com/
    private long testEndTimeNumber;
    //indicates whether we encountered a log message contains "Query yielded no results"
    private  boolean emptyQueryResults = false;
    private boolean performanceResultsError = false;
    private String logsLink;
    private String downloadsLink;
    private String failureType = TestFailureReasonEnum.UNCLASSIFIED.getName();
    private String failureExplanation;
    private String bugSeverity;
    private String bugDateCreation;
    private String bugGeneralDetails;
    private String bugGeneralDetailsForSomeBugs;
    private String bugSeverityForSomeBugs;
    private String bugDateCreationForSomeBugs;
    private String openBugDays;

    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getExcludeTags() {
        return excludeTags;
    }

    public void setExcludeTags(List<String> excludeTags) {
        this.excludeTags = excludeTags;
    }

    public String getProjectDisplayName() {
        return projectDisplayName;
    }

    public TestMetric setProjectDisplayName(String projectDisplayName) {
        this.projectDisplayName = projectDisplayName != null? projectDisplayName : "";
        return this;
    }

    public String getOpenBugDays() {
        return openBugDays;
    }

    public TestMetric setOpenBugDays(String openBugDays) {
        this.openBugDays = openBugDays;
        return this;
    }

    public String getFailureType() {
        return failureType;
    }

    public TestMetric setFailureType(String failureType) {
        this.failureType = failureType;
        return this;
    }

    public String getFailureExplanation() {
        return failureExplanation;
    }

    public TestMetric setFailureExplanation(String failureExplanation) {
        this.failureExplanation = failureExplanation;
        return this;
    }


    public String getLogsLink() {
        return logsLink;
    }

    public TestMetric setLogsLink(String logsLink) {
        this.logsLink = logsLink;
        return this;
    }

    public String getDownloadsLink() {
        return downloadsLink;
    }

    public TestMetric setDownloadsLink(String downloadsLink) {
        this.downloadsLink = downloadsLink;
        return this;
    }

    public long getTestStartTimeNumber() {
        return testStartTimeNumber;
    }

    public TestMetric setTestStartTimeNumber(long testStartTimeNumber) {
        this.testStartTimeNumber = testStartTimeNumber;
        return this;
    }

    public long getTestEndTimeNumber() {
        return testEndTimeNumber;
    }

    public TestMetric setTestEndTimeNumber(long testEndTimeNumber) {
        this.testEndTimeNumber = testEndTimeNumber;
        return this;
    }

    public boolean isWarningExists() {
        return warningExists;
    }

    public TestMetric setWarningExists(boolean warningExists) {
        this.warningExists = warningExists;
        return this;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public TestMetric setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
        return this;
    }


    public boolean isEmptyQueryResults() {
        return emptyQueryResults;
    }

    public void setEmptyQueryResults(boolean emptyQueryResults) {

        this.emptyQueryResults = emptyQueryResults;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public TestMetric setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus != null? reportStatus.toUpperCase() : "";
        return this;
    }

    public TestMetric setTimestamp(String timestamp) {
        this.timestamp = timestamp != null? timestamp : "";
        return this;
    }

    public String getBrowser() {
        return browser;
    }

    public TestMetric setBrowser(String browser) {
        this.browser = browser != null? browser : "";
        return this;
    }

    public String getEnvironment() {
        return environment;
    }

    public TestMetric setEnvironment(String environment) {
        this.environment = environment != null? environment : "";
        return this;
    }

    public String getJenkins_build_number() {
        return jenkins_build_number;
    }

    public TestMetric setJenkins_build_number(String jenkins_build_number) {
        this.jenkins_build_number = jenkins_build_number != null? jenkins_build_number : "";
        return this;
    }

    public String getJenkins_ci() {
        return jenkins_ci;
    }

    public TestMetric setJenkins_ci(String jenkins_ci) {
        this.jenkins_ci = jenkins_ci != null? jenkins_ci : "";
        return this;
    }

    public String getPlatform() {
        return platform;
    }

    public TestMetric setPlatform(String platform) {
        this.platform = platform != null? platform : "";
        return this;
    }

    public String getJenkins_job_name() {
        return jenkins_job_name;
    }

    public TestMetric setJenkins_job_name(String jenkins_job_name) {
        this.jenkins_job_name = jenkins_job_name != null? jenkins_job_name : "";
        return this;
    }

    public String getMobile_capability() {
        return mobile_capability;
    }

    public TestMetric setMobile_capability(String mobile_capability) {
        this.mobile_capability = mobile_capability != null? mobile_capability : "";
        return this;
    }

    public String getProject() {
        return project;
    }

    public TestMetric setProject(String project) {
        this.project = project != null? project : "";
        return this;
    }

    public String getSelenium_grid_version() {
        return selenium_grid_version;
    }

    public TestMetric setSelenium_grid_version(String selenium_grid_version) {
        this.selenium_grid_version = selenium_grid_version != null? selenium_grid_version : "";
        return this;
    }

    public String getReportLink() {
        return reportLink;
    }

    public TestMetric setReportLink(String reportLink) {
        this.reportLink = reportLink;
        return this;
    }
    public TestMetric setTestEndTime(String testEndTime) {
        this.testEndTime = testEndTime;
        return this;
    }

    public String getTestEndTime() {
        return testEndTime;
    }

    public TestMetric setTestStartTime(String testStartTime) {
        this.testStartTime = testStartTime;
        return this;
    }

    public String getTestStartTime() {
        return testStartTime;
    }

    public String getClassDisplayName() {
        return classDisplayName;
    }

    public TestMetric setClassDisplayName(String classDisplayName) {
        this.classDisplayName = classDisplayName;
        return this;
    }

    public String getTestId() {
        return testId;
    }

    public TestMetric setTestId(String testId) {
        this.testId = testId;
        return this;
    }

    public int getAttempt() {
        return attempt;
    }

    public TestMetric setAttempt(int attempt) {
        this.attempt = attempt;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public TestMetric setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public TestMetric setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public TestMetric setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TestMetric setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public TestMetric setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }

    public long getTestDuration() {
        return testDuration;
    }

    public TestMetric setTestDuration(long testDuration) {
        this.testDuration = testDuration;
        return this;
    }


    public String getTestReportException() {
        return testReportException;
    }

    public TestMetric setTestReportException(String testReportException) {
        this.testReportException = testReportException!= null? testReportException : "";
        return this;
    }

    public String getRunStartTime() {
        return runStartTime;
    }

    public TestMetric setRunStartTime(String runStartTime) {
        this.runStartTime = runStartTime;
        return this;
    }

    public String getRunEndTime() {
        return runEndTime;
    }

    public TestMetric setRunEndTime(String runEndTime) {
        this.runEndTime = runEndTime;
        return this;
    }

    public String getLinkToELKDashboard() {
        return linkToELKDashboard;
    }

    public TestMetric setLinkToELKDashboard(String linkToELKDashboard) {
        this.linkToELKDashboard = linkToELKDashboard;
        return this;
    }

    public void setPerformanceFailure(boolean performanceFailure) {
        this.performanceResultsError = performanceFailure;
    }

    public boolean getPerformanceFailure(){
        return performanceResultsError;
    }

    public String getBugDateCreation() {
        return bugDateCreation;
    }

    public TestMetric setBugDateCreation(String bugDateCreation) {
        this.bugDateCreation = bugDateCreation != null? bugDateCreation : "";
        return this;
    }

    public String getBugSeverity() {
        return bugSeverity;
    }

    public TestMetric setBugSeverity(String bugSeverity) {
        this.bugSeverity = bugSeverity != null? bugSeverity : "";
        return this;
    }

    public String getBugGeneralDetails() {
        return bugGeneralDetails;
    }

    public TestMetric setBugGeneralDetails(String bugGeneralDetails) {
        this.bugGeneralDetails = bugGeneralDetails != null? bugGeneralDetails : "";
        return this;
    }

    public String getBugGeneralDetailsForSomeBugs() {
        return bugGeneralDetailsForSomeBugs;
    }

    public TestMetric setBugGeneralDetailsForSomeBugs(String bugGeneralDetailsForSomeBugs) {
        this.bugGeneralDetailsForSomeBugs = bugGeneralDetailsForSomeBugs != null? bugGeneralDetailsForSomeBugs : "";
        return this;
    }

    public String getBugSeverityForSomeBugs() {
        return bugSeverityForSomeBugs;
    }

    public TestMetric setBugSeverityForSomeBugs(String bugSeverityForSomeBugs) {
        this.bugSeverityForSomeBugs = bugSeverityForSomeBugs != null? bugSeverityForSomeBugs : "";
        return this;
    }

    public String getBugDateCreationForSomeBugs() {
        return bugDateCreationForSomeBugs;
    }

    public TestMetric setBugDateCreationForSomeBugs(String bugDateCreationForSomeBugs) {
        this.bugDateCreationForSomeBugs = bugDateCreationForSomeBugs != null? bugDateCreationForSomeBugs : "";
        return this;
    }

}
