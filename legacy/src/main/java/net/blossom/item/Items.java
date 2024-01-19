package net.blossom.item;

import net.blossom.core.Blossom;
import net.blossom.data.DataType;
import net.blossom.item.properties.ItemRarity;
import net.blossom.item.types.WeaponItem;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.blossom.core.Blossom.newId;

interface Items {

    @NotNull WeaponItem TEST_SWORD = Registry.register(
            new WeaponItem(
                    newId("test_sword"), Material.IRON_SWORD,
                    "Test Sword", "A sword for testing, i dont really know what else to put here",
                    ItemRarity.MYTHIC, 1,
                    Map.of(
                            DataType.DAMAGE, 10d,
                            DataType.ATTACK_SPEED, 1.6f
                    )
            )
    );

    @NotNull Item BOAR_LEATHER = Registry.register(new Item(
            newId("boar_leather"), Material.LEATHER,
            "Boar Leather", "Leather from a boar, it's pretty rough, can be used to create equipment",
            ItemType.MATERIAL, ItemRarity.COMMON, 1
    ));

    private static void init() {
        Blossom.LOGGER.info("Loaded " + Registry.items.size() + " items");
    }

    class Registry {

        static final Tag<String> ITEM_ID = Tag.String("item_id");


        public static @Nullable Item get(ItemStack stack) {
            String s = stack.getTag(ITEM_ID);
            if (s == null) return null;
            return get(NamespaceID.from(s));
        }

        @SuppressWarnings("unchecked")
        public static <T extends Item> @Nullable T get(NamespaceID id) {
            return (T) Registry.items.get(id);
        }

        public static <T extends Item> @Nullable T get(String id) {
            return get(newId(id));
        }

        public static Set<String> getIds() {
            return Registry.items.keySet().stream().map(NamespaceID::path).collect(Collectors.toSet());
        }

        static void init(ItemFeature feature) {
            Items.init();
        }

        static final HashMap<NamespaceID, Item> items = new HashMap<>();

        private static <T extends Item> T register(T item) {
            items.put(item.getId(), item);
            Blossom.LOGGER.info("Registered item: " + item.getId());
            return item;
        }

        private static <T extends Item> T register(T item, Consumer<T> consumer) {
            register(item);
            consumer.accept(item);
            return item;
        }
    }

}
