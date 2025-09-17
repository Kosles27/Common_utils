package dbUtils;

import fileUtils.CsvUtils;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;

/**
 * class of website queries
 * @since 27.11.2014
 * @author Ofek.plot and rawand.abo_saleh
 */
public class CsvStorageQueries {

    private CsvStorageQueries()
    {

    }
    /**
     * get port of loading query
     * @param user - user to append to query
     * @return string of query
     * @throws Exception - Exception
     * @since 27.11.2024
     * @author Ofek.Plot and Rawand.abo_saleh
     */
    public static String getPortOfLoadingQuery(String user) throws Exception {
        String filePath = System.getProperty("user.dir").substring(0,39);
        filePath += "common_utils\\src\\main\\resources\\webSiteQueries.csv";
        CSVRecord csvRecord = CsvUtils.getRecordFromCSV("QueryName", "getPortOfLoadingQuery", filePath);
        return String.format(csvRecord.get(1), user);
    }

    /**
     * Fetch a query from a specified CSV file and format it with dynamic variables.
     * @param fileName  - The name of the CSV file (e.g., "webSiteQueries.csv").
     * @param queryType - The type of query to fetch (e.g., "getPortOfLoadingQuery").
     * @param params    - The parameters to format the query with.
     * @return The formatted query string.
     * @throws Exception If the file or query is not found.
     * @author Lesnichy.Kostya
     * @since 28.11.2024
     */
    public static String getQuery(String fileName, String queryType, Object... params) throws Exception {
        // Load the CSV file dynamically from the classpath
        InputStream inputStream = CsvStorageQueries.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("File '" + fileName + "' not found in resources!");
        }

        // Parse the CSV file to get the query
        CSVRecord csvRecord = CsvUtils.getRecordFromCSV("QueryName", queryType, inputStream);

        // Get the query string from the record (assume it's in the second column)
        String queryTemplate = csvRecord.get(1);

        // Format the query with the provided parameters
        return String.format(queryTemplate, params);
    }
}
