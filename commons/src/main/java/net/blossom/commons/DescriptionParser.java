package net.blossom.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DescriptionParser {

    private DescriptionParser() {}

    public static Collection<String> parse(String description) {
        String[] words = description.split(" ");
        List<String> components = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int wordCount = 0;
        for (String word : words) {
            builder.append(word).append(" ");
            if (word.length() > 4) {
                wordCount++;
            }
            if (wordCount == 4) {
                components.add(builder.toString());
                builder.setLength(0);
            }
        }
        if (!builder.isEmpty()) {
            components.add(builder.toString());
        }
        return components;
    }

}
