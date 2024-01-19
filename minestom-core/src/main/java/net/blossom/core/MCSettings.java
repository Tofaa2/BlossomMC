package net.blossom.core;

import net.blossom.dbm.DatabaseSettings;

public record MCSettings(
        String host,
        int port,
        int chunkViewDistance,
        int entityViewDistance,
        int maxPlayers,
        boolean onlineMode,
        String mongoUri,
        ProxySettings proxy
) {

    static final MCSettings DEFAULT = new MCSettings(
            "127.0.0.1",
            25565,
            8,
            8,
            100,
            true,
            "mongodb://localhost:27017",
            new ProxySettings(false, "")
    );

    public record ProxySettings(
            boolean enabled,
            String secret
    ) {}

}
