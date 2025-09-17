package dbUtils;

import Managers.ReportInstanceManager;
import Store.StoreManager;
import Store.StoreType;
import com.google.common.base.Stopwatch;
import com.relevantcodes.extentreports.ExtentTest;
import constantsUtils.CommonConstants;
import enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static reportUtils.Report.reportAndLog;

@SuppressWarnings({"unused", "unchecked"})
public class DbBase {

    private final static Logger logger = LoggerFactory.getLogger(DbBase.class);

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param resultSet the result set
     * @return a Hash map containing the query result. i.e column names and their values
     * @author Dafna Genosar
     * @since 09.10.2022
     */
    protected <K, V, T extends Map<K, V>, L extends List<T>> L getQueryStringResultMap(ResultSet resultSet) throws SQLException {

        LinkedList<LinkedHashMap<String, String>> queryResultsList = new LinkedList<>();
        ResultSetMetaData resultMetaData = resultSet.getMetaData();

        int queryColumnCount = resultMetaData.getColumnCount();

        while (resultSet.next()) {

            LinkedHashMap<String, String> rowResults = new LinkedHashMap<>();

            for (int i = 1; i <= queryColumnCount; i++) {
                String columnName = resultMetaData.getColumnName(i);

                //If key already exists, create a new key with appendix "1".
                while (rowResults.containsKey(columnName))
                {
                    columnName +="1";
                }
                rowResults.put(columnName, resultSet.getString(i));
            }
            queryResultsList.add(rowResults);
        }
        if(queryResultsList.isEmpty())
            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);

        return (L)queryResultsList;
    }

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param resultSet the result set
     * @return a Hash map containing the query result. i.e column names and their values
     *
     * @author Dafna Genosar
     * @since 09.10.2022
     * @author Dafna Genosar
     * @since 16.11.2022
     */
    protected <K, V, T extends Map<K, V>, L extends List<T>> L getQueryStringResultLinkedMap(ResultSet resultSet) throws SQLException {

        LinkedList<LinkedHashMap<String, String>> queryResultsList = new LinkedList<>();

        ResultSetMetaData resultMetaData = resultSet.getMetaData();

        int queryColumnCount = resultMetaData.getColumnCount();

        while (resultSet.next()) {

            LinkedHashMap<String, String> rowResults = new LinkedHashMap<>();

            for (int i = 1; i <= queryColumnCount; i++) {
                String columnName = resultMetaData.getColumnName(i);

                //If key already exists, create a new key with appendix "1".
                while (rowResults.containsKey(columnName))
                {
                    columnName +="1";
                }
                rowResults.put(columnName, resultSet.getString(i));
            }
            queryResultsList.add(rowResults);
        }
        if(queryResultsList.isEmpty())
            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);

        return (L)queryResultsList;
    }

    /**
     * Execute the query and report the execution time
     * @param statement Statement object
     * @param query the query
     * @return the ResultSet
     * @throws SQLException sql exception
     * @author genosar.dafna
     * @since 06.03.2023
     * @since 04.06.2024
     */
    protected ResultSet executeQuery(Statement statement, String query, boolean reportQuery, boolean reportExecutionTime) throws SQLException {

        ConcurrentHashMap<String, String> queryPerformance = StoreManager.getStore(StoreType.GLOBAL).getValueFromStore("AllQueriesTimes");

        if (queryPerformance == null) {
            StoreManager.getStore(StoreType.GLOBAL).putValueInStore("AllQueriesTimes", new ConcurrentHashMap<String, String>());
            queryPerformance = StoreManager.getStore(StoreType.GLOBAL).getValueFromStore("AllQueriesTimes");
        }

        if(reportQuery){
            reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);
            logger.info("Executing Query: " + query);}

        //Create a stop watch and start the timer
        Stopwatch stopwatch = Stopwatch.createStarted();

        ResultSet resultSet = statement.executeQuery(query);

        //Stop the watch
        stopwatch.stop();

        //Get the time that passed in millisecs
        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        //Get the time that passed in seconds
        double calcSeconds = millis * 0.001;
        String roundedSecString = String.format("%.2f", calcSeconds);

        queryPerformance.put(query, roundedSecString);

        ExtentTest currentExtentTest = ReportInstanceManager.getCurrentTestReport();
        if(currentExtentTest != null) {
            if (reportExecutionTime){
                Report.reportAndLog(String.format("Query execution time: %s", roundedSecString), MessageLevel.INFO);
                logger.info(String.format("Query execution time: %s", roundedSecString));
        }
        }

        return resultSet;
    }
}
