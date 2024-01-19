package net.blossom.utils;

import java.util.function.Consumer;

public final class TickContainer<T> {

    private final int todo;
    private final T context;
    private final Consumer<T> update;
    private int done;

    public TickContainer(final int todo, T context, Consumer<T> update) {
        this.todo = todo;
        this.context = context;
        this.done = 0;
        this.update = update;
    }

    public boolean isDone() {
        return done >= todo;
    }

    public boolean process() {
        done++;
        if (isDone()) {
            done = 0;
            update.accept(context);
            return true;
        }
        return false;
    }

    public int getTodo() {
        return todo;
    }

    public T getContext() {
        return context;
    }

    public Consumer<T> getUpdate() {
        return update;
    }

    public int getDone() {
        return done;
    }
}
