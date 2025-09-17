package extensions;

import Store.StoreManager;
import Store.StoreType;
import customAnnotations.DriverLogs;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * this extension will hold driver optional capabilities related flags on the Thread's STORE.
 * currently it only handles @DriverLogs annotation if it was injected to test.
 * @DriverLogs annotation indicates we are starting the driver with extended network logs capability activated
 *
 * Example usage for iterating extended performance logs:
 *
 * @ExtendWith{WebDriverOptionalCapabilitiesFlagExtension.class}
 * Class Demo{
 *     @Test
 *     @DriverLogs
 *     public void sendNotificationUsingDashboard(){
 *     driver.get(<url>)
 *     for (LogEntry log: WebDriverInstanceManager.getDriverFromMap().manage().logs().get(LogType.PERFORMANCE)) {
 *             if (log.getMessage().contains("mailto:?subject=")){
 *                 mailRequestJson = log.getMessage();
 *                 break;
 *             }
 *         }
 *
 *     }
 * }
 *
 *
 * @author - Tzvika Sela
 * @since - 08.05.2022
 */
public class WebDriverOptionalCapabilitiesFlagExtension implements  BeforeEachCallback {
    private Logger logger = LoggerFactory.getLogger(WebDriverOptionalCapabilitiesFlagExtension.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        DriverLogs n = extensionContext.getTestMethod().get().getAnnotation(DriverLogs.class);
        if (n == null){
            StoreManager.getStore(StoreType.LOCAL_THREAD).putValueInStore("isFullLogging",false);
        }
        else{
            StoreManager.getStore(StoreType.LOCAL_THREAD).putValueInStore("isFullLogging",true);
        }

    }



}
