package seleniumUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileReader;
import java.util.HashMap;

/**
 * Read capabilities file from Json.
 * Source can be found here:
 * https://medium.com/geekculture/how-to-set-up-appium-desired-capabilities-from-a-json-file-91b3e0bb16dc
 * TODO: to satisfy the Ofek compiler :-) we need to discuss whether to keep this file as is, respecting the source,
 * or break it up to pieces and put it in the best classes for each method
 * @author Rommel Malqued
 * @since Aug 2021
 */
public class CapabilitiesReader {

    private static JSONArray parseJSON(String jsonLocation) throws Exception {
        JSONParser jsonParser = new JSONParser();
        return (JSONArray) jsonParser.parse(new FileReader(jsonLocation));
    }

    private static JSONObject getCapability(String capabilityName, String jsonLocation) throws Exception {
        JSONArray capabilitiesArray = parseJSON(jsonLocation);
        for (Object jsonObj : capabilitiesArray) {
            JSONObject capability = (JSONObject) jsonObj;
            if (capability.get("name").toString().equalsIgnoreCase(capabilityName)) {
                return (JSONObject) capability.get("caps");
            }
        }
        return null;
    }

    private static HashMap<String, Object> convertCapsToHashMap(String capabilityName, String jsonLocation) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getCapability(capabilityName, jsonLocation).toString(), HashMap.class);
    }

    /**
     * Main method of class. This is the entry point to the class logic. Method takes a capability file and capability
     * name, builds a DesiredCapability object and returns it.
     * @param capabilityName The name of the capability to load
     * @param capsContentRootLocation path to capability
     * @return DesiredCapability file
     * @throws Exception
     */
    public static DesiredCapabilities getDesiredCapabilities(String capabilityName, String capsContentRootLocation) throws Exception {
        String jsonLocation = System.getProperty("user.dir") + "/" + capsContentRootLocation;
        HashMap<String, Object>  caps = convertCapsToHashMap(capabilityName, jsonLocation);
        return new DesiredCapabilities(caps);
    }
}
