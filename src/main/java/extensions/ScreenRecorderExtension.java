package extensions;


import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import enumerations.MessageLevel;
import listeners.MonteScreenRecorder;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;

import static reportUtils.Report.reportAndLog;

/**
 * Listener to Classes tests, deletes recorded video in case of test succeed or test repeated, and saves the video in case of test failed
 * @author Ghawi Rami
 * @since 13.06.2022
 */

public class ScreenRecorderExtension implements AfterTestExecutionCallback, BeforeTestExecutionCallback
{
    /**
     * Delete recorded video in case of test succeed
     * @author Ghawi Rami
     * @since 13-6-2022
     */
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {

        String path="./report/videos/" + MonteScreenRecorder.videoName + ".avi";
        File file = new File(path);

        if((!extensionContext.getExecutionException().isPresent())||extensionContext.getExecutionException().get().getMessage().contains("repeat")){
            try{
                MonteScreenRecorder.stopRecord();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if(!file.exists()){
                reportAndLog(String.format("Recording file to delete does not exist:'%s'", file.getAbsolutePath()),MessageLevel.INFO);
            }
            else {
                boolean fileDeleted = deleteRecording(file);
                if (fileDeleted)
                    reportAndLog("File Deleted Successfully", MessageLevel.INFO);
                else
                    reportAndLog("Failed to delete the file", MessageLevel.INFO);
            }
        }
        else {
            try {
                MonteScreenRecorder.stopRecord();
                String lineToReport = String.format(
                        "<a href='%s'>Link to recording</a>" +
                                "<br>" +
                                "<span font-size=6>If the video does not work, download this exe file: " +
                                "<a href='http://codecguide.com/download_k-lite_codec_pack_basic.htm' target='_blank'>link</a>" +
                                "</span>", file.getAbsolutePath());
                ReportInstanceManager.getCurrentTestReport().log(LogStatus.FAIL, lineToReport);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete the recording
     * @param fileToDelete the recording file to delete
     * @return true/false if the file was deleted successfully
     * @author genosar.dafna
     * @since 05.09.2022
     */
    public boolean deleteRecording(File fileToDelete)
    {
        String filePath = null;
        try
        {
            filePath = fileToDelete.getAbsolutePath();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        boolean fileDeleted = fileToDelete.delete();

        if(fileDeleted && !fileToDelete.exists())
        {
            reportAndLog("Recording was deleted successfully", MessageLevel.INFO);
            return true;
        }
        else
        {
            reportAndLog(String.format("Failed to delete the recording in path:'%s'", filePath), MessageLevel.INFO);
            return false;
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        reportAndLog("Starting Video Recording: ", MessageLevel.INFO);

        try {
            MonteScreenRecorder.startRecord(extensionContext.getTestMethod().get().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

