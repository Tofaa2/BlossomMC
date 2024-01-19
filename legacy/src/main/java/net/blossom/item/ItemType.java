package net.blossom.item;

import net.blossom.item.types.WeaponItem;
import net.minestom.server.item.Material;

import java.util.Set;

public final class ItemType<T extends Item> {

    public static ItemType<WeaponItem> WEAPON = new ItemType<>(WeaponItem.class, "Weapon");
    public static ItemType<Item> MATERIAL = new ItemType<>(Item.class, "Material");

    private final Class<T> itemClass;
    private final String displayName;
    private final Set<Material> breakableMaterials;

    private ItemType(Class<T> itemClass, String displayName) {
        this.itemClass = itemClass;
        this.displayName = displayName;
        this.breakableMaterials = Set.of();
    }

    private ItemType(Class<T> itemClass, String displayName, Set<Material> breakableMaterials) {
        this.itemClass = itemClass;
        this.displayName = displayName;
        this.breakableMaterials = breakableMaterials;
    }

    public boolean canBreak(Material material) {
        return breakableMaterials.contains(material);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<T> getItemClass() {
        return itemClass;
    }

}
