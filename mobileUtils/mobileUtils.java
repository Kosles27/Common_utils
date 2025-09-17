package mobileUtils;

import drivers.TesnetMobileDriver;
import enumerations.MessageLevel;
import enumerations.MobileScreenDirection;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waitUtils.WaitWrapper;

import java.time.Duration;

import static propertyUtils.PropertyUtils.getGlobalProperty;
import static reportUtils.Report.reportAndLog;
import static systemUtils.SystemCommonUtils.sleep;

public class mobileUtils {

    private static Logger logger= LoggerFactory.getLogger(mobileUtils.class);



    /**
     * Method to swipe from mid screen to another direction looking for an element
     * Possible directions: DOWN,UP,LEFT,RIGHT
     * This method will swipe a quarter screen in chosen direction up to 10 retries
     *
     * @author sela.zvika
     * @since 26.09.2021
     * @param driver TesnetMobileDriver instance
     * @param direction direction of swipe
     * @param elementToFind stop swipe once this element is visible
     * @param extraSwipe in case you want to further center the element you can try an extra swipe
     */
    public static void mobileSwipeTillElementFound(TesnetMobileDriver driver, MobileScreenDirection direction, WebElement elementToFind,boolean extraSwipe){
        mobileSwipeTillElementFound(driver,direction,elementToFind,10,2,extraSwipe);
    }


    /**
     * Method to swipe from mid screen to another direction looking for an element
     * Possible directions: DOWN,UP,LEFT,RIGHT
     * This method will swipe a quarter screen in chosen direction
     *
     * @author sela.zvika
     * @since 26.09.2021
     * @param driver TesnetMobileDriver instance
     * @param direction direction of swipe
     * @param elementToFind stop swipe once this element is visible
     * @param retries num of retries until element is found (>=0)
     * @param extraSwipe in case you want to further center the element you can try an extra swipe
     */
    public static void mobileSwipeTillElementFound(TesnetMobileDriver driver, MobileScreenDirection direction, WebElement elementToFind, int retries,boolean extraSwipe){
        mobileSwipeTillElementFound(driver,direction,elementToFind,retries,2,extraSwipe);
    }

    /**
     * Method to swipe from mid screen to another direction looking for an element
     * Possible directions: DOWN,UP,LEFT,RIGHT
     *
     * @author sela.zvika
     * @since 26.09.2021
     * @param driver TesnetMobileDriver instance
     * @param direction direction of swipe
     * @param elementToFind stop swipe once this element is visible
     * @param retries num of retries until element is found (>=0)
     * @param ratio Swipe Scale: Ratio of 1 means half screen swipe, Ratio of 2 means quarter screen swipe...
     * @param extraSwipe in case you want to further center the element you can try an extra swipe
     */
    public static void mobileSwipeTillElementFound(TesnetMobileDriver driver, MobileScreenDirection direction, WebElement elementToFind, int retries, int ratio, boolean extraSwipe){


        int swipeRetries = 0;
        int timeout = Integer.parseInt(getGlobalProperty("timeout"))/15;
        //TODO: find a generic isDisplayed that works both on Emulator and Real Device
        boolean isFound=WaitWrapper.isElementDisplayed(driver,elementToFind,timeout);
        while (!isFound && swipeRetries<retries)
        {
            singleSwipeScreen(driver,direction,ratio);
            swipeRetries+=1;
            isFound = WaitWrapper.isElementDisplayed(driver,elementToFind,timeout);

        }
        //TODO:consider replacing extra swipe with extraSwipeIfNotCentered
        if (isFound && extraSwipe){
            singleSwipeScreen(driver,direction,ratio);
        }

        if (!isFound){
            reportAndLog("Failed to find element after "+ swipeRetries + " retries", MessageLevel.ERROR);
            throw new Error("Failed to find element after "+ swipeRetries + " retries");
        }


    }

    /**
     * Method to swipe from mid screen to another direction looking for an element
     * Possible directions: DOWN,UP,LEFT,RIGHT
     *
     * @author Yael.Rozenfeld
     * @since 01.11.2021
     * @param driver TesnetMobileDriver instance
     * @param direction direction of swipe
     * @param elementToFind stop swipe once this element is visible
     * @param retries num of retries until element is found (>=0)
     * @param ratio Swipe Scale: Ratio of 1 means half screen swipe, Ratio of 2 means quarter screen swipe...
     * @param extraSwipe in case you want to further center the element you can try an extra swipe
     */
    public static void mobileSwipeTillElementFound(TesnetMobileDriver driver, MobileScreenDirection direction, By elementToFind, int retries, int ratio, boolean extraSwipe){


        int swipeRetries = 0;
        int timeout = Integer.parseInt(getGlobalProperty("timeout"))/15;
        //TODO: find a generic isDisplayed that works both on Emulator and Real Device
        boolean isFound=WaitWrapper.isElementDisplayed(driver,elementToFind,timeout);
        while (!isFound && swipeRetries<retries)
        {
            singleSwipeScreen(driver,direction,ratio);
            swipeRetries+=1;
            isFound = WaitWrapper.isElementDisplayed(driver,elementToFind,timeout);

        }
        //TODO:consider replacing extra swipe with extraSwipeIfNotCentered
        if (isFound && extraSwipe){
            singleSwipeScreen(driver,direction,ratio);
        }

        if (!isFound){
            reportAndLog("Failed to find element after "+ swipeRetries + " retries", MessageLevel.ERROR);
            throw new Error("Failed to find element after "+ swipeRetries + " retries");
        }


    }


    /**
     * Performs swipe from the center of screen
     *  @author sela.zvika
     *  @since 26.09.2021
     *  @param driver TesnetMobileDriver instance
     *  @param direction the direction of swipe
     *  @param ratio Swipe Scale: Ratio of 1 means half screen swipe, Ratio of 2 means quarter screen swipe...
     *
     **/
    private static void singleSwipeScreen(TesnetMobileDriver driver, MobileScreenDirection direction, int ratio) {
        logger.info("singleSwipeScreen(): dir: '" + direction.toString() + "'"); // always log your actions

        // Animation default time (added wait time in case swipe triggers another GUI behavior):
        // final value depends on your app and could be greater
        final int ANIMATION_TIME = 300; // ms
        final int PRESS_TIME = 400; // ms

        PointOption pointOptionStart, pointOptionEnd;

        // init screen variables
        Dimension dims = driver.manage().window().getSize();

        // init start point = center of screen
        pointOptionStart = PointOption.point(dims.width / 2, dims.height / 2);

        // reduce swipe ratio by multiplier, comparing to swipeScreen move

        switch (direction) {
            case DOWN: // center of footer
                pointOptionEnd = PointOption.point(dims.width / 2, (dims.height / 2) - (dims.height / 2) / ratio);
                break;
            case UP: // center of header
                pointOptionEnd = PointOption.point(dims.width / 2, (dims.height / 2) + (dims.height / 2) / ratio);
                break;
            case LEFT: // center of left side
                pointOptionEnd = PointOption.point((dims.width / 2) - (dims.width / 2) / ratio, dims.height / 2);
                break;
            case RIGHT: // center of right side
                pointOptionEnd = PointOption.point((dims.width / 2) + (dims.width / 2) / ratio, dims.height / 2);
                break;
            default:
                throw new IllegalArgumentException("singleSwipeScreen(): dir: '" + direction.toString() + "' NOT supported");
        }

        // execute swipe using TouchAction
        try {
            new TouchAction(driver)
                    .press(pointOptionStart)
                    // a bit more reliable when we add small wait
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(PRESS_TIME)))
                    .moveTo(pointOptionEnd)
                    .release().perform();
        } catch (Exception e) {
            logger.error("singleSwipeScreen(): TouchAction FAILED\n" + e.getMessage());
            return;
        }

        // always allow swipe action to complete
        sleep(ANIMATION_TIME);

    }


    /**
     * This method sends keyboard digits from a numeric String
     * It only works on ANDROID devices
     *
     * @param driver  a TesnetMobileDriver
     * @param numericString the numeric string to type
     * @author zvika.sela
     * @since 29.09.2021
     *
     */
    public static void androidNumericStringToKeyEvents(TesnetMobileDriver driver, String numericString){
        for (char c:numericString.toCharArray()){
            switch (c){
                case '0':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
                    break;
                }
                case '1':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_1));
                    break;
                }
                case '2':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_2));
                    break;
                }
                case '3':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_3));
                    break;
                }
                case '4':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_4));
                    break;
                }
                case '5':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_5));
                    break;
                }
                case '6':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));
                    break;
                }
                case '7':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_7));
                    break;
                }
                case '8':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
                    break;
                }
                case '9':{
                    driver.pressKey(new KeyEvent(AndroidKey.DIGIT_9));
                    break;
                }
                default: {
                    reportAndLog("this method should be provided with numeric String only", MessageLevel.ERROR);
                    throw new Error("this method should be provided with numeric String only");
                }
            }

        }

    }

    /** Select day from calendar
     * press on the calendar next or previous button x times according to a given parameter,
     * afterwards select day in the calendar according to a given parameter
     *
     * @param dayToPickInTheMonth - the element of the day to pick in the calendar
     * @param nextOrBack - the element of the next or back button
     * @param numberOFTimesToClickOnNextOrBack - number of time to click on next / back button in the calendar
     *
     * @author - Lior Umflat
     * @since - 2.11.2021
     **/
    public static void selectDateFromCalendar(WebElement nextOrBack, WebElement dayToPickInTheMonth, int numberOFTimesToClickOnNextOrBack) {

        //click on next/back button according to numberOFTimesToClickOnNextOrBack parameter to reach the desired month
        for (int i = 0; i < numberOFTimesToClickOnNextOrBack; i++) {
            nextOrBack.click();
        }
        //select the desired day in the month
        dayToPickInTheMonth.click();
    }
}
