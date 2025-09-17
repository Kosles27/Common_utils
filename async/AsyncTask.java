package async;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

/**
 * Defines an async set of operations to be executed during the test, in an
 * async manner.<br>
 * <b>Important: tasks are executed in the same thread as the WebDriver main
 * thread.</b> It means that: <br>
 * 1. You should avoid long async operations, or use Wait Until in your async
 * operation. It will block the main thread and prevents execution. <br>
 * 2. The async code will not be performing context switching - the code will
 * run from start to finish once it is invoked. and, <br>
 * 3. The async code uses the same driver as the test. You should consider it
 * when wrapping up the async code - bring the driver back to the position where
 * it can continue the test from the point it was paused.<br>
 * <br>
 * <p>
 * Further practices to consider:<br>
 * 1. By default, the script is set to run each time a driver.findElement() is
 * invoked. You should change it to a different dispatch time. For example, to
 * change the dispatch time to 2 seconds use
 *
 * <pre>
 * {@code listener.setDispatchInterval(2, ChronoUnit.SECONDS);}
 * </pre>
 *
 * 2. doTask() returns a boolean value. True, means the async task is done and
 * it will not be invoked again. False means the async task is not done (e.g
 * failed to locate an elements) and should be invoked next time as well.<br>
 * 3. Don't forget to register the observer with
 *
 * <pre>
 * {@code listener.register(o);}
 * </pre>
 *
 * otherwise it will not be invoked at all. <br>
 * <br>
 *
 * <b>Example #1</b><br>
 * The following code example creates an async script to get and print the page
 * title to the STDIO, and then asks to be unregistered from further
 * executions.<br>
 * <br>
 *
 * <pre>
 * {
 *     &#64;code
 *      AsyncListener listener = new AsyncListener();
 *       listener.setDispatchInterval(2, ChronoUnit.SECONDS);
 *      ((EventFiringWebDriver)driver).register(listener);
 * 
 *      Observer o = new AsyncTask(driver, new SeleniumTask() {
 * 	&#64;Override
 * 	public boolean doTask() {
 * 	    String pageTitle = driver.getCurrentUrl();
 * 	    System.out.println("Time is " + LocalTime.now() + ", page url is " + pageTitle);
 * 	    return false;
 * 	}
 *     });
 *     
 *     listener.register(o);
 * }
 * </pre>
 *
 * <br>
 * <br>
 *
 * <b>Example #2</b><br>
 * The following code waits for a popup to appear on the screen, and after it
 * appears, click on dismiss and then asks to be unregistered from further
 * executions. It uses the default dispatch interval, which is 1 second:<br>
 * <br>
 *
 * <pre>
 * {@code
 *      AsyncListener listener = new AsyncListener();
 *       listener.setDispatchInterval(2, ChronoUnit.SECONDS);
 *      ((EventFiringWebDriver)driver).register(listener);
 *
 * Observer o = new AsyncTask(driver, new SeleniumTask() {
 *             &#64;Override
 *             public boolean doTask() {
 *                 boolean result = false;
 *
 *                 List<WebElement> elements = driver.findElements(By.id("popup-button-dismiss-locator"));
 *                 if (elements.isEmpty())
 *                     return false;
 *
 *                 try{
 *                     elements.get(0).click();
 *                     result = true;
 *                 }
 *                 catch(Throwable t)
 *                     result = false;
 *
 *                 return result;
 *             }
 *         });
 *         listener.register(o);
 *         }
 * </pre>
 *
 * <br>
 * <br>
 */
public class AsyncTask implements Observer {

    private static final Logger logger = Logger.getLogger(AsyncTask.class.getName());

    
    private static final boolean isDebug = true;

    private WebDriver driver;
    private boolean dispose;
    private SeleniumTask task;

    /**
     * Default c-tor. It is recommended that SeleniumTask object will
     * be implemented anonymously. see class description for detailed informatio and examples.
     *
     * @param driver webDriver instance
     * @param task   async task to perform
     */
    public AsyncTask(WebDriver driver, SeleniumTask task) {
        dispose = false;
        this.driver = driver;
        this.task = task;
    }

    @Override
    public void update() {
        dispose = task.doTask();
    }

    @Override
    public boolean isDisposed() {
        return dispose;
    }
}

