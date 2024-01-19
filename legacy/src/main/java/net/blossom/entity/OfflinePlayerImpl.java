package net.blossom.entity;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.blossom.core.Feature;
import net.blossom.core.Rank;
import net.blossom.data.AbstractDataContainer;
import net.blossom.data.DataType;
import net.blossom.item.Item;
import net.blossom.region.RegionFeature;
import net.blossom.region.SimpleRegion;
import net.blossom.utils.JsonUtils;
import net.blossom.utils.SerializableCoordinate;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

import static net.blossom.core.Blossom.LOGGER;

final class OfflinePlayerImpl extends AbstractDataContainer implements OfflinePlayer {

    private final UUID uuid;
    private Rank rank = Rank.MEMBER;
    private SimpleRegion ownedRegion;
    private Pos home;
    private long discordId = -1L;
    private long firstLogin = -1L;
    private long lastQuit = -1L;
    private long playTime = 0L;

    OfflinePlayerImpl(final UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public @Nullable SimpleRegion getOwnedRegion() {
        return ownedRegion;
    }

    @Override
    public @Nullable Pos getHome() {
        return home;
    }

    @Override
    public @NotNull Rank getRank() {
        return rank;
    }

    @Override
    public @NotNull Collection<Item> getEquippedItems() {
        throw new UnsupportedOperationException("Cannot get equipped items of an offline player");
    }

    @Override
    public void setRank(@NotNull Rank rank) {
        this.rank = rank;
        save();
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

    @NotNull @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void save() {
        JsonObject data = new JsonObject();
        data.addProperty("uuid", uuid.toString());
        data.addProperty("rank", getRank().name());
        if (home != null) data.addProperty("home", JsonUtils.toJson(SerializableCoordinate.fromPos(getHome())));
        if (ownedRegion != null) data.addProperty("ownedRegion", ownedRegion.getUuid().toString());
        Map<String, Object> dataMap = new HashMap<>();
        for (var entry : this.data.entrySet()) {
            dataMap.put(entry.getKey().key().asString(), entry.getValue());
        }
        data.addProperty("data", JsonUtils.toJson(dataMap));
        File playerFile = Feature.getFeature(EntityFeature.class).getPlayerFile(this);
        JsonUtils.writeJson(playerFile, data, JsonObject.class);
    }

    @Override
    public void loadData(JsonObject data) {
        if (data == null) return;
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
        this.lastQuit = data.get("lastQuit").getAsLong();
        this.firstLogin = data.get("firstLogin").getAsLong();
        this.playTime = data.get("playTime").getAsLong();
        this.discordId = data.get("discordId").getAsLong();
    }

    @Override
    public void sendMessage(@NotNull String message, boolean prefix) {
        throw new UnsupportedOperationException("Cannot message an offline player");
    }

    @Override
    public void sendMessage(@NotNull String message) {
        throw new UnsupportedOperationException("Cannot message an offline player");
    }
}
