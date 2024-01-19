package net.blossom.utils;

import java.util.Random;

public record FloatRange(float min, float max) {

    public static final FloatRange CHANCE_RANGE = new FloatRange(0.0f, 1.0f);


    private static final Random random = new Random();

    public float roll() {
        return min + random.nextFloat() * (max - min); // random float between min and max
    }

    public boolean isInRange(float value) {
        return value >= min && value <= max;
    }

}
