package seleniumUtils;

import drivers.TesnetMobileDriver;
import enumerations.MessageLevel;
import objectsUtils.ObjectsUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;
import waitUtils.WaitWrapper;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static propertyUtils.PropertyUtils.getGlobalProperty;

/**
 * Class is used for defining parameters and methods that are relevant for all pages in all projects
 *
 */
@SuppressWarnings("unused")
public class BasePage<T extends WebDriver> {

    protected T driver;
    protected int timeout;
    protected Duration timeoutDuration;
    private final Logger logger = LoggerFactory.getLogger(BasePage.class);

    public BasePage(T driver) {
        this.driver = driver;
        this.timeout = Integer.parseInt(getGlobalProperty("timeout"));
        this.timeoutDuration = Duration.ofSeconds(timeout);
        if (this.driver instanceof TesnetMobileDriver)
            // TODO: this is a bug. Currently cannot activate AppiumFieldDecorator.
            //PageFactory.initElements(new AppiumFieldDecorator(driver),this);
            PageFactory.initElements(driver,this);

        else
            PageFactory.initElements(driver,this);

    }

    /**
     * Get an element by...
     * @param by  by (xpath, id etc)
     * @param elementName Name of element to display in report
     * @return the WebElement
     * @author Genosar.dafna
     * @since 06.10.2024
     */
    public WebElement findElement(By by, String elementName) {
        try{
            return driver.findElement(by);
        }
        catch (Exception e){
            throw new Error(String.format("The %s could not be found<br><b>Error:</b><br> %s", elementName, e.getMessage()));
        }
    }

    /**
     * Get an element by...
     * @param by  by (xpath, id etc)
     * @return the WebElement
     * @author Genosar.dafna
     * @since 06.10.2024
     * @since 06.01.2025
     */
    public WebElement findElement(By by) {
        try{
            return driver.findElement(by);
        }
        catch (Exception e){
            throw new NoSuchElementException(String.format("The element could not be found using BY: %s<br><b>Error:</b><br> %s", by.toString(), e.getMessage()));
        }
    }

    /**
     * Refresh the page
     * @author genosar.dafna
     * @since 02.11.2022
     */
    public void refresh(){
        driver.navigate().refresh();
    }

    /**
     * Zoom in or out by given percentage
     * @author genosar.dafna
     * @since 10.04.2025
     */
    public void zoom(int percentage){
        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='%d%%'".formatted(Math.abs(percentage)));
    }

    /**
     * Get an instance of the page
     * @param driver driver
     * @param pageType page type from enum
     * @return an instance of the page
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public static <T> T getPage(WebDriver driver, Class<T> pageType){
        return ObjectsUtils.newInstance(pageType, driver);
    }

    /**
     * Wait for the page to display
     * @param by xpath/id/etc of a unique element on page
     * @param pageType the page type
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public static <T> T waitForPage(WebDriver driver, By by, Class<T> pageType, int timeout){
        try {
            WaitWrapper.waitForVisibilityOfElementLocated(driver, by, timeout);
            return getPage(driver, pageType);
        }
        catch(Throwable e){
            throw new Error(String.format("Page '%s' did not display after %d seconds", pageType, timeout));
        }
    }

    /**
     * Click a button
     * @param button button WebElement
     * @param buttonName the button name
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 06.01.2025
     */
    protected void clickButton(WebElement button, String buttonName){

        Report.reportAndLog(String.format("Click <b>'%s'</b> button", buttonName), MessageLevel.INFO);
        button = WaitWrapper.waitForElementTobeClickable(driver, button,  buttonName + " button", 30);

        if(!button.isEnabled())
            throw new IllegalStateException(String.format("Button '%s' is disabled and cannot be clicked", buttonName));

        try {
            button.click();
        }
        catch (ElementClickInterceptedException e) {
            throw new ElementClickInterceptedException(String.format("Failed to click '%s' button. The button was intercepted by another element<br><b>Error:</b><br> %s", buttonName, e.getMessage()));
        }
    }

    /**
     * Click a button
     * @param button button WebElement
     * @author genosar.dafna
     * @since 06.01.2025
     */
    public void clickButton(WebElement button) {
        String buttonName = button.getText();
        this.clickButton(button, buttonName);
    }

    /**
     * Click a button
     * @param buttonName the button name
     * @author genosar.dafna
     * @since 06.01.2025
     * @since 01.04.2025
     */
    public void clickButton(String buttonName) {
        try {
            WebElement button = this.findElement(By.xpath(String.format(".//*[self::button[text()='%s'] or self::button[descendant::span[text()='%s']]]", buttonName, buttonName)));
            this.clickButton(button, buttonName);
        }
        catch (org.openqa.selenium.NoSuchElementException e){
            throw new org.openqa.selenium.NoSuchElementException("Button <b>%s</b> could not be found on page".formatted(buttonName));
        }
    }

    /**
     * Get a button
     * @param buttonName the button name
     * @author genosar.dafna
     * @since 28.04.2025
     */
    public WebElement getButton(String buttonName) {
        try {
            return this.findElement(By.xpath(String.format(".//*[self::button[text()='%s'] or self::button[descendant::span[text()='%s']]]", buttonName, buttonName)));
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException("Button <b>%s</b> could not be found on page".formatted(buttonName));
        }
    }

    /**
     * Get a button
     * @param buttonName the button name
     * @author genosar.dafna
     * @since 04.05.2025
     */
    public WebElement waitButton(String buttonName, int timeout) {
        try {
            return WaitWrapper.waitForVisibilityOfElementLocated(driver, By.xpath(String.format(".//*[self::button[text()='%s'] or self::button[descendant::span[text()='%s']]]", buttonName, buttonName)), buttonName, timeout);
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException("Button <b>%s</b> could not be found on page".formatted(buttonName));
        }
    }

    /**
     * Click a button
     * @param by the button by expression
     * @author genosar.dafna
     * @since 06.01.2025
     * @since 01.04.2025
     */
    public void clickButton(By by) {
        try {
            WebElement button = this.findElement(by);
            this.clickButton(button);
        }
        catch (org.openqa.selenium.NoSuchElementException e){
            throw new org.openqa.selenium.NoSuchElementException("Button could not be found on page using by: %s".formatted(by));
        }
    }

    /**
     * This is a default implementation of isOnPage.
     * It receives one or more WebElements and checks if they are present
     *
     * @param elements One or more WebElements to check for presence
     * @return true- all elements specified were present, false - otherwise
     * @author Nir.Gallner
     * @since 16.05.2021
     * @author Zvika.Sela
     */
    protected boolean isOnPage(WebElement... elements) {
        try {
            WebDriverWait Waits = new WebDriverWait(driver, timeoutDuration);
            Waits.until(new ExpectedCondition<List<WebElement>>() {
                @Override
                public @Nullable
                List<WebElement> apply(WebDriver d) {
                    for (WebElement webElement : elements) {
                        if (webElement.getLocation() == null)
                            return null;
                    }
                    return elements.length > 0 ? Arrays.asList(elements) : null;
                }
            });
            logger.info("elements " + Arrays.toString(elements) + "were present on page");
            return true;
        } catch (Exception e) {
            logger.info("elements " + Arrays.toString(elements) + "weren't present on page");
            return false;
        }
    }



    /**
     *
     * @param elem element type: combo box
     * @param index index number in cmb list
     * @author jan.naor
     * @since 23.05.21
     */
    public void selectDropDownByIndex(WebElement elem, int index)
    {
        Select myValue=new Select(elem);
        myValue.selectByIndex(index);
    }

    /**
     *
     * @param elem element type: combo box
     * @param value value to select from cmb list
     * @author jan.naor
     * @since 23.05.21
     */
    public void selectDropDownByValue(WebElement elem, String value)
    {
        Select myValue=new Select(elem);
        myValue.selectByValue(value);
    }



}

