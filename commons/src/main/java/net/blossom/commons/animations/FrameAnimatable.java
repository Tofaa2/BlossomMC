package net.blossom.commons.animations;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class FrameAnimatable<T> implements Animatable<T> {

    private final T[] frames;
    private final int ticksPerFrame;
    private final Duration updateCycle;
    private int currentFrame;
    private int tick;


    public FrameAnimatable(int ticksPerFrame, T... frames) {
        if (frames.length == 0) throw new IllegalArgumentException("frames must not be empty");
        this.frames = frames;
        this.ticksPerFrame = ticksPerFrame;
        this.currentFrame = 0;
        this.tick = 0;
        this.updateCycle = Duration.ofMillis(ticksPerFrame * 50L);
    }

    public FrameAnimatable(T... frames) {
        this(1, frames);
    }

    @Override
    public @NotNull T getCurrent() {
        if (currentFrame >= frames.length) return frames[frames.length - 1];
        return frames[currentFrame];
    }

    @Override
    public @NotNull T getPrevious() {
        if ((currentFrame - 1) < 0) return frames[frames.length - 1];
        return frames[currentFrame - 1];
    }

    @Override
    public @NotNull T getNext() {
        if ((currentFrame + 1) >= frames.length) return frames[0];
        return frames[currentFrame + 1];
    }

    @Override
    public void tick() {
        tick++;
        if (tick >= ticksPerFrame) {
            tick = 0;
            currentFrame++;
            if (currentFrame >= frames.length) currentFrame = 0;
        }
    }

    @NotNull
    @Override
    public Duration getUpdateCycle() {
        return updateCycle;
    }
}
