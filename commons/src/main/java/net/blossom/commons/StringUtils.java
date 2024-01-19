package net.blossom.commons;

public final class StringUtils {

    private StringUtils() {}



    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String fancyName(Enum<?> enumValue) {
        return capitalize(enumValue.name().toLowerCase().replace("_", " "));
    }

    public static String fancyName(String str) {
        return capitalize(str.toLowerCase().replace("_", " "));
    }

}
