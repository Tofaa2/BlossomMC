package net.blossom.item.properties;

import net.blossom.chat.ChatFeature;
import net.blossom.core.Blossom;
import net.blossom.core.Feature;
import net.kyori.adventure.text.Component;

public enum ItemRarity {

    COMMON("<gradient:#c0c0c0:#ffffff>"), // silver to white
    UNCOMMON("<gradient:#00ff00:#ffffff>"), // green to white
    RARE("<gradient:#0000ff:#ffffff>"), // blue to white
    EPIC("<gradient:#ff00ff:#ffffff>"), // purple to white
    LEGENDARY("<gradient:#ffd700:#ffa500>"), // gold to orange
    MYTHIC("<gradient:#ff0000:#ffa500>"); // red to orange
    ;

    private final String color;

    ItemRarity(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public Component colorize(String input) {
        return Feature.getFeature(ChatFeature.class).createMessage(color + input, false);
    }

    public Component colorize() {
        String fancyName = name().charAt(0) + name().substring(1).toLowerCase();
        return Feature.getFeature(ChatFeature.class).createMessage(color + fancyName, false);
    }
}
