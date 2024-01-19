package net.blossom.data;

import net.blossom.core.Blossom;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static net.blossom.core.Blossom.newId;

record DataTypeImpl<T>(
        @NotNull NamespaceID key,
        @NotNull Class<T> dataClass,
        @NotNull T defaultValue,
        boolean isWeaponApplicable,
        String icon,
        TextColor color,
        String description
) implements DataType<T> {

    public DataTypeImpl(String key, Class<T> dataClass, T defaultValue, boolean isWeaponApplicable, String icon, TextColor color, String description) {
        this(newId(key), dataClass, defaultValue, isWeaponApplicable, icon, color, description);
    }

    private static final Map<NamespaceID, DataType<?>> DATA_TYPES = new HashMap<>();

    static @NotNull <T> DataType<T> register(@NotNull DataTypeImpl<T> dataType) {
        DATA_TYPES.put(dataType.key(), dataType);
        Blossom.LOGGER.info("Registered data type: " + dataType.key());
        return dataType;
    }



    static @Nullable <T> DataType<T> get(@NotNull NamespaceID key) {
        return (DataType<T>) DATA_TYPES.get(key);
    }

    static @Nullable <T> DataType<T> getFromName(String name) {
        return get(newId(name));
    }


    @Override
    public boolean isWeaponApplicable() {
        return isWeaponApplicable;
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
