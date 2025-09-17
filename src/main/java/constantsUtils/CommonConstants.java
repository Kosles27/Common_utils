package constantsUtils;

import java.io.File;

public class CommonConstants
{
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String BREAK_ROW = "<br>";
    public static final String DOUBLE_SLASH = File.separator;
    public static final String LOG_DATE_FORMAT = "MMMM dd, yyyy HH:mm";
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static class EnvironmentParams {
        public static String EXTENT_REPORT_FOLDER = System.getProperty("user.dir") + File.separator + "report" + File.separator;
        public final static String METRIC_REPORT_PATH = System.getProperty("user.dir") + File.separator + "report" + File.separator
                + "metricsReport" + FileSuffix.JSON;
        public static String DOWNLOADS_FOLDER = System.getProperty("user.dir") + File.separator + "Downloads"+ File.separator;

        public static String RESOURCES_FOLDER_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        public static String DRIVER_LOG_FOLDER = System.getProperty("user.dir") + File.separator + "target"+ File.separator;
    }

    /*
    Date patterns - https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
     */
    public static class DatePatterns {
        public static String DDMMYYYY_HHMMSS = "ddMMyyyy_HHmmss";
    }

    public static class FileSuffix {
        public final static String JSON = ".json";
    }

    public static class FailureClassificationErrors {
        public final static String MISSING_DATA = "Query yielded no results!";
        public final static String PERFORMANCE_BASELINE_EXCEEDED = "Performance test did not meet expected baseline/s";
    }

}
