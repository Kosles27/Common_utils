package listeners;

import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Listener to WebDriver that writes to Report the actions that were taken (e.g. click, refresh etc.).
 * @author plot.ofek
 * @since 04.05.2021
 */
public class WebDriverActionReporter implements WebDriverListener {


    @Override
    public void beforeAccept(Alert alert) {
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Alert accepted");
    }

    @Override
    public void afterAccept(Alert alert) {
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Alert was dismissed");
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        if(ReportInstanceManager.getCurrentTestReport()!=null)
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Navigated to " + url);
    }


    @Override
    public void afterBack(WebDriver.Navigation navigation){
        if(ReportInstanceManager.getCurrentTestReport()!=null)
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Navigated back to previous web page");
    }


    @Override
    public void afterForward(WebDriver.Navigation navigation) {
        ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO, "Navigated to next web page");
    }


    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {

    }

}
