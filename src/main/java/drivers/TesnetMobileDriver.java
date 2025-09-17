package drivers;

import io.appium.java_client.*;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.android.nativekey.PressesKey;
import io.appium.java_client.appmanagement.*;
import io.appium.java_client.battery.BatteryInfo;
import io.appium.java_client.battery.HasBattery;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.remote.SupportsLocation;
import io.appium.java_client.remote.SupportsRotation;
import io.appium.java_client.screenrecording.BaseStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.BaseStopScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.openqa.selenium.NoSuchContextException;
import org.openqa.selenium.*;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.html5.RemoteLocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Tesnet mobile driver. A driver for mobile application. <b>This driver refers to Android driver and
 * ios driver only. All other drivers are not supported by TesnetMobileDriver</b>. The driver
 * supports both local and remote driver:- <br>
 *
 * <p>
 * Tesnet mobile driver is a concrete class which implements all the interfaces AppiumDriver and DefaultGenericMobileDriver. (MobileDriver<T>)</T> <br>
 * <p>
 * TesnetMobileDriver implements WebDriver behavior, and in addition adds functionality. The main functionality which
 * is currently supported:<br>
 * 2. Extended logging<br>
 * 3. All available events are registered. See events in the "See Also" section<br>
 * <br>
 *
 * @since July 2021
 */
@SuppressWarnings("hiding")
public class TesnetMobileDriver<T extends WebElement> extends TesnetWebDriver
        implements SupportsContextSwitching, SupportsRotation, SupportsLocation,
        HidesKeyboard, HasDeviceTime, PullsFiles,
        InteractsWithApps, SupportsLegacyAppManagement,
        HasAppStrings, PerformsTouchActions, HasOnScreenKeyboard, LocksDevice,
        PushesFiles, CanRecordScreen, HasBattery, HasSettings, PressesKey {


    private static final Logger logger = LoggerFactory.getLogger(TesnetMobileDriver.class);

    public TesnetMobileDriver(AppiumDriver driver) {
        super(driver);
    }



    /**
     * Activates the given app if it installed, but not running or if it is running in the
     * background.
     *
     * @param bundleId the bundle identifier (or app id) of the app to activate.
     * @param options  the set of activation options supported by the
     *                 particular platform.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void activateApp(String bundleId, @Nullable BaseActivateApplicationOptions options) {
        logger.debug("Appium Driver using: activateApp -> " + bundleId);
        ((InteractsWithApps) this.driver).activateApp(bundleId);
    }



    /**
     * Switch the focus of future commands for this driver to the context with the given name.
     *
     * @param name The name of the context as returned by {@link #getContextHandles()}.
     * @return This driver focused on the given window.
     * @throws NoSuchContextException If the context cannot be found.
     */
    @Override
    public WebDriver context(String name) {
        logger.debug("Appium Driver using: context -> " + name);
        return ((SupportsContextSwitching) driver).context(name);
    }

    @Override
    public Response execute(String driverCommand, Map<String, ?> parameters) {
        logger.debug("Appium Driver using: execute -> " + driverCommand);
        return ((SupportsContextSwitching) driver).execute(driverCommand, parameters);
    }

    @Override
    public Response execute(String driverCommand) {
        logger.debug("Appium Driver using: execute -> " + driverCommand);
        return ((SupportsContextSwitching) driver).execute(driverCommand);
    }


    /**
     * Get all defined Strings from an app for the default language.
     *
     * @return a map with localized strings defined in the app
     */
    @Override
    public Map<String, String> getAppStringMap() {
        logger.debug("Appium Driver using: getAppStringMap");
        return ((HasAppStrings) driver).getAppStringMap();
    }

    /**
     * Get all defined Strings from an app for the specified language.
     *
     * @param language strings language code
     * @return a map with localized strings defined in the app
     */
    @Override
    public Map<String, String> getAppStringMap(String language) {
        logger.debug("Appium Driver using: getAppStringMap -> " + language);
        return ((HasAppStrings) driver).getAppStringMap(language);
    }

    /**
     * Get all defined Strings from an app for the specified language and
     * strings filename.
     *
     * @param language   strings language code
     * @param stringFile strings filename
     * @return a map with localized strings defined in the app
     */
    @Override
    public Map<String, String> getAppStringMap(String language, String stringFile) {
        logger.debug("Appium Driver using: getAppStringMap -> " + language + " , " + stringFile);
        return ((HasAppStrings) driver).getAppStringMap(language, stringFile);
    }



    /**
     * Return an opaque handle to this context that uniquely identifies it within this driver
     * instance. This can be used to switch to this context at a later date.
     *
     * @return The current context handle.
     */
    @Override
    public String getContext() {
        logger.debug("Appium Driver using: getContext");
        return ((SupportsContextSwitching) driver).getContext();
    }

    /**
     * Return a set of context handles which can be used to iterate over all contexts of this
     * WebDriver instance.
     *
     * @return A set of context handles which can be used to iterate over available contexts.
     */
    @Override
    public Set<String> getContextHandles() {
        logger.debug("Appium Driver using: getContextHandles");
        return ((SupportsContextSwitching) driver).getContextHandles();
    }

    /**
     * Gets device date and time for both iOS(host time is returned for simulators) and Android devices.
     * The default format since Appium 1.8.2 is `YYYY-MM-DDTHH:mm:ssZ`, which complies to ISO-8601.
     *
     * @return Device time string
     */
    @Override
    public String getDeviceTime() {
        logger.debug("Appium Driver using: getDeviceTime");
        return ((HasDeviceTime) driver).getDeviceTime();
    }

    /**
     * Gets device date and time for both iOS(host time is returned for simulators) and Android devices.
     *
     * @param format The set of format specifiers. Read
     *               https://momentjs.com/docs/ to get the full list of supported
     *               datetime format specifiers. The default format is
     *               `YYYY-MM-DDTHH:mm:ssZ`, which complies to ISO-8601
     * @return Device time string
     */
    @Override
    public String getDeviceTime(String format) {
        logger.debug("Appium Driver using: getDeviceTime -> " + format);
        return ((HasDeviceTime) driver).getDeviceTime(format);
    }




    /**
     * @return the current screen orientation of the browser
     */
    @Override
    public ScreenOrientation getOrientation() {
        logger.debug("Appium Driver using: getOrientation");
        return ((SupportsRotation) driver).getOrientation();
    }



    /**
     * Hides the keyboard if it is showing.
     */
    @Override
    public void hideKeyboard() {
        logger.debug("Appium Driver using: hideKeyboard");
        ((HidesKeyboard) driver).hideKeyboard();
    }

    /**
     * Install an app on the mobile device.
     *
     * @param appPath path to app to install.
     */
    @Override
    public void installApp(String appPath) {
        logger.debug("Appium Driver using: installApp");
        ((InteractsWithApps) driver).installApp(appPath);
    }


    /**
     * Install an app on the mobile device.
     *
     * @param appPath path to app to install or a remote URL.
     * @param options Set of the corresponding instllation options for
     *                the particular platform.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void installApp(String appPath, @Nullable BaseInstallApplicationOptions options) {
        logger.debug("Appium Driver using: installApp -> " + appPath);
        ((InteractsWithApps) driver).installApp(appPath, options);
    }

    /**
     * Checks if an app is installed on the device.
     *
     * @param bundleId bundleId of the app.
     * @return True if app is installed, false otherwise.
     */
    @Override
    public boolean isAppInstalled(String bundleId) {
        logger.debug("Appium Driver using: installApp -> " + bundleId);
        return ((InteractsWithApps) driver).isAppInstalled(bundleId);
    }



    /**
     * Launches the app, which was provided in the capabilities at session creation,
     * and (re)starts the session.
     */
    @Override
    public void launchApp() {
        logger.debug("Appium Driver using: launchApp");
        ((SupportsLegacyAppManagement) driver).launchApp();
    }

    @Override
    public RemoteLocationContext getLocationContext() {
        RemoteLocationContext ctx = ((SupportsLocation) this.driver).getLocationContext();
        logger.debug("Appium Driver using: getLocationContext-> context is " + ctx);
        return ctx;
    }

    /**
     * Gets the physical location of the browser.
     *
     * @return A {@link Location} containing the location information. Returns null if the location is
     * not available
     */
    @Override
    public Location location() {
        Location location = ((SupportsLocation) this.driver).location();
        logger.debug("Appium Driver using: location-> location is " + location);
        return location;
    }



    /**
     * Performs multiple TouchAction gestures at the same time, to simulate
     * multiple fingers/touch inputs. See the Webriver 3 spec
     * https://dvcs.w3.org/hg/webdriver/raw-file/default/webdriver-spec.html
     * It's more convenient to call the perform() method of the MultiTouchAction
     * object.
     * All the existing multi touch actions will be wiped out after this method
     * is called.
     *
     * @param multiAction the MultiTouchAction object to perform.
     */
    @Override
    public MultiTouchAction performMultiTouchAction(MultiTouchAction multiAction) {
        MultiTouchAction action = ((PerformsTouchActions) this.driver).performMultiTouchAction(multiAction);
        logger.debug("Appium Driver using: performMultiTouchAction -> result: " + action);
        return action;
    }

    /**
     * Performs a chain of touch actions, which together can be considered an
     * entire gesture. See the Webriver 3 spec
     * https://dvcs.w3.org/hg/webdriver/raw-file/default/webdriver-spec.html
     * It's more convenient to call the perform() method of the TouchAction
     * object itself.
     * All the existing touch action parameters will be wiped out after this method
     * is called.
     *
     * @param touchAction A TouchAction object, which contains a list of individual
     *                    touch actions to perform
     * @return the same touch action object
     */
    @SuppressWarnings("rawtypes")
    @Override
    public TouchAction performTouchAction(TouchAction touchAction) {
        logger.debug("Appium Driver using: performTouchAction");
        return ((PerformsTouchActions) driver).performTouchAction(touchAction);
    }

    /**
     * Pull a file from the simulator/device.
     * On iOS the server should have ifuse
     * libraries installed and configured properly for this feature to work
     * on real devices.
     * On Android the application under test should be
     * built with debuggable flag enabled in order to get access to its container
     * on the internal file system.
     *
     * @param remotePath If the path starts with <em>@applicationId/</em>/ prefix, then the file
     *                   will be pulled from the root of the corresponding application container.
     *                   Otherwise the root folder is considered as / on Android and
     *                   on iOS it is a media folder root (real devices only).
     * @return A byte array of Base64 encoded data.
     * @see <a href="https://github.com/libimobiledevice/ifuse">iFuse GitHub page6</a>
     * @see <a href="https://github.com/osxfuse/osxfuse/wiki/FAQ">osxFuse FAQ</a>
     * @see <a href="https://developer.android.com/studio/debug/">'Debug Your App' developer article</a>
     */
    @Override
    public byte[] pullFile(String remotePath) {
        logger.debug("Appium Driver using: pullFile -> " + remotePath);
        return ((PullsFiles) driver).pullFile(remotePath);
    }

    /**
     * Pull a folder content from the simulator/device.
     * On iOS the server should have ifuse
     * libraries installed and configured properly for this feature to work
     * on real devices.
     * On Android the application under test should be
     * built with debuggable flag enabled in order to get access to its container
     * on the internal file system.
     *
     * @param remotePath If the path starts with <em>@applicationId/</em> prefix, then the folder
     *                   will be pulled from the root of the corresponding application container.
     *                   Otherwise the root folder is considered as / on Android and
     *                   on iOS it is a media folder root (real devices only).
     * @return A byte array of Base64 encoded zip archive data.
     * @see <a href="https://github.com/libimobiledevice/ifuse">iFuse GitHub page6</a>
     * @see <a href="https://github.com/osxfuse/osxfuse/wiki/FAQ">osxFuse FAQ</a>
     * @see <a href="https://developer.android.com/studio/debug/">'Debug Your App' developer article</a>
     */
    @Override
    public byte[] pullFolder(String remotePath) {
        logger.debug("Appium Driver using: pullFolder -> " + remotePath);
        return ((PullsFiles) driver).pullFolder(remotePath);
    }

    /**
     * Queries the state of an application.
     *
     * @param bundleId the bundle identifier (or app id) of the app to query the state of.
     * @return one of possible {@link ApplicationState} values,
     */
    @Override
    public ApplicationState queryAppState(String bundleId) {
        logger.debug("Appium Driver using: queryAppState -> " + bundleId);
        return ((InteractsWithApps) driver).queryAppState(bundleId);
    }

    /**
     * Remove the specified app from the device (uninstall).
     *
     * @param bundleId the bundle identifier (or app id) of the app to remove.
     * @return true if the uninstall was successful.
     */
    @Override
    public boolean removeApp(String bundleId) {
        logger.debug("Appium Driver using: removeApp -> " + bundleId);
        return ((InteractsWithApps) driver).removeApp(bundleId);
    }

    /**
     * Remove the specified app from the device (uninstall).
     *
     * @param bundleId the bundle identifier (or app id) of the app to remove.
     * @param options  the set of uninstall options supported by the
     *                 particular platform.
     * @return true if the uninstall was successful.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean removeApp(String bundleId, @Nullable BaseRemoveApplicationOptions options) {
        logger.debug("Appium Driver using: removeApp -> " + bundleId);
        return ((InteractsWithApps) driver).removeApp(bundleId, options);
    }

    /**
     * Resets the currently running app together with the session.
     */
    @Override
    public void resetApp() {
        logger.debug("Appium Driver using: resetApp");
        ((SupportsLegacyAppManagement) driver).resetApp();
    }

    /**
     * Changes the rotation of the browser window.
     */
    @Override
    public void rotate(DeviceRotation rotation) {
        logger.debug("Appium Driver using: rotate");
        ((SupportsRotation) driver).rotate(rotation);
    }

    /**
     * Changes the orientation of the browser window.
     *
     * @param orientation the desired screen orientation
     */
    @Override
    public void rotate(ScreenOrientation orientation) {
        logger.debug("Appium Driver using: rotate -> " + orientation);
        ((SupportsRotation) driver).rotate(orientation);
    }

    /**
     * @return DeviceOrientation describing the current screen rotation of the browser window
     */
    @Override
    public DeviceRotation rotation() {
        logger.debug("Appium Driver using: rotation");
        return ((SupportsRotation) driver).rotation();
    }

    /**
     * Runs the current app as a background app for the time
     * requested. This is a synchronous method, it returns after the back has
     * been returned to the foreground.
     *
     * @param duration The time to run App in background. Minimum time resolution is one second
     */
    @Override
    public void runAppInBackground(Duration duration) {
        logger.debug("Appium Driver using: runAppInBackground -> duration: " + duration);
        ((InteractsWithApps) this.driver).runAppInBackground(duration);
    }

    /**
     * Sets the physical location.
     *
     * @param location A {@link Location} containing the new location information
     */
    @Override
    public void setLocation(Location location) {
        logger.debug("Appium Driver using: setLocation-> location is " + location);
        ((SupportsLocation) this.driver).setLocation(location);
    }

    /**
     * Terminate the particular application if it is running.
     *
     * @param bundleId the bundle identifier (or app id) of the app to be terminated.
     * @return true if the app was running before and has been successfully stopped.
     */
    @Override
    public boolean terminateApp(String bundleId) {
        boolean terminate = ((InteractsWithApps) this.driver).terminateApp(bundleId);
        logger.debug("Appium Driver using: terminateApp -> app: " + bundleId + " result: " + terminate);
        return terminate;
    }

    @Override
    public void closeApp() {
        logger.debug("Appium Driver using: closeApp ");
        ((SupportsLegacyAppManagement) this.driver).closeApp();
    }

    /**
     * Terminate the particular application if it is running.
     *
     * @param bundleId the bundle identifier (or app id) of the app to be terminated.
     * @param options  the set of termination options supported by the
     *                 particular platform.
     * @return true if the app was running before and has been successfully stopped.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean terminateApp(String bundleId, @Nullable BaseTerminateApplicationOptions options) {
        boolean terminate = ((InteractsWithApps) this.driver).terminateApp(bundleId, options);
        logger.debug("Appium Driver using: terminateApp -> app: " + bundleId + " result: " + terminate);
        return terminate;
    }

    @Override
    public String toString() {
        return driver.toString();
    }




    @Override
    public WebDriver getWrappedDriver() {
        return driver;
    }

    @Override
    public T findElement(By by) {
        return (T) super.findElement(by);
    }

    @Override
    public List findElements(By by) {
        return super.findElements(by);
    }


    @Override
    public void pressKey(KeyEvent keyEvent) {
        PressesKey.super.pressKey(keyEvent);
    }

    @Override
    public void longPressKey(KeyEvent keyEvent) {
        PressesKey.super.longPressKey(keyEvent);
    }

    @Override
    public BatteryInfo getBatteryInfo() {
        return null;
    }

    @Override
    public boolean isKeyboardShown() {
        return HasOnScreenKeyboard.super.isKeyboardShown();
    }

    @Override
    public void lockDevice() {
        LocksDevice.super.lockDevice();
    }

    @Override
    public void lockDevice(Duration duration) {
        LocksDevice.super.lockDevice(duration);
    }

    @Override
    public void unlockDevice() {
        LocksDevice.super.unlockDevice();
    }

    @Override
    public boolean isDeviceLocked() {
        return LocksDevice.super.isDeviceLocked();
    }

    @Override
    public void pushFile(String remotePath, byte[] base64Data) {
        PushesFiles.super.pushFile(remotePath, base64Data);
    }

    @Override
    public void pushFile(String remotePath, File file) throws IOException {
        PushesFiles.super.pushFile(remotePath, file);
    }

    @Override
    public <T extends BaseStartScreenRecordingOptions> String startRecordingScreen(T options) {
        return CanRecordScreen.super.startRecordingScreen(options);
    }

    @Override
    public String startRecordingScreen() {
        return CanRecordScreen.super.startRecordingScreen();
    }

    @Override
    public <T extends BaseStopScreenRecordingOptions> String stopRecordingScreen(T options) {
        return CanRecordScreen.super.stopRecordingScreen(options);
    }

    @Override
    public String stopRecordingScreen() {
        return CanRecordScreen.super.stopRecordingScreen();
    }
}
