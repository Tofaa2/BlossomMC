package net.blossom.ability;

import net.blossom.actuation.FeatureTrigger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static net.blossom.ability.AbilityImpl.register;

interface Abilities {

    @NotNull Ability TEST_ABILITY = register(
            new AbilityImpl(
                    "Test Ability",
                    "This is a test ability",
                    FeatureTrigger.SNEAK,
                    (abilityHolder, abilityContext) -> {
                        Bukkit.broadcast(Component.text("Test ability triggered!"));
                    }
            )
    );

}
