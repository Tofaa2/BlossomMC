package net.blossom.utils;

import net.blossom.chat.ChatFeature;
import net.blossom.core.Blossom;
import net.blossom.core.Feature;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DescriptionParser {

    private DescriptionParser() {}


    // Descriptions are limited to 4 words, stuff between < and > is a color code and should not be accounted for as words,
    public static Collection<Component> parse(String description) {
        String[] words = description.split(" ");
        List<Component> components = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int wordCount = 0;
        ChatFeature f = Feature.getFeature(ChatFeature.class);
        for (String word : words) {
            if (word.startsWith("<")) {
                builder.append(word).append(" ");
                continue;
            }
            if (wordCount == 4) {
                components.add(f.pure("<gray>" + builder.toString()));
                builder = new StringBuilder();
                wordCount = 0;
            }
            builder.append(word).append(" ");
            wordCount++;
        }
        if (!builder.isEmpty()) {
            components.add(f.pure("<gray>" + builder.toString()));
        }
        return components;
    }

}
