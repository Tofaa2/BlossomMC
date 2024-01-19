package net.blossom.core.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.blossom.core.utils.ComponentUtils.normal;

public record GuiButton(@NotNull ItemStack icon, @Nullable GuiButtonClickHandler clickHandler) {

    public static final GuiButton FILLER = new GuiButton(
            ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.empty())
                    .meta(meta -> {
                        meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES);
                    })
                    .build(),
            null
    );

    public static final GuiButton RETURN = new GuiButton(
            ItemStack.builder(Material.BARRIER).displayName(normal("Return", NamedTextColor.RED))
                    .meta(meta -> {
                        meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES);
                    })
                    .build(),
            (gui, player, clickType) -> {
                Gui parent = gui.getParent();
                if (parent != null) {
                    parent.open(player);
                }
                else {
                    gui.close(player);
                }
            }
    );

    @FunctionalInterface
    public interface GuiButtonClickHandler {
        void onClick(@NotNull Gui gui, @NotNull Player player, @NotNull ClickType clickType);
    }


}