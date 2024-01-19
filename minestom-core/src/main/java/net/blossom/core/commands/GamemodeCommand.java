package net.blossom.core.commands;

import net.blossom.core.BlossomCommand;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public final class GamemodeCommand extends BlossomCommand {


    public GamemodeCommand() {
        super("commands.gamemode", "gamemode", new String[] {"gm"});
        this.addSubcommand(new Survival());
        this.addSubcommand(new Creative());
        this.addSubcommand(new Adventure());
        this.addSubcommand(new Spectator());

        ArgumentEnum<GameMode> gameModeArg = ArgumentType.Enum("gamemode", GameMode.class);
        ArgumentEntity targetArg = ArgumentType.Entity("target").singleEntity(true).onlyPlayers(true);

        addSyntax((commandSender, commandContext) -> {
            GameMode gameMode = commandContext.get(gameModeArg);
            ((Player) commandSender).setGameMode(gameMode);
            commandSender.sendMessage("<green>Set gamemode to " + gameMode.name().toLowerCase());
        }, gameModeArg);

        addSyntax((commandSender, commandContext) -> {
            GameMode gameMode = commandContext.get(gameModeArg);
            Player target = commandContext.get(targetArg).findFirstPlayer(commandSender);
            if (target == null) {
                commandSender.sendMessage("<red>Invalid target");
                return;
            }
            target.setGameMode(gameMode);
            commandSender.sendMessage("<green>Set gamemode to " + gameMode.name().toLowerCase());
            target.sendMessage("<green>Your gamemode has been updated");
        }, gameModeArg, targetArg);
    }

    public final class Survival extends BlossomCommand {

        public Survival() {
            super("commands.gamemode.survival", "survival", new String[] {"gms"});
            ArgumentEntity targetArg = ArgumentType.Entity("target").singleEntity(true).onlyPlayers(true);

            addSyntax((commandSender, commandContext) -> {
                ((Player) commandSender).setGameMode(GameMode.SURVIVAL);
                commandSender.sendMessage("<green>Set gamemode to creative");
            });

            addSyntax((commandSender, commandContext) -> {
                Player target = commandContext.get(targetArg).findFirstPlayer(commandSender);
                if (target == null) {
                    commandSender.sendMessage("<red>Invalid target");
                    return;
                }
                target.setGameMode(GameMode.CREATIVE);
                commandSender.sendMessage("<green>Set gamemode to survival");
                target.sendMessage("<green>Your gamemode has been updated");
            }, targetArg);
        }

    }

    public final class Creative extends BlossomCommand {

        public Creative() {
            super("commands.gamemode.creative", "creative", new String[] {"gmc"});
            ArgumentEntity targetArg = ArgumentType.Entity("target").singleEntity(true).onlyPlayers(true);

            addSyntax((commandSender, commandContext) -> {
                ((Player) commandSender).setGameMode(GameMode.CREATIVE);
                commandSender.sendMessage("<green>Set gamemode to creative");
            });

            addSyntax((commandSender, commandContext) -> {
                Player target = commandContext.get(targetArg).findFirstPlayer(commandSender);
                if (target == null) {
                    commandSender.sendMessage("<red>Invalid target");
                    return;
                }
                target.setGameMode(GameMode.CREATIVE);
                commandSender.sendMessage("<green>Set gamemode to creative");
                target.sendMessage("<green>Your gamemode has been updated");
            }, targetArg);
        }

    }

    public final class Adventure extends BlossomCommand {

        public Adventure() {
            super("commands.gamemode.adventure", "adventure", new String[] {"gma"});
            ArgumentEntity targetArg = ArgumentType.Entity("target").singleEntity(true).onlyPlayers(true);

            addSyntax((commandSender, commandContext) -> {
                ((Player) commandSender).setGameMode(GameMode.ADVENTURE);
                commandSender.sendMessage("<green>Set gamemode to adventure");
            });

            addSyntax((commandSender, commandContext) -> {
                Player target = commandContext.get(targetArg).findFirstPlayer(commandSender);
                if (target == null) {
                    commandSender.sendMessage("<red>Invalid target");
                    return;
                }
                target.setGameMode(GameMode.ADVENTURE);
                commandSender.sendMessage("<green>Set gamemode to adventure");
                target.sendMessage("<green>Your gamemode has been updated");
            }, targetArg);
        }

    }

    public final class Spectator extends BlossomCommand {

        public Spectator() {
            super("commands.gamemode.spectator", "spectator", new String[] {"gms"});
            ArgumentEntity targetArg = ArgumentType.Entity("target").singleEntity(true).onlyPlayers(true);

            addSyntax((commandSender, commandContext) -> {
                ((Player) commandSender).setGameMode(GameMode.SPECTATOR);
                commandSender.sendMessage("<green>Set gamemode to spectator");
            });

            addSyntax((commandSender, commandContext) -> {
                Player target = commandContext.get(targetArg).findFirstPlayer(commandSender);
                if (target == null) {
                    commandSender.sendMessage("<red>Invalid target");
                    return;
                }
                target.setGameMode(GameMode.SPECTATOR);
                commandSender.sendMessage("<green>Set gamemode to spectator");
                target.sendMessage("<green>Your gamemode has been updated");
            }, targetArg);
        }

    }

}
