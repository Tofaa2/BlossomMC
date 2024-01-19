package net.blossom.commons;

import java.util.List;
import java.util.function.Supplier;

public final class CollectionUtils {

    private CollectionUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static <T> Supplier<T> listToSupplier(List<T> list) {
        return new Supplier<T>() {
            int index = 0;
            @Override
            public T get() {
                if (index >= list.size()) {
                    index = 0;
                }
                return list.get(index++);
            }
        };
    }



}
