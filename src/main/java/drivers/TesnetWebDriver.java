package drivers;

import Managers.ReportInstanceManager;
import Store.StoreManager;
import Store.StoreType;
import com.relevantcodes.extentreports.LogStatus;
import enumerations.MessageLevel;
import listeners.WebDriverActionLogger;
import listeners.WebDriverActionReporter;
import listeners.WebDriverPerformanceLogger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.openqa.selenium.devtools.DevTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * TesnetWebDriver implements not only WebDriver interface but also TakesScreenshot, JavascriptExecutor and others,
 * to give a complete solution in one object for common actions in automation tests
 */
@SuppressWarnings({"unused", "unchecked"})
public class TesnetWebDriver
        implements WebDriver,
        JavascriptExecutor,
        HasCapabilities,
        HasVirtualAuthenticator,
        Interactive,
        PrintsPage,
        TakesScreenshot,
        WrapsDriver {

    protected WebDriver driver;
    private static final Logger logger =LoggerFactory.getLogger(TesnetWebDriver.class);

    /**
     * Constructor adds listeners to instance of WebDriver (wrapped in EventFiringWebDriver instance)
     * Depending on the logLevel parameter (Trace or Debug) adds a trace or debug level where debug is the default
     * @param options Instance of WebDriver Chromium options
     */
    public TesnetWebDriver(ChromiumOptions options) {
        //TODO: after moving to Selenium4 with Appium8 we need to check if the listeners support mobile now as well (in Selenium3 they weren't supported for Appium).
        if (options instanceof EdgeOptions){
            logger.trace("add listener to driver -> WebDriverActionLogger");
            this.driver = new EventFiringDecorator(new WebDriverActionLogger(), new WebDriverActionReporter(), new WebDriverPerformanceLogger()).decorate(new EdgeDriver((EdgeOptions) options));
        }

        if (options instanceof ChromeOptions) {
            logger.trace("add listener to driver -> WebDriverActionLogger");
            this.driver = new EventFiringDecorator(new WebDriverActionLogger(), new WebDriverActionReporter(), new WebDriverPerformanceLogger()).decorate(new ChromeDriver((ChromeOptions) options));
        }
    }
    public TesnetWebDriver(RemoteWebDriver remoteWebDriver) {
            if (Boolean.TRUE.equals(StoreManager.getStore(StoreType.LOCAL_THREAD).getValueFromStore("isFullLogging"))) {
                logger.info("Starting augmenter");
                remoteWebDriver = (RemoteWebDriver) new Augmenter().augment(remoteWebDriver);
            }

            this.driver = new EventFiringDecorator(new WebDriverActionLogger(), new WebDriverActionReporter(), new WebDriverPerformanceLogger()).decorate(remoteWebDriver);
        }

    public Optional<DevTools> getDevTools() {
        WebDriver originalDriver = getOriginalDriver();
        if (originalDriver instanceof ChromeDriver) {
            return Optional.of(((ChromeDriver) originalDriver).getDevTools());
        } else if (originalDriver instanceof EdgeDriver) {
            return Optional.of(((EdgeDriver) originalDriver).getDevTools());
        } else {
            return Optional.empty(); // DevTools not supported for this driver
        }
    }

    public WebDriver getOriginalDriver() {
        WebDriver currentDriver = this.driver;
        Report.reportAndLog("This driver: "+this.driver, MessageLevel.INFO);
        while (currentDriver instanceof WrapsDriver) {
            currentDriver = ((WrapsDriver) currentDriver).getWrappedDriver();
        }
        Report.reportAndLog("Current driver: "+currentDriver, MessageLevel.INFO);
        return currentDriver;
    }

    /**
     * Load a new web page in the current browser window. This is done using an HTTP GET operation,
     * and the method will block until the load is complete. This will follow redirects issued either
     * by the server or as a meta-redirect from within the returned HTML. Should a meta-redirect
     * "rest" for any duration of time, it is best to wait until this timeout is over, since should
     * the underlying page change whilst your test is executing the results of future calls against
     * this interface will be against the freshly loaded page. Synonym for
     * {@link Navigation#to(String)}.
     *
     * @param url The URL to load. It is best to use a fully qualified URL
     */
    @Override
    public void get(String url) {
        logger.trace("Driver using: get URL -> " + url);
        driver.get(url);
    }

    /**
     * Get a string representing the current URL that the browser is looking at.
     *
     * @return The URL of the page currently loaded in the browser
     */
    @Override
    public String getCurrentUrl() {
        logger.trace("Driver using: get Current Url");
        String  currentUrl = driver.getCurrentUrl();
        logger.trace("Driver using: get Current Url -> " + currentUrl);
        return currentUrl;
    }

    /**
     * The title of the current page.
     *
     * @return The title of the current page, with leading and trailing whitespace stripped, or null
     * if one is not already set
     */
    @Override
    public String getTitle() {
        logger.trace("Driver using: get title");
        String title = driver.getTitle();
        logger.trace("Driver using: get title -> " + title);
        return title;
    }

    /**
     * Find all elements within the current page using the given mechanism.
     * This method is affected by the 'implicit wait' times in force at the time of execution. When
     * implicitly waiting, this method will return as soon as there are more than 0 items in the
     * found collection, or will return an empty list if the timeout is reached.
     *
     * @param by The locating mechanism to use
     * @return A list of all {@link WebElement}s, or an empty list if nothing matches
     * @see By
     * @see Timeouts
     */
    @Override
    public List<WebElement> findElements(By by) {
        logger.trace("Driver using: find elements -> " + by.toString());
        List<WebElement> elements = driver.findElements(by);
        logger.trace("driver using : findElements " + elements.toString() + " By = " + by + " elements = " + Arrays.toString(elements.toArray()));
        List<WebElement> newElements = new ArrayList<>();

        for (WebElement e: elements) {
            TesnetWebElement el = new TesnetWebElement(e);
            newElements.add(el);
        }

        return newElements;
    }

    /**
     * Find the first {@link WebElement} using the given method.
     * This method is affected by the 'implicit wait' times in force at the time of execution.
     * The findElement(..) invocation will return a matching row, or try again repeatedly until
     * the configured timeout is reached.
     * <p>
     * findElement should not be used to look for non-present elements, use {@link #findElements(By)}
     * and assert zero length response instead.
     *
     * @param by The locating mechanism.
     * @return The first matching element on the current page
     * @throws NoSuchElementException If no matching elements are found
     * @see By
     * @see Timeouts
     */
    @Override
    public WebElement findElement(By by) {
        logger.trace("Driver using: find element -> " + by.toString());
        WebElement e = driver.findElement(by);
        return new TesnetWebElement(e);
    }



    /**
     * Get the source of the last loaded page. If the page has been modified after loading (for
     * example, by Javascript) there is no guarantee that the returned text is that of the modified
     * page. Please consult the documentation of the particular driver being used to determine whether
     * the returned text reflects the current state of the page or the text last sent by the web
     * server. The page source returned is a representation of the underlying DOM: do not expect it to
     * be formatted or escaped in the same way as the response sent from the web server. Think of it as
     * an artist's impression.
     *
     * @return The source of the current page
     */
    @Override
    public String getPageSource() {
        logger.trace("Driver using: get page source");
        return driver.getPageSource();
    }

    /**
     * Scroll the page all the way up
     * @author genosar.dafna
     * @since 30.01.2024
     */
    public void scrollUp() {
        // Create a JavascriptExecutor instance
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll the page up using JavaScript
        js.executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scroll the page all the way up
     * @author genosar.dafna
     * @since 30.01.2024
     */
    public static void scrollUp(WebDriver driver) {
        // Create a JavascriptExecutor instance
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll the page up using JavaScript
        js.executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scroll the page up by number of pixels
     * @author genosar.dafna
     * @since 30.01.2024
     */
    public void scrollUp(int numberOfPixels) {
        // Create a JavascriptExecutor instance
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll Down by pixels
        String script = "window.scrollBy(0, -" + numberOfPixels + ");";
        js.executeScript(script);
    }

    /**
     * Scroll the page all the way down
     * @author genosar.dafna
     * @since 30.01.2024
     */
    public void scrollDown() {
        // Create a JavascriptExecutor instance
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll the page down using JavaScript
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scroll the page all the way down
     * @author genosar.dafna
     * @since 30.01.2024
     */
    public static void scrollDown(WebDriver driver) {
        // Create a JavascriptExecutor instance
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll the down up using JavaScript
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scroll the page down by number of pixels
     * @author genosar.dafna
     * @since 30.01.2024
     */
    public void scrollDown(int numberOfPixels) {
        // Create a JavascriptExecutor instance
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll Down by pixels
        String script = "window.scrollBy(0, " + numberOfPixels + ");";
        js.executeScript(script);
    }

    /**
     * Close the current window, quitting the browser if it's the last window currently open.
     */
    @Override
    public void close() {
        logger.debug("Driver: Close");
        if(driver != null)
            driver.close();
    }

    /**
     * Quits this driver, closing every associated window.
     */
    @Override
    public void quit() {
        logger.debug("Driver using: quit -> " + " BrowserName : " + this.getCapabilities().getBrowserName());
        if(driver != null)
            driver.quit();
    }

    /**
     * Return a set of window handles which can be used to iterate over all open windows of this
     * WebDriver instance by passing them to {@link #switchTo()}.{@link Options#window()}
     *
     * @return A set of window handles which can be used to iterate over all open windows.
     */
    @Override
    public Set<String> getWindowHandles() {
        Set<String> windowHandles = driver.getWindowHandles();
        logger.trace("Driver using: get window handles - Size -> " + windowHandles.size() + " , "
                + windowHandles);
        return windowHandles;
    }

    /**
     * Return an opaque handle to this window that uniquely identifies it within this driver instance.
     * This can be used to switch to this window at a later date
     *
     * @return the current window handle
     */
    @Override
    public String getWindowHandle() {
        String windowHandle =driver.getWindowHandle();
        logger.trace("Driver using: get window handle -> " + windowHandle);
        return windowHandle;
    }

    /**
     * Send future commands to a different frame or window.
     *
     * @return A TargetLocator which can be used to select a frame or window
     * @see TargetLocator
     */
    @Override
    public TargetLocator switchTo() {
        logger.info("Switching to new window or frame");
        return new TesnetTargetLocator();
    }

    /**
     * An abstraction allowing the driver to access the browser's history and to navigate to a given
     * URL.
     *
     * @return A {@link Navigation} that allows the selection of what to
     * do next
     */
    @Override
    public Navigation navigate() {
        return new TesnetNavigation();
    }

    /**
     * Gets the Option interface
     *
     * @return An option interface
     * @see Options
     */
    @Override
    public Options manage() {
        return new TesnetDriverOptions();
    }

    /**
     * @return The driver that contains this element.
     */
    @Override
    public WebDriver getWrappedDriver() {
        return this.driver;
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
        logger.trace("Driver using: get screenshot as");
        X output = ((TakesScreenshot)this.driver).getScreenshotAs(target);
        logger.info("Took a screenshot");
        return output;
    }

    /**
     * Executes JavaScript in the context of the currently selected frame or window. The script
     * fragment provided will be executed as the body of an anonymous function.
     *
     * <p>
     * Within the script, use <code>document</code> to refer to the current document. Note that local
     * variables will not be available once the script has finished executing, though global variables
     * will persist.
     *
     * <p>
     * If the script has a return value (i.e. if the script contains a <code>return</code> statement),
     * then the following steps will be taken:
     *
     * <ul>
     * <li>For an HTML element, this method returns a WebElement</li>
     * <li>For a decimal, a Double is returned</li>
     * <li>For a non-decimal number, a Long is returned</li>
     * <li>For a boolean, a Boolean is returned</li>
     * <li>For all other cases, a String is returned.</li>
     * <li>For an array, return a List&lt;Object&gt; with each object following the rules above. We
     * support nested lists.</li>
     * <li>For a map, return a Map&lt;String, Object&gt; with values following the rules above.</li>
     * <li>Unless the value is null or there is no return value, in which null is returned</li>
     * </ul>
     *
     * <p>
     * Arguments must be a number, a boolean, a String, WebElement, or a List of any combination of
     * the above. An exception will be thrown if the arguments do not meet these criteria. The
     * arguments will be made available to the JavaScript via the "arguments" magic variable, as if
     * the function were called via "Function.apply"
     *
     * @param script The JavaScript to execute
     * @param args   The arguments to the script. May be empty
     * @return One of Boolean, Long, Double, String, List, Map or WebElement. Or null.
     */
    @Override
    public Object executeScript(String script, Object... args) {
        logger.debug("Driver using: execute  script -> " + script + " , args -> " + Arrays.toString(args));
        return ((JavascriptExecutor)this.driver).executeScript(script, args);
    }

    /**
     * Execute an asynchronous piece of JavaScript in the context of the currently selected frame or
     * window. Unlike executing {@link #executeScript(String, Object...) synchronous JavaScript},
     * scripts executed with this method must explicitly signal they are finished by invoking the
     * provided callback. This callback is always injected into the executed function as the last
     * argument.
     *
     * <p>
     * The first argument passed to the callback function will be used as the script's result. This
     * value will be handled as follows:
     *
     * <ul>
     * <li>For an HTML element, this method returns a WebElement</li>
     * <li>For a number, a Long is returned</li>
     * <li>For a boolean, a Boolean is returned</li>
     * <li>For all other cases, a String is returned.</li>
     * <li>For an array, return a List&lt;Object&gt; with each object following the rules above. We
     * support nested lists.</li>
     * <li>For a map, return a Map&lt;String, Object&gt; with values following the rules above.</li>
     * <li>Unless the value is null or there is no return value, in which null is returned</li>
     * </ul>
     *
     * <p>
     * The default timeout for a script to be executed is 0ms. In most cases, including the examples
     * below, one must set the script timeout
     * {@link Timeouts#setScriptTimeout(long, TimeUnit)}  beforehand
     * to a value sufficiently large enough.
     * <p>
     * Example #1: Performing a sleep in the browser under test. <pre>{@code
     *   long start = System.currentTimeMillis();
     *   ((JavascriptExecutor) driver).executeAsyncScript(
     *       "window.setTimeout(arguments[arguments.length - 1], 500);");
     *   System.out.println(
     *       "Elapsed time: " + System.currentTimeMillis() - start);
     * }</pre>
     *
     * <p>
     * Example #2: Synchronizing a test with an AJAX application: <pre>{@code
     *   WebElement composeButton = driver.findElement(By.id("compose-button"));
     *   composeButton.click();
     *   ((JavascriptExecutor) driver).executeAsyncScript(
     *       "var callback = arguments[arguments.length - 1];" +
     *       "mailClient.getComposeWindowWidget().onload(callback);");
     *   driver.switchTo().frame("composeWidget");
     *   driver.findElement(By.id("to")).sendKeys("bog@example.com");
     * }</pre>
     *
     * <p>
     * Example #3: Injecting a XMLHttpRequest and waiting for the result: <pre>{@code
     *   Object response = ((JavascriptExecutor) driver).executeAsyncScript(
     *       "var callback = arguments[arguments.length - 1];" +
     *       "var xhr = new XMLHttpRequest();" +
     *       "xhr.open('GET', '/resource/data.json', true);" +
     *       "xhr.onreadystatechange = function() {" +
     *       "  if (xhr.readyState == 4) {" +
     *       "    callback(xhr.responseText);" +
     *       "  }" +
     *       "};" +
     *       "xhr.send();");
     *   JsonObject json = new JsonParser().parse((String) response);
     *   assertEquals("cheese", json.get("food").getAsString());
     * }</pre>
     *
     * <p>
     * Script arguments must be a number, a boolean, a String, WebElement, or a List of any
     * combination of the above. An exception will be thrown if the arguments do not meet these
     * criteria. The arguments will be made available to the JavaScript via the "arguments"
     * variable.
     *
     * @param script The JavaScript to execute.
     * @param args   The arguments to the script. May be empty.
     * @return Object of Boolean, Long, String, List, Map, WebElement, or null.
     *
     */
    @Override
    public Object executeAsyncScript(String script, Object... args) {
        logger.trace("Driver using: execute async script -> " + script + " , args -> " + Arrays.toString(args));
        return ((JavascriptExecutor)this.driver).executeAsyncScript(script, args);
    }

    /**
     * Commonly used scripts may be "pinned" to the WebDriver session,
     * allowing them to be called efficiently by their handle rather than sending the entire script across the wire for every call.
     *
     * @param  script - The Javascript to execute.
     * @return A handle (ScriptKey) which may later be used in executeScript(ScriptKey, Object...)
     */
    @Override
    public ScriptKey pin(String script) {
        return JavascriptExecutor.super.pin(script);
    }

    /**
     * Deletes the reference to a script that has previously been pinned.
     * Subsequent calls to executeScript(ScriptKey, Object...) will fail for the given key.
     * @param key the ScriptKey
     */
    @Override
    public void unpin(ScriptKey key) {
        JavascriptExecutor.super.unpin(key);
    }

    /**
     * The ScriptKeys of all currently pinned scripts.
     * @return Set of all Script Keys which were pinned
     */
    @Override
    public Set<ScriptKey> getPinnedScripts() {
        return JavascriptExecutor.super.getPinnedScripts();
    }

    /**
     * Calls a script by the ScriptKey returned by pin(String).
     * This can be thought of as inlining the pinned script and simply calling executeScript(String, Object...).
     * @param key the ScriptKey
     * @param args arguments for the script
     * @return One of Boolean, Long, Double, String, List, Map or WebElement. Or null.
     */
    @Override
    public Object executeScript(ScriptKey key, Object... args) {
        return JavascriptExecutor.super.executeScript(key, args);
    }


    /**
     Returns:
     The capabilities of the current driver
     */
    @Override
    public Capabilities getCapabilities() {
        return ((HasCapabilities)driver).getCapabilities();
    }


    /**
     A convenience method for performing the actions
     */
    @Override
    public void perform(Collection<Sequence> actions) {
        logger.trace("Driver using: perform -> " + actions.toString());
        ((Interactive)driver).perform(actions);
    }

    /**
     Clear all actions from state
     */
    @Override
    public void resetInputState() {
        logger.trace("Driver using: reset input state");
        ((Interactive)driver).resetInputState();
    }

    /**
     *
     * Allows us to convert an html page to pdf file.
     * example:
     * Path printPage = Paths.get("src/test/screenshots/PrintPageChrome.pdf");
     * driver.get("<a href="https://www.saucedemo.com/v1/inventory.html">...</a>");
     * Pdf print = driver.print(new PrintOptions());
     * Files.write(printPage, OutputType.BYTES.convertFromBase64Png(print.getContent()));
     *
     * @param printOptions  set various page properties such as: Orientation, Scale, Margin etc..
     * @return a base 64 encoded Selenium Pdf Object
     * @throws WebDriverException WebDriverException
     */

    @Override
    public Pdf print(PrintOptions printOptions) throws WebDriverException {
        logger.trace("Driver activity log: print pdf with options " + printOptions.toString());
        return ((PrintsPage) driver).print(printOptions);
    }

    /**
     * Represents a virtual authenticator.
     * see <a href="https://www.selenium.dev/documentation/webdriver/interactions/virtual_authenticator/">...</a>
     * and <a href="https://github.com/SeleniumHQ/seleniumhq.github.io/blob/trunk/examples/java/src/test/java/dev/selenium/virtual_authenticator/VirtualAuthenticatorTest.java#L72-78">...</a>
     * @param options VirtualAuthenticatorOptions (such as authentication protocol, public key etc..)
     * @return a VirtualAuthenticator object
     */
    @Override
    public VirtualAuthenticator addVirtualAuthenticator(VirtualAuthenticatorOptions options) {
        logger.trace("Driver activity log: add virtual authenticator with options: " + options.toString());
        return ((RemoteWebDriver) driver).addVirtualAuthenticator(options);
    }

    /**
     * removes a formerly added Virtual Authenticator
     *
     * @param authenticator the virtual authenticator to remove
     */
    @Override
    public void removeVirtualAuthenticator(VirtualAuthenticator authenticator) {
        logger.debug("Driver activity log: remove virtual authenticator : " + authenticator.toString());
        ((RemoteWebDriver) driver).removeVirtualAuthenticator(authenticator);
    }


    protected class TesnetDriverOptions implements WebDriver.Options {

        /**
         * Add a specific cookie. If the cookie's domain name is left blank, it is assumed that the cookie is meant for the domain of the current document.
         * Params:
         * cookie – The cookie to add.
         * @param cookie Instance of Cookie
         */
        @Override
        public void addCookie(Cookie cookie) {
            logger.trace("Driver using: options -> add cookie -> " + cookie.toString());
            driver.manage().addCookie(cookie);
        }
        /**
         Delete the named cookie from the current domain. This is equivalent to setting the named cookie's expiry date to some time in the past.
         @param name – The name of the cookie to delete
         */
        @Override
        public void deleteCookieNamed(String name) {
            logger.trace("Driver using: options -> delete cookie named -> " + name);
            driver.manage().deleteCookieNamed(name);
        }

        /**
         *
         * Delete a cookie from the browser's "cookie jar". The domain of the cookie will be ignored.
         * @param cookie – nom nom nom
         */
        @Override
        public void deleteCookie(Cookie cookie) {
            logger.trace("Driver using: options -> delete cookie named -> " + cookie.toString());
            driver.manage().deleteCookie(cookie);
        }
        /**
         Delete all the cookies for the current domain.
         */
        @Override
        public void deleteAllCookies() {
            logger.trace("Driver using: options -> delete all cookie");
            driver.manage().deleteAllCookies();
        }
        /**
         * Get all the cookies for the current domain. This is the equivalent of calling "document.cookie" and parsing the result
         * @return A Set of cookies for the current domain
         */
        @Override
        public Set<Cookie> getCookies() {
            String cookies = "Driver using: options -> get cookies: ";
            Set<Cookie> allCookies = driver.manage().getCookies();
            for (Cookie cookie : allCookies) {
                cookies += cookie.toString();
            }
            logger.debug(cookies);
            return allCookies;
        }
        //TODO:Add Javadoc
        @Override
        public Cookie getCookieNamed(String name) {
            logger.trace("Driver using: options -> get Cookie Named -> " + name);
            return driver.manage().getCookieNamed(name);
        }
        //TODO:Add Javadoc
        @Override
        public Timeouts timeouts() {
            logger.trace("Driver using: -> manage().timeouts()");
            return driver.manage().timeouts();
        }

        //TODO:Add Javadoc
        @Override
        public Window window() {
            logger.trace("Driver using: -> manage().window()");
            return driver.manage().window();
        }
        //TODO:Add Javadoc
        @Override
        public Logs logs() {
            logger.trace("Driver using: -> manage().logs()");
            return driver.manage().logs();
        }
    }
    //TODO:Add Javadoc
    private class TesnetNavigation implements Navigation {
        @Override
        public void back() {
            String back = "Driver using: Navigation -> back From : " + getCurrentUrl() + " ";
            driver.navigate().back();
            back += "To : " + getCurrentUrl();
            logger.trace(back);
        }
        //TODO:Add Javadoc
        @Override
        public void forward() {
            String forward = "Driver using: Navigation -> forward From : " + getCurrentUrl() + " ";
            driver.navigate().forward();
            forward += "To : " + getCurrentUrl();
            logger.trace(forward);
        }
        //TODO:Add Javadoc
        @Override
        public void to(String url) {
            logger.trace("Driver using: Navigation -> to url -> " + url);
            driver.navigate().to(url);
        }
        //TODO:Add Javadoc
        @Override
        public void to(URL url) {
            logger.trace("Driver using: Navigation -> to -> " + String.valueOf(url));
            driver.navigate().to(url);
        }
        //TODO:Add Javadoc
        @Override
        public void refresh() {
            logger.trace("Driver using: Navigation -> refresh URL: " + getCurrentUrl());
            driver.navigate().refresh();
        }
    }

    protected class TesnetTargetLocator implements TargetLocator {
        //TODO:Add Javadoc
        @Override
        public WebDriver frame(int index) {
            logger.trace("Driver using: switchTo -> frame -> frameIndex : " + index);
            return driver.switchTo().frame(index);
        }
        //TODO:Add Javadoc
        @Override
        public WebDriver frame(String nameOrId) {
            logger.trace("Driver using: switchTo -> frame -> frameName : " + nameOrId);
            return driver.switchTo().frame(nameOrId);
        }
        //TODO:Add Javadoc
        @Override
        public WebDriver frame(WebElement frameElement) {
            logger.trace("Driver using: switchTo -> frame -> frameElement : " + frameElement.toString());
            return driver.switchTo().frame(frameElement);
        }
        //TODO:Add Javadoc
        @Override
        public WebDriver parentFrame() {
            logger.trace("Driver using: switchTo -> parentFrame");
            return driver.switchTo().parentFrame();
        }
        //TODO:Add Javadoc
        @Override
        public WebDriver window(String nameOrHandle) {
            logger.trace("Driver using: switchTo -> window " + nameOrHandle);
            driver = driver.switchTo().window(nameOrHandle);
            if (ReportInstanceManager.getCurrentTestReport() != null)
                ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Switched to window" + nameOrHandle);
            return driver;
        }

        /**
         * Create a new window or tab and switch to it
         * for example:
         * // Opens a new tab and switches to new tab
         * driver.switchTo().newWindow(WindowType.TAB);
         * // Opens a new window and switches to new window
         * driver.switchTo().newWindow(WindowType.WINDOW);
         * @param typeHint Window or Tab
         * @return the driver after switching context to the new window
         */
        @Override
        public WebDriver newWindow(WindowType typeHint) {
            logger.trace("Driver using: switchTo -> new window " + typeHint);
            driver = driver.switchTo().newWindow(typeHint);
            if (ReportInstanceManager.getCurrentTestReport() != null)
                ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Switched to  new window " + typeHint);
            return driver;
        }

        //TODO:Add Javadoc
        @Override
        public WebDriver defaultContent() {
            logger.trace("Driver using: switchTo -> defaultContent");
            return driver.switchTo().defaultContent();
        }
        //TODO:Add Javadoc
        @Override
        public WebElement activeElement() {
            logger.trace("Driver using: switchTo -> activeElement");
            return driver.switchTo().activeElement();
        }
        //TODO:Add Javadoc
        @Override
        public Alert alert() {
            return new TesnetAlert();
        }
    }

    private class TesnetAlert implements Alert {
        //TODO:Add Javadoc
        @Override
        public void dismiss() {
            logger.trace("Driver using: switchTo -> alert -> dismiss");
            driver.switchTo().alert().dismiss();
        }
        //TODO:Add Javadoc
        @Override
        public void accept() {
            logger.trace("Driver using: switchTo -> alert -> accept");
            driver.switchTo().alert().accept();
        }
        //TODO:Add Javadoc
        @Override
        public String getText() {
            logger.trace("Driver using: switchTo -> alert -> getText");
            return driver.switchTo().alert().getText();
        }
        //TODO:Add Javadoc
        @Override
        public void sendKeys(String keysToSend) {
            logger.trace("Driver using: switchTo -> alert -> sendKeys : " + keysToSend);
            driver.switchTo().alert().sendKeys(keysToSend);
        }
    }



}

