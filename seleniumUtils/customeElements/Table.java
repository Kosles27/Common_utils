package seleniumUtils.customeElements;

import Managers.WebDriverInstanceManager;
import collectionUtils.ListUtils;
import drivers.TesnetWebElement;
import enumerations.MessageLevel;
import objectsUtils.ObjectsUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waitUtils.WaitWrapper;

import javax.annotation.Nullable;
import java.util.*;

import static reportUtils.Report.reportAndLog;

/**
 * This class supports tables.
 * @author genosar.dafna
 * @since 07.03.2022
 */
@SuppressWarnings({"unused", "unchecked"})
public class Table extends TesnetWebElement {

    private static final Logger logger = LoggerFactory.getLogger(Table.class);

    protected WebElement localElement;
    protected WebDriver driver;
    public WebElement headerRowElement;
    public TableRow headerRow;
    public LinkedHashMap<Integer, String> headersDetails;
    public LinkedHashMap<String, Integer> headersDetailsByName;

    public Table(WebDriver driver){
        this(driver, WaitWrapper.waitForVisibilityOfElementLocated(driver, By.xpath(".//table"), "Table", 10));
    }

    public Table(WebDriver driver, WebElement element)
    {
        super(element);
        localElement = element;
        this.driver = driver;
        headerRowElement = getHeaderRowElement();
        headerRow = getHeaderRow();
        headersDetails = retrieveHeadersDetails();
    }

    public Table(WebElement element)
    {
        super(element);
        localElement = element;
        driver = WebDriverInstanceManager.getDriverFromMap();
        headerRowElement = getHeaderRowElement();
        headerRow = getHeaderRow();
        headersDetails = retrieveHeadersDetails();
    }

    /**
     * Return the header row as WebElement
     * @return the header row as WebElement
     * @author Dafna Genosar
     * @since 07.03.2022
     * @author Dafna Genosar
     * @since 08.08.2023
     */
    public WebElement getHeaderRowElement()
    {
        if(headerRowElement != null)
            return headerRowElement;

        try {
            return localElement.findElement(By.xpath(".//thead/tr"));
        }
        catch(Exception e)
        {
            logger.info("No header displays on the table");
            return null;
        }
    }

    /**
     * Return the header row as a TableRow object
     * @return the header row as a TableRow object
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 25.05.2023
     */
    public TableRow getHeaderRow()
    {
        if(headerRow != null)
            return headerRow;

        WebElement headerRowElement = getHeaderRowElement();
        if(headerRowElement == null) return null;

        return new TableRow(driver, headerRowElement, this);
    }

    /**
     * Return the header row details as a map that holds the column index as a key and the text as a value
     * @return the header row details as a map that holds the column index as a key and the text as a value
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 24.04.2024
     */
    protected LinkedHashMap<Integer, String> retrieveHeadersDetails()
    {
        if(headersDetails == null || headersDetailsByName == null) {
            logger.info("Retrieve table's headers' details");

            if(headersDetails == null) headersDetails = new LinkedHashMap<>();
            if(headersDetailsByName == null) headersDetailsByName = new LinkedHashMap<>();

            LinkedHashMap<Integer, String> detailsToReturn = new LinkedHashMap<>();

            TableRow headerRow = getHeaderRow();

            if (headerRow != null) {
                List<WebElement> cells = headerRow.getCells();

                for (int i = 0; i < cells.size(); i++) {
                    String text = cells.get(i).getText();
                    detailsToReturn.put(i, text);
                    headersDetailsByName.put(text, i);
                }
            }
            headersDetails = detailsToReturn;
        }
        return headersDetails;
    }

    /**
     * Return the header row details as a map that holds the column index as a key and the text as a value
     * @return the header row details as a map that holds the column index as a key and the text as a value
     * @author Dafna Genosar
     * @since 07.03.2022
     */
    public <K extends Integer, V extends String, T extends LinkedHashMap<K, V>> T getHeadersDetails()
    {
        return (T)headersDetails;
    }

    /**
     * Return the header row details as a map that holds the text as a key and the column index as a value
     * @return the header row details as a map that holds the text as a key and the column index as a value
     * @author Dafna Genosar
     * @since 23.04.2025
     */
    public <K extends String, V extends Integer, T extends LinkedHashMap<K, V>> T getHeadersByNameDetails()
    {
        return (T)headersDetailsByName;
    }

    /**
     * @return Headers names
     * @author genosar.dafna
     * @since 28.08.2023
     * @since 09.11.2023
     */
    public LinkedList<String> getHeadersNames(){

        TableRow headerRow = getHeaderRow();

        return getHeadersNames(headerRow);
    }

    /**
     * @param headerRow the header row to check
     * @return Headers names
     * @author genosar.dafna
     * @since 09.11.2023
     */
    public LinkedList<String> getHeadersNames(TableRow headerRow){

        LinkedList<String> namesToReturn = new LinkedList<>();

        if(headerRow != null)
        {
            List<WebElement> cells = headerRow.getCells();

            for (WebElement cell : cells) {
                String text = cell.getText();
                namesToReturn.add(text);
            }
        }
        return namesToReturn;
    }

    /**
     * Return the header row index according to the selected column name or -1 if was not found
     * @return the header row index according to the selected column name or -1 if was not found
     * @author Dafna Genosar
     * @since 07.03.2022
     */
    public int getHeaderIndex(String headerName)
    {
        int indexToReturn = -1;
        for (Map.Entry<Integer, String> columnHeader: headersDetails.entrySet()) {
            if(columnHeader.getValue().contains(headerName))
            {
                return columnHeader.getKey();
            }
        }
        return indexToReturn;
    }

    /**
     * Return the header name according to the given index
     * @return the header name according to the given index
     * @author Dafna Genosar
     * @since 30.07.2023
     */
    public String getHeaderName(int index)
    {
        if(!headersDetails.containsKey(index))
            throw new Error(String.format("The table does not have a header in index %d", index));

        return headersDetails.get(index);
    }

    /**
     * @return all rows in the table's body as WebElements or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 16.10.2023
     */
    public List<WebElement> getRowsElements()
    {
        logger.info("Get all table's rows elements");
        try {
            return new LinkedList<>(localElement.findElements(By.xpath(".//tbody/tr[not(contains(@class, 'hidden'))]")));
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

    /**
     * @param startIndex optional the index of the row to start from. first row is in index 1. if null, it will return from the 1st row
     * @param endIndex  optional the index of the row to end. . if null, it will return up to the last row
     * @return all rows in the table's body as WebElements or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 27.03.2025
     */
    public LinkedList<WebElement> getRowsElementsInRange(@Nullable Integer startIndex, @Nullable Integer endIndex){

        String startIndexString = startIndex == null? "" : " and position() >= %d".formatted(startIndex);
        String endIndexString = endIndex == null? "" : " and position() <= %d".formatted(endIndex);

        logger.info("Get the table's rows elements within the given range");
        try {
            return new LinkedList<>(localElement.findElements(By.xpath(".//tbody/tr[not(contains(@class, 'hidden'))%s%s]".formatted(startIndexString, endIndexString))));
        }
        catch (Exception e){
            return new LinkedList<>();
        }
    }

    /**
     * @return all rows in the table's body as WebElements or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 29.02.2024
     */
    public List<WebElement> waitForRowsElements()
    {
        logger.info("Wait for all table's rows element");
        try {
            return new LinkedList<>( WaitWrapper.waitForVisibilityOfAllElementsLocatedBy(driver, localElement, By.xpath(".//tbody/tr"), "Table rows", 30));
        }
        catch (Throwable e)
        {
            return new ArrayList<>();
        }
    }

    /**
     * @param timeout time to wait for the rows' elements in seconds
     * @return all rows in the table's body as WebElements or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 11.11.2024
     */
    public List<WebElement> waitForRowsElements(int timeout)
    {
        logger.info("Wait for all table's rows element");
        try {
            return new LinkedList<>( WaitWrapper.waitForVisibilityOfAllElementsLocatedBy(driver, localElement, By.xpath(".//tbody/tr"), "Table rows", timeout));
        }
        catch (Throwable e)
        {
            return new ArrayList<>();
        }
    }

    /**
     * @return all rows in the table's body as TableRow or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 07.03.2022
     * @author Dafna Genosar
     * @since 18.10.2022
     * @since 25.05.2023
     */
    public <T extends TableRow> List<T> getRows() {

        LinkedList<WebElement> rowsElements = new LinkedList<>(this.getRowsElements());

        LinkedList<T> tableRows = new LinkedList<>();
        for (WebElement rowElement: rowsElements){
            tableRows.add((T)new TableRow(driver, rowElement, this));
        }
        return tableRows;
    }

    /**
     * @param classType the class type of the table row
     * @return all rows in the table's body as TableRow or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 07.03.2022
     * @since 23.11.2023
     */
    public <T extends TableRow> List<T> getRows(Class<T> classType) {

        LinkedList<WebElement> rowsElements = new LinkedList<>(this.getRowsElements());

        LinkedList<T> tableRows = new LinkedList<>();
        for (WebElement rowElement: rowsElements){
            T row = ObjectsUtils.newInstance(classType, driver, rowElement, this);
            tableRows.add(row);
        }
        return tableRows;
    }

    /**
     * @param classType the class type of the table row
     * @param startIndex optional the index of the row to start from. first row is in index 1. if null, it will return from the 1st row
     * @param endIndex  optional the index of the row to end. . if null, it will return up to the last row
     * @return all rows in the table's body as TableRow or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 14.09.2025
     */
    public <T extends TableRow> LinkedList<T> getRowsInRange(Class<T> classType, @Nullable Integer startIndex, @Nullable Integer endIndex) {

        LinkedList<WebElement> rowsElements = this.getRowsElementsInRange(startIndex, endIndex);

        LinkedList<T> tableRows = new LinkedList<>();
        for (WebElement rowElement: rowsElements){
            T row = ObjectsUtils.newInstance(classType, driver, rowElement, this);
            tableRows.add(row);
        }
        return tableRows;
    }

    /**
     * Return all rows that match the specific data of columns' names or Indexes and values
     * @param rowData map of columns' names or Indexes and values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return all rows that match the specific data of columns' names or Indexes and values
     * @author Dafna Genosar
     * @since 18.10.2022
     * @since 17.10.2023
     */
    public <T extends TableRow, K, V, M extends Map<K, V>> List<T> getRows(M rowData)
    {
        reportAndLog(String.format("Find all rows with data:<br> %s", rowData.toString().replace("{", "").replace("}", "")), MessageLevel.INFO);

        List<T> tableRows = this.getRows();

        List<T> rowsToReturn = new ArrayList<>();

        //Go over each row in the table
        for (T tableRow: tableRows) {
            if(tableRow.doesRowMatch(rowData))
                rowsToReturn.add(tableRow);
        }

        return rowsToReturn;
    }

    /**
     * Return all rows that match the specific data of columns' names or Indexes and values
     * @param classType the class type of the table row
     * @param rowData map of columns' names or Indexes and values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return all rows that match the specific data of columns' names or Indexes and values
     * @author Dafna Genosar
     * @since 30.07.2023
     * @since 23.11.2023
     */
    public <T extends TableRow, K, V, M extends Map<K, V>> List<T> getRows(Class<T> classType, M rowData)
    {
        reportAndLog(String.format("Find all rows with data:<br> %s", rowData.toString().replace("{", "").replace("}", "")), MessageLevel.INFO);

        LinkedList<T> tableRows = new LinkedList<>(this.getRows());

        List<T> rowsToReturn = new LinkedList<>();

        //Go over each row in the table
        for (T tableRow: tableRows) {
            if(tableRow.doesRowMatch(rowData))
                rowsToReturn.add(ObjectsUtils.newInstance(classType, tableRow));
        }

        return rowsToReturn;
    }

    /**
     * Wait for the table's rows to display and return them
     * @return all rows in the table's body as TableRow or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 19.09.2022
     * @author Dafna Genosar
     * @since 18.10.2022
     * @since 25.05.2023
     */
    public <T extends TableRow> List<T> waitForRows() {

        LinkedList<WebElement> rowsElements = new LinkedList<>(this.waitForRowsElements());

        List<T> tableRows = new LinkedList<>();

        for (WebElement rowElement: rowsElements){
            tableRows.add((T)new TableRow(driver, rowElement, this));
        }
        return tableRows;
    }

    /**
     * Wait for the table's rows to display and return them
     * @param timeout time to wait for the rows' elements in seconds
     * @return all rows in the table's body as TableRow or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 11.11.2042
     */
    public <T extends TableRow> List<T> waitForRows(int timeout) {

        LinkedList<WebElement> rowsElements = new LinkedList<>(this.waitForRowsElements(timeout));

        List<T> tableRows = new LinkedList<>();

        for (WebElement rowElement: rowsElements){
            tableRows.add((T)new TableRow(driver, rowElement, this));
        }
        return tableRows;
    }

    /**
     * Wait for the table's rows to display and return them
     * @param classType class type
     * @return all rows in the table's body as TableRow or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 19.09.2022
     * @author Dafna Genosar
     * @since 18.10.2022
     * @since 26.11.2023
     */
    public <T extends TableRow> List<T> waitForRows(Class<T> classType) {
        LinkedList<WebElement> rowsElements = new LinkedList<>(this.waitForRowsElements());
        List<T> tableRows = new LinkedList<>();
        for (WebElement rowElement: rowsElements) {
            tableRows.add(ObjectsUtils.newInstance(classType, driver, rowElement, this));
        }

        return tableRows;
    }

    /**
     * Wait for the table's rows to display and return them
     * @param classType class type
     * @param timeout time to wait for the rows' elements in seconds
     * @return all rows in the table's body as TableRow or an empty list if no rows exist
     * @author Dafna Genosar
     * @since 11.11.2024
     */
    public <T extends TableRow> List<T> waitForRows(Class<T> classType, int timeout) {
        LinkedList<WebElement> rowsElements = new LinkedList<>(this.waitForRowsElements(timeout));
        List<T> tableRows = new LinkedList<>();
        for (WebElement rowElement: rowsElements) {
            tableRows.add(ObjectsUtils.newInstance(classType, driver, rowElement, this));
        }

        return tableRows;
    }

    /**
     * @return the number of rows in the table
     * @author Dafna Genosar
     * @since 07.03.2022
     */
    public int getNumberOfRows()
    {
        return this.getRowsElements().size();
    }

    /**
     * @return the number of columns in the table
     * @author Dafna Genosar
     * @since 04.06.2023
     */
    public int getNumberOfColumns(TableRow row)
    {
        return row.getNumberOfColumns();
    }

    /**
     * Return the first row found according to specific data of columns' names or Indexes and values
     * @param rowData map of columns' names or Indexes and values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return a row according to specific data of columns' names or Indexes and values
     * @author Dafna Genosar
     * @since 07.03.2022
     * @author Dafna Genosar
     * @since 18.10.2022
     */
    public <T extends TableRow, K, V, M extends Map<K, V>> T getRow(M rowData)
    {
        reportAndLog(String.format("Find a row with data:<br> %s", rowData.toString().replace("{", "").replace("}", "")), MessageLevel.INFO);

        List<T> tableRows = this.getRows();

        //Go over each row in the table
        for (T tableRow: tableRows) {
            if(tableRow.doesRowMatch(rowData))
                return tableRow;
        }
        return null;
    }

    /**
     * Return the first row found according to specific data of columns' names or Indexes and values
     * @param classType the class type of the table row
     * @param rowData map of columns' names or Indexes and values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return a row according to specific data of columns' names or Indexes and values
     * @author Dafna Genosar
     * @since 07.03.2022
     * @author Dafna Genosar
     * @since 28.11.2023
     */
    public <T extends TableRow, K, V, M extends Map<K, V>> T getRow(Class<T> classType, M rowData)
    {
        reportAndLog(String.format("Find a row with data:<br> %s", rowData.toString().replace("{", "").replace("}", "")), MessageLevel.INFO);

        List<T> tableRows = this.getRows();

        //Go over each row in the table
        for (T tableRow: tableRows) {
            if(tableRow.doesRowMatch(rowData))
                return ObjectsUtils.newInstance(classType, tableRow);
        }
        return null;
    }

    /**
     * Get the first row that has the given cell value under the given header name
     * @param headerName header name
     * @param cellValue the cell value
     * @return the first row that has the given cell value under the given header name
     * @author genosar.dafna
     * @since 20.07.2023
     */
    public <T extends TableRow> T getRow(String headerName, String cellValue){
        HashMap<Object, Object> rowData = new HashMap<>();
        rowData.put(headerName, cellValue);
        return getRow(rowData);
    }

    /**
     * Get the first row that has the given cell value under the given header name
     * @param classType the class type of the table row
     * @param headerName header name
     * @param cellValue the cell value
     * @return the first row that has the given cell value under the given header name
     * @author genosar.dafna
     * @since 30.07.2023
     */
    public <T extends TableRow> T getRow(Class<T> classType, String headerName, String cellValue){
        HashMap<Object, Object> rowData = new HashMap<>();
        rowData.put(headerName, cellValue);
        return getRow(classType, rowData);
    }

    /**
     * Return a row in a specific index
     * @param index the row's index
     * @return a row in a specific index
     * @author genosar.dafna
     * @since 07.03.2022
     * @author genosar.dafna
     * @since 16.10.2023
     */
    public <T extends TableRow> T getRow(int index)
    {
        reportAndLog(String.format("Find a row in index %d", index), MessageLevel.INFO);

        try {
            WebElement rowsElement = localElement.findElement(By.xpath(String.format(".//tbody/tr[not(contains(@class, 'hidden'))][%d]", index+1)));
            return (T)(new TableRow(driver, rowsElement, this));
        }
        catch (Exception e)
        {
            throw new Error(String.format("The table does not have a row in index %d", index));
        }
    }

    /**
     * Return a row in a specific index
     * @param classType the class type of the table row
     * @param index the row's index
     * @return a row in a specific index
     * @author Dafna Genosar
     * @since 30.07.2023
     */
    public <T extends TableRow> T getRow(Class<T> classType, int index)
    {
        reportAndLog(String.format("Find a row in index %d", index), MessageLevel.INFO);
        List<WebElement> rowsElements = getRowsElements();

        if(index >= rowsElements.size() || index < 0)
            throw new Error(String.format("The table does not have a row in index %d", index));

        return ObjectsUtils.newInstance(classType, driver, rowsElements.get(index), this);
    }


    /**
     * Wait for all rows and get the first row that has the given cell value under the given header name
     * @param headerName header name
     * @param cellValue the cell value
     * @return the first row that has the given cell value under the given header name
     * @author genosar.dafna
     * @since 20.07.2023
     */
    public <T extends TableRow> T waitForRow(String headerName, String cellValue)
    {
        reportAndLog(String.format("Wait for a row with value:<b>%s</b> under header: :<b>%s</b>", cellValue, headerName), MessageLevel.INFO);

        waitForRows();

        return getRow(headerName, cellValue);
    }

    /**
     * Wait for all rows and get the first row that has the given cell value under the given header name
     * @param headerName header name
     * @param cellValue the cell value
     * @return the first row that has the given cell value under the given header name
     * @author genosar.dafna
     * @since 20.07.2023
     */
    public <T extends TableRow> T waitForRow(Class<T> classType, String headerName, String cellValue)
    {
        reportAndLog(String.format("Wait for a row with value:<b>%s</b> under header: :<b>%s</b>", cellValue, headerName), MessageLevel.INFO);

        waitForRows();

        return getRow(classType, headerName, cellValue);
    }

    /**
     * Wait for the first row found according to specific data of columns' names or Indexes and values
     * @param rowData map of columns' names or Indexes and values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return a row according to specific data of columns' names or Indexes and values
     * @author Dafna Genosar
     * @since 07.09.2023
     */
    public <K, V, TR extends TableRow, M extends Map<K, V>> TR waitForRow(M rowData)
    {
        reportAndLog(String.format("Wait for a row with data:<br> %s", rowData.toString().replace("{", "").replace("}", "")), MessageLevel.INFO);

        List<TR> tableRows = waitForRows();

        //Go over each row in the table
        for (TR tableRow: tableRows) {
            if(tableRow.doesRowMatch(rowData))
                return tableRow;
        }
        return null;
    }

    /**
     * Wait for the first row found according to specific data of columns' names or Indexes and values
     * @param classType the expected row's class type
     * @param rowData map of columns' names or Indexes and values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return a row according to specific data of columns' names or Indexes and values
     * @author Dafna Genosar
     * @since 07.09.2023
     */
    public <K, V, TR extends TableRow, M extends Map<K, V>> TR waitForRow(Class<TR> classType, M rowData)
    {
        reportAndLog(String.format("Wait for a row with data:<br> %s", rowData.toString().replace("{", "").replace("}", "")), MessageLevel.INFO);

        List<TR> tableRows = waitForRows();

        //Go over each row in the table
        for (TR tableRow: tableRows) {
            if(tableRow.doesRowMatch(rowData))
                return tableRow;
        }
        return null;
    }

    /**
     * Return the index of the first row according to specific data of columns' names or Indexes and values
     * @param rowData Map of columns' names or Indexes and values
     * in case you would like a few values to match and to make sure others DO NOT match, please add != before the key name
     * example:
     * if you would like to make sure the row has name = 'John', family name = 'Smith', but to make sure that the salary is NOT 0, then:
     *  Map.put("Name", "John")
     *  Map.put("Family Name", "Smith")
     *  Map.put("!=Salary", 0)
     * @return the row index row according to specific data of columns' names or Indexes and values
     * @author Dafna Genosar
     * @since 07.03.2022
     * */
    public <K, V, M extends Map<K, V>> int getRowIndex(M rowData){

        List<TableRow> tableRows = getRows();

        //Go over each row in the table
        for (int i=0; i<tableRows.size(); i++) {
            if(tableRows.get(i).doesRowMatch(rowData))
                return i;
        }
        return -1;
    }

    /**
     * Get the given row's index within the table's rows
     * @param row the row to check the index for
     * @return the given row's index within the table's rows
     * @param <R> rows that extend TableRow
     * @author genosar.dafna
     * @since 17.10.2023
     */
    public <R extends TableRow> int getRowIndex(R row){

        return row.getIndex();
    }

    /**
     * Return the first row in the table
     * @return the first row in the table
     * @author Dafna Genosar
     * @since 10.03.2022
     * @author Dafna Genosar
     * @since 18.10.2022
     */
    public <T extends TableRow> T getFirstRow(){
        return getRow(0);
    }

    /**
     * Return the last row in the table
     * @return the last row in the table
     * @author Dafna Genosar
     * @since 10.03.2022
     * @since 25.06.2025
     */
    public <T extends TableRow> T getLastRow(){
        WebElement rowElement = getLastRowElement();
        if(rowElement == null)
            return null;
        return (T)(new TableRow(driver, rowElement, this));
    }

    /**
     * Return the last row in the table with Class Type
     * @return the last row in the table
     * @author Dafna Genosar
     * @since 26.06.2025
     */
    public <T extends TableRow> T getLastRow(Class<T> classType){
        WebElement rowElement = getLastRowElement();
        if(rowElement == null)
            return null;
        return ObjectsUtils.newInstance(classType, driver, rowElement, this);
    }

    /**
     * Return the last row in the table as WebElement
     * @return the last row in the table
     * @author Dafna Genosar
     * @since 23.06.2025
     * @since 25.06.2025
     */
    public WebElement getLastRowElement(){
        logger.info("Get last table's row element");
        try {
            return localElement.findElement(By.xpath("./tbody/tr[not(contains(@class, 'hidden'))][last()]"));
        }
        catch (Exception e)        {
            throw new NoSuchElementException("The last row could not be found in the table");
        }
    }

    /**
     * Get a random row from the given list of rows
     * @param <T> Type of row object
     * @param rows list of rows
     * @return a random wor from the table
     * @author genosar.dafna
     * @since  18.10.2022
     */
    public <T extends TableRow> T getRandomRow(List<T> rows){
        reportAndLog("Get a random row from the given list of rows", MessageLevel.INFO);
        return ListUtils.getRandomItemFromList(rows);
    }

    /**
     * Get a random row from the given list of rows
     * @param <T> Type of row object
     * @param rows list of rows
     * @return a random wor from the table
     * @author genosar.dafna
     * @since 01.08.2023
     * @since 28.11.2023
     */
    public <T extends TableRow> T getRandomRow(Class<T> classType, List<T> rows){
        T tableRow = getRandomRow(rows);
        return ObjectsUtils.newInstance(classType, tableRow);
    }

    /**
     * Get a random row from the table
     * @param <T> Type of row object
     * @return a random wor from the table
     * @author genosar.dafna
     * @since 18.10.2022
     */
    public <T extends TableRow> T getRandomRow(){
        reportAndLog("Get a random row from the table", MessageLevel.INFO);
        List<TableRow> rows = getRows();
        return (T)ListUtils.getRandomItemFromList(rows);
    }

    /**
     * Get a random row from the table
     * @param <T> Type of row object
     * @return a random wor from the table
     * @author genosar.dafna
     * @since 01.08.2023
     */
    public <T extends TableRow> T getRandomRow(Class<T> classType){
        T tableRow = getRandomRow();
        return ObjectsUtils.newInstance(classType, tableRow);
    }

    /**
     * Get the header cell elements
     * @return the header cell elements
     * @author genosar.dafna
     * @since 30.07.2023
     * @since 23.04.2025
     */
    protected List<WebElement> getHeaderCells(){
        return new LinkedList<>(getHeaderRow().findElements(By.tagName("th")));
    }

    /**
     * @param headerName the required header name
     * @return header cell
     * @author genosar.dafna
     * @since 10.10.2024
     */
    public WebElement getHeaderCell(String headerName)
    {
        int headerIndex = getHeaderIndex(headerName);
        if(headerIndex < 0)
            throw new Error(String.format("The header row does not have a cell with name %s", headerName));

        return getHeaderCells().get(headerIndex);
    }

    /**
     * @param headerIndex the required header index
     * @return header cell
     * @author genosar.dafna
     * @since 10.10.2024
     */
    public WebElement getHeaderCell(int headerIndex)
    {
        if(headerIndex < 0)
            throw new Error(String.format("The header row does not have a cell in index %s", headerIndex));

        return getHeaderCells().get(headerIndex);
    }

    /**
     * Get the checkbox in the header cell, by the given cell index
     * @param index the cell's index
     * @return the checkbox in the header cell, by the given cell index
     * @author genosar.dafna
     * @since 30.07.223
     */
    public CheckBox getHeaderCellCheckBox(int index)
    {
        WebElement cell = getHeaderCells().get(index);
        return getHeaderCellCheckBox(cell);
    }

    /**
     * Get the checkbox in the header cell, by the given header name
     * @param headerName the header cell header text
     * @return the checkbox in the cell, by the given header name
     * @author genosar.dafna
     * @since 30.07.223
     */
    public CheckBox getHeaderCellCheckBox(String headerName)
    {
        int headerIndex = getHeaderIndex(headerName);
        if(headerIndex < 0)
            throw new Error(String.format("The header row does not have a cell under header %s", headerName));

        WebElement cell = getHeaderCells().get(headerIndex);
        return getHeaderCellCheckBox(cell);
    }

    /**
     * Get the checkbox in the given header cell
     * @param cell the cell WebElement
     * @return the checkbox in the given header cell
     * @author genosar.dafna
     * @since 30.07.2023
     * @since 17.08.2023
     */
    public CheckBox getHeaderCellCheckBox(WebElement cell)
    {
        WebElement checkBox;
        try{
            checkBox = cell.findElement(By.xpath(".//*[self::mat-checkbox or self::input[@type='checkbox']]"));
        }
        catch (Exception e){
            throw new Error("Checkbox cannot be found in the header cell<br>Error: " + e.getMessage());
        }
        return new CheckBox(checkBox);
    }

    /**
     * Check the checkbox in the header to check all rows
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table checkAll(){

        CheckBox checkBox = getHeaderCellCheckBox(0);
        checkBox.check();
        return this;
    }

    /**
     * Uncheck the checkbox in the header to uncheck all rows
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table uncheckAll(){

        CheckBox checkBox = getHeaderCellCheckBox(0);
        checkBox.uncheck();
        return this;
    }

    /**
     * Check the checkbox in the header's cell
     * @param cell the cell element
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table checkHeaderCell(WebElement cell){

        CheckBox checkBox = getHeaderCellCheckBox(cell);
        checkBox.check();
        return this;
    }

    /**
     * Check the checkbox in the header's cell, by the given cell index
     * @param index the cell's index
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table checkHeaderCell(int index)
    {
        CheckBox checkBox = getHeaderCellCheckBox(index);
        checkBox.check();
        return this;
    }

    /**
     * Check the checkbox in the header's cell, by the given header name
     * @param headerName the cell header text
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table checkHeaderCell(String headerName)
    {
        CheckBox checkBox = getHeaderCellCheckBox(headerName);
        checkBox.check();
        return this;
    }

    /**
     * Uncheck the checkbox in the header's cell
     * @param cell the cell element
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table uncheckHeaderCell(WebElement cell){

        CheckBox checkBox = getHeaderCellCheckBox(cell);
        checkBox.uncheck();
        return this;
    }

    /**
     * Uncheck the checkbox in the header's cell, by the given cell index
     * @param index the cell's index
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table uncheckHeaderCell(int index)
    {
        CheckBox checkBox = getHeaderCellCheckBox(index);
        checkBox.uncheck();
        return this;
    }

    /**
     * Uncheck the checkbox in the header's cell, by the given header name
     * @param headerName the cell header text
     * @return the table
     * @author genosar.dafna
     * @since 30.07.223
     */
    public Table uncheckHeaderCell(String headerName)
    {
        CheckBox checkBox = getHeaderCellCheckBox(headerName);
        checkBox.uncheck();
        return this;
    }
}
