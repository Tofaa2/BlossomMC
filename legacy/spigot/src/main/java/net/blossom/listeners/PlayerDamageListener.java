package net.blossom.listeners;

import net.blossom.utils.UnicodeCharacters;
import net.blossom.core.Blossom;
import net.blossom.data.DataType;
import net.blossom.item.BlossomItem;
import net.blossom.player.BlossomPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerDamageListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        double finalDamage = event.getFinalDamage();
        if (finalDamage != 0.0D) {
            double rounded = Math.round(finalDamage * 10.0D) / 10.0D;
            TextDisplay hologram = event.getEntity().getWorld().spawn(event.getEntity().getLocation().clone().add(0, 1, 0), TextDisplay.class);
            hologram.setSeeThrough(true);
            hologram.setGravity(false);
            hologram.text(Component.text(rounded + UnicodeCharacters.HEART_ICON, NamedTextColor.DARK_RED));
            hologram.setAlignment(TextDisplay.TextAlignment.CENTER);
            hologram.setBillboard(Display.Billboard.CENTER);
            Blossom.sync(hologram::remove, 20L);
        }
        if (!(event.getEntity() instanceof Player player)) return;
        BlossomPlayer bp = BlossomPlayer.of(player);
        bp.subtractHealth(finalDamage);
        player.sendMessage(finalDamage + " damage");
        event.setDamage(0);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity entity)) return;
        if (entity.getEquipment() == null) return;
        ItemStack held = entity.getEquipment().getItemInMainHand();
        BlossomItem item = BlossomItem.fromItemStack(held);
        if (item == null) return;
        event.setDamage(item.getData(DataType.DAMAGE));
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        BlossomPlayer bp = BlossomPlayer.of(player);
        bp.addHealth(event.getAmount());
    }

}
