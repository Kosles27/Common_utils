package propertyUtils;

public class PropertyUtils {

    /**
     * Return Zim Common Utils prop file
     *
     * @since 09.05.2021
     * @return Property entity of the zim.global.properties file
     */
    public static Property getGlobalPropertyEntity() {
        return new Property();
    }

    /**
     * Return a value from the key based on zim global properties file
     * @param key  the key in the properties file
     * @since 11.05.2021
     * @return Property entity of the zim.global.properties file
     */
    public static String getGlobalProperty(String key) {
        return new Property().getProperty(key);
    }
}
