package net.blossom.item;

import net.blossom.core.Blossom;
import net.kyori.adventure.text.Component;

public enum ItemRarity {

    COMMON("<gradient:#c0c0c0:#ffffff>"),
    UNCOMMON("<gradient:#00ff00:#ffffff>"),
    RARE("<gradient:#0000ff:#ffffff>"),
    EPIC("<gradient:#ff00ff:#ffffff>"),
    LEGENDARY("<gradient:#ffd700:#ffa500>"),
    MYTHIC("<rainbow>"),
    ;

    private final String color;

    ItemRarity(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public Component colorize(String input) {
        return Blossom.getChatManager().createMessage(color + input, false);
    }

    public Component colorize() {
        String fancyName = name().substring(0, 1) + name().substring(1).toLowerCase();
        return Blossom.getChatManager().createMessage(color + fancyName, false);
    }
}
