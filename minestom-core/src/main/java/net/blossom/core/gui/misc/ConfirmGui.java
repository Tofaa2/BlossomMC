package net.blossom.core.gui.misc;

import net.blossom.core.gui.Gui;
import net.blossom.core.gui.GuiButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.blossom.core.utils.ComponentUtils.normal;

public final class ConfirmGui {

    public static final ItemStack CONFIRM_ITEM = ItemStack.builder(Material.GREEN_WOOL)
            .displayName(normal("Confirm", NamedTextColor.GREEN))
            .build();

    public static final ItemStack CANCEL_ITEM = ItemStack.builder(Material.RED_WOOL)
            .displayName(normal("Cancel", NamedTextColor.RED))
            .build();


    public static @NotNull Gui create(
            @NotNull Component title,
            @NotNull GuiButton.GuiButtonClickHandler onConfirm,
            @NotNull GuiButton.GuiButtonClickHandler onCancel,
            @Nullable Gui parent
    ) {
        Gui.Builder builder = Gui.builder(InventoryType.CHEST_6_ROW, title)
                .withParent(parent);
        builder.withFiller(GuiButton.FILLER);
        builder.withButton(21, new GuiButton(CONFIRM_ITEM, onConfirm));
        builder.withButton(23, new GuiButton(CANCEL_ITEM, onCancel));
        if (parent != null) {
            builder.withButton(49, GuiButton.RETURN);
        }
        return builder.build();
    }


}
