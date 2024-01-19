package net.blossom.item;

public enum ItemType {

    WEAPON,
    ARMOR,
    ACCESSORY,
    CONSUMABLE,
    MATERIAL,
    BLOCK,
    TOOL,
    MISC;

    public String getDisplayName() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }

}
