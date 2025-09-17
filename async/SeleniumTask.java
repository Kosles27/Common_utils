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

/**
 * This is a simple interface for an async selenium task.
 * This interface is used to describe a set of actions to be performed asynchronously.<br><br>
 * <b>Example #1</b><br>
 * The following code example creates an async script to get and print the page title to the
 * STDIO, and then asks to be unregistered from further executions.<br><br>
 * <pre>{@code
 *      AsyncListener listener = new AsyncListener();
 *       listener.setDispatchInterval(2, ChronoUnit.SECONDS);
 *      ((EventFiringWebDriver)driver).register(listener);
 *
 * Observer o = new AsyncTask(driver, new SeleniumTask() {
 *             @Override
 *             public boolean doTask() {
 *                 String pageTitle = driver.getCurrentUrl();
 *                 System.out.println("Time is " + LocalTime.now() + ", page url is " + pageTitle);
 *                 return false;
 *             }
 *         });
 *         listener.setDispatchInterval(2, ChronoUnit.SECONDS);
 *         listener.register(o);
 *         }</pre><br><br>
 *
 * <b>Example #2</b><br>
 * The following code waits for a popup to appear on the screen, and after it appears, click on dismiss
 * and then asks to be unregistered from further executions. It uses the default dispatch interval,
 * which is 1 second:<br><br>
 *
 * <pre>{@code
 * 
 *       AsyncListener listener = new AsyncListener();
 *       listener.setDispatchInterval(2, ChronoUnit.SECONDS);
 *      ((EventFiringWebDriver)driver).register(listener);
 *
 * Observer o = new AsyncTask(driver, new SeleniumTask() {
 *             @Override
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
 *         }</pre><br><br>
 */
public interface SeleniumTask {

    /**
     * Asynchronous task to be performed, repeatedly until the tasks asks to be unregistered
     *
     * @return <br><b>true</b>, meaning the task is completed and wishes to be unregistered and not to be invoked again<br>
     * <b>false</b>, meaning the task needs to run again, and should be invoked next time as well
     */
    boolean doTask();
}
