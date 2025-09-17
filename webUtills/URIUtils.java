package webUtills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/** Utility class for handling URIs.
 * This class wraps a URI object and provides functionality to handle and manipulate it.
 * The URI object represents a Uniform Resource Identifier (URI).
 * This class provides various methods to access components of a URI, such as the scheme (http, https), host, path, query, and fragment.
 * It allows parsing a given URL string into an object for further processing.
 *
 * @author daniel.shir
 * @since 29.09.24
 */
@SuppressWarnings("unused")
public class URIUtils {

    URI uri;
    private static final Logger logger = LoggerFactory.getLogger(URIUtils.class);

    public URIUtils (String url){
        try {
            this.uri = new URI(url);
        }
        catch (Throwable e) {
            logger.error("An error occurred while creating new instance of URIUtils: " + e.getStackTrace());
        }
    }

    /** Extract query pairs from a URL
     * This method parses the query string of the URI and extracts key-value pairs separated by '&'. The key and value are split by '='.
     * Example URL with real query parameters
     * URL: <a href="https://www.example.com/search?query=java&sort=popular&limit=10">...</a>
     * This method return:
     * query = java
     * sort = popular
     * limit = 10
     * @return query pairs
     * @author daniel.shir
     * @since 29.09.24
     */
    public Map<String, String> getQueryPairs(){
        Map<String, String> queryPairs = new HashMap<>();
        String query = uri.getQuery();

        // key-value pairs separated by '&'
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            // key and value are split by '='
            int idx = pair.indexOf("=");
            queryPairs.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return queryPairs;
    }
}
