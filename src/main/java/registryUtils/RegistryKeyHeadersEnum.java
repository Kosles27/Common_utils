package registryUtils;

/**
 * Enum that holds Registry key data headers as they display in the reg
 * @author genosar.dafna
 * @since 27.06.2023
 */
@SuppressWarnings("ALL")
public enum RegistryKeyHeadersEnum {

    NAME("Name"),
    TYPE("Type"),
    DATA("Data");

    //declaring private variable for getting values
    private final String localValue;

    // enum constructor
    RegistryKeyHeadersEnum(String value) {
        this.localValue = value;
    }

    //getter method
    public String getValue() {
        return this.localValue;
    }
}

