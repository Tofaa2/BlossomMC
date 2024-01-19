package net.blossom.item.enchantment;

import net.blossom.item.ItemType;
import net.blossom.entity.event.PlayerAttackMobEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public sealed interface ItemEnchantHeader permits HeaderImpl {


    @NotNull ItemEnchantHeader SHARPNESS = HeaderImpl.register(
            new HeaderImpl(
                    (short) 0,
                    "Sharpness",
                    "Increases the damage dealt by a weapon.",
                    new EnchantTrigger(
                            Set.of(PlayerAttackMobEvent.class),
                            (enchant, player, item) -> {

                            }
                    ),
                    null,
                    Set.of(ItemType.WEAPON)
            )
    );

    static @Nullable ItemEnchantHeader get(short id) {
        return HeaderImpl.get(id);
    }

    static @Nullable ItemEnchantHeader get(@NotNull String name) {
        return HeaderImpl.get(name);
    }

    static @NotNull Set<ItemEnchantHeader> values() {
        return Set.copyOf(HeaderImpl.HEADERS.values());
    }

    short id();

    String name();

    String description();

    EnchantTrigger trigger();

    Set<ItemType<?>> compatibleItems();

    boolean isCompatible(ItemEnchantHeader header);

    default boolean isCompatible(ItemEnchant enchant) {
        return isCompatible(enchant.header());
    }

}
