package dbUtils;

import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import constantsUtils.CommonConstants;
import enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tableUtils.PrettyPrintTable;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static reportUtils.Report.reportAndLog;

import static propertyUtils.PropertyUtils.getGlobalPropertyEntity;

/**
 * This class will handle Oracle DB Queries, it implements DBConnectionManager methods.
 * Updated to use HikariCP for efficient connection pooling.
 * @author zvika.sela
 * @since 15.06.2021
 */
@SuppressWarnings({"unused", "unchecked"})
public class OracleDatabaseUtil extends DbBase implements DBConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(OracleDatabaseUtil.class);
    private static final Map<String, HikariDataSource> dataSourceMap = new ConcurrentHashMap<>();
    public int queryTimeout = getGlobalPropertyEntity().getIntProperty("query_timeout");

    /**
     * This method sets the query timeout
     * @author zvika.sela
     * @since 28.06.2021
     * @param queryTimeout the amount of timeout in sec
     *
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }


    /**
     * Returns an Oracle DB connection using HikariCP.
     * @param dbConnString the server address
     * @param dbUser the user for the connection
     * @param dbPass the user's pass
     * @return a DB Connection
     */
    @Override
    public Connection connect(String dbConnString, String dbUser, String dbPass, boolean reportConnectionDetails) throws SQLException {
        if (reportConnectionDetails) {
            reportAndLog("Connecting to: " + dbConnString + " with user: " + dbUser, MessageLevel.INFO);
        } else {
            logger.info("Connecting to: " + dbConnString + " with user: " + dbUser);
        }
        return getDataSource(dbConnString, dbUser, dbPass).getConnection();
    }

    /**
     * Initializes or retrieves a HikariDataSource.
     * @param dbConnString the connection string
     * @param dbUser the user for the connection
     * @param dbPass the user's password
     * @return HikariDataSource
     */
    private HikariDataSource getDataSource(String dbConnString, String dbUser, String dbPass) {
        return dataSourceMap.computeIfAbsent(dbConnString + dbUser, key -> {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbConnString);
            config.setUsername(dbUser);
            config.setPassword(dbPass);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "500");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");
            config.setMaximumPoolSize(50);
            logger.info("Initializing new HikariDataSource for: " + key);
            return new HikariDataSource(config);
        });
    }

    /**
     * This method returns a single value from the query, please note that the query will return the upper-left
     * value even if more than 1 value exists in table
     *
     * @param query            the given query we wish to execute of type 'select user_name'
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a String representing the username
     * @author Zvika Sela
     * @since unknown
     * @author genosar.dafna
     * @since 04.07.2024
     */
    public String getSingleValueFromFirstRowAndColumn(String query, String dbConnString, String dbUser, String dbPass) {
        long start = System.currentTimeMillis();
        try (Connection connection = connect(dbConnString, dbUser, dbPass, true)) {
            try (Statement statement = connection.createStatement()) {
                 statement.setQueryTimeout(queryTimeout);
                reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        if (resultSet.next()) {
                            String value = resultSet.getString(1);
                            reportAndLog("Value from query: " + value, MessageLevel.INFO);
                            return value;
                        } else {
                            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
                        }
                    }
            }
        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            reportAndLog("timeExecuteCheck - getSingleValueFromFirstRowAndColumn with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getSingleValueFromFirstRowAndColumn take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return null;
    }



    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a single column count queries
     * Please note that each data in the list is already trimmed
     * @param query            the given query we wish to execute
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a List of database row
     * @author Zvika Sela
     * @since unknown
     * @author genosar.dafna
     * @since 04.07.2024
     */
    public List<String> getResultsFromQuery(String query, String dbConnString, String dbUser, String dbPass) {
        List<String> resultsList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try (Connection connection = connect(dbConnString, dbUser, dbPass, true)) {
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(queryTimeout);
                reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        while (resultSet.next()) {
                            String value = resultSet.getString(1);
                            resultsList.add(value != null ? value.trim() : null);
                        }

                        if (resultsList.isEmpty()) {
                            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
                        } else {
                            if(ReportInstanceManager.getCurrentTestReport() != null)
                                ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Query results: <br>" + Arrays.toString(resultsList.toArray()));
                            logger.debug("Query results: \n" + Arrays.toString(resultsList.toArray()));
                        }
                    }
            }
        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            reportAndLog("timeExecuteCheck - getResultsFromQuery with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getResultsFromQuery take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return resultsList;
    }

    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a single column count queries
     * Please note that each data in the list is already trimmed
     * @param query the given query we wish to execute
     * @param dbConnString the server address
     * @param dbUser valid user name for the server login
     * @param dbPass valid password for the server login
     * @param reportQuery true/false if to add the query string to the report (in cases when the query is very long)
     * @param reportResults true/false if to add the results string to the report (in cases when the results are very long)
     * @return a List of database row
     * @author Zvika Sela
     * @since unknown
     * @author genosar.dafna
     * @since 04.07.2024
     */
    public List<String> getResultsFromQuery(String query, String dbConnString, String dbUser, String dbPass, boolean reportQuery, boolean reportResults) {
        List<String> resultsList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try (Connection connection = connect(dbConnString, dbUser, dbPass, reportResults);
             Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(queryTimeout);

            if (reportQuery) {
                reportAndLog("<b>Iqship - Executing Query:</b> <br>" + query, MessageLevel.INFO);
            }
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String value = resultSet.getString(1);
                    resultsList.add(value != null ? value.trim() : null);
                }
            }

            if (resultsList.isEmpty()) {
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
            } else {
                if(reportResults && ReportInstanceManager.getCurrentTestReport() != null)
                    ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Query results: <br>" + Arrays.toString(resultsList.toArray()));
                logger.debug("Query results: \n" + Arrays.toString(resultsList.toArray()));
            }
        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            reportAndLog("timeExecuteCheck - getResultsFromQuery with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getResultsFromQuery take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return resultsList;
    }

    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a multi-column count queries
     * @param query            the given query we wish to execute
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a List that holds a list of each database row
     * @author Zvika Sela
     * @since unknown
     * @author genosar.dafna
     * @since 04.07.2024
     */
    public List<List<String>> getQueryResultTable(String query, String dbConnString, String dbUser, String dbPass) {
        List<List<String>> queryResultsList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try (Connection connection = connect(dbConnString, dbUser, dbPass, true)) {
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(queryTimeout);
                reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        int queryColumnCount = resultSet.getMetaData().getColumnCount();
                        while (resultSet.next()) {
                            List<String> singleRowResults = new ArrayList<>();
                            for (int i = 1; i <= queryColumnCount; i++) {
                                singleRowResults.add(resultSet.getString(i));
                            }
                            queryResultsList.add(singleRowResults);
                        }

                        if (queryResultsList.isEmpty()) {
                            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
                        } else {
                            PrettyPrintTable.print(queryResultsList);
                        }
                    }

            }
        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            reportAndLog("timeExecuteCheck - getQueryResultTable with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getQueryResultTable take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return queryResultsList;
    }



    /**
     * This method receives a Query then loops through the query results and stores them into a list
     * This is best for a single-row query results
     * @param query            the given query we wish to execute
     * @param dbConnString the server address
     * @param dbUser       valid user name for the server login
     * @param dbPass   valid password for the server login
     * @return a List that holds the resulting row, if more than 1 row is fetched it will only return the first one.
     * @author Zvika Sela
     * @since 18.05.2021
     * @author genosar.dafna
     * @since 04.07.2024
     */
    public List<String> getSingleRowResult(String query, String dbConnString, String dbUser, String dbPass) {
        List<String> singleRowResults = new ArrayList<>();
        long start = System.currentTimeMillis();

        try (Connection connection = connect(dbConnString, dbUser, dbPass, true);
             Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(queryTimeout);
            reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    int queryColumnCount = resultSet.getMetaData().getColumnCount();
                    for (int i = 1; i <= queryColumnCount; i++) {
                        singleRowResults.add(resultSet.getString(i));
                    }
                }
            }

            if (singleRowResults.isEmpty()) {
                reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
            } else {
                reportAndLog("Query results: <br>" + Arrays.toString(singleRowResults.toArray()), MessageLevel.INFO);
            }

        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            reportAndLog("timeExecuteCheck - getSingleRowResult with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getSingleRowResult take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return singleRowResults;
    }

    /**
     * Disconnect from database (close statement, resultsset and connection)
     * @param connection Instance of Connection to close
     * @param resultSet Instance of ResultSet to close
     * @param statement Instance of Statement to close
     */
    @Override
    public void disconnect(Connection connection, ResultSet resultSet, Statement statement) {
        try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { logger.error("Failed to close result set", e); }
        try { if (statement != null) statement.close(); } catch (SQLException e) { logger.error("Failed to close statement", e); }
        try { if (connection != null) connection.close(); } catch (SQLException e) { logger.error("Failed to close connection", e); }
    }

    /**
     * This method returns the count of the query results, please note that the query type needs to be 'Count'
     *
     * @param query            the given query we wish to execute of the type 'Count'
     * @param iqShipConnection the server address
     * @param iqShipUser       valid user name for the server login
     * @param iqShipPassword   valid password for the server login
     * @return an integer representing the results count
     */
    public int getQueryCountTypeResults(String query, String iqShipConnection, String iqShipUser, String iqShipPassword) {
        List<String> resultsFromQuery = getResultsFromQuery(query, iqShipConnection, iqShipUser, iqShipPassword);
        logger.info("Query count results " + resultsFromQuery);
        return Integer.parseInt(resultsFromQuery.get(0));
    }

    /**
     * Execute an SQL stored procedure with 1 int parameter
     * @param statement Sql statement to execute
     * @param intParameter Parameter of type int to be integrated into statement
     * @param iqShipConnection DB connection string
     * @param iqShipUser DB user
     * @param iqShipPassword DB password
     * @author plot.ofek
     * @since 22.05.2021
     * @author genosar.dafna
     * @since 04.07.2024
     */
    public void callableStatement(String statement, int intParameter, String iqShipConnection, String iqShipUser, String iqShipPassword) {
        try (Connection connection = connect(iqShipConnection, iqShipUser, iqShipPassword, true);
             CallableStatement stmt = connection.prepareCall(statement)) {

            stmt.setInt(1, intParameter);
            stmt.setQueryTimeout(queryTimeout);
            stmt.execute();
            logger.info("Callable statement executed");

        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            throw new Error(e);
        }
    }

    /**
     * Execute an SQL stored procedure with 1 String parameter
     * @param statement Sql statement to execute
     * @param stringParameter Parameter of type String to be integrated into statement
     * @param iqShipConnection DB connection string
     * @param iqShipUser DB user
     * @param iqShipPassword DB password
     * @author plot.ofek
     * @since 26.07.2021
     * @author genosar.dafna
     * @since 04.07.2024
     */
    public void callableStatement(String statement, String stringParameter, String iqShipConnection, String iqShipUser, String iqShipPassword) {
        try (Connection connection = connect(iqShipConnection, iqShipUser, iqShipPassword, true);
             CallableStatement stmt = connection.prepareCall(statement)) {

            stmt.setString(1, stringParameter);
            stmt.setQueryTimeout(queryTimeout);
            stmt.execute();
            logger.info("Callable statement executed");

        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            throw new Error(e);
        }
    }

    /**
     * Execute an SQL stored procedure with any number of parameters using Hikari connection.
     * @param statement Sql statement to execute
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @param params Array of all parameters
     * @author tzvika.sela
     * @since 02.08.2021
     */
    public <K, V, T extends Map<K, V>> T runGenericStoredProcedureWithoutOutput(String statement, String dbConnString, String dbUser, String dbPass, Object... params) {
        try (Connection connection = connect(dbConnString, dbUser, dbPass, true);
             CallableStatement stmt = connection.prepareCall(statement)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else {
                    throw new IllegalArgumentException("Unsupported parameter type: " + param.getClass().getName());
                }
            }

            stmt.setQueryTimeout(queryTimeout);
            stmt.execute();
            logger.info("Stored procedure executed: " + statement);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("status", "success");
            return (T) resultMap;

        } catch (SQLException e) {
            reportAndLog("Stored procedure execution failed with error " + e.getMessage(), MessageLevel.ERROR);
            throw new Error(e);
        }
    }

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, Object
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @return a Hash map containing the query result. i.e column names and their values
     * @author Dafna Genosar
     * @since 09.11.2021
     * @since 04.07.2024
     */
    public <K, V, T extends Map<K, V>, L extends List<T>> L getQueryResultMap(String query, String dbConnString, String dbUser, String dbPass){
        LinkedList<LinkedHashMap<String, Object>> queryResultsList = new LinkedList<>();
        long start = System.currentTimeMillis();
        try (Connection connection = connect(dbConnString, dbUser, dbPass, true)) {
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(queryTimeout);
                reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);
                    try (ResultSet resultSet = statement.executeQuery(query)) {

                        ResultSetMetaData resultMetaData = resultSet.getMetaData();
                        int queryColumnCount = resultMetaData.getColumnCount();

                        while (resultSet.next()) {
                            LinkedHashMap<String, Object> rowResults = new LinkedHashMap<>();
                            for (int i = 1; i <= queryColumnCount; i++) {
                                String columnName = resultMetaData.getColumnName(i);
                                while (rowResults.containsKey(columnName)) {
                                    columnName += "1";
                                }
                                rowResults.put(columnName, resultSet.getObject(i));
                            }
                            queryResultsList.add(rowResults);
                        }

                        if (queryResultsList.isEmpty()) {
                            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
                        }

                    }
            }
        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            reportAndLog("timeExecuteCheck - getQueryResultMap with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getQueryResultMap take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return (L) queryResultsList;
    }

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @return a Hash map containing the query result. i.e column names and their values
     * @author Dafna Genosar
     * @since 26.12.2021
     * @since 04.07.2024
     */
    public <K, V, T extends Map<K, V>, L extends List<T>> L getQueryStringResultMap(String query, String dbConnString, String dbUser, String dbPass) {
        LinkedList<LinkedHashMap<String, String>> queryResultsList = new LinkedList<>();
        long start = System.currentTimeMillis();
        try (Connection connection = connect(dbConnString, dbUser, dbPass, true);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(queryTimeout);
                    try (ResultSet resultSet = statement.executeQuery(query)) {

                        ResultSetMetaData resultMetaData = resultSet.getMetaData();
                        int queryColumnCount = resultMetaData.getColumnCount();

                        while (resultSet.next()) {
                            LinkedHashMap<String, String> rowResults = new LinkedHashMap<>();
                            for (int i = 1; i <= queryColumnCount; i++) {
                                String columnName = resultMetaData.getColumnName(i);
                                while (rowResults.containsKey(columnName)) {
                                    columnName += "1";
                                }
                                rowResults.put(columnName, resultSet.getString(i));
                            }
                            queryResultsList.add(rowResults);
                        }

                        if (queryResultsList.isEmpty()) {
                            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
                        }

                    }
                }
            catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
                long end = System.currentTimeMillis();
                reportAndLog("timeExecuteCheck - getQueryStringResultMap with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getQueryStringResultMap take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return (L) queryResultsList;
    }

    /**
     * Return a Hash map containing the query result as a list of HashMaps of String, string
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @param reportQueryToLog true id to write the query string to the report
     * @return a Hash map containing the query result. i.e column names and their values
     * @author Dafna Genosar
     * @since 06.08.2023
     * @since 04.07.2024
     */
    public <K, V, T extends Map<K, V>, L extends List<T>> L getQueryStringResultMap(String query, String dbConnString, String dbUser, String dbPass, boolean reportQueryToLog) {
        LinkedList<LinkedHashMap<String, String>> queryResultsList = new LinkedList<>();
        long start = System.currentTimeMillis();
        try (Connection connection = connect(dbConnString, dbUser, dbPass, reportQueryToLog)) {
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(queryTimeout);
                if(reportQueryToLog)
                    reportAndLog("Executing Query: <br>" + query, MessageLevel.INFO);
                    try (ResultSet resultSet = statement.executeQuery(query)) {

                        ResultSetMetaData resultMetaData = resultSet.getMetaData();
                        int queryColumnCount = resultMetaData.getColumnCount();

                        while (resultSet.next()) {
                            LinkedHashMap<String, String> rowResults = new LinkedHashMap<>();
                            for (int i = 1; i <= queryColumnCount; i++) {
                                String columnName = resultMetaData.getColumnName(i);
                                while (rowResults.containsKey(columnName)) {
                                    columnName += "1";
                                }
                                rowResults.put(columnName, resultSet.getString(i));
                            }
                            queryResultsList.add(rowResults);
                        }

                        if (queryResultsList.isEmpty()) {
                            reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
                        }

                    }
            }
        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            if(reportQueryToLog)
                reportAndLog("timeExecuteCheck - getQueryStringResultMap with error take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }
        long end = System.currentTimeMillis();
        if(reportQueryToLog)
            reportAndLog("timeExecuteCheck - getQueryStringResultMap take " + (end-start)/1000 + " seconds", MessageLevel.INFO);
        return (L) queryResultsList;
    }

    /**
     * Return a List of String array containing the query result, where the List.get(0) holds the columns' names
     * @param query query to the DB
     * @param dbConnString DB connection string
     * @param dbUser DB user
     * @param dbPass DB password
     * @param reportQueryToLog true id to write the query string to the report
     * @return a List of String array containing the query result, where the List.get(0) holds the columns' names
     * @author Dafna Genosar
     * @since 14.07.2025
     */
    public LinkedList<String[]> getQueryStringResultArray(String query, String dbConnString, String dbUser, String dbPass, boolean reportQueryToLog) {
        LinkedList<String[]> queryResultsList = new LinkedList<>();
        long start = System.currentTimeMillis();

        try (Connection connection = connect(dbConnString, dbUser, dbPass, reportQueryToLog);
             Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(queryTimeout);

            try (ResultSet resultSet = statement.executeQuery(query)) {
                ResultSetMetaData resultMetaData = resultSet.getMetaData();
                int columnCount = resultMetaData.getColumnCount();

                // First row: header names
                String[] headers = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    headers[i] = resultMetaData.getColumnName(i + 1);
                }
                queryResultsList.add(headers);

                // Data rows
                while (resultSet.next()) {
                    String[] row = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = resultSet.getString(i + 1);
                    }
                    queryResultsList.add(row);
                }

                if (queryResultsList.size() <= 1) { // Only header
                    reportAndLog(CommonConstants.FailureClassificationErrors.MISSING_DATA, MessageLevel.INFO);
                }
            }

        } catch (SQLException e) {
            reportAndLog("Query failed with error " + e.getMessage(), MessageLevel.ERROR);
            long end = System.currentTimeMillis();
            reportAndLog("timeExecuteCheck - getQueryStringResultArray with error took " + (end - start) / 1000 + " seconds", MessageLevel.INFO);
            throw new Error(e);
        }

        long end = System.currentTimeMillis();
        reportAndLog("timeExecuteCheck - getQueryStringResultArray took " + (end - start) / 1000 + " seconds", MessageLevel.INFO);
        return queryResultsList;
    }
}
