package net.blossom.data.map;

import net.blossom.data.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public interface DataMap extends Iterable<Map.Entry<DataType<?>, ?>> {

    @Nullable <T> T get(@NotNull DataType<T> key, @Nullable T defaultValue);

    @Nullable <T> T get(@NotNull DataType<T> key);

    <T> void put(@NotNull DataType<T> key, @Nullable T value);

    <T> void remove(@NotNull DataType<T> key);

    boolean contains(@NotNull DataType<?> key);

    void clear();

    int size();
}
