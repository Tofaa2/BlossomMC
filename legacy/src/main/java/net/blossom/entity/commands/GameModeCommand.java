package net.blossom.entity.commands;

import net.blossom.core.BlossomCommand;
import net.blossom.core.Rank;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

import java.util.Collections;
import java.util.List;

public class GameModeCommand extends BlossomCommand {

    public static final BlossomCommand[] ALL = new BlossomCommand[] {
            new GameModeCommand(),
            BlossomCommand.fast(Rank.ADMIN, "gmc", Collections.singletonList("creative"), (sender, context) -> MinecraftServer.getCommandManager().execute(sender, "gamemode CREATIVE")),
            BlossomCommand.fast(Rank.ADMIN, "gms", Collections.singletonList("survival"), (sender, context) -> MinecraftServer.getCommandManager().execute(sender, "gamemode SURVIVAL")),
            BlossomCommand.fast(Rank.ADMIN, "gma", Collections.singletonList("adventure"), (sender, context) -> MinecraftServer.getCommandManager().execute(sender, "gamemode ADVENTURE")),
            BlossomCommand.fast(Rank.ADMIN, "gmsp", Collections.singletonList("spectator"), (sender, context) -> MinecraftServer.getCommandManager().execute(sender, "gamemode SPECTATORz")),
    };

    private GameModeCommand() {
        super(Rank.ADMIN, "gamemode", "gm");
        Argument<GameMode> gmArg = ArgumentType.Enum("gamemode", GameMode.class);
        ArgumentEntity eArg = ArgumentType.Entity("target").onlyPlayers(true);
        this.addSyntax((sender, context) -> {
            GameMode gm = context.get(gmArg);
            if (gm == null) {
                sender.sendMessage("<red>Invalid gamemode specified");
                return;
            }
            List<Player> entities = context.get(eArg).find(sender).stream()
                    .map(e -> (Player) e)
                    .toList();

            if (entities.isEmpty()) {
                sender.sendMessage("<red>Invalid target/targets specified");
                return;
            }
            String user = "Console";
            if (sender instanceof Player p) {
                user = p.getUsername();
            }
            for (var player : entities) {
                player.setGameMode(gm);
                player.sendMessage("<green>Your gamemode has been updated to " + gm.name().toLowerCase() + " by " + user);
            }
            sender.sendMessage("<green>Successfully updated " + entities.size() + " players to " + gm.name().toLowerCase());
        }, gmArg, eArg);

        this.addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("<red>Only players can use this syntax");
                return;
            }
            GameMode gm = context.get(gmArg);
            if (gm == null) {
                sender.sendMessage("<red>Invalid gamemode specified");
                return;
            }
            player.setGameMode(gm);
            player.sendMessage("<green>Your gamemode has been updated to " + gm.name().toLowerCase());
        }, gmArg);
    }
}
