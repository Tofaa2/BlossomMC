package net.blossom.data;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface DataType<T> extends DataTypes permits DataTypeImpl {

    static @Nullable <T> DataType<T> getFromName(String name) {
        return DataTypeImpl.getFromName(name);
    }

    static @Nullable <T> DataType<T> getFromKey(String key) {
        return DataTypeImpl.get(NamespaceID.from(key));
    }

    static @Nullable <T> DataType<T> getFromKey(NamespaceID key) {
        return DataTypeImpl.get(key);
    }

    @NotNull Class<T> dataClass();

    @NotNull T defaultValue();

    @NotNull NamespaceID key();

    @Override
    boolean equals(Object obj);

    boolean isWeaponApplicable();

    @NotNull String icon();

    @NotNull String description();

    @NotNull TextColor color();

}
