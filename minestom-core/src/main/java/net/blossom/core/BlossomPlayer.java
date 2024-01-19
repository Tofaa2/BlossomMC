package net.blossom.core;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.blossom.core.gui.Gui;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BlossomPlayer extends Player {
    public BlossomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    public void openGui(@NotNull Gui gui) {
        gui.open(this);
    }

    public void openGui(@NotNull Gui.Builder gui) {
        gui.build().open(this);
    }

    public void spawnParticle(Particle particle, Point point, int count) {
        sendPacketToViewersAndSelf(ParticleCreator.createParticlePacket(particle, point.x(), point.y(), point.z(), 0.0f, 0.0f, 0.0f, count));
    }

    public void sendToProxyServer(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput(); // Requires Guava IIRC
        out.writeUTF("Connect");
        out.writeUTF(server);
        sendPluginMessage("BungeeCord", out.toByteArray());
    }

}
