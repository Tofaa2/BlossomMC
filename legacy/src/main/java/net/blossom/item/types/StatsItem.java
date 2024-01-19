package net.blossom.item.types;

import net.blossom.chat.ChatFeature;
import net.blossom.core.Feature;
import net.blossom.data.DataContainer;
import net.blossom.data.DataType;
import net.blossom.item.Item;
import net.blossom.item.ItemType;
import net.blossom.item.properties.ItemRarity;
import net.blossom.utils.EnumUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsItem extends Item implements DataContainer {


    protected final HashMap<DataType<?>, Object> data;

    public StatsItem(NamespaceID id, Material material, String name, String description, ItemType<?> type, ItemRarity rarity, int amount, Map<DataType<?>, Object> data) {
        super(id, material, name, description, type, rarity, amount);
        this.data = new HashMap<>(data);
    }

    @Override
    public <T> @UnknownNullability T getData(@NotNull DataType<T> type, boolean defaultIfNotSet) {
        if (data.containsKey(type)) {
            return (T) data.get(type);
        }
        if (defaultIfNotSet) {
            return type.defaultValue();
        }
        return null;
    }

    @Override
    public <T> @NotNull T getData(@NotNull DataType<T> type) {
        return getData(type, true);
    }

    @Override
    public <T> void setData(@NotNull DataType<T> type, @NotNull T data) {
        Check.stateCondition(!type.isWeaponApplicable(), "Cannot set data for non-weapon applicable data type");
        this.data.put(type, data);
        restack();
    }

    @Override
    public void setData(@NotNull Map<DataType<?>, Object> data) {
        this.data.putAll(
                data.entrySet().stream()
                        .filter(entry -> entry.getKey().isWeaponApplicable())
                        .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll)
        );
        restack();
    }

    @Override
    public void defaultData(@NotNull DataType<?> type) {
        this.data.put(type, type.defaultValue());
        restack();
    }

    @Override
    public void removeData(@NotNull DataType<?> type) {
        this.data.remove(type);
        restack();
    }

    @Override
    public @NotNull Map<DataType<?>, Object> getDataMapCopy() {
        return Map.copyOf(data);
    }

    @Override
    protected @NotNull List<Component> createLore() {
        List<Component> lore = super.createLore();
        ChatFeature f = Feature.getFeature(ChatFeature.class);
        if (this.data.isEmpty()) return lore;
        lore.add(EXTRAS_HEADER);
        for (var entry : data.entrySet()) {
            lore.add(
                    f.pure(
                            "<gray>"
                                    + EnumUtils.prettyString(entry.getKey().key().path())
                                    + "<white>: "
                                    + "<" + entry.getKey().color().asHexString() + ">"
                                    + entry.getValue()
                                    + entry.getKey().icon()
                    )
            );
        }
        lore.add(Component.empty());
        return lore;
    }
}
