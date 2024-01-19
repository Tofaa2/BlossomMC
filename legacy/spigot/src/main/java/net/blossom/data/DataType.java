package net.blossom.data;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface DataType<T> extends DataTypes permits DataTypeImpl {

    static @Nullable <T> DataType<T> getFromName(String name) {
        return DataTypeImpl.getFromName(name);
    }

    @NotNull Class<T> dataClass();

    @NotNull T defaultValue();

    @NotNull NamespacedKey key();

    @Override
    boolean equals(Object obj);

}
