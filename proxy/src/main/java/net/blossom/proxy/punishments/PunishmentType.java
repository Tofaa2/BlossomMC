package net.blossom.proxy.punishments;

import com.velocitypowered.api.proxy.Player;

import java.util.function.BiConsumer;

public enum PunishmentType {

    KICK,
    MUTE,
    BAN,
    BLACKLIST,
    WARN;

    private BiConsumer<Player, Punishment> onPunish;

    PunishmentType(BiConsumer<Player, Punishment> onPunish) {
        this.onPunish = onPunish;
    }

    PunishmentType() {
        this.onPunish = null;
    }

    public void onPunish(Player player, Punishment punishment) {
        if (this.onPunish != null) {
            this.onPunish.accept(player, punishment);
        }
    }

}
