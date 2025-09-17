package seleniumUtils;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import propertyUtils.Property;

import java.util.Map;

/**
 * Capability handling class. Currently for Appium only.
 * TODO: Refactor - add capabilities by properties file and remove the hard coded class capabilities
 *
 * @author Nir Gallner
 * @since August 2021
 */
public class GenericMobileCapabilities {

    protected static Logger logger = LoggerFactory.getLogger(GenericMobileCapabilities.class);

    private DesiredCapabilities capabilities;

    /**
     * Empty c-tor. Will set default capabilities - My zim mobile app for Android
     */
    public GenericMobileCapabilities() {
        capabilities = new DesiredCapabilities();
        capabilities.merge(setDefaultCapabilities("android"));
    }

    /**
     * c-tor with capabilities
     *
     * @param capabilities Capabilities to set
     */
    public GenericMobileCapabilities(Capabilities capabilities) {

        if (capabilities != null)
            this.capabilities = new DesiredCapabilities(capabilities);
        else
            this.capabilities = new DesiredCapabilities();
    }

    public GenericMobileCapabilities(String platformName,String propPath) {

        // First we check if the user added the capability name from mvn -D
        String capabilitiesName = "";

        Property property = new Property(propPath);
        String capabilitiesPath = property.getProperty("default_capabilities_file_location");
        if (System.getProperty("caps.name") == null) {
            // Did not find in mvn -D, get default from default file
            property = new Property(propPath);
            capabilitiesName = property.getProperty("default_caps_name");
        } else {
            capabilitiesName = System.getProperty("caps.name");
        }

        try {
            this.capabilities = CapabilitiesReader.getDesiredCapabilities(capabilitiesName, capabilitiesPath);
            return;
        } catch (Throwable t) {
            logger.error("Could not load capabilities from file, switch to default from class");
        }

        // This part will be executed only if an exception was thrown
        this.capabilities = new DesiredCapabilities();
        this.capabilities.merge(this.setDefaultCapabilities(platformName));
    }


    public DesiredCapabilities getCapabilities() {
        return this.capabilities;
    }

    /**
     * Deletes old capabilities and set new set of given capabilities
     *
     * @param capabilities New capabilities to set
     */
    public void replaceCapabilities(DesiredCapabilities capabilities) {

        this.capabilities = capabilities;

    }

    /**
     * Merges current capabilities with additional new capabilities. It uses the merge method of
     * Capabilities object.
     *
     * @param capabilities Capabilities to be merged with class capabilities
     * @return The newly merged capabilities
     */
    public Capabilities mergeCapabilities(Capabilities capabilities) {
        if (capabilities != null)
            this.capabilities.merge(capabilities);

        return this.capabilities;
    }

    /**
     * Adds new capability to the set of capabilities
     *
     * @param capabilities The new capability
     * @return The new set of capabilities including the original ones and the new added one
     */
    public Capabilities addCapabilities(Capabilities capabilities) {
        return this.mergeCapabilities(capabilities);
    }


    /**
     * Builds a default capabilities set according to platformName and sets it as the DesiredCapabilities object
     * for the class
     * @param platformName Currently support IOS or Android only
     * @return Default DesiredCapabilty object
     */
    private Capabilities setDefaultCapabilities(String platformName) {
        DesiredCapabilities caps = new DesiredCapabilities();

        if (platformName.toLowerCase().equals(Platform.ANDROID.toString().toLowerCase()))
            caps.merge(defaultAndroidCapabilities());
        else if (platformName.toLowerCase().equals(Platform.IOS.toString().toLowerCase()))
            caps.merge(defaultIOSCapabilities());
        else
            logger.error("Requested platform " + platformName + " is not supported");

        return caps;
    }

    /**
     * Builds a default, hard coded, Zim, Android desired capabilities object
     * TODO: This code is only relevant to Zim projcet. It should be removed or changed in other cases
     * @return Hard coded zim DesiredCapabilities object
     */
    private Capabilities defaultAndroidCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", "11.0");
        capabilities.setCapability("appPackage", "com.zim");
        capabilities.setCapability("appActivity", "com.zim.MainActivity");
        capabilities.setCapability("locationServicesAuthorized", true);
        capabilities.setCapability("noReset", true);

        return capabilities;
    }

    public void clearCapabilities() {
        this.capabilities = new DesiredCapabilities();
    }

    /**
     * Builds a default, hard coded, Zim, IOS desired capabilities object
     * TODO: This code is only relevant to Zim projcet. It should be removed or changed in other cases
     * @return Hard coded zim DesiredCapabilities object
     */
    private Capabilities defaultIOSCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "IOS");
        return capabilities;
    }

    @Override
    public String toString() {
        String result = "Class GenericMobileCapabilities. Capabilities:  \n";
        Map<String, Object> caps = this.capabilities.asMap();
        for (Map.Entry<String, Object> entry : caps.entrySet()) {
            result.concat(entry.getKey() + ":" + entry.getValue().toString() + "\n");
        }

        return result;
    }
}
