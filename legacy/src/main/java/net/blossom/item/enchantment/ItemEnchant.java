package net.blossom.item.enchantment;

import net.kyori.adventure.text.format.TextColor;

public record ItemEnchant(int level, ItemEnchantHeader header) {

    // from 1-4 is blue, from 5-10 is green, the rest are gold
    private static TextColor LOWER_LEVEL_COLOR = TextColor.color(0x8CE0FF); // blue
    private static TextColor HIGHER_LEVEL_COLOR = TextColor.color(0xF2FF62);
    private static TextColor HIGHEST_LEVEL_COLOR = TextColor.color(0xFF449B); // gold

    public TextColor color() {
        if (level <= 4) return LOWER_LEVEL_COLOR;
        if (level <= 10) return HIGHER_LEVEL_COLOR;
        return HIGHEST_LEVEL_COLOR;
    }
}
