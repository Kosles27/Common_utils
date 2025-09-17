package listeners;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Listener to WebDriver that writes to log the actions that were taken (e.g. click, refresh etc.).
 * @author plot.ofek
 * @since 04.05.2021
 */
public class WebDriverActionLogger implements WebDriverListener {
    private final Logger logger = LoggerFactory.getLogger(WebDriverActionLogger.class);
    public WebDriverActionLogger()
    {
        logger.trace("Logging that new WebDriverActionLogger instance was created");
    }

    @Override
    public void beforeAccept(Alert alert) {
        logger.info("Accepting Alert");
    }

    @Override
    public void afterAccept(Alert alert) {
        logger.info("Alert accepted");
    }

    @Override
    public void afterDismiss(Alert alert) {
        logger.info("Alert was dismissed");
    }

    @Override
    public void beforeDismiss(Alert alert) {
        logger.info("Dismissing Alert");
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, String url)  {
        logger.info("Navigating to " + url);
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url){
        logger.trace("Navigated to " + url);
    }

    @Override
    public void beforeBack(WebDriver.Navigation navigation) {
        logger.info("Navigating to previous web page");
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation){
        logger.trace("Navigated back to previous web page");
    }

    @Override
    public void beforeForward(WebDriver.Navigation navigation)  {
        logger.info("Navigating back to next web page");
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation)  {
        logger.trace("Navigated to next web page");
    }

    @Override
    public  void beforeRefresh(WebDriver.Navigation navigation) {
        logger.info("Reloading the web page");
    }

    @Override
    public  void afterRefresh(WebDriver.Navigation navigation) {
        logger.trace("Reloaded the web page");
    }

    @Override
    public  void beforeFindElement(WebElement element, By locator) {
        if (element != null)
        {
            logger.trace("Element to look for: " + element);
        }
        logger.trace("Trying to find element by locator " + locator.toString());
    }

    @Override
    public void afterFindElement(WebElement element, By locator, WebElement result)  {
        if (element != null)
        {
            logger.trace("Element found: " + element);
        }
        logger.trace("Found element by locator " + locator.toString());
    }

    @Override
    public void beforeClick(WebElement element) {
        logger.info("Clicking on " + element.toString() );
    }

    @Override
    public void afterClick(WebElement element) {
        logger.trace("Clicked on " + element.toString() );
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend){
        logger.info("Changing value of " + element.toString() + " to: " + Arrays.toString(keysToSend));
    }

    @Override
    public  void afterSendKeys(WebElement element, CharSequence... keysToSend){
        logger.trace("Changed value of " + element.toString() + " to: " + Arrays.toString(keysToSend));
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        logger.debug("Executing script " + script);
    }



    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        logger.trace("Exception or Error raised: " + e.getMessage());
    }


    @Override
    public void beforeGetText(WebElement element) {
        logger.trace("Getting text inside element: " + element.toString());
    }

    @Override
    public void afterGetText(WebElement element, String result) {
        logger.trace("Inside element " + element.toString() + " the following text was found: " + result );
    }
}
