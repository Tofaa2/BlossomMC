package net.blossom.region;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface Region {

    @NotNull UUID getUuid();

    @NotNull String getName();

    @NotNull Set<UUID> getMembers();

    @NotNull Point getMin();

    @NotNull Point getMax();

    @NotNull Point getCenter();

    @NotNull Pos getSpawn();

    @NotNull UUID getOwner();

    void setSpawn(@NotNull Pos spawn);

    boolean contains(@NotNull Point point);

    void expand(int amount, Direction direction);

    void teleport(@NotNull Player player);

    void addMember(@NotNull UUID uuid);

    default void addMember(@NotNull Player player) {
        addMember(player.getUuid());
    }


}
