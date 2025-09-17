package objectsUtils;

import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import drivers.TesnetWebDriver;
import drivers.TesnetWebElement;
import enumerations.MessageLevel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tableUtils.PrettyPrintTable;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static reportUtils.Report.reportAndLog;

/**
 * Class that holds methods for objects handling
 * @author Yael Rozenfeld
 * @since 1/03/2022
 */
public class ObjectsUtils {

    private static final Logger logger = LoggerFactory.getLogger(ObjectsUtils.class);

    /**
     * Creates a new instance of the desired object.
     * The method searches all the object's constructors and find the matching one to create the object with.
     * If no matching constructor is found the method will throw an error
     * Example: If you want to create and object of type Animal and give it the driver and the WebElement, you should call it like this:
     * newInstance(Animal.class, driver, element)
     * @param classType The class type we want to wrap the object with. example: CardWithArrow.class
     * @param params Optional - The required params to create the object. Example: the WebElement that was found
     * @param <T> Generic Class Type
     * @param <P> Generic Parameter
     * @return a new instance of the object, as the class type
     * @author Dafna Genosar
     * @since 24.11.2021
     * @author Dafna Genosar
     * @since 06.10.2024
     */
    public static <T, P> T newInstance(Class<T> classType, @Nullable P... params) {


        //Get all the class's constructors
        Constructor<?>[] classConstructors = classType.getConstructors();

        logger.info(String.format("Number of class constructors: %d", classConstructors.length));

        for (int i=0; i < classConstructors.length; i++) {

            //if the correct constructor was found, create a new instance
            if(isMatchingConstructor(classConstructors[i], params)) {
                try {
                    logger.info(String.format("Create new instance of %s with Param: %s", classType,params));
                    return (T) classConstructors[i].newInstance(params);
                }
                catch (Throwable e) {
                    throw new Error(String.format("Failed to create a new instance of class %s<br>Error: %s", classType, e.getMessage()));
                }
            }
        }

        throw new Error(String.format("Could not create a new instance of '%s'. No matching constructor was found",classType));
    }

    private static <P> boolean isMatchingConstructor(Constructor<?> constructor, @Nullable P... param){

        LinkedList<P> receivedParamsList = new LinkedList<>();

        if(param != null)
            receivedParamsList = new LinkedList<>(Arrays.asList(param));

        LinkedList<Class<?>> constructorParameterTypes = new LinkedList<>(Arrays.asList((constructor.getParameterTypes())));

        //Check if the number of received params matches the number of the current checked constructor
        boolean numberOfConstructorParamsMatchesReceivedParams = constructorParameterTypes.size() == receivedParamsList.size();

        //Check if the constructor's number of parameters matches the received parames
        if(!numberOfConstructorParamsMatchesReceivedParams)
            return false;

        //Go over the constructor params and check they match the received params
        for(int i=0; i< constructorParameterTypes.size(); i++){

            Class<?> constructorParameterType = constructorParameterTypes.get(i);
            P receivedParam = receivedParamsList.get(i);

            //Check if the param types match
            if(!receivedParam.getClass().equals(constructorParameterType)){

                boolean bothParamsDriver = false;
                boolean bothParamsElement = false;
                boolean bothParamsString = false;
                boolean paramExtendsConstructorParam = false;

                //Check if the received param (current checked param) is a WebDriver TesnetWebDriver
                if(receivedParam.getClass().equals(WebDriver.class) || receivedParam.getClass().equals(TesnetWebDriver.class)) {
                    if(constructorParameterType.equals(WebDriver.class))
                        bothParamsDriver = true;
                }
                //Check if the received param (current checked param) is a WebElement or TesnetWebElement
                else if(receivedParam.getClass().equals(WebElement.class) || receivedParam.getClass().equals(TesnetWebElement.class)){
                    if(constructorParameterType.equals(WebElement.class))
                        bothParamsElement = true;
                }
                //Check if the received param (current checked param) and the current constructor are Strings
                else if(receivedParam.getClass().equals(String.class) && constructorParameterType.equals(String.class)) {
                    bothParamsString = true;
                }
                //Check if the received param (current checked param) and the current constructor are Strings
                else if(constructorParameterType.isAssignableFrom(receivedParam.getClass())){
                    paramExtendsConstructorParam = true;
                }

                if(!bothParamsDriver && !bothParamsElement && !bothParamsString && !paramExtendsConstructorParam)
                    return false;
            }
        }
        return true;
    }

    /**
     *
     * @param classType The class type we want to wrap the object with. example: CardWithArrow.class
     * @param param Optional - The required param to create the object. Example: the Webelement that was found
     * @param <T> Generic Class Type
     * @param <P> Generic Parameter
     * @return a new instance of the object, as the class type
     * @author Dafna Genosar
     * @since 24.11.2021
     * @author Dafna Genosar
     * @since 17.07.2022
     */
    public static <T, P> T newInstanceWithoutDriver(Class<T> classType, @Nullable P param) {

        try {

            logger.info(String.format("Create new instance of %s with Param: %s", classType, param));

            Constructor<?>[] classConstructors = classType.getConstructors();
            logger.info(String.format("Number of class constructors: %d", classConstructors.length));

            //If with params
            if (param != null) {
                for (int i=0; i<classConstructors.length; i++) {

                    List<Class<?>> parameterTypes  = Arrays.asList(classConstructors[i].getParameterTypes());
                    logger.info(String.format("Number of parameters in class constructor in index %d: %d", i, parameterTypes.size()));

                    if(parameterTypes.size() > 0)
                    {
                        for (Class<?> parameterType : parameterTypes) {
                            boolean constructorFound = false;
                            //If the parameter in the constructor is a WebElement
                            if(parameterType.equals(WebElement.class)){
                                if((param.getClass().equals(TesnetWebElement.class) || (param.getClass().equals(WebElement.class)))){
                                    constructorFound = true;
                                }
                            }
                            else if(parameterType.equals(param.getClass()) && !parameterType.getName().contains("WebDriver"))
                                constructorFound = true;

                            if(constructorFound){
                                logger.info(String.format("Create a new instance with constructor in index %d", i));
                                return (T) classConstructors[i].newInstance(param);
                            }
                        }
                    }
                }
                throw new Error(String.format("Could not create a new instance of class '%s'. No matching constructor was found", classType.toString()));
            }
            //If no params
            else
            {
                for (int i=0; i<classConstructors.length; i++) {
                    Class<?>[] parameterTypes = classConstructors[i].getParameterTypes();
                    logger.info(String.format("Number of parameters in class constructor in index %d: %d", i, parameterTypes.length));

                    if(parameterTypes.length == 0)
                    {
                        return (T) classConstructors[i].newInstance();
                    }
                }
                throw new Error(String.format("Could not create a new instance of class '%s'. No matching constructor was found", classType.toString()));
            }
        }
        catch (Throwable e)
        {
            throw new Error(String.format("Could not create a new instance of class '%s'<br>%s", classType.toString(), e.getMessage()));
        }
    }

    /**
     * Get differences between 2 objects (from the same class)
     * @param firstObject - first object to compare.
     * @param secondObject - second object to compare.
     * @return A list of Hashmap. each item in list contains information of differences between objects for a specific field.
     *         each Hashmap contains 3 entries:
     *         fieldName - field was compare
     *         firstObjectValue - value as String in first object
     *         secondObjectValue - value as String in second object
     *         If fields are equals information about them will not be insert to the list
     * @throws IllegalAccessException IllegalAccessException
     * @author Yael Rozenfeld
     * @since 2/10/2022
     */
    public static List<HashMap<String,String>> getDifferenceBetweenObjects(Object firstObject,Object secondObject) throws IllegalAccessException  {

        List<HashMap<String,String>> diffValues = new ArrayList<>();
        //verify if objects classes are the same
        if(!firstObject.getClass().equals(secondObject.getClass())){
            HashMap<String,String> classdif= new HashMap<>();
            classdif.put("fieldName","class");
            classdif.put("firstObjectValue",firstObject.getClass().getName());
            classdif.put("secondObjectValue",secondObject.getClass().getName());
            diffValues.add(classdif);
            return diffValues;
        }

        Field[] objectFields =  firstObject.getClass().getFields();
        for (Field field : objectFields) {
            HashMap<String,String> itemDif= new HashMap<>();
            //Indicates that the reflected object should suppress checks for Java language access control when it is used.
            field.setAccessible(true);
            Object value1 = field.get(firstObject);
            Object value2 = field.get(secondObject);
           if (value1 != null && value2 != null) {
                if (!java.util.Objects.equals(value1, value2)) {
                    itemDif.put("fieldName",field.getName());
                    itemDif.put("firstObjectValue",String.valueOf(value1));
                    itemDif.put("secondObjectValue",String.valueOf(value2));
                    diffValues.add(itemDif);
                }
            }
            else if (value1 != null && value2 == null) {
                itemDif.put("fieldName",field.getName());
                itemDif.put("firstObjectValue",String.valueOf(value1));
                itemDif.put("secondObjectValue","Null");
                diffValues.add(itemDif);
            }
            else if(value1 == null && value2 != null) {
                itemDif.put("fieldName", field.getName());
                itemDif.put("firstObjectValue", "Null");
                itemDif.put("secondObjectValue", String.valueOf(value2));
                diffValues.add(itemDif);
            }

        }
        return diffValues;
    }

    /**
     * compare between 2 objects and report the differences between them
     * @param firstObject - first object to compare
     * @param secondObject - second object to compare
     * @param Object1Name - parameter to add the name of the object,
     *      *                     so it will be clear in the report. Like: "current lead object etc".
     * @param Object2Name - parameter to add the name of the object,
     *                          so it will be clear in the report. Like: "expected lead object etc".
     * @param statusReportDif - status to report the differences.
     * @return List<HashMap>  - A list of Hashmap. each item in list contains information of differences between objects for a specific field.
     *                each Hashmap contains 3 entries:
     *                fieldName - field was compare
     *                firstObjectValue - value as String in first object
     *                secondObjectValue - value as String in second object
     *                If fields are equals information about them will not be insert to the list
     * @throws IllegalAccessException IllegalAccessException
     * @author Yael Rozenfeld
     * @since 2/10/2022
     * @author genosar.dafna
     * @since 01.01.2023
     */
    public static List<HashMap<String,String>> ReportDifferencesBetweenObjects(Object firstObject, Object secondObject, String Object1Name,String Object2Name, @Nullable MessageLevel statusReportDif) throws IllegalAccessException {

        String headerStyle = "background-color: #D6EEEE; width: 100%; border: 1px solid black; font-weight: bold;";

        MessageLevel difStatus =statusReportDif !=null ? statusReportDif:MessageLevel.INFO;
        List<HashMap<String,String>> differences = getDifferenceBetweenObjects(firstObject,secondObject);
        if (differences.size()==0){
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,String.format("No differences were found between objects %s and %s",Object1Name,Object2Name));
        }
        else {
            String reportDifferences = "";
            for (HashMap<String, String> dif : differences) {
                //Highlight the differences in yellow
                List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(dif.get("firstObjectValue"),  dif.get("secondObjectValue"));
                String value = PrettyPrintTable.getTableRowToPrint(null, 100, dif.get("fieldName"), highlightedDifferences.get(0),  highlightedDifferences.get(1));

                reportDifferences += value;
            }

            String reportTitleLine = String.format("Object '%s' and '%s' have the following entries differences: <br>", Object1Name, Object2Name);
            String tableHeaderLine = PrettyPrintTable.getTableRowToPrint(headerStyle, 100, "Field Name", Object1Name, Object2Name);
            String reportLine = reportTitleLine + tableHeaderLine + reportDifferences;

            reportAndLog(reportLine, difStatus);
        }
        return differences;
    }

    /**
     * Check if values are equal
     * @param value1 value1
     * @param value2 value2
     * @return true if the values are equal/ false otherwise
     * @author genosar.dafna
     * @since 06.09.2023
     * @since 17.02.2025
     */
    public static <T> boolean areEqual(T value1, T value2){

         if(value1 instanceof DateTime)
            return ((DateTime)value1).equals((DateTime) value2);

         else
            return Objects.equals(value1, value2);
    }
    //todo - 18.02.2025 - Dafna: I am leaving this for the time being until I am sure the changes didnt break other things
//    public static <T> boolean areEqual(T value1, T value2){
//
//        if(value1 == null)
//            return value2 == null;
//
//        //String
//        if(value1 instanceof String)
//            return value1.toString().equals(value2.toString());
//
//        //Integer
//        else if(value1 instanceof Integer)
//            return (int)value1 == (int)value2;
//
//        //Double
//        else if(value1 instanceof Double){
//            double epsilon = 0.000001d;
//            return (Math.abs((Double)value1 - (Double)value2) < epsilon);
//        }
//
//        //DateTime
//        else if(value1 instanceof DateTime)
//            return ((DateTime)value1).equals((DateTime) value2);
//
//        //Boolean
//        else if(value1 instanceof Boolean){
//            Boolean value1B = (Boolean) value1;
//            Boolean value2B = (Boolean) value2;
//            return Boolean.compare(value1B, value2B) == 0;
//        }
//         else
//            throw new Error("The expected value type in doValuesMatch() is not supported. Please add support");
//    }
}
