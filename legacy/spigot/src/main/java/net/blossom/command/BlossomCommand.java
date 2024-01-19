package net.blossom.command;

import net.blossom.player.BlossomPlayer;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BlossomCommand {

    @NotNull
    default BlossomPlayer getPlayer(final Player player) {
        return BlossomPlayer.of(player);
    }

    @NotNull
    default BlossomPlayer getPlayer(final UUID uuid) {
        return BlossomPlayer.of(uuid);
    }

    @Nullable
    default BlossomPlayer getPlayer(final String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) return null;
        return BlossomPlayer.of(player);
    }

    default @Nullable CommandAPICommand create() {
        return null;
    }

    default @Nullable CommandAPICommand[] createMany() {
        return null;
    }

}
