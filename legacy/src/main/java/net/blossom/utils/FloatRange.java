package net.blossom.utils;

public record FloatRange(float min, float max) {

    public static final FloatRange EMPTY = new FloatRange(0, 0);

    public float roll() {
        return min + (float) (Math.random() * (max - min));
    }

    public boolean contains(float value) {
        return value >= min && value <= max;
    }

}
