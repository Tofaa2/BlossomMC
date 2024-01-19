package net.blossom.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

/**
 * Represents an object that can store and retrieve {@link DataType}s.
 */
public interface DataContainer {


    @NotNull Map<DataType<?>, Object> DEFAULT_PLAYER_DATA = Map.of(
            DataType.MAX_HEALTH, DataType.MAX_HEALTH.defaultValue(),
            DataType.HEALTH, DataType.HEALTH.defaultValue(),
            DataType.LEVEL, DataType.LEVEL.defaultValue(),
            DataType.EXPERIENCE, DataType.EXPERIENCE.defaultValue()
    );

    /**
     * Gets the value of a data type, or the default value if it is not set.
     * @param type the data type
     * @return the value of the data type
     * @param <T> the data type
     */
    @UnknownNullability <T> T getData(@NotNull DataType<T> type, boolean defaultIfNotSet);

    @NotNull <T> T getData(@NotNull DataType<T> type);

    /**
     * Sets the data type to its appropriate value.
     * @param type the data type
     * @param data the data
     * @param <T> the data type
     */
    <T> void setData(@NotNull DataType<T> type, @NotNull T data);

    void setData(@NotNull Map<DataType<?>, Object> data);

    /**
     * Sets the data type to its default value.
     * @param type the data type
     */
    void defaultData(@NotNull DataType<?> type);

    /**
     * Completely removes a data type from the container.
     * @param type the data type
     */
    void removeData(@NotNull DataType<?> type);

    /**
     * Returns a copy of the data map. Handling casting and conversions is the responsibility of the caller.
     * @return a copy of the data map
     */
    @NotNull Map<DataType<?>, Object> getDataMapCopy();

}
