package net.blossom.ability;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class AbilityContext {

    static final AbilityContext EMPTY_CONTEXT = new AbilityContext(Map.of());



    private final Map<String, Object> context;

    public AbilityContext(Map<String, Object> context) {
        this.context = context;
    }

    public <T> @Nullable T get(String key) {
        return (T) context.get(key);
    }

    public <T> @NotNull T get(String key, T defaultValue) {
        return (T) context.getOrDefault(key, defaultValue);
    }

}
