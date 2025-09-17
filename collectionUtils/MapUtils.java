package collectionUtils;

import Managers.ReportInstanceManager;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.relevantcodes.extentreports.LogStatus;
import dateTimeUtils.DateTime;
import enumerations.MessageLevel;
import org.apache.commons.collections4.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reportUtils.Report;
import tableUtils.PrettyPrintTable;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static objectsUtils.StringUtils.removeByteOrderMark;
import static reportUtils.Report.reportAndLog;

import java.lang.reflect.Field;

/**
 * Utility methods for Map and its child classes like HashMap, LinkedHashMap
 * @author genosar.dafna
 * @since 16.07.2023
 */
@SuppressWarnings({"unused", "unchecked"})
public class MapUtils {

    private static final Logger logger = LoggerFactory.getLogger(MapUtils.class);

    /**
     * Get the variables of the given object and return a map the holds the variables names and values
     * For example: the given object is Car. Its variables are: String Name and int Model
     * key: "Name"    value: "Kia"
     * key: "Model"    value: 2025
     * @param obj the given object, like Car
     * @return  a map that holds the given object's variables names and values
     * @author genosar.dafna
     * @since 10.08.2025
     */
    public static LinkedHashMap<String, Object> objectsVariablesToMap(Object obj) {

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        try {
            Class<?> clazz = obj.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // Allow access to a protected /private fields
                result.put(field.getName(), field.get(obj));
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing fields", e);
        }
        return result;
    }

    /**
     * Get the map as separated lines with bold keys, to use nicely in a report
     * @param dataToPrint the map
     * @param <K> generic Key
     * @param <V> generic value
     * @return the map as separated lines with bold keys, to use nicely in a report
     * @author genosar.dafna
     * @since 06.06.2022
     * @since 10.07.2025
     *
     */
    public static <K, V, T extends Map<K, V>> String prettyPrintMap(T dataToPrint)
    {
        StringBuilder line = new StringBuilder();
        for (Map.Entry<K,V> entry : dataToPrint.entrySet()) {
            line.append(String.format("<b>%s: </b> %s<br>", entry.getKey().toString(), entry.getValue()));
        }
        return line.substring(0, line.length()-4);
    }

    /**
     * returns a String of the hashmap as "Key=>value","Key=>value"
     * @author zvika.sela
     * @since 20.06.2021
     * @param map - a Map Collection
     * @return String representation of the map
     */
    public static <K, V, T extends Map<K, V>> String getPrettyMap(T map) {

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            sb.append('"');
            sb.append(entry.getKey());
            sb.append('"');
            sb.append("=>").append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();

    }

    /**
     * Convert a list of String arrays to a list of hashmaps
     * The first entry in the list should represent the keys in the map
     * @param entries a list of String arrays, where the first entry in the list should represent the keys in the map
     *                For example:
     *                List in index 0 = [First name, Last name, City] - the keys/ headers
     *                List in index 1 = [Abdullaa, Rahmaninov, Paris]
     *                List in index 0 = [Pinhas, McAdams, Haifa]
     * @return a list of hashmaps
     * @author genosar.dafna
     * @since 29.06.2025
     */
    public static <M extends Map<String, String>, L extends List<M>> L convertListOfStringArraysToListOfHashMaps(List<String[]> entries) {

        LinkedList<LinkedHashMap<String, String>> mapToReturn = new LinkedList<>();

        //Get the headers names from the first entry in the list
        String[] keysNames = entries.get(0);

        for(int i=1; i< entries.size(); i++) {
            String[] currentLine = entries.get(i);
            LinkedHashMap<String, String> currentLineHash = new LinkedHashMap<>();

            for(int c=0; c< currentLine.length; c++)
            {
                String key = removeByteOrderMark(keysNames[c]);
                String value = removeByteOrderMark(currentLine[c]);
                currentLineHash.put(key, value);
            }
            mapToReturn.add(currentLineHash);
        }
        return (L)mapToReturn;
    }

    /**
     * Convert a list of String arrays to a Hashmap of hashmaps
     * @param entries a list of String arrays, where the first entry in the list should represent the keys in the map
     *                For example:
     *                List in index 0 = [First name, Last name, City] - the keys/ headers
     *                List in index 1 = [Abdullaa, Rahmaninov, Paris]
     *                List in index 0 = [Pinhas, McAdams, Haifa]
     * @param uniqueKey The unique key name that will be the key of each hashmap
     * @return a Hashmap of hashmaps
     * @author genosar.dafna
     * @since 30.06.2025
     */
    public static <M extends Map<String, String>, MP extends Map<String, M>> MP convertListOfStringArraysToHashMapOfHashMaps(List<String[]> entries, String uniqueKey) {

        Map<String, Map<String, String>> mapToReturn = new HashMap<>();

        String uniqueValue = null;

        //Get the headers names from the first entry in the list
        String[] keysNames = entries.get(0);

        for(int i=1; i< entries.size(); i++) {
            String[] currentLine = entries.get(i);
            Map<String, String> currentLineHash = new HashMap<>();

            for(int c=0; c< currentLine.length; c++)
            {
                String key = removeByteOrderMark(keysNames[c]);
                String value = removeByteOrderMark(currentLine[c]);
                currentLineHash.put(key, value);

                if(key.equals(uniqueKey))
                    uniqueValue = value;
            }
            if(uniqueValue == null)
                Report.reportAndLog("convertListOfStringArraysToHashMapOfHashMaps() - Entry in index %d does not have a value under unique ket '%s'".formatted(i, uniqueKey), MessageLevel.WARN);
            else
                mapToReturn.put(uniqueValue, currentLineHash);
        }
        return (MP)mapToReturn;
    }

    /**
     * Convert a list of hashmaps to a Hashmap of hashmaps
     * @param entries a list of hashmaps
     * @param uniqueKey The unique key name that will be the key of each hashmap
     * @return a Hashmap of hashmaps
     * @author genosar.dafna
     * @since 30.06.2025
     */
    public static <K, V, M extends LinkedHashMap<K, V>, MP extends LinkedHashMap<V, M>> MP convertListOfHashMapsToHashMapOfHashMaps(LinkedList<LinkedHashMap<K, V>> entries, String uniqueKey) {

        LinkedHashMap<V, LinkedHashMap<K, V>> mapToReturn = new LinkedHashMap<>();

        for(LinkedHashMap<K, V> listEntry : entries){
            V uniqueValue = listEntry.get(uniqueKey);
            mapToReturn.put(uniqueValue, listEntry);
        }

        return (MP)mapToReturn;
    }


    /**
     * Remove selected entries from a Map
     * @param MapToClean the Map to remove the entries from
     * @param entriesToRemove a Map of entries to remove
     * @return the new Map without the entries
     * @author Dafna Genosar
     * @since 17.02.2022
     */
    public static <K, V, T extends Map<K, V>> T removeEntriesFromMap(T MapToClean, T entriesToRemove)
    {
        for (Map.Entry<K, V> entryToRemove:  entriesToRemove.entrySet()) {

            K keyToRemove = entryToRemove.getKey();
            V valueToRemove = entryToRemove.getValue();

            if(MapToClean.containsKey(keyToRemove) && MapToClean.get(keyToRemove).equals(valueToRemove))
                MapToClean.remove(entryToRemove.getKey(), entryToRemove.getValue());
        }
        return MapToClean;
    }

    /**
     * Remove selected entries from a map by a value
     * @param mapToClean the map to remove the entries from
     * @param entriesValueToRemove a values by which to search and remove from the map
     * @return the new map without the entries that include the value
     * @author Dafna Genosar
     * @since 02.03.2022
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T removeEntriesFromMapByValue(T mapToClean, V entriesValueToRemove)
    {
        List<V> entriesValuesToRemove = Arrays.asList(entriesValueToRemove);

        return removeEntriesFromMapByValues(mapToClean, entriesValuesToRemove);
    }

    /**
     * Remove selected entries from a Map by a list of values
     * @param mapToClean the Map to remove the entries from
     * @param entriesValuesToRemove a list of values by which to search and remove from the Map
     * @return the new Map without the entries that include these values
     * @author Dafna Genosar
     * @since 02.03.2022
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T removeEntriesFromMapByValues(T mapToClean, List<V> entriesValuesToRemove)
    {
        //Go over the list of values and check if the map has any key with these values
        //If it has, add them to a new hash called entriesToRemove
        Map<K, V> entriesToRemove = new HashMap<>();

        for (V valueToRemove: entriesValuesToRemove) {
            for (Map.Entry<K,V> entry:  mapToClean.entrySet()) {
                V entryValue = entry.getValue();
                K entryKey = entry.getKey();
                boolean match;

                if(entryValue == null)
                    match = (valueToRemove == null);
                else
                    match = entryValue.equals(valueToRemove);

                if(match)
                    entriesToRemove.put(entryKey, entryValue);
            }
        }

        return removeEntriesFromMap(mapToClean, (T)entriesToRemove);
    }

    public static <K, V, M extends Map<K, V>, L extends List<M>> boolean compareListsOfMaps(L mapList_1, L mapList_2, String mapListName1, String mapListName2, MessageLevel messageLevel){

        int match = 0;

        for(int i=0 ; i < mapList_1.size(); i++){
            if(!compareMaps(mapList_1.get(i), mapList_2.get(i), mapListName1, mapListName2, messageLevel))
                match++;
        }
        return match == 0;
    }

    /**
     * The method compares 2 String maps and prints any differences
     * @param map_1 1st hashMap
     * @param map_2 2nd hashMap
     * @param mapName1 an optional, yet advised, parameter to add the name of the map,
     *                     so it will be clear in the report. Like: "DB_HashMap", "Page_Details" etc
     * @param mapName2 an optional, yet advised, parameter to add the name of the map,
     *                     so it will be clear in the report.
     * @param messageLevel messageLevel like ERROR/INFO
     * @return true/false if the maps are equal
     * @author Dafna Genosar
     * @since 11.04.2022
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> boolean compareMaps(T map_1, T map_2, String mapName1, String mapName2, MessageLevel messageLevel)
    {
        double rowWidth = 90;
        String headerStyle = String.format("background-color: #D6EEEE; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);

        boolean match = true;

        //Check if the hashmaps are equal
        if(!map_1.equals(map_2))
        {
            if(map_1.size() == 0)
                reportAndLog(String.format("The Map '%s' has no entries", mapName1), messageLevel);
            else if(map_2.size() == 0)
                reportAndLog(String.format("The Map '%s' has no entries", mapName2), messageLevel);
            else {

                //Different entries values between the maps
                String mapDifferences = getMapDifferencesToPrint(map_1, map_2);

                String reportTitleLine = String.format("Map '%s' and '%s' have the following entries differences: <br>", mapName1, mapName2);
                String tableHeaderLine = PrettyPrintTable.getTableRowToPrint(headerStyle, rowWidth, "", mapName1, mapName2);
                String reportLine = reportTitleLine + tableHeaderLine + mapDifferences;

                reportAndLog(reportLine, messageLevel);
            }

            match = false;
        }
        return match;
    }

    /**
     * Get the differences between the 2 maps as a String to print to the report
     * @param <T> generic type
     * @return a String to print to the report
     * @author genosar.dafna
     * @since 23.03.2022
     * @since 11.06.2025
     */
    private static <K extends Object, V extends Object, T extends Map<K, V>> String getMapDifferencesToPrint(T map_1, T map_2)
    {
        double rowWidth = 90;
        String headerStyle = String.format("background-color: #D6EEEE; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String innerHeaderStyle = String.format("background-color: rgb(233,233,233); width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String innerSeparatorStyle = String.format("background-color: rgb(39,39,39); font-size: 2px; height: 3px; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String rowStyle = String.format("width: %s%%; border: 1px solid black;", rowWidth);

        StringBuilder error = new StringBuilder();

        MapDifference<K, V> mapDifferences = Maps.difference(map_1, map_2);

        if (!mapDifferences.areEqual()) {

            //Get the entries differences
            Map<K, MapDifference.ValueDifference<V>> entriesDiffering = mapDifferences.entriesDiffering();

            for(Map.Entry<K, MapDifference.ValueDifference<V>> entry : entriesDiffering.entrySet())
            {
                String key = entry.getKey().toString();

                //If the entry's value itself is a list
                if((entry.getValue()).leftValue() instanceof ArrayList)
                {
                    List<Map<String, MapDifference.ValueDifference<V>>> left = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().leftValue());
                    List<Map<String, MapDifference.ValueDifference<V>>> right = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().rightValue());

                    error.append(getHashMapListToPrint(rowWidth, key, left, right));
                }
                else if((entry.getValue()).leftValue() instanceof LinkedList){
                    List<Map<String, MapDifference.ValueDifference<V>>> left = ((LinkedList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().leftValue());
                    List<Map<String, MapDifference.ValueDifference<V>>> right = ((LinkedList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().rightValue());

                    error.append(getHashMapListToPrint(rowWidth, key, left, right));
                }
                //Else, if the entry's value is not a list
                else {
                    String value;

                    //If neither of the sides' values are null, change them to Strings and highlight the differences in yellow
                    if(entry.getValue().leftValue() != null && entry.getValue().rightValue() != null)
                    {
                        String leftValue = entry.getValue().leftValue().toString();
                        String rightValue = entry.getValue().rightValue().toString();

                        //Highlight the differences in yellow
                        List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);
                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, highlightedDifferences.get(0), highlightedDifferences.get(1));
                    }
                    //Else, if one of the sides is null, just print it
                    else {
                        Object leftValue = entry.getValue().leftValue() == null? "null" : entry.getValue().leftValue();
                        Object rightValue = entry.getValue().rightValue() == null? "null" : entry.getValue().rightValue();
                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, leftValue, rightValue);
                    }

                    //Add the table row to the final error
                    error.append(value);//PrettyPrintTable.getTableRowToPrint(null, 100, "", key, value);
                }
            }

            //Check if there are extra entries on the left (1st Map)
            Map<K, V> differingOnLeft = mapDifferences.entriesOnlyOnLeft();

            //Add the extras to the final error
            error.append(getEntriesDifferencesErrorForReport(differingOnLeft, "left"));


            //Check if there are extra entries on the right (2nd Map)
            Map<K, V> differingOnRight = mapDifferences.entriesOnlyOnRight();

            //Add the extras to the final error
            error.append(getEntriesDifferencesErrorForReport(differingOnRight, "right"));
        }
        return error.toString();
    }
//    private static <K extends Object, V extends Object, T extends Map<K, V>> String getMapDifferencesToPrint(T map_1, T map_2)
//    {
//        double rowWidth = 90;
//        String headerStyle = String.format("background-color: #D6EEEE; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
//        String innerHeaderStyle = String.format("background-color: rgb(233,233,233); width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
//        String innerSeparatorStyle = String.format("background-color: rgb(39,39,39); font-size: 2px; height: 3px; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
//        String rowStyle = String.format("width: %s%%; border: 1px solid black;", rowWidth);
//
//        String error = "";
//
//        MapDifference<K, V> mapDifferences = Maps.difference(map_1, map_2);
//
//        if (!mapDifferences.areEqual()) {
//
//            //Get the entries differences
//            Map<K, MapDifference.ValueDifference<V>> entriesDiffering = mapDifferences.entriesDiffering();
//
//            for(Map.Entry<K, MapDifference.ValueDifference<V>> entry : entriesDiffering.entrySet())
//            {
//                String key = entry.getKey().toString();
//
//                //If the entry's value itself is a list
//                if((entry.getValue()).leftValue() instanceof ArrayList)
//                {
//                    List<Map<String, MapDifference.ValueDifference<V>>> left = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().leftValue());
//                    List<Map<String, MapDifference.ValueDifference<V>>> right = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().rightValue());
//
//                    error += getHashMapListToPrint(rowWidth, key, left, right);
//                }
//                else if((entry.getValue()).leftValue() instanceof LinkedList){
//                    List<Map<String, MapDifference.ValueDifference<V>>> left = ((LinkedList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().leftValue());
//                    List<Map<String, MapDifference.ValueDifference<V>>> right = ((LinkedList<Map<String, MapDifference.ValueDifference<V>>>)entry.getValue().rightValue());
//
//                    error += getHashMapListToPrint(rowWidth, key, left, right);
//                }
//                //Else, if the entry's value is not a list
//                else {
//                    String value;
//
//                    //If neither of the sides' values are null, change them to Strings and highlight the differences in yellow
//                    if(entry.getValue().leftValue() != null && entry.getValue().rightValue() != null)
//                    {
//                        String leftValue = entry.getValue().leftValue().toString();
//                        String rightValue = entry.getValue().rightValue().toString();
//
//                        //Highlight the differences in yellow
//                        List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);
//                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, highlightedDifferences.get(0), highlightedDifferences.get(1));
//                    }
//                    //Else, if one of the sides is null, just print it
//                    else {
//                        Object leftValue = entry.getValue().leftValue() == null? "null" : entry.getValue().leftValue();
//                        Object rightValue = entry.getValue().rightValue() == null? "null" : entry.getValue().rightValue();
//                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, leftValue, rightValue);
//                    }
//
//                    //Add the table row to the final error
//                    error += value;//PrettyPrintTable.getTableRowToPrint(null, 100, "", key, value);
//                }
//            }
//
//            //Check if there are extra entries on the left (1st Map)
//            Map<K, V> differingOnLeft = mapDifferences.entriesOnlyOnLeft();
//            if(differingOnLeft.size() > 0)
//            {
//                for(Map.Entry<K, V> leftEntry : differingOnLeft.entrySet())
//                {
//                    String key = leftEntry.getKey().toString();
//                    Object entryValue = leftEntry.getValue() == null? "null" : leftEntry.getValue();
//                    String value;
//                    if(entryValue instanceof ArrayList){
//
//                        List<Map<String, MapDifference.ValueDifference<V>>> left = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entryValue);
//                        List<Map<String, MapDifference.ValueDifference<V>>> right = new ArrayList<>();
//                        value = getHashMapListToPrint(rowWidth, key, left, right);
//                    }
//                    else{
//                        List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(null, entryValue.toString());
//                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, highlightedDifferences.get(0), highlightedDifferences.get(1));
//                    }
//
//                    //Add the table row to the final error
//                    error += value;
//                }
//            }
//
//            //Check if there are extra entries on the right (2nd Map)
//            Map<K, V> differingOnRight = mapDifferences.entriesOnlyOnRight();
//            if(differingOnRight.size() > 0)
//            {
//                for(Map.Entry<K, V> rightEntry : differingOnRight.entrySet())
//                {
//                    String key = rightEntry.getKey().toString();
//                    Object entryValue = rightEntry.getValue() == null? "null" : rightEntry.getValue();
//                    String value;
//                    if(entryValue instanceof ArrayList){
//
//                        List<Map<String, MapDifference.ValueDifference<V>>> left = new ArrayList<>();
//                        List<Map<String, MapDifference.ValueDifference<V>>> right = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entryValue);
//                        value = getHashMapListToPrint(rowWidth, key, left, right);
//                    }
//                    else{
//                        List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(entryValue.toString(), null);
//                        value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, highlightedDifferences.get(0), highlightedDifferences.get(1));
//                    }
//                    //Add the table row to the final error
//                    error += value;
//                }
//            }
//        }
//        return error;
//    }

    private static <K, V> String getEntriesDifferencesErrorForReport(Map<K, V> extraEntriesOfOneSide, String sideTheExtraEntriesAreOn){

        double rowWidth = 90;
        String rowStyle = String.format("width: %s%%; border: 1px solid black;", rowWidth);

        StringBuilder errorForReport = new StringBuilder();

        //Check if there are extra entries on the left/right
        if(extraEntriesOfOneSide.size() > 0)
        {
            //Go over each extra entry
            for(Map.Entry<K, V> extraEntry : extraEntriesOfOneSide.entrySet())
            {
                String key = extraEntry.getKey().toString();
                Object entryValue = extraEntry.getValue() == null? "null" : extraEntry.getValue();
                String value;

                //If the extra entry is a list
                if(entryValue instanceof ArrayList){

                    List<Map<String, MapDifference.ValueDifference<V>>> left = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entryValue);
                    List<Map<String, MapDifference.ValueDifference<V>>> right = new ArrayList<>();

                    if(sideTheExtraEntriesAreOn.equalsIgnoreCase("left")){
                        left = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entryValue);
                        right = new ArrayList<>();
                    }
                    else {
                        left = new ArrayList<>();
                        right = ((ArrayList<Map<String, MapDifference.ValueDifference<V>>>)entryValue);
                    }

                    value = getHashMapListToPrint(rowWidth, key, left, right);
                }
                else{
                    List<String> highlightedDifferences;

                    if(sideTheExtraEntriesAreOn.equalsIgnoreCase("left")){
                        highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(entryValue.toString(), null);
                    }
                    else
                        highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(null, entryValue.toString());

                    value = PrettyPrintTable.getTableRowToPrint(rowStyle, rowWidth, key, highlightedDifferences.get(0), highlightedDifferences.get(1));
                }
                //Add the table row to the final error
                errorForReport.append(value);
            }
        }

        return errorForReport.toString();
    }

    /**
     * private method to support the printing of hash lists to the report
     * @return a string to print
     * @author genosar.dafna
     * @since 19.01.2023
     * @since 16.07.2023
     */
    private static <V> String getHashMapListToPrint(double rowWidth, String key, List<Map<String, MapDifference.ValueDifference<V>>> left, List<Map<String, MapDifference.ValueDifference<V>>> right){

        String innerHeaderStyle = String.format("background-color: rgb(233,233,233); width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);
        String innerSeparatorStyle = String.format("background-color: rgb(39,39,39); font-size: 2px; height: 3px; width: %s%%; border: 1px solid black; font-weight: bold;", rowWidth);

        StringBuilder error = new StringBuilder();

        //If the lists are not the same size, just print the sizes of each list
        if(left.size() != right.size())
        {
            //Add the table row to the final error
            error.append(PrettyPrintTable.getTableRowToPrint(innerHeaderStyle, rowWidth, key, "List size is " + left.size(), "List size is " + right.size()));
        }
        else
        {
            for (int i = 0; i < left.size(); i++) {
                Map<String, MapDifference.ValueDifference<V>> leftMap = left.get(i);
                Map<String, MapDifference.ValueDifference<V>> rightMap = right.get(i);

                //Call the method in a recursive way to find the differences between the maps
                String innerMapDifferences = getMapDifferencesToPrint(leftMap, rightMap);

                if (!innerMapDifferences.equals("")) {
                    String innerHeader = PrettyPrintTable.getTableRowToPrint(innerHeaderStyle, rowWidth, key+"_"+i, "", "");
                    String innerEnd = PrettyPrintTable.getTableRowToPrint(innerSeparatorStyle, rowWidth, "", "", "");

                    error.append(innerHeader);          //add the inner header
                    error.append(innerMapDifferences); //add the inner rows
                    error.append(innerEnd);            //add separation line
                }
            }
        }
        return error.toString();
    }

    /**
     * Comparing hashtables or dictionaries for specific key
     * @param A hashtable
     * @param B hashtable
     * @param key that exist in hashtable
     */
    public static void compareDictionariesForKey(Hashtable A, Hashtable B, String key) {
        if (A.containsKey(key) && B.containsKey(key)) {
            if (A.get(key).toString().equals(B.get(key).toString())) {
                logger.info("Compare " + key + " " + A.get(key).toString());
            } else {
                logger.error("Mismatch in comparing " + key + "- Value A: " + A.get(key).toString() + " Value B: " + B.get(key).toString());
            }
        }else{
            logger.error("Compare " + key + " " +  "Key not exist: " + key);
        }
    }

    /**
     * Comparing hashtables or dictionaries for all keys
     * @param dict1 dictionary 1
     * @param dict2 dictionary 2
     */
    public static void compareDictionaries(Hashtable dict1, Hashtable dict2)
    {
        List<String> keys = Collections.list(dict1.keys());
        if(dict1.size()==dict2.size()) {
            for (String key:keys) {
                if((dict1.get(key) != null) && (dict2.get(key) != null)){
                    if(dict1.get(key).equals(dict2.get(key))){
                        logger.info("Compare " + key + " " + dict1.get(key).toString());
                    } else {
                        logger.error("Mismatch in comparing " + key + "- Value A: " + dict1.get(key).toString() + " Value B: " + dict2.get(key).toString());
                    }
                } else {
                    logger.error("key: " + key + " doesn't exist in one of the dictionaries");
                }
            }
        }
        else
        {
            logger.error("dictionaries size is not equal");
        }
    }

    /**
     * compare 2 dictionaries according to the first dictionary keys and values
     * make sure all the values of the keys in dict1 are equal to the values of the second dictionary for the same keys
     * @param dict1 - first dictionary
     * @param dict2 - second dictionary
     * @return true -if all dict1 keys and values are equal to the keys and value on dict2
     * @author - Lior Umflat
     * @since - 2.6.2021
     */
    @Deprecated
    public static boolean isHashtable1SubsetOfHashtable2(Hashtable dict1, Hashtable dict2) {
        List<String> keys = Collections.list(dict1.keys());
        List<Boolean> results = new ArrayList<>();
        for (String key : keys) {
            if ((dict1.get(key) != null) && (dict2.get(key) != null)) {
                if (dict1.get(key).equals(dict2.get(key))) {
                    logger.info("Match in comparing 2 dictionaries with key:  " + key + " and with value: " +  dict1.get(key).toString());
                    ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,"Match in comparing 2 dictionaries with key:  " + key + " and with value: " +  dict1.get(key).toString());
                    results.add(true);
                } else {
                    logger.error("Mismatch in comparing 2 dictionaries with key: " + key + ". Value in dictionary 1: " + dict1.get(key).toString() + " and Value in dictionary 2: " + dict2.get(key).toString());
                    ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,"Mismatch in comparing 2 dictionaries with key: " + key + ". Value in dictionary 1: " + dict1.get(key).toString() + " and Value in dictionary 2: " + dict2.get(key).toString());
                    results.add(false);
                }
            } else {
                logger.error("key: " + key + " doesn't exist in one of the dictionaries");
                ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,"key: " + key + " doesn't exist in one of the dictionaries");
                results.add(false);

            }
        }
        //return false if we had a mismatch with at least one of the values
        return !results.contains(false);

    }

    /**
     * Retrieve map record from list based on value.
     * @param list List of maps
     * @param mapValue value to look for in list.
     * @return map record from list based on value. Null if not found.
     * @author genosar.dafna
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T getMapRecord(List<T> list, String mapValue)
    {
        for (T record : list)
        {
            if (record.containsValue(mapValue))
            {
                return record;
            }
        }

        return null;
    }

    /**
     * Retrieve map record from list based on HashMap of data.
     * @param list List of maps
     * @param rowData map of row data to search
     * @return map record from list based on HashMap of data.
     * @param <T> generic type of value
     * @author genosar.dafna
     * @since 24.04.2023
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T getMapRecord(List<T> list, T rowData){

        for (T entrySet : list) {
            boolean rowFound = true;
            for (Map.Entry<K, V> dataSet: rowData.entrySet()) {
                K key = dataSet.getKey();
                V value = Objects.equals(dataSet.getValue(), "")? null : dataSet.getValue();
                if(!entrySet.containsKey(key)){
                    rowFound = false;
                    break;
                }
                else{
                    if(!Objects.equals(entrySet.get(key), value))
                    {
                        rowFound = false;
                        break;
                    }
                }
            }
            if(rowFound)
                return entrySet;
        }
        return null;
    }

    /**
     * Remove One key-value pair From Map.
     * @param map map to base the search on.
     * @param keyToRemove Key for removal of key-value pair.
     * @return the map, without the specified key-value pair
     * @author Dafna Genosar
     * @since  16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T RemoveOnePairFromMap(T map, K keyToRemove) throws Error
    {
        if (map.containsKey(keyToRemove))
        {
            map.remove(keyToRemove);
        }
        else {
            throw new Error("The Key " + keyToRemove + " was not found in hashmap");
        }
        return map;

    }

    /**
     * split a record in a map and set the splits values in map with keys according to newKeys input
     * @param map - map to split any record value
     * @param keyToSplit - A key to split its value
     * @param splitBy - split value by
     * @param newKeys - new keys to set the values from split according to split values order
     * @return a map with split record and original data
     * @author Yael Rozenfeld
     * @since 11.8.2022
     * @author genosar.dafna
     * @since 16.07.2023
     */
    public static <K, V, T extends Map<K, V>> T splitMapRecord(T map, String keyToSplit, String splitBy, List<String> newKeys){

        Map<K, V> splitRecord = new HashMap<>(map);
        if(splitRecord.containsKey(keyToSplit)){
            List<String> splitValue = Arrays.asList(splitRecord.get(keyToSplit).toString().split(splitBy));
            //set size of loop to the shorter list - splitValue or newKeys, or newKeys size if lists are equals.
            //The optimal state -is that lists size is equal, but if there is a difference between them, use the shorter list
            //Note! in this case(list size isn't equal) some values in the longer list will not be used
            int size = splitValue.size() < newKeys.size() ? splitValue.size() : newKeys.size();
            for (int i=0;i<size;i++){
                splitRecord.put((K)newKeys.get(i), (V)splitValue.get(i));
            }
        }
        return (T)splitRecord;
    }

    /**
     * sort map by values
     * @param mapToSort - instance of map<String,Double> to sort
     * @return instance of map<String,Double> sorted by values
     * @author Yael.Rozenfeld
     * @since 25.12.2022
     * @modifier dafna genosar
     * @since 16.07.2023
     */
    public static <T extends Map<String, Double>> T sortingMapByValue(T mapToSort){

        Comparator<Double> values = (Double obj1, Double obj2)->obj1.compareTo(obj2);

        LinkedHashMap<String, Double> sortedMap = mapToSort.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(values))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return (T)sortedMap;
    }

    /**
     * Check if there are equals values in map,
     * if yes write the equals values and their key to log and report
     * @param map instance of map<String,Double>
     * @return true if there  are equals values in map, otherwise false
     * @author Yael Rozenfeld
     * @since 25.12.2022
     * @author dafna genosar
     * @since 16.07.2023
     */
    public static <T extends Map<String, Double>>boolean areEqualsValuesInMap(T map) {

        Map<String,Double> sortingMap =  sortingMapByValue(map);
        boolean areEqualsValues = false;
        Iterator<Map.Entry<String, Double>> iterator = sortingMap.entrySet().iterator();
        Map.Entry<String,Double> previousEntryMap=null;
        while (iterator.hasNext()){
            Map.Entry<String,Double> newEntryMap = iterator.next();
            //compare current map to previous map
            if(previousEntryMap!= null) {
                if (newEntryMap.getValue().equals(previousEntryMap.getValue())) {
                    reportAndLog(String.format("Equals values found in Map in key %s and key %s. equal value is: %s",previousEntryMap.getKey(),newEntryMap.getKey(),newEntryMap.getValue()), MessageLevel.INFO);
                    areEqualsValues = true;
                }
            }
            //save current map to previous map
            previousEntryMap =newEntryMap;

        }
        return areEqualsValues;
    }

    /**
     * sort List<Map<K,V>> according to a map keys values
     * @param keyToSortBy - the key we want to sort teh values according to
     * @param listOfMapsToSort the list of maps to sort
     * @return the sorted list of maps
     * @author umflat.lior
     * @since 11.4.2023
     * @author dafna genosar
     * @since 28.08.2023
     */
    public static <K, V, T extends Map<K, V>, L extends List<T>> L sortListOfHashMapsAccordingToKey(String keyToSortBy, L listOfMapsToSort){

        //sort the list before comparing them
        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T map1, T map2) {

                //Compare two maps based on the value associated with the key keyToSortBy
                V value1 = map1.get(keyToSortBy);
                V value2 = map2.get(keyToSortBy);

                if(value1 instanceof String)
                    return value1.toString().compareToIgnoreCase(value2.toString());

                if(value1 instanceof DateTime)
                    return ((DateTime)value1).compareTo((DateTime)value2);

                if(value1 instanceof Integer)
                    return Integer.compare((int)value1, (int)value1);

                if(value1 instanceof Double)
                    return Double.compare((int)value1, (int)value1);

                throw new Error(String.format("Please add a support for %s in Common Utils - sortListOfHashMapsAccordingToKey()", value1.getClass().toString()));
            }
        };

        listOfMapsToSort.sort(comparator);

        return listOfMapsToSort;
    }

    /**
     * Sort Map<K ,Map<K, V>> by key
     * @param map the map to sort
     * @return new sorted map
     * @author umflat.lior and Jan.Naor
     * @since 7.6.2023
     * @author dafna genosar, sela.zvika
     * @since 16.07.2023
     */
    public static <N extends Number, K extends Comparable<K>, V extends Map<K, N>, M extends Map<K, V>> M sortMapsByKey(M map){

        //Convert the map to a LinkedHashMap
        LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<>(map);

        // Create a list of entries from the LinkedHashMap
        List<Map.Entry<K, V>> entryList = new ArrayList<>(linkedHashMap.entrySet());
        String s = "";

        // Sort the list based on the keys
        Collections.sort(entryList, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getKey()).compareTo(o2.getKey());
            }
        });

        // Create a new LinkedHashMap for the sorted entries
        LinkedHashMap<K, V> sortedHashMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entryList) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (M)sortedHashMap;
    }

    /**
     * Sort a map by values
     * @param map the map
     * @return sorted map
     * @author genosar.dafna
     * @since 28.08.2023
     */
    public static <M extends Map<String, Comparable<?>>> M sortMapsByValue(Map<String, Comparable<?>> map){

        //Convert the map to a LinkedHashMap
        List<Map.Entry<String, Comparable<?>>> entryList = new ArrayList<>(map.entrySet());

        // Sort the list based on the values
        Collections.sort(entryList, new Comparator<Map.Entry<String, Comparable<?>>>() {
            @Override
            public int compare(Map.Entry<String, Comparable<?>> entry1, Map.Entry<String, Comparable<?>> entry2) {

                //Compare two maps based on the value
                Comparable<?> value1 = entry1.getValue();
                Comparable<?> value2 = entry2.getValue();

                if(value1 instanceof String)
                    return value1.toString().compareToIgnoreCase(value2.toString());

                if(value1 instanceof DateTime)
                    return ((DateTime)value1).compareTo((DateTime)value2);

                if(value1 instanceof Integer)
                    return Integer.compare(((Integer) value1).intValue(), ((Integer)value1).intValue());

                if(value1 instanceof BigInteger){
                    BigInteger bigIntValue1 = new BigInteger(String.valueOf(value1));
                    BigInteger bigIntValue2 = new BigInteger(String.valueOf(value2));
                    return bigIntValue1.compareTo(bigIntValue2);
                }

                if(value1 instanceof Double)
                    return Double.compare(((Double) value1).doubleValue(), ((Double) value1).doubleValue());

                throw new Error(String.format("Please add a support for %s in Common Utils - sortMapsByValue()", value1.getClass().toString()));
            }
        });

        // Create a LinkedHashMap to maintain the order
        Map<String, Comparable<?>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Comparable<?>> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return (M)sortedMap;
    }

    /**
     *  method to check if two HashMaps are equal, excluding a specific key
     * @param map1 - first map to check
     * @param map2 - second map to check
     * @param excludedKey - the key to exclude
     * @param reportAndLog - details of what comparison failed or not
     * @return - true if all values are equal excluding the key that excluded
     * @since 14.08.2023
     * @author abo_saleh.rawand
     */
    public static boolean isMatchingExcludingKey(HashMap<String, Object> map1, HashMap<String, Object> map2, String excludedKey,boolean reportAndLog) {
        if (map1.size() != map2.size()) {
            reportAndLog(String.format("Sizes of maps are not equal ,first map size is:%s , second map size is:%s",map1.size(),map2.size() ), MessageLevel.INFO);
            return false;
        }
        for (String key : map1.keySet()) {
            if (!excludedKey.equals(key) && !map1.get(key).equals(map2.get(key))) {
                if (reportAndLog)
                    reportAndLog(String.format("values of keys %s are not matching : first value is %s, second value is %s",key,map1.get(key),map2.get(key)),MessageLevel.INFO);
                return false;
            }
        }
        return true;
    }

    /**
     * Sort outer HashMap by a specific key in the inner map
     * @param map the HashMap<String,HashMap<String,Double>> to sort
     * @param innerKey the inner key to sort the otter table according too
     * @param ascending true to sert by asc, false to sort by desc
     * @return the sortted HashMap
     * @author umflat.lior and ChatGPT
     * @since 13.9.2023
     */
    public static HashMap<String, HashMap<String, Double>> sortByInnerMapKey(HashMap<String, HashMap<String, Double>> map, String innerKey, boolean ascending) {
        HashMap<String, HashMap<String, Double>> sortedMap = new LinkedHashMap<>();

        // Convert the outer map entries to a list for sorting
        List<Map.Entry<String, HashMap<String, Double>>> outerList = new ArrayList<>(map.entrySet());

        // Sort the list based on the specified key's value in the inner map
        outerList.sort((entry1, entry2) -> {
            HashMap<String, Double> innerMap1 = entry1.getValue();
            HashMap<String, Double> innerMap2 = entry2.getValue();
            Double value1 = innerMap1.getOrDefault(innerKey, 0.0); // Default to 0.0 if key not found
            Double value2 = innerMap2.getOrDefault(innerKey, 0.0); // Default to 0.0 if key not found
            int comparison = Double.compare(value1, value2);
            return ascending ? comparison : -comparison; // Adjust for ascending or descending
        });

        // Populate the sorted map
        for (Map.Entry<String, HashMap<String, Double>> entry : outerList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
