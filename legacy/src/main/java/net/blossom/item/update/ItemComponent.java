package net.blossom.item.update;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface ItemComponent {

    void apply(@NotNull ItemStack.Builder builder, @NotNull ArrayList<Component> lore);

}
