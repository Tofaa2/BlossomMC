package net.blossom.region;

import com.google.gson.JsonObject;
import net.blossom.utils.JsonUtils;
import net.blossom.utils.SerializableCoordinate;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public final class SimpleRegion implements Region {

    private final UUID uuid;
    private final UUID owner;
    private final String name;
    private final Set<UUID> members;
    private Pos spawn;
    private Vec min, max;
    private Vec center;

    public SimpleRegion(UUID uuid, UUID owner, String name, Set<UUID> members, Pos spawn, Vec min, Vec max) {
        this.uuid = uuid;
        this.name = name;
        this.members = members;
        this.spawn = spawn;
        this.min = min;
        this.max = max;
        this.owner = owner;
        refreshCenter();
    }

    @NotNull
    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public @NotNull UUID getOwner() {
        return owner;
    }

    @Override
    public void setSpawn(@NotNull Pos spawn) {
        this.spawn = spawn;
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
        return min;
    }

    @Override
    public @NotNull Point getMax() {
        return max;
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
    public boolean contains(@NotNull Point point) {
        boolean isXBetween = (point.x() >= Math.min(max.x(), min.x()))
                && (point.x() <= Math.max(max.x(), min.x()));

        boolean isYBetween = (point.y() >= Math.min(max.y(), min.y()))
                && (point.y() <= Math.max(max.y(), min.y()));

        boolean isZBetween = (point.z() >= Math.min(max.z(), min.z()))
                && (point.z() <= Math.max(max.z(), min.z()));
        return isXBetween && isYBetween && isZBetween;
    }

    @Override
    public void expand(int amount, Direction direction) {
        this.min = new Vec(min.x() - amount, min.y() - amount, min.z() - amount);
        this.max = new Vec(max.x() + amount, max.y() + amount, max.z() + amount);
        refreshCenter();
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

    private void refreshCenter() {
        this.center = new Vec((min.x() + max.x()) / 2, (min.y() + max.y()) / 2, (min.z() + max.z()) / 2);
    }

}
