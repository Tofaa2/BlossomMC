package net.blossom.region;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class SphericalRegion implements Region{
    private final UUID uuid;
    private final UUID owner;
    private final String name;
    private final Set<UUID> members;
    private final Point center;
    private int radius;
    private Pos spawn;

    public SphericalRegion(UUID uuid, UUID owner, String name, Set<UUID> members, Point center, int radius, Pos spawn) {
        this.uuid = uuid;
        this.name = name;
        this.members = members;
        this.center = center;
        this.radius = radius;
        this.spawn = spawn;
        this.owner = owner;
    }

    @NotNull
    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public @NotNull  UUID getOwner() {
        return owner;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Set<UUID> getMembers() {
        return Set.copyOf(members);
    }

    @Override
    public @NotNull Point getMin() {
        return new Vec(
                center.x() - radius,
                center.y() - radius,
                center.z() - radius
        );
    }

    @Override
    public @NotNull Point getMax() {
        return new Vec(
                center.x() + radius,
                center.y() + radius,
                center.z() + radius
        );
    }

    @Override
    public @NotNull Point getCenter() {
        return center;
    }

    @Override
    public @NotNull Pos getSpawn() {
        return spawn;
    }

    @Override
    public void setSpawn(@NotNull Pos spawn) {
        this.spawn = spawn;
    }

    @Override
    public boolean contains(@NotNull Point point) {
        return center.distance(point) <= radius;
    }

    @Override
    public void expand(int amount, Direction direction) {
        this.radius += amount;
    }

    @Override
    public void teleport(@NotNull Player player) {
        player.teleport(spawn);
        player.sendMessage("<green>You have been teleported to the region <gold>" + name + "<green>.");
    }

    @Override
    public void addMember(@NotNull UUID uuid) {
        members.add(uuid);
    }
}
