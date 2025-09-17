package objectsUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class NumberUtils {

    static SecureRandom random;

    public static boolean isInRange(Number number, Number min, Number max, boolean includeEquals) {

        if(number instanceof Double) {
            double val = number.doubleValue();
            double lower = min.doubleValue();
            double upper = max.doubleValue();

            if(includeEquals)
                return val >= lower && val <= upper;
            else
                return val > lower && val < upper;
        }
        else if(number instanceof Integer) {
            int val = number.intValue();
            int lower = min.intValue();
            int upper = max.intValue();

            if(includeEquals)
                return val >= lower && val <= upper;
            else
                return val > lower && val < upper;
        }
        else if(number instanceof Float) {
            float val = number.floatValue();
            float lower = min.floatValue();
            float upper = max.floatValue();

            if(includeEquals)
                return val >= lower && val <= upper;
            else
                return val > lower && val < upper;
        }
        else
            throw new IllegalArgumentException("Please add support to isInRange as it does not support %s".formatted(number.getClass()));
    }

    /**
     * Compares two Doubles - supports Null as well
     * @param double_value1 1st Double to be compared.
     * @param double_value2 2nd Double to be compared
     * @return  0 if the values are equal
     *          -1 if the 1st Double is less than the 2nd Double (null is less)
     *          -1 if the 2nd Double is less than the 1st Double
     * @author genosar.dafna
     * @since 18.12.2024
     */
    public static int compareDoubles(Double double_value1, Double double_value2) {

        int valueToReturn;

        boolean double_1_is_Null = double_value1 == null;
        boolean double_2_is_Null = double_value2 == null;

        if (double_1_is_Null && double_2_is_Null) //Both are null
            valueToReturn = 0;

        else if (double_1_is_Null) //only double_value1 is null
            valueToReturn = -1;

        else if (double_2_is_Null) //only double_value2 is null
            valueToReturn = 1;

        else { //Both are not null
            valueToReturn = double_value1.compareTo(double_value2);
        }

        return valueToReturn;
    }

    /**
     * Cut the given total percentage to random parts
     * @param totalPercentage the percentage to cut
     * @param partsCount number of parts to cut the percentage
     * @return a list of the random percentage cuts
     * @author genosar.dafna
     * @since 11.11.2024
     * @since 13.11.2024
     */
    public static List<Integer> dividePercentageToRandomCuts(int totalPercentage, int partsCount) {

        random = new SecureRandom();
        List<Integer> cuts = new ArrayList<>();

        // Generate random cut points
        for (int i = 0; i < partsCount - 1; i++) {
            cuts.add(random.nextInt(totalPercentage));
        }

        // Sort the cuts
        Collections.sort(cuts);

        //Calculate the differences to get the percentages
        List<Integer> parts = new ArrayList<>();
        int previousCut = 0;
        for (int cut : cuts) {
            parts.add(cut - previousCut);
            previousCut = cut;
        }
        //Add the remaining part up to 100%
        parts.add(totalPercentage - previousCut);

        return parts;
    }

    /**
     * Cut the given total percentage to random parts
     * @param totalPercentage the percentage to cut
     * @param roundingMode round option:
     *      * CEILING - Rounds towards positive infinity. Any positive decimal value will be rounded up, and any negative value will be rounded towards zero.
     *      * FLOOR - Rounds towards negative infinity. Positive values are rounded down, while negative values are rounded away from zero.
     *      * UP - Rounds away from zero, regardless of whether the number is positive or negative.
     *      * DOWN - Rounds towards zero, effectively truncating the decimal part.
     *      * HALF_UP - Rounds towards the nearest neighbor. If equidistant, it rounds up. This is the most common rounding method in general arithmetic.
     *      * HALF_DOWN - Also rounds towards the nearest neighbor, but if equidistant, it rounds down.
     *      * HALF_EVEN - Rounds towards the nearest neighbor, but if equidistant, it rounds to the nearest even number. This is also known as "bankers' rounding" and helps minimize rounding errors in repeated calculations.
     *      * UNNECESSARY - This mode does not allow any rounding; it throws an ArithmeticException if rounding would be necessary (e.g., if the result has more decimal places than the scale specified).
     * @return a list of the random percentage cuts
     * @author genosar.dafna
     * @since 17.11.2024
     */
    public static List<Float> dividePercentageToRandomCuts(float totalPercentage, int partsCount, int numberOfDecimalPoints, @Nullable RoundingMode roundingMode) {

        random = new SecureRandom();
        List<Float> cuts = new ArrayList<>();

        if(roundingMode == null)
            roundingMode = RoundingMode.HALF_EVEN;

        float roundedTotalPercentage = roundFloat(totalPercentage, numberOfDecimalPoints, roundingMode);

        // Generate random cut points
        for (int i = 0; i < partsCount - 1; i++) {
            float rand = roundFloat(random.nextFloat(roundedTotalPercentage), numberOfDecimalPoints, roundingMode);
            cuts.add(rand);
        }

        // Sort the cuts
        Collections.sort(cuts);

        //Calculate the differences to get the percentages
        List<Float> parts = new ArrayList<>();
        float previousCut = 0;
        for (Float cut : cuts) {
            float partToAdd = roundFloat(cut - previousCut, numberOfDecimalPoints, roundingMode);
            parts.add(partToAdd);
            previousCut = cut;
        }
        //Add the remaining part up to 100%
        parts.add(roundFloat(roundedTotalPercentage - previousCut, numberOfDecimalPoints, roundingMode));

        return parts;
    }

    /**
     * Cut the given total percentage to random parts
     * @param totalPercentage the percentage to cut
     * @param partsCount number of parts to cut the percentage
     * @param roundingMode round option:
     *      * CEILING - Rounds towards positive infinity. Any positive decimal value will be rounded up, and any negative value will be rounded towards zero.
     *      * FLOOR - Rounds towards negative infinity. Positive values are rounded down, while negative values are rounded away from zero.
     *      * UP - Rounds away from zero, regardless of whether the number is positive or negative.
     *      * DOWN - Rounds towards zero, effectively truncating the decimal part.
     *      * HALF_UP - Rounds towards the nearest neighbor. If equidistant, it rounds up. This is the most common rounding method in general arithmetic.
     *      * HALF_DOWN - Also rounds towards the nearest neighbor, but if equidistant, it rounds down.
     *      * HALF_EVEN - Rounds towards the nearest neighbor, but if equidistant, it rounds to the nearest even number. This is also known as "bankers' rounding" and helps minimize rounding errors in repeated calculations.
     *      * UNNECESSARY - This mode does not allow any rounding; it throws an ArithmeticException if rounding would be necessary (e.g., if the result has more decimal places than the scale specified).

     * @return a list of the random percentage cuts
     * @author genosar.dafna
     * @since 17.11.2024
     */
    public static List<Double> dividePercentageToRandomCuts(double totalPercentage, int partsCount, int numberOfDecimalPoints, @Nullable RoundingMode roundingMode) {

        random = new SecureRandom();
        List<Double> cuts = new ArrayList<>();

        if(roundingMode == null)
            roundingMode = RoundingMode.HALF_EVEN;

        double roundedTotalPercentage = roundDouble(totalPercentage, numberOfDecimalPoints, roundingMode);

        // Generate random cut points
        for (int i = 0; i < partsCount - 1; i++) {
            double rand = roundDouble(random.nextDouble(roundedTotalPercentage), numberOfDecimalPoints,
                    roundingMode);
            cuts.add(rand);
        }

        // Sort the cuts
        Collections.sort(cuts);

        //Calculate the differences to get the percentages
        List<Double> parts = new ArrayList<>();
        double previousCut = 0;
        for (Double cut : cuts) {
            double partToAdd = roundDouble(cut - previousCut, numberOfDecimalPoints, roundingMode);
            parts.add(partToAdd);
            previousCut = cut;
        }
        //Add the remaining part up to 100%
        parts.add(roundDouble(roundedTotalPercentage - previousCut, numberOfDecimalPoints, roundingMode));

        return parts;
    }
    public static List<Double> dividePercentageToRandomCuts(double totalPercentage, int partsCount) {

        random = new SecureRandom();
        List<Double> cuts = new ArrayList<>();

        // Generate random cut points
        for (int i = 0; i < partsCount - 1; i++) {
            cuts.add(random.nextDouble(totalPercentage));
        }

        // Sort the cuts
        Collections.sort(cuts);

        //Calculate the differences to get the percentages
        List<Double> parts = new ArrayList<>();
        double previousCut = 0;
        for (Double cut : cuts) {
            parts.add(cut - previousCut);
            previousCut = cut;
        }
        //Add the remaining part up to 100%
        parts.add(totalPercentage - previousCut);

        return parts;
    }

    /**
     * Round a double number and leave the given number of decimal points
     * Example: roundDouble(0.958478, 2, RoundingMode.CEILING) will round the number up and the result will be 0.96)
     * @param number the double number to round
     * @param numberOfDecimalPoints number of decimal points
     * @param roundingMode round option:
     * CEILING - Rounds towards positive infinity. Any positive decimal value will be rounded up, and any negative value will be rounded towards zero.
     * FLOOR - Rounds towards negative infinity. Positive values are rounded down, while negative values are rounded away from zero.
     * UP - Rounds away from zero, regardless of whether the number is positive or negative.
     * DOWN - Rounds towards zero, effectively truncating the decimal part.
     * HALF_UP - Rounds towards the nearest neighbor. If equidistant, it rounds up. This is the most common rounding method in general arithmetic.
     * HALF_DOWN - Also rounds towards the nearest neighbor, but if equidistant, it rounds down.
     * HALF_EVEN - Rounds towards the nearest neighbor, but if equidistant, it rounds to the nearest even number. This is also known as "bankers' rounding" and helps minimize rounding errors in repeated calculations.
     * UNNECESSARY - This mode does not allow any rounding; it throws an ArithmeticException if rounding would be necessary (e.g., if the result has more decimal places than the scale specified).
     * @return the rounded number
     * @author genosar.dafna
     * @since 13.11.2024
     */
    public static double roundDouble(double number, int numberOfDecimalPoints, RoundingMode roundingMode){
        return (BigDecimal.valueOf(number).setScale(numberOfDecimalPoints, roundingMode)).doubleValue();
    }

    /**
     * Round a float number and leave the given number of decimal points
     * Example: roundDouble(0.958478, 2, RoundingMode.CEILING) will round the number up and the result will be 0.96)
     * @param number the double number to round
     * @param numberOfDecimalPoints number of decimal points
     * @param roundingMode round option:
     * CEILING - Rounds towards positive infinity. Any positive decimal value will be rounded up, and any negative value will be rounded towards zero.
     * FLOOR - Rounds towards negative infinity. Positive values are rounded down, while negative values are rounded away from zero.
     * UP - Rounds away from zero, regardless of whether the number is positive or negative.
     * DOWN - Rounds towards zero, effectively truncating the decimal part.
     * HALF_UP - Rounds towards the nearest neighbor. If equidistant, it rounds up. This is the most common rounding method in general arithmetic.
     * HALF_DOWN - Also rounds towards the nearest neighbor, but if equidistant, it rounds down.
     * HALF_EVEN - Rounds towards the nearest neighbor, but if equidistant, it rounds to the nearest even number. This is also known as "bankers' rounding" and helps minimize rounding errors in repeated calculations.
     * UNNECESSARY - This mode does not allow any rounding; it throws an ArithmeticException if rounding would be necessary (e.g., if the result has more decimal places than the scale specified).
     * @return the rounded number
     * @author genosar.dafna
     * @since 17.11.2024
     */
    public static float roundFloat(float number, int numberOfDecimalPoints, RoundingMode roundingMode){
        return (BigDecimal.valueOf(number).setScale(numberOfDecimalPoints, roundingMode)).floatValue();
    }
}
