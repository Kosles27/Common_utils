package waitUtils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seleniumUtils.ElementWrapper;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class WaitWrapper
{
    private static final Logger logger = LoggerFactory.getLogger(WaitWrapper.class);

    /**
     * @param elementNameForLogPurpose Optional name of element(s) for log/report purpose
     * @param timeoutInSeconds the given timeout
     * @param errorActionText the error action text to display, like 'could not be found', 'could not be clicked'
     * @param e the exception or throwable
     * @return the full error message to display in the report
     * @author genosar.dafna
     * @since 28.02.2024
     * @since 10.11.2024
     */
    private static String getErrorMessage(@Nullable String elementNameForLogPurpose, @Nullable Integer timeoutInSeconds, String errorActionText, @Nullable Throwable e){

        String timeoutInSecondsText;
        if(timeoutInSeconds != null)
            timeoutInSecondsText = timeoutInSeconds + " seconds";
        else
            timeoutInSecondsText = "the given wait time";

        String elementNameForLogPurposeText = (elementNameForLogPurpose != null)? elementNameForLogPurpose: "The element(s)";

        if(e == null)
            return String.format("%s %s after %s", elementNameForLogPurposeText, errorActionText, timeoutInSecondsText);

        return String.format("%s %s after %s<br><b>Error:</b> %s", elementNameForLogPurposeText, errorActionText, timeoutInSecondsText, e.getMessage());
    }

    private static String getElementNotFoundErrorMessage(@Nullable String elementNameForLogPurpose, @Nullable Integer timeoutInSeconds, Throwable e){

        return getErrorMessage(elementNameForLogPurpose, timeoutInSeconds, "could not be found", e);
    }

    private static String getElementFoundErrorMessage(@Nullable String elementNameForLogPurpose, @Nullable Integer timeoutInSeconds, @Nullable Throwable e){

        return getErrorMessage(elementNameForLogPurpose, timeoutInSeconds, "could still be found", e);
    }

    private static String getElementNotClickableErrorMessage(@Nullable String elementNameForLogPurpose, @Nullable Integer timeoutInSeconds, Throwable e){

        return getErrorMessage(elementNameForLogPurpose, timeoutInSeconds, "could not be clicked", e);
    }

    /**
     * wait for the element to disappear
     *
     * @param elem - element that we wait to disappear
     * @param timeout - timeout in seconds
     * @author Nir.Gallnar
     * @since 19.05.2021
     * @return True if the elem has disappeared after <timeout> seconds, else false
     */
    public static boolean waitForElementToDisappear(WebElement elem, int timeout) {
        boolean result = false;
        for (int i = 0; i < timeout; i++) {

            try {
                elem.isDisplayed();
                Thread.sleep(1000);
            } catch (Throwable e) {
                result = true;
                break;
            }
        }
        //if we are here than result is set

        return result;

    }

    /**
     * Waiting for element to appear on page
     * @param driver WebDriver instance
     * @param element WebElement to appear
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author plot.ofek
     * @since unknown
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static WebElement waitForElementToAppear(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOf(element));
        }
        catch (Throwable e){
            throw new Error(getElementNotFoundErrorMessage(null, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for element to appear on page
     * @param driver WebDriver instance
     * @param element WebElement to appear
     * @param elementName name of element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public static WebElement waitForElementToAppear(WebDriver driver, WebElement element, String elementName, int timeoutInSeconds)
    {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOf(element));
        }
        catch (Throwable e){
            throw new Error(getElementNotFoundErrorMessage(elementName, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for WebElement to be invisible
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForElementToDisappear(WebDriver driver, By by, int timeoutInSeconds)
    {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(ExpectedConditions.invisibilityOfElementLocated(by));
        }
        catch (NoSuchElementException e)
        {
            return true;
        }
    }

    /**
     * Waiting for All WebElements in the list to be invisible
     * @param driver WebDriver instance
     * @param elements - A list of element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 08.05.2024
     */
    public static boolean waitForAllElementsToDisappear(WebDriver driver, List<WebElement> elements, int timeoutInSeconds)
    {
        try{
            return (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(ExpectedConditions.invisibilityOfAllElements(elements));
        }
        catch (Exception e){
            return true;
        }
    }

    /**
     * Waiting for All WebElements in the list to be invisible
     * @param driver WebDriver instance
     * @param elements - A list of element
     * @param elementsName name of elements
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 10.11.2024
     */
    public static boolean waitForAllElementsToDisappear(WebDriver driver, List<WebElement> elements, String elementsName, int timeoutInSeconds)
    {
        boolean elementsDisappeared;
        try{
            elementsDisappeared = (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(ExpectedConditions.invisibilityOfAllElements(elements));
        }
        catch (Exception e){
            elementsDisappeared = true;
        }

        if(!elementsDisappeared){
            throw new Error(getElementFoundErrorMessage(elementsName, timeoutInSeconds, null));
        }
        return elementsDisappeared;
    }

    /**
     * Waiting for All WebElements in the list to be invisible
     * @param driver WebDriver instance
     * @param elements - The elements, separated by comma
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 08.05.2024
     */
    public static boolean waitForAllElementsToDisappear(WebDriver driver, int timeoutInSeconds, WebElement... elements)
    {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(ExpectedConditions.invisibilityOfAllElements(elements));
        }
        catch (Exception e){
            return true;
        }
    }

    /**
     * Waiting for element to disappear from page
     * @param driver WebDriver instance
     * @param element WebElement to disappear
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return boolean (true if element disappeared, false otherwise).
     * @author sela.Zvika
     * @since unknown
     * @author genosar.dafna
     * @since 15.10.2024
     */
    public static boolean waitForElementToDisappear(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        try {
            if(!ElementWrapper.elementExistsAndDisplayed(element))
                return true;

            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.invisibilityOf(element));
        }
        catch (NoSuchElementException e)
        {
            return true;
        }
        catch (TimeoutException e)
        {
            return !ElementWrapper.elementExistsAndDisplayed(element);
        }
        catch (Exception e)
        {
            throw new Error(String.format("Element did not disappear after %d seconds<br><b>Error</b> %s", timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Waiting for WebElement to be clickable
     * @param driver WebDriver instance
     * @param element WebElement to be clickable
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @param elementNameForLogPurposeText name of the element for report/log purpose, like, "Schedule page header", "VVL dropdown"
     * @return WebElement we waited for
     * @author genosar.dafna
     * @since 29.02.2024
     */
    public static WebElement waitForElementTobeClickable(WebDriver driver, WebElement element, String elementNameForLogPurposeText, int timeoutInSeconds)
    {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.elementToBeClickable(element));
        }
        catch(Exception e){
            throw new Error(getElementNotClickableErrorMessage(elementNameForLogPurposeText, timeoutInSeconds, e));
        }
    }


    /**
     * Waiting for WebElement to be clickable
     * @param driver WebDriver instance
     * @param element WebElement to be clickable
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author lotem.ofek
     */
    public static WebElement waitForElementTobeClickable(WebDriver driver, WebElement element, int timeoutInSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waiting for page title to be displayed, then matching it with the given param
     * Might not work when executing headless browser
     * @param driver WebDriver instance
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @param pageTitle The exact page title string as presented in the <title></title> tag
     * @return boolean value indicates if the title displayed\matches or not
     * @author mor.liran
     * @since unknown
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static boolean waitForPageTitleToBeDisplayed(WebDriver driver, int timeoutInSeconds, String pageTitle) {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.titleIs(pageTitle));
        }
        catch (Throwable e){
            throw new Error(getElementNotFoundErrorMessage(String.format("Page title '%s'", pageTitle), timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for page title to contains the given param
     * Might not work when executing headless browser
     * @param driver WebDriver instance
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @param pageTitle The page title string or part of it as presented in the <title></title> tag
     * @return boolean value indicates if the title displayed\matches or not
     * @author genosar.dafna
     * @since 29.06.2022
     */
    public static boolean waitForPageTitleToContain(WebDriver driver, String pageTitle, int timeoutInSeconds) {
        logger.info(String.format("Waiting for page title to contain '%s'", pageTitle));
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.titleContains(pageTitle));
        }
        catch (Exception e)
        {
            throw new Error(String.format("Page title did not contain '%s' after %d seconds. <br><b>Error:</b> %s", pageTitle, timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Waiting for element to found by By object on page
     * @param driver WebDriver instance
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author rozenfeld.yael
     * @since unknown
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static WebElement waitForVisibilityOfElementLocated(WebDriver driver, By by, int timeoutInSeconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfElementLocated(by));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(null, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for element to found by By object on page
     * @param driver WebDriver instance
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static WebElement waitForVisibilityOfElementLocated(WebDriver driver, By by, @Nullable String elementNameForLogForLogPurpose, int timeoutInSeconds)
    {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfElementLocated(by));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(elementNameForLogForLogPurpose, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for element to found under another element by a locator
     * @param driver WebDriver instance
     * @param rootElement element to search under
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author genosar.dafna
     * @since 18.07.2022
     * @since 28.02.2024
     */
    public static WebElement waitForVisibilityOfElementLocated(WebDriver driver, WebElement rootElement, By by, int timeoutInSeconds)
    {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(visibilityOfElementLocated(rootElement, by));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(null, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for element to found under another element by a locator
     * @param driver WebDriver instance
     * @param rootElement element to search under
     * @param by by to be found
     * @param elementNameForLogForLogPurpose optional title or name for the searched element for report/log purpose, like, "Schedule page header", "VVL dropdown"
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author genosar.dafna
     * @since 18.07.2022
     * @since 28.02.2024
     */
    public static WebElement waitForVisibilityOfElementLocated(WebDriver driver, WebElement rootElement, By by, @Nullable String elementNameForLogForLogPurpose, int timeoutInSeconds)
    {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(visibilityOfElementLocated(rootElement, by));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(elementNameForLogForLogPurpose, timeoutInSeconds, e));
        }
    }

    /**
     * Support private method to wait and search an element under another element
     * @param rootElement the root element to search from
     * @param locator By locator to search with
     * @return a WebElement if was found or null otherwise
     * @author genosar.dafna
     * @since 18.07.2022
     */
    private static ExpectedCondition<WebElement> visibilityOfElementLocated(WebElement rootElement, final By locator) {
        return new ExpectedCondition<>() {
            public WebElement apply(WebDriver driver) {
                try {
                    WebElement webElement = rootElement.findElement(locator);
                    return webElement.isDisplayed() ? webElement : null;
                } catch (StaleElementReferenceException var3) {
                    return null;
                }
            }

            public String toString() {
                return "visibility of element located by " + locator;
            }
        };
    }

    /**
     * Support private method to wait and search all elements under another element
     * @param rootElement the root element to search from
     * @param locator By locator to search with
     * @param elementsNameForLogForLogPurpose optional title or name for the searched elements for report/log purpose, like, "Schedule Table's rows", "Tabs' headers"
     * @return all WebElements if was found or null otherwise
     * @author genosar.dafna
     * @since 25.07.2022
     * @since 28.02.2024
     */
    private static ExpectedCondition<List<WebElement>> visibilityOfAllElementsLocatedBy(WebElement rootElement, final By locator, @Nullable String elementsNameForLogForLogPurpose) {

        return new ExpectedCondition<>() {
            public List<WebElement> apply(WebDriver driver) {
                List<WebElement> elements = rootElement.findElements(locator);
                Iterator<WebElement> var3 = elements.iterator();

                WebElement element;
                do {
                    if (!var3.hasNext()) {
                        return elements.size() > 0 ? elements : null;
                    }

                    element = var3.next();
                } while (element.isDisplayed());

                return null;
            }

            public String toString() {
                return "visibility of all elements located by " + locator;
            }
        };
    }

    /**
     * Waiting for all elements to be found under another element by a locator
     * @param driver WebDriver instance
     * @param rootElement element to search under
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElements we waited for
     * @author genosar.dafna
     * @since 25.07.2022
     * @since 28.02.2024
     */
    public static List<WebElement> waitForVisibilityOfAllElementsLocatedBy(WebDriver driver, WebElement rootElement, By by, int timeoutInSeconds)
    {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(visibilityOfAllElementsLocatedBy(rootElement, by, null));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(null, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for all WebElements in a list to appear on page
     * @param driver WebDriver instance
     * @param elementsList List of WebElements to appear
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return List of WebElements we waited for
     * @author unknown
     * @since unknown
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static List<WebElement> waitForAllElementsToAppear(WebDriver driver, List<WebElement> elementsList, int timeoutInSeconds)
    {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfAllElements(elementsList));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(null, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for all elements to be found under another element by a locator
     * @param driver WebDriver instance
     * @param rootElement element to search under
     * @param by by to be found
     * @param elementsNameForLogForLogPurpose optional title or name for the searched elements for report/log purpose, like, "Schedule Table's rows", "Tabs' headers"
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElements we waited for
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static List<WebElement> waitForVisibilityOfAllElementsLocatedBy(WebDriver driver, WebElement rootElement, By by, @Nullable String elementsNameForLogForLogPurpose, int timeoutInSeconds)
    {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(visibilityOfAllElementsLocatedBy(rootElement, by, elementsNameForLogForLogPurpose));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(elementsNameForLogForLogPurpose, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for all WebElements found on page by By object
     * @param driver WebDriver instance
     * @param by by to be found
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return List of WebElements we waited for
     * @author rozenfeld.yael
     * @since unknown
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static List<WebElement> waitForVisibilityOfAllElementsLocatedBy(WebDriver driver, By by, int timeoutInSeconds)
    {
        try{
            return new  WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(null, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for all WebElements found on page by By object
     * @param driver WebDriver instance
     * @param by by to be found
     * @param elementNameForLogForLogPurpose optional title or name for the searched element for report/log purpose, like, "Schedule page header", "VVL dropdown"
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return List of WebElements we waited for
     * @author genosar.dafna
     * @since 28.02.2023
     */
    public static List<WebElement> waitForVisibilityOfAllElementsLocatedBy(WebDriver driver, By by, @Nullable String elementNameForLogForLogPurpose,  int timeoutInSeconds)
    {
        try{
            return new  WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage(elementNameForLogForLogPurpose, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for WebElement to be clickable
     * @param driver WebDriver instance
     * @param by -find by to be clickable
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author rozenfeld.yael
     * @since unknown
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static WebElement waitForElementTobeClickable(WebDriver driver, By by, int timeoutInSeconds)
    {
        return waitForElementTobeClickable(driver, by, null, timeoutInSeconds);
    }

    /**
     * Waiting for WebElement to be clickable
     * @param driver WebDriver instance
     * @param by -find by to be clickable
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return WebElement we waited for
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static WebElement waitForElementTobeClickable(WebDriver driver, By by, @Nullable String elementNameForLogPurposeText, int timeoutInSeconds)
    {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.elementToBeClickable(by));
        }
        catch(Exception e){
            throw new Error(getElementNotClickableErrorMessage(elementNameForLogPurposeText, timeoutInSeconds, e));
        }
    }

    /**
     * Waiting for WebElement to be disabled
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeDisabled(WebDriver driver, By by, int timeoutInSeconds)
    {
        return waitForElementToBeDisabled(driver, by, null, timeoutInSeconds);
    }

    /**
     * Waiting for WebElement to be disabled
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeDisabled(WebDriver driver, By by, @Nullable String elementNameForLogPurpose, int timeoutInSeconds)
    {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(by)));
        }
        catch (Exception e){
            throw new Error(getErrorMessage(elementNameForLogPurpose, timeoutInSeconds, "did not become disabled", e));
        }
    }

    /**
     * Waiting for WebElement to be disabled
     * @param driver WebDriver instance
     * @param element - the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeDisabled(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        return waitForElementToBeDisabled(driver, element, null, timeoutInSeconds);
    }

    /**
     * Waiting for WebElement to be disabled
     * @param driver WebDriver instance
     * @param element - the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeDisabled(WebDriver driver, WebElement element, @Nullable String elementNameForLogPurpose, int timeoutInSeconds)
    {
        try{
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(element)));
        }
        catch (Exception e){
            throw new Error(getErrorMessage(elementNameForLogPurpose, timeoutInSeconds, "did not become disabled", e));
        }
    }

    /**
     * Waiting for WebElement to be enabled
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeEnabled(WebDriver driver, By by, int timeoutInSeconds)
    {
        return waitForElementToBeEnabled(driver, by, null, timeoutInSeconds);
    }

    /**
     * Waiting for WebElement to be enabled
     * @param driver WebDriver instance
     * @param by -find by for the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeEnabled(WebDriver driver, By by, @Nullable String elementNameForLogPurpose, int timeoutInSeconds)
    {
        WebElement element = waitForVisibilityOfElementLocated(driver, by, elementNameForLogPurpose, timeoutInSeconds);
        return waitForElementToBeEnabled(driver, element, elementNameForLogPurpose, timeoutInSeconds);
    }

    /**
     * Waiting for WebElement to be enabled
     * @param driver WebDriver instance
     * @param element - the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeEnabled(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        return waitForElementToBeEnabled(driver, element, null, timeoutInSeconds);
    }

    /**
     * Waiting for WebElement to be enabled
     * @param driver WebDriver instance
     * @param element - the element
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false for success
     * @author genosar.dafna
     * @since 29.06.2022
     * @since 28.02.2024
     */
    public static boolean waitForElementToBeEnabled(WebDriver driver, WebElement element, @Nullable String elementNameForLogPurpose, int timeoutInSeconds)
    {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(d -> element.isEnabled());
        }
        catch (Exception e){
            throw new Error(getErrorMessage(elementNameForLogPurpose, timeoutInSeconds, "did not become enabled", e));
        }
    }

    /**
     * Checking if element is displayed or not
     * @param driver WebDriver instance
     * @param element WebElement to appear
     * @param timeout Maximum number of seconds to wait
     * @return boolean (true if element appeared, false otherwise).
     */
    public static boolean isElementDisplayed(WebDriver driver,WebElement element, int timeout) {
        try {
            //driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
            waitForElementToAppear(driver,element, timeout).isDisplayed();
            return true;
        } catch (Throwable e) {
            if (element == null) {
                logger.info("Element does not exist in DOM");
            }
            else logger.info("Element:" + element + "is not displayed");
            return false;
        }

    }

    /**
     * Checking if element is displayed or not
     * @param driver WebDriver instance
     * @param by WebElement locator
     * @param timeout Maximum number of seconds to wait
     * @return boolean (true if element appeared, false otherwise).
     */
    public static boolean isElementDisplayed(WebDriver driver,By by, int timeout) {

        try {
            //driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
            waitForVisibilityOfElementLocated(driver,by,timeout);
            return true;
        } catch (Throwable e) {
            logger.info("Element:" + by.toString() + "is not displayed");
            return false;
        }

    }

    /**
     * Wait for a specific frame and switch to it
     * @param driver WebDriver instance
     * @param frameName the frame name/id
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return the web driver instance after switching to the frame
     * @author genosar.dafna
     * @since 24.07.2022
     * @since 28.02.2024
     */
    public static WebDriver waitForFrameToBeAvailableAndSwitchToIt(WebDriver driver, String frameName, int timeoutInSeconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
        }
        catch(Exception e){
            throw new Error(getElementNotFoundErrorMessage("Frame '" + frameName + "'", timeoutInSeconds, e));
        }
    }

    /**
     * Wait for the number of window handles to be as expected
     * @param driver WebDriver instance
     * @param expectedNumOfWindowHandles optional expected number of window handles. If null - will expect 2
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return true/false if the number of windows is as expected
     * @author genosar.dafna
     * @since 28.07.2022
     * @since 28.02.2024
     */
    public static boolean waitForNumberOfWindowHandlesToBe(WebDriver driver, @Nullable Integer expectedNumOfWindowHandles, int timeoutInSeconds){

        try {
            expectedNumOfWindowHandles = (expectedNumOfWindowHandles == null) ? 2 : expectedNumOfWindowHandles;
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.numberOfWindowsToBe(expectedNumOfWindowHandles));
        }
        catch (Exception e){
            throw new Error(String.format("The number of window handles did not become %d after %d seconds<br><b>Error:</b> %s", expectedNumOfWindowHandles, timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Wait for a specific window handle and switch to it
     * @param driver WebDriver instance
     * @param expectedWindowTitle the window handle title
     * @param timeoutInSeconds Maximum number of seconds to wait
     * @return the web driver instance after switching to the new window handle
     * @author genosar.dafna
     * @since 28.07.2022
     */
    public static WebDriver waitForWindowHandleAndSwitchToIt(WebDriver driver, String expectedWindowTitle, int timeoutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));

        return wait.until(
                new ExpectedCondition<>() {
                    public WebDriver apply(WebDriver driver) {
                        try {
                            //Get the initial window handle
                            String initialWindowHandle = driver.getWindowHandle();

                            //Get all opened window handles
                            Set<String> windowHandles = driver.getWindowHandles();

                            for (String handle : windowHandles) {
                                //Switch to the next window handle
                                driver.switchTo().window(handle);

                                String title = driver.getTitle();
                                if (driver.getTitle().equals(expectedWindowTitle)) {
                                    return driver;
                                }
                            }
                            driver.switchTo().window(initialWindowHandle);

                            throw new Error(String.format("Window handle '%s' could not be found after %d seconds", expectedWindowTitle, timeoutInSeconds));
                        } catch (Exception var3) {
                            throw new Error(String.format("Cannot switch to Window handle '%s'. <br><b>Error:</b> %s", expectedWindowTitle, var3.getMessage()));
                        }
                    }

                    public String toString() {
                        return "Window to be available: " + expectedWindowTitle;
                    }
                });
    }

    /**
     * Waiting for <timeout> seconds till the value attribute changes from <originalValue> to any different value.
     * @param driver WebDriver instance
     * @param webElement WebElement
     * @param timeout Maximum number of seconds to wait
     * @return String (null if element value attribute did not change, returns new value otherwise).
     * @author Sela.Tzvika
     * @since 27.05.2021
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static String waitForWebElementValueAttributeToUpdate(WebDriver driver, WebElement webElement, @Nullable String elementNameForLogPurpose, int timeout, String originalValue){

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

            return wait.until((ExpectedCondition<String>) driver1 -> {
                String value1 = webElement.getAttribute("value");
                if (value1 == null || value1.equals(originalValue))
                    return null;
                return value1;
            });
        }
        catch (Throwable e){
            String elementNameForLogPurposeText = (elementNameForLogPurpose != null)? elementNameForLogPurpose + " " : "";
            throw new Error(String.format("The value of element %sdid not change from %s after %d seconds<br><b>Error:</b> %s", elementNameForLogPurposeText, originalValue, timeout, e.getMessage()));
        }
    }

    /**
     * wait until count of element with specific xpath changes to numOfElements
     * @param driver driver
     * @param by - locator of the element
     * @param timeout - time to wait for the element count to be numOfElements
     * @param numOfElements - num of elements to wait for
     * @return true - if count of element changes to numOfElements, else false
     * @author umflat.lior
     * @since unknown
     * @author genosar.dafna
     * @since 28.02.2024
     * */
    public static boolean waitUntilCountChanges(WebDriver driver,By by, int timeout, int numOfElements) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until((ExpectedCondition<Boolean>) driver1 -> {
                int elementCount = driver1.findElements(by).size();
                return (elementCount == numOfElements);
            });
        }
        catch (Exception e){
            throw new Error(String.format("The number of elements did not change to %d after %d seconds<br><b>Error:</b> %s", numOfElements, timeout, e.getMessage()));
        }
    }

    /**
     * wait till number of elements in a list is greater than a given value number
     * @param driver - web-driver
     * @param elements - list of the elements
     * @param timeout - time to wait till the number of elements are greater than the given value
     * @param numOfElements - the given number to wait till the number of elements are greater then
     * @return True if the number of elements in the list is greater than the numOfElements, else false
     * @author umflat.lior
     * @since 17.8.2022
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static boolean waitUntilNumberOfElementsGreaterThan(WebDriver driver, final List<WebElement> elements, int timeout, final int numOfElements) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until((ExpectedCondition<Boolean>) driver1 -> {
                int elementCount = elements.size();
                return elementCount > numOfElements;
            });
        }
        catch (Exception e){
            throw new Error(String.format("The number of elements did not become greater than %d after %d seconds<br><b>Error:</b> %s", numOfElements, timeout, e.getMessage()));
        }
    }

    /**
     * Waiting for <timeout> seconds till the value attribute changes from <originalValue> to any different value.
     * @param driver WebDriver instance
     * @param webElement WebElement
     * @param timeout Maximum number of seconds to wait
     * @return String (null if element value attribute did not change, returns new value otherwise).
     * @author Sela.Tzvika
     * @since 27.05.2021
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static String waitForWebElementValueAttributeToUpdate(WebDriver driver, WebElement webElement,int timeout, String originalValue){

        return waitForWebElementValueAttributeToUpdate(driver, webElement, null, timeout, originalValue);
    }

    /**
     * Wait until element's attribute is as desired
     * @param driver the driver
     * @param element the element
     * @param attribute the name of the attribute
     * @param value the desired value of attribute
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the attribute is as desired
     * @author Dafna Genosar
     * @since 1.12.2021
     * @since 28.02.2024
     */
    public static Boolean waitForAttributeToBe(WebDriver driver, WebElement element, String attribute, String value, int timeoutInSeconds)
    {
        return waitForAttributeToBe(driver, element, null, attribute, value, timeoutInSeconds);
    }

    /**
     * Wait until element's attribute is as desired
     * @param driver the driver
     * @param element the element
     * @param attribute the name of the attribute
     * @param value the desired value of attribute
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the attribute is as desired
     * @author Dafna Genosar
     * @since 1.12.2021
     * @since 28.02.2024
     */
    public static Boolean waitForAttributeToBe(WebDriver driver, WebElement element, @Nullable String elementNameForLogPurpose, String attribute, String value, int timeoutInSeconds)
    {
        String elementNameForLogPurposeText = elementNameForLogPurpose == null? "" : elementNameForLogPurpose + " ";

        logger.info(String.format("Waiting for attribute '%s' to be '%s'", attribute, value));
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.attributeToBe(element, attribute, value));
        }
        catch (Exception e)
        {
            throw new Error(String.format("%selement's attribute '%s' did not become '%s' after %d seconds.<br><b>Error:</b> %s", elementNameForLogPurposeText, attribute, value, timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Wait until element's attribute contains the word received
     * @param driver the driver
     * @param element the element
     * @param attribute the name of the attribute
     * @param value the desired attribute value or part of value
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the attribute value contains the desired value
     * @author Dafna Genosar
     * @since 1.12.2021
     * @since 28.02.2024
     */
    public static Boolean waitForAttributeToContains(WebDriver driver, WebElement element, String attribute, String value, int timeoutInSeconds)
    {
        return waitForAttributeToContains(driver, element, null, attribute, value, timeoutInSeconds);
    }

    /**
     * Wait until element's attribute contains the word received
     * @param driver the driver
     * @param element the element
     * @param attribute the name of the attribute
     * @param value the desired attribute value or part of value
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the attribute value contains the desired value
     * @author Dafna Genosar
     * @since 1.12.2021
     * @since 28.02.2024
     */
    public static Boolean waitForAttributeToContains(WebDriver driver, WebElement element, @Nullable String elementNameForLogPurpose, String attribute, String value, int timeoutInSeconds)
    {
        String elementNameForLogPurposeText = elementNameForLogPurpose == null? "" : elementNameForLogPurpose + " ";

        logger.info(String.format("Waiting for attribute '%s' to contain '%s'", attribute, value));

        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.attributeContains(element, attribute, value));
        }
        catch (Exception e)
        {
            throw new Error(String.format("%selement's attribute '%s' did not contains '%s' after %d seconds.<br><b>Error:</b> %s", elementNameForLogPurposeText, attribute, value, timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Wait until element's attribute does NOT contain the word received
     * @param driver the driver
     * @param element the element
     * @param attribute the name of the attribute
     * @param value the attribute value or part of value
     * @param timeoutInSeconds timeout to wait
     * @return returns true if the attribute no longer contains the value/false if the attribute still contains the value
     * @author Dafna Genosar
     * @since 06.01.2025
     */
    public static Boolean waitForAttributeNotToContain(WebDriver driver, WebElement element, @Nullable String elementNameForLogPurpose, String attribute, String value, int timeoutInSeconds)
    {
        String elementNameForLogPurposeText = elementNameForLogPurpose == null? "" : elementNameForLogPurpose + " ";

        logger.info(String.format("Waiting for attribute '%s' NOT to contain '%s'", attribute, value));

        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.not(ExpectedConditions.attributeContains(element, attribute, value)));
        }
        catch (Exception e)
        {
            throw new TimeoutException(String.format("%selement's attribute '%s' still contains '%s' after %d seconds.<br><b>Error:</b> %s", elementNameForLogPurposeText, attribute, value, timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Wait until the text is present in the element
     * @param driver the driver
     * @param element the element
     * @param text the expected text
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the text is present in the element
     * @author Dafna Genosar
     * @since 13.02.2022
     * @since 28.02.2024
     */
    public static Boolean waitForTextToBePresentInElement(WebDriver driver, WebElement element, String text, int timeoutInSeconds)
    {
        return waitForTextToBePresentInElement(driver, element, null, text, timeoutInSeconds);
    }

    /**
     * Wait until the text is present in the element
     * @param driver the driver
     * @param element the element
     * @param text the expected text
     * @param timeoutInSeconds timeout to wait
     * @return returns true/false if the text is present in the element
     * @author Dafna Genosar
     * @since 13.02.2022
     * @since 28.02.2024
     */
    public static Boolean waitForTextToBePresentInElement(WebDriver driver, WebElement element, @Nullable String elementNameForLogPurpose, String text, int timeoutInSeconds)
    {
        String elementNameForLogPurposeText = elementNameForLogPurpose == null? "" : elementNameForLogPurpose + " ";

        logger.info(String.format("Waiting for text '%s' to be present in element%s", text, elementNameForLogPurposeText));

        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.textToBePresentInElement(element, text));
        }
        catch (Exception e)
        {
            throw new Error(String.format("The text '%s' did not become present in %selement after %d seconds.<br><b>Error:</b> %s", text, elementNameForLogPurposeText, timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Wait until the text is NOT present in the element
     * @param driver the driver
     * @param element the element
     * @param text the text
     * @param timeoutInSeconds timeout to wait
     * @return returns true if the text is not present in the element / flase if is it present
     * @author Dafna Genosar
     * @since 26.07.2022
     * @since 28.02.2024
     */
    public static boolean waitForTextNotToBePresentInElement(WebDriver driver, WebElement element, String text, int timeoutInSeconds)
    {
        return waitForTextNotToBePresentInElement(driver, element, null, text, timeoutInSeconds);
    }

    /**
     * Wait until the text is NOT present in the element
     * @param driver the driver
     * @param element the element
     * @param text the text
     * @param timeoutInSeconds timeout to wait
     * @return returns true if the text is not present in the element / flase if is it present
     * @author Dafna Genosar
     * @since 26.07.2022
     * @since 28.02.2024
     */
    public static boolean waitForTextNotToBePresentInElement(WebDriver driver, WebElement element, @Nullable String elementNameForLogPurpose, String text, int timeoutInSeconds)
    {
        String elementNameForLogPurposeText = elementNameForLogPurpose == null? "" : elementNameForLogPurpose + " ";

        logger.info(String.format("Waiting for text '%s' not to be present in element%s", text, elementNameForLogPurposeText));

        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, text)));
        }
        catch (Exception e)
        {
            throw new Error(String.format("The text '%s' is still present on %selement after %d seconds.<br><b>Error:</b> %s", text, elementNameForLogPurposeText, timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Wait until the element has no text inside it
     * @param driver the driver
     * @param element the element
     * @param timeoutInSeconds timeout to wait
     * @return returns true if no text is present in the element / false if text is present
     * @author Dafna Genosar
     * @since 26.07.2022
     * @since 28.02.2024
     */
    public static Boolean waitForElementToHaveNoText(WebDriver driver, WebElement element, int timeoutInSeconds)
    {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));

            return wait.until(new ExpectedCondition<>() {
                public Boolean apply(WebDriver driver1) {
                    try {
                        return element.getText() == null || element.getText().equals("");
                    } catch (StaleElementReferenceException var3) {
                        return null;
                    }
                }

                public String toString() {
                    return "No text to be present in element";
                }
            });
        }
        catch(Throwable e){
            throw new Error(String.format("The element still has text after %d seconds<br><b>Error:</b>  %s", timeoutInSeconds, e.getMessage()));
        }
    }

    /**
     * Wait until the text not contains in url.
     * @param text The text that should not be included in the url
     * @param driver the driver
     * @param timeout -
     * @return returns true if text does not present in url,otherwise false
     * @author Yael.Rozenfeld
     * @since 03.04.2022
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static boolean waitUntilUrlDoesNotContain(String text, WebDriver  driver, int timeout)  {
        try {
            return (new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.not(ExpectedConditions.urlContains(text))));
        }
        catch(Exception e){
            throw new Error(String.format("The URL still has text '%s' after %d seconds<br><b>Error:</b> %s", text, timeout, e.getMessage()));
        }
    }
    /**
     * Wait until the text not contains in url.
     * @param text -The text that should be included in the url
     * @param driver the driver
     * @param timeout timeout
     * @return returns true if text presents in url,otherwise false
     * @author Yael.Rozenfeld
     * @since 03.04.2022
     * @author genosar.dafna
     * @since 28.02.2024
     */
    public static boolean waitUntilUrlContains(String text,WebDriver  driver, int timeout){
        try{
            return (new WebDriverWait(driver, Duration.ofSeconds(timeout))).until(ExpectedConditions.urlContains(text));
        }
        catch(Exception e){
            throw new Error(String.format("The URL does nor contain text '%s' after %d seconds<br><b>Error:</b> %s", text, timeout, e.getMessage()));
        }
    }

    /** Wait until new tab is opened and switch to it
     *
     * @param driver instance of WebDriver
     * @param expectedTabNumber expected tab number to wait for.
     *                             for example 3 - > wait till tab 3 will be open
     * @author umflat.lior
     * @since 19.1.2023
     */
    public static void waitForNewTabToOpenAndSwitchToIt(WebDriver driver, int expectedTabNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for the tab with index expectedTabNumber to be opened
        wait.until((ExpectedCondition<Boolean>) driver1 -> driver1.getWindowHandles().size() == expectedTabNumber);

        // Switch to the tab with index expectedTabNumber
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(expectedTabNumber-1));
    }
}