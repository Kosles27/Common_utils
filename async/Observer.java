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
 * Implementation of the Observer pattern.<br>
 * This implementation is tailored for the async operation of WebDriver.
 * This class assumes you are familiar with the observer pattern. <br>
 * To learn more about observers  <a href="https://en.wikipedia.org/wiki/Observer_pattern">visit the wikipedia site</a></a>
 * <br>In this implementation an additional method was added to the observer pattern - isDisposed.
 * It gives control to the observer to ask the subject to stop being notifited whenever a new message arrives
 * In this project, observer is implemented @see co.verisoft.selenium.framework.async.{@link AsyncListener}
 */
public interface Observer {

    /**
     * An action to be performed once the subject has triggered the notify method.<br> This update should hold the action
     * to be performed, typically the sequence of actions needed to be executed asynchroniously.
     * Each invocation of the subject's notify method fires up this update.<br>
     * Tips:<br> 1. Since Selenium WebDriver does not support multithread operations, the async operation runs on the main
     * thread. So if you block it with long waits, the main thread will get blocked. Make sure the async script is as
     * small and thin as possible to avoid blocking the main thread. <br>
     * 2. Since there is no real context switching between the main activity and the async script, make sure you
     * save the necessary information of the driver states (e.g - current url) to leave things to be continuted once
     * you are done with your async actions.
     */
    void update();

    /**
     * Inform the subject whether the observer wish to remain on the notification list or would like to be
     * removed from it. Typically, the observer should hold a private field which initially is
     * false, and changes to true once once the observer has finished his task.
     * Since an observer cannot register itself with the subject, and cannot signal a subject that it wished
     * to be included in the notification list, once an observer is removed from the notification list, it cannot
     * be re-register. So default this method to false at first, and change it to true only when the observer has
     * finished his role.
     * For example - an observer for dismissing an automatic pop up of a page. Once the pop up has been dismissed,
     * the observer has filled his purpose and is no longer necessary
     *
     * @return true if the subject should remove the observer from the notification list,
     * false otherwise
     */
    boolean isDisposed();
}
