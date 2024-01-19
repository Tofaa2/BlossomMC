package net.blossom.item.types;

import net.blossom.chat.ChatFeature;
import net.blossom.data.DataType;
import net.blossom.item.ItemType;
import net.blossom.item.enchantment.ItemEnchant;
import net.blossom.item.enchantment.ItemEnchantHeader;
import net.blossom.item.properties.ItemRarity;
import net.blossom.utils.Utils;
import net.kyori.adventure.text.Component;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.collections.ImmutableIntArray;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTIntArray;

import java.util.*;

import static net.blossom.core.Feature.getFeature;

public class EnchantableItem extends StatsItem {

    public static final Tag<NBT> ENCHANTS = Tag.NBT("enchants");
    public static Set<ItemEnchant> getEnchants(ItemStack stack) {
        NBT tag = stack.getTag(ENCHANTS);
        if (tag == null) return Collections.emptySet();
        NBTCompound c = (NBTCompound) tag;
        ImmutableIntArray ids = c.getIntArray("ids");
        ImmutableIntArray levels = c.getIntArray("levels");
        if (ids == null || levels == null) return Collections.emptySet();
        Set<ItemEnchant> enchants = new HashSet<>();
        for (int i = 0; i < ids.getSize(); i++) {
            int id = ids.get(i);
            int level = levels.get(i);
            ItemEnchantHeader header = ItemEnchantHeader.get((short) id);
            if (header == null) continue;
            ItemEnchant enchant = new ItemEnchant(level, header);
            enchants.add(enchant);
        }
        return enchants;
    }

    private final Set<ItemEnchant> enchants = new HashSet<>();

    public EnchantableItem(
            NamespaceID id,
            Material material,
            String name,
            String description,
            ItemType<?> type,
            ItemRarity rarity,
            int amount,
            Map<DataType<?>, Object> data
    ) {
        super(id, material, name, description, type, rarity, amount, data);
    }


    public boolean setEnchantments(Set<ItemEnchant> enchants) {
        if (enchants.stream()
                .anyMatch(enchant -> !enchant.header().compatibleItems().contains(this.getType()))) {
            return false;
        }
        this.enchants.clear();
        this.enchants.addAll(enchants);
        restack();
        return true;
    }

    public boolean addEnchantment(ItemEnchant enchant) {
        if (!enchant.header().compatibleItems().contains(this.getType())) {
            return false;
        }
        enchants.removeIf(enc -> enc.header().id() == enchant.header().id());
        this.enchants.add(enchant);
        restack();
        return true;
    }

    public void removeEnchantment(ItemEnchantHeader enchant) {
        enchants.removeIf(enc -> enc.header().id() == enchant.id());
        restack();
    }

    public @Nullable ItemEnchant getEnchantment(ItemEnchantHeader header) {
        return enchants.stream()
                .filter(enchant -> enchant.header().id() == header.id())
                .findFirst()
                .orElse(null);
    }

    @Override
    protected @NotNull List<Component> createLore() {
        List<Component> lore = super.createLore();
        if (enchants.isEmpty()) return lore;
        lore.add(ENCHANTMENT_HEADER);
        ChatFeature f = getFeature(ChatFeature.class);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        // A line of lore can store at most 3 enchantments
        for (ItemEnchant enchant : enchants) {
            var header = enchant.header();
            int level = enchant.level();
            sb.append("<").append(enchant.color()).append(">").append(header.name()).append(" ").append(Utils.toRoman(level));
            if (i == 2) {
                lore.add(f.pure(sb.toString()));
                sb.setLength(0);
                i = 0;
            } else {
                sb.append(" ");
                i++;
            }
        }
        if (!sb.isEmpty()) {
            lore.add(f.pure(sb.toString()));
        }
        return lore;
    }

    @Override
    protected @Nullable Set<TagContainer<?>> createTags() {
        Set<TagContainer<?>> tags = super.createTags();
        if (enchants.isEmpty()) return tags;
        if (tags == null) tags = new HashSet<>();
        int[] ids = new int[enchants.size()];
        int[] levels = new int[enchants.size()];
        int i = 0;
        for (ItemEnchant enchant : enchants) {
            ids[i] = enchant.header().id();
            levels[i] = enchant.level();
            i++;
        }
        NBTCompound c = new NBTCompound(Map.of(
                "ids", new NBTIntArray(ids),
                "levels", new NBTIntArray(levels)
        ));
        tags.add(new TagContainer<>(ENCHANTS, c));
        return tags;
    }

    @Override
    protected boolean shouldStackGlow() {
        return !enchants.isEmpty();
    }
}
