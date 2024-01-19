package net.blossom.player;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.blossom.core.Blossom;
import net.blossom.utils.JsonUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerManager {

    private final Map<UUID, BlossomPlayer> players = new ConcurrentHashMap<>();
    private final Set<UUID> recentlyQuit = new HashSet<>();
    private final File playersDir;
    private static final Component SPLITTER = Component.text(": ", NamedTextColor.DARK_GRAY);
    private static final ChatRenderer CHAT_RENDERER = ((source, sourceDisplayName, message, viewer) ->  {
        BlossomPlayer player = BlossomPlayer.of(source.getUniqueId());
        return Blossom.getChatManager().createMessage(
                player.getRank().getPrefix() + " " + player.getRank().getNameColor() + player.getPlayer().getName(), false
        ).append(SPLITTER).append(message);
    });


    public PlayerManager() {
        playersDir = new File(Blossom.getDataFolder(), "players");
        if (!playersDir.exists()) {
            playersDir.mkdirs();
        }
        Blossom.async(() -> {
            for (BlossomPlayer player : players.values()) {
                player.asyncTick();
            }
        }, 0L, 1L);
        Blossom.registerBukkitListener(new Listener() {
            @EventHandler
            public void onChat(AsyncChatEvent event) {
                event.viewers().clear();
                event.viewers().addAll(BlossomPlayer.of(event.getPlayer().getUniqueId()).getWhoCanReceiveMessages());
                event.renderer(CHAT_RENDERER);
            }

            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Blossom.async(() -> {
                    getOrCreatePlayer(event.getPlayer().getUniqueId());
                    Blossom.getChatManager().sendJoinMessage(getOrCreatePlayer(event.getPlayer().getUniqueId()));
                });
                Blossom.sync(() -> {
                    BlossomPlayer.of(event.getPlayer()).refreshEquipment();
                }, 10L);
            }


            @EventHandler
            public void onConnect(AsyncPlayerPreLoginEvent event) {
                if (recentlyQuit.contains(event.getUniqueId())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("You recently quit the server, please wait a few seconds before reconnecting!"));
                }
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                recentlyQuit.add(event.getPlayer().getUniqueId());
                final BlossomPlayer player = getOrCreatePlayer(event.getPlayer().getUniqueId());
                player.asyncLeave();
                Blossom.getChatManager().sendQuitMessage(player);
                Blossom.async(() -> {
                    save(player);
                    players.remove(event.getPlayer().getUniqueId());
                    recentlyQuit.remove(event.getPlayer().getUniqueId());
                });
            }

        });
    }


    public void save(BlossomPlayer player) {
        File file = getFileOfPlayer(player.getUUID());
        Gson gson = JsonUtils.GSON;
        try {
            FileWriter writer = new FileWriter(file);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            gson.toJson(player.toJson(), JsonObject.class, jsonWriter);
            jsonWriter.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    BlossomPlayer getOrCreatePlayer(final UUID uuid) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        }
        final BlossomPlayer player = new BlossomPlayer(uuid);
        JsonObject data = JsonUtils.castJson(getFileOfPlayer(uuid), JsonObject.class);
        if (data == null) {
            data = player.toJson();
        }
        player.load(data);
        players.put(uuid, player);
        return player;
    }

    public File getFileOfPlayer(final UUID uuid) {
        return new File(playersDir, uuid + ".json");
    }
}
