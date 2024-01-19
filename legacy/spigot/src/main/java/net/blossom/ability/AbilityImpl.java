package net.blossom.ability;

import net.blossom.actuation.FeatureTrigger;
import net.blossom.player.BlossomPlayer;
import net.blossom.utils.cooldown.Cooldown;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

final class AbilityImpl implements Ability {

    private static final Map<String, Ability> ABILITIES = new HashMap<>();

    static Ability register(AbilityImpl impl) {
        ABILITIES.put(impl.getName(), impl);
        return impl;
    }


    private final String name;
    private final String description;
    private final FeatureTrigger trigger;
    private final BiConsumer<AbilityHolder, AbilityContext> handle;
    private final Cooldown<BlossomPlayer> cooldown;

    AbilityImpl(String name, String description, FeatureTrigger trigger, BiConsumer<AbilityHolder, AbilityContext> handle, int cooldown) {
        this.name = name;
        this.description = description;
        this.trigger = trigger;
        this.handle = handle;
        this.cooldown = new Cooldown<>(cooldown);
    }

    AbilityImpl(String name, String description, FeatureTrigger trigger, BiConsumer<AbilityHolder, AbilityContext> handle) {
        this(name, description, trigger, handle, -1);
    }

    @Override
    public long getCooldown() {
        return cooldown.getDuration();
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @NotNull FeatureTrigger getTrigger() {
        return trigger;
    }

    @Override
    public void handle(@NotNull AbilityHolder holder, @NotNull AbilityContext context) {
        handle.accept(holder, context);
    }
}
