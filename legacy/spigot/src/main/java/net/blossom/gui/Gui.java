package net.blossom.gui;

import net.blossom.core.Blossom;
import net.blossom.player.BlossomPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Gui implements InventoryHolder {

    static {
        Blossom.registerBukkitListener(new Listener() {

            @EventHandler
            public void onClose(InventoryCloseEvent event) {
                if (event.getInventory().getHolder() instanceof Gui gui) {
                    gui.close((Player) event.getPlayer());
                }
            }

            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (event.getInventory().getHolder() instanceof Gui gui) {
                    GuiButton button = gui.getButton(event.getSlot());
                    if (button == null) return;
                    if (button.shouldCancelClick()) event.setCancelled(true);
                    button.getCallback().onClick(gui, BlossomPlayer.of((Player) event.getWhoClicked()), event.getClick());
                }
            }
        });
    }

    private final Component title;
    private final Map<Integer, GuiButton> buttons;
    private final Inventory inventory;
    private GuiCloseCallback[] closeCallbacks;

    public Gui(@NotNull Component title, int rows) {
        this.title = title;
        this.buttons = new HashMap<>();
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    public Gui(@NotNull String title, int rows) {
        this(Blossom.getChatManager().createMessage(title, false), rows);
    }


    public Component getTitle() {
        return title;
    }

    public void addButton(@NotNull GuiButton button, int slot) {
        this.buttons.put(slot, button);
    }

    public void addButton(@NotNull GuiButton button, int x, int y) {
        this.buttons.put(x + y * 9, button);
    }

    public void refresh() {
        this.inventory.clear();
        for (Map.Entry<Integer, GuiButton> entry : this.buttons.entrySet()) {
            this.inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
    }

    public @Nullable GuiButton getButton(int slot) {
        return this.buttons.get(slot);
    }

    public @Nullable GuiButton getButton(int x, int y) {
        return this.buttons.get(x + y * 9);
    }

    public void open(@NotNull Player player) {
        player.openInventory(this.inventory);
    }

    public void open(@NotNull BlossomPlayer player) {
        open(player.getPlayer());
    }

    public void close(@NotNull Player player) {
        player.closeInventory();
        if (this.closeCallbacks != null) {
            for (GuiCloseCallback callback : this.closeCallbacks) {
                callback.onClose(this, BlossomPlayer.of(player));
            }
        }
    }


    public void close(@NotNull BlossomPlayer player) {
        player.getPlayer().closeInventory();
        if (this.closeCallbacks != null) {
            for (GuiCloseCallback callback : this.closeCallbacks) {
                callback.onClose(this, player);
            }
        }
    }

    public void addCloseCallback(@NotNull GuiCloseCallback callback) {
        if (this.closeCallbacks == null) {
            this.closeCallbacks = new GuiCloseCallback[1];
            this.closeCallbacks[0] = callback;
        }
        else {
            GuiCloseCallback[] newCallbacks = new GuiCloseCallback[this.closeCallbacks.length + 1];
            System.arraycopy(this.closeCallbacks, 0, newCallbacks, 0, this.closeCallbacks.length);
            newCallbacks[this.closeCallbacks.length] = callback;
            this.closeCallbacks = newCallbacks;
        }
    }

    public void clearCloseCallbacks() {
        this.closeCallbacks = null;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
