package net.blossom.entity.mob.generators;

import com.google.gson.JsonObject;
import net.blossom.entity.mob.EntityAIGenerator;
import net.blossom.entity.mob.Mob;
import net.minestom.server.entity.ai.EntityAIGroup;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomLookAroundGoal;
import org.jetbrains.annotations.NotNull;

public class BasicMovementAIGenerator implements EntityAIGenerator<EntityAIGroup> {
    @Override
    public Class<EntityAIGroup> getAIClass() {
        return EntityAIGroup.class;
    }

    @Override
    public @NotNull String getAIName() {
        return "basic_movement";
    }

    @Override
    public @NotNull EntityAIGroup generate(JsonObject json, Mob mob) {
        return new EntityAIGroupBuilder()
                .addGoalSelector(new RandomLookAroundGoal(mob, json.get("chance").getAsInt()))
                .build();
    }
}
