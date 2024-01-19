package net.blossom.item.impl;


import net.blossom.item.BlossomItem;
import net.blossom.item.ItemRarity;
import net.blossom.data.DataType;
import net.blossom.item.type.WeaponItem;
import org.bukkit.Material;

import java.util.Map;

public final class VanillaSwords extends WeaponItem  {

    public static BlossomItem wooden() {
        return new VanillaSwords(
                "wooden_sword",
                "Wooden Sword",
                Material.WOODEN_SWORD,
                4.0
        );
    }

    public static BlossomItem stone() {
        return new VanillaSwords(
                "stone_sword",
                "Stone Sword",
                Material.STONE_SWORD,
                5.0
        );
    }

    public static BlossomItem iron() {
        return new VanillaSwords(
                "iron_sword",
                "Iron Sword",
                Material.IRON_SWORD,
                6.0
        );
    }

    public static BlossomItem golden() {
        return new VanillaSwords(
                "golden_sword",
                "Golden Sword",
                Material.GOLDEN_SWORD,
                4.0
        );
    }

    public static BlossomItem diamond() {
        return new VanillaSwords(
                "diamond_sword",
                "Diamond Sword",
                Material.DIAMOND_SWORD,
                7.0
        );
    }

    public static BlossomItem netherite() {
        return new VanillaSwords(
                "netherite_sword",
                "Netherite Sword",
                Material.NETHERITE_SWORD,
                8.0
        );
    }

    private VanillaSwords(String id, String name, Material material, double damage) {
        super(id, name, "A common weapon used to damage monsters and enemies", material, ItemRarity.COMMON, Map.of(DataType.DAMAGE, damage));
    }
}
