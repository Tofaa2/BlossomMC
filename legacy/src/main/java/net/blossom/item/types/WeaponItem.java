package net.blossom.item.types;

import net.blossom.data.DataType;
import net.blossom.item.ItemType;
import net.blossom.item.properties.ItemRarity;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;

import java.util.Map;

public class WeaponItem extends EnchantableItem {

    public WeaponItem(NamespaceID id, Material material, String name, String description, ItemRarity rarity, int amount, Map<DataType<?>, Object> data) {
        super(id, material, name, description, ItemType.WEAPON, rarity, amount, data);
    }

    @Override
    public WeaponItem withAmount(int amount) {
        return new WeaponItem(getId(), getMaterial(), getName(), getDescription(), getRarity(), amount, getDataMapCopy());
    }
}
