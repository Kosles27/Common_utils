package drivers;

import Managers.WebDriverInstanceManager;
import dateTimeUtils.DateUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seleniumUtils.ElementWrapper;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static propertyUtils.PropertyUtils.getGlobalProperty;
import static systemUtils.SystemCommonUtils.sleep;

/**
 * TesnetWebElement implements WebElement, Locatable and WrapsElement to provide a complete solution in one object
 * for common actions of WebElement
 */
@SuppressWarnings("unused")
public class TesnetWebElement implements WebElement, Locatable,WrapsElement {
    private static final Logger logger = LoggerFactory.getLogger(TesnetWebElement.class);
    protected WebElement localElement;
    protected String elementAbsoluteXpath = null;
    protected WebDriver driver;
    protected int timeout;

    public TesnetWebElement(WebElement element){
        this(WebDriverInstanceManager.getDriverFromMap(), element);
    }

    public TesnetWebElement(WebDriver driver, WebElement element){
        this.localElement = element;
        this.driver = driver;
        this.timeout = getTimeout();
    }

    public TesnetWebElement(By by){
        this(WebDriverInstanceManager.getDriverFromMap(), by);
    }

    public TesnetWebElement(WebDriver driver, By by){
        try {
            this.localElement = driver.findElement(by);
        }
        catch(Exception e){
            throw new Error(String.format("The element could not be found using by: %s<br>Error: %s", by.toString(), e.getMessage()));
        }
        this.driver = driver;
        this.timeout = getTimeout();
    }

    private int getTimeout(){
        try{
            return Integer.parseInt(getGlobalProperty("timeout"));
        }
        catch (Throwable t){
            return 5;
        }
    }

    /**
     * @return local element
     * @author genosar.dafna
     * @since 31.10.2024
     */
    public WebElement getLocalElement()
    {
        int timeout = 10;
        if(localElement == null)
            throw new NullPointerException("The local element is null");
        else if(isStaleElement())
                throw new StaleElementReferenceException("The local element is stale");
        else
            return localElement;
    }

    /**
     * Get the absolute XPath using JavaScript
     * @return the absolute XPath using JavaScript
     * @author genosar.dafna
     * @since 06.10.2024
     * @since 31.10.2024
     */
    public String getAbsoluteXpath(){

        if(elementAbsoluteXpath == null) {

            //Get the absolute XPath using JavaScript
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            elementAbsoluteXpath = (String) jsExecutor.executeScript(
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
                            "return getXPath(arguments[0]);", localElement);
        }
        return elementAbsoluteXpath;
    }

    /**
     * Checks if the element is stale
     * @return true if the element is stale / false otherwise
     * @author Genosar.dafna
     * @since 06.11.2023
     */
    public boolean isStaleElement(){
        try{
            localElement.getTagName();
            return false;
        }
        catch(Exception e){
            return true;
        }
    }

    /**
     * Click this element. If this causes a new page to load, you
     * should discard all references to this element and any further
     * operations performed on this element will throw a
     * StaleElementReferenceException.
     * <p>
     * Note that if click() is done by sending a native event (which is
     * the default on most browsers/platforms) then the method will
     * _not_ wait for the next page to load and the caller should verify
     * that themselves.
     * <p>
     * There are some preconditions for an element to be clicked. The
     * element must be visible and it must have a height and width
     * greater then 0.
     *
     * @throws StaleElementReferenceException If the element no
     *                                        longer exists as initially defined
     */
    @Override
    public void click() {

        // TODO - Nir is not happy to get the driver from the map. Find alternative
        WebDriver originalDriver = ((WrapsDriver) WebDriverInstanceManager.getDriverFromMap()).getWrappedDriver();
        logger.debug("clicked on element -> " + localElement.toString());
        try {
            this.localElement.click();
        } catch (Throwable t) {
            logger.info("****************** could not click on element: " + localElement.toString() + " - try scroll and then click");

            ElementWrapper.scrollToElement(originalDriver, this.localElement);
            this.localElement.click();
        }
    }

    /**
     * If this current element is a form, or an element within a form, then this will be submitted to
     * the remote server. If this causes the current page to change, then this method will block until
     * the new page is loaded.
     *
     * @throws NoSuchElementException If the given element is not within a form
     */
    @Override
    public void submit() {
        logger.debug("element using : submit " + localElement.toString());
        this.localElement.submit();
    }

    /**
     * Use this method to simulate typing into an element, which may set its value.
     *
     * @param keysToSend character sequence to send to the element
     * @throws IllegalArgumentException if keysToSend is null
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        logger.debug("element using : sendKeys " + localElement.toString() + " keysToSend = " + Arrays.toString(keysToSend));
        this.localElement.sendKeys(keysToSend);
    }

    /**
     * Type text letter by letter, as a user
     * @param text text to type
     * @author genosar.dafna
     * @since 21.06.2023
     */
    public void typeText(String text){

        logger.debug("element using : typeText " + localElement.toString() + " text to type = " + text);

        for (int i = 0; i < text.length(); i++){
            char c = text.charAt(i);
            String s = new StringBuilder().append(c).toString();
            localElement.sendKeys(s);
            sleep(200);
        }
    }

    /**
     * Set the value in the element (input) using JS executor
     * @param value value to set
     * @author genosar.dafna
     * @since 21.06.2023
     */
    public void setValueByJsExecutor(String value){

        logger.debug("element using : setValueByJsExecutor " + localElement.toString() + " value to set = " + value);
        JavascriptExecutor jse = (JavascriptExecutor)WebDriverInstanceManager.getDriverFromMap();
        jse.executeScript("arguments[0].setAttribute('value', '" + value +"')", localElement);
    }

    /**
     * If this element is a text entry element, this will clear the value. Has no effect on other
     * elements. Text entry elements are INPUT and TEXTAREA elements.
     * <p>
     * Note that the events fired by this event may not be as you'd expect.  In particular, we don't
     * fire any keyboard or mouse events.  If you want to ensure keyboard events are fired, consider
     * using something like {@link #sendKeys(CharSequence...)} with the backspace key.  To ensure
     * you get a change event, consider following with a call to {@link #sendKeys(CharSequence...)}
     * with the tab key.
     */
    @Override
    public void clear() {
        logger.debug("element using : clear " + localElement.toString());
        this.localElement.clear();
    }

    /**
     * Get the tag name of this element. <b>Not</b> the value of the name attribute: will return
     * <code>"input"</code> for the element <code>&lt;input name="foo" /&gt;</code>.
     *
     * @return The tag name of this element.
     */
    @Override
    public String getTagName() {
        logger.debug("element using : getTagName");
        String tagName = this.localElement.getTagName();
        logger.debug("element using : getTagName " + localElement.toString() + " TagName = " + tagName);
        return tagName;
    }

    /**
     * Get the value of the given attribute of the element. Will return the current value, even if
     * this has been modified after the page has been loaded.
     *
     * <p>More exactly, this method will return the value of the property with the given name, if it
     * exists. If it does not, then the value of the attribute with the given name is returned. If
     * neither exists, null is returned.
     *
     * <p>The "style" attribute is converted as best can be to a text representation with a trailing
     * semi-colon.
     *
     * <p>The following are deemed to be "boolean" attributes, and will return either "true" or null:
     *
     * <p>async, autofocus, autoplay, checked, compact, complete, controls, declare, defaultchecked,
     * defaultselected, defer, disabled, draggable, ended, formnovalidate, hidden, indeterminate,
     * iscontenteditable, ismap, itemscope, loop, multiple, muted, nohref, noresize, noshade,
     * novalidate, nowrap, open, paused, pubdate, readonly, required, reversed, scoped, seamless,
     * seeking, selected, truespeed, willvalidate
     *
     * <p>Finally, the following commonly mis-capitalized attribute/property names are evaluated as
     * expected:
     *
     * <ul>
     * <li>If the given name is "class", the "className" property is returned.
     * <li>If the given name is "readonly", the "readOnly" property is returned.
     * </ul>
     *
     * <i>Note:</i> The reason for this behavior is that users frequently confuse attributes and
     * properties. If you need to do something more precise, e.g., refer to an attribute even when a
     * property of the same name exists, then you should evaluate Javascript to obtain the result
     * you desire.
     *
     * @param name The name of the attribute.
     * @return The attribute/property's current value or null if the value is not set.
     */
    @Override
    public String getAttribute(String name) {
        logger.debug("element using : getAttribute");
        String attribute = this.localElement.getAttribute(name);
        logger.debug("element using : getAttribute " + localElement.toString() + " Attribute = " + attribute);
        return attribute;
    }

    /**
     * Checks if an attribute exists in an element
     * @param attribute the attribute name
     * @return true/false if the element has the attribute
     * @author Dafna Genosar
     * @since 18.10.2022
     * @author Dafna Genosar
     * @since 19.02.2023
     */
    public boolean attributeExists(String attribute)
    {
        try
        {
            String returnAttribute = localElement.getAttribute(attribute);
            return returnAttribute != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Determine whether or not this element is selected or not. This operation only applies to input
     * elements such as checkboxes, options in a select and radio buttons.
     * For more information on which elements this method supports,
     * refer to the <a href="https://w3c.github.io/webdriver/webdriver-spec.html#is-element-selected">specification</a>.
     *
     * @return True if the element is currently selected or checked, false otherwise.
     */
    @Override
    public boolean isSelected() {
        logger.debug("element using : isSelected");
        boolean isSelected = this.localElement.isSelected();
        logger.debug("element using : isSelected " + localElement.toString() + " isSelected = " + isSelected);
        return isSelected;

    }

    @Override
    public String getDomAttribute(String name) {
        logger.debug("element using : getDomAttribute");
        return localElement.getDomAttribute(name);

    }

    /**
     * Is the element currently enabled or not? This will generally return true for everything but
     * disabled input elements.
     *
     * @return True if the element is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        logger.debug("element using : isEnabled");
        boolean isEnabled = localElement.isEnabled();
        logger.debug("element using : isEnabled " + localElement.toString() + " isEnabled = " + isEnabled);
        return isEnabled;
    }

    /**
     * Get the visible (i.e. not hidden by CSS) text of this element, including sub-elements.
     *
     * @return The visible text of this element.
     * @see <a href="https://w3c.github.io/webdriver/#get-element-text">"Get Element Text" section
     * in W3C WebDriver Specification</a>
     */
    @Override
    public String getText() {
        logger.debug("element using : getText");
        String getText = this.localElement.getText();
        logger.debug("element using : getText " + getText + " Text = " + getText);
        return getText;
    }

    /**
     * Find all elements within the current context using the given mechanism. When using xpath be
     * aware that webdriver follows standard conventions: a search prefixed with "//" will search the
     * entire document, not just the children of this current node. Use ".//" to limit your search to
     * the children of this WebElement.
     * This method is affected by the 'implicit wait' times in force at the time of execution. When
     * implicitly waiting, this method will return as soon as there are more than 0 items in the
     * found collection, or will return an empty list if the timeout is reached.
     * @param by The locating mechanism to use
     * @return A list of all {@link WebElement}s, or an empty list if nothing matches.
     * @see By
     * @see WebDriver.Timeouts
     */
    @Override
    public List<WebElement> findElements(By by) {
        logger.debug("element using : findElements");
        this.localElement = getLocalElement();
        List<WebElement> elements = this.localElement.findElements(by);
        logger.debug("element using : findElements " + localElement.toString() + " By = " + by.toString() + " elements = " + Arrays.toString(elements.toArray()));
        List<WebElement> newElements = new ArrayList<>();
        for (WebElement e: elements) {
            TesnetWebElement el = new TesnetWebElement(e);
            newElements.add(el);
        }
        return newElements;
    }

    /**
     * Find the first {@link WebElement} using the given method. See the note in
     * {@link #findElements(By)} about finding via XPath.
     * This method is affected by the 'implicit wait' times in force at the time of execution.
     * The findElement(..) invocation will return a matching row, or try again repeatedly until
     * the configured timeout is reached.
     * <p>
     * findElement should not be used to look for non-present elements, use {@link #findElements(By)}
     * and assert zero length response instead.
     * @param by The locating mechanism
     * @return The first matching element on the current context.
     * @throws NoSuchElementException If no matching elements are found
     * @see By
     * @see WebDriver.Timeouts
     * @author ?
     * @since ?
     * @author genosar.dafna
     * @since 15.10.2024
     */
    @Override
    public WebElement findElement(By by) {
        logger.debug("element using : findElement");
        this.localElement = getLocalElement();
        WebElement foundElement = this.localElement.findElement(by);
        logger.debug("element using : findElement " + this.localElement.toString() + " By = " + by.toString() + " found element = " + foundElement.toString());
        return new TesnetWebElement(foundElement);
    }

    /**
     * Is this element displayed or not? This method avoids the problem of having to parse an
     * element's "style" attribute.
     * @return Weather or not the element is displayed
     * @author genosar.dafna
     * @since 15.10.2024
     */
    @Override
    public boolean isDisplayed() {
        logger.debug("element using : isDisplayed");
        boolean isDisplayed = localElement.isDisplayed();
        logger.debug("element using : isDisplayed " + localElement.toString());
        return isDisplayed;
    }

    /**
     * Method to highlight an element with a yellow background
     * @param driver  driver
     * @param bgColor optional background color. Can be a name of a color, like RED, or like RGB(225,100,100)
     * @param borderColor optional background color style text. for example: 3px solid red
     * @author genosar.dafna
     * @since 13.10.2024
     */
    public void highlightElement(WebDriver driver, @Nullable String bgColor, @Nullable String borderColor) {

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
        js.executeScript(script, getLocalElement());
    }

    /**
     * Method to remove highlight from an element
     * @param driver  driver
     * @param removeBgColor true to reset the background color to nothing / false will not change the background
     * @param removeBorder true to reset the border style to nothing / false will not change the border
     * @author genosar.dafna
     * @since 20.08.2024
     */
    public void removeHighlight(WebDriver driver, boolean removeBgColor, boolean removeBorder) {

        String bgColorString = "";
        String borderColorString = "";

        if(removeBgColor)
            bgColorString = "arguments[0].style.backgroundColor=''; ";
        if(removeBorder)
            borderColorString = "arguments[0].style.border=''; ";

        String script = bgColorString + borderColorString;

        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Reset the background color and border properties
        js.executeScript(script, getLocalElement());
    }

    /**
     * Where on the page is the top left-hand corner of the rendered element?
     *
     * @return A point, containing the location of the top left-hand corner of the element
     */
    @Override
    public Point getLocation() {
        logger.debug("element using : getLocation");
        Point point = localElement.getLocation();
        logger.debug("element using : getLocation " + localElement.toString() + "Location = " + point.toString());
        return point;
    }

    /**
     * What is the width and height of the rendered element?
     *
     * @return The size of the element on the page.
     */
    @Override
    public Dimension getSize() {
        logger.debug("element using : getSize");
        Dimension dimension = localElement.getSize ();
        logger.debug("element using : getSize " + localElement.toString() + " Size = " + dimension.toString());
        return dimension;
    }

    /**
     * @return The location and size of the rendered element
     */
    @Override
    public Rectangle getRect() {
        logger.debug("element using : getRect");
        Rectangle rectangle = localElement.getRect();
        logger.debug("element using : getRect " + localElement.toString() + " Rectangle = " + rectangle.toString());
        return rectangle;
    }

    /**
     * Return the element's parent element
     * @return the element's parent element
     * @author Dafna Genosar
     * @since 20.12.2022
     */
    public WebElement getParentElement()
    {
        return localElement.findElement(By.xpath("./.."));
    }

    /**
     * Get the value of a given CSS property.
     * Color values should be returned as rgba strings, so,
     * for example if the "background-color" property is set as "green" in the
     * HTML source, the returned value will be "rgba(0, 255, 0, 1)".
     * <p>
     * Note that shorthand CSS properties (e.g. background, font, border, border-top, margin,
     * margin-top, padding, padding-top, list-style, outline, pause, cue) are not returned,
     * in accordance with the
     * <a href="http://www.w3.org/TR/DOM-Level-2-Style/css.html#CSS-CSSStyleDeclaration">DOM CSS2 specification</a>
     * - you should directly access the longhand properties (e.g. background-color) to access the
     * desired values.
     *
     * @param propertyName the css property name of the element
     * @return The current, computed value of the property.
     */
    @Override
    public String getCssValue(String propertyName) {
        String cssValue = localElement.getCssValue(propertyName);
        logger.debug("element using : getCssValue " + localElement.toString() + " CssValue = " + cssValue);
        return cssValue;
    }

    /**
     * Capture the screenshot and store it in the specified location.
     *
     * <p>For WebDriver extending TakesScreenshot, this makes a best effort
     * depending on the browser to return the following in order of preference:
     * <ul>
     *   <li>Entire page</li>
     *   <li>Current window</li>
     *   <li>Visible portion of the current frame</li>
     *   <li>The screenshot of the entire display containing the browser</li>
     * </ul>
     *
     * <p>For WebElement extending TakesScreenshot, this makes a best effort
     * depending on the browser to return the following in order of preference:
     *   - The entire content of the HTML element
     *   - The visible portion of the HTML element
     *
     * @param target target type, @see OutputType
     * @return Object in which is stored information about the screenshot.
     * @throws WebDriverException            on failure.
     * @throws UnsupportedOperationException if the underlying implementation does not support
     *                                       screenshot capturing.
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        logger.debug("element using : getScreenshotAs " + target.toString());
        return this.localElement.getScreenshotAs(target);
    }

    /**
     * take screenshot of the given element for reporting
     * @param driver WebDriver instance
     * @return screenShotPath string new location of file
     * @author genosar.dafna
     * @since 10.11.2024
     */
    public String takeElementScreenShot(WebDriver driver, @Nullable String elementName)
    {
        String screenShotPath;

        try
        {
            screenShotPath = "images" + File.separator +  DateUtils.getUniqueTimestamp() +".png" ;

            //Capture the element screenshot
            File screenshot = localElement.getScreenshotAs(OutputType.FILE);

            FileUtils.copyFile(screenshot, new File("report" + File.separator + screenShotPath));
            logger.info(screenShotPath);
        }

        //if invalid session id exception exists - take driver from map
        catch (Exception exc) {
            if(elementName == null)
                throw new Error(String.format("Error taking the element's screenshot<br>Error: %s", exc.getMessage()));
            else
                throw new Error(String.format("Error taking a screenshot or element '%s'<br>Error: %s", elementName, exc.getMessage()));
        }

        return screenShotPath;
    }

    /**
     * get actual WebDriver implementor. i.e:ChromeWebDriver, FireFoxWebDriver etc..
     */
    @Override
    public WebElement getWrappedElement() {
        return this.localElement;
    }

    /**
     * returns coordinates of an element for advanced interactions.
     * Note that some coordinates (such as screen coordinates) are evaluated lazily since the element may have to be scrolled into view.
     * @return Coordinates
     */
    @Override
    public Coordinates getCoordinates() {
        logger.debug("element using : getCoordinates");
        return ((Locatable)this.localElement).getCoordinates();
    }

    /**
     * Scroll all the way down the element's height
     * @param driver the driver
     * @author Dafna Genosar
     * @since 10.03.2024
     */
    public void scrollDownElementHeight(WebDriver driver)
    {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop += arguments[0].scrollHeight;", localElement);
    }

    /**
     * Scroll all the way up the element's height
     * @param driver the driver
     * @author Dafna Genosar
     * @since 10.03.2024
     */
    public void scrollUpElementHeight(WebDriver driver)
    {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTo(0,0);", localElement);
    }

    /**
     * @return true if the element is scrolled all the way down / false otherwise
     * please note that the element must be the scrollable element
     * @author genosar.dafna
     * @since 10.03.2024
     */
    public boolean isScrolledToBottom(WebDriver driver){
        return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollTop + arguments[0].offsetHeight == arguments[0].scrollHeight;", localElement);
    }

    /**
     * Checks if the element has child nodes
     * @return true if the element has child nodes, otherwise false
     * @author genosar.dafna
     * @since 28.10.2022
     */
    public boolean hasChildNodes(){
        int numOfChildren = getNumberOfChildNodes();
        return numOfChildren > 0;
    }

    /**
     * @return the number of child nodes the element has
     * @author genosar.dafna
     * @since 28.10.2022
     */
    public int getNumberOfChildNodes(){
        List<WebElement> childNodes = this.localElement.findElements(By.xpath(".//*"));
        return childNodes.size();
    }
}
