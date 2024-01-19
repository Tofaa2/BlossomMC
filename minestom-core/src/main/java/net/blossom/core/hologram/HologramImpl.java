package net.blossom.core.hologram;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class HologramImpl implements Hologram{

    private final Instance instance;
    private HologramLineImpl<?>[] entities;

    HologramImpl(Instance instance) {
        this.instance = instance;
    }

    HologramImpl(Instance instance, HologramLine<?>[] entities) {
        this.instance = instance;
        this.entities = new HologramLineImpl[entities.length];
        for (int i = 0; i < entities.length; i++) {
            this.entities[i] = (HologramLineImpl<?>) entities[i];
        }
    }

    @Override
    public @NotNull Collection<HologramLine<?>> getLines() {
        if (entities == null) {
            return Collections.emptyList();
        }
        return List.of(entities);
    }

    @Override
    public @Nullable HologramLine<?> getLine(int index) {
        if (entities == null) {
            return null;
        }
        return entities[index];
    }

    @Override
    public void addLine(HologramLine.@NotNull Item item) {
        if (entities == null) {
            entities = new HologramLineImpl[1];
        }
        int index = entities.length;
        entities = Arrays.copyOf(entities, entities.length + 1);
        entities[index] = (HologramLineImpl<?>) item;
    }

    @Override
    public void addLine(HologramLine.@NotNull Text text) {
        if (entities == null) {
            entities = new HologramLineImpl[1];
        }
        int index = entities.length;
        entities = Arrays.copyOf(entities, entities.length + 1);
        entities[index] = (HologramLineImpl<?>) text;
    }

    @Override
    public void setLine(int index, HologramLine.@NotNull Item item) {
        if (entities == null) {
            entities = new HologramLineImpl[1];
        }
        if (index >= entities.length) {
            entities = Arrays.copyOf(entities, index + 1);
        }
        entities[index] = (HologramLineImpl<?>) item;
    }

    @Override
    public void setLine(int index, HologramLine.@NotNull Text text) {
        if (entities == null) {
            entities = new HologramLineImpl[1];
        }
        if (index >= entities.length) {
            entities = Arrays.copyOf(entities, index + 1);
        }
        entities[index] = (HologramLineImpl<?>) text;
    }


    @Override
    public void show() {
        if (entities == null) {
            return;
        }
        for (HologramLineImpl<?> entity : this.entities) {
            entity.getEntity().setInstance(this.instance, entity.getPosition());
        }
    }

    @Override
    public void hide() {
        if (this.entities == null) {
            return;
        }
        for (HologramLineImpl<?> entity : this.entities) {
            entity.getEntity().remove();
        }
    }

    @Override
    public void delete() {
        hide();
        this.entities = null;
    }

    @Override
    public @NotNull Instance getInstance() {
        return instance;
    }

}
