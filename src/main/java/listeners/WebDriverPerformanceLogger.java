package listeners;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener to WebDriver that writes to log how long an action takes (e.g. click, refresh etc.).
 * @author plot.ofek
 * @since 04.05.2021
 */
public class WebDriverPerformanceLogger implements WebDriverListener {

    long startTime, endTime;
    private Logger logger = LoggerFactory.getLogger(WebDriverPerformanceLogger.class);
    @Override
    public void beforeRefresh(WebDriver.Navigation navigation) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        endTime = System.currentTimeMillis();
        logger.info("Page load time was (in milliseconds) : " + (endTime - startTime));
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        endTime = System.currentTimeMillis();
        logger.trace("Time to find element (in milliseconds) : " + (endTime - startTime));
    }

    @Override
    public void beforeClick(WebElement element) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterClick(WebElement element) {
        endTime = System.currentTimeMillis();
        logger.trace("Time to complete click on element " + element.toString() +" (in milliseconds) : " + (endTime - startTime));
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        endTime = System.currentTimeMillis();
        logger.trace("Time to execute script (in milliseconds) : " + (endTime - startTime));
    }



    @Override
    public void beforeGetText(WebElement element) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterGetText(WebElement element, String result) {
        endTime = System.currentTimeMillis();
        logger.trace("Time to get text of element " + element.toString() +" (in milliseconds) : " + (endTime - startTime));
    }

}
