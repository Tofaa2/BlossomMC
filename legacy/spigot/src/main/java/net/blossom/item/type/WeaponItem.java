package net.blossom.item.type;

import net.blossom.item.BlossomItem;
import net.blossom.item.ItemRarity;
import net.blossom.item.ItemType;
import net.blossom.data.DataType;
import org.bukkit.Material;

import java.util.Map;

public class WeaponItem extends BlossomItem {

    public WeaponItem(String id, String name, String description, Material material, ItemRarity rarity) {
        super(id, name, description, material, rarity, ItemType.WEAPON);
    }

    public WeaponItem(String id, String name, String description, Material material, ItemRarity rarity, Map<DataType<?>, Object> data) {
        super(id, name, description, material, rarity, ItemType.WEAPON, data);
    }

    public void onAttack() {

    }


}
