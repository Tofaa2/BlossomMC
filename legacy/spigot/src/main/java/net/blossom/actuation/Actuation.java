package net.blossom.actuation;

import java.util.List;
import java.util.function.Consumer;

public abstract sealed class Actuation<T> permits BukkitActuation, PacketActuation {

    private List<Consumer<T>> consumers;

    protected Actuation() {

    }

    public void handle(T t) {
        consumers.forEach(consumer -> consumer.accept(t));
    }

    public void register(Consumer<T> consumer) {
        consumers.add(consumer);
    }

}
