package dbUtils;

import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import constantsUtils.CommonConstants;
import enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static propertyUtils.PropertyUtils.getGlobalPropertyEntity;
import static reportUtils.Report.reportAndLog;

/**
 * this class will handle SQLite DB Queries.
 * @author tzvika.sela
 * @since 02.06.2023
 *
 */
@SuppressWarnings("unused")
public class SqliteDatabaseUtil  {
    private static final Logger logger = LoggerFactory.getLogger(SqliteDatabaseUtil.class);
    public int queryTimeout = getGlobalPropertyEntity().getIntProperty("query_timeout");

    /**
     * This method sets the query timeout
     * @author zvika.sela
     * @since 02.06.2023
     * @param queryTimeout the amount of timeout in sec
     *
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    /**
     * This method returns an SQLite connection which has no user/pass
     *
     * @author zvika.sela
     * @since 02.06.2023
     * @param dbConnString the server address
     * @return a DB Connection
     */

    public Connection connect(String dbConnString) throws ClassNotFoundException, SQLException {
        reportAndLog("Connecting to:" + dbConnString, MessageLevel.INFO);
        return DriverManager.getConnection(dbConnString);
    }

    /**
     * This method returns a single value from the query, please note that the query will return the upper-left
     * value even if more than 1 value exists in table
     *
     * @param query  the given query we wish to execute such as 'select user_name from table'
     * @param dbConnString the server address
     * @return a String representing the username
     */
    public String getSingleValueFromFirstRowAndColumn(String query, String dbConnString){
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {
            reportAndLog("Connecting to:" + dbConnString, MessageLevel.INFO);
            reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);

            connection = connect(dbConnString);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);
            resultSet = statement.executeQuery(query);
                if (!resultSet.next()) {
                    reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
                }
                    else{
                        String value = resultSet.getString(1);
                        reportAndLog("Value from query: " + value, MessageLevel.INFO);
                        return value;
                    }


        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return null;
    }



    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a single column count queries
     * Please note that each data in the list is already trimmed
     *
     * @param query the given query we wish to execute
     * @param dbConnString the server address
     * @since 02.06.2023
     * @author sela.tzvika
     * @return a List of database row
     */
    public List<String> getResultsFromQuery(String query, String dbConnString) {
        List<String> resultsList = new ArrayList<>();
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {

            reportAndLog("<b>Executing Query: </b><br>" + query , MessageLevel.INFO);

            connection = connect(dbConnString);
            statement = connection.createStatement();
                statement.setQueryTimeout(queryTimeout);
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String value = resultSet.getString(1);
                    if(value == null)
                        resultsList.add(value);
                    else
                        resultsList.add(value.trim());
                }
                if(resultsList.isEmpty())
                    reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);
                else
                    if(ReportInstanceManager.getCurrentTestReport() != null)
                        ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Query results: <br>" + Arrays.toString(resultsList.toArray()));
                    logger.debug("Query results: \n" + Arrays.toString(resultsList.toArray()));

        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return resultsList;
    }


    /**
     * Returns a List of Hash maps containing the query result
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @return a Hash map containing the query result. i.e column names and their values
     *
     * @author Sela Tzvika
     * @since 02.06.2023
     */
    public List<HashMap<String, String>> getQueryResultMap(String query, String dbConnString) {

        List<HashMap<String, String>> queryResultsList = new ArrayList<>();
        Connection connection=null;
        Statement statement =null;
        ResultSet resultSet =null;
        try {
            reportAndLog("Executing Query: <br>" + query , MessageLevel.INFO);

            connection = connect(dbConnString);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            resultSet = statement.executeQuery(query);
            ResultSetMetaData resultMetaData = resultSet.getMetaData();

            int queryColumnCount = resultMetaData.getColumnCount();

            while (resultSet.next()) {

                HashMap<String, String> rowResults = new HashMap<>();

                for (int i = 1; i <= queryColumnCount; i++) {
                    String columnName = resultMetaData.getColumnName(i);

                    //If key already exists, create a new key with appendix "1".
                    while (rowResults.containsKey(columnName))
                    {
                        columnName +="1";
                    }
                    rowResults.put(columnName, (String)resultSet.getObject(i));
                }
                queryResultsList.add(rowResults);
            }
            if(queryResultsList.isEmpty())
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA,MessageLevel.INFO);

        } catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            sqlException.printStackTrace();
            throw new Error(sqlException);
        }
        finally {
            disconnect(connection,resultSet,statement);
        }
        return queryResultsList;
    }



    /**
     * Execute a modification query (such as: create table, insert row, drop, truncate etc..
     * This should not be used for Selection queries
     * @param query the query to execute
     * @param dbConnString connection string
     * @author sela.zvika
     * @since 15.05.2023
     */
    public void executeUpdate(String query, String dbConnString) {

        Statement statement = null;
        Connection connection = null;

        try {

            reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);

            connection = connect(dbConnString);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            statement.executeUpdate(query);
            // Close the statement and connection
            statement.close();
            connection.close();
        }
        catch (SQLException | ClassNotFoundException sqlException) {
            reportAndLog("Query failed with error " + sqlException.getMessage(),MessageLevel.ERROR);
            throw new Error(String.format("Failed to run query, got error <b> %s </b>", sqlException.getMessage()));
        }
        finally {
            disconnect(connection,null,statement);
        }
    }

    /**
     * Disconnect from database (close statement, resultsset and connection)
     * @param connection Instance of Connection to close
     * @param resultSet Instance of ResultSet to close
     * @param statement Instance of Statement to close
     */
    public void disconnect(Connection connection, ResultSet resultSet, Statement statement){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) { logger.error("Failed to close result set"); logger.error(Arrays.toString(e.getStackTrace()));}
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) { logger.error("Failed to close statement"); logger.error(Arrays.toString(e.getStackTrace()));}
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) { logger.error("Failed to close connection"); logger.error(Arrays.toString(e.getStackTrace()));}
        }
    }
}
