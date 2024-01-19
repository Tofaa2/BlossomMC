package net.blossom.entity.loot;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BoundItemEntity extends ItemEntity {

    private final UUID entityUUID;

    public BoundItemEntity(@Nullable UUID entityUUID, @NotNull ItemStack itemStack) {
        super(itemStack);
        this.entityUUID = entityUUID;
    }

    public BoundItemEntity(@NotNull Entity entity, @NotNull ItemStack itemStack) {
        this(entity.getUuid(), itemStack);
    }

    public @Nullable  UUID getEntityUUID() {
        return entityUUID;
    }

    public boolean isBoundTo(@NotNull UUID uuid) {
        return entityUUID.equals(uuid);
    }

    public boolean isBoundTo(@NotNull Entity e) {
        return isBoundTo(e.getUuid());
    }

}
