package net.blossom.commons.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * A concurrent map, internally backed by a non-thread-safe map but carefully
 * managed in a matter such that any changes are thread-safe. Internally, the
 * map is split into a {@code read} and a {@code dirty} map. The read map only
 * satisfies read requests, while the dirty map satisfies all other requests.
 *
 * <p>The map is optimized for two common use cases:</p>
 *
 * <ul>
 *     <li>The entry for the given map is only written once but read many
 *         times, as in a cache that only grows.</li>
 *
 *     <li>Heavy concurrent modification of entries for a disjoint set of
 *         keys.</li>
 * </ul>
 *
 * <p>In both cases, this map significantly reduces lock contention compared
 * to a traditional map paired with a read and write lock, along with maps
 * with an exclusive lock (such as using {@link Collections#synchronizedMap(Map)}.</p>
 *
 * <p>Null values are not accepted. Null keys are supported if the backing collection
 * supports them.</p>
 *
 * <p>Based on: https://golang.org/src/sync/map.go</p>
 *
 * @param <K> the key type
 * @param <V> the value type
 * @since 0.1.0
 */
public interface SyncMap<K, V> extends ConcurrentMap<K, V> {
    /**
     * Returns a new sync map, backed by a {@link HashMap}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a sync map
     * @since 0.1.0
     */
    @SuppressWarnings("RedundantTypeArguments")
    static <K, V> @NotNull SyncMap<K, V> hashmap() {
        return of(HashMap<K, ExpungingEntry<V>>::new, 16);
    }

    /**
     * Returns a new sync map, backed by a {@link HashMap} with a provided initial
     * capacity.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param <K> the key type
     * @param <V> the value type
     * @return a sync map
     * @since 0.1.0
     */
    @SuppressWarnings("RedundantTypeArguments")
    static <K, V> @NotNull SyncMap<K, V> hashmap(final int initialCapacity) {
        return of(HashMap<K, ExpungingEntry<V>>::new, initialCapacity);
    }

    /**
     * Returns a new mutable set view of a sync map, backed by a {@link HashMap}.
     *
     * @param <K> the key type
     * @return a mutable set view of a sync map
     * @since 0.1.0
     */
    @SuppressWarnings("RedundantTypeArguments")
    static <K> @NotNull Set<K> hashset() {
        return setOf(HashMap<K, ExpungingEntry<Boolean>>::new, 16);
    }

    /**
     * Returns a new mutable set view of a sync map, backed by a {@link HashMap} with
     * a provided initial capacity.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param <K> the key type
     * @return a mutable set view of a sync map
     * @since 0.1.0
     */
    @SuppressWarnings("RedundantTypeArguments")
    static <K> @NotNull Set<K> hashset(final int initialCapacity) {
        return setOf(HashMap<K, ExpungingEntry<Boolean>>::new, initialCapacity);
    }

    /**
     * Returns a new sync map, backed by the provided {@link Map} implementation
     * with a provided initial capacity.
     *
     * @param function the map creation function
     * @param initialCapacity the map initial capacity
     * @param <K> the key type
     * @param <V> the value type
     * @return a sync map
     * @since 0.2.0
     */
    static <K, V> @NotNull SyncMap<K, V> of(final @NotNull IntFunction<Map<K, ExpungingEntry<V>>> function, final int initialCapacity) {
        return new SyncMapImpl<>(function, initialCapacity);
    }

    /**
     * Returns a new mutable set view of a sync map, backed by the provided
     * {@link Map} implementation with a provided initial capacity.
     *
     * @param function the map creation function
     * @param initialCapacity the map initial capacity
     * @param <K> they key type
     * @return a mutable set view of a sync map
     * @since 0.2.0
     */
    static <K> @NotNull Set<K> setOf(final @NotNull IntFunction<Map<K, ExpungingEntry<Boolean>>> function, final int initialCapacity) {
        return Collections.newSetFromMap(new SyncMapImpl<>(function, initialCapacity));
    }

    /**
     * {@inheritDoc}
     *
     * Iterations over a sync map are thread-safe, and the keys iterated over will not change for a single iteration
     * attempt, however they may not necessarily reflect the state of the map at the time the iterator was created.
     *
     * <p>Performance Note: If entries have been appended to the map, iterating over the entry set will automatically
     * promote them to the read map.</p>
     */
    @Override
    @NotNull Set<Entry<K, V>> entrySet();

    /**
     * {@inheritDoc}
     *
     * This implementation is {@code O(n)} in nature due to the need to check for any expunged entries. Likewise, as
     * with other concurrent collections, the value obtained by this method may be out of date by the time this method
     * returns.
     *
     * @return the size of all the mappings contained in this map
     */
    @Override
    int size();

    /**
     * {@inheritDoc}
     *
     * This method clears the map by resetting the internal state to a state similar to as if a new map had been created.
     * If there are concurrent iterations in progress, they will reflect the state of the map prior to being cleared.
     */
    @Override
    void clear();

    /**
     * The expunging entry the backing map wraps for its values.
     *
     * @param <V> the value type
     * @since 2.0.0
     */
    interface ExpungingEntry<V> {
        /**
         * Returns {@code true} if this entry has a value, that is
         * neither {@code null} or expunged. Otherwise, it returns
         * {@code false}.
         *
         * @return true if a value exists, otherwise false
         * @since 2.0.0
         */
        boolean exists();

        /**
         * Returns the value or {@code null} if it has been expunged.
         *
         * @return the value or null if it is expunged
         * @since 2.0.0
         */
        @Nullable V get();

        /**
         * Returns the value if it exists, otherwise it returns the
         * default value.
         *
         * @param other the default value
         * @return the value if present, otherwise the default value
         * @since 2.0.0
         */
        @NotNull V getOr(final @NotNull V other);

        /**
         * Sets the specified value if a value doesn't already exist,
         * returning an {@link InsertionResult}.
         *
         * @param value the value
         * @return the result entry
         * @since 2.0.0
         */
        @NotNull InsertionResult<V> setIfAbsent(final @NotNull V value);

        /**
         * Computes the specified value if a value doesn't already exist,
         * returning an {@link InsertionResult}.
         *
         * @param key the key
         * @param function the function
         * @param <K> the key type
         * @return the result entry
         * @since 2.0.0
         */
        <K> @NotNull InsertionResult<V> computeIfAbsent(final @Nullable K key, final @NotNull Function<? super K, ? extends V> function);

        /**
         * Computes the specified value if a value already exists, returning
         * an {@link InsertionResult}.
         *
         * @param key the key
         * @param remappingFunction the function
         * @param <K> the key type
         * @return the result entry
         * @since 2.0.0
         */
        <K> @NotNull InsertionResult<V> computeIfPresent(final @Nullable K key, final @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction);

        /**
         * Computes the specified value, returning an {@link InsertionResult}.
         *
         * @param key the key
         * @param remappingFunction the function
         * @param <K> the key type
         * @return the result entry
         * @since 2.0.0
         */
        <K> @NotNull InsertionResult<V> compute(final @Nullable K key, final @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction);

        /**
         * Sets the value.
         *
         * @param value the value
         * @since 2.0.0
         */
        void set(final @NotNull V value);

        /**
         * Replaces the current value if it matches {@code compare}
         * with the new {@code value} and returns {@code true}. Otherwise,
         * it returns {@code false}.
         *
         * @param compare The comparing value
         * @param value The new value
         * @return true if the value was replaced, otherwise false
         * @since 2.0.0
         */
        boolean replace(final @NotNull Object compare, final @Nullable V value);

        /**
         * Clears the value.
         *
         * @return the value
         * @since 2.0.0
         */
        @Nullable V clear();

        /**
         * Attempts to set the value, if the value is not expunged
         * and returns {@code true}. Otherwise, it returns {@code false}.
         *
         * @return true if the value was set, otherwise false
         * @since 2.0.0
         */
        boolean trySet(final @NotNull V value);

        /**
         * Attempts to replace the value, if the value is not expunged or
         * {@code null} and returns the previous value. Otherwise, it returns
         * {@code null}.
         *
         * @param value the value
         * @return the previous value if it exists, otherwise null
         * @since 2.0.0
         */
        @Nullable V tryReplace(final @NotNull V value);

        /**
         * Attempts to expunge the value, if the value is
         * {@code null} and returns {@code true}. Otherwise, it returns
         * {@code false}.
         *
         * @return true if the value was expunged, otherwise false
         * @since 2.0.0
         */
        boolean tryExpunge();

        /**
         * Attempts to unexpunge the value and set it, if the value
         * was expunged and returns {@code true}. Otherwise, it returns
         * {@code false}.
         *
         * @param value the value
         * @return true if the value was unexpunged, otherwise false
         * @since 2.0.0
         */
        boolean tryUnexpungeAndSet(final @NotNull V value);

        /**
         * Attempts to unexpunge the value and compute it, if the value
         * was expunged and returns {@code true}. Otherwise, it returns
         * {@code false}.
         *
         * @param key the key
         * @param function the function
         * @param <K> the key type
         * @return true if the value was unexpunged, otherwise false
         * @since 2.0.0
         */
        <K> boolean tryUnexpungeAndCompute(final @Nullable K key, final @NotNull Function<? super K, ? extends V> function);

        /**
         * Attempts to unexpunge the value and compute it, if the value
         * was expunged and returns {@code true}. Otherwise, it returns
         * {@code false}.
         *
         * @param key the key
         * @param remappingFunction the remapping function
         * @param <K> the key type
         * @return true if the value was unexpunged, otherwise false
         * @since 2.0.0
         */
        <K> boolean tryUnexpungeAndCompute(final @Nullable K key, final @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction);
    }

    /**
     * The insertion result.
     *
     * @param <V> the value type
     * @since 2.0.0
     */
    interface InsertionResult<V> {
        /**
         * The operation result.
         *
         * @return the operation
         * @since 2.0.0
         */
        byte operation();

        /**
         * The previous value.
         *
         * @return the input
         * @since 2.0.0
         */
        @Nullable V previous();

        /**
         * The current value.
         *
         * @return the output
         * @since 2.0.0
         */
        @Nullable V current();
    }
}