package net.blossom.commons.animations;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public interface Animatable<T> {

    @NotNull T getCurrent();

    @NotNull T getPrevious();

    @NotNull T getNext();

    void tick();

    /**
     * DOES NOT AUTO_UPDATE, THIS SIMPLY RETURNS WHEN THE ANIMATION SHOULD UPDATE
     * @return the update cycle
     */
    @NotNull Duration getUpdateCycle();

}
