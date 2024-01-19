package net.blossom.data.map;

import net.blossom.data.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

final class ImmutableDataMap implements DataMap{

    private final Map<DataType<?>, Object> data;

    ImmutableDataMap(Map<DataType<?>, Object> data) {
        this.data = Map.copyOf(data);
    }

    @Override
    public <T> @Nullable T get(@NotNull DataType<T> key, @Nullable T defaultValue) {
        T value = get(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public <T> @Nullable T get(@NotNull DataType<T> key) {
        return (T) data.get(key);
    }

    @Override
    public <T> void put(@NotNull DataType<T> key, @Nullable T value) {
        throw new UnsupportedOperationException("Cannot modify immutable data map");
    }

    @Override
    public <T> void remove(@NotNull DataType<T> key) {
        throw new UnsupportedOperationException("Cannot modify immutable data map");
    }

    @Override
    public boolean contains(@NotNull DataType<?> key) {
        return data.containsKey(key);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot modify immutable data map");
    }

    @Override
    public int size() {
        return data.size();
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<DataType<?>, ?>> iterator() {
        return null;
    }
}
