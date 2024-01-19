package net.blossom.item.enchantment;

import net.blossom.item.types.EnchantableItem;
import net.blossom.entity.BlossomPlayer;
import net.minestom.server.event.trait.PlayerEvent;

import java.util.Set;

public record EnchantTrigger(Set<Class<? extends PlayerEvent>> events, Handle handle) {

    @FunctionalInterface
    public interface Handle {
        void handle(ItemEnchant enchant, BlossomPlayer player, EnchantableItem item);
    }

}
