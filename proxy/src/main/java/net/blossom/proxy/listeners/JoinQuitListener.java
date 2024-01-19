package net.blossom.proxy.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.blossom.communications.Communications;
import net.blossom.communications.ServerSample;
import net.blossom.proxy.BlossomPlayer;
import net.blossom.proxy.BlossomProxy;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JoinQuitListener {

    private final Map<RegisteredServer, Long> lastUpdateTime = new ConcurrentHashMap<>();
    private final MinecraftChannelIdentifier channelIdentifier;

    public JoinQuitListener() {
        String[] split = Communications.SERVER_SAMPLE_IDENTIFIER.split(":");
        this.channelIdentifier = MinecraftChannelIdentifier.create(split[0], split[1]); // Stupid velocity not having a constructor for this
    }

    @Subscribe
    public void onProxyConnect(ServerPreConnectEvent event) {
        Document document = BlossomProxy.getInstance().getDatabase().findOne("players", new Document("uuid", event.getPlayer().getUniqueId()));
        if (document == null) {
            document = new Document("uuid", event.getPlayer().getUniqueId());
        }
        document.put("last-known-username", event.getPlayer().getUsername());
        BlossomProxy.getInstance().getDatabase().replaceOne("players", new Document("uuid", event.getPlayer().getUniqueId()), document);
        BlossomProxy.getInstance().UNSAFE_setPlayer(event.getPlayer(), new BlossomPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getUsername(), document));
    }

    @Subscribe
    public void onPlayerJoin(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        RegisteredServer server = event.getServer();
        if (!lastUpdateTime.containsKey(server)) {
            postUpdate(server, player);
        }
        if (lastUpdateTime.get(server) + 1000 * 60 * 5 < System.currentTimeMillis()) {
            postUpdate(server, player);
        }
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        BlossomProxy.getInstance().getDatabase().replaceOne("players", new Document("uuid", event.getPlayer().getUniqueId()), BlossomProxy.getInstance().getPlayer(event.getPlayer().getUniqueId()).getDocument());
        BlossomProxy.getInstance().UNSAFE_removePlayer(event.getPlayer().getUniqueId());
    }

    private void postUpdate(RegisteredServer server, Player player) {
        lastUpdateTime.put(server, System.currentTimeMillis());
        Collection<RegisteredServer> servers = BlossomProxy.getInstance().getServer().getAllServers();
        List<ServerSample> samples = new ArrayList<>(servers.size());
        for (RegisteredServer s : servers) {
            samples.add(new ServerSample(
                    s.getServerInfo().getName(),
                    server.getPlayersConnected().size()
            ));
        }
        player.sendPluginMessage(channelIdentifier, ServerSample.toBytes(samples));
        BlossomProxy.getInstance().getLogger().info("Sent server sample update to " + server.getServerInfo().getName());
    }

}
