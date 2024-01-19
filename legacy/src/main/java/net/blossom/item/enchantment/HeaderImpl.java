package net.blossom.item.enchantment;

import net.blossom.item.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

record HeaderImpl(
        short id,
        String name,
        String description,
        EnchantTrigger trigger,
        @Nullable Set<Class<? extends ItemEnchantHeader>> incompatibles,
        Set<ItemType<?>> compatibleItems
) implements ItemEnchantHeader {

    static final HashMap<Short, ItemEnchantHeader> HEADERS = new HashMap<>();

    static ItemEnchantHeader get(short id) {
        return HEADERS.get(id);
    }

    static ItemEnchantHeader get(String name) {
        return HEADERS.values().stream().filter(h -> h.name().equals(name)).findFirst().orElse(null);
    }

    static ItemEnchantHeader register(ItemEnchantHeader header) {
        HEADERS.put(header.id(), header);
        return header;
    }

    HeaderImpl(
            short id,
            String name,
            String description,
            EnchantTrigger trigger,
            Set<Class<? extends ItemEnchantHeader>> incompatibles
    ) {
        this(id, name, description, trigger, incompatibles, Set.of());
    }

    @Override
    public boolean isCompatible(ItemEnchantHeader header) {
        if (incompatibles == null) return true;
        return !incompatibles.contains(header.getClass());
    }

}
