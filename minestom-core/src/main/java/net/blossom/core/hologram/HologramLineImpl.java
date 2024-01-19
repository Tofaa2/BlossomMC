package net.blossom.core.hologram;

import net.blossom.commons.animations.Animatable;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

non-sealed class HologramLineImpl<T extends AbstractDisplayMeta> implements HologramLine<T> {

    static final class Text extends HologramLineImpl<TextDisplayMeta> implements HologramLine.Text {
        Text(Component text, Pos position, Animatable<Pos> animation) {
            super(position, TextDisplayMeta.class, animation);
            queueModification(meta -> meta.setText(text));
        }

        @Override
        public void set(@NotNull Component component) {
            queueModification(meta -> meta.setText(component));
        }

        @Override
        public @NotNull Component get() {
            return ((TextDisplayMeta) getEntity().getEntityMeta()).getText();
        }
    }

    static final class Item extends HologramLineImpl<ItemDisplayMeta> implements HologramLine.Item {
        Item(ItemStack item, Pos position, Animatable<Pos> animation) {
            super(position, ItemDisplayMeta.class, animation);
            queueModification(meta -> meta.setItemStack(item));
        }

        @Override
        public void setItem(@NotNull ItemStack itemStack) {
            queueModification(meta -> meta.setItemStack(itemStack));
        }

        @Override
        public @NotNull ItemStack getItem() {
            return ((ItemDisplayMeta) getEntity().getEntityMeta()).getItemStack();
        }
    }


    private final Pos position;

    private final Animatable<Pos> animation;
    private final Entity entity;

    HologramLineImpl(Pos position, Class<T> metaClass, Animatable<Pos> animation) {
        EntityType type = findMetaType(metaClass);
        this.entity = new Entity(type);
        this.position = position;
        this.entity.getEntityMeta().setHasNoGravity(true);
        this.animation = animation;
        if (animation != null) {
            this.entity.scheduler().buildTask(() -> {
                if (entity.isActive()) {
                    Pos next = animation.getNext();
                    this.entity.setVelocity(next.direction());
                    entity.getViewers().forEach(player -> {
                        player.sendMessage("A");
                    });
                }
            }).repeat(animation.getUpdateCycle()).schedule();
        }
    }

    HologramLineImpl(Pos position, Class<T> metaClass) {
        this(position, metaClass, null);
    }

    Entity getEntity() {
        return entity;
    }

    @Override
    public @NotNull Pos getPosition() {
        return position;
    }

    @Override
    public void queueModification(Consumer<T> modifier) {
        T meta = (T) entity.getEntityMeta();
        modifier.accept(meta);
    }

    @Override
    public @Nullable Animatable<Pos> getAnimation() {
        return animation;
    }

    private static <T extends AbstractDisplayMeta> EntityType findMetaType(Class<T> metaClass) {
        if (metaClass == TextDisplayMeta.class) {
            return EntityType.TEXT_DISPLAY;
        } else if (metaClass == ItemDisplayMeta.class) {
            return EntityType.ITEM_DISPLAY;
        } else {
            throw new IllegalArgumentException("Unknown meta class " + metaClass);
        }
    }

}
