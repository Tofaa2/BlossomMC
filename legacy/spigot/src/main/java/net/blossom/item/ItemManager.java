package net.blossom.item;

import net.blossom.item.type.ToolItem;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.blossom.core.Blossom;
import net.blossom.player.BlossomPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ItemManager {

    static BlossomItem register(BlossomItem item) {
        Blossom.getItemManager().baseItems.put(item.getId(), item);
        Blossom.getPlugin().getLogger().info("Registered item " + item.getId());
        return item;
    }

    private final Map<String, BlossomItem> baseItems = new HashMap<>();

    public ItemManager() {
        Blossom.registerBukkitListener(new Listener() {

            @EventHandler
            public void onSlotChange(PlayerItemHeldEvent event) {
                BlossomItem old = BlossomItem.fromItemStack(event.getPlayer().getInventory().getItem(event.getPreviousSlot()));
                if (old != null) {
                    old.onUnequip(BlossomPlayer.of(event.getPlayer()));
                }
                BlossomItem current = BlossomItem.fromItemStack(event.getPlayer().getInventory().getItem(event.getNewSlot()));
                if (current != null) {
                    current.onEquip(BlossomPlayer.of(event.getPlayer()));
                }
            }

            @EventHandler
            public void onHand(PlayerSwapHandItemsEvent event) {
                BlossomItem main = BlossomItem.fromItemStack(event.getMainHandItem());
                if (main != null) {
                    main.onHandChange(BlossomPlayer.of(event.getPlayer()), false);
                }
                BlossomItem off = BlossomItem.fromItemStack(event.getOffHandItem());
                if (off != null) {
                    off.onHandChange(BlossomPlayer.of(event.getPlayer()), true);
                }
            }

            @EventHandler
            public void onEquipmentChange(PlayerArmorChangeEvent event) {
                BlossomPlayer player = BlossomPlayer.of(event.getPlayer());
                BlossomItem old = BlossomItem.fromItemStack(event.getOldItem());
                if (old != null) {
                    old.onUnequip(player);
                }
                BlossomItem current = BlossomItem.fromItemStack(event.getNewItem());
                if (current != null) {
                    current.onEquip(player);
                }
            }

            @EventHandler
            public void onBlockBreak(BlockBreakEvent event) {
                BlossomPlayer player = BlossomPlayer.of(event.getPlayer());
                ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
                BlossomItem item = BlossomItem.fromItemStack(itemStack);
                if (item instanceof ToolItem tool) {
                    tool.onBreak(player, event.getBlock());
                }
            }
        });
    }

    public @Nullable BlossomItem getFromId(@NotNull String key) {
        return baseItems.get(key);
    }

    public @Nullable <T extends BlossomItem> T getFromId(@NotNull String key, @NotNull Class<T> clazz) {
        BlossomItem item = getFromId(key);
        if (item == null) return null;
        if (clazz.isAssignableFrom(item.getClass())) {
            return (T) item;
        }
        return null;
    }

    public Set<String> getItemIds() {
        return Set.copyOf(baseItems.keySet());
    }

    public void init() {
        BlossomItems.init();
    }

}
