package devToolsUtils;

import drivers.TesnetWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.devtools.v114.network.model.RequestId;
import org.openqa.selenium.devtools.v114.network.model.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class of methods that support the browser network
 * @author genosar.dafna
 * @since 15.04.2024
 */
@SuppressWarnings({"unused", "unchecked"})
public class NetworkUtils {

    /**
     * Get data from the browser network as hash map
     *    "url" - the url the browser called
     *    "request id" - the request ID
     *    "status code" - the status code from the API call
     *    "response" - the response object
     *    "message" - the returned message
     * @param driver the driver
     * @param containsUrlString the url the API calls (or part of it)
     *                          Note: if you want to get the data from an upload button (browse button) you can go to Network tab when inspecting the browser, then send the file
     *                          to the input field of the button.
     *                          Then check the URL in the call.
     *                          For example: if you upload a CSV file, the network tab will display 'csv', click on it and under 'Headers' you will find the Request URL.
     *                          Please make sure you do not add the whole url, as we test on different environments
     * @author genosar.dafna
     * @since 15.04.2024
     */
    public static <V> Map<String, V> getNetworkData(WebDriver driver, String containsUrlString){

        Map<String, V> dataToReturn = new HashMap<>();

        //Create devtools session
        DevTools devTools = ((HasDevTools) ((TesnetWebDriver)driver).getWrappedDriver()).getDevTools();
        devTools.createSession();

        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        AtomicReference<Response> responseReference = new AtomicReference<>();
        AtomicReference<String> responseBodyReference = new AtomicReference<>();
        AtomicReference<String> responseUrlReference = new AtomicReference<>();
        AtomicReference<RequestId> requestIdReference = new AtomicReference<>();
        AtomicReference<Integer> statusCode = new AtomicReference<>();

        devTools.addListener(Network.responseReceived(), responseReceived -> {

            Response response = responseReceived.getResponse();
            requestIdReference.set(responseReceived.getRequestId());

            if (response.getUrl().contains(containsUrlString)) { // Adjust this condition to match your upload URL

                responseReference.set(response);
                requestIdReference.set(responseReceived.getRequestId());
                statusCode.set(response.getStatus());
                responseUrlReference.set(response.getUrl());

                System.out.println("URL: " + response.getUrl());
                System.out.println("Status Code: " + response.getStatus());

                if (responseReceived.getRequestId() != null) {
                    String body = devTools.send(Network.getResponseBody(requestIdReference.get())).getBody();
                    responseBodyReference.set(body);
                }
            }
        });

        dataToReturn.put("url", (V)responseUrlReference);
        dataToReturn.put("request id", (V)requestIdReference);
        dataToReturn.put("status code", (V)statusCode);
        dataToReturn.put("response", (V)responseReference);
        dataToReturn.put("message", (V)responseBodyReference);

        return dataToReturn;
    }
}
