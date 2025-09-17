package Managers;

import collectionUtils.ListUtils;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Holds all the instances of WebDriver
 * This class is a singletone. It holds a map of all the instances of WebDriver created during tests.
 * It puts the WebDriver object along with it's thread id, so the class will work properly even for
 * multi thread environment
 *
 * @author Lior Umflat
 * @since April 21
 */
@SuppressWarnings("unused")
public class WebDriverInstanceManager {

    private static Map<Integer, WebDriver> driverMap = new LinkedHashMap<>();

    private static LinkedList<WebDriver> drivers = new LinkedList<>();


    private WebDriverInstanceManager() {
    }

    /**
     * @return the open drivers
     * @author genosar.dafna
     * @since 04.08.2024
     */
    public static Map<Integer, WebDriver> getDrivers() {
        return driverMap;
    }

    /**
     * @return the open drivers
     * @author genosar.dafna
     * @since 04.08.2024
     */
    public static LinkedList<WebDriver> getDriversList() {
        return drivers;
    }

    /**
     * Quit all open drivers
     * @author genosar.dafna
     * @since 04.08.2024
     */
    public static void quitAllDrivers() {

        for (WebDriver driver : drivers) {
            driver.quit();

            deleteDriverFromList(driver);
            driverMap = new LinkedHashMap<>();
        }
    }

    /**
     * Add a driver to the map, with the thread id as key
     *
     * @param driver Instance of WebDriver
     */
    public static void addDriverToMap(WebDriver driver) {
        Integer threadId = (int) (Thread.currentThread().getId());
        driverMap.put(threadId, driver);
        drivers.add(driver);
    }

    /**
     * Get the driver from the map using the thread id
     *
     * @return Instance of WebDriver
     */
    public static WebDriver getDriverFromMap() {
        Integer threadId = (int) (Thread.currentThread().getId());
        return driverMap.get(threadId);
    }

    public static WebDriver getDriverFromList(int index) {
        return drivers.get(index);
    }

    /**
     * Delete the driver from the map using the thread id
     * @author genosar.dafna
     * @since 29.06.2023
     */
    public static void deleteDriverFromMap() {

        Integer threadId = (int) Thread.currentThread().getId();
        driverMap.put(threadId, null);
    }

    /**
     * Delete the driver from the map using the thread id
     * @author genosar.dafna
     * @since 04.08.2024
     */
    public static void deleteDriverFromMap(Integer threadId) {

        driverMap.put(threadId, null);
    }

    /**
     * Delete the driver from thelist
     * @author genosar.dafna
     * @since 04.08.2024
     */
    public static void deleteDriverFromList(WebDriver driver) {

        drivers = new LinkedList<>(ListUtils.removeItemFromList(drivers, driver));
    }
}


