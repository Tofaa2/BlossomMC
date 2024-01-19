package net.blossom.entity.mob;

import net.blossom.chat.ChatFeature;
import net.blossom.data.DataType;
import net.blossom.data.SimpleDataContainer;
import net.blossom.entity.EntityFeature;
import net.blossom.utils.UnicodeCharacters;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.damage.EntityDamage;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.LivingEntityMeta;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Mob extends EntityCreature implements SimpleDataContainer {

    private final BaseMob base;
    private final HashMap<DataType<?>, Object> data;

    public Mob(@NotNull BaseMob baseMob, @NotNull Map<DataType<?>, Object> data) {
        super(baseMob.getEntityType());
        this.base = baseMob;
        this.data = new HashMap<>(data);
    }

    public Mob(@NotNull BaseMob baseMob) {
        this(baseMob, new HashMap<>(baseMob.getRawMap()));
    }

    @Override
    public float getHealth() {
        return getData(DataType.HEALTH);
    }

    @Override
    public float getMaxHealth() {
        return getData(DataType.MAX_HEALTH);
    }

    public void setHealth(final float health) {
        if (instance == null) {
            scheduleNextTick(a -> {
                if (health <= 0 && !isDead()) {
                    kill();
                }
                if (health >= getMaxHealth()) {
                    setData(DataType.HEALTH, getMaxHealth());
                }
                LivingEntityMeta m = (LivingEntityMeta) getEntityMeta();
                m.setHealth(health);
                updateDisplayName();
                setData(DataType.HEALTH, health);
            });
        }
        else {
            if (health <= 0 && !isDead()) {
                kill();
            }
            if (health >= getMaxHealth()) {
                setData(DataType.HEALTH, getMaxHealth());
            }
            LivingEntityMeta m = (LivingEntityMeta) getEntityMeta();
            m.setHealth(health);
            updateDisplayName();
            setData(DataType.HEALTH, health);
        }
    }

    public void setMaxHealth(float maxHealth) {
        if (instance == null) {
            scheduleNextTick(e -> {
                setData(DataType.MAX_HEALTH, maxHealth);
                updateDisplayName();
            });
        }
        else {
            setData(DataType.MAX_HEALTH, maxHealth);
            updateDisplayName();
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition).thenRun(this::updateDisplayName);
    }

    public void updateDisplayName() {
        EntityMeta m = getEntityMeta();
        m.setNotifyAboutChanges(false);

        m.setCustomNameVisible(true);
        m.setCustomName(
                ChatFeature.getFeature(ChatFeature.class)
                        .createMessage(
                                "<red>[" + getLevel() + "] " + base.getDisplayName() + " <green>" + getHealth() + "<gray>/<green>" + getMaxHealth() + UnicodeCharacters.HEART_ICON,
                                false
                        )

        );
        m.setNotifyAboutChanges(true);
    }

    @Override
    public void kill() {
        super.kill();
        UUID killer;
        DamageType damage = getLastDamageSource();
        if (damage instanceof EntityDamage a ) {
            killer = a.getSource().getUuid();
        } else {
            killer = null;
        }
        base.getLootTable().rollAndDrop(getInstance(), getPosition(), killer);
    }

    @Override
    public boolean damage(@NotNull DamageType type, float value) {
        EntityFeature.spawnDamageDisplay(getInstance(), getPosition(), value);
        return super.damage(type, value);
    }

    public void setLevel(int level) {
        setData(DataType.LEVEL, level);
        updateDisplayName();
    }

    public int getLevel() {
        return getData(DataType.LEVEL);
    }

    @Override
    public @NotNull Map<DataType<?>, Object> getRawMap() {
        return data;
    }
}
