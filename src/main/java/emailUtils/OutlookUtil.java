package emailUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Date;


public class OutlookUtil {
    Store store;
    Folder inbox, used;
    String email;
    String password;
    String subject;
    Date receivedDate;
    String content;
    Session session;

    private RetrievedEmail retrievedEmail = new RetrievedEmail();

    public OutlookUtil() {
    }

    public OutlookUtil(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public boolean VerifyElementDisplayedOnMail(WebDriver outLookDriver, String xPathElem)
    {
      return (outLookDriver.findElements(By.xpath(xPathElem)).size()>0);

    }

    public void scrollTOElementInOutlook (WebDriver outLookDriver,WebElement Element)
    {
        System.out.println(Element.getLocation());
        ((JavascriptExecutor) outLookDriver).executeScript("scroll" + Element.getLocation());
    }


}
