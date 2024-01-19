package net.blossom.item.update.components;

import net.blossom.item.update.ItemComponent;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public record DataItemComponent(
) implements ItemComponent {




    @Override
    public void apply(ItemStack.@NotNull Builder builder, @NotNull ArrayList<Component> lore) {

    }
}
