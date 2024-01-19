package net.blossom.hub.features.cosmetics;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;

public enum CosmeticRarity {

    BASIC(TextColor.color(0xFFFFFF)),
    RARE(TextColor.color(0x55FF55)),
    FANCY(TextColor.color(0x55FFFF)),
    EPIC(TextColor.color(0xAA00FF)),
    LEGENDARY(TextColor.color(0xFFAA00)),
    SPECIAL(TextColor.color(0xFF5555));

    private final TextColor color;

    CosmeticRarity(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }

}
