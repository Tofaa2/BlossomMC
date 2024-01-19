package net.blossom.data;

import net.blossom.core.Blossom;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

record DataTypeImpl<T>(@NotNull NamespacedKey key, @NotNull Class<T> dataClass, @NotNull T defaultValue) implements DataType<T> {

    private static final Map<NamespacedKey, DataType<?>> DATA_TYPES = new HashMap<>();
    static @NotNull <T> DataType<T> register(@NotNull String key, @NotNull Class<T> dataClass, @NotNull T defaultValue) {
        var namespace = new NamespacedKey(Blossom.getPlugin(), key);
        var dataType = new DataTypeImpl<>(namespace, dataClass, defaultValue);
        DATA_TYPES.put(namespace, dataType);
        return dataType;
    }

    static @Nullable <T> DataType<T> get(@NotNull NamespacedKey key) {
        return (DataType<T>) DATA_TYPES.get(key);
    }

    static @Nullable <T> DataType<T> getFromName(String name) {
        return get(new NamespacedKey(Blossom.getPlugin(), name));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataType<?> impl) {
            return impl.key().equals(key);
        } else {
            return false;
        }
    }
}
