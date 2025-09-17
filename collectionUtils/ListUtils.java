package collectionUtils;

import Managers.ReportInstanceManager;
import com.relevantcodes.extentreports.LogStatus;
import enumerations.MessageLevel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tableUtils.PrettyPrintTable;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.sort;
import static reportUtils.Report.reportAndLog;
@SuppressWarnings({"unused", "unchecked"})
public class ListUtils {

    private static final Logger logger = LoggerFactory.getLogger(ListUtils.class);

    static Random rand = new Random();

    /**
     * Return the last item of a list
     * @param list the list
     * @param <T> the type of the list
     * @return the last item of a list
     * @author genosar.dafna
     * @since 31.01.2023
     */
    public static <T> T getLast(List<T> list){
        if (list.isEmpty())
            return null;
        return list.get(list.size()-1);
    }

    /**
     * Return the first item of a list
     * @param list the list
     * @param <T> the type of the list
     * @return the first item of a list
     * @author genosar.dafna
     * @since 31.01.2023
     */
    public static <T> T getFirst(List<T> list){
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

    /**
     * Get the minimum value
     * @param list a list of values
     * @return the minimum value
     * @param <T> values that extends Number object
     * @author genosar.dafna
     * @since 31.08.2025
     */
    public static <T extends Number & Comparable<T>> T getMinimum(List<T> list) {

        BigDecimal min = list.stream()
                .map(v -> new BigDecimal(v.toString()))
                .min(BigDecimal::compareTo)
                .orElseThrow(() -> new IllegalStateException("Unexpected empty list after filtering"));

        return convertBack(min, list.get(0));
    }

    /**
     * Get the maximum value
     * @param list a list of values
     * @return the maximum value
     * @param <T> values that extends Number object
     * @author genosar.dafna
     * @since 31.08.2025
     */
    public static <T extends Number & Comparable<T>> T getMaximum(List<T> list) {
        BigDecimal max = list.stream()
                .map(v -> new BigDecimal(v.toString()))
                .max(BigDecimal::compareTo)
                .orElseThrow(() -> new IllegalStateException("Unexpected empty list after filtering"));

        return convertBack(max, list.get(0));
    }

    /**
     * Get the average value
     * @param list a list of values
     * @return the average value
     * @param <T> values that extends Number object
     * @author genosar.dafna
     * @since 31.08.2025
     */
    public static <T extends Number & Comparable<T>> T getAverage(List<T> list) {
        BigDecimal sum = list.stream()
                .map(v -> new BigDecimal(v.toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avg = sum.divide(BigDecimal.valueOf(list.size()), 10, RoundingMode.HALF_UP);
        return convertBack(avg, list.get(0));
    }

    /**
     * Get the median value
     * @param list a list of values
     * @return the median value
     * @param <T> values that extends Number object
     * @author genosar.dafna
     * @since 31.08.2025
     */
    public static <T extends Number & Comparable<T>> T getMedian(List<T> list) {
        List<BigDecimal> sorted = new ArrayList<>();
        for (T v : list) sorted.add(new BigDecimal(v.toString()));
        sorted.sort(BigDecimal::compareTo);

        BigDecimal median;
        int size = sorted.size();
        if (size % 2 == 1) {
            median = sorted.get(size / 2);
        } else {
            median = sorted.get(size / 2 - 1)
                    .add(sorted.get(size / 2))
                    .divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);
        }
        return convertBack(median, list.get(0));
    }

    private static <T extends Number> T convertBack(BigDecimal value, T sample) {
        if (sample instanceof Integer) return (T) Integer.valueOf(value.intValue());
        if (sample instanceof Long) return (T) Long.valueOf(value.longValue());
        if (sample instanceof Double) return (T) Double.valueOf(value.doubleValue());
        if (sample instanceof Float) return (T) Float.valueOf(value.floatValue());
        if (sample instanceof BigDecimal) return (T) value;
        throw new IllegalArgumentException("Unsupported number type: " + sample.getClass());
    }

    /**
     * Join list's String values to one String that can be used for printing.
     * @param values a list to join
     * @return a String made of all the list's elements
     * @author Dafna Genosar
     * @since 7.12.2021
     * @since 05.12.2022
     */
    public static <T> String joinListValuesToString(List<T> values)
    {
        return Arrays.toString(values.toArray()).replace("[", "").replace("]", "");
    }


    /** count occurrences of a String in a List<Strings>
     *
     * @param list the list of strings
     * @param value the value of string to count its occurrences in the list
     * @return number of occurrences of the string in the list
     * for example list {1,1,3,2,1}, value to count its occurrences 1. method results will be 3
     * @author umflat.lior
     * @since 26.2.2023
     */
    public static int countOccurrencesOfValueInAList(List<String> list, String value) {
        int count = 0;
        for (String item : list) {
            if (item.equals(value)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Join list's String values to one String separated by the selected separator
     * @param values a list to join
     * @param separator required separator between each of the list's elements
     * For example:
     *                  if the list is: 1,2,3 and the separator is ' | '
     *                  the returned String will be: 1 | 2 | 3
     *                  if the list is: 1,2,3 and the separator is ' @ '
     *      *                  the returned String will be: 1 @ 2 @ 3
     * @return a String made of all the list's elements separated by the selected separator
     * @author Dafna Genosar
     * @since 05.12.2022
     */
    public static <T> String joinListValuesToStringWithSeparator(List<T> values, String separator)
    {
        return StringUtils.join(values, separator);
    }

    /** Convert List<String> to lowerCase letters
     *
     * @param list - list to convert
     * @return list in lowercase letters
     *
     * @author abo_saleh.rawand
     * @since 18.8.2022
     */
    public static List<String> convertListToLowerCase(List<String> list){
        List<String> lowerCaseList = new ArrayList<>();
        for(String item:list){
            lowerCaseList.add(item.toLowerCase());
        }
        return lowerCaseList;
    }

    /**
     * Select a random number within a given list's size and return a random item from the list
     * @param list a list
     * @param <T> generic type
     * @return a random item from the list
     * @author Dafna Genosar
     * @since 7.12.2021
     * @since 15.04.2025
     */
    public static <T> T getRandomItemFromList(List<T> list)
    {
        if(list.isEmpty())
            throw new IllegalArgumentException("Cannot return a random item from the list. The list is empty");

        int randomNumber = rand.nextInt(list.size());
        return list.get(randomNumber);
    }

    /**
     * Get a random item from the given list, excluding the given excluded items
     * @param list a list
     * @param excludeItems can be one item or a list of items to exclude
     * @return a random item from the list, excluding the given excluded items
     * @author Dafna Genosar
     * @since 14.04.2025
     */
    public static <T, TT> T getRandomItemFromList(List<T> list, TT excludeItems)
    {
        if(list.isEmpty())
            throw new IllegalArgumentException("Cannot return a random item from the list. The list is empty");

        List<T> excludeItemsList;
        if(!(excludeItems instanceof List))
            excludeItemsList = (List<T>) List.of(excludeItems);
        else
            excludeItemsList = (List<T>)excludeItems;

        List<T> filteredExcludeItemsList = list.stream()
                .filter(item -> !excludeItemsList.contains(item))
                .toList();

        int randomNumber = rand.nextInt(filteredExcludeItemsList.size());
        return list.get(randomNumber);
    }

    /**
     * get random items from the given list
     * @param list a list
     * @param <T> generic type
     * @param numberOfItemsToReturn number of random items to return
     * @return random items from the list
     * @author Dafna Genosar
     * @since 25.02.2024
     */
    public static <T> List<T> getRandomItemsFromList(List<T> list, int numberOfItemsToReturn)
    {
        if(list.isEmpty())
            throw new IllegalArgumentException("Cannot return random items from the list. The list is empty");

        if(numberOfItemsToReturn > list.size())
            throw new IllegalArgumentException("The number of random items to return is higher than the list's size");

        List<T> listCopy = new ArrayList<>(list);

        List<T> randomItemsToReturn = new ArrayList<>();

        Random rand = new Random();
        int counter = 0;
        while(counter < numberOfItemsToReturn) {

            int randomNumber = rand.nextInt(listCopy.size());
            T randomItem = listCopy.get(randomNumber);
            randomItemsToReturn.add(randomItem);

            //remove the item from the copyList
            listCopy = removeItemFromList(listCopy, randomItem);
            counter++;
        }
        return randomItemsToReturn;
    }

    /**
     * get random items from the given list, excluding the given items
     * @param list a list
     * @param numberOfItemsToReturn number of random items to return
     * @param excludeItems can be one item or a list of items to exclude
     * @return random items from the list, excluding the given items
     * @author Dafna Genosar
     * @since 14.04.2025
     */
    public static <T, TT> List<T> getRandomItemsFromList(List<T> list, int numberOfItemsToReturn, TT excludeItems)
    {
        if(list == null || list.size() == 0)
            throw new Error("Cannot return random items from the list. The list is empty");

        if(numberOfItemsToReturn > list.size())
            throw new Error("The number of random items to return is higher than the list's size");

        List<T> excludeItemsList;
        if(!(excludeItems instanceof List))
            excludeItemsList = (List<T>) List.of(excludeItems);
        else
            excludeItemsList = (List<T>)excludeItems;

        List<T> filteredExcludeItemsList = list.stream()
                .filter(item -> !excludeItemsList.contains(item))
                .collect(Collectors.toList());

        return getRandomItemsFromList(filteredExcludeItemsList, numberOfItemsToReturn);
    }

    /**
     * Sort 2 lists and compare them for equality.
     * @param list1 1st list to compare
     * @param list2 2nd list to compare
     * @param <T> generic comparable type
     * @return Returns true if and only if the specified object is also a list, both lists have the same size,
     * and all corresponding pairs of elements in the two lists are equal.
     * @author Dafna Genosar
     * @since 6.12.2021
     * @author Dafna Genosar
     * @since 25.02.2024
     */
    public static <T extends Comparable<? super T>> boolean sortAndCompareLists(List<T> list1, List<T> list2)
    {
        sort(list1);
        sort(list2);
        boolean areListsEqual = list1.equals(list2);

        if(!areListsEqual)
        {
            List<T> differences = getListsDisjunction(list1, list2);
            reportAndLog(
                    String.format("""
                                    The lists are different.\s
                                    <b>1st list:</b> %s\s
                                    <b>2nd list:</b> %s\s
                                    <b>Differences:</b> <mark>%s</mark>""",
                            Arrays.toString(list1.toArray()),
                            Arrays.toString(list2.toArray()),
                            Arrays.toString(differences.toArray())),
                    MessageLevel.INFO);
        }

        return areListsEqual;
    }

    /**
     * Sort 2 lists and compare them for equality. Displays the differences as a table
     * @param list1 1st list to compare
     * @param list2 2nd list to compare
     * @param listName1 1st list name
     * @param listName2 2nd list name
     * @param <T> generic comparable type
     * @return Returns true if and only if the specified object is also a list, both lists have the same size,
     * and all corresponding pairs of elements in the two lists are equal.
     * @author Dafna Genosar
     * @since 6.12.2021
     * @author Dafna Genosar
     * @since 29.08.2023
     */
    public static <T extends Comparable<? super T>> boolean sortAndCompareLists(List<T> list1, List<T> list2, String listName1, String listName2, MessageLevel messageLevel)
    {
        sort(list1);
        sort(list2);

        return compareLists(list1, list2, true, listName1, listName2, messageLevel);
    }

    /**
     * Compare 2 lists and write the differences to the report
     * @param list1 list1
     * @param list2 list2
     * @param reportDifferences true if to log the differences to the report / false otherwise
     * @param listName1 list1 name or null
     * @param listName2 list2 name or null
     * @param messageLevel messageLevel
     * @return true if the lists are equal or false otherwise
     * @author genosar.dafna
     * @since 29.08.2023
     */
    public static <T> boolean compareLists(List<T> list1, List<T> list2, boolean reportDifferences, @Nullable String listName1, @Nullable String listName2, MessageLevel messageLevel)
    {
        boolean areListsEqual = list1.equals(list2);

        if(!areListsEqual)
        {
            listName1 = (listName1 == null)? "List 1" : listName1;
            listName2 = (listName2 == null)? "List 2" : listName2;

            //Different entries values between the lists
            String errorLine = getListsDifferingToPrint(list1, list2);

            reportAndLog(String.format(
                    "Lists '%s' and '%s' have the following entries differences: <br>" +
                            "<table style=\"border: 1px solid black;\">" +
                            "<tr style=\"border: 1px solid black;\">" +
                            "<th style=\"background-color: #D6EEEE; border: 1px solid black; width: 30%%\">%s</th>" +
                            "<th style=\"background-color: #D6EEEE; border: 1px solid black; width: 30%%\">%s</th>" +
                            "</tr>" +
                            "%s" +
                            "</table>", listName1, listName2, listName1, listName2, errorLine), messageLevel);
        }

        return areListsEqual;
    }

    public static <T> boolean printListsDifferences(List<T> list1, List<T> list2, HashMap<Integer, List<T>> differences, @Nullable String listName1, @Nullable String listName2, MessageLevel messageLevel)
    {
        boolean areListsEqual = list1.equals(list2);

        if(!areListsEqual)
        {
            listName1 = (listName1 == null)? "List 1" : listName1;
            listName2 = (listName2 == null)? "List 2" : listName2;

            //Different entries values between the lists
            String errorLine = getListsDifferingToPrint(list1, list2, differences);

            reportAndLog(String.format(
                    "Lists '%s' and '%s' have the following entries differences: <br>" +
                            "<table style=\"border: 1px solid black;\">" +
                            "<tr style=\"border: 1px solid black;\">" +
                            "<th style=\"background-color: #D6EEEE; border: 1px solid black; width: 30%%\">%s</th>" +
                            "<th style=\"background-color: #D6EEEE; border: 1px solid black; width: 30%%\">%s</th>" +
                            "</tr>" +
                            "%s" +
                            "</table>", listName1, listName2, listName1, listName2, errorLine), messageLevel);
        }

        return areListsEqual;
    }

    /**
     * Get the differences between the 2 lists as a String to print to the report
     * @param left the left list
     * @param right the right list
     * @param <T> generic type
     * @return a String to print to the report
     * @author genosar.dafna
     * @since 08.06.2022
     * @author genosar.dafna
     * @since 25.02.2024
     */
    public static <T> String getListsDifferingToPrint(List<T> left, List<T> right)
    {
        HashMap<Integer, List<T>> differences = getListsDifferences(left, right);

        String error = "";
        String tableCell = "<td style=\"border: 1px solid black;\">%s</td>";
        String tableRow = tableCell + tableCell;

        if(left.size() != right.size())
        {
            String value = String.format(tableRow, left.size() + " entries", right.size() + " entries");

            error += String.format(
                    "<tr style=\"border: 1px solid black;\">" +
                            "%s" +
                            "</tr>", value);
        }

        List<T> leftDifferencesList = differences.get(1);
        List<T> rightDifferencesList = differences.get(2);

        //The different entries on the left
        if(leftDifferencesList != null) {
            for (T leftDifference : leftDifferencesList) {

                String value;
                String rightValue = "";

                if (leftDifference != null) {
                    String leftValue = leftDifference.toString();

                    //get the String differences highlighted in yellow
                    List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);

                    value = String.format(tableRow, highlightedDifferences.get(0), highlightedDifferences.get(1));
                } else {
                    value = String.format(tableRow, leftDifference, rightValue);
                }

                error = String.format(
                        "%s<tr style=\"border: 1px solid black;\">" +
                                "%s" +
                                "</tr>", error, value);
            }
        }

        //The different entries on the right
        if(rightDifferencesList != null) {
            for (T rightDifference : rightDifferencesList) {

                String value;
                String leftValue = "";

                if (rightDifference != null) {
                    String rightValue = rightDifference.toString();

                    //get the String differences highlighted in yellow
                    List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);

                    value = String.format(tableRow, highlightedDifferences.get(0), highlightedDifferences.get(1));
                } else {
                    value = String.format(tableRow, rightDifference, leftValue);
                }

                error = String.format(
                        "%s<tr style=\"border: 1px solid black;\">" +
                                "%s" +
                                "</tr>", error, value);
            }
        }
        return error;
    }

    public static <T> String getListsDifferingToPrint(List<T> left, List<T> right, HashMap<Integer, List<T>> differences)
    {
        String error = "";
        String tableCell = "<td style=\"border: 1px solid black;\">%s</td>";
        String tableRow = tableCell + tableCell;

        if(left.size() != right.size())
        {
            String value = String.format(tableRow, left.size() + " entries", right.size() + " entries");

            error += String.format(
                    "<tr style=\"border: 1px solid black;\">" +
                            "%s" +
                            "</tr>", value);
        }

        List<T> leftDifferencesList = differences.get(1);
        List<T> rightDifferencesList = differences.get(2);

        //The different entries on the left
        for (T leftDifference : leftDifferencesList) {

            String value;
            String rightValue = "";

            if(leftDifference != null )
            {
                String leftValue = leftDifference.toString();

                //get the String differences highlighted in yellow
                List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);

                value = String.format(tableRow, highlightedDifferences.get(0), highlightedDifferences.get(1));
            }
            else {
                value = String.format(tableRow, leftDifference, rightValue);
            }

            error = String.format(
                    "%s<tr style=\"border: 1px solid black;\">" +
                            "%s" +
                            "</tr>", error, value);
        }

        //The different entries on the right
        for (T rightDifference : rightDifferencesList) {

            String value;
            String leftValue = "";

            if(rightDifference != null )
            {
                String rightValue = rightDifference.toString();

                //get the String differences highlighted in yellow
                List<String> highlightedDifferences = PrettyPrintTable.highlightStringsDifferences(leftValue, rightValue);

                value = String.format(tableRow, highlightedDifferences.get(0), highlightedDifferences.get(1));
            }
            else {
                value = String.format(tableRow, rightDifference, leftValue);
            }

            error = String.format(
                    "%s<tr style=\"border: 1px solid black;\">" +
                            "%s" +
                            "</tr>", error, value);
        }
        return error;
    }

    /**
     * Find the differences between 2 lists
     * @param list1 list 1
     * @param list2 list 2
     * @param <T> generic comparable type
     * @return a HashMap of lists of the differences between the 2 lists
     * @author Dafna Genosar
     * @since 08.06.2022
     * @since 06.03.2024
     */
    public static <T> HashMap<Integer, List<T>> getListsDifferences(List<T>list1, List<T>list2)
    {
        HashMap<Integer, List<T>> differences = new HashMap<>();

        // Find elements unique to each list
        List<T> uniqueElementsList1 = new ArrayList<>(list1);
        uniqueElementsList1.removeAll(list2);

        List<T> uniqueElementsList2 = new ArrayList<>(list2);
        uniqueElementsList2.removeAll(list1);

        differences.put(1, uniqueElementsList1);
        differences.put(2, uniqueElementsList2);

        return differences;
    }

    /**
     * Find the differences between 2 lists
     * @param listOne list 1
     * @param listTwo list 2
     * @param <T> generic comparable type
     * @return a list of the differences between the 2 lists
     * @author Dafna Genosar
     * @since 16.12.2021
     * @author Dafna Genosar
     * @since 06.02.2022
     */
    public static <T> List<T> getListsDisjunction(List<T>listOne, List<T>listTwo)
    {
        return new ArrayList<>(CollectionUtils.disjunction(listOne, listTwo));
    }

    /**
     * Find the common entries between 2 lists
     * @param listOne list 1
     * @param listTwo list 2
     * @param <T> generic comparable type
     * @return a list of common entries between 2 lists
     * @author Dafna Genosar
     * @since 08.06.2022
     */
    public static <T> List<T> getListsIntersection(List<T>listOne, List<T>listTwo)
    {
        return new ArrayList<>(CollectionUtils.intersection(listOne, listTwo));
    }

    /**
     * Retrieve a string that contains all hashMaps details in the list
     * @param list a list of hashMaps
     * @return a string that contains all hashMaps details in the list
     * @author Dafna Genosar
     * @since 06.02.2022
     * @since 25.02.2024
     */
    public static String getHashListAsString(List<HashMap<String, String>> list)
    {
        String stringToReturn = "";

        for (HashMap<String, String> item: list) {
            stringToReturn = String.format("%s%s", stringToReturn, item.toString().replace("{", "").replace("}", "") + " | </br>");
        }
        return stringToReturn;
    }

    /**
     * Remove items from a list
     * @param listToRemoveFrom list to remove from
     * @param itemsToRemove a list of items to remove
     * @author genosar.dafna
     * @since 25.02.2024
     */
    public static <T> List<T> removeItemsFromList(List<T> listToRemoveFrom, List<T> itemsToRemove) {

        // Create a new ArrayList to avoid modifying the original list
        List<T> newList = new ArrayList<>(listToRemoveFrom);

        // Remove items from the new list
        for (T itemToRemove : itemsToRemove) {
            newList.remove(itemToRemove);
        }

        return newList;
    }

    /**
     * Remove items from a list
     * @param listToRemoveFrom list to remove from
     * @param itemsIndexesToRemove an array of items' indexes to remove
     * @author genosar.dafna
     * @since 25.02.2024
     */
    public static <T> List<T> removeItemsFromList(List<T> listToRemoveFrom, Integer[] itemsIndexesToRemove) {

        // Create a new ArrayList to avoid modifying the original list
        List<T> newList = new ArrayList<>(listToRemoveFrom);

        // Remove items from the new list
        for (int itemIndexToRemove : itemsIndexesToRemove) {
            if(itemIndexToRemove >= listToRemoveFrom.size())
                logger.info(String.format("Cannot remove item in index %d from a list with size %s", itemIndexToRemove, listToRemoveFrom.size()));
            else {
                T itemToRemove = listToRemoveFrom.get(itemIndexToRemove);
                newList.remove(itemToRemove);
            }
        }
        return newList;
    }

    /**
     * Remove an item from a list and return the new list
     * @param originalList the list to remove from
     * @param itemToRemove the item to remove
     * @return the new list without the item
     * @param <T> generic type
     * @author genosar.dafna
     * @since 24.02.2024
     */
    public static <T> List<T> removeItemFromList(List<T> originalList, T itemToRemove) {

        //Create a new ArrayList to avoid modifying the original list
        List<T> newList = new ArrayList<>(originalList);

        //Remove the specified item from the new list
        newList.remove(itemToRemove);

        return newList;
    }

    /**
     * Remove an item from a list and return the new list
     * @param originalList the list to remove from
     * @param itemIndexToRemove the index of the item to remove
     * @return the new list without the item
     * @param <T> generic type
     * @author genosar.dafna
     * @since 24.02.2024
     */
    public static <T> List<T> removeItemFromList(List<T> originalList, Integer itemIndexToRemove) {

        //Create a new ArrayList to avoid modifying the original list
        List<T> newList = new ArrayList<>(originalList);
        T itemToRemove = newList.get(itemIndexToRemove);

        //Remove the specified item from the new list
        newList.remove(itemToRemove);

        return newList;
    }

    /**
     * compare between lists values. Be aware: All values are turn to uppercase!!!
     * @param listA List<String>
     * @param listB List<String>
     */
    public static void compareListsValues(List<String> listA,List<String> listB){

        if (listA.size()==listB.size()) {
            for (int i = 0; i < listA.size(); i++) {
                if (listA.get(i).equalsIgnoreCase(listB.get(i))) {
                    logger.info(i + ": " + listA.get(i).toUpperCase() + " " + listB.get(i).toUpperCase());
                } else {
                    logger.error("Mismatch in " + i + ": " + listA.get(i).toUpperCase() + " " + listB.get(i).toUpperCase());
                }
            }
        }else{
            logger.error("Arr size not equal. First size:" + listA.size()+ " Second size:" + listB.size());
        }
    }


    /**
     * compare between lists values without sort, and write the differences to report and log.
     * Be aware: All values are turn to uppercase!!!
     * @param listA List<String>
     * @param listB List<String>
     * @param firstListName String - name of first list. used for log
     * @param secondListName String - name of second list. used for log
     * @return true if the lists are equal, else false
     * @author Yael.Rozenfeld
     * @since 6.11.2022
     * @author genosar.dafna
     * @since 25.02.2024
     */
    public static boolean compareListsValues(List<String> listA, List<String> listB,String firstListName,String secondListName) {
        //create a list that contain the compare results
        List<Boolean> results = new ArrayList<>();
        if (listA.size() == listB.size()) {
            for(int i = 0; i < listA.size(); ++i) {
                if (listA.get(i).equalsIgnoreCase(listB.get(i))) {
                    reportAndLog(i + ": " + (listA.get(i)).toUpperCase() + " " + (listB.get(i)).toUpperCase(),MessageLevel.INFO);
                    results.add(true);
                } else {
                    reportAndLog("Mismatch in " + i + ". "+firstListName+": " + listA.get(i).toUpperCase() + ""+secondListName+": " + listB.get(i).toUpperCase(),MessageLevel.ERROR);
                    results.add(false);
                }
            }
        } else {
            reportAndLog("Arr size not equal. "+firstListName+" size:" + listA.size() + " "+secondListName+" size:" + listB.size(),MessageLevel.ERROR);
            return false;
        }

        //check if one of the items in the results list is false then return false
        for(boolean result:results){
            if(!result)
                return false;
        }
        //if no item is false then return true
        return true;
    }

    /**
     * compare between lists size
     * @param A List<String>
     * @param B List<String>
     */
    public static void compareListsSize(List<String> A,List<String> B){

        if (A.size()==B.size()){
            logger.info( "Arr size equal");
        }else{
            logger.error("Arr size not equal. First size:" + A.size()+ " Second size:" + B.size());
        }

    }

    /**
     * this function verify that all data in list B is not included in list A
     * @param stringListA list of string type
     * @param stringListB list of string type
     * @return true if B not in A, false if B in A
     * @author Naor Jan
     * @since 9.02.2022
     */
    public static boolean verifyAllDataInListANotInListB(List<String> stringListA ,List<String> stringListB ){

        for(int i=0;i<=stringListA.size()-1;i++){
            for(int k=0;k<=stringListB.size()-1;k++){
                if(stringListA.get(i).toUpperCase().trim().equals(stringListB.get(k).toUpperCase().trim())){
                    reportAndLog(String.format("Same data in 1st list and 2nd List : %s",stringListA.get(i)),MessageLevel.INFO);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * this function verify if List<String> is sorted in asc or desc order
     * @param StringList is the list of String to check if their text is order in asc/desc order
     * @param sortOrder is the type of sorting to check  - asc or desc
     * @return true if the list is sorted according to the sortOrder or false if it is not
     */
    public static boolean isStringListSorted(List<String> StringList, String sortOrder)
    {
        /*copy StringList to temp list, sort the temp list according to the sortOrder
        and verify if temp list is equal to StringList */
        List<String> temp = new ArrayList<>(StringList);
        boolean isSorted = false;
        switch (sortOrder) {
            case "asc" -> {
                Collections.sort(temp);
                isSorted = temp.equals(StringList);
            }
            case "desc" -> {
                Collections.sort(temp);
                Collections.reverse(temp);
                isSorted = temp.equals(StringList);
            }
            default -> logger.error("invalid sort order parameter");
        }
        return isSorted;
    }

    /**
     * This function gets a List<String> and returns a concatenated String from all List elements
     * @param lst List<String> ex List<"A","B","C">
     * @return string ex "A B C"
     * @author Jan Naor
     * @since 21.04.2021
     * @author genosar.dafna
     * @since 25.02.2024
     */
    public static String getStringFromAllListElement(List<String> lst) {
        String StringOfAllItems = "";
        for (int i = 0; i <= lst.size() - 1; i++) {
            StringOfAllItems = StringOfAllItems + lst.get(i) + " ";
            StringOfAllItems = String.format("%s%s ", StringOfAllItems, lst.get(i));
        }
        return StringOfAllItems.substring(0,StringOfAllItems.length()-1);

    }

    /**
     * print List<List<String>> to the report
     * @param list - List<List<String>> to print to the report
     * @author Yael.Rozenfeld
     * @since 29.12.2021
     */
    public static void writeListToTheReport(List<List<String>> list){
        for(int i=0;i<list.size();i++) {
            ReportInstanceManager.getCurrentTestReport().log(LogStatus.INFO,  "print list record: " +i,"record: " + i + " values: " + getStringFromAllListElement(list.get(i)));
        }
    }

    /**
     * change all Strings in List<List<String>> to lower case letters
     * @param list - the List<List<String>> that will be modified to lower case letters
     * @return the list after modifying its letters to lower case
     * @author - Lior Umflat
     * @since - 8.8.2022
     */
    public static List<List<String>> changeListOfListOfStringsToLowerCase(List<List<String>> list){
        for (List<String> strings : list) {
            strings.replaceAll(String::toLowerCase);
        }
        return  list;
    }

    /** sort list of string dates by asc/desc order
     *
     * @param dates list of the dates in form dateCurrentFormat
     * @param ascending true if to sort asc. else sort in desc
     * @param dateCurrentFormat the current date format
     */
    public static void sortDates(List<String> dates, boolean ascending,String dateCurrentFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateCurrentFormat);
        dates.sort((date1, date2) -> {
            try {
                Date d1 = formatter.parse(date1);
                Date d2 = formatter.parse(date2);
                return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format", e);
            }
        });
    }

    /**
     * filter list of hashmap by specific key and value
     *
     * @param originalList original list
     * @param targetKey specific key to filter by
     * @param targetValue specific value to filter by
     * @return list of rows with key and value
     * @since 31.07.2025
     */
    public static List<HashMap<String, Object>> filterListOfMapByKeyAndValue(List<HashMap<String, Object>> originalList, Object targetKey, Object targetValue) {
        List<HashMap<String, Object>> filteredList = new ArrayList<>();

        for (HashMap<String, Object> map : originalList) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (targetKey.equals(entry.getKey())) {
                    if (targetValue.equals(entry.getValue()))
                        filteredList.add(map);
                }
            }
        }

        return filteredList;
    }
}
