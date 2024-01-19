package net.blossom.hub.features.cosmetics;

import net.blossom.core.BlossomPlayer;
import net.blossom.hub.features.HubPlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerStartFlyingEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.Material;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public record Cosmetic<T extends PlayerEvent>(
        String name,
        String description,
        int shardsCost,
        Material icon,
        CosmeticRarity rarity,
        CosmeticType type,
        Class<T> eventClass,
        Consumer<T> eventConsumer,
        @Nullable Consumer<HubPlayer> onEquip,
        @Nullable Consumer<HubPlayer> onUnequip
) {

    private static final Map<String, Cosmetic<? extends PlayerEvent>> COSMETICS = new HashMap<>();

    public static @NotNull Cosmetic<PlayerStartFlyingEvent> DOUBLE_JUMP = register(new Cosmetic<>(
            "Double Jump",
            "Double jump in the hub.",
            100,
            Material.FEATHER,
            CosmeticRarity.BASIC,
            CosmeticType.MOVEMENT,
            PlayerStartFlyingEvent.class,
            event -> ((HubPlayer) event.getPlayer()).attemptDoubleJump(),
            player -> {
                player.setAllowFlying(true);
                player.setFlying(false);
            },
            player -> {
                player.setAllowFlying(false);
                player.setFlying(false);
            }
    ));

    public static @NotNull Cosmetic<PlayerMoveEvent> HEART_TRAIL = register(new Cosmetic<>(
            "Heart Trail",
            "Leave a trail of hearts behind you.",
            100,
            Material.RED_DYE,
            CosmeticRarity.BASIC,
            CosmeticType.TRAIL,
            PlayerMoveEvent.class,
            event -> {
                ((BlossomPlayer) event.getPlayer()).spawnParticle(
                        Particle.HEART,
                        event.getPlayer().getPosition().add(0, 1, 0),
                        1
                );
            },
            null,
            null
    ));


    private static <T extends PlayerEvent> Cosmetic<T> register(Cosmetic<T> cosmetic) {
        COSMETICS.put(cosmetic.name, cosmetic);
        return cosmetic;
    }

    public static @Nullable Cosmetic<?> getByName(String name) {
        if (name == null) return null;
        return COSMETICS.get(name);
    }

    public static @NotNull Collection<Cosmetic<?>> getAll() {
        return Collections.unmodifiableCollection(COSMETICS.values());
    }

    public Cosmetic(
            String name,
            String description,
            int shardsCost,
            Material icon,
            CosmeticRarity rarity,
            CosmeticType type,
            Class<T> eventClass,
            Consumer<T> eventConsumer
    ) {
        this(name, description, shardsCost, icon, rarity, type, eventClass, eventConsumer, null, null);
    }

}
