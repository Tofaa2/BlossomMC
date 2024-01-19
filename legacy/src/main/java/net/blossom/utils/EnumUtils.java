package net.blossom.utils;

import java.util.Collection;

public final class EnumUtils {

    private EnumUtils() {
    }

    public static String prettyString(String name) {
        StringBuilder builder = new StringBuilder();
        for (String word : name.split("_")) {
            builder.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
        }
        return builder.toString().trim();
    }

    public static String prettyString(Enum<?> value) {
        return prettyString(value.name());
    }

    public static String[] getNames(Class<? extends Enum<?>> clazz) {
        var constants = clazz.getEnumConstants();
        var names = new String[constants.length];
        for (int i = 0; i < constants.length; i++) {
            names[i] = constants[i].name();
        }
        return names;
    }

}
