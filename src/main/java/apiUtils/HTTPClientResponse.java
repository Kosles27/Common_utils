package apiUtils;

import collectionUtils.MapUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;


/**
 * This class is used to save an HTTP response
 *
 * @author - Tzvika Sela
 * @since - 13.7.2021
 */
public class HTTPClientResponse {

    private static Logger logger = LoggerFactory.getLogger(HTTPClientResponse.class);



    HashMap<String,String> headers;
    HashMap<String,String> cookies;
    String body;
    int responseCode;
    String responseMessage;

    /**
     * constructor for the HTTPClientResponse
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param body response's body
     * @param headers response's headers
     * @param cookies response's cookies
     * @param responseCode response code
     * @param responseMessage response message
     *
     */
    public HTTPClientResponse(String body, HashMap<String,String> headers, HashMap<String,String> cookies, int responseCode, String responseMessage){
        this.cookies = cookies;
        this.headers = headers;
        this.body = body;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    /**
     * A Getter for the cookies
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @return hashMap of cookies
     */
    public HashMap<String, String> getCookies() {
        return cookies;
    }

    /**
     * A Getter for the headers
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @return Hashmap of headers
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * A Getter for the body
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @return String of body
     */
    public String getBody() {
        return body;
    }

    /**
     * A Getter for the response code
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @return response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * A Getter for the reponse message
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @return response message
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * pretty print the response object
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     *
     */
    public void print() {
        logger.info("Cookies: " + MapUtils.getPrettyMap(cookies));
        logger.info("Headers: " + MapUtils.getPrettyMap(headers));
        logger.info("Body: will appear in full log");
        logger.debug("Body: " + body);
        logger.info("Status Code: " + this.responseCode);
        logger.info("Status Message: " + this.responseMessage);
    }

    /**
     * this is a service method which returns all cookies from an Apache HttpResponse object
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param response the Apache HttpResponse Object
     * @return hashmap of cookies
     */
    public static HashMap<String,String> extractCookiesFromResponse(CloseableHttpResponse response){
        HashMap<String,String> cookies = new HashMap<String,String>();
        for (Header h:response.getHeaders("Set-Cookie")){
            cookies.put(h.getValue().split("=")[0],h.getValue().split("=")[1]);
        }
        return cookies;
    }

    /**
     * this is a service method which returns the body from an Apache HttpResponse object
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param response the Apache HttpResponse Object
     * @return String of response's Body
     */
    public static String getResponseBody(CloseableHttpResponse response){
        try {
            HttpEntity printEntity = response.getEntity();
            return EntityUtils.toString(printEntity);
        }
        catch(IOException | ParseException e) {
            logger.debug(Arrays.toString(e.getStackTrace()));
            throw new Error("Failed to get response body");

        }

    }

    /**
     * this is a service method which returns the headers from an Apache HttpResponse object
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param response the Apache HttpResponse Object
     * @return headers as String
     */
    public static HashMap<String,String> extractHeadersFromResponse(CloseableHttpResponse response){
        HashMap<String,String> headers = new HashMap<String,String>();
        for (Header h:response.getHeaders()){
            if (!(h.getName().equals("Set-Cookie")))
                headers.put(h.getName(),h.getValue());
        }
        return headers;
    }

}
