package fileUtils;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Managers.ExcelSqlManager;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


/**
 * Handles readeing Excel files using SQL queries. It uses
 * {@link com.codoid.products.fillo} package to query Excel files as SQL. It
 * supports Select, Update and Insert.
 *
 * @author <a href="mailto:nir@verisoft.co">Nir Gallner</a>
 * @since 2.0.1
 *
 * @see ExcelSqlManager
 * @see com.codoid.products.fillo
 */
public class ExcelSqlConnector implements ExcelSqlManager {

    private static Logger logger= LoggerFactory.getLogger(ExcelSqlConnector.class);

    private String pathToFile;
    private final Fillo fillo;
    private Connection connection;

    /**
     * Sets the path and create a new {@link com.codoid.products.fillo.Fillo} object
     */
    public ExcelSqlConnector(final String path) {
        this.pathToFile = path;
        fillo = new Fillo();
        connection = null;

    }

    /**
     * Create a new {@link com.codoid.products.fillo.Fillo} object
     */
    public ExcelSqlConnector() {
        pathToFile = "";
        fillo = new Fillo();
        connection = null;
    }

    /**
     * Connects to the File. Opens up the file according path.
     *
     * @return true if connection succeded, false otherwise
     * @throws RuntimeException  if the path is invalid
     * @throws NullPointerException if the given path is null
     * @throws FilloException       (RunTimeException) if exception occured during
     *                              connection
     */
    public boolean connect() {

        // Connect
        try {
            connection = this.fillo.getConnection(this.pathToFile);
            logger.debug("Connected to Excel file as DB. File is: " + pathToFile);
        } catch (final FilloException ex) {
            logger.error("could not open a connection to the DB. Error is: " + ex.getMessage());
            throw new RuntimeException(ex);
        }

        return isConnected();
    }

    /**
     * Closes the connection
     */
    @Override
    public void closeSession() {
        logger.debug("Close Excel DB connection. File is: " + pathToFile);
        connection.close();
        connection = null;
    }

    /**
     * Query the DB (Excel) and build a 2 dimension {@link String} with the results
     *
     * @return String[][] holding the result of the query
     * @param Query the SQL query to be executed
     */
    @Override
    public Object[][] queryDB(final String Query) {

        Recordset rs = null;
        String[][] result = null;
        try {

            // 1. Query DB
            rs = connection.executeQuery(Query);
            final List<String> names = rs.getFieldNames();

            // 2. Transfer the data to String array
            result = new String[rs.getCount()][rs.getFieldNames().size()];
            int i = 0;
            while (rs.next()) {
                for (int j = 0; j < names.size(); j++)
                    result[i][j] = rs.getField(names.get(j));
                i++;
            }

        } catch (final FilloException ex) {
            logger.error("Error performing query, query string was: " + Query + " error is: " + ex.getMessage());
            throw new RuntimeException(ex);

        }

        return result;
    }

    /**
     * Executes all types of SQl commands: Select, Update and Insert
     *
     * @param command The commcand to be executed
     * @return boolean true if the command was executed without error or exceptions.
     *         False otherwise
     */
    @Override
    public boolean executeCommand(final String command) throws SQLException {

        final String[] cmd = command.split(" ");

        // Validation
        if (cmd.length < 3) {
            logger.error("Minimum sql command length is 3");
            return false;
        }
        if (!this.isConnected()) {
            logger.error("Not connected to data source");
            return false;
        }

        boolean result = false;
        switch (cmd[0].toLowerCase()) {
            case "select":
                try {
                    this.connection.executeQuery(command);
                    result = true;
                } catch (final FilloException ex) {
                    logger.info("Could not execute query. Query is: " + command + " error is: " + ex.getMessage());
                    result = false;
                }
                break;

            case "update":
                // Same as insert - continue
            case "insert":
                try {
                    connection.executeUpdate(command);
                    result = true;
                } catch (final FilloException ex) {
                    logger.info("Could not execute update/insert query. Query is: " + command + " error is: "
                            + ex.getMessage());
                    result = false;
                }
                break;
            default:
                logger.error("Illegal query, query is: " + command);
                throw new SQLException("Illegal query-" + command);

        }

        return result;
    }

    /**
     * @return true if connection established, false otherwise
     */
    @Override
    public boolean isConnected() {
        return !Objects.isNull(connection);
    }

    /**
     * @return String return the pathToFile
     */
    public String getFilePath() {
        return pathToFile;
    }

    /**
     * @param pathToFile the pathToFile to set
     */
    public void setFilePath(final String pathToFile) {
        this.pathToFile = pathToFile;
    }

    /**
     * @return Fillo return the fillo
     */
    public Fillo getFilloObject() {
        return fillo;
    }

    /**
     * @return Connection return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        final String path = pathToFile.isEmpty() ? "NULL" : pathToFile;

        final String result = "Fillo Object: Path to file is: " + path + " , Connection is currently open? "
                + isConnected();

        return result;
    }

}
