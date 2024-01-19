package net.blossom.utils;

import net.blossom.core.Blossom;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DescriptionParser {

    private DescriptionParser() {}


    // Descriptions are limited to 4 words, stuff between < and > is a color code and should not be accounted for as words
    public static Collection<Component> parse(String description) {
        String[] words = description.split(" ");
        List<Component> components = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int wordCount = 0;
        for (String word : words) {
            builder.append(word).append(" ");
            if (word.length() > 4) {
                wordCount++;
            }
            if (wordCount == 4) {
                components.add(Blossom.getChatManager().pure("<gray>" + builder.toString()));
                builder.setLength(0);
            }
        }
        if (!builder.isEmpty()) {
            components.add(Blossom.getChatManager().pure("<gray>" + builder.toString()));
        }
        return components;
    }

}
