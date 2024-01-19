package net.blossom.hub.features.cosmetics;

import net.blossom.core.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;

import java.util.Map;

import static net.blossom.commons.StringUtils.fancyName;

public enum CosmeticType {

    MOVEMENT(Material.ELYTRA),
    TRAIL(Material.BLAZE_POWDER),
    EMOTE(Material.EMERALD),
    HAT(Material.LEATHER_HELMET);


    private final Component fancyName;
    private final Material material;

    CosmeticType(Material material) {
        this.fancyName = ComponentUtils.normal(fancyName(name()));
        this.material = material;
    }

    public Component getFancyName() {
        return fancyName;
    }

    public Material getMaterial() {
        return material;
    }


    static final Map<Integer, CosmeticType> buttonValues = Map.of(
            10, MOVEMENT,
            12, TRAIL,
            14, EMOTE,
            16, HAT
    );


}
