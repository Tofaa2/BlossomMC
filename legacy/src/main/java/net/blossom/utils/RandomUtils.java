package net.blossom.utils;

import java.util.Random;

public final class RandomUtils {

    private RandomUtils() {}
    private static final Random JVM_RANDOM = new Random();

    public static int nextInt(int bound) {
        return JVM_RANDOM.nextInt(bound);
    }

    public static int nextInt(int min, int max) {
        return min + nextInt(max - min);
    }

}
