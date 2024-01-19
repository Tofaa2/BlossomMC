package net.blossom.entity.mob;

import net.kyori.adventure.util.RGBLike;
import org.jetbrains.annotations.Range;

public enum AggressionType implements RGBLike {

    //#ca0000
    PASSIVE(202, 0, 0),
    //#dec158
    NEUTRAL(222, 193, 88),
    //#84ff22
    HOSTILE(132, 255, 34),
    ;

    private int red;
    private int green;
    private int blue;

    AggressionType(int r, int g, int b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    @Override
    public @Range(from = 0L, to = 255L) int red() {
        return red;
    }

    @Override
    public @Range(from = 0L, to = 255L) int green() {
        return green;
    }

    @Override
    public @Range(from = 0L, to = 255L) int blue() {
        return blue;
    }

    public String toHex() {
        return String.format("#%02x%02x%02x", red, green, blue);
    }

}
