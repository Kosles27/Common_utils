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

import org.javatuples.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A listener to enable asynchronous operations. The listener is a Selenium
 * WebDrvier based listener which implements both the Junit 5 listener design
 * and the observer design pattern.<br>
 * <br>
 * <b>VeriSoft's framework asynchronous operation model explained:</b><br>
 * Since WebDriver is a single thread application, and it's operations are not
 * thread safe, a solution is needed to async operations within a single thread.
 * <br>
 * org.openqa.selenium.{@lind WebDriver} @see
 * JavascriptExecutor#executeAsyncScript(String, Object...) method does not
 * provide a solution since the js is indeed executed asynchronously, however
 * WebDriver does not, hence it is hanged while it waits for the JS to finish
 * it's execution.<br>
 * So, the current solution is based on the JS model of running async operations
 * on the same thread. It is async rather than paralel. In order to run
 * operations in an async way, breaking points during the execution are needed.
 * The most common breaking point in a Selenium WebDriver scenario is the
 * findElement operation. It is the most common operation and the most
 * frequently used operation. So... We hooked on, using the listener and
 * EventFiringWebDriver mechnism to hook in before every single findElement
 * method is invoked.<br>
 * In order to to not overdue, a time interval was introduced, so if <br>
 * a. a findElement method was invoked, and <br>
 * b. interval has elapsed, a method will be called. <br>
 * We use the observer pattern to manage all the async code in one place. If you
 * are not familiar with the observer patter,
 * <a href="https://en.wikipedia.org/wiki/Observer_pattern">visit the wikipedia
 * site</a></a>. Essentially, this class serves as both listener, which
 * implements the SearchingEventListener interface, and subject (from the
 * observer mechanism), which implements the Subject interface.
 *
 * @author Nir Gallner
 */
public class AsyncListener implements Subject {

    private static final Logger logger = Logger.getLogger(AsyncListener.class.getName());
    private final boolean isDebug = true;

    private LocalDateTime baseTime;
    private Pair<Integer, ChronoUnit> interval;

    private final List<Observer> observers = new ArrayList<Observer>();


    public AsyncListener(int dispatchInterval, ChronoUnit timeUnit) {
        interval = new Pair<>(1, ChronoUnit.SECONDS);
        this.setDispatchInterval(dispatchInterval, timeUnit);
        baseTime = LocalDateTime.now();
    }

    public AsyncListener() {
        interval = new Pair<Integer, ChronoUnit>(1, ChronoUnit.SECONDS);
        baseTime = LocalDateTime.now();
    }

    /**
     * Setter for the dispatch interval and the time unit.
     * The defailt valie of the dispatcher is 1 second and it is the minimum
     * dispatcher possible. If tried to set less than 1 second, setter will not
     * update the values
     *
     * @param newInterval new interval for invocation
     * @param newTimeUnit timeunit coupled with
     */
    public void setDispatchInterval(int newInterval, ChronoUnit newTimeUnit) {
        LocalDateTime t = LocalDateTime.now();
        LocalDateTime t1 = t.plus(newInterval, newTimeUnit);
        if (t1.minus(1, ChronoUnit.SECONDS).isAfter(t)) {
            interval = interval.setAt0(newInterval);
            interval = interval.setAt1(newTimeUnit);
        }

    }

    /**
     * Getter
     *
     * @return interval field
     */
    public Pair<Integer, ChronoUnit> getDispatchInterval() {
        return interval;
    }


    @Override
    public void register(Observer o) {
        if (isDebug)
            logger.finest("Added a new observer to the list " + o.toString()+ " at index: " + observers.size());
        this.observers.add(o);
    }

    @Override
    public void unregister(Observer o) {
        int index = observers.indexOf(o);
        if (index == -1) {
            if (isDebug)
                logger.finest("Attempt to delete an unregistered observer " + o.toString());
            return;
        }

        if (isDebug)
            logger.finest("Observer " + index + 1 + " Deleted");
        observers.remove(o);
    }

    /**
     * Unregisters all observers from subject
     */
    public void unregisterAll() {
        observers.forEach(observer -> unregister(observer));
    }


    @Override
    public void notifyObservers() {
        observers.forEach(observer -> observer.update());
    }

    /**
     * Loops through the observers list and looks for observers which flags themselves as observers
     * who want to unregister themselves from the subject's list.
     * Technically, the method calls observer.isDisposed() for each observer on the list, and if the
     * result is true, the method performs unregister(observer)
     */
    public void collectGarbage() {
        List<Observer> observersCopy = new ArrayList<>(observers);
        observersCopy.forEach(observer -> {
            if (observer.isDisposed())
                unregister(observer);
        });
    }


    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
	    LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.minus(interval.getValue0(), interval.getValue1()).isAfter(baseTime)) {
            baseTime = currentTime;
            notifyObservers();
            collectGarbage();
        }
    }


}
