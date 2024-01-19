package net.blossom.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDataContainer implements DataContainer {

    protected final Map<DataType<?>, Object> data;

    public AbstractDataContainer(boolean concurrent) {
        this.data = concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    public AbstractDataContainer() {
        this(false);
    }

    @Override
    public <T> @NotNull T getData(@NotNull DataType<T> type) {
        if (this.data.containsKey(type)) {
            return (T) this.data.get(type);
        }
        return type.defaultValue();
    }

    @Override
    public <T> @UnknownNullability T getData(@NotNull DataType<T> type, boolean defaultIfNotSet) {
        if (this.data.containsKey(type)) {
            return (T) this.data.get(type);
        }
        if (defaultIfNotSet) {
            return type.defaultValue();
        }
        return null;
    }

    @Override
    public void setData(@NotNull Map<DataType<?>, Object> data) {
        this.data.putAll(data);
    }

    @Override
    public <T> void setData(@NotNull DataType<T> type, @NotNull T data) {
        this.data.put(type, data);
    }

    @Override
    public void defaultData(@NotNull DataType<?> type) {
        this.data.put(type, type.defaultValue());
    }

    @Override
    public void removeData(@NotNull DataType<?> type) {
        this.data.remove(type);
    }

    @Override
    public @NotNull Map<DataType<?>, Object> getDataMapCopy() {
        return Map.copyOf(this.data);
    }
}
