package net.blossom.listeners;

import net.blossom.ability.Ability;
import net.blossom.actuation.FeatureTrigger;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.blossom.player.BlossomPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public final class AbilityListeners implements Listener {

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        BlossomPlayer player = BlossomPlayer.of(event.getPlayer());
        var abilities = player.getAbilities(FeatureTrigger.SNEAK);
        if (abilities != null) {
            for (Ability ability : abilities) {
                ability.handle(player);
            }
        }
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent event) {
        if (!event.isSprinting()) return;
        BlossomPlayer player = BlossomPlayer.of(event.getPlayer());
        var abilities = player.getAbilities(FeatureTrigger.SPRINT);
        if (abilities != null) {
            for (Ability ability : abilities) {
                ability.handle(player);
            }
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        BlossomPlayer player = BlossomPlayer.of(event.getPlayer());
        var abilities = player.getAbilities(FeatureTrigger.JUMP);
        if (abilities != null) {
            for (Ability ability : abilities) {
                ability.handle(player);
            }
        }
    }

    @EventHandler
    public void onEntityAttackEntity(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            BlossomPlayer player = BlossomPlayer.of(event.getPlayer());
            var abilities = player.getAbilities(FeatureTrigger.LEFT_CLICK);
            if (abilities != null) {
                for (Ability ability : abilities) {
                    ability.handle(player);
                }
            }
        }
    }

}
