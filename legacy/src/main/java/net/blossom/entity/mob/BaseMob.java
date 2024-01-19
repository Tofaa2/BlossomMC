package net.blossom.entity.mob;

import com.google.gson.JsonObject;
import net.blossom.core.Blossom;
import net.blossom.data.DataType;
import net.blossom.data.SimpleDataContainer;
import net.blossom.entity.EntityFeature;
import net.blossom.entity.loot.LootTable;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class BaseMob implements SimpleDataContainer {

    private final String name;
    private final Map<DataType<?>, Object> data;
    private final EntityType entityType;
    private final AggressionType aggressionType;
    private final Map<String, JsonObject> aiData;
    private final LootTable lootTable;

    public BaseMob(
            String name,
            EntityType entityType,
            Map<DataType<?>, Object> data,
            AggressionType aggressionType,
            Map<String, JsonObject> aiData,
            LootTable lootTable
    ) {
        this.name = name;
        this.entityType = entityType;
        this.data = data;
        this.aggressionType = aggressionType;
        this.aiData = aiData;
        this.lootTable = lootTable;
    }

    public BaseMob(
            String name,
            EntityType entityType,
            AggressionType aggressionType,
            Map<DataType<?>, Object> data
    ) {
        this(name, entityType, data, aggressionType, null, LootTable.EMPTY);
    }

    public @NotNull Mob create(@Nullable Map<String, JsonObject> aiData) {
        Mob mob = new Mob(this);
        EntityFeature feature = EntityFeature.getFeature(EntityFeature.class);
        if (aiData != null) {
            for (var entry : aiData.entrySet()) {
                var ai = feature.getAIGenerator(entry.getKey());
                if (ai != null) {
                    mob.addAIGroup(ai.generate(entry.getValue(), mob));
                }
                else {
                    Blossom.LOGGER.warn("AI generator for " + entry.getKey() + " not found");
                }
            }
        }
        return mob;
    }

    public @NotNull Mob create() {
        return create(aiData);
    }


    public LootTable getLootTable() {
        return lootTable;
    }

    public @NotNull EntityType getEntityType() {
        return entityType;
    }

    public @NotNull String getName() {
        return name;
    }

    public @Nullable Map<String, JsonObject> getAIGenerators() {
        return aiData;
    }

    public @NotNull AggressionType getAggressionType() {
        return aggressionType;
    }

    public @NotNull String getDisplayName() {
        return "<" + aggressionType.toHex() + ">" + name;
    }
    @Override
    public @NotNull Map<DataType<?>, Object> getRawMap() {
        return data;
    }
}
