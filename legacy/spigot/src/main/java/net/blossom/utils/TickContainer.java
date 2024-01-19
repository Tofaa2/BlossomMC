package net.blossom.utils;

public final class TickContainer {

    private final int todo;
    private int done = 0;

    public TickContainer(final int todo) {
        this.todo = todo;
        this.done = 0;
    }

    public boolean isDone() {
        return done >= todo;
    }

    public boolean process() {
        done++;
        if (isDone()) {
            done = 0;
            return true;
        }
        return false;
    }


}
