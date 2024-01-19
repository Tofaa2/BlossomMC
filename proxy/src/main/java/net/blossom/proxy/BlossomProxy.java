package net.blossom.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.blossom.commons.json.Config;
import net.blossom.dbm.BlossomDatabase;
import net.blossom.dbm.DatabaseSettings;
import net.blossom.proxy.commands.*;
import org.bson.Document;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(
        id = "blossom-proxy",
        name = "BlossomProxy",
        version = "1.0.0"
)
public class BlossomProxy {

    private static BlossomProxy instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDir;
    private final Map<UUID, BlossomPlayer> blossomPlayers = new ConcurrentHashMap<>();
    private  BlossomDatabase database;

    @Inject
    public BlossomProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDir) {
        this.server = server;
        this.logger = logger;
        this.dataDir = dataDir;
        this.dataDir.toFile().mkdirs();
    }

    public static BlossomProxy getInstance() {
        return instance;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        Config<DatabaseSettings> config = Config.createAndLoad(DatabaseSettings.class, dataDir.resolve("database.json"), new DatabaseSettings(DatabaseSettings.DEFAULT_MONGO_URI, "proxy"));
        this.database = new BlossomDatabase(config.get());
        instance = this;
        registerCommands(
                new FriendsCommand(),
                new WarnCommand()
        );
        Command.register(this.server, new BetterWarnCommand());
    }

    public void registerCommands(BlossomCommand... commands) {
        for (BlossomCommand command : commands) {
            this.server.getCommandManager().register(command.getName(), command, command.getAliases());
        }
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        this.database.close();
    }

    public void registerListeners(Object... listeners) {
        for (Object listener : listeners) {
            this.server.getEventManager().register(this, listener);
        }
    }

    public BlossomDatabase getDatabase() {
        return database;
    }

    public void UNSAFE_setPlayer(Player player, BlossomPlayer blossomPlayer) {
        this.blossomPlayers.put(player.getUniqueId(), blossomPlayer);
    }

    public void UNSAFE_removePlayer(UUID uuid) {
        this.blossomPlayers.remove(uuid);
    }

    public Logger getLogger() {
        return logger;
    }

    public BlossomPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public ProxyServer getServer() {
        return server;
    }


    public BlossomPlayer getPlayer(UUID uuid) {
        if (blossomPlayers.containsKey(uuid)) {
            return blossomPlayers.get(uuid);
        }
        Document document = database.findOne("players", new Document("uuid", uuid));
        if (document == null) {
            document = new Document("uuid", uuid);
            database.replaceOne("players", new Document("uuid", uuid), document);
        }
        BlossomPlayer blossomPlayer = new BlossomPlayer(uuid, document.getString("last-known-username"), document);
        blossomPlayers.put(uuid, blossomPlayer);
        return blossomPlayer;
    }
}
