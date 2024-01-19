package net.blossom.entity.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blossom.item.Item;
import net.blossom.utils.FloatRange;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record LootTable(FloatRange experienceRange, Entry[] entries) {


    public static @NotNull LootTable fromJson(JsonObject json) {
        if (json == null) return EMPTY;

        JsonObject experience = json.getAsJsonObject("experience");
        FloatRange exp;
        if (experience == null) {
            exp = FloatRange.EMPTY;
        }
        else {
            float min = experience.get("min").getAsFloat();
            float max = experience.get("max").getAsFloat();
            exp = new FloatRange(min, max);
        }

        JsonArray jsonEntries = json.getAsJsonArray("entries");

        if (jsonEntries == null) return new LootTable(exp, new Entry[0]);

        Entry[] entries = new Entry[jsonEntries.size()];
        for (int i = 0; i < jsonEntries.size(); i++) {
            JsonObject entry = jsonEntries.get(i).getAsJsonObject();
            float chance = entry.get("chance").getAsFloat();

            int min, max;
            if (entry.has("amount")) {
                min = max = entry.get("amount").getAsInt();
            }
            else if (entry.has("min") && entry.has("max")) {
                min = entry.get("min").getAsInt();
                max = entry.get("max").getAsInt();
            }
            else {
                min = max = 1;
            }
            Item item = Item.Registry.get(NamespaceID.from(entry.get("item").getAsString()));
            entries[i] = new Entry(chance, min, max, item);
        }
        return new LootTable(exp, entries);
    }

    public static @NotNull Entry always(Item item, int min, int max) {
        return new Entry(1, min, max, item);
    }

    public static @NotNull Entry always(Item item, int amount) {
        return always(item, amount, amount);
    }

    public static final LootTable EMPTY = new LootTable(FloatRange.EMPTY, new Entry[0]);

    public LootTable(Entry... entries) {
        this(FloatRange.EMPTY, entries);
    }


    public void rollAndDrop(@NotNull Instance instance, @NotNull Point point, @Nullable UUID killer) {
        for (var entry : entries) {
            if (entry.roll()) {
                int amount = entry.min + (int) (Math.random() * (entry.max - entry.min));
                ItemStack stack = entry.item.toItemStack().withAmount(amount);
                BoundItemEntity e = new BoundItemEntity(killer, stack);
                e.setInstance(instance, point).thenRun(() -> {
                    e.setVelocity(new Vec(
                            -0.5 + Math.random(),
                            0.5 + Math.random(),
                            -0.5 + Math.random()));
                });
            }
        }
    }

    public void rollAndDrop(@NotNull Instance instance, @NotNull Point point) {
        rollAndDrop(instance, point, null);
    }


    public record Entry(float chance, int min, int max, Item item) {

        public Entry {
            if (chance < 0 || chance > 1) {
                chance = 1;
            }
        }


        public boolean roll() {
            return Math.random() <= chance;
        }

    }

}
