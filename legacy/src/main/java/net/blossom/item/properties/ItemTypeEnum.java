package net.blossom.item.properties;

import net.blossom.item.Item;

import java.lang.reflect.Constructor;

public enum ItemTypeEnum {

    WEAPON(Item.class),
    ARMOR(Item.class),
    ACCESSORY(Item.class),
    CONSUMABLE(Item.class),
    MATERIAL(Item.class),
    BLOCK(Item.class),
    TOOL(Item.class),
    MISC(Item.class);

    private final Class<? extends Item> itemClass;

    ItemTypeEnum(Class<? extends Item> itemClass) {
        this.itemClass = itemClass;
    }

    public String getDisplayName() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }

}
