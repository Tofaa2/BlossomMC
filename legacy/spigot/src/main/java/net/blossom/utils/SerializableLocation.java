package net.blossom.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record SerializableLocation(double x, double y, double z, UUID world) {


    public @NotNull Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static @NotNull SerializableLocation fromLocation(@NotNull Location location) {
        return new SerializableLocation(location.getX(), location.getY(), location.getZ(), location.getWorld().getUID());
    }

}
