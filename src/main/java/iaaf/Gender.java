package iaaf;

public enum Gender {
    MALE, FEMALE, UNKNOWN;

    public static Gender fromString(String name) {
        switch (name.toLowerCase()) {
            case "women":
                return Gender.FEMALE;
            case "men":
                return Gender.MALE;
            default:
                return Gender.valueOf(name);
        }
    }
}
