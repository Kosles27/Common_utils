package propertyUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;


/**
 * decorator for java.util.Properties that handles config.properties file. <br>
 * Extension functionality load properties file allowing load file by parameter
 * or default.
 *
 * Extension functionality get from Properties.<br>
 * So it is possible get the parameter according to the parameter type and not
 * only String also possible remove and set property (from Properties object,
 * not from file),<br>
 * using java.util.Properties functionality <b>Example #1</b><br>
 * The following example get int property from default project properties file
 * final int i = new Property().getIntProperty("keyForIntVal");
 *
 * <b>Example #2</b><br>
 * The following example get boolean property from specific properties file
 * boolean b=new
 * Property("C:/data/config.properties").getBooleanProperty("keyForBoolVal")
 *
 * @author Yael Rozenfeld
 */
public class Property {

    private Logger logger = LoggerFactory.getLogger(Property.class);
    private Properties properties;
    private final String default_global_file = "zim.global.properties";


    /**
     * private c-tor. Handle c-tor with path parm and without param initial instance
     * of Property object with properties file <br>
     * (default or according to param).
     *
     * @param pathToPropertyFile - optional- path to properties file<br>
     *                           - in case don't interested use default
     */
    private Property(Optional<String> pathToPropertyFile) {
    // Get the path to the file and initial properties object
        String propertyFile = pathToPropertyFile.orElse("");
        properties = initPropertyObject(propertyFile);
    }

    /**
     * c-tor - will load properties from default file
     */
    public Property() {
        this(Optional.empty());
    }

    /**
     * c-tor - will load properties from specific file in case file not found - will
     * load default.
     *
     * @param pathToProperty - path to specific properties file
     */
    public Property(String pathToProperty) {
        this(Optional.of(pathToProperty));
    }

    /**
     * initial properties object - load properties file to object
     *
     * @param path - path to property file or "" for load default.
     */
    private Properties initPropertyObject(String path) {
        Properties prop = new Properties();
        try {
            if (!path.isEmpty() && new File(path).exists()) {
                // load properties file according to parameter
                FileInputStream inputStream = new FileInputStream(path);
                prop.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            } else {
                prop.load(Property.class.getClassLoader().getResourceAsStream(default_global_file));
            }

        } catch (Throwable t) {
            logger.info("Could not initialize property file");
            logger.info(t.getStackTrace().toString());
        }
        return prop;
    }

    /**
     * return the value by fine the key send from user parameter
     *
     * @param key - key name for get his value
     * @return return string type value of key if key doesn't exist return null
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * set new property by key and value set property in stuck. doesn't in the
     * config.properties file
     *
     * @param key  - new key to add to stuck
     * @param value - value of new key
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * remove property from stuck doesn't erase from the config.properties file
     *
     * @param key - key to remove
     */
    public void removeProperty(String key) {
        properties.remove(key);
    }

    /**
     * get property by key and convert it to type Integer from String Note! If you
     * are using <code>int x= getIntProperty("some_value") </code><br>
     * and the result is NULL, an exception will be thrown since you are trying to
     * assign null into a basic java type, which cannot contain null.
     *
     * @param key -  the key to look for and return it's value (key with int value)
     * @return Integer value. if doesn't match to Integer return null
     */
    public Integer getIntProperty(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException numberFormatException) {
            logger.info("Value of key: " + key + "is: " + getProperty(key) + "can't parse it to int");
            logger.info(numberFormatException.getMessage());
            return (Integer) null;

        }
    }

    /**
     * get property by key and convert it to type Double from String Note! If you
     * are using <code>double x= getIntProperty("some_value") </code><br>
     * and the result is NULL, an exception will be thrown since you are trying to
     * assign null into a basic java type, which cannot contain null.
     *
     * @param key -  the key to look for and return it's value (key with double value)
     * @return Double value. if doesn't match to Double will throw exception
     */
    public Double getDoubleProperty(String key) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (NumberFormatException numberFormatException) {
            logger.info("Value of key: " + key + "is: " + getProperty(key) + "can't parse it to double");
            logger.info(numberFormatException.getMessage());
            return (Double) null;
        }
    }

    /**
     * return boolean value if the key (String) is equals to True || true return
     * true other wise return false
     *
     * @param key - key to look for and return it's value (key with boolean value)
     * @return true if key equals to True || true false other wise
     */
    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    /** Get All the Keys in property file
     *
     * @return Set of all the property keys
     * @author umflat.lior
     * @since 26.1.2023
     */
    public Set<String> getAllKeysInPropertyFile(){
        Set<Object> propertyKeysObject = properties.keySet();
        Set<String> propertiesKeysString = new HashSet<>();

        for(Object key:propertyKeysObject){
            propertiesKeysString.add((String) key);
        }
        return propertiesKeysString;
    }

}

