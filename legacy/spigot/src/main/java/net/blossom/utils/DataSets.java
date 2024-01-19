package net.blossom.utils;


import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Map.entry;

public final class DataSets {

    private DataSets() {}

    public static ItemStack clearAttackSpeed(ItemStack itemStack) {
        if (!ATTACK_SPEEDS.containsKey(itemStack.getType())) {
            return itemStack;
        }

        double num = ATTACK_SPEEDS.get(itemStack.getType());
        num = 4.0 - num;
        ItemMeta meta = itemStack.getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", num, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static final Set<Material> LOGS = Set.of(
            Material.OAK_LOG,
            Material.ACACIA_LOG,
            Material.JUNGLE_LOG,
            Material.BIRCH_LOG,
            Material.CHERRY_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.SPRUCE_LOG,
            Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_CHERRY_LOG,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_MANGROVE_LOG,
            Material.STRIPPED_SPRUCE_LOG
    );

    public static final Set<Material> WOOD_LOGS = Set.of(
            Material.OAK_LOG,
            Material.ACACIA_LOG,
            Material.JUNGLE_LOG,
            Material.BIRCH_LOG,
            Material.CHERRY_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.SPRUCE_LOG
    );

    public static final Map<Material, Double> ATTACK_SPEEDS = Map.ofEntries(
            entry(Material.WOODEN_SWORD, 1.6),
            entry(Material.STONE_SWORD, 1.6),
            entry(Material.IRON_SWORD, 1.6),
            entry(Material.GOLDEN_SWORD, 1.6),
            entry(Material.DIAMOND_SWORD, 1.6),
            entry(Material.NETHERITE_SWORD, 1.6),
            entry(Material.WOODEN_AXE, 0.8),
            entry(Material.STONE_AXE, 0.8),
            entry(Material.IRON_AXE, 0.9),
            entry(Material.GOLDEN_AXE, 1.0),
            entry(Material.DIAMOND_AXE, 1.0),
            entry(Material.NETHERITE_AXE, 1.0),
            entry(Material.WOODEN_PICKAXE, 1.2),
            entry(Material.STONE_PICKAXE, 1.2),
            entry(Material.IRON_PICKAXE, 1.2),
            entry(Material.GOLDEN_PICKAXE, 1.2),
            entry(Material.DIAMOND_PICKAXE, 1.2),
            entry(Material.NETHERITE_PICKAXE, 1.2),
            entry(Material.WOODEN_SHOVEL, 1.0),
            entry(Material.STONE_SHOVEL, 1.0),
            entry(Material.IRON_SHOVEL, 1.0),
            entry(Material.GOLDEN_SHOVEL, 1.0),
            entry(Material.DIAMOND_SHOVEL, 1.0),
            entry(Material.NETHERITE_SHOVEL, 1.0),
            entry(Material.WOODEN_HOE, 1.0),
            entry(Material.STONE_HOE, 1.0),
            entry(Material.IRON_HOE, 1.0),
            entry(Material.GOLDEN_HOE, 1.0),
            entry(Material.DIAMOND_HOE, 1.0),
            entry(Material.NETHERITE_HOE, 1.0),
            entry(Material.TRIDENT, 1.1)
    );


}
