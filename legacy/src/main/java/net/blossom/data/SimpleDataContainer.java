package net.blossom.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public interface SimpleDataContainer extends DataContainer {

    @ApiStatus.Internal
    @NotNull Map<DataType<?>, Object> getRawMap();

    @Override
    default <T> @NotNull T getData(@NotNull DataType<T> type) {
        if (this.getRawMap().containsKey(type)) {
            return (T) this.getRawMap().get(type);
        }
        return type.defaultValue();
    }

    @Override
    default <T> @UnknownNullability T getData(@NotNull DataType<T> type, boolean defaultIfNotSet) {
        if (this.getRawMap().containsKey(type)) {
            return (T) this.getRawMap().get(type);
        }
        if (defaultIfNotSet) {
            return type.defaultValue();
        }
        return null;
    }

    @Override
    default void setData(@NotNull Map<DataType<?>, Object> data)  {
       this.getRawMap().putAll(data);
    }


    @Override
    default <T> void setData(@NotNull DataType<T> type, @NotNull T data) {
        this.getRawMap().put(type, data);
    }


    @Override
    default void defaultData(@NotNull DataType<?> type) {
        this.getRawMap().put(type, type.defaultValue());
    }

    @Override
    default void removeData(@NotNull DataType<?> type) {
        this.getRawMap().remove(type);
    }

    @Override
    default @NotNull Map<DataType<?>, Object> getDataMapCopy() {
        return Map.copyOf(this.getRawMap());
    }
}
