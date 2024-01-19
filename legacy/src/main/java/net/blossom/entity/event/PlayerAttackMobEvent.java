package net.blossom.entity.event;

import net.blossom.entity.BlossomPlayer;
import net.blossom.entity.mob.Mob;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAttackMobEvent implements PlayerEvent, CancellableEvent {

    private final BlossomPlayer player;
    private final Mob attacked;

    private boolean cancelled = false;
    private boolean applyKnockback = true;
    private double damage = 0.0d;


    public PlayerAttackMobEvent(BlossomPlayer player, Mob attacked) {
        this.player = player;
        this.attacked = attacked;
    }

    public Mob getAttacked() {
        return attacked;
    }

    @Override
    public @NotNull BlossomPlayer getPlayer() {
        return player;
    }

    public boolean shouldApplyKnockback() {
        return applyKnockback;
    }

    public void setApplyKnockback(boolean applyKnockback) {
        this.applyKnockback = applyKnockback;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
