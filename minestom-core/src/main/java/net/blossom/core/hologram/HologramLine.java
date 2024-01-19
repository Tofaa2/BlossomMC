package net.blossom.core.hologram;

import net.blossom.commons.animations.Animatable;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public sealed interface HologramLine<T extends AbstractDisplayMeta> permits HologramLine.Item, HologramLine.Text, HologramLineImpl {

    static @NotNull Item item(@NotNull Pos position, @NotNull ItemStack itemStack) {
        return new HologramLineImpl.Item(itemStack, position, null);
    }

    static @NotNull Item item(@NotNull Pos position, @NotNull ItemStack itemStack, @NotNull Animatable<Pos> animation) {
        return new HologramLineImpl.Item(itemStack, position, animation);
    }

    static @NotNull Text text(@NotNull Pos position, @NotNull Component component) {
        return new HologramLineImpl.Text(component, position, null);
    }

    static @NotNull Text text(@NotNull Pos position, @NotNull Component component, @NotNull Animatable<Pos> animation) {
        return new HologramLineImpl.Text(component, position, animation);
    }


    @NotNull Pos getPosition();

    void queueModification(Consumer<T> modifier);

    @Nullable Animatable<Pos> getAnimation();

    sealed interface Item extends HologramLine<ItemDisplayMeta> permits HologramLineImpl.Item {

        void setItem(@NotNull ItemStack itemStack);

        @NotNull ItemStack getItem();

    }

    sealed interface Text extends HologramLine<TextDisplayMeta> permits HologramLineImpl.Text {
        void set(@NotNull Component component);

        @NotNull Component get();

    }

}
