package net.blossom.entity.mob;

import com.google.gson.JsonObject;
import net.minestom.server.entity.ai.EntityAIGroup;
import org.jetbrains.annotations.NotNull;

public interface EntityAIGenerator<T extends EntityAIGroup> {

    Class<T> getAIClass();

    @NotNull String getAIName();

    @NotNull T generate(JsonObject json, Mob mob);

}
