package enumerations;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Holds enum utils methods
 * @author genosar.dafna
 * @since 04.06.2023
 */
public class EnumUtils {

    /**
     * Returns true if the given enum has the given value, false otherwise
     * @param enumClass the enum class. like DirectionsEnum.class
     * @param value the value to check if exists
     * @return true if the given enum has the given value, false otherwise
     * @param <T> generic type of enum
     * @author genosar.dafna
     * @since 04.06.2023
     */
    public static <T extends Enum<T>> boolean containsEnumValue(Class<T> enumClass, String value) {
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns true if the given enum has one of the given values, false otherwise
     * @param enumClass the enum class. like DirectionsEnum.class
     * @param values the list of values to search in
     * @return true if the given enum has one of the given values, false otherwise
     * @param <T> generic type of enum
     * @author genosar.dafna
     * @since 04.06.2023
     */
    public static <T extends Enum<T>> boolean containsEnumValueFromList(Class<T> enumClass, List<String> values) {

        for (String value : values) {
            try {
                Enum.valueOf(enumClass, value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Return a list of the enum values
     * @param enumClass the enum class. like DirectionsEnum.class
     * @return a list of the enum values
     * @param <T> generic type of enum
     * @author genosar.dafna
     * @since 04.06.2023
     */
    public static <T extends Enum<T>> List<T> toList(Class<T> enumClass){
        return new ArrayList<>(EnumSet.allOf(enumClass));
    }
}
