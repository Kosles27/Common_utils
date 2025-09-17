package seleniumUtils.customeElements;

import Managers.WebDriverInstanceManager;
import collectionUtils.ListUtils;
import drivers.TesnetWebElement;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seleniumUtils.ActionsWrapper;
import seleniumUtils.ElementWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class refers to dropdown input object which opens a list menu when set text in it, like the dropdown under
 * @author genosar.dafna
 * @since 06.10.2024
 * */

@SuppressWarnings("unused")
public class SelectField extends TesnetWebElement {

    private static final Logger logger = LoggerFactory.getLogger(SelectField.class);
    WebDriver driver;
    WebElement inputFieldElement;

    public SelectField(WebElement element) {
        this(WebDriverInstanceManager.getDriverFromMap(), element);
    }

    public SelectField(WebDriver driver, WebElement element) {
        super(element);
        this.driver = driver;
        this.inputFieldElement = getInputElement();
    }

    /**
     * Get the input element under the dropdown
     * @return the input element under the dropdown
     * @author genosar.dafna
     * @since 06.10.2024
     */
    private WebElement getInputElement(){
        if(this.inputFieldElement == null || ElementWrapper.isStaleElement(this.inputFieldElement)){
            try{
                inputFieldElement = getLocalElement().findElement(By.xpath(".//input"));
            }
            catch (Throwable e) {
                throw new Error("Cannot find input element under the dropdown<br><b>Error:</b><br> " + e.getMessage());
            }
        }
        return inputFieldElement;
    }

    /**
     * Insert a value in the dropdown input
     * @param value - value to set in the dropdown input
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public SelectList insertValue(String value, boolean clearBeforeInserting){
        if(clearBeforeInserting) {
            logger.info("Clear the dropdown object");
            clear();
        }
        WebElement inputField = getInputElement();

        logger.info(String.format("Insert value '%s' in the dropdown", value));
        inputField.sendKeys(value);
        try{
            return new SelectList(driver);
        }
        catch (Throwable t){
            throw new Error(t.getMessage());
        }
    }

    /**
     * Insert a value and select the option from the dropdown menu
     * @param optionName - name of the option in the dropdown menu
     * @param matchValueToListOption true if the option in the list should match the value received / false if the option should contain the value
     * @param clearBeforeInserting true if to clear any existing text that displays in the input field
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public void setAndSelectOption(String optionName, boolean matchValueToListOption, boolean clearBeforeInserting){

        //If the current value is the same, exit the method
        String currentSelection = getText();
        if((matchValueToListOption && currentSelection.equalsIgnoreCase(optionName)) || (!matchValueToListOption && currentSelection.contains(optionName)))
            return;

        String option = optionName;
        if(optionName.endsWith(" ")) {
            option = optionName.trim();
            logger.info(String.format("Option to select was trimmed from '%s' to '%s'", optionName, option));
        }

        SelectList dropDownSelectObj = insertValue(option, clearBeforeInserting);
        dropDownSelectObj.selectByText(optionName, matchValueToListOption);
    }

    /**
     * Insert values and select the options from the dropdown menu
     * @param optionsNames - names of the options in the dropdown menu
     * @param matchValueToListOption true if the option in the list should match the value received / false if the option should contain the value
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public void setAndSelectOptions(List<String> optionsNames, boolean matchValueToListOption, boolean clearBeforeInserting){

        for(int i=0; i< optionsNames.size(); i++){

            //For cases like marine code that have a few with the same beginning, like AGABTM1 and AGABTM16
            String optionName = optionsNames.get(i);
            String option = optionName;
            if(optionName.endsWith(" ")) {
                option = optionName.trim();
                logger.info(String.format("Option to select was trimmed from '%s' to '%s'", optionName, option));
            }
            SelectList dropDownSelectObj = (i == 0)? insertValue(option, clearBeforeInserting) : insertValue(option, false);
            if(dropDownSelectObj.getOptions().size() == 1)
                dropDownSelectObj.selectByIndex(0);
            else
                dropDownSelectObj.selectByText(optionName, matchValueToListOption);
        }
    }

    /**
     * Insert a value to the dropdown input field and return the list object that opens
     * @param optionName - name of the option in the dropdown menu
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public SelectList setSelectOption(String optionName){

        return insertValue(optionName, true);
    }

    /**
     * Select an option from the dropdown menu without setting text
     * @param optionName - name of the option in the dropdown menu
     * @param matchValueToListOption true if the option in the list should match the value received / false if the option should contain the value
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public void selectOption(String optionName, boolean matchValueToListOption){

        String currentSelection = getText();
        if((matchValueToListOption && currentSelection.equalsIgnoreCase(optionName)) || (!matchValueToListOption && currentSelection.contains(optionName)))
            return;

        SelectList dropDownSelectObj;

        dropDownSelectObj = expand();
        dropDownSelectObj.selectByText(optionName, matchValueToListOption);
    }

    /**
     * Select an option from the dropdown menu by its index
     * @param optionIndex - index of the option in the dropdown menu
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public void selectOption(int optionIndex){

        SelectList dropDownSelectObj;
        dropDownSelectObj = expand();
        dropDownSelectObj.selectByIndex(optionIndex);
    }

    /**
     * Select a random option from the dropdown menu without setting text.
     * @return the selected option
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 06.11.2024
     */
    public String selectRandomOption(){
        SelectList dropDownSelectObj;
        if (!isExpanded())
            dropDownSelectObj = expand();
        else
            dropDownSelectObj = new SelectList(driver);
        return dropDownSelectObj.selectRandomFromListMenu();
    }

    /**
     * Select a random option from the dropdown menu without setting text.
     * @param optionsToAvoid option/s you would like NOT to select. Can be a String, A List or an Array
     * @param matchText true - checks if any of the options in the list MATCHES any of the options to avoid
     *                  false - checks if any of the options in the list CONTAINS any of the options to avoid
     * @return the selected option
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 06.11.2024
     */
    public <T> String selectRandomOption(T optionsToAvoid, boolean matchText){

        SelectList dropDownSelectObj;
        if (!isExpanded())
            dropDownSelectObj = expand();
        else
            dropDownSelectObj = new SelectList(driver);

        if(optionsToAvoid instanceof List){
            return dropDownSelectObj.selectRandomFromListMenu((List<String>)optionsToAvoid, matchText);
        }
        else if(optionsToAvoid instanceof String){
            return dropDownSelectObj.selectRandomFromListMenu(optionsToAvoid.toString(), matchText);
        }
        else if(optionsToAvoid.getClass().isArray()){
            List<String> list = Arrays.asList((String[]) optionsToAvoid);
            return dropDownSelectObj.selectRandomFromListMenu(list, matchText);
        }
        else
            throw new IllegalArgumentException("Parameter received in method selectRandomOption() must either be a String, a List or an Array");

    }

    /**
     * Select a random option from the dropdown menu without setting text.
     * @param optionsToAvoid option/s you would like NOT to select. Can be a String, A List or an Array
     * @return the selected option
     * @author genosar.dafna
     * @since 07.11.2024
     */
    public <T> String selectRandomOption(T optionsToAvoid){

        return selectRandomOption(optionsToAvoid, true);
    }

    /**
     * Select random options from the dropdown menu without setting text.
     * @param optionsToAvoid option/s you would like NOT to select. Can be a String, A List or an Array
     * @param matchText true - checks if any of the options in the list MATCHES any of the options to avoid
     *                  false - checks if any of the options in the list CONTAINS any of the options to avoid
     * @return the selected option
     * @author genosar.dafna
     * @since 25.05.2025
     */
    public <T> LinkedList<String> selectRandomOptions(@Nullable T optionsToAvoid, int numberOfItemsToSelect, boolean matchText){

        LinkedList<String> selectedOptions = new LinkedList<>();

        //Get the list options
        LinkedList<String> optionsToSelectFrom = getOptions(false);

        //Remove the options to avoid from the options list
        LinkedList<String> filteredItems;

        if(optionsToAvoid != null){
            List<String> optionsToAvoidList;
            if(optionsToAvoid instanceof List){
                optionsToAvoidList = (List)optionsToAvoid;
            }
            else if(optionsToAvoid instanceof String)
                optionsToAvoidList = List.of((String)optionsToAvoid);
            else
                throw new IllegalArgumentException("Param optionsToAvoid must be either a String of a List");

            filteredItems = new LinkedList<>();
            for(String optionText : optionsToSelectFrom){

                if(matchText) {
                    if (!optionsToAvoidList.contains(optionText))
                        filteredItems.add(optionText);
                }
                else{
                    for(String optionToAvoid : optionsToAvoidList){
                        if(!optionText.contains(optionToAvoid))
                            filteredItems.add(optionText);
                    }
                }
            }
        }
        else
            filteredItems = new LinkedList<>(optionsToSelectFrom);

        for(int i=0; i < numberOfItemsToSelect; i++){

            SelectList dropDownSelectObj = expand();

            String selectedOption = dropDownSelectObj.selectRandomFromListMenuFromGivenOptions(filteredItems, matchText);

            selectedOptions.add(selectedOption);
            ListUtils.removeItemFromList(filteredItems, selectedOption);
        }
        return selectedOptions;
    }

    /**
     * Get the dropdown select obj - when click on dropdown input obj
     * @return DropDownSelectObj
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public SelectList getDropdownSelectObj(){

        if (!isExpanded())
            return expand();
        else
            return new SelectList(driver);
    }

    /**
     * Get the dropdown select obj options - when click on dropdown input obj
     * @return List of options
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public LinkedList<String> getOptions(boolean closeDropDown){

        SelectList dropDownSelectObj = getDropdownSelectObj();
        LinkedList<String> options = dropDownSelectObj.getOptions();

        if(closeDropDown)
            close();

        return options;
    }

    /**
     * Get the dropdown select obj options - when click on dropdown input obj
     * @return List of options
     * @author genosar.dafna
     * @since 26.05.2025
     */
    public LinkedList<WebElement> getOptionsElements(boolean closeDropDown){

        SelectList dropDownSelectObj = getDropdownSelectObj();
        LinkedList<WebElement> options = dropDownSelectObj.getOptionsElements();

        if(closeDropDown)
            close();

        return options;
    }

    /**
     * @return true/false if the dropdown menu is expanded
     * @author genosar.dafna
     * @since 06.10.2024
     */
    private boolean isExpanded(){
        WebElement elementToCheck;
        boolean expanded = false;

        try{
            elementToCheck = getLocalElement().findElement(By.xpath(".//*[@aria-expanded]"));
            expanded = elementToCheck.getAttribute("aria-expanded").equals("true");
            logger.info(expanded? "Dropdown is expanded" : "Dropdown is not expanded");
            return expanded;
        }
        catch (Throwable t){
            logger.info("aria-expanded attribute does not exist in the SelectObj");
            return expanded;
        }
    }

    /**
     * Click the X button on the dropdown
     * @author genosar.dafna
     * @since 06.10.2024
     */
    @Override
    public void clear(){

        String currentValue = getText();

        //exit the method if the dropdown's current value is already null or empty
        if (currentValue == null || currentValue.equals(""))
            return;

        try{
            WebElement xbutton = getClearButton();
            ActionsWrapper.mouseOverAndClick(driver, xbutton);
        }
        catch (Throwable t) {
            try {

                //Find the X button
                WebElement inputField = getInputElement();

                inputField.sendKeys(Keys.chord(Keys.CONTROL, "a"));

                // Send Delete
                inputField.sendKeys(Keys.DELETE);

                // Send Backspace
                inputField.sendKeys(Keys.BACK_SPACE);
            } catch (Exception e) {
                throw new Error(String.format("Cannot clear the dropdown object.<br><b>Error:</b><br> %s", e.getMessage()));
            }
        }
    }

    /**
     * Find the X button on the dropdown. It does not display or enabled, hover over the dropdown to display it
     * @return the X button on the dropdown
     * @author genosar.dafna
     * @since 06.10.2024
     */
    private WebElement getClearButton(){

        By clearButtonFindBy = By.xpath(".//button[@title='Clear']");

        try{
            WebElement clearButton = getLocalElement().findElement(clearButtonFindBy);

            if(!clearButton.isDisplayed() || !clearButton.isEnabled())
                //Hover over
                ActionsWrapper.mouseOver(driver, getLocalElement());

            return getLocalElement().findElement(clearButtonFindBy);
        }
        catch (NoSuchElementException | ElementNotInteractableException e)
        {
            try{
                ActionsWrapper.mouseOver(driver, getLocalElement());
                return getLocalElement().findElement(clearButtonFindBy);
            }
            catch (Exception e2){
                throw new Error(String.format("Unable to return the Clear button (X button) on the dropdown.<br><b>Error:</b><br> %s", e.getMessage()));
            }
        }
        catch (Exception e){
            throw new Error(String.format("Unable to return the Clear button (X button) on the dropdown.<br><b>Error:</b><br> %s", e.getMessage()));
        }
    }

    /**
     * Click the arrow or magnifying glass button on the dropdown (if exists) to expand it and return the dropdown select obj
     * @return the list menu in the dropdown as DropDownSelectObj
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public SelectList expand(){

        try{
            if(!isExpanded()) {
                WebElement expandButton = getLocalElement().findElement(By.xpath(".//*[self::div[@role='combobox'] or self::button[@title='Open']]"));
                expandButton.click();
            }
            return new SelectList(driver);
        }
        catch (Throwable e){
            throw new Error("Could not expand the dropdown<br><b>Error:</b><br> " + e.getMessage());
        }
    }

    /**
     * Click the arrow or magnifying glass button on the dropdown to close it
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public void close(){

        try{
            if(isExpanded()) {
                WebElement closeButton = getLocalElement().findElement(By.xpath(".//button[@title='Close']"));
                closeButton.click();
            }
        }
        catch (Exception e){
            throw new Error(String.format("Close button (arrow or magnifying glass) could not be found in the dropdown. Error: %s", e.getMessage()));
        }
    }

    /**
     * Get the current displayed value in the dropdown
     * @return the current displayed value in the dropdown
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public String getText(){

        try {
            WebElement span = getLocalElement().findElement(By.xpath("./div[contains(@class, 'MuiSelect-select')]"));
            return span.getText();
        }
        catch (Throwable t) {
            WebElement inputElement = getInputElement();
            try {
                if (ElementWrapper.attributeExists(inputElement, "value"))
                    return inputElement.getAttribute("value");
                else
                    return inputElement.getText();
            } catch (Throwable tt) {
                throw new Error(String.format("Cannot return the dropdown current value.<br><b>Error:</b><br> %s", tt.getMessage()));
            }
        }
    }
}
