package miscellaneousUtils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import systemUtils.SystemCommonUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.HashMap;

@SuppressWarnings("unused")
public class MiscellaneousUtils
{
    private static final Logger logger = LoggerFactory.getLogger(MiscellaneousUtils.class);

    /**
     * Validates the given VVL format and splits it into parts.
     * @param vvl the VVL string
     * @return an HashMap of VVL parts
     * @throws IllegalArgumentException if the format is invalid
     * @author genosar.dafna
     * @since 13.11.2024
     */
    private static HashMap<String, String> validateAndSplitVVL(String vvl) {
        if (vvl == null || vvl.isEmpty()) {
            throw new IllegalArgumentException("The VVL argument received is null or empty");
        }

        //LinkedList<String> vvlParts = new LinkedList<>();
        HashMap<String, String> vvlData = new HashMap<>();

        if(vvl.contains(",")){ //ABC,2/W
            String[] parts1 = vvl.split(",");
            if(parts1.length != 2)
                throw new IllegalArgumentException(String.format("The VVL argument received (%s) is not supported. Please add support", vvl));

            String[] parts2 = parts1[1].split("/");
            if(parts2.length != 2)
                throw new IllegalArgumentException(String.format("The VVL argument received (%s) is not supported. Please add support", vvl));

            String vessel = parts1[0];
            String voyage = parts2[0];
            String leg = parts2[1];
            vvlData.put("vessel", vessel);
            vvlData.put("voyage", voyage);
            vvlData.put("leg", leg);
        }
        else {
            String[] parts1 = vvl.split("/");
            if(parts1.length != 3)
                throw new IllegalArgumentException(String.format("The VVL argument received (%s) is not supported. Please add support", vvl));
            String vessel = parts1[0];
            String voyage = parts1[1];
            String leg = parts1[2];
            vvlData.put("vessel", vessel);
            vvlData.put("voyage", voyage);
            vvlData.put("leg", leg);
        }

        return vvlData;
    }

    /**
     * Extracts a specific part of the VVL by index.
     * @param vvl           the VVL string
     * @param requiredVvlPart  vessel, voyage, leg
     * @param throwException whether to throw an exception for invalid VVL
     * @return the requested VVL part or null if invalid and throwException is false
     * @author genosar.dafna
     * @since 13.11.2024
     */
    private static String extractPartFromVVL(String vvl, String requiredVvlPart, boolean throwException) {

        String vvlPartToReturn = null;
        try{
            HashMap<String, String> vvlParts = validateAndSplitVVL(vvl);
            vvlPartToReturn = vvlParts.get(requiredVvlPart.toLowerCase());
            if(requiredVvlPart == null){
                if(throwException)
                    throw new IllegalArgumentException(String.format("The requested VVL part '%s' is not present in VVL '%s'.", requiredVvlPart, vvl));
            }
            return vvlPartToReturn;
        }
        catch (Throwable t){
            if(throwException)
                throw t;
        }
        return vvlPartToReturn;
    }

    /**
     * Extracts the vessel from the VVL.
     * @param vvl the VVL string
     * @param throwException whether to throw an exception for invalid VVL
     * @return the vessel part of the VVL or null if invalid and throwException is false
     */
    public static String extractVesselFromVVL(String vvl, boolean throwException) {
        return extractPartFromVVL(vvl, "vessel", throwException);
    }

    /**
     * Extracts the voyage from the VVL.
     * @param vvl the VVL string
     * @param throwException whether to throw an exception for invalid VVL
     * @return the voyage part of the VVL or null if invalid and throwException is false
     */
    public static String extractVoyageFromVVL(String vvl, boolean throwException) {
        return extractPartFromVVL(vvl, "voyage", throwException);
    }

    /**
     * Extracts the leg from the VVL.
     * @param vvl the VVL string
     * @param throwException whether to throw an exception for invalid VVL
     * @return the leg part of the VVL or null if invalid and throwException is false
     */
    public static String extractLegFromVVL(String vvl, boolean throwException) {
        return extractPartFromVVL(vvl, "leg", throwException);
    }

    /**
     * This function take care in case of window certificate
     */
    public static void Robot_CertificateWindow() {
        try {

            Robot r = new Robot();
            Thread.sleep(10000);
            r.keyPress(java.awt.event.KeyEvent.VK_TAB);

            Thread.sleep(2000);
            r.keyPress(java.awt.event.KeyEvent.VK_TAB);

            Thread.sleep(2000);
            r.keyPress(java.awt.event.KeyEvent.VK_ENTER);
            Thread.sleep(2000);

        }catch(Exception e){
            logger.error("Robot_CertificateWindow: "+ e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    /**
     * Use the Robot class to handle the file upload Windows dialog
     * @param filePath file path
     * @author tseva.yehonatan
     * @since 26.09.23
     */
    public static void Robot_uploadFileByWindowsHandler(String filePath){
        try{
            Robot robot = new Robot();

            // Enter the file path (replace with your file path)
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(filePath);
            clipboard.setContents(stringSelection, null);

            // press Contol+V for pasting
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);

            // release Contol+V for pasting
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);

            // for pressing and releasing Enter
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

            // Wait for a few seconds to ensure the file is uploaded
            SystemCommonUtils.sleep(2500);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Copies a given string to the clipboard using JavaScript execution.
     * @param driver WebDriver instance to execute JavaScript
     * @param text String to be copied to the clipboard
     * @author Lesnichy.Kostya
     * @since 22.04.24
     */
    public static void copyTextToClipboard(WebDriver driver, String text) {
        // Execute JavaScript to copy text to clipboard
        ((JavascriptExecutor) driver).executeScript("navigator.clipboard.writeText(arguments[0]);", text);
    }
}
