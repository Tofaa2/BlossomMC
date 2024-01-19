package net.blossom.core;

import net.blossom.entity.BlossomPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.condition.CommandCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class BlossomCommand extends Command {

    public static BlossomCommand fast(@NotNull Rank rank, @NotNull String name, @Nullable List<String> aliases, @NotNull CommandExecutor executor, @NotNull Argument<?>... args) {

        BlossomCommand cmd;
        if (aliases == null) {
            cmd = new BlossomCommand(rank, name);
        } else {
            String[] aliasesArray = aliases.toArray(new String[0]);
            cmd = new BlossomCommand(rank, name, aliasesArray);
        }
        cmd.addSyntax(executor, args);
        return cmd;
    }

    public static BlossomCommand fast(@NotNull Rank rank, @NotNull String name, @NotNull CommandExecutor executor, @NotNull Argument<?>... args) {
        return fast(rank, name, null, executor, args);
    }

    public static BlossomCommand fast(@NotNull String name, @NotNull CommandExecutor executor, @NotNull Argument<?>...args) {
        return fast(Rank.MEMBER, name, executor, args);
    }

    public static BlossomCommand fast(@NotNull String name, @NotNull List<String> aliases, CommandExecutor executor, Argument<?>... args) {
        return fast(Rank.MEMBER, name, aliases, executor, args);
    }

    private final CommandCondition rankCondition;


    public BlossomCommand(@NotNull Rank rank, @NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
        this.rankCondition = (sender, commandString) -> {
            if (!(sender instanceof BlossomPlayer player)) return true;
            return player.getRank().inherits(rank);
        };
    }

    public BlossomCommand(@NotNull Rank rank, @NotNull String name) {
        super(name);
        this.rankCondition = (sender, commandString) -> {
            if (!(sender instanceof BlossomPlayer player)) return true;
            return player.getRank().inherits(rank);
        };
    }

    public BlossomCommand(@NotNull String name, @Nullable String... aliases) {
        this(Rank.MEMBER, name, aliases);
    }

    public BlossomCommand(@NotNull String name) {
        this(Rank.MEMBER, name);
    }

    @Override
    public @NotNull Collection<CommandSyntax> addSyntax(@NotNull CommandExecutor executor, @NotNull Argument<?>... args) {
        return addConditionalSyntax(rankCondition, executor, args);
    }
}
