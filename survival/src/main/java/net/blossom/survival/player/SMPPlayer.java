package net.blossom.survival.player;

import net.blossom.core.BlossomPlayer;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SMPPlayer extends BlossomPlayer {

    public SMPPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }
}
