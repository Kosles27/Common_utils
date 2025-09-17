package tableUtils;

import objectsUtils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.format;

@SuppressWarnings("unused")
public class PrettyPrintTable {

    private static final char BORDER_KNOT = '+';
    private static final char HORIZONTAL_BORDER = '-';
    private static final char VERTICAL_BORDER = '|';
    private static final Logger logger = LoggerFactory.getLogger(PrettyPrintTable.class);
    private static final String DEFAULT_AS_NULL = "(NULL)";


    /**
     * This method pretty prints a List of HashMap<String,String> as if it was a relational DB table
     * @param table  List of Hashmaps table
     * @author zvika.sela
     * @since 02.06.2023
     *
     */
    public static void printTable(List<HashMap<String,String>> table) {
        List<List<String>> resultList = new ArrayList<>();

        // Iterate over each HashMap in the input list
        for (HashMap<String, String> hashMap : table) {
            List<String> innerList = new ArrayList<>();

            // Iterate over each value in the HashMap and add it to the inner list
            for (String value : hashMap.values()) {
                innerList.add(value);
            }

            resultList.add(innerList);
        }
        print(resultList);
    }

    /**
     * This method pretty prints a 2d String array
     * @param table  a bi-dimensional String array
     * @author zvika.sela
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    public static void  print(String[][] table) {
        if ( table == null ) {
            throw new IllegalArgumentException("No tabular data provided");
        }
        if ( table.length == 0 ) {
            return;
        }
        final int[] widths = new int[getMaxColumns(table)];
        adjustColumnWidths(table, widths);
        printPreparedTable(table, widths, getHorizontalBorder(widths));
    }

    /**
     * This method pretty prints List of Lists (List elements types are Strings)
     * @param table  a List<List<String>> we wish to print
     * @author zvika.sela
     * @since 26.04.2021
     */
    public static void print(List<List<String>> table) {


        String[][] array = new String[table.size()][];
        String[] blankArray = new String[0];
        for(int i=0; i < table.size(); i++) {
            array[i] = table.get(i).toArray(blankArray);

        }

        print(array);
    }

    /**
     * inner method for pretty printing
     * @param table  a bi-dimensional String array
     * @param widths  a int array representing the width of each row
     * @param horizontalBorder  aggregated sign for horizontal border
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static void printPreparedTable(String[][] table, int widths[], String horizontalBorder) {
        final int lineLength = horizontalBorder.length();
        logger.debug(horizontalBorder);
        for ( final String[] row : table ) {
            if ( row != null ) {
                logger.debug(getRow(row, widths, lineLength));
                logger.debug(horizontalBorder);
            }
        }
    }

    /**
     * inner method for pretty printing a single row
     * @param row  row from the 2-d array
     * @param widths  a int array representing the width of each row
     * @param lineLength  aggregated sign for horizontal border
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static String getRow(String[] row, int[] widths, int lineLength) {
        final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
        final int maxWidths = widths.length;
        for ( int i = 0; i < maxWidths; i++ ) {
            builder.append(padRight(getCellValue(safeGet(row, i, null)), widths[i])).append(VERTICAL_BORDER);
        }
        return builder.toString();
    }

    /**
     * inner method for pretty printing a border between each cell (and border knot)
     * @param widths  a int array representing the width of each row
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static String getHorizontalBorder(int[] widths) {
        final StringBuilder builder = new StringBuilder(256);
        builder.append(BORDER_KNOT);
        for ( final int w : widths ) {
            for ( int i = 0; i < w; i++ ) {
                builder.append(HORIZONTAL_BORDER);
            }
            builder.append(BORDER_KNOT);
        }
        return builder.toString();
    }

    /**
     * inner method to get the longest row from the table
     * @param rows  the 2-d array we wish to print
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static int getMaxColumns(String[][] rows) {
        int max = 0;
        for ( final String[] row : rows ) {
            if ( row != null && row.length > max ) {
                max = row.length;
            }
        }
        return max;
    }

    /**
     * inner method to adjust the column's width when printing
     * @param rows  the 2-d array we wish to print
     * @param widths  a int array representing the width of each row
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static void adjustColumnWidths(String[][] rows, int[] widths) {
        for ( final String[] row : rows ) {
            if ( row != null ) {
                for ( int c = 0; c < widths.length; c++ ) {
                    final String cv = getCellValue(safeGet(row, c, ""));
                    final int l = cv.length();
                    if ( widths[c] < l ) {
                        widths[c] = l;
                    }
                }
            }
        }
    }

    /**
     * inner method to adjust the column's width when printing
     * @param s  char for padding
     * @param n  amount of padding
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static String padRight(String s, int n) {
        return format("%1$-" + n + "s", s);
    }

    /**
     * inner method to adjust the column's width when printing
     * @param array  row of 2-d array
     * @param index  index to get from
     * @param defaultValue  default value for padding
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static String safeGet(String[] array, int index, String defaultValue) {
        if (index < array.length)
            return array[index];

        return defaultValue;
    }


    /**
     * inner method to adjust the column's width when printing
     * @param value  the value we wish to print
     * @author zvika.sela
     * @since 26.04.2021
     *
     */
    private static String getCellValue(Object value) {
        if (value == null)
            return "(null)";

        return value.toString();
    }

    /**
     * Get a table header row to print
     * @param rowStyle the desired header style. like "background-color: #D6EEEE; width: 100%; border: 1px solid black; font-weight: bold;"
     * @param rowWidth row width
     * @param columnText an array of the columns names. If you want to leave the 1st header empty, enter "" as the 1st column name
     * @return an html text that will print a header row
     * @author genosar.dafna
     * @since 19.01.2023
     * @since 18.01.2024
     */
    public static String getTableRowToPrint(@Nullable String rowStyle, double rowWidth, @Nullable Object... columnText)
    {
        int numberOfColumns = columnText != null? columnText.length : 0;        //Get the number of columns
        double columnWidth = numberOfColumns != 0? rowWidth/numberOfColumns : rowWidth;  //Calculate the width of each column
        String borderedCellStyle = String.format("border-right: 1px solid black; margin-left: 5px; width: %s%%; display: inline-block;", columnWidth);
        String cellStyle = "display: inline-block; margin-left: 5px;";

        //If the 1st column is empty, must put some text in the same color as bg-color is order for the span to display
        String emptyCellFontColor = "color: white;";
        String rowBgColor = "background-color: white;";

        if(rowStyle != null){
            if(rowStyle.contains("background-color:")){
                String[] styleArr = rowStyle.split(";");
                for (String styleParam: styleArr) {
                    if (!styleParam.contains("background-color"))
                        continue;

                    String[] bgColorArr = styleParam.split(": ");
                    String bgColor = bgColorArr[1];
                    emptyCellFontColor = "color: " + bgColor;
                    break;
                }
            }
        }

        rowStyle = (rowStyle == null)? "" : String.format("style=\"%s\"", rowStyle);

        //init columns text (equivalent to <td>)
        String columnsText = "";

        if(columnText != null) {
            for (int i = 0; i < columnText.length; i++) {
                String style = (i != columnText.length - 1) ? borderedCellStyle : cellStyle;
                //if an empty cell - put an x with the same backgrounf color, so it will not display
                if (columnText[i].equals("")) {
                    columnsText = String.format("%s<span style=\"%s %s\">-</span>", columnsText, style, emptyCellFontColor);
                } else {
                    columnsText = String.format("%s<span style=\"%s\">%s</span>", columnsText, style, columnText[i].toString());
                }
            }
        }

        //init row (equivalent to <tr>)

        return String.format(
                    "<div %s>" +        //tr
                        "%s" +          //td
                    "</div>", rowStyle, columnsText);
    }

    /**
     * support method for comparison between 2 values
     * Returns a String of TR table to print
     * @param rowStyle the style string of the row and columns for example:"border: 1px solid black;"
     * @param key - The named of what of being compare like field name etc.
     * @param value  - value to write in the row (Tds contains the values should be printed).
     *               recommend use getTableColsToPrint function to get the value
     * @return a String to print to the report a html row with input value
     * @author genosar.dafna
     * @since 26.06.2022
     */
    public static String getTableRowToPrint(String rowStyle, String leftColStyle, String key, String value)
    {
        return String.format(
                "<div style=\"%s\">" +
                    "<span style=\"%s\">%s</span>" + //first cell that displays the key
                        "%s" +                      //value that holds all the rest of the cells
                "</div>", rowStyle, leftColStyle, key, value);
    }

    /**
     * Get 2 Strings and highlight each difference character
     * @param a first String
     * @param b second String
     * @return the 2 Strings with highlighted different characters
     * @author genosar.dafna
     * @since 01.05.2022
     * @since 08.01.2025
     */
    public static List<String> highlightStringsDifferences(@Nullable String a, @Nullable String b) {

        String newFirstText = "";
        String newSecondText = "";

        if(a == null){
            newFirstText = "<mark>No value</mark>";
            newSecondText = b.equals("") ? "Empty space displays" : b;
        }
        else if(b == null){
            newFirstText = a.equals("") ? "Empty space displays" : a;
            newSecondText = "<mark>No value</mark>";
        }
        else {
            int stringLengthCompare = StringUtils.compareStringsLength(a, b);

            String first;
            String second;
            boolean didVarsSwapOrder = false;
            if (stringLengthCompare < 0) {
                first = a;
                second = b;
            } else {
                first = b;
                second = a;
                didVarsSwapOrder = true;
            }

            for (int i = 0; i < first.length(); i++) {

                char charToCompare1 = first.charAt(i);
                char charToCompare2 = second.charAt(i);
                if (charToCompare1 != charToCompare2) {
                    newFirstText = String.format("%s<mark>%s</mark>", newFirstText, charToCompare1);
                    newSecondText = String.format("%s<mark>%s</mark>", newSecondText, charToCompare2);
                } else {
                    newFirstText = String.format("%s%s", newFirstText, charToCompare1);
                    newSecondText = String.format("%s%s", newSecondText, charToCompare2);
                }
            }
            second = second.substring(first.length());
            for (int i = 0; i < second.length(); i++) {
                if (second.charAt(i) == ' ')
                    newSecondText = String.format("%s<mark>&nbsp;</mark>", newSecondText);
                else
                    newSecondText = String.format("%s<mark>%s</mark>", newSecondText, second.charAt(i));
            }

            if (didVarsSwapOrder)
                return Arrays.asList(newSecondText, newFirstText);
        }

        return Arrays.asList(newFirstText, newSecondText);
    }

    /**
     * Get 2 Strings and highlight each difference character
     * @param str1 first String
     * @param str2 second String
     * @return the 2 Strings with highlighted different characters
     * @author genosar.dafna
     * @since 11.01.2024
     */
    public static List<String> getHighlightedStringsDifferences(String str1, String str2) {

        HashMap<String, String> stringsDifferences = StringUtils.getStringsDifferences(str1, str2);

        String leftDif = stringsDifferences.get("left");
        String rightDif = stringsDifferences.get("right");

        leftDif = leftDif.replace("{{ }}", "<mark>&nbsp;</mark>");
        rightDif = rightDif.replace("(( ))", "<mark>&nbsp;</mark>");

        leftDif = leftDif.replace("{{", "<mark>").replace("}}", "</mark>");
        rightDif = rightDif.replace("((", "<mark>").replace("))", "</mark>");

        return Arrays.asList(leftDif, rightDif);
    }

    /**
     * Get 2 Strings and html highlight each different word
     * @param a first String
     * @param b second String
     * @return the 2 Strings with html highlighted different word
     * @author genosar.dafna
     * @since 08.06.2022
     */
    public static List<String> highlightWordsDifferences(String a, String b) {

        String newFirstText = "";
        String newSecondText = "";

        String[] arrA = a.split(" ");
        String[] arrB = b.split(" ");
        int stringLengthCompare = arrA.length - arrB.length;

        String[] shortArray;
        String[] longArray;
        boolean didVarsSwapOrder = false;
        if(stringLengthCompare < 0)
        {
            shortArray = arrA;
            longArray = arrB;
        }
        else
        {
            shortArray = arrB;
            longArray = arrA;
            didVarsSwapOrder = true;
        }

        List<String> listShort = new ArrayList<>(Arrays.asList(shortArray));
        List<String> listLong = new ArrayList<>(Arrays.asList(longArray));

        List<String> listToRemove = new ArrayList<>();

        for (int i = 0; i < listShort.size(); i++) {

            String strToCompare1 = listShort.get(i);
            String strToCompare2 = listLong.get(i);

            //Is the word is not equal, highlight in yellow
            if (!strToCompare1.equals(strToCompare2)) {
                newFirstText = String.format("%s<mark>%s</mark> ", newFirstText, strToCompare1);
                newSecondText = String.format("%s<mark>%s</mark> ", newSecondText, strToCompare2);
            } else {
                newFirstText = String.format("%s%s ", newFirstText, strToCompare1);
                newSecondText = String.format("%s%s ", newSecondText, strToCompare2);
            }
            listToRemove.add(strToCompare2);
        }

        //Remove the items the times that were checks from the 2 list (the longer list)
        listLong.removeAll(listToRemove);

        for (String word: listLong) {
            newSecondText = String.format("%s<mark>%s</mark> ", newSecondText, word);
        }

        if(didVarsSwapOrder)
            return Arrays.asList(newSecondText, newFirstText);

        return Arrays.asList(newFirstText, newSecondText);
    }
}
