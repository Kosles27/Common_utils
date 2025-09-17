package enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerations for bug severity when writing to log
 */
public enum BugSeverityEnum {

    VERY_HIGH("Very High", "VH"),
    HIGH("High", "H"),
    MEDIUM("Medium", "M"),
    LOW("Low", "L"),
    CRITICAL_VERY_HIGH("Critical Very High", "CVH"),
    CRITICAL_HIGH("Critical High", "CH"),
    CRITICAL_MEDIUM("Critical Medium", "CM"),
    CRITICAL_LOW("Critical Low", "CL"),
    SHOW_STOPPER("Show Stopper", "SS");

    //declaring private variable for getting values
    private String name;
    private String letter;
    private static final Map<String, BugSeverityEnum> BY_NAME = new HashMap<>();
    private static final Map<String, BugSeverityEnum> BY_LETTER = new HashMap<>();

    static {
        for (BugSeverityEnum e : values()) {
            BY_NAME.put(e.name, e);
            BY_LETTER.put(e.letter, e);
        }
    }

    // enum constructor
    BugSeverityEnum(String name, String letter) {
        this.name = name;
        this.letter = letter;
    }

    //getter methods
    public String getName() {
        return this.name;
    }
    public String getLetter() {
        return this.letter;
    }

    //Get the Enum by the given name. for example: valueOfName("Product Bug") returns PRODUCT_BUG
    public static BugSeverityEnum valueOfName(String name) {
        return BY_NAME.get(name);
    }
    public static BugSeverityEnum valueOfLetter(String letter) {
        return BY_LETTER.get(letter);
    }
}
