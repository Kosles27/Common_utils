package systemUtils;
import enumerations.Browsers;

import java.io.IOException;
public class ExecuteRunTimeCmd {
    /**
     * kill browser - according to browser type
     * @param browserName
     * @throws IOException
     * @author Yael.Rozenfeld
     * @since 5.4.2022
     */
    public static void  BrowserKiller(Browsers browserName) throws IOException {
        switch (browserName) {
            case CHROME:
                Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
            case FIREFOX:
                Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
                break;
            case EDGE:
                Runtime.getRuntime().exec("taskkill /F /IM msedge.exe");
                break;
            case EXPLORER:
                Runtime.getRuntime().exec("taskkill /F /IM iexplorer.exe");
                break;
            case SAFARI:
                Runtime.getRuntime().exec("taskkill /F /IM safari.exe");
                break;
        }




    }

}
