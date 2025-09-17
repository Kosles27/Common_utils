package dbUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *   @author zvika.sela
 *   @since 15.06.2021
 * this interface describes the basic methods a DBConnectionManager is expected to provide.
 */
@SuppressWarnings("unused")
public interface DBConnectionManager {

    
    /** return a single value from the query */
    Object getSingleValueFromFirstRowAndColumn(String query, String dbConnString,String dbUser, String dbPass);
    /** return a single row from Query */
    Object getSingleRowResult(String query, String dbConnString,String dbUser, String dbPass);
    /** return a table from the query */
    Object getQueryResultTable(String query, String dbConnString,String dbUser, String dbPass);

    /** return a DB connection */
    Connection connect(String dbConnString,String dbUser, String dbPass, boolean reportConnectionDetails) throws ClassNotFoundException, SQLException;
    /** close all resources */
    void disconnect(Connection connection, ResultSet resultSet, Statement statement);


}
