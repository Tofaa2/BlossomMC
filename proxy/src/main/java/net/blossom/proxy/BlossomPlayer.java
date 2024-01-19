package net.blossom.proxy;

import com.velocitypowered.api.proxy.Player;
import net.blossom.proxy.punishments.Punishment;
import net.blossom.proxy.punishments.PunishmentType;
import net.blossom.proxy.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class BlossomPlayer {

    private final UUID uuid;
    private final String username;
    private final Document document;
    private List<Punishment> punishmentHistory;
    private List<UUID> friends;

    public BlossomPlayer(UUID uuid, String username, Document document) {
        this.uuid = uuid;
        this.document = document;
        this.punishmentHistory = new ArrayList<>(0);
        this.friends = new ArrayList<>(0);
        if (document.containsKey("punishments")) {
            this.punishmentHistory.addAll(document.getList("punishments", Punishment.class));
        }
        if (document.containsKey("friends")) {
            this.friends.addAll(document.getList("friends", UUID.class));
        }
        if (username == null) {
            this.username = Utils.getUsername(uuid.toString());
        }
        else {
            this.username = username;
        }
        this.document.put("last-known-username", this.username);
    }

    public Document getDocument() {
        return new Document(document);
    }

    public void warn(@NotNull String reason, @NotNull UUID issuer) {
        addPunishment(new Punishment(UUID.randomUUID(), System.currentTimeMillis(), -1L, reason, issuer, PunishmentType.WARN));
        if (isOnline()) {
            getPlayer().sendMessage(Component.text("You have been warned for ", NamedTextColor.RED).append(Component.text(reason, NamedTextColor.WHITE)));
        }
    }

    public Player getPlayer() {
        return BlossomProxy.getInstance().getServer().getPlayer(uuid).orElse(null);
    }

    public boolean isOnline() {
        return BlossomProxy.getInstance().getServer().getPlayer(uuid).isPresent();
    }

    public String getUsername() {
        return username;
    }

    public void addFriend(@NotNull UUID friend) {
        this.friends.add(friend);
        this.document.put("friends", this.friends);
    }

    public void removeFriend(@NotNull UUID friend) {
        this.friends.remove(friend);
        this.document.put("friends", this.friends);
    }

    public boolean isFriend(@NotNull UUID friend) {
        return this.friends.contains(friend);
    }

    public Collection<UUID> getFriends() {
        return List.copyOf(this.friends);
    }

    public boolean isMutualFriend(@NotNull UUID friend) {
        return this.friends.contains(friend) && BlossomProxy.getInstance().getPlayer(friend).isFriend(this.uuid);
    }

    public void addPunishment(@NotNull Punishment punishment) {
        this.punishmentHistory.add(punishment);
        punishment.type().onPunish(BlossomProxy.getInstance().getServer().getPlayer(uuid).orElseThrow(), punishment);
        this.document.put("punishments", this.punishmentHistory);
    }

    public Collection<Punishment> getPunishmentsByType(PunishmentType type) {
        List<Punishment> punishments = new ArrayList<>(0);
        for (Punishment punishment : this.punishmentHistory) {
            if (punishment.type() == type) {
                punishments.add(punishment);
            }
        }
        return punishments;
    }

}
