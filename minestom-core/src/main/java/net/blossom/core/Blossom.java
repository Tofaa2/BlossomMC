package net.blossom.core;

import net.blossom.commons.json.Config;
import net.blossom.communications.Communications;
import net.blossom.communications.ServerSample;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

public final class Blossom {
    private Blossom() {}
    public static final Logger LOGGER = LoggerFactory.getLogger(Blossom.class);
    private static MCSettings settings;
    private static List<ServerSample> proxyServers = List.of();

    public static void init(Runnable beforeFeatures, Runnable afterFeatures) {
        var minestom = MinecraftServer.init();
        MinecraftServer.setBrandName("Blossom");
        Config<MCSettings> settingsConfig = Config.createAndLoad(
                MCSettings.class,
                Path.of("./settings.json"),
                MCSettings.DEFAULT
        );
        settings = settingsConfig.get();
        MinecraftServer.setEntityViewDistance(settings.entityViewDistance());
        MinecraftServer.setChunkViewDistance(settings.chunkViewDistance());

        ExtraDimensionTypes.init();
        checkProxyAndOnlineMode();

        if (beforeFeatures != null) beforeFeatures.run();
        Feature.loadFeatures();
        if (afterFeatures != null) afterFeatures.run();

        minestom.start(settings.host(), settings.port());
    }

    public static void init() {
        init(null, null);
    }

    public static MCSettings getSettings() {
        return settings;
    }

    public static List<ServerSample> getProxyServers() {
        return proxyServers;
    }

    public static int getTotalOnline() {
        return getProxyServers().stream().mapToInt(ServerSample::onlinePlayers).sum();
    }

    private static void checkProxyAndOnlineMode() {
        boolean onlineMode = settings.onlineMode();
        if (settings.proxy().enabled()) {
            if (onlineMode) {
                LOGGER.error("Online mode is enabled, but proxy is enabled too. Disabling online mode...");
                onlineMode = false;
            }
            String secret = settings.proxy().secret();
            if (secret != null && !secret.isBlank() && !secret.isEmpty()) {
                VelocityProxy.enable(secret);
                LOGGER.info("Velocity proxy enabled.");
            }
        }
        if (onlineMode) {
            LOGGER.info("Online mode enabled.");
            MojangAuth.init();
        }
        else {
            LOGGER.info("Offline mode enabled.");
        }
        if (VelocityProxy.isEnabled()) {
            MinecraftServer.getGlobalEventHandler().addListener(PlayerPluginMessageEvent.class, event -> {
                String channel = event.getIdentifier();
                if (!channel.equals(Communications.SERVER_SAMPLE_IDENTIFIER)) return;
                proxyServers = ServerSample.fromBytes(event.getMessage());
            });
        }
    }

}
