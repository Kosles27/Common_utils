package apiUtils;

import enumerations.AscDescEnum;
import enumerations.MessageLevel;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;
import secretsManager.Secret;
import secretsManager.SecretUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static propertyUtils.PropertyUtils.getGlobalProperty;
import static systemUtils.SystemCommonUtils.sleep;

/**
 * API methods reading from Elastic
 * @author genosar.dafna
 * @since 07.11.2024
 */
@SuppressWarnings("unused")
public class ElasticApi {

    private static final Logger logger = LoggerFactory.getLogger(ElasticApi.class);

    /**
     * Get Elastic API response
     * @param environment environment
     * @param index Elastic index (Same as Kafka's topic) (Top left dropdown on Elastic page)
     * @param filters Optional filters
     * @param waitTimeForKafkaToUpdateElastic optional - time (in seconds) to wait for Kafka to update Elastic. If null - will wait 120 seconds
     * @param reportTimeWaitedForResponse true if to write the waitTimeForKafkaToUpdateElastic to the report / false if to log the time without writing to the report
     * @param reportRequestUrl true if to write the request url to the report / false if to log the url without writing to the report
     * @return the response
     * @author Lesnichy.Kostya + Genosar Dafna
     * @since 07.11.2024
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public static Response getElasticResponse(String environment, String index, @Nullable HashMap<String, Object> filters, @Nullable Integer waitTimeForKafkaToUpdateElastic,
                                              boolean reportTimeWaitedForResponse, boolean reportRequestUrl) {

        int sleepTime = waitTimeForKafkaToUpdateElastic == null? 120000: waitTimeForKafkaToUpdateElastic * 1000;

        //Wait for Kafka to update Elastic index
        if(reportTimeWaitedForResponse)
            Report.reportAndLog(String.format("Wait %d seconds for Elastic to be updated = %s", sleepTime, index), MessageLevel.INFO);
        else
            logger.info(String.format("Wait %d seconds for Elastic to be updated = %s", sleepTime, index));

        sleep(sleepTime);

        try {
            String host = getGlobalProperty("ELASTIC_URI");
            int port = 443;

            //Get AKEYLESS credentials
            Secret elasticSecret = SecretUtils.getElasticSecret(environment);
            String username = elasticSecret.getUsername();
            String password = elasticSecret.getPassword();

            // Construct the Elasticsearch query URL

            //Build query string from filters
            StringBuilder queryBuilder = new StringBuilder();
            String url;

            if (filters != null) {

                StringBuilder endOfQueryBuilder = new StringBuilder();

                for (Map.Entry<String, Object> entry : filters.entrySet()) {

                    String key = entry.getKey();
                    String value;

                    if (entry.getValue() instanceof String)
                        value = String.format("\"%s\"", entry.getValue());
                    else
                        value = entry.getValue().toString();

                    if (key.startsWith("&"))
                        endOfQueryBuilder.append(key).append(":").append(value);
                    else if (queryBuilder.length() > 0) {
                        queryBuilder.append(" AND ").append(key).append(":").append(value);
                    } else {
                        queryBuilder.append(key).append(":").append(value);
                    }
                }
                queryBuilder.append(endOfQueryBuilder);

                String query = queryBuilder.toString();
                url = "https://" + username + ":" + password + "@" + host + ":" + port + "/" + index + "/_search?q=" + query;
            }
            else
                url = "https://" + username + ":" + password + "@" + host + ":" + port + "/" + index + "/_search";

            if (reportRequestUrl)
                Report.reportUrl("Get API response from URL: ", url, MessageLevel.INFO);
            else
                logger.info("Get API response from URL: " + url);

            // Use RestApi class to make the request
            RestApi restApi = new RestApi();
            return restApi.get(url);
        }
        catch (Throwable e){
            throw new Error(String.format("Failed to get a response from Elastic<br>Index: <b>%s</b><br>Error: %s", index, e.getMessage()));
        }
    }

    /**
     * Get Elastic API response
     * @param environment environment
     * @param index Elastic index (Same as Kafka's topic) (Top left dropdown on Elastic page)
     * @param filters Optional filters
     * @param waitTimeForKafkaToUpdateElastic optional - time (in seconds) to wait for Kafka to update Elastic. If null - will wait 120 seconds
     * @param sort Enum DESC/ASC to sort the results
     * @param time optional time in seconds. If you want to get only the results of the last 15 minutes then set time to be 15
     * @param reportTimeWaitedForResponse true if to write the waitTimeForKafkaToUpdateElastic to the report / false if to log the time without writing to the report
     * @param reportRequestUrl true if to write the request url to the report / false if to log the url without writing to the report
     * @return the response
     * @author Lesnichy.Kostya + Genosar Dafna
     * @since 07.11.2024
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public static Response getElasticResponse(String environment, String index, @Nullable HashMap<String, Object> filters, @Nullable Integer waitTimeForKafkaToUpdateElastic,
                                              AscDescEnum sort, @Nullable Integer time, boolean reportTimeWaitedForResponse, boolean reportRequestUrl){

        if(filters == null)
            filters = new HashMap<>();
        if(time != null){
            StringBuilder value = new StringBuilder(); //This is of type StringBuilder on purpose, please DO NOT change it to a String type
            value.append("[now-").append(time).append("m TO now]");
            filters.put("LAST_UPDATE", value);
        }
        if(!sort.equals(AscDescEnum.NONE)){
            filters.put("&sort=LAST_UPDATE", sort.toString());
        }
        return getElasticResponse(environment, index, filters, waitTimeForKafkaToUpdateElastic, reportTimeWaitedForResponse, reportRequestUrl);
    }

    /**
     * Get Elastic API response and Extract the hits from the response
     * @param environment environment
     * @param index Elastic index (Same as Kafka's topic) (Top left dropdown on Elastic page)
     * @param filters Optional filters
     * @param waitTimeForKafkaToUpdateElastic optional - time (in seconsa) to wait for Kafka to update Elastic. If null - will wait 120 seconds
     * @param reportTimeWaitedForResponse true if to write the waitTimeForKafkaToUpdateElastic to the report / false if to log the time without writing to the report
     * @param reportRequestUrl true if to write the request url to the report / false if to log the url without writing to the report
     * @return the response
     * @author Lesnichy.Kostya + Genosar Dafna
     * @since 07.11.2024
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public static List<HashMap<String, Object>> getDataFromElastic(String environment, String index, @Nullable HashMap<String, Object> filters, @Nullable Integer waitTimeForKafkaToUpdateElastic,
                                                                   boolean reportTimeWaitedForResponse, boolean reportRequestUrl) {

        Response response = getElasticResponse(environment, index, filters, waitTimeForKafkaToUpdateElastic, reportTimeWaitedForResponse, reportRequestUrl);

        // Extract hits from the response
        return getDataFromElasticResponse(response);
    }

    /**
     * Extract the hits from the response
     * @param response the response
     * @author genosar.dafna
     * @since 19.11.2024
     */
    public static List<HashMap<String, Object>> getDataFromElasticResponse(Response response) {

        // Extract hits from the response
        return response.jsonPath().get("hits.hits._source");
    }
}
