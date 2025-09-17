package waitUtils;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

/**
 * Retry a logic, until something defined will happen<br>
 * or logic was retried several times according to count class parameter
 * use anonymous inner class for specified what needs to be retried
 * @author: Yael
 * @since 28.6.2021
 * <b>Example</b><br>
 *     random number less than 5
 *     Retry retry = new Retry(WebDriverInstanceManager.getDriverFromMap(), 3, 1, TimeUnit.SECONDS);
 *         retry.attempt(new Attemptable() {
 *             @Override
 *             public void onAttemptFail() {
 *                  logger.info(" b is less than a" );
 *             }
 *             @Override
 *             public void attempt() throws Throwable {
 *                 int a=5;
 *                 Random rand = new Random();
 *                 int b= rand.nextInt(10);
 *                  logger.info(" b is  "  + b);
 *                 if (b<a){
 *                      logger.info(" b is less than a "  + b);
 *                     throw new Error("b is less than a");
 *                 }
 *             }
 *         });
 *     <b>Example #2</b><br>
 *     verify that sendKey was perform
 *     Retry retry = new Retry(WebDriverInstanceManager.getDriverFromMap(), 3, 1, TimeUnit.SECONDS);
 *         retry.attempt(new Attemptable() {
 *             @Override
 *             public void onAttemptFail() {
 *                  logger.info(" send key was not perform" );
 *             }
 *             @Override
 *             public void attempt() throws Throwable {
 *                 Element element = driver.findElement(BY);
 *                 element.sendKey("aaa")
 *                 assertTrue(element.getText(),"aaa")
 *             }
 *         });
 */
@SuppressWarnings("unused")
public class Retry {

    private final long interval;
    private final TimeUnit unit;
    private long count;
    private WebDriver driver;

    /**
     *
     * @param driver
     * @param count - count retry
     * @param interval sleep interval between tries
     * @param unit timeUnit of wait interval
     */
    public Retry(WebDriver driver, int count, int interval, TimeUnit unit) {
        this.count = count;
        this.interval = interval;
        this.unit = unit;
        this.driver = driver;
    }

    /**
     *
     * repated on specific logic until something defined will happen <br>
     * or logic was retried several times according to count class parameter
     * Attemptable.attempt function  - try and throw exception or error if nou success
     * Attemptable.onAttemptFail function - perform while attempt throwable catch and needs retry
     * @param attemptable - interface,on using need to implement attempt() and onAttemptFail()
     * @author: Yael
     * @since 28.6.2021
     */
    public void attempt(Attemptable attemptable) {
        for (int i = 0; i < count; i++) {
            try {
                attemptable.attempt();
                return;
            } catch (Throwable e) {
                if (i == count - 1) {
                    throw new IllegalStateException(e);
                }
                    attemptable.onAttemptFail();
                }
                try {
                    unit.sleep(interval);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
        }
    }
}
