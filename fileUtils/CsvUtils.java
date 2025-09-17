package fileUtils;

import collectionUtils.MapUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import enumerations.MessageLevel;
import miscellaneousUtils.RandomUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static objectsUtils.StringUtils.removeByteOrderMark;
import static reportUtils.Report.reportAndLog;


/**
 * Class holds methods to work with CSV files
 */
@SuppressWarnings({"unused", "unchecked"})
public class CsvUtils {

    private final static Logger logger= LoggerFactory.getLogger(CsvUtils.class);

    /**
     * Return all lines of text from CSV file
     * @return all lines of text from CSV file
     * @author plot.ofek
     * @since 02.05.2021
     */
    public static List<String[]> getAllLinesOfTextFromCsv(String filePath) {

        List<String> csvList = new ArrayList<>();
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readAll(reader);
    }

    /**
     * Return all lines from CSV file
     * @return all lines from CSV file
     * @author genosar.dafna
     * @since 17.07.2025
     */
    public static LinkedList<String[]> readAll(String filePath) {

        Reader reader;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
            return (LinkedList<String[]>) readAll(reader);
        }
        catch (Exception e) {
            throw new RuntimeException ("Failed to read CSV file %s".formatted(filePath));
        }
    }

    /**
     * Return all lines of text from CSV file
     * @param reader Reader instance
     * @return all lines of text from CSV file
     * @author plot.ofek
     * @since 02.05.2021
     * @author genosar.dafna
     * @since 14.07.2025
     */
    public static List<String[]> readAll(Reader reader) {

        List<String[]> list;
        try {
            CSVReader csvReader = new CSVReader(reader);
            list = csvReader.readAll();
            reader.close();
            csvReader.close();
            return list;
        }
        catch (Exception e) {
            throw new RuntimeException ("Failed to read CSV file");
        }
    }

    /**
     * Return all lines from CSV file - line by line, instead of all content together (line by line is better for large CVS files)
     * @param filePath the path of the file to read
     * @param useCsvReader true if to user CsvReader / false if to user BufferedReader (BufferedReader is faster, while CsvReader is more robust for tricky CSV files)
     * @return all lines from CSV file
     * @author genosar.dafna
     * @since 29.06.2025
     */
    public static LinkedList<String[]> readAllLineByLine(String filePath, boolean useCsvReader) throws IOException, CsvValidationException {

        FileReader fileReader = FileUtils.getFileReader(filePath);

        List<String[]> allRows = new ArrayList<>();

        if(useCsvReader){
            CSVReader reader = new CSVReader(fileReader);
            return readAllLineByLine(reader);
        }
        else {
            BufferedReader reader = new BufferedReader(fileReader);
            return readAllLineByLine(reader);
        }
    }

    /**
     * Return all lines from CSV file - line by line, instead of all content together (line by line is better for large CVS files)
     * using BufferedReader which is faster than CSVReader while CSVReader is more robust for tricky CSV files
     * @param reader BufferedReader instance
     * @return all lines from CSV file
     * @author genosar.dafna
     * @since 29.06.2025
     */
    public static LinkedList<String[]> readAllLineByLine(BufferedReader reader) throws IOException {

        LinkedList<String[]> allRows = new LinkedList<>();
        int lineCounter = 0;
        if (reader != null) {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(","); // basic CSV split
                    allRows.add(values);
                }
                lineCounter++;
            }
            catch(IOException e){
                throw new IOException("Failed to read CVS file - line index %d".formatted(lineCounter));
            }
        }
        return allRows;
    }

    /**
     * Return all lines from CSV file - line by line, instead of all content together (line by line is better for large CVS files)
     * using CSVReader which is more robust for tricky CSV files. For faster performance with no tricky CVSs it is better to use readAllLineByLine(BufferedReader reader)
     * @param reader CSVReader  instance
     * @return all lines from CSV file
     * @author genosar.dafna
     * @since 29.06.2025
     */
    public static LinkedList<String[]> readAllLineByLine(CSVReader reader) throws CsvValidationException {

        LinkedList<String[]> allRows = new LinkedList<>();
        int lineCounter = 0;
        if (reader != null) {
            try {
                String[] line;
                while ((line = reader.readNext()) != null){
                    allRows.add(line);
                }
                lineCounter++;
            }
            catch(IOException e){
                throw new CsvValidationException("Failed to read CVS file - line index %d".formatted(lineCounter));
            }
            catch(CsvValidationException e2){
                throw new CsvValidationException("CsvValidationException when reading CVS file - line index %d".formatted(lineCounter));
            }
        }
        return allRows;
    }

    /**
     * Return all lines of text from CSV file as a list of hashmaps
     * @return all lines of text from CSV file as a list of hashmaps
     * @author Dafna Genosar
     * @since 22.12.2021
     * @since 08.12.2024
     */
    public static <M extends Map<String, String>, L extends List<M>> L getAllLinesFromCsvAsHashMap(String filePath) {

        List<String[]> csvListLines = getAllLinesOfTextFromCsv(filePath);

        List<Map<String, String>> mapToReturn = new ArrayList<>();

        String[] columns = csvListLines.get(0);

        for(int i=1; i< csvListLines.size(); i++)
        {
            String[] currentLine = csvListLines.get(i);
            Map<String, String> currentLineHash = new HashMap<>();

            for(int c=0; c< currentLine.length; c++)
            {
                String columnName = removeByteOrderMark(columns[c]);
                String columnValue = removeByteOrderMark(currentLine[c]);
                currentLineHash.put(columnName, columnValue);
            }
            mapToReturn.add(currentLineHash);
        }
        return (L)mapToReturn;
    }

    /**
     * Return all lines of text from CSV file as a list of hashmaps
     * @return all lines of text from CSV file as a list of hashmaps
     * @author Dafna Genosar
     * @since 29.06.2025
     */
    public static <M extends Map<String, String>, L extends List<M>> L getCsvLinesAsListOfHashMaps(String filePath, boolean readLineByLine, boolean useCsvReader) throws CsvValidationException, IOException {

        LinkedList<String[]> csvListLines = readLineByLine? readAllLineByLine(filePath, useCsvReader) : readAll(filePath);
        return MapUtils.convertListOfStringArraysToListOfHashMaps(csvListLines);
    }

    /**
     * Return all lines of text from CSV file as a Hashmap that hols hashmaps
     * Each entry represent a line in the CSV.
     * The uniqueKey is the name of the header in the CSV that has unique values under it (each line in the CSV has a unique value under this header)
     * This uniqueKey will be the main hashmap's key for each entry
     * Example: where the uniqueKey is 'id'
     *          key: 12345
     *          value: (Hashmap)
     *                  key: id             value: 12345
     *                  key: first name     value: Shoshana
     *                  key: last name      value: Katz
     *
     * @return all lines of text from CSV file as a Hashmap that hols hashmaps
     * @author Dafna Genosar
     * @since 30.06.2025
     */
    public static <M extends Map<String, String>, MP extends Map<String, M>> MP getCsvLinesAsHashMapOfHashMaps(String filePath, String uniqueKey, boolean readLineByLine, boolean useCsvReader) throws CsvValidationException, IOException {

        List<String[]> csvListLines = readLineByLine? readAllLineByLine(filePath, useCsvReader) : readAll(filePath);
        return MapUtils.convertListOfStringArraysToHashMapOfHashMaps(csvListLines, uniqueKey);
    }

    /**
     * get List of all the rows in CSV file
     *
     * @param csvPath      - the path of the csv file
     * @return list of all the data in each row (each String contain all the data of one row)
     * @author - Lior Umflat
     * @since - 2.6.2021
     */
    public static List<String> getCSVFileRows(String csvPath) {
        List<String> csvFileRows = new ArrayList<>();
        try (BufferedReader csvReader = new BufferedReader(new FileReader(csvPath))) {
            String row;
            while ((row = csvReader.readLine()) != null) {
                csvFileRows.add(row);
            }
        } catch (IOException e) {
            System.out.println("couldn't readLine or close the csv file");
            logger.info("couldn't readLine or close the csv file. see details - " + Arrays.toString(e.getStackTrace()));
        }
        return csvFileRows;
    }


    /**
     * get List of all the rows in CSV file by numberOfRows in the file
     *
     * @param csvPath      - the path of the csv file
     * @param numberOfRows - number of rows in the file
     * @return list of all the data in each row (each String contain all the data of one row)
     * @author - Lior Umflat
     * @since - 2.6.2021
     */
    public static List<String> getCSVFileRows(String csvPath, int numberOfRows) {
        //create list that will contains the csv file rows
        List<String> csvFileRows = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csvPath));
            String row;
            for (int i = 0; i < numberOfRows + 1; i++) //adding one because the first row is the headers row
            {
                row = csvReader.readLine();
                //close the csvReader and break from the loop when reaching the end of the file
                if (row == null) {
                    csvReader.close();
                    break;
                }
                //add all the rows to the list
                csvFileRows.add(row);

            }
        } catch (Exception e) {
            logger.warn("couldn't readLine or close the csv file. see details - " + Arrays.toString(e.getStackTrace()));
        }
        return csvFileRows;
    }

    /**
     * function search value in a specific column in csv file and return the record row
     * @param columnNameToSearch - column to search value
     * @param valueToSearch -value to search
     * @param filepath - file to search
     * @return record with request value in request column
     * @throws Exception Exception
     * @author Yael.Rozenfeld
     * @since 1.11.2021
     */
    public static CSVRecord getRecordFromCSV(String columnNameToSearch,String valueToSearch, String filepath) throws Exception {
        //loud csv file
        URL url = new File(filepath).toURI().toURL();
        //read CSV file
        Reader in = new InputStreamReader(new BOMInputStream(url.openStream()), StandardCharsets.UTF_8);
        CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader());
//      search value in the request column
        for (CSVRecord record : parser) {
            String value = record.get(columnNameToSearch);
            if(value.equalsIgnoreCase(valueToSearch))
                return record;
        }
        //if value could not be found / if value was not found
        logger.info("value: " + valueToSearch +" wasn't found in column: "  +columnNameToSearch+ "in file: " + filepath);
        return null;
    }

    /**
     * Retrieves a record from a CSV file by matching a specific column value.
     * @param columnName  - The column name to match.
     * @param columnValue - The value to search for.
     * @param inputStream - The InputStream of the CSV file.
     * @return The matching CSVRecord.
     * @throws Exception If an error occurs or the record is not found.
     * @author Lesnichy.Kostya
     * @since 28.11.2024
     */
    public static CSVRecord getRecordFromCSV(String columnName, String columnValue, InputStream inputStream) throws Exception {
        try (CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(new InputStreamReader(inputStream))) {

            for (CSVRecord record : parser) {
                if (record.get(columnName).equals(columnValue)) {
                    return record;
                }
            }

            throw new IllegalArgumentException("No record found with " + columnName + " = " + columnValue);
        }
    }

    /**
     * Get all rows from csv file
     * @param csvFilePath - file get its rows
     * @param isCommaDelimiter - true for comma delimiter, false for tab delimiter
     * @author Yael Rozenfeld
     * @since 23.02.2023
     * @throws IOException IOException
     */
    public static List<String[]> getCsvRows(String csvFilePath,boolean isCommaDelimiter) throws IOException {
        // List to store CSV rows
        List<String[]> rows = new ArrayList<>();
        String delimiter =isCommaDelimiter?",":"\t";
        BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] row = line.split(delimiter);
            rows.add(row);
        }
        return rows;
    }

     /**
     * Get random row from csv file
     * @param filePath - file to random row
     * @param startRandom - first row to start random (first row in file is 0)
     * @param isCommaDelimiter - true for comma delimiter, false for tab delimiter
     * @return String[] contains random row
     * @author Yael Rozenfeld
     * @since 23.02.2023
     * @throws IOException IOException
     */
    public static String[] getRandomRowFromCsvFile(String filePath,int startRandom,boolean isCommaDelimiter) throws IOException {
        // List to store CSV rows
        List<String[]> rows = getCsvRows(filePath,isCommaDelimiter);
        reportAndLog("count of rows in csv file: " + rows.size(), MessageLevel.INFO);
        // Get a random row from the list
        int index= RandomUtils.getRandomNumber(startRandom,rows.size());
        reportAndLog("random row is "  + index,MessageLevel.INFO);
        return rows.get(index);
    }

    /**
     * @param csvFilePath csv file path
     * @param allData the data to write
     * @author genosar.dafna
     * @since 14.07.2024
     */
    public static void writeToCsv(String csvFilePath, List<String[]> allData) {

        try{
            FileWriter fileWriter = new FileWriter(csvFilePath);
            CSVWriterBuilder writer = new CSVWriterBuilder(fileWriter).withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER);
            ICSVWriter ICSVWriter = writer.build();

            for(String[] data : allData){
                ICSVWriter.writeNext(data);
            }

            ICSVWriter.close();
            fileWriter.close();
        }
        catch (Exception e){
            throw new Error(String.format("Failed to write to path: %s<br>Error: %s", csvFilePath, e.getMessage()));
        }
    }

    /**
     * Updates a specific cell in a CSV file.
     *
     * @param filePath The path to the CSV file.
     * @param row The zero-based row index to update.
     * @param col The zero-based column index to update.
     * @param newValue The new value for the specified cell.
     * @throws IOException If an I/O error occurs.
     *
     */
    public static void updateCell(String filePath, int row, int col, String newValue) throws IOException {
        List<String[]> csvData = new ArrayList<>();
        String line;

        // Read the CSV file into memory
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                csvData.add(line.split(","));
            }
        }

        // Validate the row and column
        if (row < 0 || row >= csvData.size() || col < 0 || col >= csvData.get(row).length) {
            throw new IllegalArgumentException("Invalid row or column index.");
        }

        // Update the specific cell
        csvData.get(row)[col] = newValue;

        // Write the updated data back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] rowData : csvData) {
                bw.write(String.join(",", rowData));
                bw.newLine();
            }
        }
    }
}
