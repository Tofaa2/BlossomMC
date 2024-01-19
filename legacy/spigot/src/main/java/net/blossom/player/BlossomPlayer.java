package net.blossom.player;

import net.blossom.ability.Ability;
import net.blossom.ability.AbilityHolder;
import net.blossom.data.AbstractDataContainer;
import net.blossom.item.BlossomItem;
import net.blossom.utils.SerializableLocation;
import net.blossom.utils.UnicodeCharacters;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.blossom.core.Blossom;
import net.blossom.data.DataType;
import net.blossom.utils.DoNotSerialize;
import net.blossom.utils.JsonUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BlossomPlayer extends AbstractDataContainer implements AbilityHolder {

    public static BlossomPlayer of(final UUID uuid) {
        return Blossom.getPlayerManager().getOrCreatePlayer(uuid);
    }

    public static BlossomPlayer of(final Player player) {
        return Blossom.getPlayerManager().getOrCreatePlayer(player.getUniqueId());
    }

    private final UUID uuid;
    private Rank rank = Rank.MEMBER;
    private long balance = 0L;
    private Set<UUID> friends = new HashSet<>();
    private Set<UUID> blocked = new HashSet<>();
    private int lives = 20;
    private SerializableLocation home;
    @DoNotSerialize private final ScoreboardWrapper scoreboard;
    @DoNotSerialize private final Map<DataType<?>, Object> bonusData = new HashMap<>();

    public BlossomPlayer(final UUID uuid) {
        this.uuid = uuid;
        this.scoreboard = new ScoreboardWrapper(this);
        Blossom.sync(this::syncLogin, 25L);
    }

    private void syncLogin() {
        final Player player = getPlayer();
        scoreboard.enable();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(lives * 2);
        player.setHealth(lives * 2);
    }

    public void asyncTick() {
        scoreboard.tick();
        sendActionBar("<red>" + getHealth() + " <gray>/ <red>" + getMaxHealth() + UnicodeCharacters.HEART_ICON);
    }

    public void asyncLeave() {
        scoreboard.disable();
    }

    public void sendActionBar(String message) {
        final Player player = getPlayer();
        if (player == null) return;
        player.sendActionBar(Blossom.getChatManager().createMessage(message, false));
    }

    public void refreshEquipment() {
        this.bonusData.clear();
        handleEquip(BlossomItem.fromItemStack(getPlayer().getInventory().getItemInMainHand()));
        handleEquip(BlossomItem.fromItemStack(getPlayer().getInventory().getItemInOffHand()));
        handleEquip(BlossomItem.fromItemStack(getPlayer().getInventory().getHelmet()));
        handleEquip(BlossomItem.fromItemStack(getPlayer().getInventory().getChestplate()));
        handleEquip(BlossomItem.fromItemStack(getPlayer().getInventory().getLeggings()));
        handleEquip(BlossomItem.fromItemStack(getPlayer().getInventory().getBoots()));
    }


    public void refreshEquipmentItem(BlossomItem item) {
        if (item != null) {
            handleUnequip(item);
            handleEquip(item);
        }
    }

    public void handleEquip(BlossomItem item) {
        if (item == null) return;
        Double health = item.getData(DataType.MAX_HEALTH, false);
        if (health == null) health = 0D;
        Double currentHealth = getBonusData(DataType.MAX_HEALTH);
        if (currentHealth == null) currentHealth = 0D;
        setBonusData(DataType.MAX_HEALTH, currentHealth + health);
    }

    public void handleUnequip(BlossomItem item) {
        if (item == null) return;
        Double health = item.getData(DataType.MAX_HEALTH, false);
        if (health == null) health = 0D;
        Double currentHealth = getBonusData(DataType.MAX_HEALTH);
        if (currentHealth == null) currentHealth = 0.0D;
        setBonusData(DataType.MAX_HEALTH, currentHealth - health);
    }

    public void teleportHome() {
        if (home == null) {
            sendMessage("<red>You do not have a home set!");
            return;
        }
        getPlayer().teleport(home.toLocation());
    }

    public void setHome(Location location) {
        this.home = SerializableLocation.fromLocation(location);
        sendMessage("<green>You have set your home!");
    }

    public void setHome() {
        setHome(getPlayer().getLocation());
    }

    public void deleteHome() {
        if (home == null) {
            sendMessage("<red>You do not have a home set!");
            return;
        }
        this.home = null;
        sendMessage("<green>You have deleted your home!");
    }

    public World getWorld() {
        return getPlayer().getWorld();
    }

    public Rank getRank() {
        return rank;
    }

    public long getBalance() {
        return balance;
    }

    public void sendMessage(String message, boolean prefix) {
        Blossom.getChatManager().sendMessage(getPlayer(), message, prefix);
    }

    public void sendMessage(String message) {
        sendMessage(message, true);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Set<BlossomPlayer> getWhoBlockedThisUser() {
        Set<BlossomPlayer> players = new HashSet<>();
        for (final org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            BlossomPlayer bp = of(player.getUniqueId());
            if (bp.blocked.contains(this.uuid)) {
                players.add(bp);
            }
        }
        return players;
    }

    public Set<Player> getWhoCanReceiveMessages() {
        Set<Player> players = new HashSet<>();
        for (final org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            BlossomPlayer bp = of(player.getUniqueId());
            if (!bp.blocked.contains(this.uuid)) {
                players.add(player);
            }
        }
        return players;
    }

    public void setLives(int lives) {
        this.lives = lives;
        final Player player = getPlayer();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(lives * 2);
        player.setHealth(lives * 2);
    }

    public int getLives() {
        return lives;
    }

    public void setHealth(double  health) {
        setData(DataType.HEALTH, health);
        if (getHealth() <= 0) {
            final Player player = getPlayer();
            ItemStack[] inventory = Arrays.copyOf(player.getInventory().getContents(), player.getInventory().getContents().length);
            player.getInventory().clear();
            setHealth(getMaxHealth());
            setLives(getLives() - 1);
            player.clearActivePotionEffects();
            if (getLives() == 0) {
                setLives(20);
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                sendMessage("<red>You have died and ran out of lives, good luck next time!");
            }
            else {
                Location home = player.getBedSpawnLocation();
                if (home == null) {
                    home = Bukkit.getWorlds().get(0).getSpawnLocation();
                }
                player.teleport(home);
                player.getInventory().setContents(inventory);
            }
        }
    }

    public void subtractHealth(double amount) {
        setHealth(getHealth() - amount);
    }

    public void addHealth(double amount) {
        setHealth(getHealth() + amount);
    }

    public double getHealth() {
        return getData(DataType.HEALTH);
    }

    public void setMaxHealth(double maxHealth, boolean heal) {
        setData(DataType.MAX_HEALTH, maxHealth);
        if (heal) {
            setHealth(maxHealth);
        }
    }

    private <T> void setBonusData(DataType<T> type, T value) {
        bonusData.put(type, value);
    }

    private <T> T getBonusData(DataType<T> type) {
        if (bonusData.containsKey(type)) {
            return (T) bonusData.get(type);
        }
        return null;
    }

    public double getMaxHealth() {
        Double extra = getBonusData(DataType.MAX_HEALTH);
        if (extra == null) extra = 0D;
        return getData(DataType.MAX_HEALTH) + extra;
    }

    public UUID getUUID() {
        return uuid;
    }

    public JsonObject toJson() {
        Gson gson = JsonUtils.GSON;
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());
        json.addProperty("rank", rank.name());
        json.addProperty("balance", balance);
        json.add("friends", gson.toJsonTree(friends));
        json.add("blocked", gson.toJsonTree(blocked));
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<DataType<?>, Object> entry : this.data.entrySet()) {
            data.put(entry.getKey().key().getKey(), entry.getValue());
        }
        json.add("data", gson.toJsonTree(data));
        json.addProperty("lives", lives);
        return json;
    }

    @SuppressWarnings("unchecked")
    public void load(JsonObject json) {
        Gson gson = JsonUtils.GSON;
        this.rank = Rank.valueOf(json.get("rank").getAsString());
        this.balance = json.get("balance").getAsLong();
        this.friends = gson.fromJson(json.get("friends"), Set.class);
        this.blocked = gson.fromJson(json.get("blocked"), Set.class);
        Map<String, Object> data = gson.fromJson(json.get("data"), Map.class);
        for (var entry : data.entrySet()) {
            DataType<?> type = DataType.getFromName(entry.getKey());
            if (type == null) {
                Blossom.getPlugin().getSLF4JLogger().warn("Unknown data type: " + entry.getKey());
                continue;
            }
            this.data.put(type, entry.getValue());
        }


        this.lives = json.get("lives").getAsInt();

    }

    @Override
    public @Nullable Ability[] getAbilities() {
        Player player = getPlayer();
        if (player == null) return null;
        List<Ability> abilities = new ArrayList<>();
        transferAbilities(BlossomItem.fromItemStack(player.getInventory().getItemInMainHand()), abilities);
        transferAbilities(BlossomItem.fromItemStack(player.getInventory().getItemInOffHand()), abilities);
        transferAbilities(BlossomItem.fromItemStack(player.getInventory().getHelmet()), abilities);
        transferAbilities(BlossomItem.fromItemStack(player.getInventory().getChestplate()), abilities);
        transferAbilities(BlossomItem.fromItemStack(player.getInventory().getLeggings()), abilities);
        transferAbilities(BlossomItem.fromItemStack(player.getInventory().getBoots()), abilities);
        if (abilities.isEmpty()) return null;
        return abilities.toArray(new Ability[0]);
    }


    private void transferAbilities(BlossomItem item, List<Ability> list) {
        if (item == null) return;
        Ability[] abilities = item.getAbilities();
        if (abilities == null) return;
        Collections.addAll(list, abilities);
    }
}
