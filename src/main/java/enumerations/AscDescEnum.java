package enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum of Descending and Ascending
 * @author genosar.dafna
 * @since 27.08.2024
 */
@SuppressWarnings("ALL")
public enum AscDescEnum {

    NONE("None"),
    ASC("Ascending"),
    DESC("Descending");

    private static final Map<String, AscDescEnum> BY_MEANING = new HashMap<>();

    static {
        for (AscDescEnum e : values()) {
            BY_MEANING.put(e.meaning, e);
        }
    }

    //declaring private variable for getting values
    private final String meaning;

    // enum constructor
    AscDescEnum(String meaning) {
        this.meaning = meaning;
    }

    //getter method
    public String getMeaning() {
        return this.meaning;
    }

    public static AscDescEnum valueOfMeaning(String meaning) {
        return BY_MEANING.get(meaning);
    }
}

