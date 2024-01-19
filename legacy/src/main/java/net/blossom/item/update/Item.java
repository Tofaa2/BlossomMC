package net.blossom.item.update;

import net.blossom.item.properties.ItemRarity;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Item {

    @NotNull NamespaceID getId();

    @NotNull Material getMaterial();

    @NotNull ItemRarity getRarity();

    int getAmount();

    @NotNull Item withAmount(int amount);

    @NotNull Collection<ItemComponent> getComponents();

    @NotNull Item withComponent(@NotNull ItemComponent component);
}
