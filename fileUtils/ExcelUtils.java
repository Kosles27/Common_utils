package fileUtils;


import Managers.ReportInstanceManager;
import collectionUtils.ListUtils;
import com.github.pjfanning.xlsx.SharedStringsImplementationType;
import com.github.pjfanning.xlsx.StreamingReader;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import dateTimeUtils.DateUtils;
import enumerations.AscDescEnum;
import enumerations.MessageLevel;
import objectsUtils.NumberUtils;
import objectsUtils.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static constantsUtils.CommonConstants.EMPTY_STRING;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * Class holds methods to work with Excel files
 */
@SuppressWarnings({"unused", "unchecked"})
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * get Excel sheet to work on it
     * @param filePath - Excel file path
     * @param sheetIndex - index of sheet to work on it
     * @return the sheet to work on it
     * @throws IOException IOException
     * @author abo_saleh.rawand
     * @since 03.10.2022
     * @author genosar.dafna
     * @since 18.12.2024
     */
    public static Sheet getExcelWorkSheet(String filePath,int sheetIndex) throws IOException {
        File file = new File(filePath);

        // Use try-with-resources to manage XSSFWorkbook
        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            return workbook.getSheetAt(sheetIndex);
        }
    }

    /**
     * @return the details of XLS headers. (as HashMap of header name and index)
     * @author genosar.dafna
     * @since 19.06.2024
     * @since 25.07.2024
     */
    public static LinkedHashMap<String, Integer> getHeadersNamesAndIndexes(String filePath, int sheetIndex, int headerRowIndex){

        LinkedHashMap<String, Integer> headerDetailsMap = new LinkedHashMap<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);

            try {
                Workbook workbook = new XSSFWorkbook(fileInputStream);

                try {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);

                    //Get the header row
                    Row headerRow = sheet.getRow(headerRowIndex);

                    headerDetailsMap = getHeadersNamesAndIndexes(headerRow);
                }
                catch (Throwable t1) {
                    try {
                        workbook.close();
                    }
                    catch (Throwable t2) {
                        t1.addSuppressed(t2);
                    }
                    throw t1;
                }
                workbook.close();
            }
            catch (Throwable t3) {
                try {
                    fileInputStream.close();
                }
                catch (Throwable t4) {
                    t3.addSuppressed(t4);
                }
                throw t3;
            }
            fileInputStream.close();
        }
        catch (IOException t5) {
            t5.printStackTrace();
        }

        return headerDetailsMap;
    }

    /**
     * @return the details of XLS headers. (as HashMap of header name and index)
     * @author genosar.dafna
     * @since 01.12.2024
     */
    public static LinkedHashMap<String, Integer> getHeadersNamesAndIndexes(Row headerRow){

        LinkedHashMap<String, Integer> headerDetailsMap = new LinkedHashMap<>();

        //get the first cell in the header row
        int firstColumn = headerRow.getFirstCellNum();

        //get the last cell in the header row
        int numColumns = headerRow.getPhysicalNumberOfCells();

        for(int i = firstColumn; i < numColumns; ++i) {

            String headerText = headerRow.getCell(i).getStringCellValue();

            headerDetailsMap.put(headerText, i);
        }

        return headerDetailsMap;
    }

    /**
     * @return the details of XLS headers. (as HashMap of index and header name)
     * @author genosar.dafna
     * @since 19.06.2024
     * @since 25.07.2024
     */
    public static LinkedHashMap<Integer, String> getHeadersIndexesAndNames(String filePath, int sheetIndex, int headerRowIndex){

        LinkedHashMap<Integer, String> headerDetailsMap = new LinkedHashMap<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);

            try {
                Workbook workbook = new XSSFWorkbook(fileInputStream);

                try {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);

                    //Get the header row
                    Row headerRow = sheet.getRow(headerRowIndex);

                    //get the first cell in the header row
                    int firstColumn = headerRow.getFirstCellNum();

                    //get the total number of cells in the header
                    int numColumns = headerRow.getLastCellNum();

                    for(int i = firstColumn; i < numColumns; ++i) {

                        String headerText = headerRow.getCell(i).getStringCellValue();

                        headerDetailsMap.put(i, headerText);
                    }
                }
                catch (Throwable t1) {
                    try {
                        workbook.close();
                    }
                    catch (Throwable t2) {
                        t1.addSuppressed(t2);
                    }
                    throw t1;
                }
                workbook.close();
            }
            catch (Throwable t3) {
                try {
                    fileInputStream.close();
                }
                catch (Throwable t4) {
                    t3.addSuppressed(t4);
                }
                throw t3;
            }
            fileInputStream.close();
        }
        catch (IOException t5) {
            t5.printStackTrace();
        }

        return headerDetailsMap;
    }

    /**
     * Method opens the file with the name sent to method and retrieves a random value from the column with the index
     * sent to the method
     *
     * @param fileName    Name of the file to extract random value from
     * @param columnIndex index of the column to extract random value from
     * @return The text in the random location in the specified column
     * @throws IOException when InputStream or XSSFWorkbook can't be closed
     */
    public static String getRandomValueFromSpecifiedColumn(String fileName, int columnIndex) throws IOException {
        String randomValue = EMPTY_STRING;
        InputStream inputFS = null;
        XSSFWorkbook workbook = null;
        Random rand = new Random();
        try {
            logger.info("Opening excel file: " + fileName);
            inputFS = new FileInputStream(fileName);
//        	POIFSFileSystem poifs = new POIFSFileSystem(inputFS);
            workbook = new XSSFWorkbook(inputFS);
//    		Workbook workbook2 = new SXSSFWorkbook(workbook1, 100);

            // Creating a Workbook from an Excel file (.xls or .xlsx)
//            Workbook workbook = WorkbookFactory.create(poifs);


            // Getting the Sheet at index zero
            Sheet sheet = workbook.getSheetAt(0);
            logger.info("Opening first sheet");


            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();

            int randomNum = rand.nextInt(sheet.getLastRowNum()) + 1;
            while (!(dataFormatter.formatCellValue(sheet.getRow(randomNum).getCell(columnIndex)).trim().length() > 0)) {
                randomNum = rand.nextInt(sheet.getLastRowNum()) + 1;
            }

            randomValue = dataFormatter.formatCellValue(sheet.getRow(randomNum).getCell(columnIndex)).trim();
            logger.info("Random value found: " + randomValue);


            // Closing the workbook
            workbook.close();
            inputFS.close();
            logger.info("Closing file: " + fileName);


        } catch (Throwable e) {

            if (workbook != null) {
                workbook.close();
            }

            if (inputFS != null) {
                inputFS.close();
            }

            e.printStackTrace();
        }
        return randomValue;
    }


    /**
     * get number of rows in the first sheet of excel file
     * @param filePath   -  file path to .xlsx file
     * @param sheetIndex - the index of the sheet to find its number of rows
     * @return the number of rows in the file
     * @author - Lior umflat
     * @since - 13.7.2021
     */
    public static int getNumberOfRowsInExcelFile(String filePath, int sheetIndex) {
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;
        int rowNum;
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            // Getting the Sheet at index sheetIndex
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            //get number of rows
            rowNum = sheet.getLastRowNum() + 1;
        } catch (Exception e) {
            logger.error("couldn't read the file. view error: " + e.getMessage());
            throw new Error("couldn't read the file. view error: " + e.getMessage());
        } finally {
            try {
                //close workbook and fis
                if (workbook != null) {
                    workbook.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the file. see error: " + e.getMessage());
            }
        }

        return rowNum;

    }

    /**
     * get list of unique random values from specific column in excel file
     *
     * @param filePath            - the path of the excel file
     * @param columnIndex         - the index of the column to choose random values from
     * @param numOfValuesToSelect - number of unique random values to select
     * @return List of all the unique random values that were chosen
     * @author - Lior Umflat
     * @since - 18.7.2021
     */
    public static List<String> getAListOfUniqueRandomValuesFromSpecificColumn(int numOfValuesToSelect, String filePath, int columnIndex) throws IOException {
        //create list that will contain all the chosen random values
        List<String> randomChosenValues = new ArrayList<>();
        //if numOfValuesToSelect is greater than number of rows in the excel file throw an error
        if(numOfValuesToSelect>getNumberOfRowsInExcelFile(filePath,0)){
            logger.error("number of values to select from the excel file is more than the actual rows in the file");
            throw new Error("number of values to select from the excel file is more than the actual rows in the file");
        }
        //get random values from the excel file
        for (int i = 0; i < numOfValuesToSelect; i++) {
            //get random value from the column
            String stringToAddToList = getRandomValueFromSpecifiedColumn(filePath, columnIndex);
            //while the string exists in the randomChosenValues list choose different string
            //create attempt value that will end the loop if we try up to 500 attempts
            int attempt=0;
            while (randomChosenValues.contains(stringToAddToList)) {
                stringToAddToList = getRandomValueFromSpecifiedColumn(filePath, columnIndex);
                attempt++;
                //if we got to attempt #500, throw error, enter message to the log and break from the loop
                if(attempt==500)
                {
                    logger.error("ExcelUtils - getAListOfUniqueRandomValuesFromSpecificColumn - 500 attempts were executed, but still didn't find random value that doesn't exist in the list of randomChosenValues");
                    throw new Error("ExcelUtils - getAListOfUniqueRandomValuesFromSpecificColumn - 500 attempts were executed, but still didn't find random value that doesn't exist in the list of randomChosenValues");
                }
            }
            //add the string to the list
            randomChosenValues.add(stringToAddToList);
        }

        //return the list
        return  randomChosenValues;
    }

    /**
     * Get the row with the most amount of cells in the sheet
     * @param sheet the sheet
     * @return the row with the most amount of cells in the sheet
     * @author genosar.dafna
     * @since 14.09.2025
     */
    public static Row getRowWithTheMostCells(Sheet sheet){

        Row rowWithMostCells = null;
        int maxCells = -1;

        for (Row row : sheet) {
            int numCells = row.getLastCellNum(); // includes empty cells too
            if (numCells > maxCells) {
                maxCells = numCells;
                rowWithMostCells = row;
            }
        }
        return rowWithMostCells;
    }

    /**
     * Update value in Excel, based on file name, sheet, row and column.
     * @param updatedValue New value
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param rowNumber Number of row
     * @param columnNumber Number of column
     * @author plot.ofek
     * @since 17.08.2021
     * @author genosar.dafna
     * @since 18.12.2024
     */
    public static void updateSpecificCellInExcelFile(String updatedValue, String excelFilePath, int sheetNumber, int rowNumber, int columnNumber) {

        FileInputStream inputStream;
        Workbook workbook = null;
        FileOutputStream outputStream = null;
        
        try {
            inputStream = getFileInputStream(excelFilePath);
            workbook = getWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            Cell cell = sheet.getRow(rowNumber).getCell(columnNumber);
            if (cell == null) {
                cell = sheet.getRow(rowNumber).createCell(columnNumber);
            }

            cell.setCellValue(updatedValue);

            closeInputStream(inputStream);

            outputStream = getFileOutputStream(excelFilePath);
            workbook.write(outputStream);

        }
        catch (IOException | EncryptedDocumentException ex) {
            logger.error(ex.getMessage(),ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, String.format("Failed to update specific cell in Excel file %s</br>Error: %s", excelFilePath, ex.getMessage()));
        }
        finally {
            try {
                //close workbook and outputStream
                closeWorkbook(workbook);
                closeOutputStream(outputStream);
            }
            catch (Exception e) {
                logger.error("Error occured while closing the file. <br>Error: " + e.getMessage());
            }
        }
    }

    /**
     * Delete a row's content in Excel file, leaving the row blank
     * @param excelFilePath file path
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param rowIndexToDelete index of row to delete
     * @param <S> generic type of sheet
     */
    public static <S> void deleteRowContent(String excelFilePath, S sheet, int rowIndexToDelete){

        if(rowIndexToDelete < 0)
            throw new Error(String.format("Cannot delete a row in index %s from excel sheet", rowIndexToDelete));

        FileInputStream fileInputStream;
        Workbook workbook = null;
        FileOutputStream fileOutputStream = null;

        try {
            File file = new File(excelFilePath);
            if(!file.exists()) {
                throw new Error(String.format("Excel file '%s' cannot be found", excelFilePath));
            }
            fileInputStream = new FileInputStream(excelFilePath);
            workbook = new XSSFWorkbook();
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetObj = getSheetObject(workbook, sheet);

            // total no. of rows
            int totalRows = sheetObj.getLastRowNum();
            System.out.println("Total no of rows : " + totalRows);

            // remove values from third row but keep third row blank
            if (sheetObj.getRow(rowIndexToDelete) != null) {
                sheetObj.removeRow(sheetObj.getRow(rowIndexToDelete));
            }

            fileOutputStream = new FileOutputStream(excelFilePath);
            workbook.write(fileOutputStream);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, String.format("Failed to delete row content in Excel file %s</br>Error: %s", excelFilePath, ex.getMessage()));
        }
        finally {
            try {
                //close workbook and outputStream
                if (workbook != null) {
                    workbook.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the Excel file. Error: " + e.getMessage());
            }
        }
    }

    /**
     * Delete the last row in Excel file, leaving the row blank
     * @param excelFilePath file path
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param <S> generic type of sheet
     */
    public static <S> void deleteLastRow(String excelFilePath, S sheet){

        FileInputStream fileInputStream;
        Workbook workbook = null;
        FileOutputStream fileOutputStream = null;

        try {
            File file = new File(excelFilePath);
            if(!file.exists()) {
                throw new Error(String.format("Excel file '%s' cannot be found", excelFilePath));
            }
            fileInputStream = new FileInputStream(excelFilePath);
            workbook = new XSSFWorkbook();
            workbook = new XSSFWorkbook(fileInputStream);
           // workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheetObj = getSheetObject(workbook, sheet);

            sheetObj.setSelected(true);

            int rowIndexToDelete = sheetObj.getLastRowNum();

            // remove values from third row but keep third row blank
            if (sheetObj.getRow(rowIndexToDelete) != null) {
                sheetObj.removeRow(sheetObj.getRow(rowIndexToDelete));
            }

            fileOutputStream = new FileOutputStream(excelFilePath);
            workbook.write(fileOutputStream);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, String.format("Failed to delete last row in Excel file %s</br>Error: %s", excelFilePath, ex.getMessage()));
        }
        finally {
            try {
                //close workbook and outputStream
                if (workbook != null) {
                    workbook.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error("couldn't close the Excel file. Error: " + e.getMessage());
            }
        }
    }

    /**
     * Remove all the rows from the sheet
     * @param sheet the sheet
     * @param removeHeader true if to remove the header as well / false otherwise
     * @param headerIndex the index of the header or null if no header
     * @author genosar.dafna
     * @since 02.09.2024
     */
    public static void removeAllRows(Sheet sheet, boolean removeHeader, @Nullable Integer headerIndex) {

        int startRow = 0;

        if(!removeHeader){
            if (headerIndex != null)
                startRow = headerIndex + 1;
            else
                startRow = 1;
        }
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if( row!= null)
                sheet.removeRow(row);
        }
    }

    /**
     * Update the Excel file
     * @param excelFilePath Excel file
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param rowsToUpdate the values to update
     *                     Key: row number
     *                     Value: hashMap of values
     *                              key: column number
     *                              value: the value to update
     * @author Dafna Genosar
     * @since 30.06.2024
     */
    public static <S, T> void updateRowsInExcel(String excelFilePath, S sheet, HashMap<Integer, HashMap<Integer, T>> rowsToUpdate) {

        //Get/Create the file object by the file path
        getFile(excelFilePath, sheet);

        FileInputStream inputStream = createFileInputStream(excelFilePath);
        Workbook workbook = null;
        FileOutputStream outputStream = null;

        try {
            workbook = WorkbookFactory.create(inputStream);

            //Set the values in the sheet
            setSheetValues(workbook, sheet, rowsToUpdate);

            inputStream.close();

            outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
        }
        catch (Throwable ex) {
            Report.reportAndLog(String.format("Failed to update row in Excel file %s</br>Error: %s", excelFilePath, ex.getMessage()), MessageLevel.ERROR);
        }
        finally {
            //close workbook and outputStream
            closeWorkbook(workbook);
            closeOutputStream(outputStream);
        }
    }

    /**
     * Get the Excel file. Creates the file, if does not exist
     * @param excelFilePath the path to excel file
     * @param sheet the sheet - can be sheet object, sheet name or sheet number
     * @return the File object
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static <S> File getFile(String excelFilePath, S sheet){

        File file;
        try {
            file = new File(excelFilePath);
            if (!file.exists()) {
                if (sheet instanceof String)
                    file = createExcelFile(excelFilePath, (String) sheet);
                else
                    file = createExcelFile(excelFilePath, null);
            }
        }
        catch(Throwable t){
            throw new Error(String.format("Failed to get Excel file %s<br>Error: %s", excelFilePath, t.getMessage()));
        }
        return file;
    }

    /**
     * Get the sheet object according to the given type
     * @param workbook the workbook object
     * @param sheet the sheet - can be sheet object, sheet name or sheet number
     * @return the sheet object
     * @author genosar.dafna
     * @since 30.06.2024
     * @author genosar.dafna
     * @since 18.126.2024
     */
    private static <S> Sheet getSheetObject(Workbook workbook, S sheet){

        Sheet sheetObject;

        if(sheet instanceof Sheet)
            sheetObject = (Sheet)sheet;
        else if(sheet instanceof String)
            sheetObject = workbook.getSheet((String)sheet);
        else if(sheet instanceof Integer)
            sheetObject = workbook.getSheetAt((int)sheet);
        else throw new Error("Sheet type received in the method must be of types: String, int or Sheet object");

        if(sheetObject == null){
            if(sheet instanceof Sheet)
                sheetObject = workbook.createSheet();
            else if(sheet instanceof String)
                sheetObject = workbook.createSheet((String)sheet);
            else throw new IllegalArgumentException("The sheet parameter received in method getSheetObject() must be of type Sheet or a String.");
        }
        return sheetObject;
    }

    private static FileInputStream createFileInputStream(String excelFilePath){
        try{
            return new FileInputStream(excelFilePath);
        }
        catch (Exception e){
            throw new Error(String.format("Failed to create FileInputStream to path: %s", excelFilePath));
        }
    }

    /**
     * Set the values in the given sheet
     * @param workbook the workbook object
     * @param sheet the sheet - can be sheet object, sheet name or sheet number
     * @param rowsToUpdate the values to update
     *                     Key: row number
     *                     Value: hashMap of values
     *                              key: column number
     *                              value: the value to update
     * @return the sheet object
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static <T, S> Sheet setSheetValues(Workbook workbook, S sheet, HashMap<Integer, HashMap<Integer, T>> rowsToUpdate){

        //Get the sheet
        Sheet sheetObject = getSheetObject(workbook, sheet);

        //Set the rows in the sheet
        return setSheetValues(sheetObject, rowsToUpdate);
    }

    /**
     * Set the values in the given sheet
     * @param sheetObject the sheet object
     * @param rowsToUpdate the values to update
     *                     Key: row number
     *                     Value: hashMap of values
     *                              key: column number
     *                              value: the value to update
     * @return the sheet object
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static <T> Sheet setSheetValues(Sheet sheetObject, HashMap<Integer, HashMap<Integer, T>> rowsToUpdate){

        //Set the rows in the sheet
        for (Map.Entry<Integer, HashMap<Integer, T>> rowToUpdate : rowsToUpdate.entrySet()) {

            int rowNumber = rowToUpdate.getKey();
            HashMap<Integer, T> columnsToUpdate = rowToUpdate.getValue();

            //Set the values in the row
            setRowValues(sheetObject, rowNumber, columnsToUpdate);
        }
        return sheetObject;
    }

    /**
     * Set the given values in the row
     * @param sheetObject the sheet object to update
     * @param rowIndex the row index
     * @param columnsToUpdate the columns to update (index of column, value to update)
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static <T> Row setRowValues(Sheet sheetObject, int rowIndex, HashMap<Integer, T> columnsToUpdate){

        Row row = sheetObject.getRow(rowIndex);
        if (row == null)
            row = sheetObject.createRow(rowIndex);

        logger.info(String.format("Update Excel row %d", rowIndex));

        //Set the row's values
        return setRowValues(row, columnsToUpdate);
    }

    /**
     * Set the given values in the row
     * @param row the row object
     * @param columnsToUpdate the columns to update (index of column, value to update)
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static <T> Row setRowValues(Row row, HashMap<Integer, T> columnsToUpdate){

        //Set the columns values
        for (Map.Entry<Integer, T> columnToUpdate : columnsToUpdate.entrySet()) {
            int columnNumber = columnToUpdate.getKey();
            T valueToUpdate = columnToUpdate.getValue();
            setCellValue(row, columnNumber, valueToUpdate);
        }
        return row;
    }

    /**
     * Set cell value
     * @param row the row object
     * @param columnNumber the number of the column to update
     * @param valueToUpdate the value to update
     * @return the cell object
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static <T> Cell setCellValue(Row row, int columnNumber, T valueToUpdate){

        Cell cell = row.getCell(columnNumber);
        if (cell == null) {
            cell = row.createCell(columnNumber);
        }

        try {
            return setCellValue(cell, valueToUpdate);
        }
        catch(Throwable t){
            logger.error(String.format("Failed to set the value in column number %d. Error: %s", columnNumber, t.getMessage()));
            Report.reportAndLog(String.format("Failed to set the value in column number %d. Error: %s", columnNumber, t.getMessage()), MessageLevel.ERROR);
            throw t;
        }
    }

    /**
     * Set cell value
     * @param cell the cell object
     * @param valueToUpdate the value to update
     * @return the cell object
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static <T> Cell setCellValue(Cell cell, T valueToUpdate){

        try {
            if (valueToUpdate instanceof String)
                cell.setCellValue((String) valueToUpdate);
            else if (valueToUpdate instanceof Integer)
                cell.setCellValue((Integer) valueToUpdate);
            else if (valueToUpdate instanceof Double)
                cell.setCellValue((Double) valueToUpdate);
            else if (valueToUpdate instanceof Long) {
                double d = Long.parseLong(valueToUpdate.toString());
                cell.setCellValue(d);
            } else if (valueToUpdate instanceof Date)
                cell.setCellValue((Date) valueToUpdate);
            else if (valueToUpdate instanceof DateTime)
                cell.setCellValue(((DateTime) valueToUpdate).getDateObject());
        }
        catch(Exception eex){
            logger.error(String.format("Failed to set value: %s in Excel cell. Error: %s", valueToUpdate, eex.getMessage()), eex);
            Report.reportAndLog(String.format("Failed to set value: %s in Excel cell. Error: %s", valueToUpdate, eex.getMessage()), MessageLevel.ERROR);
            throw eex;
        }
        return cell;
    }

    /**
     * add new rows to Excel, based on file name, sheet, and  cells and values HashMap.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param lineToUpdate -  HashMap  -  each item in HashMap is cell. Key=cellIndex, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     * @author Dafna Genosar
     * @since 13.11.2022
     */
    public static <T> void writeNewSingleRowToExcel(String excelFilePath, int sheetNumber, HashMap<Integer, T> lineToUpdate) {

        List<HashMap<Integer, T>> linesToUpdate = new ArrayList<>();
        linesToUpdate.add(lineToUpdate);
        writeNewRowsToExcel(excelFilePath, sheetNumber, linesToUpdate);
    }

    /**
     * add new rows to Excel, based on file name, sheet, and  cells and values from List of HashMap.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param linesToUpdate -  list of hashMap each item in List is a row, each item in HashMap is cell. Key=column name, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     * @author Dafna Genosar
     * @since 13.11.2022
     */
    public static <T> void writeNewLinesToExcelByColumnName(String excelFilePath, int sheetNumber,  List<HashMap<String, T>> linesToUpdate) {

        List<HashMap<Integer,T>> rowsColumnsIndexAndValues = new ArrayList<>();

        for(HashMap<String, T> line : linesToUpdate){
            List<String> columns = new ArrayList<>(line.keySet());
            HashMap<String,Integer> columnsNameAndIndex= getColumnsIndex(excelFilePath,sheetNumber,0,columns);
            HashMap<Integer,T> columnsIndexAndValues=new HashMap<>();
            for(Map.Entry<String, Integer> columnIndex:columnsNameAndIndex.entrySet()){
                columnsIndexAndValues.put(columnIndex.getValue(),line.get(columnIndex.getKey()));
            }
            rowsColumnsIndexAndValues.add(columnsIndexAndValues);
        }

        writeNewRowsToExcel(excelFilePath,sheetNumber,rowsColumnsIndexAndValues);
    }

    /**
     * add new single row to Excel, based on file name, sheet, and  cells and values HashMap.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param lineToUpdate -  HashMap  -  each item in HashMap is cell. Key=column Name, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     */
    public  static void writeNewSingleRowToExcelByColumnName(String excelFilePath, int sheetNumber, HashMap<String, Object> lineToUpdate) {

        List<HashMap<String, Object>> linesToUpdate = new ArrayList<>();
        linesToUpdate.add(lineToUpdate);
        writeNewLinesToExcelByColumnName(excelFilePath, sheetNumber, linesToUpdate);
    }

    /**
     * Update / Set lines in an existing Excel file
     * @param excelFilePath The Excel file path
     * @param sheetsAndLines the lines to update in each sheet
     *   Key: the sheet name
     *   Value: a list of lines to add to the sheet as HashMap that holds:
     *                       Key: cell index
     *                       value: value to set in cell
     * @param indexOfFirstRowToUpdate optional index of the first row to update. Excel rows start from index 0.
     *                                If Null - the 1st row to update will be the first available row
     * @param logTheLines true if to write the lines to the report / false otherwise
     * @param addTheHeaderLine true if to add the header / false if not to add the header (when the header already exists)
     * @return the absolute file path
     * @author genosar.dafna
     * @since 18.12.2024
     */
    public static String writeRowsToExcelFile(String excelFilePath, HashMap<String, List<HashMap<Integer, Object>>> sheetsAndLines, @Nullable Integer indexOfFirstRowToUpdate, boolean logTheLines, boolean addTheHeaderLine) {

        Report.reportAndLog(String.format("<b>Write to XLS file %s</b>", excelFilePath), MessageLevel.INFO);

        //Go over each sheet and set the lines
        for(Map.Entry<String, List<HashMap<Integer, Object>>> sheetAndLines : sheetsAndLines.entrySet()) {

            //Get the sheet name from the map
            String xlsSheetName = sheetAndLines.getKey();

            //Get the lines to update in this sheet
            List<HashMap<Integer, Object>> lines = sheetAndLines.getValue();

            //If true - log the headers and lines to the report. Will display as a table
            if(logTheLines) {
                String linesToLog = getLinesToLogAsATable( lines);
                reportUtils.Report.reportAndLog(String.format("Write the following data to the Excel file - Sheet '%s' <br>%s", xlsSheetName, linesToLog), MessageLevel.INFO);
            }
            else {
                reportUtils.Report.reportAndLog(String.format("Write the data to the Excel file - Sheet '%s'", xlsSheetName), MessageLevel.INFO);
            }

            //if false - remove the header line from the lines to update (in case the header already exists in the Excel)
            if(!addTheHeaderLine)
                lines = ListUtils.removeItemFromList(lines, 0);

            //Write the rows to the sheet
            writeRowsToExcel(excelFilePath, xlsSheetName, lines, indexOfFirstRowToUpdate);
        }
        return excelFilePath;
    }

    /**
     * Get the given lines details as an html table to log to the report
     * @param lines a list of lines as HashMaps that hold:
     *                             Key: cell index
     *                             value: value to set in cell
     * @author genosar.dafna
     * @since 18.12.2024
     */
    private static String getLinesToLogAsATable(List<HashMap<Integer, Object>> lines){
        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            HashMap<Integer, Object> entry = lines.get(i);
            StringBuilder columns = new StringBuilder();
            for (int x = 0; x < entry.values().size(); x++) {
                Object value = entry.values().toArray()[x];
                String width = "width: auto;";
                columns.append(String.format("<td style=\"%s border: 1px solid black;\">%s</td>", width, value));
            }
            if (i == 0) {
                rows.append(String.format("<tr style=\"background-color: #D6EEEE; border: 1px solid black; font-weight: bold;\">%s</tr>", columns));
            } else
                rows.append(String.format("<tr>%s</tr>", columns));
        }

        return String.format("<table style=\"width: 100%%; table-layout: auto; border-collapse: collapse;\">%s</table>", rows);
    }

    /**
     * Create the Excel file if does not exist and add new rows to Excel, based on file name, sheet, and  cells and values from List of HashMap.
     * @param excelFilePath Excel file
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param linesToUpdate List of hashMaps.
     *                     Each item in List is a row, each item in HashMap is cell. Key=cellIndex, value=value to set in cell
     * @param indexOfFirstRowToUpdate optional index of the first row to update. Excel rows start from index 0.
     *                                If Null - the 1st row to update will be the first available row
     * @author Dafna Genosar
     * @since 17.12.2024
     */
    public static <S, T> void writeRowsToExcel(String excelFilePath, S sheet, List<HashMap<Integer, T>> linesToUpdate, @Nullable Integer indexOfFirstRowToUpdate) {

        //Get/Create the file object by the file path
        getFile(excelFilePath, sheet);

        FileInputStream inputStream = createFileInputStream(excelFilePath);
        Workbook workbook = null;
        FileOutputStream outputStream = null;

        try {
            workbook = getWorkbook(inputStream);

            //Get the sheet
            Sheet sheetObject = getSheetObject(workbook, sheet);

            int newRowIndex = indexOfFirstRowToUpdate == null? sheetObject.getLastRowNum() : indexOfFirstRowToUpdate;

            HashMap<Integer, HashMap<Integer, T>> rowsToUpdate = new LinkedHashMap<>();

            for (HashMap<Integer, T> integerTHashMap : linesToUpdate) {
                rowsToUpdate.put(newRowIndex, integerTHashMap);
                newRowIndex++;
            }

            //Set the values in the sheet
            setSheetValues(workbook, sheetObject, rowsToUpdate);

            closeInputStream(inputStream);

            outputStream = getFileOutputStream(excelFilePath);
            workbook.write(outputStream);

        }
        catch (Throwable ex) {
            Report.reportAndLog(String.format("Failed to write rows to Excel file %s</br>Error: %s", excelFilePath, ex.getMessage()), MessageLevel.ERROR);
        }
        finally {
            //close workbook and outputStream
            closeWorkbook(workbook);
            closeOutputStream(outputStream);
        }
    }

    /**
     * Create the Excel file if does not exist and add new rows to Excel, based on file name, sheet, and  cells and values from List of HashMap.
     * @param excelFilePath Excel file
     * @param sheet Sheet can be either sheet number (int) / sheet name (String) / Sheet object
     * @param linesToUpdate -  list of hashMap each item in List is a row, each item in HashMap is cell. Key=cellIndex, value=value to set in cell
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     * @author Dafna Genosar
     * @since 01.07.2024
     */
    public static <S, T> void writeNewRowsToExcel(String excelFilePath, S sheet, List<HashMap<Integer, T>> linesToUpdate) {

        //Get/Create the file object by the file path
        getFile(excelFilePath, sheet);

        FileInputStream inputStream = createFileInputStream(excelFilePath);
        Workbook workbook = null;
        FileOutputStream outputStream = null;

        try {
            workbook = getWorkbook(inputStream);

            //Get the sheet
            Sheet sheetObject = getSheetObject(workbook, sheet);

            int newRowIndex = sheetObject.getLastRowNum() + 1;

            HashMap<Integer, HashMap<Integer, T>> rowsToUpdate = new LinkedHashMap<>();

            for (HashMap<Integer, T> integerTHashMap : linesToUpdate) {
                rowsToUpdate.put(newRowIndex, integerTHashMap);
                newRowIndex++;
            }

            //Set the values in the sheet
            setSheetValues(workbook, sheetObject, rowsToUpdate);

            inputStream.close();

            outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);

        }
        catch (Throwable ex) {
            Report.reportAndLog(String.format("Failed to write new rows to Excel file %s</br>Error: %s", excelFilePath, ex.getMessage()), MessageLevel.ERROR);
        }
        finally {
            //close workbook and outputStream
            closeWorkbook(workbook);
            closeOutputStream(outputStream);
        }
    }

    /**
     * Create a new Excel file
     * @param filePath the file path
     * @param sheetName optional sheet name or null
     * @return the File object
     * @author genosar.dafna
     * @since 2.5.2023
     */
    public static File createExcelFile(String filePath, @Nullable String sheetName){

        File file = FileUtils.createNewFile(filePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = createSheet(workbook, sheetName);
            workbook.write(outputStream);
        }
        catch(FileNotFoundException e1){
            throw new Error(String.format("File could not be found in path: %s<br>Error: %s", filePath, e1.getMessage()));
        }
        catch(IOException e2){
            throw new Error(String.format("Cannot create a new XSSFWorkbook in path: %s<br>Error: %s", filePath, e2.getMessage()));
        }
        return file;
    }

    /**
     * Create a new sheet
     * @param workbook the workbook object
     * @param sheetName optional sheet name or null
     * @return the sheet object
     * @author genosar.dafna
     * @since 2.5.2023
     */
    public static XSSFSheet createSheet(XSSFWorkbook workbook, @Nullable String sheetName){
        XSSFSheet sheet;
        if(sheetName != null)
            sheet = workbook.createSheet(sheetName);
        else
            sheet = workbook.createSheet();

        return sheet;
    }

    /**
     * Get Excel columns Index .function return a HashMap with column name and column index in each entry.
     * @param excelFilePath Excel file
     * @param sheetNumber Sheet number in file
     * @param ValuesToSearch -  List  -  each value in list is a column name in the excel
     * @param rowIndex - row index to search the value
     * @return HashMap<String,Integer> Key contains column name value contains column index
     * @author Yael.Rozenfeld
     * @since 25/1/2022
     */
     public static HashMap<String,Integer> getColumnsIndex(String excelFilePath, int sheetNumber,int rowIndex, List<String> ValuesToSearch){
        FileInputStream inputStream;
        Workbook workbook = null;
        FileOutputStream outputStream = null;
        HashMap<String,Integer> columnsIndex= new HashMap<>();
        try {
            inputStream = getFileInputStream(excelFilePath);
            workbook = getWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(sheetNumber);
            Row row = sheet.getRow(rowIndex);

            for (String column : ValuesToSearch) {
                boolean cellFound=false;
                for(Cell cell:row){
                    if(cell.getStringCellValue().trim().equalsIgnoreCase(column)){
                        columnsIndex.put(column,cell.getColumnIndex());
                        cellFound=true;
                        break;
                    }
                }
                if(!cellFound){
                    columnsIndex.put(column,-1);
                    logger.info(String.format("column: %s doesn't exist in excel: %s",column,excelFilePath ));
                }

            }
            closeInputStream(inputStream);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.ERROR, String.format("Failed to read column index from Excel file %s</br>Error: %s", excelFilePath, ex.getMessage()));
        }
        finally {
            try {
                //close workbook and outputStream
                closeWorkbook(workbook);
                closeOutputStream(outputStream);
            }
            catch (Exception e) {
                logger.error("Exception when closing the file.<br>Error: " + e.getMessage());
            }
        }
        return columnsIndex;
    }

    /**
     *
     * @param filePathToSort the file to sort
     * @param columnToSortBy Index of the column you want to sort by (0-based index)
     * @param sheet the sheet you want (can be index or name). index starts at 0
     * @param headerIndex if the excel has a header - the index of the header / otherwise null. index starts at 0
     * @param sortOrder ASC or DESC
     * @author genosar.dafna
     * @since 02.09.2024
     * @since 18.12.2024
     */
    public static <S> void sortExcel(String filePathToSort, String outputFilePath, S sheet, int columnToSortBy, @Nullable Integer headerIndex, AscDescEnum sortOrder){

        FileInputStream inputStream = null;
        Workbook workbook = null;
        Workbook newWorkbook = null;
        FileOutputStream outputStream = null;

        try {
            inputStream = getFileInputStream(filePathToSort);
            workbook = getWorkbook(inputStream);

            //Get the sheet
            Sheet sheetObject = getSheetObject(workbook, sheet);

            //Read all rows into a list
            List<Row> rows = new ArrayList<>();
            for (Row row : sheetObject) {
                rows.add(row);
            }

            Row header = null;
            List<Row> dataRows = new LinkedList<>(rows);

            //Remove header row if you have one
            if (headerIndex != null && headerIndex >= 0) {
                header = rows.get(headerIndex);
                dataRows = dataRows.subList(1, rows.size());
            }

            Comparator<Row> comparator = getRowsComparator(columnToSortBy, sortOrder, true);

            //Sort by the comparator
            dataRows.sort(comparator);

            outputStream = getFileOutputStream(outputFilePath);
            newWorkbook = new XSSFWorkbook();

            Sheet newSheet = newWorkbook.createSheet();

            //Write header
            if (header != null) {
                Row newHeader = newSheet.createRow(headerIndex);
                for (int i = 0; i < header.getLastCellNum(); i++) {
                    Cell oldCell = header.getCell(i);
                    Cell newCell = newHeader.createCell(i);
                    if (oldCell != null) {
                        newCell.setCellValue(oldCell.getStringCellValue());
                    }
                }
            }

            //Write sorted data rows
            int rowIndex = headerIndex == null ? 0 : headerIndex + 1;
            for (Row row : dataRows) {
                Row newRow = newSheet.createRow(rowIndex++);
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell oldCell = row.getCell(i);
                    Cell newCell = newRow.createCell(i);
                    if (oldCell != null) {
                        switch (oldCell.getCellType()) {
                            case STRING -> newCell.setCellValue(oldCell.getStringCellValue());
                            case NUMERIC -> newCell.setCellValue(oldCell.getNumericCellValue());
                            case BOOLEAN -> newCell.setCellValue(oldCell.getBooleanCellValue());
                            default -> {
                            }
                        }
                    }
                }
            }

            try {
                newWorkbook.write(outputStream);
            } catch (IOException o) {
                Report.reportAndLog(String.format("Failed to write to output file %s", outputFilePath), MessageLevel.ERROR);
                throw new UncheckedIOException(o);
            }
        }
        finally {
            closeWorkbook(workbook);

            closeInputStream(inputStream);

            closeWorkbook(newWorkbook);

            closeOutputStream(outputStream);
        }
    }

    private static Comparator<Row> getRowsComparator(int columnToSortBy, AscDescEnum sortOrder, boolean caseSensitive){

        return (row1, row2) -> {
            Cell cell1 = row1.getCell(columnToSortBy);
            Cell cell2 = row2.getCell(columnToSortBy);

            int valueToReturn;

            String string_value1 = null;
            String string_value2 = null;
            Double double_value1 = null;
            Double double_value2 = null;

            if (cell1 != null) {
                switch (cell1.getCellType()) {
                    case STRING -> string_value1 = cell1.getStringCellValue();
                    case NUMERIC -> double_value1 = cell1.getNumericCellValue();
                    default -> throw new IllegalArgumentException(String.format("Please add support to cell type %s in sortExcel()", cell1.getCellType()));
                }
            }
            if (cell2 != null) {
                switch (cell2.getCellType()) {
                    case STRING -> string_value2 = cell2.getStringCellValue();
                    case NUMERIC -> double_value2 = cell2.getNumericCellValue();
                    default -> throw new Error(String.format("Please add support to cell type %s in sortExcel()", cell2.getCellType()));
                }
            }

            //String cell
            if (Objects.requireNonNull(cell1).getCellType().equals(STRING) && Objects.requireNonNull(cell2).getCellType().equals(STRING)) {
                valueToReturn = StringUtils.compareStrings(string_value1, string_value2, caseSensitive);
            }

            //Numeric cell
            else if (cell1.getCellType().equals(NUMERIC) && Objects.requireNonNull(cell2).getCellType().equals(NUMERIC)) {
                valueToReturn = NumberUtils.compareDoubles(double_value1, double_value2);
            }
            else
                throw new IllegalArgumentException("getRowsComparator() - Please add support to cell type " + cell1.getCellType());

            if (sortOrder.equals(AscDescEnum.DESC))
                valueToReturn = valueToReturn * (-1);

            return valueToReturn;
        };
    }


    /**
     * Get FileInputStream
     * @param filePathToSort file path
     * @return FileInputStream
     * @author genosar.dafna
     * @since 18.12.2024
     */
    private static FileInputStream getFileInputStream(String filePathToSort) {
        try{
            return new FileInputStream(filePathToSort);
        }
        catch (FileNotFoundException e){
            Report.reportAndLog(String.format("Cannot get File Input Stream. File could not be found: %s", filePathToSort), MessageLevel.ERROR);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get FileOutputStream
     * @param outputFilePath file path
     * @return FileOutputStream
     * @author genosar.dafna
     * @since 18.12.2024
     */
    private static FileOutputStream getFileOutputStream(String outputFilePath){
        try{
            return new FileOutputStream(outputFilePath);
        }
        catch (FileNotFoundException e){
            Report.reportAndLog(String.format("Cannot get File Output Stream. File could not be found: %s", outputFilePath), MessageLevel.ERROR);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get Workbook
     * @param inputStream FileInputStream
     * @return Workbook
     * @author genosar.dafna
     * @since 18.12.2024
     */
    private static Workbook getWorkbook(FileInputStream inputStream) {
        try{
            return WorkbookFactory.create(inputStream);
        }
        catch (IOException e){
            Report.reportAndLog("Filed to get Workbook", MessageLevel.ERROR);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Convert xlsx file to csv file.
     * @param sourceExcelFile - path to xlsx file
     * @param destinationCsvFile - path to save the new CSV file
     * @throws IOException IOException
     * @author Yael Rozenfeld
     * @since 9.2.2023
     */

    public static void convertXlsxToCsv (String sourceExcelFile, String destinationCsvFile) throws IOException {
        // Read the xlsx file
        FileInputStream fis = new FileInputStream(sourceExcelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        // Write the csv file
        FileWriter writer = new FileWriter(destinationCsvFile);
        for (Row row : sheet) {
            for (Cell cell : row) {
                try{
                cell.setCellValue(cell.getStringCellValue().replaceAll(",",""));}
                catch (Exception e){}
                writer.append(cell.toString());
                writer.append(',');
            }
            writer.append('\n');
        }
        writer.flush();
        writer.close();
        workbook.close();
        fis.close();
    }

    /**
     * Convert file XLSX to file CSV without X Top rows
     *
     * @param reportPath xpath of file XLSX
     * @param csvReportFixedName xpath and name of file CSV
     * @param numOfRows number of top rows that don't need to be moved to file CSV
     * @author sela.zvika
     * @since 01.05.2023
     */

    public void  convertCSVFromExcelWithoutXTopRows(String reportPath, String csvReportFixedName, int numOfRows) {

        File csvOutputFile = new File(csvReportFixedName);
        File xlsxFile = new File(reportPath);


        List<String[]> dataLines = new ArrayList<>();

        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .setSharedStringsImplementationType(SharedStringsImplementationType.TEMP_FILE_BACKED)
                .setEncryptSstTempFile(true)
                .open(xlsxFile)) {
            for (Sheet sheet : workbook) {
                System.out.println("Sheet: " + sheet.getSheetName());
                int i=0;
                for (Row r : sheet) {
                    if (i<numOfRows) {
                        i+=1;
                        continue;
                    }
                    List<String> dataLine = new ArrayList<>();
                    for (Cell c : r) {
                        System.out.print('"');
                        System.out.print(c.getStringCellValue());
                        System.out.print("\",");
                        dataLine.add(c.getStringCellValue());
                    }
                    dataLines.add( dataLine.toArray(new String[0]));

                }
            }
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                dataLines.stream()
                        .map(this::convertToCSV)
                        .forEach(pw::println);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * read all Excel file header as key and rows are the values
     * @param filePath - path of Excel file
     * @param sheetIndex - sheet index to extract
     * @return  HashMap<String, ArrayList<String>>
     * @since 15.06.2023
     * @author abo_saleh.rawand
     */
    public static HashMap<String, ArrayList<String>> readExcelFile(String filePath, int sheetIndex) {
        HashMap<String, ArrayList<String>> excelData = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(filePath);

            // Create an Excel workbook object from the file
            Workbook workbook = new XSSFWorkbook(fis);

            // Get the first sheet of the workbook
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // Get the header row to retrieve column names
            Row headerRow = sheet.getRow(0);

            // Iterate over each column in the header row
            for (int columnIndex = 0; columnIndex < headerRow.getLastCellNum(); columnIndex++) {
                // Get the column name from the header row
                Cell headerCell = headerRow.getCell(columnIndex);
                String columnName = headerCell.getStringCellValue();

                // Iterate over each row in the column
                String cellValue = null;
                ArrayList<String> values = excelData.getOrDefault(cellValue, new ArrayList<>());
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    Cell cell = row.getCell(columnIndex);
                    // Get the cell value based on the cell type
                    cellValue = "";
                    if (cell != null) {
                        cellValue = getCellValueAsString(cell);
                        // Add data to the HashMap with multiple values
                        values.add(cellValue);
                        excelData.put(columnName, values);
                    }
                }
                //there are sheets that have no rows , so we enter a "" to null values
                if (cellValue == null) {
                    cellValue = "";
                    values.add(cellValue);
                    excelData.put(columnName, values);
                }
            }
            // Close the workbook and input stream
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelData;
    }

    /**
     * Return all lines of text from Excel file as a list of hashmaps
     * @param filePath file path
     * @param sheetIndexOrName the sheet index or name. index starts at 0
     * @return all lines of text from Excel file as a list of hashmaps
     * @author Dafna Genosar
     * @since 26.06.2023
     * @since 14.04.2025
     */
    public static <S, M extends Map<String, Object>, L extends List<M>> L readExcel(String filePath, S sheetIndexOrName) {

        LinkedList<LinkedHashMap<String, Object>> dataList = new LinkedList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            //Get the sheet
            Sheet sheet = getSheetObject(workbook, sheetIndexOrName);

            Row headerRow = sheet.getRow(0);            //header row
            int numColumns = headerRow.getLastCellNum();  //number of cells

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();

                for (int j = 0; j < numColumns; j++) {
                    Cell cell = row.getCell(j);
                    String header = headerRow.getCell(j).getStringCellValue();
                    String value = cell == null ? "" : cell.toString();
                    dataMap.put(header, value);
                }

                dataList.add(dataMap);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return (L)dataList;
    }

    /**
     * Read selected columns and rows from Excel file as a list of hashmaps
     * @param filePath file path
     * @param sheetIndexOrName the sheet index or name. index starts at 0
     * @param columnsToRead can be an integer or a String or a list (integers or Strings) of columns to read (indexes start from 1)
     * @param startFromRowIndex index of row to start reading the data from. row indexes start from 1.
     *                          If null - the start row will be 1 row after the header row
     *                          If -1 - the start row will be the last row
     * @param numberOfRowsToRead number of rows to read
     * @param headerIndex the index the header is located in (rows indexes start from 1). If there is no header you can leave null
     * @return all lines of text from Excel file as a list of hashmaps
     * @author Dafna Genosar
     * @since 27.08.2024
     * @since 14.09.2025
     */
    public static <S, C> List<Map<String, Object>> readExcelSelectedColumns(String filePath, S sheetIndexOrName, C columnsToRead, @Nullable Integer startFromRowIndex, @Nullable Integer numberOfRowsToRead, @Nullable Integer headerIndex) {

        List<Map<String, Object>> dataList = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            //Get the sheet
            Sheet sheet = getSheetObject(workbook, sheetIndexOrName);

            //Get the header row if exists
            Row headerRow = getHeaderRow(sheet, headerIndex);

            //Init the starting row
            int startReadingFromRow = calculateReadingStartRow(sheet, headerIndex, startFromRowIndex);

            //Init the end row to read down to
            int endRowToReadTo = calculateEndRow(sheet, startReadingFromRow, numberOfRowsToRead);

            List<Integer> columnsIndexesToRead = getColumnIndexesToRead(columnsToRead, headerRow, sheet);

            for (int i = startReadingFromRow; i <= endRowToReadTo; i++) {
                Row row = sheet.getRow(i);
                Map<String, Object> dataMap = new HashMap<>();

                if(!columnsIndexesToRead.isEmpty()) {

                    for (int columnIndexToRead : columnsIndexesToRead) {
                        String cellValue;
                        if(row == null){
                            cellValue = "";
                        }
                        else {
                            Cell cell = row.getCell(columnIndexToRead);
                            cellValue = cell == null ? "" : cell.toString();
                        }
                        String header = headerRow == null ? String.valueOf(columnIndexToRead) : headerRow.getCell(columnIndexToRead).getStringCellValue().replace("\n", "");

                        dataMap.put(header, cellValue);
                    }
                    dataList.add(dataMap);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    /**
     * @param sheet the Excel sheet
     * @param headerIndex optional header index
     * @return the header row if exists or null
     * @author genosar.dafna
     * @since 02.12.2024
     */
    public static Row getHeaderRow(Sheet sheet, @Nullable Integer headerIndex){
        return(headerIndex != null)? sheet.getRow(headerIndex) : null;
    }

    /**
     * Init the starting row to read from
     * @param sheet Excel sheet
     * @param startFromRowIndex optional index of row to start reading from.
     *                          if null - if there is a header - return header index + 1, otherwise 0
     *                          if -1 - the start row will be the last row
     *                          else - the given index
     *
     * @return the starting row to read from
     * @author genosar.dafna
     * @since 02.12.2024
     */
    private static int calculateReadingStartRow(Sheet sheet, @Nullable Integer headerIndex, @Nullable Integer startFromRowIndex) {
        if (startFromRowIndex == null) {
            return headerIndex != null ? headerIndex + 1 : 0;
        }
        return startFromRowIndex == -1 ? sheet.getLastRowNum() : startFromRowIndex;
    }

    /**
     * @param columnsToRead can be an integer or a String or a list (integers or Strings) of columns to read (indexes start from 1)
     * @param headerRow optional header row
     * @return the indexes of the columns to read
     * @author genosar.dafna
     * @since 02.12.2024
     */
    public static <C> List<Integer> getColumnIndexesToRead(C columnsToRead, @Nullable Row headerRow, Sheet sheet) {

        List<Integer> columnIndexes = new ArrayList<>();

        // Case 1: columnsToRead is null or empty
        if (columnsToRead == null || (columnsToRead instanceof List<?> && ((List<?>) columnsToRead).isEmpty())) {
            return getColumnIndexesToRead(headerRow, sheet);
        }

        // Case 2: columnsToRead is provided
        List<?> columnsToReadList = new ArrayList<>();
        if (columnsToRead instanceof List<?>) {
            columnsToReadList = (List<?>) columnsToRead;
        }
        else {
            columnsToReadList = Collections.singletonList(columnsToRead);
        }

        boolean isInt = columnsToReadList.get(0) instanceof Integer;

        // If we're working with Strings but headerRow is missing, fallback to searching values directly
        LinkedHashMap<String, Integer> headerDetailsMap = new LinkedHashMap<>();
        if (!isInt) {
            if (headerRow == null) {
                // No header row → search all rows for text matches
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = cell.toString().trim();
                        for (Object o : columnsToReadList) {
                            if (o instanceof String && ((String) o).equalsIgnoreCase(cellValue)) {
                                columnIndexes.add(cell.getColumnIndex());
                            }
                        }
                    }
                }
                return columnIndexes;
            }
            else {
                // Header row exists → build header map
                headerDetailsMap = getHeadersNamesAndIndexes(headerRow);
            }
        }

        // Case 3: normal processing of explicit columnsToRead
        for (Object o : columnsToReadList) {
            int columnIndexToRead;
            if (isInt) {
                // user passed an index (1-based → convert to 0-based)
                columnIndexToRead = ((Integer) o) - 1;
            } else {
                columnIndexToRead = headerDetailsMap.get((String) o);
            }
            columnIndexes.add(columnIndexToRead);
        }

        return columnIndexes;
    }

    /**
     * @param headerRow optional header row
     *                  if no header row received → return indexes of all cells in the row that has the most amount of cells
     *                  else if Header row exists → return indexes of all header cells
     * @return the indexes of the columns to read
     * @author genosar.dafna
     * @since 14.09.2025
     */
    public static List<Integer> getColumnIndexesToRead(@Nullable Row headerRow, Sheet sheet) {

        List<Integer> columnIndexes = new ArrayList<>();

        // No header row → return indexes of all cells in the row that has the most amount of cells
        if (headerRow == null) {

            Row rowWithMostCells = getRowWithTheMostCells(sheet);
            if (rowWithMostCells != null) {
                for (Cell cell : rowWithMostCells) {
                    columnIndexes.add(cell.getColumnIndex());
                }
            }
        }
        else {
            // Header row exists → return indexes of all header cells
            for (Cell cell : headerRow) {
                columnIndexes.add(cell.getColumnIndex());
            }
        }
        return columnIndexes;
    }

    /**
     * Init the end row to read down to
     * @param sheet Excel sheet
     * @param startFromRow the index of the row to start reading from
     * @return the end row to read down to
     * @author genosar.dafna
     * @since 02.12.2024
     */
    private static int calculateEndRow(Sheet sheet, int startFromRow, @Nullable Integer numberOfRowsToRead) {
        int lastRow = sheet.getLastRowNum();
        if (numberOfRowsToRead != null && startFromRow + numberOfRowsToRead - 1 <= lastRow) {
            return startFromRow + numberOfRowsToRead - 1;
        }
        return lastRow;
    }

    /**
     * get cell value as string
     * @param dataCell - value of the cell
     * @return value of the cell as string
     * @since 16.06.2023
     * @author abo_saleh.rawand
     */
    public static String getCellValueAsString(Cell dataCell) {
        String data = "";
        switch (dataCell.getCellType()) {
            case STRING -> data = dataCell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(dataCell)) {
                    Date date = dataCell.getDateCellValue();
                    data = DateUtils.convertDateToStringInFormat("MM/dd/yyy", date);
                } else
                    data = String.valueOf(dataCell.getNumericCellValue());
            }
            case BOOLEAN -> data = String.valueOf(dataCell.getBooleanCellValue());
            case FORMULA -> data = dataCell.getCellFormula();
            default -> {
            }
            // Handle other cell types as needed
        }
        return data;
    }
    /**
     * Get the first excel row that has the expected value under the column name
     * @param filePath path to excel
     * @param sheetIndex sheet index
     * @param columnName the desired column name to search under
     * @param columnValue the desired value
     * @return the first excel row that has the expected value under the column name
     * @author genosar.dafna
     * @since 26.06.2023
     */
    public static Map<String, Object> getRowByColumnValue(String filePath, int sheetIndex, String columnName, String columnValue) {

        LinkedList<LinkedHashMap<String, Object>> excelDataList = readExcel(filePath, sheetIndex);
        try {
            return excelDataList.stream().filter(x -> x.get(columnName).toString().equalsIgnoreCase(columnValue)).findFirst().orElse(null);
        }
        catch(Exception e)
        {
            throw new Error(String.format("Error when trying to return an excel row with column name '%s' and column value '%s'<br>Error: %s", columnName, columnValue, e.getMessage()));
        }
    }

    /**
     * Get All excel rows that has the expected value under the column name
     * @param filePath path to excel
     * @param sheetIndex sheet index
     * @param columnName the desired column name to search under
     * @param columnValue the desired value
     * @return All excel rows that has the expected value under the column name
     * @author abo_saleh.rawand
     * @since 05.07.2023
     */
    public static List<Map<String, Object>> getAllRowsByColumnValue(String filePath, int sheetIndex, String columnName, String columnValue) {
        LinkedList<LinkedHashMap<String, Object>> excelDataList = readExcel(filePath, sheetIndex);
        try {
            return  excelDataList.stream()
                    .filter(row -> row.containsKey(columnName) && row.get(columnName).equals(columnValue))
                    .collect(Collectors.toList());
        } catch (Exception var6) {
            throw new Error(String.format("Error when trying to return an excel row with column name '%s' and column value '%s'<br>Error: %s", columnName, columnValue, var6.getMessage()));
        }
    }

    /**
     * Close the given workbook
     * @param workbook the workbook
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static void closeWorkbook(@Nullable Workbook workbook){
        try {
            if (workbook != null) {
                workbook.close();
            }
        }
        catch (Exception e){
            throw new Error(String.format("Failed to close the workbook<br>Error: %s", e.getMessage()));
        }
    }

    /**
     * Close the given InputStream
     * @param inputStream the inputStream
     * @author genosar.dafna
     * @since 18.12.2024
     */
    private static void closeInputStream(@Nullable InputStream inputStream){
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        catch (Exception e){
            throw new Error(String.format("Failed to close the inputStream<br>Error: %s", e.getMessage()));
        }
    }

    /**
     * Close the given OutputStream
     * @param outputStream the outputStream
     * @author genosar.dafna
     * @since 30.06.2024
     */
    private static void closeOutputStream(@Nullable OutputStream outputStream){
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        catch (Exception e){
            throw new Error(String.format("Failed to close the outputStream<br>Error: %s", e.getMessage()));
        }
    }

    /** Get all excel data as a list of hashmap
     *
     * @param filePath path of the file
     * @return list of hashmap with all the file data
     * @throws IOException IOException
     * @author umflat.lior
     * @since 7.1.2025
     */
    public static List<HashMap<String, String>> getExcelFileDataAsListOfHashMaps(String filePath) throws IOException {
        List<HashMap<String, String>> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        // Assuming the first row contains column headers
        Row headerRow = rowIterator.next();
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            HashMap<String, String> rowData = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                rowData.put(headers.get(i), cell.toString());
            }
            data.add(rowData);
        }

        workbook.close();
        fis.close();
        return data;
    }

    /**
     * remove X first rows from excel file and save this changes
     *
     * @param path path to excel file
     * @param rowsAmountToDelete amount of rows to delete
     * @throws IOException IOException
     * @author reed.dakota
     * @since 10.08.2025
     */
    public static void removeXFirstRows(String path, int rowsAmountToDelete)  throws IOException {
        Workbook workbook;

        // Open file
        try (FileInputStream fis = new FileInputStream(path)) {
            workbook = WorkbookFactory.create(fis);
        } // Input stream is closed before writing

        Sheet sheet = workbook.getSheetAt(0);

        // Remove merged regions in the first N rows
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            if (mergedRegion.getFirstRow() < rowsAmountToDelete) {
                sheet.removeMergedRegion(i);
            }
        }

        // Shift rows up
        for (int i=0; i<rowsAmountToDelete; i++) {
            sheet.shiftRows(1, sheet.getLastRowNum(), -1);
        }

        // Save to same file
        try (FileOutputStream fos = new FileOutputStream(path)) {
            workbook.write(fos);
        }
        workbook.close();
    }
}
