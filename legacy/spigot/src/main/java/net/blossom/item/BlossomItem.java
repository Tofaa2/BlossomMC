package net.blossom.item;

import net.blossom.ability.Ability;
import net.blossom.ability.AbilityHolder;
import net.blossom.data.AbstractDataContainer;
import net.blossom.item.type.BlockItem;
import net.blossom.utils.DataSets;
import net.blossom.utils.DescriptionParser;
import net.blossom.utils.EnumUtils;
import net.blossom.core.Blossom;
import net.blossom.data.DataType;
import net.blossom.player.BlossomPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BlossomItem extends AbstractDataContainer implements BlossomItems, AbilityHolder {

    private static final NamespacedKey ID_KEY = new NamespacedKey(Blossom.getPlugin(), "item_id");
    protected static final Component  INFO_HEADER = Blossom.getChatManager().pure("<gray>---------- <gold><bold>INFO</bold> <gray>----------");
    protected static final Component DESCRIPTION_HEADER = Blossom.getChatManager().pure("<gray>---------- <gold><bold>DESCRIPTION</bold> <gray>----------");
    protected static final Component EXTRAS_HEADER = Blossom.getChatManager().pure("<gray>---------- <gold><bold>EXTRAS</bold> <gray>----------");
    protected static final Component ABILITY_HEADER = Blossom.getChatManager().pure("<gray>---------- <gold><bold>ABILITIES</bold> <gray>----------");

    public static @Nullable BlossomItem fromItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return null;
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String id = container.get(ID_KEY, PersistentDataType.STRING);
        if (id == null) return null;
        return Blossom.getItemManager().getFromId(id);
    }


    /**
     * TODO: For now, we can only detect vanilla blocks
     * @param block The block to check
     * @return The item that represents the block, or null if it doesn't exist
     */
    public static @Nullable BlockItem fromBlock(@NotNull Block block) {
        Material material = block.getType();
        return Blossom.getItemManager().getFromId(material.getKey().getKey(), BlockItem.class);
    }

    private final String id;
    private final String name;
    private final String description;
    private final ItemStack linkedItem;
    private final ItemRarity rarity;
    private final ItemType type;
    private final Ability[] ability;

    protected BlossomItem(String id, String name, String description, Material material, ItemRarity rarity, ItemType type, Map<DataType<?>, Object> data, Ability... ability) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.type = type;
        this.ability = ability;
        this.linkedItem = new ItemStack(material);
        if (data != null) {
            setData(data);
        }
        ItemMeta meta = linkedItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(ID_KEY, PersistentDataType.STRING, id);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        Float da = getData(DataType.ATTACK_SPEED);
        if (!da.equals(DataType.ATTACK_SPEED.defaultValue())) {
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -(4.0 - da), AttributeModifier.Operation.ADD_NUMBER, material.getEquipmentSlot()));
        }
        linkedItem.setItemMeta(meta);
        DataSets.clearAttackSpeed(linkedItem);
        refreshLore();
    }

    protected BlossomItem(String id, String name, String description, Material material, ItemRarity rarity, ItemType type, Map<DataType<?>, Object> data) {
        this(id, name, description, material, rarity, type, data, null);
    }

    protected BlossomItem(String id, String name, String description, Material material, ItemRarity rarity, ItemType type) {
        this(id, name, description, material, rarity, type, null, null);
    }

    public void refreshLore() {
        ItemMeta meta = linkedItem.getItemMeta();
        List<Component> lore = new ArrayList<>();
        meta.displayName(Blossom.getChatManager().pure(name));
        lore.add(INFO_HEADER);
        lore.add(Blossom.getChatManager().pure("<gray>Rarity: ").append(rarity.colorize()));
        lore.add(Blossom.getChatManager().pure("<gray>Type: <gold>" + type.getDisplayName()));
        lore.add(Component.empty());
        lore.add(DESCRIPTION_HEADER);
        lore.addAll(DescriptionParser.parse(description));
        if (ability != null) {
            int index = 0;
            for (var a: ability) {
                lore.add(Component.empty());
                lore.add(ABILITY_HEADER);
                lore.addAll(DescriptionParser.parse(a.getDescription()));
                lore.add(Blossom.getChatManager().pure("<gold>" + EnumUtils.prettyString(a.getTrigger()) + " <gray>to activate"));
                index++;
                if (index < ability.length) lore.add(Component.empty());
            }
         }
        if (!data.isEmpty()) {
            lore.add(Component.empty());
            lore.add(EXTRAS_HEADER);
            for (var entry : data.entrySet()) {
                var type = entry.getKey();
                var value = getData(type);
                if (value.equals(type.defaultValue())) continue;
                lore.add(Blossom.getChatManager().pure("<white>" + EnumUtils.prettyString(type.key().getKey()) + ": <gold>" + value));
            }
        }
        meta.lore(lore);
        linkedItem.setItemMeta(meta);
    }

    @Nullable @Override
    public Ability[] getAbilities() {
        return ability;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public @NotNull ItemStack getLinkedItem() {
        return linkedItem;
    }

    public void give(Player player) {
        player.getInventory().addItem(linkedItem.clone());
    }

    public void give(Player player, int amount) {
        ItemStack clone = linkedItem.clone();
        clone.setAmount(amount);
        player.getInventory().addItem(clone);
    }

    public void onEquip(BlossomPlayer player) {
        player.handleEquip(this);
    }

    public void onUnequip(BlossomPlayer player) {
        player.handleUnequip(this);
    }

    public void onHandChange(BlossomPlayer player, boolean offhand) {
        player.refreshEquipment();
    }

    @Override
    public <T> void setData(@NotNull DataType<T> type, @NotNull T data) {
        if (
                type.equals(DataType.LEVEL)
                || type.equals(DataType.EXPERIENCE)
                || type.equals(DataType.HEALTH)
        ) {
            throw new IllegalArgumentException("Cannot set " + type + " on an item");
        }
        super.setData(type, data);
    }
}
