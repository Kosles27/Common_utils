package fileUtils;

import Managers.WebDriverInstanceManager;
import constantsUtils.CommonConstants;
import enumerations.MessageLevel;
import miscellaneousUtils.RandomUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;
import seleniumUtils.ElementWrapper;
import systemUtils.SystemCommonUtils;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

import static reportUtils.Report.reportAndLog;
import static systemUtils.SystemCommonUtils.sleep;

/**
 * Class holds methods to work with files
 */
@SuppressWarnings("unused")
public class FileUtils
{
    private static final Logger logger= LoggerFactory.getLogger(FileUtils.class);

    /**
     * Read the given file and return as FileReader object
     * @param filePath the file path
     * @return FileReader object
     * @throws FileNotFoundException FileNotFoundException
     * @author genosar.dafna
     * @since 29.06.2025
     */
    public static FileReader getFileReader(String filePath) throws FileNotFoundException{
        try{
            return new FileReader(filePath);
        }
        catch (FileNotFoundException e){
            throw new FileNotFoundException("File %s could not be found".formatted(filePath));
        }
    }

    /**
     * @param fileNameOrPath file name or path
     * @return the file extension, like xlsx
     * @author genosar.dafna
     * @since 26.02.2025
     */
    public static String getFileExtension(String fileNameOrPath){
        if (fileNameOrPath == null || fileNameOrPath.lastIndexOf(".") == -1) {
            return ""; // No extension found
        }
        return fileNameOrPath.substring(fileNameOrPath.lastIndexOf(".") + 1);
    }

    /**
     * @param fileNameOrPath file name or path
     * @return the file extension, like xlsx
     * @author genosar.dafna
     * @since 09.06.2025
     */
    public static String getFileExtension(Path fileNameOrPath){
        String fileName = fileNameOrPath.getFileName().toString();
        return getFileExtension(fileName);
    }

    /**
     * Move a file to the destination folder
     * @param source source path
     * @param destination destination path
     * @author genosar.dafna
     * @since 24.01.2024
     */
    public static Path moveFile(String source, String destination, boolean replaceExisting){

        Report.reportAndLog(String.format("Move file<br>From: %s<br>To: %s", source, destination), MessageLevel.INFO);

        Path sourcePath = Paths.get(source);
        Path destinationPath = Paths.get(destination);

        if(destination.endsWith("\\")){
            Path fileName = sourcePath.getFileName();
            destinationPath = destinationPath.resolve(fileName);
        }

        if (!Files.exists(sourcePath)){
            throw new IllegalArgumentException(String.format("File cannot be moved. The file cannot be found: %s", source));
        }

        try {
            Path newFilePath;

            // Move the file
            if(replaceExisting)
                newFilePath = Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            else
                newFilePath = Files.move(sourcePath, destinationPath);

            Report.reportAndLog("File moved successfully", MessageLevel.INFO);
            return newFilePath;
        }
        catch (Throwable e) {
            throw new RuntimeException(String.format("File was not moved successfully<br>Error: %s", e.getMessage()));
        }
    }

    /**
     * Upload file
     * @param uploadElement upload element (can be the upload button)
     * @param filePath file path
     * @return true / false if file was uploaded
     * @author genosar.dafna
     * @since 02.05.2023
     * @since 02.03.2025
     */
    public static boolean uploadFile(WebElement uploadElement, String filePath){

        //Check if the upload element is enabled
        if (!uploadElement.isEnabled() || (ElementWrapper.attributeExists(uploadElement, "class") && uploadElement.getDomAttribute("class").contains("disabled")))
            throw new Error(String.format("Cannot upload file %s. The upload element is disabled", filePath));

        reportUtils.Report.reportAndLog("Upload file " + filePath, MessageLevel.INFO);

        try {
            uploadElement.sendKeys(filePath);
            return true;
        }
        catch(Exception e){
            throw new Error(String.format("Failed to upload file %s<br>Error: %s", filePath, e.getMessage()));
        }
    }

    /**
     * upload file by JS
     *
     * @param driver the current WebDriver instance
     * @param inputElement input webelement
     * @param filePath file path to upload file
     * @author reed.dakota
     * @since 19.12.2023
     */
    public static void uploadFileByJS(WebDriver driver, WebElement inputElement, String filePath)
    {
        // Use JavaScript to set the value of the file input element
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", inputElement);
        inputElement.sendKeys(filePath);
        SystemCommonUtils.sleep(500);
    }


    /**
     * Delete All files from Downloads folder
     * @return true if all files deleted successfully
     * @since 02.04.2023
     * @author abo_saleh.rawand
     * @author genosar.dafna
     * @since 02.12.2024
     */
    public static boolean deleteDownloadFiles() {
        String downloadFolderPath = CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER;
        return deleteAllFilesInFolder(downloadFolderPath);
    }

    /**
     * Delete All files from folder
     * @return true if all files deleted successfully
     * @author genosar.dafna
     * @since 02.12.2024
     * @author genosar.dafna
     * @since 02.09.2025
     */
    public static boolean deleteAllFilesInFolder(String folderPath) {

        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : Objects.requireNonNull(files)) {
                if (!file.delete()) {
                    logger.error(String.format("File '%s' was not deleted", file.getName()));
                    return false;
                }
            }
            logger.info("All files deleted successfully from folder " + folderPath);
            return true;
        }
        else {
            logger.error("Folder does not exist or is not a directory." + folderPath);
            return false;
        }
    }

    /**
     * Delete the selected file
     * @param filePath full file path to delete
     * @return true/false if the file was deleted
     * @author Dafna Genosar
     * @since 23.12.2021
     * @since 14.05.2023
     */
    public static boolean deleteFile(String filePath)
    {
        logger.info(String.format("Delete File '%s'", filePath));

        File file = new File(filePath);

        if(!file.exists())
            return true;

        if(file.delete())
        {
            return true;
        }
        else
        {
            logger.error(String.format("File '%s' was not deleted", filePath));
            return false;
        }
    }

    /**
     * Delete the all files with similar names, regardless of the number at the end, like calculations.xlsx, calculations (1).xlsx, calculations (2).xlsx
     * @param folder Path of the folder that holds the files
     * @param baseFileName the base file name. like calculations.xlsx
     * @return true/false if all files were deleted
     * @author Dafna Genosar
     * @since 23.02.2025
     * @since 14.04.2025
     */
    public static boolean deleteFilesWithSameName(Path folder, String baseFileName)
    {
        baseFileName = baseFileName.replaceFirst("\\s*\\(\\d+\\)(?=\\.[^.]+$)", "");

        logger.info(String.format("Delete all files with the same name '%s'", baseFileName));

        //Get file extension
        String fileExtension = getFileExtension(baseFileName);
        fileExtension = !fileExtension.equals("")? "." + fileExtension : fileExtension;

        String fileName = (fileExtension.equals(""))? baseFileName : baseFileName.replace(fileExtension, "");

        String regex = "%s( \\(\\d+\\))?\\%s".formatted(fileName, fileExtension);

        boolean deleted = true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path file : stream) {
                if (Pattern.matches(regex, file.getFileName().toString())) {
                    Files.delete(file);
                    logger.info("Deleted: " + file);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            deleted = false;
        }

        if(!deleted)
            Report.reportAndLog("Not all files with name '%s' were deleted successfully from folder %s".formatted(baseFileName, folder), MessageLevel.ERROR);
        return deleted;
    }

    /**
     * Delete the all files with similar names, regardless of the number at the end, like calculations.xlsx, calculations (1).xlsx, calculations (2).xlsx
     * @param folderPath Path of the folder that holds the files
     * @param baseFileName the base file name. like calculations.xlsx
     * @return true/false if all files were deleted
     * @author Dafna Genosar
     * @since 14.04.2025
     */
    public static boolean deleteFilesWithSameName(String folderPath, String baseFileName)
    {
        Path path = Paths.get(folderPath);
        return deleteFilesWithSameName(path, baseFileName);
    }

    /**
     * Delete the all files with similar names, regardless of the number at the end, like calculations.xlsx, calculations (1).xlsx, calculations (2).xlsx
     * @param filePath full file path to delete
     * @return true/false if all files were deleted
     * @author Dafna Genosar
     * @since 23.02.2025
     */
    public static boolean deleteFilesWithSameName(String filePath)
    {
        Path path = Paths.get(filePath);

        //Get parent folder (directory)
        Path folder = path.getParent();

        //Get file name (without directory)
        String fileName = path.getFileName().toString();

        return deleteFilesWithSameName(folder, fileName);
    }

    /**
     * Delete all files that contain the given text, regardless of the number at the end, like calculations.xlsx, calculations (1).xlsx, calculations (2).xlsx
     * @param folder Path of the folder that holds the files
     * @param containsText the text the file to delete should contain
     * @param fileExtension file extension, like xlsx
     * @return true/false if all files were deleted
     * @author Dafna Genosar
     * @since 09.06.2025
     */
    public static boolean deleteAllFilesThatContainText(Path folder, String containsText, String fileExtension)
    {
        fileExtension = fileExtension.startsWith(".")? fileExtension.substring(1) : fileExtension;

        logger.info(String.format("Delete all '%s' files that contain text: '%s'", fileExtension, containsText));

        boolean deleted = true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path file : stream) {

                String fileName = file.getFileName().toString();

                if(getFileExtension(fileName).equals(fileExtension) && fileName.contains(containsText)) {
                    Files.delete(file);
                    logger.info("Deleted: " + fileName);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            deleted = false;
        }

        if(!deleted)
            Report.reportAndLog("Not all %s files that contain text: '%s' were deleted successfully from folder %s".formatted(fileExtension, containsText, folder), MessageLevel.ERROR);
        return deleted;
    }

    /**
     * Delete all files that contain the given text, regardless of the number at the end, like calculations.xlsx, calculations (1).xlsx, calculations (2).xlsx
     * @param folderPath Path of the folder that holds the files
     * @param containsText the text the file to delete should contain
     * @param fileExtension file extension, like xlsx
     * @return true/false if all files were deleted
     * @author Dafna Genosar
     * @since 09.06.2025
     */
    public static boolean deleteAllFilesThatContainText(String folderPath, String containsText, String fileExtension)
    {
        Path path = Paths.get(folderPath);
        return deleteAllFilesThatContainText(path, containsText, fileExtension);
    }

    /**
     * Delete all the existing files with according to partial name and file type. For example Booking Confirmation..csv
     * @param folderPathToClean - default is download folder
     * @param partialFileName file name or partial of file name
     * @param fileType file extensions like csv, pdf, html
     * @author Dafna Genosar
     * @since 23.01.2022
     */
    public static void deleteFiles(@Nullable String folderPathToClean, String partialFileName, String fileType)
    {
        logger.info(String.format("Delete all '%s' %s files from %s folder", partialFileName, fileType,folderPathToClean));
        if(folderPathToClean==null)
            folderPathToClean= CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER;
        File downloadFolderPath = new File(folderPathToClean);

        //List of all files and directories
        File[] files = downloadFolderPath.listFiles();

        //Delete all the files with the module's name
        for (File file: Objects.requireNonNull(files)) {
            if(file.isFile() && file.getName().contains(partialFileName) && file.getName().contains("." + fileType))
            {
                if(!file.delete())
                    logger.error(String.format("File '%s' was not deleted", file.getName()));
            }
        }
    }

    /**
     * Creating a file with 1 line for each text string that is sent to method
     * @param text String to be written to file in separate lines
     * @param filePath Path to file to create
     * @throws IOException When there is a problem writing to file
     */
    public static void createFileWithLinesOfText(String text,String filePath) throws IOException
    {
           Path file = createFile(filePath);
           writeLinesToFile(text, file);
    }

    /**
     * Creating a file in the location that is sent to the method
     * @param filePath Path to file to create
     * @return created file
     * @author unknown
     * @since unknown
     * @author (modifier) dafna
     * @since 02.09.2025
     */
    public static Path createFile(String filePath)
    {
        Path path = Paths.get(filePath);

        // Make sure parent directories exist
        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to create directories in path: %s<br>Error: %s".formatted(path.getParent(), e.getMessage()));
            }
        }

        // Create the file if it does not exist
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to create a file in path: %s<br>Error: %s".formatted(path, e.getMessage()));
            }
        }

        return path;
    }

    /**
     * Create a new file in the given file path if does not already exist
     * @param filePath the path of the file
     * @return the File object
     * @author genosar.dafna
     * @since 1.05.2023
     */
    public static File createNewFile(String filePath){

        File file = new File(filePath);
        return createNewFile(file);
    }

    /**
     * Create a new file in the given file path if does not already exist
     * @param file the file as File object
     * @return the File object
     * @author genosar.dafna
     * @since 1.05.2023
     */
    public static File createNewFile(File file){

        String filePath = file.getPath();

        try {
            if (file.createNewFile()) {
                logger.info(String.format("File %s was created successfully.", filePath));
            }
            else {
                logger.info(String.format("File %s already exists.", filePath));
                System.out.println("File already exists.");
            }
        }
        catch (IOException e) {
            logger.error(String.format("An error occurred while creating file %s", filePath));
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Write lines to a file
     * @param lines a list of lines
     * @param filePath the file path
     * @author genosar.dafna
     * @since 1.05.2023
     */
    public static void writeToFile(List<String> lines, String filePath){

        // create a PrintWriter object to write to the file
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileWriter(filePath));
        } catch (IOException e) {
            throw new Error(String.format("Error creating PrintWriter when trying to write to file: %s<br>Error: %s", filePath, e.getMessage()));
        }
        for (String line: lines) {
            writer.println(line);
        }

        // close the writer
        writer.close();
    }

    /**
     * Writing lines of text into the file that is sent to method
     * @param text String to be written to file in separate lines
     * @param file File to write into
     * @throws IOException When there is a problem writing to file
     */
    public static void writeLinesToFile(String text,Path file) throws IOException
    {
        try
        {
            List<String> lines = Collections.singletonList(text);
            Files.write(file, lines, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            logger.warn("Caught exception: " + e);
            throw e;
        }
    }

    /**
     * Return full path to most updated file in directory
     * @param path Directory to look for file
     * @return Full path to most updated file in directory
     * @author plot.ofek
     * @since 02.05.2021
     */
    public static String LastFileFromFolder (String path)
    {
        try {
            Path dir = Paths.get(path);  // specify your directory

            Optional<Path> lastFilePath;

            lastFilePath = Files.list(dir)    // here we get the stream with full directory listing
                    .filter(f -> !Files.isDirectory(f))  // exclude subdirectories from listing
                    .max(Comparator.comparingLong(f -> f.toFile().lastModified()));

            if ( lastFilePath.isPresent() ) // your folder may be empty
            {
                System.out.println(lastFilePath.get());
                return lastFilePath.get().toString();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }  // finally get the last file using simple comparator by lastModified field

        return null;
    }

    /**
     * this function return Date of modification file according asked string FORMAT
     * @param FilePath file path
     * @return Date of modification
     */
    public static String FileDate(String FilePath, String dateFormat)
    {
        String fileDate;
        File file = new File(FilePath);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        fileDate = sdf.format(file.lastModified());
        return fileDate;
    }

    /**
     * Returns a String of the full/absolute path to the last updated file in Downloads
     *
     * @param downloadBtn element to click which will start the download
     * @return a String of the path to the last updated file in Downloads
     * @author sela.zvika
     * @since 02.05.2021
     * @author genosar.dafna
     * @since 15.05.2023
     */
    public static synchronized String clickDownloadAndGetLastFileFromDownloads(WebElement downloadBtn) {

        return clickDownloadAndGetLastFileFromDownloads(downloadBtn, 200, 10);
    }

    /**
     * Returns a String of the full/absolute path to the last updated file in Downloads, with a specific given path
     *
     * @param downloadBtn element to click which will start the download
     * @return a String of the path to the last updated file in Downloads
     * @author ghawi.rami
     * @since 26.06.2024
     */
    public static synchronized String clickDownloadAndGetLastFileFromDownloads(WebElement downloadBtn, String path) {

        return clickDownloadAndGetLastFileFromDownloads(downloadBtn, 200, 10,path);
    }

    /**
     * Returns a String of the full/absolute path to the last updated file in Downloads
     * @param downloadBtn element to click which will start the download
     * @param sleepMilliSecAfterClick time to sleep after clicking the download element - in millisec
     * @param numberOfRetries number of retries to wait until the file is downloaded
     * @return a String of the path to the last updated file in Downloads
     * @author sela.zvika
     * @since 02.05.2021
     * @author genosar.dafna
     * @since 15.05.2023
     */
    public static synchronized String clickDownloadAndGetLastFileFromDownloads(WebElement downloadBtn, int sleepMilliSecAfterClick, int numberOfRetries) {

        //count files
        int numOfFilesBeforeDownload = countFilesInFolder(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);

        try {
            //start the download
            downloadBtn.click();
        }
        catch(Exception e){
            reportAndLog("Could not click on download button. Trying JS executor", MessageLevel.INFO);
            ElementWrapper.clickElementByJavascriptExecutor(WebDriverInstanceManager.getDriverFromMap(), downloadBtn);
        }
        sleep(sleepMilliSecAfterClick);
        String lastFile;

        try {
            int numOfFilesAfterDownload = countFilesInFolder(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);
            int retries = 0;
            while (numOfFilesBeforeDownload + 1 != numOfFilesAfterDownload && retries < numberOfRetries) {
                //waiting 1 sec for download to start
                reportAndLog("Still waiting for download to start..", MessageLevel.INFO);
                sleep(3000);
                retries += 1;
                numOfFilesAfterDownload = countFilesInFolder(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lastFile = FileUtils.LastFileFromFolder(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);
        return lastFile;
    }

    /**
     * Returns a String of the full/absolute path to the last updated file in Downloads, with given path
     * @param downloadBtn element to click which will start the download
     * @param sleepMilliSecAfterClick time to sleep after clicking the download element - in millisec
     * @param numberOfRetries number of retries to wait until the file is downloaded
     * @return a String of the path to the last updated file in Downloads
     * @author ghawi.rami
     * @since 26.06.2024
     */
    public static synchronized String clickDownloadAndGetLastFileFromDownloads(WebElement downloadBtn, int sleepMilliSecAfterClick, int numberOfRetries, String path) {

        //count files
        int numOfFilesBeforeDownload = countFilesInFolder(path);

        try {
            //start the download
            downloadBtn.click();
        }
        catch(Exception e){
            reportAndLog("Could not click on download button. Trying JS executor", MessageLevel.INFO);
            ElementWrapper.clickElementByJavascriptExecutor(WebDriverInstanceManager.getDriverFromMap(), downloadBtn);
        }
        sleep(sleepMilliSecAfterClick);
        String lastFile;

        try {
            int numOfFilesAfterDownload = countFilesInFolder(path);
            int retries = 0;
            while (numOfFilesBeforeDownload + 1 != numOfFilesAfterDownload && retries < numberOfRetries) {
                //waiting 1 sec for download to start
                reportAndLog("Still waiting for download to start..", MessageLevel.INFO);
                sleep(3000);
                retries += 1;
                numOfFilesAfterDownload = countFilesInFolder(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lastFile = FileUtils.LastFileFromFolder(path);
        return lastFile;
    }

    /**
     * Check if the file is empty of has content in it
     * @param filePath the file path
     * @return true if the file is empty, or false if the file has content
     * @author genosar.dafna
     * @since 25.04.2023
     */
    public static boolean isFileEmpty(String filePath){
        File file = new File(filePath);
        return isFileEmpty(file);
    }

    /**
     * Check if the file is empty of has content in it
     * @param file the file object
     * @return true if the file is empty, or false if the file has content
     * @author genosar.dafna
     * @since 25.04.2023
     */
    public static boolean isFileEmpty(File file){

        return !((file.exists() && file.length() > 0));
    }

    /**
     * Waiting for a file to complete its download
     * @throws Exception in case of null file
     * @return true if file doesn't have .crdownload suffix, false otherwise
     * @author sela.zvika
     * @since 02.05.2021
     */
    public static  boolean verifyFileCompletedDownloading(String fileName) throws Exception {
        if (fileName == null)
            throw new Exception("File is Null");

        if (!fileName.endsWith(".crdownload")) {
            return true;
        } else {
            //first lets check if the file still exists
            boolean fileExists = new File(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER + fileName).exists();
            int retries = 0;
            while (fileExists && retries < 15) {
                reportAndLog("Waiting up to 30 sec for download to finish", MessageLevel.INFO);
                sleep(2000);
                retries += 1;
                fileExists = new File(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER + fileName).exists();
            }
            return !fileExists;
        }


    }

    /**
     * Return num of files in a folder
     * @return num of files in folder as int
     * @param folderPath absolute folder path
     * @author sela.zvika
     * @since 02.05.2021
     * @author genosar.dafna
     * @since 15.04.2025
     */
    public static int countFilesInFolder(String folderPath){
        return Objects.requireNonNull(new File(folderPath).list()).length;
    }


    /**
     * Returns a String of the path to the last updated file in Downloads
     * @return a String of the path to the last updated file in Downloads
     * @author plot.ofek
     * @since 02.05.2021
     * @author Dafna Genosar
     * @since 23.01.2022
     */
    public static String getLastFileFromDownloads() {

        String LastFile = null;
        try {
            Report.reportAndLog("Download folder: "+CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER, MessageLevel.INFO);
            LastFile = FileUtils.LastFileFromFolder(CommonConstants.EnvironmentParams.DOWNLOADS_FOLDER);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return LastFile;
    }

    /** encode file to base 64
     *
     * @param file File object
     * @return a String of the file in base 64
     *
     * @author umflat.lior and plot.ofek
     * @since 22.9.2022
     */
    public static String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    /**
     * Duplicate file - replace if file already exist in target file
     * @param sourceFilePath - source file path
     * @param target - destination file path
     * @return - Path - the path to the target file
     * @author Yael Rozenfeld
     * @since 6.11.2022
     * @author genosar.dafna
     * @since 16.12.2024
     */
    public static Path duplicateFile(String sourceFilePath,String target)  {

        try {
           return Files.copy(Paths.get(sourceFilePath), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ioException) {
            throw new IllegalStateException(String.format("Could not copy file. <br>Source path: %s<br>Destination path: %s<br>Error: %s", sourceFilePath, target, ioException.getMessage()));
        }
    }



    /**
     * Get random file from directory
     * @param directoryPath  - path of directory to random from it
     * @return String contains path to random file, or null if no files in directory
     * @author Yael.Rozenfeld
     * @since 21.02.2023
     */
    public static String getRandomFileFromDirectory(String directoryPath){
        File directory =new File(directoryPath);
        File[] files= directory.listFiles();
        if(files!=null && files.length>0){
            Random random = new Random();
            int randomIndex = random.nextInt(files.length);
            logger.info("random file is: " + files[randomIndex].getPath());
            return files[randomIndex].getPath();

        }
        //in case of no files in folder.
        return null;
    }

    /**
     * Get count rows in file
     * @param filePath -  file path to get count rows
     * @return int contains number of random row
     * @throws FileNotFoundException FileNotFoundException
     * @author Yael Rozenfeld
     * @since 21.02.2023
     */
    public static int  getContRowsInFile(String filePath) throws IOException {
        Path path=Paths.get(filePath);
        return (int)Files.lines(path).count();

    }

    /**
     * random row from csv file
     * @param filePath -  file path to random row from it
     * @param startRndomRow - first row can be random
     * @return long contains number of random row
     * @throws FileNotFoundException FileNotFoundException
     * @author Yael Rozenfeld
     * @since 21.02.2023
     */
    public static int  randomRowFromFile(String filePath,int startRndomRow) throws IOException {
        int countRows=FileUtils.getContRowsInFile(filePath);
        if(startRndomRow<=countRows)
            return RandomUtils.getRandomNumber(startRndomRow,countRows);
        logger.error(String.format("can't random row from file: %s start random row is: %s greater than count rows in file: %s",filePath,startRndomRow,countRows));
        return -1;
    }

    /**
     * Upload file with robot, when a file dialog is opened. use it when 'sendKeys' can not set a file path.
     * @param uploadElement upload element (can be the upload button)
     * @param filePath file path
     * @return true / false if file was uploaded
     * @author ghawi.rami
     * @since 02.08.2023
     * @since 02.05.2025
     */
    public static boolean uploadFileWithRobot(WebElement uploadElement, String filePath){

        //Check if the upload element is enabled
        if (!uploadElement.isEnabled() || (ElementWrapper.attributeExists(uploadElement, "class") && uploadElement.getDomAttribute("class").contains("disabled")))
            throw new Error(String.format("Cannot upload file %s. The upload element is disabled", filePath));

        reportUtils.Report.reportAndLog("Upload file " + filePath, MessageLevel.INFO);

        try {
            uploadElement.click();
            StringSelection stringSelection = new StringSelection(filePath);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

            // Use Robot class to simulate keyboard inputs
            Robot robot = new Robot();
            robot.delay(1000); // Add delay to ensure proper focus on the file dialog

            // Paste the file path from the clipboard
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);

            robot.delay(1000); // Add delay to ensure the file path is pasted before pressing Enter

            // Press Enter to confirm the file selection
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            return true;
        }
        catch(Exception e){
            throw new Error(String.format("Failed to upload file %s<br>Error: %s", filePath, e.getMessage()));
        }
    }
}