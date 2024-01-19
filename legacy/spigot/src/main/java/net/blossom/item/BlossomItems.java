package net.blossom.item;

import net.blossom.item.impl.AxePlus;
import net.blossom.core.Blossom;
import net.blossom.item.impl.VanillaSwords;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

interface BlossomItems {

    @NotNull BlossomItem WOODEN_SWORD = ItemManager.register(VanillaSwords.wooden());
    @NotNull BlossomItem STONE_SWORD = ItemManager.register(VanillaSwords.stone());
    @NotNull BlossomItem IRON_SWORD = ItemManager.register(VanillaSwords.iron());
    @NotNull BlossomItem GOLDEN_SWORD = ItemManager.register(VanillaSwords.golden());
    @NotNull BlossomItem DIAMOND_SWORD = ItemManager.register(VanillaSwords.diamond());
    @NotNull BlossomItem NETHERITE_SWORD = ItemManager.register(VanillaSwords.netherite());

    @NotNull BlossomItem WOODEN_AXE_PLUS = ItemManager.register(AxePlus.wooden());
    @NotNull BlossomItem STONE_AXE_PLUS = ItemManager.register(AxePlus.stone());
    @NotNull BlossomItem IRON_AXE_PLUS = ItemManager.register(AxePlus.iron());
    @NotNull BlossomItem GOLDEN_AXE_PLUS = ItemManager.register(AxePlus.golden());
    @NotNull BlossomItem DIAMOND_AXE_PLUS = ItemManager.register(AxePlus.diamond());
    @NotNull BlossomItem NETHERITE_AXE_PLUS = ItemManager.register(AxePlus.netherite());

    static void init() {
        Blossom.getItemManager().getItemIds().forEach(id -> {
            Blossom.getPlugin().getLogger().info("Loaded item " + id);
        });
    }

}
