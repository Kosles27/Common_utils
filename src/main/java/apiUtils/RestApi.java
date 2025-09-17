package apiUtils;

import collectionUtils.MapUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static io.restassured.config.EncoderConfig.encoderConfig;

/**
 * This class supports Rest-Assured requests type.
 * @author - Dafna Genosar
 * @since - 28.02.2022
 */
@SuppressWarnings({"unused", "unchecked"})
public class RestApi {

    private static final Logger logger = LoggerFactory.getLogger(RestApi.class);

    //define connection manager timeout - the time to wait for a connection from the connection manager/pool
    //time is milliseconds
    RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig().setParam("CONNECTION_MANAGER_TIMEOUT", 3000));

    private final RequestSpecification requestSpecification;

    static {
        setConfigToRelaxedHTTPSValidation();
    }

    public RestApi()
    {
        this.requestSpecification = RestAssured.given();
    }

    /**
     * Constructor in Case you want to change the url encoding default (default=true)
     * @param urlEncode set the urlEncoding
     * @author Sela Tzvika
     * @since 10.04.2022
     */
    public RestApi(boolean urlEncode)
    {
        this.requestSpecification = RestAssured.given();
        this.requestSpecification.urlEncodingEnabled(urlEncode);
    }

    /**
     * Set RestAssured global configuration so that it accepts all SSL certificates (even expired or untrusted).
     * @author Sela Tzvika
     * @since 10.04.2022
     * @author genosar.dafna
     * @since 18.01.2024
     */
    public static void setConfigToRelaxedHTTPSValidation()
    {
        RestAssured.config = RestAssured.config().sslConfig(
                new SSLConfig().relaxedHTTPSValidation());
    }

    /**
     * Set RestAssured base URL
     * @param url the url to set
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public static void setBaseURL(String url)
    {
        RestAssured.baseURI = url;
    }

    /**
     * Set RestAssured base URL
     * @param url the url to set
     * @param requestSpecification requestSpecification object
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public static void setBaseURL(String url, RequestSpecification requestSpecification)
    {
        RestAssured.baseURI = url;
        requestSpecification.baseUri(url);
    }

    /**
     * Initialises RequestSpecification object with username and password only
     * @param userName username
     * @param password password
     * @return RequestSpecification object
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public RestApi setRequestAuthorization(String userName, String password)
    {
        this.requestSpecification.auth().preemptive().basic(userName, password);
        return this;
    }

    /**
     * Specifies the headers that will be sent with the request.
     * @param headersMap headers as a hash map
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public RestApi setRequestHeaders(HashMap<String,String> headersMap)
    {
        String reportLine = MapUtils.prettyPrintMap(headersMap).replace(":", "=");

        logger.info(String.format("Set request headers: <br> %s", reportLine));

        this.requestSpecification.headers(headersMap);
        return this;
    }

    /**
     * Specifies a header that will be sent with the request.
     * @param headerName header name
     * @param headerValue header value
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public RestApi setHeader(String headerName, String headerValue)
    {
        this.requestSpecification.header(headerName, headerValue);
        return this;
    }

    /**
     * Specifies the query params that will be sent with the request.
     * @param queryParams the params as a HashMap
     * @author Dafna Genosar
     * @since 21.02.2022
     * @since 29.07.2024 - changed .params(...) to .queryParams(...)
     */
    public RestApi setRequestQueryParams(HashMap<String,String> queryParams)
    {
        this.requestSpecification.queryParams(queryParams);

        logger.info(String.format("Request params were set: <br> %s", getRequestParams()));
        return this;
    }

    /**
     * Get the request parameters
     * @return the request parameters
     * @author Dafna Genosar
     * @since 24.07.2022
     */
    public Map<String, String> getRequestParams()
    {
        return ((RequestSpecificationImpl) this.requestSpecification).getRequestParams();
    }

    /**
     * Specifies the cookies params that will be sent with the request.
     * @param cookiesMap the params as a HashMap
     * @author Dafna Genosar
     * @since 21.02.2022
     * @since 16.07.2023
     */
    public RestApi setRequestCookies(HashMap<String,String> cookiesMap)
    {
        String reportLine = MapUtils.prettyPrintMap(cookiesMap);

        logger.info(String.format("Set request cookies: <br> %s", reportLine));

        this.requestSpecification.cookies(cookiesMap);
        return this;
    }

    /**
     * Performs a GET request to a path. Gets data from server and initialises the response object
     * @param url the url
     * @return a Response object
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public Response get(String url) {

        Response resp = null;
        try {
            resp = this.requestSpecification.when().get(url).then().extract().response();
            logger.debug(resp.asString());
        }
        catch (Exception e) {
            logger.error("Get request Failed " + e.getMessage(),e);
        }

        return resp;
    }

    /**
     * Performs a POST request. Posts data to server.
     * @param JSONObjectRequestBody the request parameters (body) to post as JSONObject.
     * @param resource the path required to perform the request
     * @return a Response object
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public Response post(JSONObject JSONObjectRequestBody, String resource)
    {
        return post(JSONObjectRequestBody.toJSONString(), resource);
    }

    /**
     * Performs a POST request. Posts data to server.
     * @param requestBody the request parameters (body) to post as HashMap.
     * @return a Response object
     * @author Dafna Genosar
     * @since 24.04.2022
     */
    public Response post(HashMap<String,String> requestBody, String resource)
    {
        String bodyString ="";
        for (String key : requestBody.keySet()) {
            bodyString += key + "=" + requestBody.get(key) + "&";
        }
        bodyString = bodyString.substring(0, bodyString.length()-1);

        return post(bodyString, resource);
    }

    /**
     * Performs a POST request. Posts data to server.
     * @param requestBody the request parameters (body) to post as String.
     * @return a Response object
     * @author Dafna Genosar
     * @since 21.02.2022
     * @since 26.02.2023 - changed reporting to the HTML log to reporting to the normal log to prevent heavy HTML
     * @since 25.07.2024
     */
    public Response post(String requestBody, String resource)
    {
        logger.info(String.format("Post <br><b>Request body:</b> %s <br><b>Url:</b> %s", requestBody, resource));
        RequestSpecification requestSpecification = RestAssured.given();
        this.requestSpecification.body(requestBody);
        Response response = this.requestSpecification.when().post(resource).then().extract().response();
        logger.debug(response.asString());
        return response;
    }

    /**
     * Performs a PUT request. Updates data in server.
     * @param JSONObjectRequestBody the request parameters (body) to post as JSONObject
     * @param resource the path required to perform the request
     * @return a Response object
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public Response put(JSONObject JSONObjectRequestBody, String resource)
    {
        return put(JSONObjectRequestBody.toJSONString(), resource);
    }

    /**
     * Performs a PUT request. Updates data in server.
     * @param requestBody the request parameters (body) to post as JSONObject
     * @param resource the path required to perform the request
     * @return a Response object
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public Response put(String requestBody, String resource)
    {
        requestSpecification.body(requestBody);
        return requestSpecification.put(resource);
    }

    /**
     * Performs a DELETE request. Deletes data from server.
     * @param resource the path required to perform the request
     * @return a Response object
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public Response delete(String resource)
    {
        return requestSpecification.delete(resource);
    }

    /**
     * Get the access token string from the API response
     * @param response the API response
     * @return access token from string the API response.
     * @author Dafna Genosar
     * @since 05.04.2022
     */
    public String getAccessTokenString(Response response)
    {
        return getValueFromJSON(response, "access_token");
    }

    /**
     * Get the access token type from the API response
     * @param response the API response
     * @return access token type from string the API response.
     * @author Dafna Genosar
     * @since 24.04.2022
     */
    public String getAccessTokenType(Response response)
    {
        return getValueFromJSON(response, "token_type");
    }

    /**
     * Get the response message if exists or null
     * @param response the response
     * @return the response message if exists or null
     * @author Dafna Genosar
     * @since 28.03.2022
     */
    public static String getResponseMessage(Response response)
    {
        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("returnMessage");
        message = message == null? jsonPath.get("message") : message;
        return message;
    }

    /**
     * Extracts value from JSON format.
     * Gets a JsonPath view of the response body.
     * This will let you use the JsonPath syntax to get values from the response.
     * @param response Response object.
     * @param path the path required to perform the request.
     * @return the value returned from jsonPath as a String.
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public static String getValueFromJSON(Response response, String path)
    {
        JsonPath jsonPath = response.jsonPath();
        return jsonPath.get(path).toString();
    }

    /**
     * Extracts value from JSON object.
     * Returns the first value that matches the received key
     * @param jsonObject JSONObject
     * @param keyToReturn the key to return
     * @return the value returned from JSONObject.
     * @author Dafna Genosar
     * @since 09.11.2022
     */
    public static <T> T getValueFromJsonObject(JSONObject jsonObject, String keyToReturn){

        for (Object key : jsonObject.keySet()) {

            Object value = jsonObject.get(key);

            if(value instanceof ArrayList){
                JSONArray array = (JSONArray)value;
                Iterator iterator = array.iterator();

                //Go over the array
                while (iterator.hasNext()) {
                    JSONObject innerJsonObject = (JSONObject) iterator.next();

                    for (Object innerKey : innerJsonObject.keySet()) {
                        if(innerKey.toString().equals(keyToReturn))
                            return (T)innerJsonObject.get(innerKey);
                    }
                }
            }
            else{
                return (T)value;
            }
        }
        logger.info(String.format("Key '%s' could not be found in JSONObject", keyToReturn));
        return null;
    }

    /**
     * Extracts a list of values from JSON format.
     * Gets a JsonPath view of the response body.
     * This will let you use the JsonPath syntax to get values from the response.
     * @param response Response object.
     * @param path the path required to perform the request.
     * @param <T> the Type of the returned list.
     * @return a list of values returned from jsonPath.
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public static <T> List<T> getListFromJSON(Response response, String path)
    {
        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getList(path);
    }

    /**
     * Extracts a map of values from JSON format.
     * Gets a JsonPath view of the response body.
     * This will let you use the JsonPath syntax to get values from the response.
     * @param response Response object.
     * @param path the path required to perform the request.
     * @param <K> key
     * @param <V> value
     * @return a map of values returned from jsonPath.
     * @author Dafna Genosar
     * @since 21.02.2022
     */
    public static <K, V> Map<K, V> getMapFromJSON(Response response, String path)
    {
        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getMap(path);
    }

    /**
     * Sets the request's header content type.
     * @param headerContentType content type. i.e: json / XML...
     * @return the RequestSpecification object
     * @author Dafna Genosar
     * @since 21.02.2022
     * @since 20.04.2022
     */
    public RequestSpecification setHeaderContentType(String headerContentType)
    {
        requestSpecification.config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));
        setHeader("Content-Type", headerContentType);
        return requestSpecification;
    }

    /**
     * Convert Json String to Json object
     * @param jsonString Json String
     * @return Json object
     * @author Dafna Genosar
     * @since 09.11.2022
     */
    public static JSONObject convertToJsonObject(String jsonString) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(jsonString);
        }
        catch (Exception e){
            throw new Error(String.format("Cannot convert the String to JSONObject<br>Error: %s", e.getMessage()));
        }
    }

    /**
     * Read JSON file to a String
     * @param filePath the path to the file
     * @return JSON file String text
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static String readJsonFileToText(String filePath){
        try{
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        }
        catch(Exception e){
            throw new Error(String.format("Failed to read JSON file %s", filePath));
        }
    }

    /**
     * Read JSON file to a JSONObject
     * @param filePath the path to the file
     * @return JSONObject
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static JSONObject readJsonFileToJsonObject(String filePath){
        String jsonString = readJsonFileToText(filePath);
        return RestApi.convertToJsonObject(jsonString);
    }

    /**
     * Parse a JSON String to a Json element
     * @param jsonText JSON String
     * @return Json element
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static JsonElement parseJsonStringToJsonElement(String jsonText){
        return JsonParser.parseString(jsonText);
    }

    /**
     * Get a Json element as a Json object
     * @param jsonElement Json element
     * @return a Json object
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static JsonObject getJsonElementAsJsonObject(JsonElement jsonElement){
        return ((JsonObject) jsonElement);
    }

    /**
     * Read JSON file to a List<JsonElement>
     * @param jsonFilePath the path to the file
     * @return List<JsonElement>
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static List<JsonElement> readJsonFileToJsonList(String jsonFilePath){

        String jsonText = readJsonFileToText(jsonFilePath);

        JsonElement je = parseJsonStringToJsonElement(jsonText);

        return getJsonElementListValue(je);
    }

    /**
     * Read JSON JsonElement String value
     * @param jsonElement JsonElement
     * @param key the key to read
     * @return the String value from the given key
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static String getJsonElementStringValue(JsonElement jsonElement, String key){
        return getJsonElementAsJsonObject(jsonElement).get(key).toString();
    }

    /**
     * Read JSON JsonElement int value
     * @param jsonElement JsonElement
     * @param key the key to read
     * @return the int value from the given key
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static int getJsonElementIntValue(JsonElement jsonElement, String key){
        return getJsonElementAsJsonObject(jsonElement).get(key).getAsInt();
    }

    /**
     * Get a Json element as a list of elements
     * @param jsonElement Json element
     * @return a list of elements
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static List<JsonElement> getJsonElementListValue(JsonElement jsonElement){
        return ((JsonArray) jsonElement).asList();
    }

    /**
     * Get a Json element as a list of elements
     * @param jsonElement Json element
     * @param key the key to read
     * @return a list of elements
     * @author Genosar.dafna
     * @since 14.01.2024
     */
    public static List<JsonElement> getJsonElementListValue(JsonElement jsonElement, String key){
        return ((JsonArray)((JsonObject) jsonElement).get(key)).asList();
    }
}
