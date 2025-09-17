package listeners;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import constantsUtils.CommonConstants;
import dateTimeUtils.DateUtils;
import metricsReport.MetricReport;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;


/**
 *
 * @author tzvika.sela
 * @since 31.01.2023
 * Junit Listener to create the json report for the Elastic once all tests are done
 *
 */
public class ElasticMetricsListener implements TestExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(ElasticMetricsListener.class);
    public static String testPlanStartTime = "Unknown";
    public static String testPlanEndTime = "Unknown";

    /**
     * method to capture the run's start time
     * @param testPlan - the suite test plan
     * @author tzvika.sela
     * @since 31.01.2023
     */
    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
            long startTimeLong = System.currentTimeMillis();
            testPlanStartTime = DateUtils.convertDateToStringInFormatWithTimezone("yyyy-MM-dd###HH:mm:ss",
                    new Date(startTimeLong), "Asia/Jerusalem").replace("###", "T");
    }


    /**
     * method to create the Elastic json report once all the tests are completed
     * @param testPlan - the suite test plan
     * @author tzvika.sela
     * @since 31.01.2023
     */
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        try {
            logger.info("update start of run time for all tests");
            MetricReport.getReport().updateRunStartTimeForAllTest();

            logger.info("update end of run time for all tests");
            long endTimeLong = System.currentTimeMillis();
            testPlanEndTime = DateUtils.convertDateToStringInFormatWithTimezone("yyyy-MM-dd###HH:mm:ss",
                    new Date(endTimeLong), "Asia/Jerusalem").replace("###", "T");
            MetricReport.getReport().updateRunEndTimeForAllTest();

            logger.info("Creating metrics report");
            File file = new File(CommonConstants.EnvironmentParams.METRIC_REPORT_PATH);
            if (file.exists())
                file.delete();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper.writeValue(new FileWriter(CommonConstants.EnvironmentParams.METRIC_REPORT_PATH),MetricReport.getReport());

        }catch(Exception e){
            logger.error("Failed to create tests metrics json report!",e);
        }
    }
}
