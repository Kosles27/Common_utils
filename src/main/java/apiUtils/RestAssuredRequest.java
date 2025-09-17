package apiUtils;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static io.restassured.RestAssured.given;


/**
 * This class handles  Rest-Assured request types.
 *
 * @author - Yael.Rozenfeld
 * @since - 05.09.2021
 */
@SuppressWarnings("unused")
public class RestAssuredRequest {

    private final static Logger logger = LoggerFactory.getLogger(RestAssuredRequest.class);

    //define connection manager timeout - the time to wait for a connection from the connection manager/pool
    //time is milliseconds
    RestAssuredConfig config = RestAssured.config()
            .httpClient(HttpClientConfig.httpClientConfig().setParam("CONNECTION_MANAGER_TIMEOUT", 3000));
    /**
     * perform request GET API with basic authorization
     * @param url -url of API
     * @param params - params for send to API
     * @param headersMap - header for API
     * @param cookiesMap - cookies for API
     * @param user - user who performs the API action
     * @param password - password of user who performs the API action
     * @return Response - response of the request
     * @author - Zvika.Sela
     * @since - 05.09.2021
     */
    public Response apiGet(String url, HashMap<String,String> params, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap,String user,String password) {


        Response resp = null;
        try {
            RequestSpecification rs = given().params(params);
            rs = rs.given().headers(headersMap).cookies(cookiesMap).auth().basic(user,password).log().all();
            resp = rs.when().get(url).then().extract().response();
            logger.debug(resp.asString());

        } catch (Exception e) {

            logger.error("Get request Failed "  +e.getMessage(),e);
        }

        return resp;

    }

    /**
     * perform request Head API  with basic authorization by rest-Assured
     * @param url -url of API
     * @param headersMap - header for API
     * @param cookiesMap - cookies for API
     * @param user - user who performs the API action - for authentication
     * @param password - password of user who performs the API action - for authentication
     * @return Response - response of the request
     * @author - Zvika.Sela
     * @since - 05.09.2021
     */
    public Response apiHead(String url, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap,String user,String password) {


        Response resp = null;
        try {
            RequestSpecification rs = given();
            rs = rs.given().headers(headersMap).cookies(cookiesMap).auth().basic(user,password).log().all();
            resp = rs.when().head(url).then().extract().response();
            logger.debug(resp.asString());

        } catch (Exception e) {
            logger.error("HEAD request Failed",e.getMessage(),e);


        }
        return resp;

    }

    /**
     * perform request POST API  with basic authorization by rest-Assured
     * @param url -url of API
     * @param entityBody - body for send in the request post
     * @param headersMap - header for API
     * @param cookiesMap - cookies for API
     * @param user - user who performs the API action - for authentication
     * @param password - password of user who performs the API action - for authentication
     * @return Response - response of the request
     * @author - Zvika.Sela
     * @since - 05.09.2021
     */

    public Response apiPost(String url, String entityBody, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap, String user, String password) {
        Response resp = null;
        try {
            RequestSpecification rs = given().body(entityBody);
            rs = rs.given().headers(headersMap).cookies(cookiesMap).auth().basic(user,password).log().all();
            resp = rs.when().post(url).then().extract().response();
            logger.debug(resp.asString());
        } catch (Exception e) {
            logger.error("POST request Failed",e.getMessage(),e);

        }
        return resp;
    }

}



