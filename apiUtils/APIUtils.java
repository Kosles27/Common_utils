package apiUtils;

import enumerations.MessageLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static reportUtils.Report.reportAndLog;


/**
 * general API service methods
 *
 * @author Sela.Tzvika
 * @since 10.05.2022
 */
public class APIUtils {

    /**
     * Get the access token including the access type. both values are returned in one string one after the other
     * @param clientID client ID
     * @param clientSecret client secret
     * @param scope scope, like Tracing / P2P
     * @param tokenAuthorizationURL the token authorization URL
     * @return The access token including the access type. both values are returned in one string one after the other
     * @author genosar.dafna
     * @since 24.04.2022
     */
    public static String getAccessToken(String clientID, String clientSecret, String scope, String tokenAuthorizationURL)
    {
        reportAndLog("Get access token", MessageLevel.INFO);

        RestApi restApi = new RestApi();

        restApi.setHeaderContentType(ContentType.URLENC.toString());

        HashMap<String,String> body = new HashMap<>();

        body.put("grant_type","client_credentials");
        body.put("client_id",clientID);
        body.put("client_secret",clientSecret);
        body.put("scope",scope);

        Response response = restApi.post(body, tokenAuthorizationURL);

        if(response.getStatusCode() != 200)
            throw new Error(String.format("Status code %d when posting for token. Message: %s", response.getStatusCode(), response.getStatusLine()));

        String accessToken = restApi.getAccessTokenString(response);
        String tokenType = restApi.getAccessTokenType(response);

        return String.format("%s %s", tokenType, accessToken);
    }

    /**
     * this method parses the URL's params (GET) into hash map
     * example: http://zim.com?me=xxx&y=zzz
     * will result in:
     * <me,xxx>
     * <y,zzz>
     * @param url url which to extract the params from
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @author Sela.Tzvika
     * @since 5.4.2022
     */
    public static Map<String, String> getUrlParamsAsHashMap(java.net.URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new HashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }




}


