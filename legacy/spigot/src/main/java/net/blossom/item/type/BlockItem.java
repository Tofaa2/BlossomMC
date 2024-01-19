package net.blossom.item.type;

import net.blossom.item.BlossomItem;
import net.blossom.item.ItemRarity;
import net.blossom.item.ItemType;
import net.blossom.player.BlossomPlayer;
import org.bukkit.Material;

import java.util.function.Consumer;

public class BlockItem extends BlossomItem {

    private final Consumer<BlossomPlayer> onPlace;
    private final Consumer<BlossomPlayer> onBreak;


    public BlockItem(String id, String name, String description, Material material, ItemRarity rarity) {
        this(id, name, description, material, rarity, null, null);
    }

    public BlockItem(String id, String name, String description, Material material, ItemRarity rarity, Consumer<BlossomPlayer> onPlace, Consumer<BlossomPlayer> onBreak) {
        super(id, name, description, material, rarity, ItemType.BLOCK);
        this.onPlace = onPlace;
        this.onBreak = onBreak;
    }


    public void onPlace(BlossomPlayer player) {
        if (onPlace != null) {
            onPlace.accept(player);
        }
    }

    public void onBreak(BlossomPlayer player) {
        if (onBreak != null) {
            onBreak.accept(player);
        }
    }

}
