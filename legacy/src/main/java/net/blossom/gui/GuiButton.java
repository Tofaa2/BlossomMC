package net.blossom.gui;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record GuiButton(@NotNull ItemStack icon, @Nullable Consumer<InventoryPreClickEvent> clickHandler) {
}