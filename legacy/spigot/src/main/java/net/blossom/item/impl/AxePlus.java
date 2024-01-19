package net.blossom.item.impl;

import net.blossom.utils.DataSets;
import net.blossom.utils.FloatRange;
import net.blossom.data.DataType;
import net.blossom.item.BlossomItem;
import net.blossom.item.ItemRarity;
import net.blossom.item.type.ToolItem;
import net.blossom.player.BlossomPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public final class AxePlus extends ToolItem {

    public static BlossomItem wooden() {
        return new AxePlus(
                "wooden_axe_plus",
                "Wooden Axe+",
                "A wooden axe with a little extra power. Has the ability to drop more logs when breaking trees.",
                Material.WOODEN_AXE,
                ItemRarity.COMMON,
                Map.of(
                        DataType.DAMAGE, 7.0,
                        DataType.ATTACK_SPEED, 0.8f
                )
        );
    }

    public static BlossomItem stone() {
        return new AxePlus(
                "stone_axe_plus",
                "Stone Axe+",
                "A stone axe with a little extra power. Has the ability to drop more logs when breaking trees.",
                Material.STONE_AXE,
                ItemRarity.COMMON,
                Map.of(
                        DataType.DAMAGE, 9.0,
                        DataType.ATTACK_SPEED, 0.8f
                )
        );
    }

    public static BlossomItem iron() {
        return new AxePlus(
                "iron_axe_plus",
                "Iron Axe+",
                "An iron axe with a little extra power. Has the ability to drop more logs when breaking trees.",
                Material.IRON_AXE,
                ItemRarity.COMMON,
                Map.of(
                        DataType.DAMAGE, 9.0,
                        DataType.ATTACK_SPEED, 0.9f
                )
        );
    }

    public static BlossomItem golden() {
        return new AxePlus(
                "golden_axe_plus",
                "Golden Axe+",
                "A gold axe with a little extra power. Has the ability to drop more logs when breaking trees.",
                Material.GOLDEN_AXE,
                ItemRarity.COMMON,
                Map.of(
                        DataType.DAMAGE, 7.0,
                        DataType.ATTACK_SPEED, 1f
                )
        );
    }

    public static BlossomItem diamond() {
        return new AxePlus(
                "diamond_axe_plus",
                "Diamond Axe+",
                "A diamond axe with a little extra power. Has the ability to drop more logs when breaking trees.",
                Material.DIAMOND_AXE,
                ItemRarity.UNCOMMON,
                Map.of(
                        DataType.DAMAGE, 10.0,
                        DataType.ATTACK_SPEED, 1.0f
                )
        );
    }

    public static BlossomItem netherite() {
        return new AxePlus(
                "netherite_axe_plus",
                "Netherite Axe+",
                "A netherite axe with a little extra power. Has the ability to drop more logs when breaking trees.",
                Material.NETHERITE_AXE,
                ItemRarity.UNCOMMON,
                Map.of(
                        DataType.DAMAGE, 11.0,
                        DataType.ATTACK_SPEED, 1.0f
                )
        );
    }

    private AxePlus(String id, String name, String description, Material material, ItemRarity rarity, Map<DataType<?>, Object> data) {
        super(id, name, description, material, rarity, data);
    }


    @Override
    public void onBreak(BlossomPlayer player, Block block) {
        if (!DataSets.WOOD_LOGS.contains(block.getType())) return;
        Player p = player.getPlayer();
        if (p.getGameMode().equals(GameMode.CREATIVE)) return;
        super.onBreak(player, block);
        Collection<ItemStack> drops = block.getDrops(p.getInventory().getItemInMainHand());
        float chance = FloatRange.CHANCE_RANGE.roll();
        if (chance <= .5f) {
            player.sendMessage("<green>Your axe+ has dropped extra logs!");
            Location location = block.getLocation();
            for (ItemStack drop : drops) {
                location.getWorld().dropItemNaturally(location, drop);
            }
        }
    }

    @Override
    public void onEquip(BlossomPlayer player) {
        super.onEquip(player);
        player.sendMessage("<green>You equipped an axe+!");
    }
}
