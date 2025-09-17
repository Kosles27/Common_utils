package enumerations;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that holds the Failures Classifications
 * @author sela.zvika
 *
 * @since 05.07.2023
 */
public enum TestFailureReasonEnum {

    PRODUCT_BUG("Product Bug"),
    AUTOMATION_BUG("Automation Bug"),
    UNCLASSIFIED("Unclassified"),
    SYSTEM_ISSUE("System Issue"),
    PERFORMANCE("Performance"),
    MISSING_DATA("Missing Test Data"),
    OTHER("Other")
    ;

    //declaring private variable for getting values
    private String name;
    private static final Map<String, TestFailureReasonEnum> BY_NAME = new HashMap<>();

    static {
        for (TestFailureReasonEnum e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    // enum constructor
    TestFailureReasonEnum(String name) {
        this.name = name;

    }

    //getter methods
    public String getName() {
        return this.name;
    }



    //Get the Enum by the given name. for example: valueOfName("Product Bug") returns PRODUCT_BUG
    public static TestFailureReasonEnum valueOfName(String name) {
        return BY_NAME.get(name);
    }


}
