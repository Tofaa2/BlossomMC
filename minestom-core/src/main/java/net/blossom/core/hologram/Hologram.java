package net.blossom.core.hologram;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Hologram {


    static @NotNull Hologram create(@NotNull Instance instance) {
        return new HologramImpl(instance);
    }

    static @NotNull Hologram create(@NotNull Instance instance, @NotNull HologramLine<?>... lines) {
        return new HologramImpl(instance, lines);
    }


    @NotNull Instance getInstance();

    @NotNull Collection<HologramLine<?>> getLines();

    @Nullable HologramLine<?> getLine(int index);

    void addLine(@NotNull HologramLine.Item item);

    void addLine(@NotNull HologramLine.Text text);

    void setLine(int index, @NotNull HologramLine.Item item);

    void setLine(int index, @NotNull HologramLine.Text text);

    void show();

    void hide();

    void delete();

}
