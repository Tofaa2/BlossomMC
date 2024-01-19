package net.blossom.ability;

import net.blossom.actuation.FeatureTrigger;
import org.jetbrains.annotations.NotNull;

public interface Ability extends Abilities {

    @NotNull String getName();

    @NotNull String getDescription();

    @NotNull FeatureTrigger getTrigger();

    long getCooldown();

    void handle(@NotNull AbilityHolder holder, @NotNull AbilityContext context);

    default void handle(@NotNull AbilityHolder holder) {
        handle(holder, AbilityContext.EMPTY_CONTEXT);
    }

}
