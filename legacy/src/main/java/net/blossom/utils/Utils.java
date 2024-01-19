package net.blossom.utils;

import net.minestom.server.coordinate.Pos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Utils {

    private Utils() {}


    public static <T> List<T> fromIterable(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public static int rgbToHex(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    public static int rgbToHex(int[] rgb) {
        return rgbToHex(rgb[0], rgb[1], rgb[2]);
    }

    public static Pos randomizeNearest(Pos position, double maxDistance) {
        return position.add(
                Math.random() * maxDistance * (Math.random() > 0.5 ? 1 : -1),
                Math.random() * maxDistance * (Math.random() > 0.5 ? 1 : -1),
                Math.random() * maxDistance * (Math.random() > 0.5 ? 1 : -1)
        );
    }


    public static float toHundrethDecimal(float input) {
        return Math.round(input * 100) / 100f;
    }
    public static String toRoman(int input) {
        StringBuilder appendable = new StringBuilder();
        int current = input;
        for (RomanNumerals r : Arrays.stream(RomanNumerals.values()).filter(r -> r.getValue() <= input).toList()) {
            int remove = Math.floorDiv(current, r.getValue());
            appendable.append(r.name().repeat(remove));
            current -= remove * r.getValue();
        }
        return appendable.toString();
    }

    public enum RomanNumerals {

        M(1000),
        CM(900),
        D(500),
        CD(400),
        C(100),
        XC(90),
        L(50),
        XL(40),
        X(10),
        IX(9),
        V(5),
        IV(4),
        I(1);


        private final int value;

        RomanNumerals(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


    }

}
