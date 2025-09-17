package enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that holds the Directions
 * @author genosar.dafna
 * @since 01.01.2023
 * @since 05.02.2023
 */
public enum DirectionsEnum {

    NORTH("North", "N"),
    SOUTH("South", "S"),
    EAST("East", "E"),
    WEST("West", "W");

    //declaring private variable for getting values
    private String name;
    private String letter;
    private static final Map<String, DirectionsEnum> BY_NAME = new HashMap<>();
    private static final Map<String, DirectionsEnum> BY_LETTER = new HashMap<>();

    static {
        for (DirectionsEnum e : values()) {
            BY_NAME.put(e.name, e);
            BY_LETTER.put(e.letter, e);
        }
    }

    // enum constructor
    DirectionsEnum(String name, String letter) {
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

    //Get the Enum by the given name. for example: valueOfName("North") returns NORTH
    public static DirectionsEnum valueOfName(String name) {
        return BY_NAME.get(name);
    }

    //Get the Enum by the given letter. for example: valueOfName("N") returns NORTH
    public static DirectionsEnum valueOfLetter(String letter) {
        return BY_LETTER.get(letter);
    }
}
