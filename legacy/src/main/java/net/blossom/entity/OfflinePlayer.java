package net.blossom.entity;

import com.google.gson.JsonObject;
import net.blossom.core.Rank;
import net.blossom.data.DataContainer;
import net.blossom.item.Item;
import net.blossom.region.SimpleRegion;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface OfflinePlayer extends DataContainer {

    long getDiscordId();

    long getFirstLogin();

    long getLastQuitTime();

    long getPlayTime();

    @NotNull UUID getUuid();

    @Nullable SimpleRegion getOwnedRegion();

    @Nullable Pos getHome();

    @NotNull Rank getRank();

    @NotNull Collection<Item> getEquippedItems();

    void setRank(@NotNull Rank rank);

    void sendMessage(@NotNull String message, boolean prefix);

    void sendMessage(@NotNull String message);

    void save();

    void loadData(JsonObject data);

}
