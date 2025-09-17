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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Implementation of the Observer pattern.<br>
 * This implementation is tailored for the async operation of WebDriver.
 * This interface assumes you are familiar with the observer pattern. <br>
 * To learn more about observers  <a href="https://en.wikipedia.org/wiki/Observer_pattern">visit the wikipedia site</a></a>
 * The subject is closely coupled with the co.verisoft.selenium.framework.async.{@link Observer} class
 * In this framework, the subject is implemented by co.verisoft.selenium.framework.async.{@link AsyncListener} <br>
 * The interface was designed to be triggered without any data, rather serve as a trigger for async scripts to perform
 * their async operation
 */
    public interface Subject {

    /**
     * Registers a new observer to be notified. Typically it adds an object which implements the Observer interface
     * to an ArrayList. Once the observer is on the list, each invocation of notify will trigger an update for all
     * observers on the list
     *
     * @param o Observer object to be added
     */
    void register(Observer o);

    /**
     * Unregister an existing observer, and stop notifying the observer upon invocation of notifyObservers method
     *
     * @param o Observer object to be removed
     */
    void unregister(Observer o);


    /**
     * Notify all observers when time is due to execute async
     * Typically, when the implementation uses an ArrayList to keep track of all the observes, this method will
     * loop through the list and activate the update method for each observer
     */
    void notifyObservers();

    void beforeFindBy(By by, WebElement element, WebDriver driver);
}
