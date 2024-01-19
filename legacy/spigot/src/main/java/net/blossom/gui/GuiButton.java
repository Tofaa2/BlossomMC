package net.blossom.gui;

import net.blossom.player.BlossomPlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class GuiButton {

    private final ItemStack itemStack;
    private final GuiClickCallback callback;
    private boolean shouldCancelClick;

    public GuiButton(@NotNull ItemStack itemStack, @NotNull GuiClickCallback callback, boolean shouldCancelClick) {
        this.itemStack = itemStack;
        this.callback = callback;
        this.shouldCancelClick = shouldCancelClick;
    }

    public GuiButton(@NotNull ItemStack itemStack, @NotNull GuiClickCallback callback) {
        this(itemStack, callback, true);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public GuiClickCallback getCallback() {
        return callback;
    }

    public boolean shouldCancelClick() {
        return shouldCancelClick;
    }

    public void setCancelClick(boolean shouldCancelClick) {
        this.shouldCancelClick = shouldCancelClick;
    }

    @FunctionalInterface
    public interface GuiClickCallback {

        void onClick(@NotNull Gui gui, @NotNull BlossomPlayer player, @NotNull ClickType clickType);

    }


}
