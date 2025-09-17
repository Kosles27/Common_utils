package apiUtils;


import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



/**
 * This class handles common HTTP request types
 *
 * @author - Tzvika Sela
 * @since - 13.7.2021
 */
public class HTTPClientRequest {

    private static final Logger logger = LoggerFactory.getLogger(HTTPClientRequest.class);



    /**
     * this method sends GET request with BASIC authorization
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @param user The user for the authentication
     * @param password The password for the authentication
     * @param params Request's params
     * @param headersMap Request's headers
     * @param cookiesMap Request's cookies
     * @return HTTPClientResponse The request's response from server
     */
    public HTTPClientResponse sendGetRequestWithBasicAuthorization(String url, String user, String password, HashMap<String,String> params, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap)  {

        CloseableHttpClient httpclient = null;
        try {
            //set credentials in credentials provider
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();

            //this is setting the authentication scope by domain and port
            credsProvider.setCredentials(
                    new AuthScope(getDomainFromURL(url), getPortFromURL(url)),
                    new UsernamePasswordCredentials(user, password.toCharArray()));

            //build the url if params exist
            if (params != null) {
                URIBuilder urlBuilder = new URIBuilder(url);
                for (Map.Entry<String,String> param:params.entrySet()){
                    urlBuilder.addParameter(param.getKey(),param.getValue());
                }

                url = urlBuilder.build().toURL().toString();
            }


            BasicCookieStore cookieStore = new BasicCookieStore();

            //building the cookiestore if needed
            if (cookiesMap !=null) {
                //set the cookie store if needed
                for (Map.Entry<String, String> cookieEntry : cookiesMap.entrySet()) {
                    BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie.setDomain(getDomainFromURL(url));
                    cookieStore.addCookie(cookie);
                }
            }

            //starting the HTTP client
             httpclient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .setDefaultCookieStore(cookieStore)
                    .build();

            //declaring type of request
            HttpGet httpget = new HttpGet(url);

            if (headersMap !=null) {
                //initiate headers
                for (Map.Entry<String, String> header : headersMap.entrySet()) {
                    httpget.addHeader(header.getKey(), header.getValue());
                }
            }


            //print request
            logger.info("----------------------------------------");
            logger.info("Executing request " + httpget.getMethod() + " " + httpget.getUri());

            //send request
            CloseableHttpResponse response = httpclient.execute(httpget);
            logger.info("****************************************");
            logger.info("Response From Server: ");
            logger.info(response.getCode() + " " + response.getReasonPhrase());

            //put response into our response object
            HTTPClientResponse fullResponseEntities = new HTTPClientResponse(HTTPClientResponse.getResponseBody(response), HTTPClientResponse.extractHeadersFromResponse(response), HTTPClientResponse.extractCookiesFromResponse(response),response.getCode(),response.getReasonPhrase());

            //print response
            fullResponseEntities.print();
            logger.info("----------------------------------------");


            return fullResponseEntities;

        } catch (Exception e) {
            logger.info("Failed Running Request. StackTrace will appear in log file");
            logger.debug(Arrays.toString(e.getStackTrace()));

            return null;
        }
        finally {
            try {
                if (httpclient!=null)
                    httpclient.close();
            } catch (IOException e) {
                logger.info("Failed to close connection");
                logger.info(Arrays.toString(e.getStackTrace()));
            }
        }

    }



    /**
     * this method sends POST request with BASIC authorization
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @param user The user for the authentication
     * @param password The password for the authentication
     * @param postBody Request's Body
     * @param headersMap Request's headers
     * @param cookiesMap Request's cookies
     * @return HTTPClientResponse The request's response from server
     */
    public HTTPClientResponse sendPostRequestWithBasicAuthorization(String url, String user, String password, String postBody, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap)  {


        CloseableHttpClient httpClient = null;
        try {
            //set credentials in credentials provider
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();

            credsProvider.setCredentials(
                    new AuthScope(getDomainFromURL(url), getPortFromURL(url)),
                    new UsernamePasswordCredentials(user, password.toCharArray()));



            //building the cookiestore if needed
            BasicCookieStore cookieStore = new BasicCookieStore();
            if (cookiesMap !=null) {
                //set the cookie store if needed
                for (Map.Entry<String, String> cookieEntry : cookiesMap.entrySet()) {
                    BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie.setDomain(getDomainFromURL(url));
                    cookieStore.addCookie(cookie);
                }
            }


            //start the HTTP client
            httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .setDefaultCookieStore(cookieStore)
                    .build();

            // initiate with proper request type
            HttpPost httpPost = new HttpPost(url);

            //set the body
            StringEntity entity = new StringEntity(postBody);
            httpPost.setEntity(entity);


            if (headersMap !=null) {
                //initiate headers
                for (Map.Entry<String, String> header : headersMap.entrySet()) {
                    httpPost.addHeader(header.getKey(), header.getValue());
                }
            }

            //print request
            logger.info("----------------------------------------");
            logger.info("Executing request " + httpPost.getMethod() + " " + httpPost.getUri());
            logger.info("POST BODY: " + postBody);

            //send request
            CloseableHttpResponse response = httpClient.execute(httpPost);

            //print response
            logger.info("****************************************");
            logger.info("Response From Server: ");
            logger.info(response.getCode() + " " + response.getReasonPhrase());

            //put Apache response into our response object
            HTTPClientResponse fullResponseEntities = new HTTPClientResponse(HTTPClientResponse.getResponseBody(response), HTTPClientResponse.extractHeadersFromResponse(response), HTTPClientResponse.extractCookiesFromResponse(response),response.getCode(),response.getReasonPhrase());
            fullResponseEntities.print();
            logger.info("----------------------------------------");

            return fullResponseEntities;

        } catch (Exception e) {
            logger.info("Failed Running Request");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (httpClient!=null)
                    httpClient.close();
            } catch (IOException e) {
                logger.info("Failed to close connection");
                e.printStackTrace();
            }
        }

    }


    /**
     * this method sends HEAD request with BASIC authorization
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @param user The user for the authentication
     * @param password The password for the authentication
     * @param params Request's params
     * @param headersMap Request's headers
     * @param cookiesMap Request's cookies
     * @return HTTPClientResponse The request's response from server
     */
    public HTTPClientResponse sendHeadRequestWithBasicAuthorization(String url, String user, String password, HashMap<String,String> params, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap)  {


        CloseableHttpClient httpclient = null;
        try {
            //set credentials in credentials provider
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();

            credsProvider.setCredentials(
                    new AuthScope(getDomainFromURL(url), getPortFromURL(url)),
                    new UsernamePasswordCredentials(user, password.toCharArray()));

            //build the url if params exist
            if (params != null) {
                URIBuilder urlBuilder = new URIBuilder(url);
                for (Map.Entry<String,String> param:params.entrySet()){
                    urlBuilder.addParameter(param.getKey(),param.getValue());
                }

                url = urlBuilder.build().toURL().toString();
            }


            BasicCookieStore cookieStore = new BasicCookieStore();

            //building the cookiestore if needed
            if (cookiesMap !=null) {
                //set the cookie store if needed
                for (Map.Entry<String, String> cookieEntry : cookiesMap.entrySet()) {
                    BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie.setDomain(getDomainFromURL(url));
                    cookieStore.addCookie(cookie);
                }
            }

            //starting the HTTP client
            httpclient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .setDefaultCookieStore(cookieStore)
                    .build();


            HttpHead httpHead = new HttpHead(url);

            if (headersMap !=null) {
                //initiate headers
                for (Map.Entry<String, String> header : headersMap.entrySet()) {
                    httpHead.addHeader(header.getKey(), header.getValue());
                }
            }


            //print request
            logger.info("----------------------------------------");
            logger.info("Executing request " + httpHead.getMethod() + " " + httpHead.getUri());

            //send request
            CloseableHttpResponse response = httpclient.execute(httpHead);

            //print response
            logger.info("****************************************");
            logger.info("Response From Server: ");
            logger.info(response.getCode() + " " + response.getReasonPhrase());

            //put response into our response object
            HTTPClientResponse fullResponseEntities = new HTTPClientResponse("", HTTPClientResponse.extractHeadersFromResponse(response), HTTPClientResponse.extractCookiesFromResponse(response),response.getCode(),response.getReasonPhrase());

            logger.info("----------------------------------------");

            return fullResponseEntities;

        } catch (Exception e) {
            logger.info("Failed Running Request");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (httpclient!=null)
                    httpclient.close();
            } catch (IOException e) {
                logger.info("Failed to close connection");
                e.printStackTrace();
            }
        }

    }


    /**
     * this method extracts only the domain part from a URL
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @return domain as String. for example: www.zim.com:8080/home.asp -> zim.com
     */
    private String getDomainFromURL(String url) {
        String domain = null;
        try {
            URI uri = new URI(url);
            domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }
        catch (Exception e){
            logger.error("Failed to get domain from URL. StackTrace will appear in log file");
            logger.debug(Arrays.toString(e.getStackTrace()));
            return null;
        }

    }

    /**
     * this method extracts only the port part from a URL
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @return port as String. for example: www.zim.com:8081/home.asp -> 8081
     */
    private int getPortFromURL(String url) throws Exception {
        int port;
        try {
            URI uri = new URI(url);
            port = uri.getPort();
            return port;
        }
        catch (Exception e){
            logger.error("Failed to get port from URL");
            logger.info(Arrays.toString(e.getStackTrace()));

        }
        throw new Exception("Failed to find port");
    }


    /**
     * this method sends GET request
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @param params Request's params
     * @param headersMap Request's headers
     * @param cookiesMap Request's cookies
     * @return HTTPClientResponse The request's response from server
     */
    public HTTPClientResponse sendGetRequest(String url, HashMap<String,String> params, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap)  {

        CloseableHttpClient httpclient = null;
        try {


            //build the url if params exist
            if (params != null) {
                URIBuilder urlBuilder = new URIBuilder(url);
                for (Map.Entry<String,String> param:params.entrySet()){
                    urlBuilder.addParameter(param.getKey(),param.getValue());
                }

                url = urlBuilder.build().toURL().toString();
            }


            BasicCookieStore cookieStore = new BasicCookieStore();

            //building the cookiestore if needed
            if (cookiesMap !=null) {
                //set the cookie store if needed
                for (Map.Entry<String, String> cookieEntry : cookiesMap.entrySet()) {
                    BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie.setDomain(getDomainFromURL(url));
                    cookieStore.addCookie(cookie);
                }
            }

            //starting the HTTP client
            httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();

            //declaring type of request
            HttpGet httpget = new HttpGet(url);

            if (headersMap !=null) {
                //initiate headers
                for (Map.Entry<String, String> header : headersMap.entrySet()) {
                    httpget.addHeader(header.getKey(), header.getValue());
                }
            }


            //print request
            logger.info("----------------------------------------");
            logger.info("Executing request " + httpget.getMethod() + " " + httpget.getUri());

            //send request
            CloseableHttpResponse response = httpclient.execute(httpget);
            logger.info("****************************************");
            logger.info("Response From Server: ");
            logger.info(response.getCode() + " " + response.getReasonPhrase());

            //put response into our response object
            HTTPClientResponse fullResponseEntities = new HTTPClientResponse(HTTPClientResponse.getResponseBody(response), HTTPClientResponse.extractHeadersFromResponse(response), HTTPClientResponse.extractCookiesFromResponse(response),response.getCode(),response.getReasonPhrase());

            //print response
            fullResponseEntities.print();
            logger.info("----------------------------------------");


            return fullResponseEntities;

        } catch (Exception e) {
            logger.info("Failed Running Request");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (httpclient!=null)
                    httpclient.close();
            } catch (IOException e) {
                logger.info("Failed to close connection");
                e.printStackTrace();
            }
        }

    }


    /**
     * this method sends POST request
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @param postBody Request's Body
     * @param headersMap Request's headers
     * @param cookiesMap Request's cookies
     * @return HTTPClientResponse The request's response from server
     */
    public HTTPClientResponse sendPostRequest(String url, String postBody, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap)  {


        CloseableHttpClient httpClient = null;
        try {

            //building the cookiestore if needed
            BasicCookieStore cookieStore = new BasicCookieStore();
            if (cookiesMap !=null) {
                //set the cookie store if needed
                for (Map.Entry<String, String> cookieEntry : cookiesMap.entrySet()) {
                    BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie.setDomain(getDomainFromURL(url));
                    cookieStore.addCookie(cookie);
                }
            }


            //start the HTTP client
            httpClient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();

            // initiate with proper request type
            HttpPost httpPost = new HttpPost(url);

            //set the body
            StringEntity entity = new StringEntity(postBody);
            httpPost.setEntity(entity);


            if (headersMap !=null) {
                //initiate headers
                for (Map.Entry<String, String> header : headersMap.entrySet()) {
                    httpPost.addHeader(header.getKey(), header.getValue());
                }
            }

            //print request
            logger.info("----------------------------------------");
            logger.info("Executing request " + httpPost.getMethod() + " " + httpPost.getUri());
            logger.info("POST BODY: " + postBody);

            //send request
            CloseableHttpResponse response = httpClient.execute(httpPost);

            //print response
            logger.info("****************************************");
            logger.info("Response From Server: ");
            logger.info(response.getCode() + " " + response.getReasonPhrase());

            //put Apache response into our response object
            HTTPClientResponse fullResponseEntities = new HTTPClientResponse(HTTPClientResponse.getResponseBody(response), HTTPClientResponse.extractHeadersFromResponse(response), HTTPClientResponse.extractCookiesFromResponse(response),response.getCode(),response.getReasonPhrase());
            fullResponseEntities.print();
            logger.info("----------------------------------------");

            return fullResponseEntities;

        } catch (Exception e) {
            logger.info("Failed Running Request");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (httpClient!=null)
                    httpClient.close();
            } catch (IOException e) {
                logger.info("Failed to close connection");
                e.printStackTrace();
            }
        }

    }


    /**
     * this method sends HEAD request
     *
     * @author - Tzvika Sela
     * @since - 13.7.2021
     * @param url The URL for the request
     * @param params Request's params
     * @param headersMap Request's headers
     * @param cookiesMap Request's cookies
     * @return HTTPClientResponse The request's response from server
     */
    public HTTPClientResponse sendHeadRequest(String url, HashMap<String,String> params, HashMap<String,String> headersMap, HashMap<String,String> cookiesMap)  {


        CloseableHttpClient httpclient = null;
        try {


            //build the url if params exist
            if (params != null) {
                URIBuilder urlBuilder = new URIBuilder(url);
                for (Map.Entry<String,String> param:params.entrySet()){
                    urlBuilder.addParameter(param.getKey(),param.getValue());
                }

                url = urlBuilder.build().toURL().toString();
            }


            BasicCookieStore cookieStore = new BasicCookieStore();

            //building the cookiestore if needed
            if (cookiesMap !=null) {
                //set the cookie store if needed
                for (Map.Entry<String, String> cookieEntry : cookiesMap.entrySet()) {
                    BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie.setDomain(getDomainFromURL(url));
                    cookieStore.addCookie(cookie);
                }
            }

            //starting the HTTP client
            httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();


            HttpHead httpHead = new HttpHead(url);

            if (headersMap !=null) {
                //initiate headers
                for (Map.Entry<String, String> header : headersMap.entrySet()) {
                    httpHead.addHeader(header.getKey(), header.getValue());
                }
            }


            //print request
            logger.info("----------------------------------------");
            logger.info("Executing request " + httpHead.getMethod() + " " + httpHead.getUri());

            //send request
            CloseableHttpResponse response = httpclient.execute(httpHead);

            //print response
            logger.info("****************************************");
            logger.info("Response From Server: ");
            logger.info(response.getCode() + " " + response.getReasonPhrase());

            //put response into our response object
            HTTPClientResponse fullResponseEntities = new HTTPClientResponse("", HTTPClientResponse.extractHeadersFromResponse(response), HTTPClientResponse.extractCookiesFromResponse(response),response.getCode(),response.getReasonPhrase());

            logger.info("----------------------------------------");

            return fullResponseEntities;

        } catch (Exception e) {
            logger.info("Failed Running Request");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (httpclient!=null)
                    httpclient.close();
            } catch (IOException e) {
                logger.info("Failed to close connection");
                e.printStackTrace();
            }
        }

    }



}



