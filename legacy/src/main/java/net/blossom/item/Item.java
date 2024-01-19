package net.blossom.item;

import net.blossom.chat.ChatFeature;
import net.blossom.core.Feature;
import net.blossom.item.properties.ItemRarity;
import net.blossom.item.types.EnchantableItem;
import net.blossom.entity.BlossomPlayer;
import net.blossom.utils.DescriptionParser;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagWritable;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Item implements Items{

    public static Item fromItemStack(ItemStack stack) {
        Item base = Registry.get(stack);
        if (base == null) return null;
        int amount = stack.amount();
        base = base.withAmount(amount);
        if (base instanceof EnchantableItem ench) {
            ench.setEnchantments(EnchantableItem.getEnchants(stack));
        }
        return base;
    }

    protected static final Component INFO_HEADER;
    protected static final Component DESCRIPTION_HEADER;
    protected static final Component EXTRAS_HEADER;
    protected static final Component ABILITY_HEADER;
    protected static final Component ENCHANTMENT_HEADER;
    static {
        ChatFeature f = Feature.getFeature(ChatFeature.class);
        INFO_HEADER = f.pure("<dark_gray>---------- <gold><bold>INFO</bold> <dark_gray>----------");
        DESCRIPTION_HEADER = f.pure("<dark_gray>---------- <gold><bold>DESCRIPTION</bold> <dark_gray>----------");
        EXTRAS_HEADER = f.pure("<dark_gray>---------- <gold><bold>EXTRAS</bold> <dark_gray>----------");
        ABILITY_HEADER = f.pure("<dark_gray>---------- <gold><bold>ABILITIES</bold> <dark_gray>----------");
        ENCHANTMENT_HEADER = f.pure("<dark_gray>---------- <gold><bold>ENCHANTMENTS</bold> <dark_gray>----------");
    }

    private final NamespaceID id;
    private final Material material;
    private final String name, description;
    private final ItemType<?> type;
    private final ItemRarity rarity;
    private final int amount;
    protected ItemStack stack;

    public Item(NamespaceID id, Material material, String name, String description, ItemType<?> type, ItemRarity rarity, int amount) {
        Check.stateCondition(material == Material.AIR, "Material cannot be AIR");
        Check.stateCondition(amount == 0, "Amount cannot be 0");
        this.id = id;
        this.material = material;
        this.name = name;
        this.description = description;
        this.type = type;
        this.rarity = rarity;
        this.amount = amount;
    }

    public NamespaceID getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemType<?> getType() {
        return type;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public int getAmount() {
        return amount;
    }

    public Item withAmount(int amount) {
        return new Item(id, material, name, description, type, rarity, amount);
    }

    public ItemStack toItemStack() {
        return toItemStack(true);
    }

    public ItemStack toItemStack(boolean restack) {
        if (restack || stack == null) restack();
        return stack;
    }

    public boolean give(BlossomPlayer player) {
        return player.getInventory().addItemStack(toItemStack());
    }

    public void restack() {
        this.stack = ItemStack.builder(material)
                .amount(amount)
                .displayName(Feature.getFeature(ChatFeature.class).pure(name))
                .meta(meta -> {
                    meta.hideFlag(
                            ItemHideFlag.HIDE_ATTRIBUTES,
                            ItemHideFlag.HIDE_ENCHANTS
                    );

                    if (shouldStackGlow()) {
                        meta.enchantment(Enchantment.AQUA_AFFINITY, (short)1);
                    }

                    meta.lore(createLore());
                    Set<TagContainer<?>> tags = createTags();
                    if (tags != null) {
                        for (var container : tags) {
                            container.apply(meta);
                        }
                    }
                    meta.setTag(Registry.ITEM_ID, id.asString());
                })
                .build();
    }

    protected @NotNull List<Component> createLore() {
        ChatFeature f = Feature.getFeature(ChatFeature.class);
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(INFO_HEADER);
        lore.add(f.pure("<gray>Rarity: ").append(rarity.colorize()));
        lore.add(f.pure("<gray>Type: <gold>" + type.getDisplayName()));
        lore.add(Component.empty());
        lore.add(DESCRIPTION_HEADER);
        lore.addAll(DescriptionParser.parse(description));
        return lore;
    }

    protected @Nullable Set<TagContainer<?>> createTags() {
        return null;
    }

    protected boolean shouldStackGlow() {
        return false;
    }

    public record TagContainer<T>(Tag<T> tag, T value) {

        public void apply(TagWritable meta) {
            meta.setTag(tag, value);
        }

    }

}
