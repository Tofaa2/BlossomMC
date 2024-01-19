package net.blossom.command.player;

import dev.jorel.commandapi.CommandAPICommand;
import net.blossom.command.BlossomCommand;
import net.blossom.player.BlossomPlayer;
import org.jetbrains.annotations.NotNull;

public final class HomeCommand implements BlossomCommand {
    @Override
    public @NotNull CommandAPICommand[] createMany() {
        return new CommandAPICommand[] {

                new CommandAPICommand("home")
                        .executesPlayer((player, context) -> {
                    getPlayer(player).teleportHome();
                }),
                new CommandAPICommand("sethome")
                        .executesPlayer((player, context) -> {
                    BlossomPlayer.of(player).setHome();
                }),
                new CommandAPICommand("delhome")
                        .withAliases("deletehome")
                        .executesPlayer((player, context) -> {
                    BlossomPlayer.of(player).deleteHome();
                })
        };
    }
}
