package net.blossom.entity;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.blossom.chat.ChatFeature;
import net.blossom.core.Blossom;
import net.blossom.core.Feature;
import net.blossom.core.Rank;
import net.blossom.data.DataContainer;
import net.blossom.data.DataType;
import net.blossom.entity.loot.BoundItemEntity;
import net.blossom.entity.mob.Mob;
import net.blossom.item.Item;
import net.blossom.item.types.StatsItem;
import net.blossom.region.RegionFeature;
import net.blossom.region.SimpleRegion;
import net.blossom.utils.JsonUtils;
import net.blossom.utils.SerializableCoordinate;
import net.kyori.adventure.bossbar.BossBar;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.function.IntSupplier;

import static net.blossom.core.Blossom.LOGGER;
import static net.blossom.core.Feature.async;

public class BlossomPlayer extends Player implements net.blossom.data.SimpleDataContainer, OfflinePlayer {

    private final HashMap<DataType<?>, Object> data = new HashMap<>(DataContainer.DEFAULT_PLAYER_DATA);
    private Rank rank = Rank.MEMBER;
    private SimpleRegion ownedRegion;
    private Pos home;
    private long discordId = -1L;
    private long firstLogin = System.currentTimeMillis();
    private long lastQuit = -1L;
    private long playTime = 0L;
    private long sessionStart = System.currentTimeMillis();

    // Long digging state
    private Point diggingBlock = null;
    private IntSupplier diggingDamageFn = null;
    private int diggingLastStage = 0;
    private int diggingBlockHealth = 0;
    private int diggingBlockMaxHealth = 0;
    private BlockFace diggingFace = null;
    private BossBar healthBar, manaBar;

    public BlossomPlayer(
            @NotNull UUID uuid,
            @NotNull String username,
            @NotNull PlayerConnection playerConnection
    ) {
        super(uuid, username, playerConnection);
    }

    @Override
    public void update(long time) {
        super.update(time);
        tickBlockBreaking();
    }

    /* returns the health of a player including the buffs given by equipment etc. */
    @Override
    public float getHealth() {
        float health = getData(DataType.HEALTH);
        Collection<Item> equipment = getEquippedItems();
        for (Item item : equipment) {
            if (item instanceof StatsItem s) {
                health += s.getData(DataType.HEALTH);
            }
        }
        return health;
    }

    /* returns the max health of a player including the buffs given by equipment etc. */
    @Override
    public float getMaxHealth() {
        float maxHealth = getData(DataType.MAX_HEALTH);
        Collection<Item> equipment = getEquippedItems();
        for (Item item : equipment) {
            if (item instanceof StatsItem s) {
                maxHealth += s.getData(DataType.MAX_HEALTH);
            }
        }
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        setData(DataType.MAX_HEALTH, maxHealth);
    }

    public void setHealth(float health) {
        setData(DataType.HEALTH, health);
    }

    private void tickBlockBreaking() {
        if (diggingBlock == null) return;
        int damage = (diggingDamageFn.getAsInt());
        diggingBlockHealth = Math.max(0, diggingBlockHealth - damage);
        if (diggingBlockHealth == 0) {
            // Break the block & reset
            getInstance().breakBlock(this, diggingBlock, diggingFace);
            clearLongDigging();
        } else {
            updateDiggingBlock();
        }
    }

    void clearLongDigging() {
        diggingBlockHealth = 0;
        diggingBlockMaxHealth = 0;
        diggingLastStage = 0;
        diggingFace = null;

        // Send update to clear digging animation
        updateDiggingBlock();
        diggingBlock = null;
    }

    private void updateDiggingBlock() {
        if (diggingBlock == null) return;

        byte stage = Byte.MAX_VALUE;
        if (diggingBlockMaxHealth != 0) {
            stage = (byte) (((float) (diggingBlockMaxHealth - diggingBlockHealth) / diggingBlockMaxHealth) * 12f);
            if (stage == diggingLastStage) return;
            diggingLastStage = stage;
        }

        // New stage, send packet
        var packet = new BlockBreakAnimationPacket(getEntityId() + 1, diggingBlock, stage);
        sendPacket(packet);
    }

    void setDiggingBlock(Point diggingBlock) {
        this.diggingBlock = diggingBlock;
    }

    void setDiggingDamageFn(IntSupplier diggingDamageFn) {
        this.diggingDamageFn = diggingDamageFn;
    }

    void setDiggingLastStage(int diggingLastStage) {
        this.diggingLastStage = diggingLastStage;
    }

    void setDiggingBlockHealth(int diggingBlockHealth) {
        this.diggingBlockHealth = diggingBlockHealth;
    }

    void setDiggingBlockMaxHealth(int diggingBlockMaxHealth) {
        this.diggingBlockMaxHealth = diggingBlockMaxHealth;
    }

    void setDiggingFace(BlockFace diggingFace) {
        this.diggingFace = diggingFace;
    }

    @Override
    public void save() {
        setLastQuitTime(System.currentTimeMillis());
        JsonObject data = new JsonObject();
        data.addProperty("uuid", getUuid().toString());
        data.addProperty("rank", getRank().name());
        if (home != null) data.addProperty("home", JsonUtils.toJson(SerializableCoordinate.fromPos(getHome())));
        if (ownedRegion != null) data.addProperty("ownedRegion", ownedRegion.getUuid().toString());
        Map<String, Object> dataMap = new HashMap<>();
        for (var entry : this.data.entrySet()) {
            dataMap.put(entry.getKey().key().asString(), entry.getValue());
        }
        data.addProperty("data", JsonUtils.toJson(dataMap));
        data.addProperty("discordId", discordId);
        data.addProperty("firstLogin", firstLogin);
        data.addProperty("lastQuit", lastQuit);
        data.addProperty("playTime", playTime);
        File playerFile = Feature.getFeature(EntityFeature.class).getPlayerFile(this);
        JsonUtils.writeJson(playerFile, data, JsonObject.class);
    }

    @Override
    public void loadData(JsonObject data) {
        this.rank = Rank.valueOf(data.get("rank").getAsString());
        if (data.has("home")) this.home = SerializableCoordinate.fromJson(data.get("home").getAsString());
        if (data.has("ownedRegion")) {
            SimpleRegion region = Feature.getFeature(RegionFeature.class).getRegion(UUID.fromString(data.get("ownedRegion").getAsString()));
            if (region == null) {
                LOGGER.error("Could not find region with UUID " + data.get("ownedRegion").getAsString());
            }
            this.ownedRegion = region;
        }
        if (data.has("data")) {
            Map<String, Object> dataMap = JsonUtils.castJson(data.get("data").getAsString(), TypeToken.getParameterized(Map.class, String.class, Object.class).getType());
            if (dataMap != null) {
                for (var entry : dataMap.entrySet()) {
                    DataType<?> type = DataType.getFromName(entry.getKey());
                    if (type == null) {
                        LOGGER.error("Could not find data type with key " + entry.getKey());
                        continue;
                    }
                    this.data.put(type, entry.getValue());
                }
            }
        }
        this.discordId = data.get("discordId").getAsLong();
        this.firstLogin = data.get("firstLogin").getAsLong();
        this.lastQuit = data.get("lastQuit").getAsLong();
        this.playTime = data.get("playTime").getAsLong();
    }

    void loadDataAsync() {
        File playerFile = Feature.getFeature(EntityFeature.class).getPlayerFile(this);
        if (!playerFile.exists()) {
            async(this::save);
        }
        async(() -> {
            JsonObject data = JsonUtils.castJson(playerFile, JsonObject.class);
            if (data == null) return;
            loadData(data);
        });
    }

    @Override
    public boolean damage(@NotNull DamageType type, float value) {
        EntityFeature.spawnDamageDisplay(getInstance(), getPosition(), value);
        return super.damage(type, value);
    }

    @Override
    public boolean dropItem(@NotNull ItemStack item) {
        if (item.isAir()) return false;
        BoundItemEntity e = new BoundItemEntity(this, item);
        e.setInstance(getInstance(), getPosition()).thenRun(() -> {
            e.setPickupDelay(Duration.ofSeconds(1));
            e.setPickable(true);
            e.setVelocity(getPosition().add(0, 2, 0).direction().mul(482));
        });
        ItemDropEvent event = new ItemDropEvent(this, item);
        EventDispatcher.call(event);
        return !event.isCancelled();
    }

    public double getDamage(Mob mob) {
        double damage = getData(DataType.DAMAGE);
        double strength = getData(DataType.STRENGTH);
        double critChance = getData(DataType.CRITICAL_CHANCE);
        double critMultiplier = getData(DataType.CRITICAL_MULTIPLIER);

        for (Item item : getEquippedItems()) {
            if (item instanceof StatsItem s) {
                damage += s.getData(DataType.DAMAGE);
                strength += s.getData(DataType.STRENGTH);
                critChance += s.getData(DataType.CRITICAL_CHANCE);
                critMultiplier += s.getData(DataType.CRITICAL_MULTIPLIER);
            }
        }
        return Blossom.calculateFinalDamage(damage, strength, critChance, critMultiplier, mob.getData(DataType.DEFENSE));
    }


    @Override
    public @NotNull Collection<Item> getEquippedItems() {
        PlayerInventory i = getInventory();
        Item mainHand = Item.fromItemStack(i.getItemInMainHand());
        Item offHand = Item.fromItemStack(i.getItemInOffHand());
        Item helmet = Item.fromItemStack(i.getHelmet());
        Item chestplate = Item.fromItemStack(i.getChestplate());
        Item leggings = Item.fromItemStack(i.getLeggings());
        Item boots = Item.fromItemStack(i.getBoots());
        Set<Item> items = new HashSet<>();
        if (mainHand != null) items.add(mainHand);
        if (offHand != null) items.add(offHand);
        if (helmet != null) items.add(helmet);
        if (chestplate != null) items.add(chestplate);
        if (leggings != null) items.add(leggings);
        if (boots != null) items.add(boots);

        return items;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        sendMessage(message, true);
    }

    public void sendMessage(@NotNull String message, boolean prefix) {
        Feature.getFeature(ChatFeature.class).sendMessage(this, message, prefix);
    }

    @Override
    public long getDiscordId() {
        return discordId;
    }

    @Override
    public long getFirstLogin() {
        return firstLogin;
    }

    @Override
    public long getLastQuitTime() {
        return lastQuit;
    }

    @Override
    public long getPlayTime() {
        return playTime;
    }

    void setLastQuitTime(long lastQuit) {
        this.lastQuit = lastQuit;
        refreshSessionStart();
    }

    void refreshSessionStart() {
        long current = System.currentTimeMillis();
        this.playTime += current - sessionStart;
        this.sessionStart = current;
    }

    @Override
    public @Nullable SimpleRegion getOwnedRegion() {
        return ownedRegion;
    }

    @Override
    public @Nullable Pos getHome() {
        return home;
    }

    public @NotNull Rank getRank() {
        return rank;
    }

    @Override
    public void setRank(@NotNull Rank rank) {
        this.rank = rank;
        save();
    }

    @Override
    public @NotNull Map<DataType<?>, Object> getRawMap() {
        return data;
    }

    public boolean addItem(Item item) {
        return item.give(this);
    }

}
