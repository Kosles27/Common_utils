package objectsUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.WordUtils;
import org.apache.commons.text.diff.StringsComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that supports String methods
 */
@SuppressWarnings("unused")
public class StringUtils extends WordUtils{

    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

    /**
     * Get the given double number as a String with the given number of decimal places
     * For example: if the given number is 20.365478 and the decimalPlaces is 2 - the returned String will be "20.36"
     * @param number the double number
     * @param decimalPlaces number of decimal places
     * @return the given double number as a String with the given number of decimal places
     * @author genosar.dafna
     * @since 07.11.2024
     */
    public static String getDoubleStringByDecimalPlaces(double number, int decimalPlaces){
        return String.format("%.2f", number);
    }

    /**
     * Get the given number as a String with the given number of decimal places
     * For example: if the given number is 20.365478 and the decimalPlaces is 2 - the returned String will be "20.36"
     * @param number the number
     * @param decimalPlaces number of decimal places
     * @param roundingMode round option:
     *      * CEILING - Rounds towards positive infinity. Any positive decimal value will be rounded up, and any negative value will be rounded towards zero.
     *      * FLOOR - Rounds towards negative infinity. Positive values are rounded down, while negative values are rounded away from zero.
     *      * UP - Rounds away from zero, regardless of whether the number is positive or negative.
     *      * DOWN - Rounds towards zero, effectively truncating the decimal part.
     *      * HALF_UP - Rounds towards the nearest neighbor. If equidistant, it rounds up. This is the most common rounding method in general arithmetic.
     *      * HALF_DOWN - Also rounds towards the nearest neighbor, but if equidistant, it rounds down.
     *      * HALF_EVEN - Rounds towards the nearest neighbor, but if equidistant, it rounds to the nearest even number. This is also known as "bankers' rounding" and helps minimize rounding errors in repeated calculations.
     *      * UNNECESSARY - This mode does not allow any rounding; it throws an ArithmeticException if rounding would be necessary (e.g., if the result has more decimal places than the scale specified).

     * @return the given number as a String with the given number of decimal places
     * @author genosar.dafna
     * @since 31.08.2025
     */
    public static <T extends Number> String getNumberStringByDecimalPlaces(T number, int decimalPlaces, @Nullable RoundingMode roundingMode) {

        if (number == null) return null;

        if(roundingMode == null)
            roundingMode = RoundingMode.HALF_EVEN;

        BigDecimal bd;

        // Convert different Number types to BigDecimal
        if (number instanceof BigDecimal) {
            bd = (BigDecimal) number;
        }
        else {
            bd = new BigDecimal(number.toString());
        }

        //Round to the desired decimal places
        bd = bd.setScale(decimalPlaces, roundingMode);

        return bd.toPlainString();
    }

    /**
     * Get the longest substring within the given string that has no space
     * @param s the string
     * @return the longest substring within the given string that has no space
     * @author genosar.dafna
     * @since 06.10.2024
     */
    public static String getLongestSubstringWithoutSpace(String s) {
        String result = "";
        StringTokenizer st = new StringTokenizer(s, " ");

        while(st.hasMoreTokens()) {
            String t = st.nextToken();
            if (t.length() > result.length()) {
                result = t;
            }
        }

        return result;
    }

    /**
     * Check if one char is before the other in a giver String
     * @param str the string
     * @param letter1 char 1
     * @param letter2 char 1
     * @return true if the letter 1 is before letter 2, false otherwise
     * @author genosar.dafna
     * @since 27.06.2023
     */
    public static boolean isLetterBefore(String str, char letter1, char letter2) {
        int index1 = str.indexOf(letter1);
        int index2 = str.indexOf(letter2);

        return index1 < index2;
    }

    /**
     * Comparing string by ASCII
     * @param s1 String A
     * @param s2 String B
     * @return True if strings are equals. False if not
     */
    public static boolean compareStringsByAscii(String s1, String s2) {
        int c1=0, c2=0;
        logger.info("compareStringsByAscii -  String 1: " + s1 + " and String 2: " + s2);
        if(s1.length()!=s2.length()) {
            logger.error("compareStringsByAscii -  " + "length of string 1: " + s1.length() + " length of string 2: " + s2.length());
            return false;
        }
        for(int i = 0; i < s1.length();i++ ) {
            c1 = s1.charAt(i);
            c2 = s2.charAt(i);
            if(c1==c2)
            {
                logger.error("compareStringsByAscii (comparing strings " + s1 + " and " + s2 + ") - Chars in index " + (i) + " are not equals" + "Location of char" + i + "ascii 1: " + c1 + "ascii 2: " + c2);
                break;
            }
        }
        return c1==c2;
    }

    /**
     * Algorithm executes comparison of ‘left’ & ‘right’ Strings character by character.
     * If a character is present in ‘left’ String but not in ‘right’, the character will be 'wrapped' in {{ }}
     * If a character is present in ‘right’ String but not in ‘left’, the character will be 'wrapped' in (( ))
     * Example:
     * String leftStr = "A BC E";
     * String rightStr = "ABC D";
     * HashMap.get("left") = A{{ }}BC {{E}}
     *         additional space ↑       ↑ additional E
     * HashMap.get("right") = ABC ((D))
     *                              ↑ additional D
     * @param str1 String 1
     * @param str2 String 2
     * @return hashMap with keys "left" and "right" that represent the differences
     * @author Genosar.dafna
     * @since 11.01.2024
     */
    public static HashMap<String, String> getStringsDifferences(String str1, String str2) {

        char[] str1Arr = str1.toCharArray();
        char[] str2Arr = str2.toCharArray();

        str1 = "";
        for (char c: str1Arr) {
            str1 = String.format("%s%sא", str1, c);
        }

        str2 = "";
        for (char c: str2Arr) {
            str2 = String.format("%s%sא", str2, c);
        }

        StringsComparator comparator = new StringsComparator(str1, str2);
        StringCommandsVisitor stringCommandsVisitor = new StringCommandsVisitor();

        comparator.getScript().visit(stringCommandsVisitor);

        HashMap<String, String> diffs = new HashMap<>();

        diffs.put("left", stringCommandsVisitor.left.replace("{{א}}", "").replace("א", ""));
        diffs.put("right", stringCommandsVisitor.right.replace("((א))", "").replace("א", ""));

        return diffs;
    }

    /**
     * Moved from Collection
     * sort strings values - Ascending Lexicographic order
     * @param values - values for sort
     * @return List<String> with sorted values
     * @author Yael.Rozenfeld
     * @since 06.07.2021
     * @author genosar.dafna
     * @since 18.12.2024
     */
    public static List<String> sortedStringValues(String... values){
        List<String> collection = new ArrayList<>();
        Collections.addAll(collection, values);
        Collections.sort(collection);
        return collection;
    }

    /**
     * Compares two strings - supports Null as well
     * @param string1 1st String to be compared.
     * @param string2 2nd String to be compared
     * @return  0 if the Strings are equal
     *          -1 if the 1st String is less than the 2nd String (null is less)
     *          -1 if the 2nd String is less than the 1st String
     * @author genosar.dafna
     * @since 18.12.2024
     */
    public static int compareStrings(String string1, String string2, boolean caseSensitive) {

        int valueToReturn;

        boolean string_1_is_Null = string1 == null;
        boolean String_2_is_Null = string2 == null;


        if (string_1_is_Null && String_2_is_Null) //If both are null
            valueToReturn = 0;

        else if (string_1_is_Null) //Only String 1 is null
            valueToReturn = -1;

        else if (String_2_is_Null) //Only String 2 is null
            valueToReturn = 1;

        else { //Both are not null

            if (caseSensitive)
                valueToReturn = string1.compareTo(string2);
            else
                valueToReturn = string1.compareToIgnoreCase(string2);
        }
        return valueToReturn;
    }

    /**
     * Compare 2 String length.
     * @param firstString firstString
     * @param secondString secondString
     * @return if first length > second length, will return 1
     *         if first length < second length, will return -1
     *         if first length == second length, will return 0
     * @author genosar.dafna
     * @since 01.05.2022
     * @since 18.12.2024
     */
    public static int compareStringsLength(String firstString, String secondString)
    {
        return Integer.compare(firstString.length(), secondString.length());
    }

    /**
     * Get decoding string and return encoding script(use for password)
     * @param str decoding string
     * @return String encoding
     */
    public static String decodingString(String str) {

        byte[] pwdDecode= Base64.decodeBase64(str);
        return(new String(pwdDecode));

    }

    /**
     * Method to modify string to keep only alpahnumeric chars
     * @param stringToConvert String to convert to alphanumeric
     * @return Alphanumeric string
     * @author rozenfeld.yael
     * @since 10.06.2021
     * @author genosar.dafna
     * @since 18.12.2024
     */
    public static String getStringByRegex(String stringToConvert){

        //regex
        String alphaNumericReg = "([a-zA-Z0-9])+";
        Pattern p = Pattern.compile(alphaNumericReg);
        Matcher m = p.matcher(stringToConvert);
        StringBuilder s = new StringBuilder();

        while(m.find()) {
            logger.info("found reg: " + m.group() + ", in String: " + stringToConvert);
            s.append(m.group());
        }
        return s.toString();
    }

    /**
     * Get string and encoding it
     * @param str encoding string
     */
    public static void encodingString(String str) {
        // how to encode new psd
        byte[] pwdEncode=Base64.encodeBase64(str.getBytes());
        logger.info(new String(pwdEncode));

    }

    /**
     * @param str String
     * @return true if the string is a number / false otherwise
     * @author genosar.dafna
     * @since 28.08.2023
     */
    public static boolean isNumeric(String str){

        try{
            new BigInteger(str);
            return true;
        }
        catch(Exception e){
            return NumberUtils.isCreatable(str);
        }
    }

    /**
     * Capitalize the first letter
     * if capitalizeOnlyFirstWord = true then capitalize only the 1st letter of the first word in the sentence: like "Hello world"
     * if capitalizeOnlyFirstWord = false then capitalize the 1st letter of each word in the sentence: like "Hello World"
     * @param sentence the sentence to edit
     * @param capitalizeOnlyFirstWord true if to only capitalize the first word in the sentence: like "Hello world"
     *                                false if to capitalize the 1st letter of each word in the sentence: like "Hello World"
     * @return the edited sentence
     * @author genosar.dafna
     * @since 16.06.2024
     */
    public static String capitalizeFirstLetter(String sentence, boolean capitalizeOnlyFirstWord) {
        if (sentence == null || sentence.isEmpty()) {
            return sentence;
        }

        if(capitalizeOnlyFirstWord)
            return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);

        StringBuilder capitalizedSentence = new StringBuilder();
        String[] words = sentence.split("\\s+");

        for (String word : words) {
            if (word.length() > 0) {
                capitalizedSentence.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        // Remove the trailing space
        return capitalizedSentence.toString().trim();
    }

    /**
     * Byte Order Mark (BOM). (example: \uFEFF
     * It is a special character used in text files to indicate the encoding and byte order of the file.
     * The BOM is not visible as text but can appear as an invisible or unintended character in text editors or systems that do not expect it.
     * @param content content
     * @return clean String
     * @author genosar.dafna
     * @since 08.12.2024
     */
    public static String removeByteOrderMark(String content) {

        // Check for BOM
        if (content.startsWith("\uFEFF")) {
            return content.substring(1); // Remove the BOM
        }
        return content;
    }
}
