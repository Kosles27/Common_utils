package seleniumUtils.customeElements;

import collectionUtils.ListUtils;
import drivers.TesnetWebElement;
import enumerations.MessageLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;
import waitUtils.WaitWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class refers to the Dropdown Select list - opened when click on a dropdown
 * The class includes the list's elements and methods
 * @author genosar.dafna
 * @since 06.10.2024
 * */
@SuppressWarnings({"unused"})
public class SelectList extends TesnetWebElement {

    private final static Logger logger = LoggerFactory.getLogger(SelectList.class);
    protected WebElement localElement;
    protected WebDriver driver;
    protected String title;

    public SelectList(WebDriver driver) {
        this(driver, WaitWrapper.waitForVisibilityOfElementLocated(driver, By.xpath("//ul[@role='listbox']"), "Options list", 10));
    }

    public SelectList(WebDriver driver, String title) {
        this(driver, WaitWrapper.waitForVisibilityOfElementLocated(driver, By.xpath("//ul[@role='listbox']"), "'%s' Options list".formatted(title), 10), title);
    }

    public SelectList(WebDriver driver, WebElement element) {
        this(driver, element, "Drop down list box");
    }

    public SelectList(WebDriver driver, WebElement element, String title) {
        super(element);
        this.driver = driver;
        this.title = title;
    }

    public SelectList(WebDriver driver, By by, String title) {
        this(driver, WaitWrapper.waitForVisibilityOfElementLocated(driver, by, "'%s' Options list".formatted(title), 10), title);
    }

    /**
     * Wait for the local item
     * @return the local item
     * @author genosar.dafna
     * @since 06.10.2024
     */
    @Override
    public WebElement getLocalElement(){
        try {
            logger.info("Wait for visibility of the List menu");
            return super.getLocalElement();
        }
        catch (Throwable e) {
            throw new Error(String.format("The List menu could not be found after 10 seconds<br><b>Error:</b><br> %s", e.getMessage()));
        }
    }

    /**
     * Return all items as a list
     * @return List<WebElement> of all items
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public LinkedList<WebElement> getOptionsElements() {

        LinkedList<WebElement> elements;
        try {
            elements = new LinkedList<>(WaitWrapper.waitForVisibilityOfAllElementsLocatedBy(driver, getLocalElement() ,By.xpath(".//*[self::p or self::li or self::div[@role='option']]"),timeout));
        }
        catch (Throwable e) {
            throw new Error(String.format("List of item Elements could not be found in the list menu after %d seconds", timeout));
        }

        return elements;
    }

    /**
     * Wait for all options elements
     * @return all options elements
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public LinkedList<WebElement> waitForOptionsElements() {

        try {
            logger.info(String.format("Wait for all options in the Select list box %s", title));
            return new LinkedList<>(WaitWrapper.waitForVisibilityOfAllElementsLocatedBy(driver, getLocalElement(), By.xpath(".//*[self::p or self::li or self::div[@role='option']]"), timeout));
        }
        catch(Throwable e){
            throw new Error(String.format("All options in %s did not display after %d seconds", title, timeout));
        }
    }

    /**
     * Wait for all options elements
     * @return all options elements
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public LinkedList<WebElement> waitForOptionsElements(int elementTimeout) {

        try {
            logger.info(String.format("Wait for all options in the Select list box %s", title));
            return new LinkedList<>(WaitWrapper.waitForVisibilityOfAllElementsLocatedBy(driver, getLocalElement(), By.xpath(".//*[self::p or self::li or self::div[@role='option']]"), elementTimeout));
        }
        catch(Throwable e){
            throw new Error(String.format("All options in %s Select list box did not display after %d seconds", title, elementTimeout));
        }
    }

    /**
     * Find all options by a text
     * @param text the text of the options to find
     * @return all options by a text
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public LinkedList<WebElement> waitForOptionsElementsByVisibleText(String text) {

        try {
            return new LinkedList<>(WaitWrapper.waitForVisibilityOfAllElementsLocatedBy(driver, getLocalElement(), By.xpath(String.format(".//*[contains(text(), \"%s\")]", text)), timeout));
        }
        catch(Throwable e){
            logger.info(String.format("The Select list box does not contain an option with text '%s'.<br>Error: %s", text, e.getMessage()));
            return new LinkedList<>();
        }
    }

    /**
     * Check if an option with the received text exists in the listBox
     * @return true if the option exists / false otherwise
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public boolean isOptionExist(String text){
        LinkedList<WebElement> options = this.waitForOptionsElementsByVisibleText(text);
        return options.size() != 0;
    }

    /**
     * Return the list item as WebElement - please note this is case INSENSITIVE, which means if the param is 'active' it can select 'ACTIVE / Active / active'
     * @param listItem - item name
     * @param matchValueToListOption true if the option in the list should match the value received / false if the option should contain the value
     * @return WebElement
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public WebElement getOptionElement(String listItem, boolean matchValueToListOption) {
        logger.info(String.format("Find option '%s' in the list menu",listItem));
        try {
            if(matchValueToListOption)
                return WaitWrapper.waitForVisibilityOfElementLocated(driver, getLocalElement(),By.xpath(String.format(".//*[ (self::p or self::li or self::div[@role='option'] or self::p/a or self::li/a) and translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '%s']", listItem.toLowerCase())),timeout);
            else
                return WaitWrapper.waitForVisibilityOfElementLocated(driver, getLocalElement(),By.xpath(String.format(".//*[ (self::p or self::li or self::div[@role='option'] or self::p/a or self::li/a) and contains(translate(string(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]", listItem.toLowerCase())),timeout);
        }
        catch (Throwable e) {
            throw new Error("List Item Element '" + listItem + "' not found in the list menu<br><b>Error:</b><br> " + e.getMessage());
        }
    }

    /**
     * Return the list item as WebElement
     * @param itemIndex - item index
     * @return WebElement
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 23.12.2024
     */
    public WebElement getOptionElement(int itemIndex) {
        logger.info(String.format("Find option in index '%d' in the list menu", itemIndex));
        try {
            return WaitWrapper.waitForVisibilityOfElementLocated(driver, getLocalElement(), By.xpath(String.format(".//*[self::p or self::li or self::div[@role='option']][%d]", itemIndex + 1)),timeout);
        } catch (Throwable e) {
            throw new Error("List Item Element in index " + itemIndex + " was found in the list menu<br><b>Error:</b><br> " + e.getMessage());
        }
    }

    /**
     * @return the last option element
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 23.12.2024
     */
    public WebElement getLastDisplayedOptionElement(){
        return getLocalElement().findElement(By.xpath(".//*[self::p or self::li or self::div[@role='option']][last()]"));
    }

    /**
     * @return the first option element
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 02.02.2025
     */
    public WebElement getFirstDisplayedOptionElement(){
        localElement = getLocalElement();
        try {
            return localElement.findElement(By.xpath(".//*[self::p or self::li or self::div[@role='option']][1]"));
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException("Could not find the 1st option element in the Select list");
        }
    }

    /**
     * @return the first option
     * @author genosar.dafna
     * @since 02.02.2025
     */
    public String getFirstDisplayedOption(){
        return getFirstDisplayedOptionElement().getText();
    }

    /**
     * In case of muli-selection list box - Get the selected options elements (only the ones the HTML displays)
     * when the list os too long the html will not display all
     * @return all the selected options elements
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public LinkedList<WebElement> getSelectedOptionsElements(){
        return new LinkedList<>(getLocalElement().findElements(By.xpath(".//*[self::p or self::li or self::div[@role='option']][@aria-selected='true']")));
    }

    /**
     * Select item in the list menu
     * @param listItem - the item name
     * @param matchValueToListOption true if the option in the list should match the value received / false if the option should contain the value
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 10.10.2024
     */
    public void selectByText(String listItem, boolean matchValueToListOption){
        logger.info(String.format("Select <b>'%s'</b> from the list menu", listItem));

        WebElement option = getOptionElement(listItem, matchValueToListOption);
        WebElement optionLink = null;
        try{
            optionLink = option.findElement(By.tagName("a"));

        }
        catch (Exception e){
            logger.info(String.format("No need to click on the href of the list item '%s'. The item itself can be clicked", listItem));
        }

        if(optionLink != null)
            optionLink.click();
        else
            option.click();
    }

    /**
     * Select item in the list menu
     * @param itemIndex - the index of the item to select
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public void selectByIndex(int itemIndex){
        logger.info(String.format("Select item in index '%d' from the list menu", itemIndex));

        getOptionElement(itemIndex).click();
    }

    /**
     * Get options from the list menu
     * @return List of option as strings
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public LinkedList<String> getOptions(){
        LinkedList<WebElement> itemsList = getOptionsElements();
        LinkedList<String> options = new LinkedList<>();
        for (WebElement element : itemsList) {
            options.add(element.getText());
        }
        return options;
    }

    /**
     * @return true if the 'Loading…' text displays in the dropdown list / false otherwise
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public boolean isLoadingDisplay(){
        try{
            WaitWrapper.waitForVisibilityOfElementLocated(driver, By.xpath(".//*[text()='Loading…']"), 3);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * Select a random option from the list menu
     * @return the selected option
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 26.05.2025
     */
    public String selectRandomFromListMenu(){
        LinkedList<WebElement> allOptions = getOptionsElements();
        return selectRandomFromListMenu(allOptions);
    }

    /**
     * Select a random option from the list menu
     * @param optionsToAvoid option/s you would like NOT to select
     * @param matchText true - checks if any of the options in the list MATCHES any of the options to avoid
     *                  false - checks if any of the options in the list CONTAINS any of the options to avoid
     * @return the selected option
     * @author genosar.dafna
     * @since 06.11.2024
     * @since 26.05.2025
     */
    public <T> String selectRandomFromListMenu(T optionsToAvoid, boolean matchText){

        LinkedList<WebElement> allOptions = getCleanOptionsElements(optionsToAvoid, matchText);
        return selectRandomFromListMenu(allOptions);
    }

    /**
     * Select a random option from the list menu
     * @param optionElementsToSelectFrom options to select from
     * @return the selected option
     * @author genosar.dafna
     * @since 26.05.2025
     */
    public String selectRandomFromListMenu(List<WebElement> optionElementsToSelectFrom){

        if(optionElementsToSelectFrom.size()==0){
            throw new Error("Cannot select a random item. There are no items in the list Menu");
        }

        WebElement randomOption = ListUtils.getRandomItemFromList(optionElementsToSelectFrom);
        String randomOptionText = randomOption.getText();
        Report.reportAndLog(String.format("Select a random item <b>'%s'</b> from the list menu", randomOptionText), MessageLevel.INFO);
        randomOption.click();
        return randomOptionText;
    }

    /**
     * Select a random option from the list menu
     * @param optionsToSelectFrom options to select from
     * @return the selected option
     * @author genosar.dafna
     * @since 26.05.2025
     */
    public String selectRandomFromListMenuFromGivenOptions(List<String> optionsToSelectFrom, boolean matchText){

        if(optionsToSelectFrom.size()==0){
            throw new Error("Cannot select a random item. There are no items in the list Menu");
        }

        String randomOptionText = ListUtils.getRandomItemFromList(optionsToSelectFrom);

        WebElement randomOption = getOptionElement(randomOptionText, matchText);
        Report.reportAndLog(String.format("Select a random item <b>'%s'</b> from the list menu", randomOptionText), MessageLevel.INFO);
        randomOption.click();
        return randomOptionText;
    }

    /**
     * Get the Select options excluding the options to avoid
     * @param optionsToAvoid the options to avoid
     * @param matchText true if to match the option text / false if to check if the option contains
     * @return the Select options excluding the options to avoid
     * @author genosar.dafna
     * @since 26.05.2025
     */
    private <T> LinkedList<WebElement> getCleanOptionsElements(T optionsToAvoid, boolean matchText) {
        LinkedList<WebElement> allOptions = new LinkedList<>(getOptionsElements());

        if(optionsToAvoid instanceof String) {
            for (WebElement option : allOptions) {
                String optionText = option.getText();
                if (matchText) {
                    if (optionText.equals(optionsToAvoid))
                        allOptions = new LinkedList<>(ListUtils.removeItemFromList(allOptions, option));
                } else {
                    if (optionText.contains((String)optionsToAvoid))
                        allOptions = new LinkedList<>(ListUtils.removeItemFromList(allOptions, option));
                }
            }
        }
        else if(optionsToAvoid instanceof List) {
            LinkedList<WebElement> filteredItems = new LinkedList<>();

            for(WebElement option : allOptions){
                String optionText = option.getText();
                if(matchText) {
                    if (!((List)optionsToAvoid).contains(option.getText()))
                        filteredItems.add(option);
                }
                else{
                    for(String optionToAvoid : (List<String>)optionsToAvoid){
                        if(!optionText.contains(optionToAvoid))
                            filteredItems.add(option);
                    }
                }
            }
            allOptions = new LinkedList<>(filteredItems);
        }
        return allOptions;
    }
}
