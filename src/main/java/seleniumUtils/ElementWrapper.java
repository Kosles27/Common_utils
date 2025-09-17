package seleniumUtils;

import Managers.WebDriverInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import constantsUtils.CommonConstants;
import drivers.TesnetWebElement;
import enumerations.MessageLevel;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.ExtentReportUtils;
import waitUtils.WaitWrapper;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static constantsUtils.CommonConstants.EMPTY_STRING;
import static imageUtils.ScreenCaptureUtils.takeScreenShot;
import static objectsUtils.ObjectsUtils.newInstance;
import static reportUtils.Report.reportAndLog;
import static systemUtils.SystemCommonUtils.sleep;

@SuppressWarnings("unused")
public class ElementWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ElementWrapper.class);

    /**
     * Checks if an element is stale
     * @param element the element
     * @return true if the element is stale / false otherwise
     * @author Genosar.dafna
     * @since 06.11.2023
     */
    public static boolean isStaleElement(WebElement element){
        try{
            element.getTagName();
            return false;
        }
        catch(Exception e){
            return true;
        }
    }

    /**
     * Checking if element is displayed after waiting for a number of seconds as sent to the method
     *
     * @param driver           WebDriver instance
     * @param element          WebElement to check if displayed
     * @param timeoutInSeconds Maximum number of seconds to wait for element to appear
     * @return true if element is displayed. False otherwise
     */
    public static boolean isElementDisplayedAfterWait(WebDriver driver, WebElement element, int timeoutInSeconds) {
        try {
            WaitWrapper.waitForElementToAppear(driver, element, timeoutInSeconds).isDisplayed();
            logger.info("Element " + element + " appears on page");
            return true;
        } catch (TimeoutException | NoSuchElementException exception) {
            reportAndLog("Element " + element + " does not appear on page",MessageLevel.INFO);
            return false;
        }
    }

    /**
     * Get the absolute XPath using JavaScript
     * @param driver driver
     * @param element element
     * @return the absolute XPath using JavaScript
     * @author genosar.dafna
     * @since 09.09.2024
     */
    public static String getAbsoluteXpath(WebDriver driver, WebElement element){
        // Get the absolute XPath using JavaScript
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        return (String) jsExecutor.executeScript(
                "function getXPath(el) {" +
                        "   if (el.tagName === 'HTML') return '/html';" +
                        "   if (el === document.body) return '/html/body';" +
                        "   var ix = 0;" +
                        "   var siblings = el.parentNode.childNodes;" +
                        "   for (var i = 0; i < siblings.length; i++) {" +
                        "       var sibling = siblings[i];" +
                        "       if (sibling === el) return getXPath(el.parentNode) + '/' + el.tagName.toLowerCase() + '[' + (ix + 1) + ']';" +
                        "       if (sibling.nodeType === 1 && sibling.tagName === el.tagName) ix++;" +
                        "   }" +
                        "}" +
                        "return getXPath(arguments[0]);", element);
    }

    /**
     * Get the Xpath that was used to find the element. Only valid for elements that were searched and found by xpath
     * @param element the element
     * @return the Xpath that was used to find the element. If the element was not found by xpath it will return null.
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static String getXpathUsedToFindElement(WebElement element)
    {
        String elementString = element.toString();
        if(!elementString.contains("xpath"))
        {
            logger.info("Element does not contain xpath");
            return null;
        }
        String[] xpathParts = elementString.split(" -> xpath: ");

        String xpath = "";

        for(int i=1; i< xpathParts.length; i++)
        {
            String xpathPart = xpathParts[i];
            if(xpathPart.startsWith("."))
                xpathPart = xpathPart.substring(1);

            if(i == xpathParts.length-1)
                xpathPart = xpathPart.substring(0, xpathPart.length()-1);
            else
                xpathPart = xpathPart.substring(0, xpathPart.length()-2);
            xpath += xpathPart;
        }
        return xpath;
    }

    /**
     * Convert a list of objects to a different object type.
     * The new object type must contain a constructor that receives the driver and the local WebElement of the object/class. (See example in CardComponentBase)
     * @param classType Class type to convert to. Example Person.class
     * @param listToConvert list to convert
     * @param <T> Type
     * @param <E> Elements
     * @return The converted list
     * @author Dafna Genosar
     * @since 24.11.2021
     */
    public static <T, E> List<T> convertObjectsList(Class<T> classType, List<E> listToConvert) {

        List<T> itemsToReturn = new ArrayList<>(listToConvert.size());

        for (int i=0; i< listToConvert.size(); i++)
        {
            WebElement elementToConvert;

            if(listToConvert instanceof WebElement)
                elementToConvert = ((WebElement)listToConvert.get(i));
            else
                elementToConvert = ((TesnetWebElement)listToConvert.get(i)).getLocalElement();

            itemsToReturn.add(newInstance(classType, elementToConvert));
        }

        return itemsToReturn;
    }

    /**
     * Check if element exists (not null) and is displayed on page
     * @param element the element to check
     * @return true / false if the element exists and displayed
     * @author Dafna Genosar
     * @since 22.11.2021
     * @since 15.10.2024
     */
    public static boolean elementExistsAndDisplayed(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        }
        catch (Throwable e) {
            return false;
        }
    }

    /**
     * Return the element's parent element
     * @param element the element
     * @return the element's parent element
     * @author Dafna Genosar
     * @since 20.12.2022
     */
    public WebElement getParentElement(WebElement element)
    {
        return element.findElement(By.xpath("./.."));
    }

    /**
     * Checking if element is displayed after waiting for a number of seconds as sent to the method and clicking it
     * @param driver           WebDriver instance
     * @param elem             WebElement to click
     * @param timeoutInSeconds Maximum number of seconds to wait for element to appear
     * @author rozenfeld.yael
     * @since unknown
     * @author genosar.dafna
     * @since 03.03.2024
     */
    public static void waitForVisibilityAndClick(WebDriver driver, WebElement elem, int timeoutInSeconds) {
        try{
            WaitWrapper.waitForElementToAppear(driver, elem, timeoutInSeconds);
        }
        catch(Exception e){
            throw new Error(e.getMessage());
        }
        logger.info("Element " + elem + " appears on page");
        click(driver, elem, "Element to click on", timeoutInSeconds);
        logger.info("Clicked on element " + elem);
    }

    /**
     * Checking if element is displayed after waiting for a number of seconds as sent to the method and inserting
     * text to it
     * @param driver           WebDriver instance
     * @param elem             WebElement to click
     * @param text             Text to insert to element
     * @param timeoutInSeconds Maximum number of seconds to wait for element to appear
     * @author plot.ofek
     * @since unknown
     * @author genosar.dafna
     * @since 03.03.2024
     */
    public static void waitForVisibilityAndInsertText(WebDriver driver, WebElement elem, String text, int timeoutInSeconds){
        try {
            WaitWrapper.waitForElementToAppear(driver, elem, timeoutInSeconds);
        }
        catch(Exception e){
           throw new Error(e.getMessage());
        }
        logger.info("Element " + elem + " appears on page");
        elem.sendKeys(text);
        elem.sendKeys(Keys.ENTER);
        logger.info("Inserted to element " + elem + " text: " + text);
    }

    /**
     * Checking that one of the webElements in list contains attribute with value as sent to method
     *
     * @param elemList       List of WebElements
     * @param elemAtrribute  Attribute to search for
     * @param attributeValue Attribute value to search for
     * @return true if element is found in list. false otherwise
     */
    public static boolean isElementWithAttributeInList(List<WebElement> elemList, String elemAtrribute, String attributeValue) {
        try {
            for (WebElement elem : elemList) {
                if (getAttribute(elem, elemAtrribute, "Element in list").contains(attributeValue)) {
                    logger.info("Attribute: " + elemAtrribute + " with value: " + attributeValue + "exists in element: " + elem);
                    return true;
                }
            }
            logger.info("Attribute: " + elemAtrribute + " with value: " + attributeValue + "does not exist in list : " + elemList);
            return false;
        } catch (NullPointerException e) {
            logger.error("Caught exception " + e);
            ExtentReportUtils.extentLogger(LogStatus.INFO, "Failed to find element in list by attribute" + elemAtrribute  + " and value " + attributeValue);
            return false;
        }
    }

    /**
     * Clicking on WebElement after waiting for a maximum number of seconds as sent to the method
     * @param driver      WebDriver instance
     * @param elem        WebElement to click
     * @param description Description of the element
     * @param timeout     Maximum number of seconds to wait
     * @author rozenfeld.yael
     * @since unknown
     * @author genosar.dafna
     * @since 03.03.2024
     */
    public static void click(WebDriver driver, WebElement elem, String description, int timeout) {
        logger.info("Waiting until " + description + " is clickable");
        try {
            WaitWrapper.waitForElementTobeClickable(driver, elem, description, timeout).click();
        }
        catch(Exception e){
            throw new Error(e.getMessage());
        }
        logger.info("Clicked on " + description);
    }

    /**
     * Try to click on WebElement. If not successful, catch the exception
     *
     * @param driver           WebDriver instance
     * @param elem             WebElement to click
     * @param description      Description of the element
     * @param timeOutInSeconds Maximum number of seconds to wait
     */
    public static void clickAndCatchException(WebDriver driver, WebElement elem, String description, int timeOutInSeconds) {
        try {
            click(driver, elem, description, timeOutInSeconds);
        } catch (Exception e) {
            logger.error("Caught Exception: " + e);

        }
    }

    /**
     * Get WebElement's attribute's value
     *
     * @param elem               WebElement to look for its attribute
     * @param attribute          Attribute to look for its value
     * @param elementDescription Description of the WebElement
     * @return Attribute value
     */
    public static String getAttribute(WebElement elem, String attribute, String elementDescription) {
        logger.info("Getting value of " + attribute + " attribute of " + elementDescription);
        String value = elem.getDomAttribute(attribute);
        logger.info("Value of attribute is: " + value);
        return value;
    }

    /**
     * Checks if an attribute exists in an element
     * @param elem the element to check
     * @param attribute the attribute name
     * @return true/false if the element has the attribute
     * @author Dafna Genosar
     * @since 9.11.2021
     * @author Dafna Genosar
     * @since 02.03.2025
     */
    public static boolean attributeExists(WebElement elem, String attribute)
    {
        try
        {
            String returnAttribute = elem.getDomAttribute(attribute);
            return returnAttribute != null && !returnAttribute.equals("");
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    /**
     * Get WebElement's text
     *
     * @param driver           WebDriver instance
     * @param elem             WebElement to look for its text
     * @param description      Description of the WebElement
     * @param timeOutInSeconds Maximum number of seconds to wait
     * @return text of element
     */
    public static String getText(WebDriver driver, WebElement elem, String description, int timeOutInSeconds) {
        String value = null;
        try {
            logger.info("getting text of: " + description);
            WaitWrapper.waitForElementToAppear(driver, elem, timeOutInSeconds);
            value = elem.getText();
            logger.info("Text of " + description + " is: " + value);
        } catch (Exception e) {
            reportAndLog("Tried to get text of " + description + " and Caught Exception: " + e,MessageLevel.INFO);
        }
        return value;
    }

    /**
     * Clear text in WebElement sent to method
     * @param driver           WebDriver instance
     * @param elem             WebElement to clear
     * @param description      Description of the WebElement
     * @param timeOutInSeconds Maximum number of seconds to wait
     */
    public static void clearText(WebDriver driver, WebElement elem, String description, int timeOutInSeconds) {
        do {
            logger.info("Clearing text of: " + description);
            try {
                WaitWrapper.waitForElementToAppear(driver, elem, timeOutInSeconds).clear();
            }
            catch(Exception e){
                throw new Error(String.format("Could not find '%s' element after %d seconds", description, timeOutInSeconds));
            }

            logger.info("Element " + description + " was cleared");
        } while (!elem.getText().equals(EMPTY_STRING));
    }

    /**
     * Add text to the WebElement sent to the method
     * @param driver             WebDriver instance
     * @param elem               WebElement to update with text
     * @param text               Text to insert to WebElement
     * @param elementDescription Description of the WebElement
     * @param timeOutInSeconds   Maximum number of seconds to wait
     * @return true if text was updated. false otherwise
     */
    public static boolean sendKeys(WebDriver driver, WebElement elem, String text, String elementDescription, int timeOutInSeconds) {
        boolean noException = true;

        try {
            WaitWrapper.waitForElementToAppear(driver, elem, timeOutInSeconds).sendKeys(text);
            logger.info("Element " + elementDescription + " was updated with " + text);
        } catch (Exception e) {
            noException = false;
            logger.info("Tried to update " + elementDescription + " with value: " + text + " and Caught Exception: " + e);
        }
        return noException;
    }

    /**
     * Type text letter by letter, as a user
     * @param text text to type
     * @author genosar.dafna
     * @since 21.06.2023
     */
    public static void typeText(WebElement element, String text){

        typeText(element, text, 200);
    }

    /**
     * Type text letter by letter, as a user
     * @param text text to type
     * @author genosar.dafna
     * @since 05.12.2023
     */
    public static void typeText(WebElement element, String text, int milliSecBetweenEachLetter){

        logger.debug("element using : typeText " + element.toString() + " text to type = " + text);

        for (int i = 0; i < text.length(); i++){
            char c = text.charAt(i);
            String s = new StringBuilder().append(c).toString();
            element.sendKeys(s);
            sleep(milliSecBetweenEachLetter);
        }
    }

    /**
     * Set the value in the element (input) using JS executor
     * @param value value to set
     * @author genosar.dafna
     * @since 21.06.2023
     */
    public static void setValueByJsExecutor(WebElement element, String value){

        logger.debug("element using : setValueByJsExecutor " + element.toString() + " value to set = " + value);
        JavascriptExecutor jse = (JavascriptExecutor) WebDriverInstanceManager.getDriverFromMap();
        jse.executeScript("arguments[0].setAttribute('value', '" + value +"')", element);
    }


    /**
     * Select all the objects inside the WebElement and delete them
     *
     * @param elem               WebElement with objects to delete
     * @param elementDescription Description of the WebElement
     */
    public static void selectAllAndPressOnDeleteKey(WebElement elem, String elementDescription) {
        elem.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        elem.sendKeys(Keys.chord(Keys.DELETE));
        logger.info("Cleared all content of " + elementDescription);
    }

    /**
     * Retrieving WebElement with text as sent to method out of list of elements as sent to method
     *
     * @param driver           WebDriver instance
     * @param text             Text to look for in each element in list until found
     * @param list             List of WebElements
     * @param timeOutInSeconds Maximum number of seconds to wait
     * @return Found WebElement. Null if no element is found
     */
    private static WebElement getWebElementFromListWithCertainText(WebDriver driver, String text, List<WebElement> list, int timeOutInSeconds) {
        for (WebElement webElement : list) {
            if (getText(driver, webElement, "Element in pickList", timeOutInSeconds).contains(text)) {
                logger.info("Element was found in list");
                return webElement;
            }
        }

        logger.info("Element was not found in list");
        return null;
    }

    /**
     * Retrieving WebElement with text as sent to method out of list of elements as sent to method
     * @param driver           WebDriver instance
     * @param text             Text to look for in each element in list until found
     * @param list             List of WebElements
     * @param timeOutInSeconds Maximum number of seconds to wait
     * @return Found WebElement
     */
    public static WebElement findByTextElementInList(WebDriver driver, String text, List<WebElement> list, int timeOutInSeconds) {
        try {
            WaitWrapper.waitForElementToAppear(driver, list.get(0), timeOutInSeconds);
        }
        catch(Exception e){
            throw new Error(e.getMessage());
        }
        return getWebElementFromListWithCertainText(driver, text, list, timeOutInSeconds);
    }

    /**
     * Press on WebElement in picklist or dropdown box that has text as sent to method
     *
     * @param driver           WebDriver instance
     * @param text             Text to look for in each element in list until found
     * @param list             List of WebElements
     * @param timeOutInSeconds Maximum number of seconds to wait
     */
    public static void selectByTextElementInPickListOrDropDown(WebDriver driver, String text, List<WebElement> list, int timeOutInSeconds) {
        ActionsWrapper.press(driver, findByTextElementInList(driver, text, list, timeOutInSeconds));
    }

    /**
     * Click on WebElement in picklist or dropdown box that has text as sent to method
     *
     * @param driver                       WebDriver instance
     * @param XpathStringForListOfElements xpath locator string of list of WebElements
     * @param text                         Text to look for in each element in list until found
     * @param timeOutInSeconds             Maximum number of seconds to wait
     */
    public static void selectByTextElementInPickListOrDropDown(WebDriver driver, String XpathStringForListOfElements, String text, int timeOutInSeconds) {
        List<WebElement> options = driver.findElements(By.xpath(XpathStringForListOfElements));
        click(driver, findByTextElementInList(driver, text, options, timeOutInSeconds), "Element in list with text: " + text, timeOutInSeconds);
    }

    /**
     * Pick an option of Select Box by its value as sent to method
     *
     * @param elem          Select WebElement
     * @param valueFromList value to look for in options
     */
    public static void selectFromSelectBoxByValue(WebElement elem, String valueFromList) {
        try {
            Select myValue = new Select(elem);
            myValue.selectByValue(valueFromList);
            logger.info(valueFromList + " " + "Element selected!");
        } catch (Exception e) {
            logger.info("Element not found");
        }
    }

    /**
     * Pick an option of Select Box by its index as sent to method
     *
     * @param elem     Select WebElement
     * @param indexNum index of option to choose
     */
    public static void selectFromSelectBoxByIndex(WebElement elem, int indexNum) {
        try {
            Select myValue = new Select(elem);
            myValue.selectByIndex(indexNum);
        } catch (Exception e) {
            logger.info(elem + "Element not found ");
        }
    }

    /**
     * Retrieving a random option of a Select Box
     *
     * @param driver           WebDriver instance
     * @param selectBox        WebElement of Select Box
     * @param timeOutInSeconds Maximum number of seconds to wait
     * @return random element of option
     */
    public static WebElement getRandomOptionFromSelectBox(WebDriver driver, WebElement selectBox, int timeOutInSeconds) {
        WebElement selectedOption = null;
        try {
            click(driver, selectBox, "Select box to choose random value from", timeOutInSeconds);
            List<WebElement> myList = selectBox.findElements(By.xpath(".//option"));
            Random rand = new Random();
            int myRandomOption = rand.nextInt(myList.size());
            selectedOption = myList.get(myRandomOption);
        } catch (Exception e) {
            logger.info("Failiure in selectRandomOptionFromDropDown function: " + e);
        }

        return selectedOption;
    }

    /**
     * Method to highlight an element with a yellow background
     * @param driver  driver
     * @param element the element to highlight
     * @param bgColor optional background color. Can be a name of a color, like RED, or like RGB(225,100,100)
     * @param borderColor optional background color style text. for example: 3px solid red
     * @author genosar.dafna
     * @since 20.08.2024
     */
    public static void highlightElement(WebDriver driver, WebElement element, @Nullable String bgColor, @Nullable String borderColor) {

        String bgColorString = "";
        String borderColorString = "";
        if(bgColor != null){
            bgColorString = String.format("arguments[0].style.backgroundColor='%s'; ", bgColor);
        }
        if(borderColor != null){
            borderColorString = String.format("arguments[0].style.border='%s'; ", borderColor);
        }

        String script = bgColorString + borderColorString;

        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Change the background color to yellow and add a red border
        js.executeScript(script, element);
    }

    /**
     * Method to remove highlight from an element
     * @param driver  driver
     * @param element the highlighted element
     * @param removeBgColor true to reset the background color to nothing / false will not change the background
     * @param removeBorder true to reset the border style to nothing / false will not change the border
     * @author genosar.dafna
     * @since 20.08.2024
     */
    public static void removeHighlight(WebDriver driver, WebElement element, boolean removeBgColor, boolean removeBorder) {

        String bgColorString = "";
        String borderColorString = "";

        if(removeBgColor)
            bgColorString = "arguments[0].style.backgroundColor=''; ";
        if(removeBorder)
            borderColorString = "arguments[0].style.border=''; ";

        String script = bgColorString + borderColorString;

        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Reset the background color and border properties
        js.executeScript(script, element);
    }

    /**
     * Clicking on a random option of a Select Box and retrieving its text
     *
     * @param driver           WebDriver instance
     * @param selectBox        WebElement of Select Box
     * @param timeOutInSeconds Maximum number of seconds to wait
     * @return text of chosen option
     */
    public static String clickOnAndGetTextOfRandomOptionFromSelectBox(WebDriver driver, WebElement selectBox, int timeOutInSeconds) {
        WebElement option = getRandomOptionFromSelectBox(driver, selectBox, timeOutInSeconds);
        click(driver, option, "Selected option from Select Box", timeOutInSeconds);
        return getText(driver, option, "Selected option from Select Box", timeOutInSeconds);
    }

    /**
     * Click on an element by using JavascriptExecutor command
     * @param driver the driver
     * @param element the element to click on
     * @author Dafna Genosar
     * @since 19.01.2022
     */
    public static void clickElementByJavascriptExecutor(WebDriver driver, WebElement element)
    {
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Scroll all the way down the element's height
     * @param driver the driver
     * @param element the element to scroll. Can also be an inner div window
     * @author Dafna Genosar
     * @since 02.01.2022
     */
    public static void scrollDownElementHeight(WebDriver driver, WebElement element)
    {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop += arguments[0].scrollHeight;", element);
    }

    /**
     * Scroll all the way up the element's height
     * @param driver the driver
     * @param element the element to scroll. Can also be an inner div window
     * @author Dafna Genosar
     * @since 10.03.2024
     */
    public static void scrollUpElementHeight(WebDriver driver, WebElement element)
    {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTo(0,0);", element);
    }

    /**
     * Scroll the scrollable element up/down by the given number of pixels
     * @param driver driver
     * @param scrollableElement the element to scroll - note that sometimes the scrollable element is the wrapping element of your element
     * @param numOfPixelsToScrollHorizontally number of pixels to scroll horizontally. To scroll up - set a positive number of pixels, to scroll down set a negative number
     * @param numOfPixelsToScrollVertically number of pixels to scroll vertically.
     * @author genosar.dafna
     * @since 20.08.2024
     */
    public static void scrollBy(WebDriver driver, WebElement scrollableElement, int numOfPixelsToScrollHorizontally, int numOfPixelsToScrollVertically){

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollBy(arguments[1], arguments[2]);", new Object[]{scrollableElement, numOfPixelsToScrollHorizontally, numOfPixelsToScrollVertically});
    }

    /**
     * Scroll teh scrollable element to the given x, y point
     * @param driver the driver
     * @param x the x point to scroll to
     * @param y the y point to scroll to
     * @author Dafna Genosar
     * @since 20.08.2024
     */
    public static void scrollTo(WebDriver driver, WebElement scrollableElement, int x, int y)
    {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTo(arguments[1], arguments[2]);", scrollableElement, x, y);
    }

    /**
     * Scroll until the element is in view
     * @param driver the driver
     * @param elementToScroll the outer element to scroll
     * @param elementToScrollTo the element to scroll to
     * @author Dafna Genosar
     * @since 20.12.2022
     */
    public static void scrollToElementWithinAnElement(WebDriver driver, WebElement elementToScroll, WebElement elementToScrollTo)
    {
        int x = elementToScrollTo.getRect().x;
        int y = elementToScrollTo.getRect().y;
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTo(arguments[1], arguments[2]);", elementToScroll, x, y);
    }

    /**
     * Scroll until the element is in view
     * @param driver the driver
     * @param element the element to scroll to
     * @author Dafna Genosar
     * @since 02.01.2021
     */
    public static void scrollElementIntoView(WebDriver driver, WebElement element)
    {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Scroll to specific location on screen according to element location
     *
     * @param Element WebElement to scroll to
     */
    public static void scrollToElement(WebDriver driver, WebElement Element) {
        ((JavascriptExecutor) driver).executeScript("scroll" + Element.getLocation());
    }

    /**
     * @return true if the element is scrolled all the way down / false otherwise
     * please note that the element must be the scrollable element
     * @author genosar.dafna
     * @since 10.03.2024
     */
    public static boolean isScrolledToBottom(WebDriver driver, WebElement scrollableElement){
        return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollTop + arguments[0].offsetHeight == arguments[0].scrollHeight;", scrollableElement);
    }

    /**
     * scroll to element's most right
     * @param WrapperElement WrapperWebElement to scroll it most right
     * @author Yael Rozenfeld
     * @since 8.6.2021
     */
    public static void scrollWrapperElementRight(WebDriver driver, WebElement WrapperElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollLeft = arguments[0].offsetWidth", WrapperElement);
    }

    /**
     * scroll to webElement horizontally by pixel
     * @param driver driver
     * @param WrapperElement - WrapperWebElement to scroll it
     * @param pixel - positive number for scrolling right, negative number for scrolling left
     * @author Yael Rozenfeld
     * @since 8.6.2021
     */
    public static void scrollWrapperElementHorizontalByPixel(WebDriver driver, WebElement WrapperElement,int pixel) {
        String plusOrMinus = (pixel>0) ? "+":"-";
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollLeft "+plusOrMinus+"= "+Math.abs(pixel), WrapperElement);

}


    /**
     * Method accepts an index of a row in a table on page and returns all of the cells in a specific row
     * @param tableRowList The list of rows in the table on page
     * @param rowIndex the index of the desired row
     * @return a list of all the cells in the desired row
     */
    public static List<WebElement> getAllCellsInRow(List<WebElement> tableRowList, int rowIndex)
    {
        return tableRowList.get(rowIndex).findElements(By.tagName("td"));
    }

    /**
     * Method accepts an index of a row in a table on page and returns a specific cell in in a specific row
     * @param tableRowList The list of rows in the table on page
     * @param rowIndex the index of the desired row
     * @param cellIndex the index of the desired cell
     * @return The desired cell
     */
    public static WebElement getSpecificCellInRow(List<WebElement> tableRowList, int rowIndex, int cellIndex)
    {
        return getAllCellsInRow(tableRowList, rowIndex).get(cellIndex);
    }


    /**
     * Clear value from text box
     * instead of clear function - simulates better manual operation
     * @param elem WebElement type txt
     * @author Yael Rozenfeld
     * @since 7.06.21
     */
    public static void clearField(WebElement elem) {
        elem.click();
        elem.sendKeys(Keys.CONTROL+"a");
        elem.sendKeys(Keys.DELETE);
        elem.sendKeys( Keys.TAB);
    }

    /**
     * Return true if the element is enabled or false if disabled (or stale)
     * @param element the element
     * @return true if the element is enabled or false if disabled (or stale)
     * @author genosar.dafna
     * @since 04.04.2023
     */
    public boolean isEnabled(WebElement element){
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *  Select element from pick list
     * @param tries Number of retries to select the element
     * @param driver Instance of WebDriver
     * @param toTerminate Indication if application should terminate on fail
     * @param field filed to pick element from
     * @param description description of field
     * @param value value to select from pick list
     * @param list list of options in pick list
     * @param exactMatch true - indication if value should be pick only if found exactly in list. false - pick the first value from the pick list
     * @return true if value pick. false if not
     */

    public static boolean selectElementInPickList(int tries, WebDriver driver, boolean toTerminate, WebElement field, String description, String value, List<WebElement> list, boolean exactMatch)
    {
        reportAndLog("STARTING" + " " + Thread.currentThread().getStackTrace()[1].getMethodName(), MessageLevel.INFO);

        boolean noException = true;
        try
        {
            List<WebElement> innerList, updataedList;

            WebDriverWait myWait = new WebDriverWait(driver, Duration.ofSeconds(1));

            int size;


            updataedList = list;
         //  Waiting up to 30 second for the first element in the list to not be stale
            try
            {
                myWait.until(ExpectedConditions.stalenessOf(updataedList.get(0)));

                tries++;
                if (tries<=3)
                {
                    sleep(10000);
                    return selectElementInPickList(tries, driver, toTerminate, field, description, value, updataedList, exactMatch);
                }
                else
                {
                    reportAndLog("Tried 3 times but could not pick element in list",MessageLevel.ERROR);
                    return false;
                }

            }
            catch (TimeoutException x)
            {
                // Do nothing and continue

            }

            myWait.until(ExpectedConditions.visibilityOf(list.get(0)));
            innerList = list;
            size = innerList.size();	//Number of options.

            String option;

            if (!exactMatch)
            {
                ActionsWrapper.press(driver, innerList.get(0));
            }
            else
            {
                for (int x=0;x<size;x++)
                {
                    int loopTries = 0;
                    do
                    {
                        option = innerList.get(x).getText();
                        loopTries++;
                        System.out.println("loopTries= "+loopTries);
                        sleep(300);
                    } while (option.equals(CommonConstants.EMPTY_STRING) && loopTries < 30);
                    System.out.println(option);
                    if (option.contains(value))
                    {
                        System.out.println("before click");
                        ActionsWrapper.press(driver, innerList.get(x));
                        sleep(1000);
                        System.out.println("after click");
                        break;
                    }
                    System.out.println("outside loop");
                }
            }
            reportAndLog("ENDING" + " " + Thread.currentThread().getStackTrace()[1].getMethodName(), MessageLevel.INFO);

        }
        catch(Exception e)
        {
            noException = false;
            System.out.println("Caught Exception: " + e);
            e.printStackTrace();
            ExtentReportUtils.extentLogger(LogStatus.WARNING, "PickList selection failed. Caught Exception: " + e);
            ExtentReportUtils.attachScreenshotToExtentReport(takeScreenShot(driver));
        }
        return noException;

    }

    /**
     * get a WebElements List and return List with text of the webElements
     * @param lisOfWebElements - List of WebElements
     * @return List with text of the webElements
     * @author Yael Rozenfeld
     * @since 1.12.2021
     */
    public static List<String>  getWebElementsText(List<WebElement> lisOfWebElements){
        List<String> webElementsText = new ArrayList<>();
        for(WebElement element:lisOfWebElements) {
            webElementsText.add(element.getText());
        }
        return webElementsText;
    }
}
