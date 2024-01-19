package net.blossom.hub.features;

import com.google.auto.service.AutoService;
import net.blossom.commons.StringUtils;
import net.blossom.commons.animations.FrameAnimatable;
import net.blossom.communications.ServerSample;
import net.blossom.core.*;
import net.blossom.core.gui.Gui;
import net.blossom.core.gui.GuiButton;
import net.blossom.core.hologram.Hologram;
import net.blossom.core.hologram.HologramLine;
import net.blossom.core.utils.AnimationUtils;
import net.blossom.core.utils.ComponentUtils;
import net.blossom.hub.Hub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.PlayerConnection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AutoService(Feature.class)
public class HubFeature extends Feature {

    private InstanceContainer instance;
    private ItemStack serverSelector;
    private ItemStack cosmetics;


    @Override
    public void init() {
        setupHubItems();
        instance = process().instance().createInstanceContainer(ExtraDimensionTypes.FULLBRIGHT);
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 50, Block.STONE));

        Hologram hologram = Hologram.create(
                instance,
                HologramLine.text(new Pos(0, 52, 0), Component.text("Hello World")),
                HologramLine.item(
                        new Pos(0, 55, 0),
                        ItemStack.builder(Material.DIAMOND_SWORD).build(),
                        new FrameAnimatable<>(1, AnimationUtils.forwardBackward(new Pos(0, 55, 0), 200))
                )
        );
        hologram.show();

        addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);
            event.getPlayer().setRespawnPoint(new Pos(0, 51, 0));
        });
        addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            PlayerInventory inventory = event.getPlayer().getInventory();
            inventory.setItemStack(0, serverSelector);
            inventory.setItemStack(4, cosmetics);
        });

        addListener(PlayerDisconnectEvent.class, event -> {
            HubPlayer player = (HubPlayer) event.getPlayer();
            async(() -> {
                Hub.getDatabase().replaceOne("players", new Document("uuid", player.getUuid()), player.document);
                getLogger().info("Saved player " + player.getUsername() + " to database.");
            });
        });

        process().connection().setPlayerProvider(new PlayerProvider() {
            @Override
            public @NotNull Player createPlayer(@NotNull UUID uuid, @NotNull String s, @NotNull PlayerConnection playerConnection) {
                Document data = Hub.getDatabase().findOne("players", new Document("uuid", uuid));
                if (data == null) data = new Document("uuid", uuid);
                return new HubPlayer(uuid, s, playerConnection, data);
            }
        });
        registerCommands(
                BlossomCommand.fast("togglesidebar", (sender, args) -> {
                    if (sender instanceof HubPlayer player) {
                        if (player.getPlayerSidebar().isViewer(player)) {
                            player.getPlayerSidebar().removeViewer(player);
                            player.sendMessage("<red>You have disabled the sidebar.");
                        } else {
                            player.getPlayerSidebar().addViewer(player);
                            player.sendMessage("<green>You have enabled the sidebar.");
                        }
                    }
                }),
                new ShardsCommand(),
                BlossomCommand.fast("play", (sender, args) -> {
                    if (!(sender instanceof HubPlayer player)) return;
                    Gui.Builder gui = Gui.builder(InventoryType.CHEST_6_ROW, Component.text("Server Selector"));
                    List<ServerSample> servers = Blossom.getProxyServers();
                    for (int i = 0; i < servers.size(); i++) {
                        ServerSample server = servers.get(i);
                        int online = server.onlinePlayers();
                        String name = StringUtils.fancyName(server.name());
                        int slot = i + 10;
                        if (i % 2 == 0) slot += 1;
                        List<Component> lore = new ArrayList<>();
                        lore.add(Component.empty());
                        lore.add(ComponentUtils.normal("Online: ", NamedTextColor.GRAY).append(ComponentUtils.normal(String.valueOf(online), NamedTextColor.GREEN)));
                        gui.withButton(
                                slot,
                                new GuiButton(
                                        ItemStack.builder(Material.PLAYER_HEAD)
                                                .displayName(ComponentUtils.normal(name, NamedTextColor.DARK_PURPLE))
                                                .lore(lore)
                                                .build(),
                                        (ui, p,  clickType) -> {
                                            p.closeInventory();
                                            p.sendMessage(ComponentUtils.normal("Connecting to " + name + "...", NamedTextColor.GRAY));
                                            ((BlossomPlayer)p).sendToProxyServer(server.name());
                                        }
                                )
                        );
                    }
                })
        );
    }

    private void setupHubItems() {

        serverSelector = ItemStack.builder(Material.COMPASS)
                .displayName(ComponentUtils.normal("Server Selector", NamedTextColor.DARK_PURPLE))
                .lore(Component.empty(), ComponentUtils.normal("Click to open the server selector.", NamedTextColor.GRAY))
                .build();
        cosmetics = ItemStack.builder(Material.CHEST)
                .displayName(ComponentUtils.normal("Cosmetics", NamedTextColor.DARK_PURPLE))
                .lore(Component.empty(), ComponentUtils.normal("Click to open the cosmetics menu.", NamedTextColor.GRAY))
                .build();

        addListener(PlayerUseItemEvent.class, event -> {
            ItemStack itemStack = event.getItemStack();
            if (itemStack.isSimilar(serverSelector)) {
                MinecraftServer.getCommandManager().execute(event.getPlayer(), "play");
            }
            else if (itemStack.isSimilar(cosmetics)) {
                MinecraftServer.getCommandManager().execute(event.getPlayer(), "cosmetics");
            }
        });
    }

}
