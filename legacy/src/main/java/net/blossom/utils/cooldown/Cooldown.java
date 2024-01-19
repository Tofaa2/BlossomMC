package net.blossom.utils.cooldown;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class Cooldown<T> {

    private final Map<T, Long> cooldowns;
    private final long duration;

    public Cooldown(Duration duration) {
        this.duration = duration.toMillis();
        this.cooldowns = new HashMap<>(0);
    }

    public Cooldown(long duration) {
        this.duration = duration;
        this.cooldowns = new HashMap<>(0);
    }

    public boolean isOnCooldown(T key) {
        if (duration == -1) {
            return false;
        }
        return cooldowns.containsKey(key) && cooldowns.get(key) > System.currentTimeMillis();
    }

    public long getRemaining(T key) {
        if (duration == -1) {
            return -1;
        }
        return cooldowns.getOrDefault(key, 0L) - System.currentTimeMillis();
    }

    public void setCooldown(T key) {
        if (duration == -1) {
            return;
        }
        cooldowns.put(key, System.currentTimeMillis() + duration);
    }

    public long getDuration() {
        return duration;
    }

}
