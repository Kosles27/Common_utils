package kubernetesUtils;

import enumerations.ExecutionEnvironments;
import enumerations.MessageLevel;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static enumerations.ExecutionEnvironments.KUBERNETES;
import static propertyUtils.PropertyUtils.getGlobalProperty;
import static reportUtils.Report.reportAndLog;
import static systemUtils.SystemCommonUtils.getTestExecutionEnvironment;

@SuppressWarnings("unused")
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Checks if the test is running on Kubernetes.
     * If so, it will copy the file/folder to the shared dir
     * @author Tzvika.Sela
     * @param sourceFile full path of source file/folder
     * @since 15.01.2023
     * @author genosar.dafna
     * @since 17.12.2024
     */
    public static Path copyFileToKubernetesSharedDir(String sourceFile){
        String gridHubURL = System.getProperty("hubURL");
        if(gridHubURL == null || gridHubURL.equals("NA")) {
            reportAndLog("Test is running locally, no need to copy to shared Dir", MessageLevel.INFO);
            return Paths.get(sourceFile);
        }
        else {
            String jobName = System.getProperty("jobName");
            String buildId = System.getProperty("BuildID");
            //in case of k8s we should copy the file to the shared dir first
            String destFolder = getGlobalProperty("kubernetes_download_path") + File.separator
                    + jobName + File.separator + buildId + File.separator;

            reportAndLog(String.format("Test is running on Kubernetes, copying source file %s to shared Dir %s",sourceFile, destFolder), MessageLevel.INFO);

            String destFile = destFolder + FilenameUtils.getName(sourceFile);

            try {
                return Files.copy(Paths.get(sourceFile), Paths.get(destFile), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e){
                throw new IllegalStateException(String.format("Failed to copy file to Kubernetes Shared Dir<br>Source file: %s<br>Destination: %s<br>Error: %s", sourceFile, destFile, e.getMessage()));
            }
        }
    }

    /**
     * @return if Execution environment is Kubernetes then return the Kubernetes shared Download Folder Path (Shared dir) / otherwise return null
     * @author genosar.dafna
     * @since 26.06.2024
     */
    public static String getKubernetesDownloadFolderPath(){

        ExecutionEnvironments executionEnvironments = getTestExecutionEnvironment();

        if(executionEnvironments.equals(KUBERNETES)) {

            String jobName = System.getProperty("jobName");
            String buildId = System.getProperty("BuildID");

            String destFolder = getGlobalProperty("kubernetes_download_path") + File.separator
                    + jobName + File.separator + buildId + File.separator;

            reportAndLog(String.format("Test is running on Kubernetes, Shared Dir: %s", destFolder), MessageLevel.INFO);

            return destFolder;
        }
        else{
            logger.info(String.format("Execution environment is: %s. Kubernetes shared download folder is irrelevant", executionEnvironments));
            return null;
        }
    }
}
