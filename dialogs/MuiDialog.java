package dialogs;

import drivers.TesnetWebElement;
import enumerations.MessageLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import waitUtils.WaitWrapper;

/**
 * Base Class that supports MUI dialogs
 * A MUI Dialog is a component from the Material-UI (MUI) library (formerly known as Material-UI, now part of the MUI Core library).
 * It is a UI element designed to display a modal dialog box that appears in front of the main content.
 * The dialog forces users to interact with it before returning to the main interface.
 * @author genosar.dafna
 * @since 13.10.2024
 */
@SuppressWarnings("unused")
public class MuiDialog extends TesnetWebElement {

    public String title = null;

    By byTitle = By.xpath(".//*[self::h1 or self::h2 or self::h3 or self::h4 or self::h5 or self::h6 or self::p or self::div[@class='header'] or self::*[contains(@data-testid, 'DialogTitle')] or self::*[contains(@data-testid, 'dialog-title')] or self::*[contains(@class, 'MuiDialogTitle')]][1]");

    public MuiDialog(WebElement element){
        super(element);
    }

    public MuiDialog(WebElement element, String title){
        super(element);
        this.title = title;
    }

    public MuiDialog(WebDriver driver, WebElement element){
        super(driver, element);
    }

    public MuiDialog(WebDriver driver, WebElement element, String title){
        super(driver, element);
        this.title = title;
    }

    public MuiDialog(WebDriver driver){
        super(By.xpath("(//div[@role='dialog'])[last()]"));
    }

    public MuiDialog(By by){
        super(by);
    }

    public MuiDialog(By by, String title){
        super(by);
        this.title = title;
    }

    public MuiDialog(WebDriver driver, By by){
        super(driver, by);
    }

    public MuiDialog(WebDriver driver, By by, String title){
        super(driver, by);
        this.title = title;
    }

    public MuiDialog(WebDriver driver, String title){
        this(driver, By.xpath(String.format("(//div[@role='dialog'])[last()][descendant::*[(self::h1 or self::h2 or self::h3 or self::h4 or self::h5 or self::h6 " +
                                            "or self::p " +
                                            "or self::div[@class='header'] " +
                                            "or self::*[contains(@data-testid, 'dialog-title')] " +

                                            "or self::*[contains(@data-testid, 'DialogTitle')] " +
                                            "or self::*[contains(@class, 'MuiDialogTitle')]) " +
                                            "and contains(., '%s') ]]", title)), title);
    }

    /**
     * @return the dialog's title as String
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public String getTitle() {
        if(title != null)
            return title;

        try{
            WebElement titleEl = getLocalElement().findElement(byTitle);
            return titleEl.getText().trim();
        }
        catch(Throwable e){
            return null;
        }
    }

    /**
     * Wait for the title element to display
     * @return the title as String
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public String waitForTitle() {

        if(title != null)
            return title;

        WebElement titleEl = WaitWrapper.waitForVisibilityOfElementLocated(driver, getLocalElement(), byTitle, "Dialog's title", 5);

        try{
            title = titleEl.getText().trim();
            return title;
        }
        catch(Throwable e){
            return null;
        }
    }

    /**
     * Get the dialog's text (might need to be adjusted in child classes)
     * @return the dialog's text
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public String getDialogText() {

        try{
            WebElement textEl = getLocalElement().findElement(By.xpath("./div[contains(@class, 'MuiDialogContent')]"));
            return textEl.getText().trim();
        }
        catch(Throwable e){
            throw new Error("Cannot find the text on the dialog");
        }
    }

    /**
     * Get a button on the dialog using the given By statement
     * @param by By statement
     * @return a button on the dialog using the given By statement
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public WebElement getButton(By by){
        try {
            return findElement(by);
        }
        catch (Exception e){
            throw new Error(String.format("The button could not be found on '%s' using BY: %s<br>Error: %s", getTitle(), by.toString(), e.getMessage()));
        }
    }

    /**
     * Click the button by given element
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public void clickButton(WebElement button){
        String buttonName = button.getText();
        reportUtils.Report.reportAndLog(String.format("Click <b>'%s'</b> button", buttonName), MessageLevel.INFO);
        try{
            button.click();
        }
        catch(Exception e){
            throw new Error(String.format("The '%s' button could not be clicked on '%s' dialog<br>Error: %s", buttonName, getTitle(), e.getMessage()));
        }
    }

    /**
     * Click the button by name
     * @author genosar.dafna
     * @since 13.10.2024
     * @since 01.04.2025
     */
    public void clickButton(String buttonName){
        try {
            WebElement button = findElement(By.xpath(String.format(".//*[self::button[text()='%s'] or self::button[descendant::span[text()='%s']]]", buttonName, buttonName)));
            clickButton(button);
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException("Button <b>%s</b> could not be found on the dialog".formatted(buttonName));
        }
    }

    /**
     * Click the button by By
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public void clickButton(By by){
        WebElement button = getButton(by);
        clickButton(button);
    }

    //button[@role='continue-editing']

    /**
     * Get the close button (the X button) on the dialog
     * @return the close button (the X button) on the dialog
     * @author genosar.dafna
     * @since 13.10.2024
     */
    private WebElement getCloseButton(){
        try{
            return findElement(By.xpath(".//button[descendant::*[@data-testid='CloseIcon']]"));
        }
        catch(Exception e){
            throw new Error(String.format("The close button (X) could not be found on the '%s' dialog", getTitle()));
        }
    }

    /**
     * Click the X button to close the dialog
     * @author genosar.dafna
     * @since 13.10.2024
     * @since 15.10.2024
     */
    public void closeByX(){
        reportUtils.Report.reportAndLog("Click the X button to close the dialog", MessageLevel.INFO);
        getCloseButton().click();
        WaitWrapper.waitForElementToDisappear(driver, localElement, 5);
    }
}