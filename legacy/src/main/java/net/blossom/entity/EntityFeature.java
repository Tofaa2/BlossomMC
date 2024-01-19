package net.blossom.entity;

import com.google.auto.service.AutoService;
import com.google.gson.JsonObject;
import net.blossom.chat.ChatFeature;
import net.blossom.core.BlossomCommand;
import net.blossom.core.Feature;
import net.blossom.core.FeatureDepends;
import net.blossom.data.DataType;
import net.blossom.entity.commands.GameModeCommand;
import net.blossom.entity.commands.SpawnMobCommand;
import net.blossom.entity.event.PlayerAttackMobEvent;
import net.blossom.entity.event.PlayerBlockDiggingEvent;
import net.blossom.entity.loot.BoundItemEntity;
import net.blossom.entity.loot.LootTable;
import net.blossom.entity.mob.AggressionType;
import net.blossom.entity.mob.BaseMob;
import net.blossom.entity.mob.EntityAIGenerator;
import net.blossom.entity.mob.Mob;
import net.blossom.item.ItemFeature;
import net.blossom.utils.DateUtils;
import net.blossom.utils.JsonUtils;
import net.blossom.utils.UnicodeCharacters;
import net.blossom.utils.Utils;
import net.blossom.world.WorldFeature;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.EntityAIGroup;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityItemMergeEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@AutoService(Feature.class)
@FeatureDepends({ChatFeature.class, ItemFeature.class})
public final class EntityFeature extends Feature {

    public static void spawnDamageDisplay(Instance instance, Pos position, float damage) {
        Entity e = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta meta = (TextDisplayMeta) e.getEntityMeta();
        meta.setNotifyAboutChanges(false);

        meta.setSeeThrough(true);
        meta.setHasNoGravity(true);
        meta.setHasGlowingEffect(true);
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        meta.setShadow(false);

        meta.setNotifyAboutChanges(true);
        meta.setText(getFeature(ChatFeature.class).createMessage("<gradient:#F05941:#872341>" + Utils.toHundrethDecimal(damage) + UnicodeCharacters.HEART_ICON, false));
        e.setInstance(instance, Utils.randomizeNearest(position, 0.5).add(0, 2, 0));
        e.scheduleRemove(Duration.ofMillis(1500));
    }

    private File playerDataDir;
    private File mobDataDir;
    private final Map<Class<? extends EntityAIGroup>, EntityAIGenerator<?>> aiGenerators = new HashMap<>();
    private final Map<String, BaseMob> baseMobs = new HashMap<>();

    @Override
    public void init() {
        playerDataDir = new File(DATA_FOLDER, "players");
        if (!playerDataDir.exists()) {
            playerDataDir.mkdirs();
        }
        process().connection().setPlayerProvider(((uuid, username, connection) -> {
            BlossomPlayer player = new BlossomPlayer(uuid, username, connection);
            player.loadDataAsync();
            return player;
        }));

        getEventNode().addListener(EntityItemMergeEvent.class, event -> {
            if (!(event.getEntity() instanceof BoundItemEntity entity) ||
                    !(event.getMerged() instanceof BoundItemEntity merged))
                return;
            if (!entity.getEntityUUID().equals(merged.getEntityUUID()))
                event.setCancelled(true);
        });

        getEventNode().addListener(PickupItemEvent.class, event -> {
            if (!(event.getItemEntity() instanceof BoundItemEntity itemEntity)) return;
            if (!(event.getEntity() instanceof BlossomPlayer player)) {
                event.setCancelled(true);
                return;
            }
            if (!itemEntity.isBoundTo(player)) {
                event.setCancelled(true);
                return;
            }
            boolean added = player.getInventory().addItemStack(itemEntity.getItemStack());
            if (!added) {
                event.setCancelled(true);
            }
        });


        getEventNode().addListener(EntityAttackEvent.class, event -> {
            if (!(event.getEntity() instanceof BlossomPlayer player)) return;
            if (event.getTarget() instanceof Mob target) {
                PlayerAttackMobEvent e = new PlayerAttackMobEvent(player, target);
                e.setDamage(player.getDamage(target));
                MinecraftServer.getGlobalEventHandler().call(e);
                if (e.isCancelled()) return;
                target.damage(DamageType.fromPlayer(player), (float) e.getDamage());
                if (e.shouldApplyKnockback()) {
                    target.takeKnockback(
                            0.4f,
                            Math.sin(player.getPosition().yaw() * 0.017453292),
                            -Math.cos(player.getPosition().yaw() * 0.017453292)
                    );
                }
                if (target.getHealth() <= 0 || target.isDead() || !target.isActive()) {
                    target.remove();
                }
            }
        });

        getEventNode().addListener(PlayerBlockDiggingEvent.class, event -> event.setDiggingBlock(100, () -> 10));

        getEventNode().addListener(PlayerPacketEvent.class, event -> {
            if (event.getPacket() instanceof ClientPlayerDiggingPacket packet) {
                handlePlayerDigging(event.getPlayer(), packet);
            }
        });

        getEventNode().addListener(PlayerDisconnectEvent.class, event -> {
            BlossomPlayer player = (BlossomPlayer) event.getPlayer();
            async(player::save);
        });

        registerCommands(GameModeCommand.ALL);
        registerCommands(
                BlossomCommand.fast("spawn", (sender, context) -> {
                    if (!(sender instanceof Player player)) {
                        return;
                    }
                    player.teleport(Feature.getFeature(WorldFeature.class).getSpawnPoint());
                    player.sendMessage("<green>Teleported to spawn!");
                }),
                BlossomCommand.fast("playtime", (sender, context) -> {
                    if (!(sender instanceof BlossomPlayer player)) {
                        return;
                    }
                    player.refreshSessionStart();
                    player.sendMessage("<green>You have played for <white>" + DateUtils.convertTime(player.getPlayTime()) + "<green>!");
                }),
                new SpawnMobCommand()
        );
        registerAIGenerators();
        registerMobs();
    }


    public @Nullable BaseMob getBaseMob(String name) {
        return baseMobs.get(name);
    }

    public @NotNull Collection<String> getBaseMobNames() {
        return Collections.unmodifiableCollection(baseMobs.keySet());
    }

    public <T extends EntityAIGroup> void registerAIGenerator(Class<T> clazz, EntityAIGenerator<T> generator) {
        aiGenerators.put(clazz, generator);
    }

    public @Nullable EntityAIGenerator<?> getAIGenerator(Class<?> clazz) {
        return aiGenerators.get(clazz);
    }

    public @Nullable EntityAIGenerator<?> getAIGenerator(String name) {
        return aiGenerators.values().stream().filter(generator -> generator.getAIName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private void registerAIGenerators() {

    }

    private void registerMobs() {
        File mobsDir = new File(DATA_FOLDER, "mobs");
        if (!mobsDir.exists()) {
            mobsDir.mkdirs();
        }
        File[] mobFiles = mobsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (mobFiles == null) {
            return;
        }
        for (File file : mobFiles) {
            JsonObject mobJson = JsonUtils.castJson(file, JsonObject.class);
            if (mobJson == null) {
                getLogger().warn("Failed to load mob file: " + file.getName());
                continue;
            }
            String mobName = mobJson.get("name").getAsString();
            EntityType entityType = EntityType.fromNamespaceId(mobJson.get("type").getAsString());
            AggressionType aggressionType = AggressionType.valueOf(mobJson.get("aggression").getAsString().toUpperCase());
            JsonObject dataJson = mobJson.getAsJsonObject("data");
            Map<DataType<?>, Object> data = new HashMap<>();
            if (dataJson != null) {
                for (var entry : dataJson.entrySet()) {
                    NamespaceID key = NamespaceID.from(entry.getKey());
                    DataType<?> dataType = DataType.getFromKey(key);
                    if (dataType == null) {
                        getLogger().warn("Failed to load data type: " + key);
                        continue;
                    }
                    data.put(dataType, JsonUtils.castJson(entry.getValue().toString(), dataType.dataClass()));
                }
            }

            JsonObject aiJson = mobJson.getAsJsonObject("ai");
            Map<String, JsonObject> aiData = new HashMap<>();

            if (aiJson != null) {
                for (var entry : aiJson.entrySet()) {
                    aiData.put(entry.getKey(), entry.getValue().getAsJsonObject());
                }
            }

            LootTable lootTable = LootTable.fromJson(mobJson.getAsJsonObject("loot"));

            Check.stateCondition(mobName == null, "Mob name cannot be null");
            Check.stateCondition(entityType == null, "Mob type cannot be null");
            BaseMob mob = new BaseMob(mobName, entityType, data, aggressionType, aiData, lootTable);
            baseMobs.put(mobName, mob);
            getLogger().info("Loaded mob: " + mobName);
        }
    }

    public File getPlayerFile(OfflinePlayer player) {
        return new File(playerDataDir, player.getUuid() + ".json");
    }

    public File getPlayerFile(final UUID uuid) {
        return new File(playerDataDir, uuid + ".json");
    }

    @Blocking
    public OfflinePlayer getOrLoadPlayer(final UUID uuid) {
        var online = process().connection().getPlayer(uuid);
        if (online != null && online.isOnline()) {
            return (OfflinePlayer) online;
        }
        var file = getPlayerFile(uuid);
        OfflinePlayer p = new OfflinePlayerImpl(uuid);
        if (!file.exists()) {
            p.save();
            return p;
        }
        p.loadData(JsonUtils.castJson(file, JsonObject.class));
        return p;
    }

    public CompletableFuture<OfflinePlayer> getOrLoadPlayerAsync(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getOrLoadPlayer(uuid));
    }

    private void handlePlayerDigging(Player p, ClientPlayerDiggingPacket packet) {
        BlossomPlayer player = (BlossomPlayer) p;
        switch (packet.status()) {
            case STARTED_DIGGING -> {
                final Instance instance = player.getInstance();
                if (instance == null) return;
                final Block block = player.getInstance().getBlock(packet.blockPosition());

                // Call event
                PlayerBlockDiggingEvent e = new PlayerBlockDiggingEvent(player, block);
                MinecraftServer.getGlobalEventHandler().call(e);

                // Setup internal state for digging
                if (e.getMaxHealth() != 0) {
                    player.setDiggingBlock(packet.blockPosition());
                    player.setDiggingDamageFn(e.getDamageFunction());
                    player.setDiggingBlockHealth(e.getMaxHealth());
                    player.setDiggingBlockMaxHealth(e.getMaxHealth());
                    player.setDiggingLastStage(0);
                    player.setDiggingFace(packet.blockFace());
                }
            }
            case CANCELLED_DIGGING -> player.clearLongDigging();
            case FINISHED_DIGGING -> {
                // This would only happen if the player somehow lost mining fatigue (or is running a hacked client)
                //todo need to cancel this i guess?
                // Also if this happens it means the client has lost mining fatigue, so we should give it back to them
                // unless they are in the bypass mode that might exist on the dev server
                player.clearLongDigging();
            }
            default -> {
                // Ignore cases.
            }
        }
    }

}
