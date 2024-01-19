package net.blossom.item.type;

import net.blossom.data.DataType;
import net.blossom.item.BlossomItem;
import net.blossom.item.ItemRarity;
import net.blossom.item.ItemType;
import net.blossom.player.BlossomPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;

public class ToolItem extends BlossomItem {


    protected ToolItem(String id, String name, String description, Material material, ItemRarity rarity, Map<DataType<?>, Object> data) {
        super(id, name, description, material, rarity, ItemType.TOOL, data);
    }

    protected ToolItem(String id, String name, String description, Material material, ItemRarity rarity) {
        super(id, name, description, material, rarity, ItemType.TOOL);
    }

    public void onBreak(BlossomPlayer player, Block block) {

    }


}
