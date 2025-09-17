package seleniumUtils;

import enumerations.MessageLevel;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import waitUtils.WaitWrapper;

import static propertyUtils.PropertyUtils.getGlobalProperty;

/**
 * Base class for components
 * @author genosar.dafna
 * @since 02.02.2025
 */
@SuppressWarnings("unused")
public class BaseComponent {

    protected int timeout =  Integer.parseInt(getGlobalProperty("timeout"));
    protected WebElement localElement;
    protected WebDriver driver;

    public BaseComponent(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public BaseComponent(WebDriver driver, WebElement element){
        this.driver = driver;
        this.localElement = element;
        PageFactory.initElements(driver, this);
    }

    public WebElement getLocalElement(){
        return this.localElement;
    }

    /**
     * get the button by its text
     * @author genosar.dafna
     * @since 11.07.2024
     * @since 03.02.2025
     */
    public WebElement getButton(String buttonName){
        try {
            return getLocalElement().findElement(By.xpath(String.format(".//button[text()='%s']", buttonName)));
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException(String.format("Button '%s' could not be found on component<br>Error: %s", buttonName, e.getMessage()));
        }
    }

    /**
     * Click the button by its text
     * @author genosar.dafna
     * @since 11.07.2024
     */
    public void clickButton(String buttonName){
        WebElement button = getButton(buttonName);
        clickButton(button);
    }

    /**
     * Click the button by element
     * @author genosar.dafna
     * @since 11.07.2024
     * @since 02.02.20025
     */
    public void clickButton(WebElement button){
        String buttonName = button.getText();
        clickButton(button, buttonName);
    }

    /**
     * Click the button by element
     * @author genosar.dafna
     * @since 11.07.2024
     * @since 02.02.20025
     */
    public void clickButton(WebElement button, String buttonName){
        if(!button.isEnabled()) {
            try {
                WaitWrapper.waitForElementToBeEnabled(driver, button, buttonName + " button", 5);
            }
            catch (Throwable t){
                throw new TimeoutException(String.format("<b>%s</b> button could not be clicked. The button is disabled<br>Error: %s", buttonName, t.getMessage()));
            }
        }

        reportUtils.Report.reportAndLog(String.format("Click <b>%s</b> button", buttonName), MessageLevel.INFO);
        try{
            button.click();
        }
        catch(ElementClickInterceptedException e){
            throw new ElementClickInterceptedException(String.format("<b>%s</b> button could not be clicked. The button is intercepted<br>Error: %s", buttonName, e.getMessage()));
        }
    }

    /**
     * @param xpath xpath to find the element
     * @param elementName element name for logging purpose
     * @return WebElement
     * @author genosar.dafna
     * @since 27.08.2024
     */
    protected WebElement getElement(String xpath, String elementName){

        try{
            return localElement.findElement(By.xpath(xpath));
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException(String.format("%s could not be found with xpath: %s<br>Error: %s", elementName, xpath, e.getMessage()));
        }
    }

    /**
     * @param by by statement to find the element
     * @param elementName element name for logging purpose
     * @return WebElement
     * @author genosar.dafna
     * @since 27.08.2024
     */
    protected WebElement getElement(By by, String elementName){

        try{
            return localElement.findElement(by);
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException(String.format("%s could not be found using By: %s<br>Error: %s", elementName, by, e.getMessage()));
        }
    }
}
