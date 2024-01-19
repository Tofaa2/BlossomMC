package net.blossom.hub.features.parkour;

import net.minestom.server.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public class ParkourMap {

    private static final Comparator<Map.Entry<UUID, Float>> BEST_TIME_COMPARATOR = (a, b) -> {
        if (a.getValue() == b.getValue()) {
            return 0;
        }
        return a.getValue() < b.getValue() ? -1 : 1;
    };
    private final String name;
    private final Map<UUID, Float> bestTimes;
    private final List<ParkourCheckpoint> checkpoints;
    private final Map<UUID, Float> currentlyRunning;

    public ParkourMap(
            String name,
            Map<UUID, Float> bestTimes,
            List<ParkourCheckpoint> checkpoints
    ) {
        this.name = name;
        this.bestTimes = bestTimes;
        this.checkpoints = checkpoints;
        this.currentlyRunning = new HashMap<>();
    }


    public String getName() {
        return name;
    }

    public Map<UUID, Float> getFirstTenBest() {
        return bestTimes.entrySet().stream()
                .sorted(BEST_TIME_COMPARATOR)
                .limit(10)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
    }

    public void removeRunner(UUID uuid) {
        currentlyRunning.remove(uuid);
    }

    public void removeRunner(Player player) {
        removeRunner(player.getUuid());
    }

    public void addRunner(UUID uuid, float time) {
        currentlyRunning.put(uuid, time);
    }

    public void addRunner(Player player, float time) {
        addRunner(player.getUuid(), time);
    }

    public void addRunner(UUID uuid) {
        addRunner(uuid, 0);
    }

    public void addRunner(Player player) {
        addRunner(player.getUuid());
    }

    public void isRunning(UUID uuid) {
        currentlyRunning.containsKey(uuid);
    }

    public void isRunning(Player player) {
        isRunning(player.getUuid());
    }

    public float getRunnerTime(UUID uuid) {
        return currentlyRunning.get(uuid);
    }

    public List<ParkourCheckpoint> getCheckpoints() {
        return List.copyOf(checkpoints);
    }
}
