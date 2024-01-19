package net.blossom.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class ComponentUtils {

    private ComponentUtils() {}

    public static Component normal(String text, TextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static Component normal(String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

}
