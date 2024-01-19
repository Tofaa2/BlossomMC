package net.blossom.core;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.CommandSyntax;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class BlossomCommand extends Command {

    public static BlossomCommand fast(@Nullable String permission, @NotNull String name, @Nullable List<String> aliases, @NotNull CommandExecutor executor, @NotNull Argument<?>... args) {

        BlossomCommand cmd;
        if (aliases == null) {
            cmd = new BlossomCommand(permission, name);
        } else {
            String[] aliasesArray = aliases.toArray(new String[0]);
            cmd = new BlossomCommand(permission, name, aliasesArray);
        }
        cmd.addSyntax(executor, args);
        return cmd;
    }

    public static BlossomCommand fast(@Nullable String permission, @NotNull String name, @NotNull CommandExecutor executor, @NotNull Argument<?>... args) {
        return fast(permission, name, null, executor, args);
    }

    public static BlossomCommand fast(@NotNull String name, @NotNull CommandExecutor executor, @NotNull Argument<?>...args) {
        return fast(null, name, executor, args);
    }

    public static BlossomCommand fast(@NotNull String name, @NotNull List<String> aliases, CommandExecutor executor, Argument<?>... args) {
        return fast(null, name, aliases, executor, args);
    }

    private final CommandCondition permissionCondition;

    public BlossomCommand(@Nullable String permission, @NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
        if (permission == null) this.permissionCondition = null;
        else this.permissionCondition = (sender, commandString) -> {
            if (!(sender instanceof Player player)) return true;
            return player.hasPermission(permission);
        };
    }

    public BlossomCommand(@Nullable String permission, @NotNull String name) {
        super(name);
        if (permission == null) this.permissionCondition = null;
        else this.permissionCondition = (sender, commandString) -> {
            if (!(sender instanceof Player player)) return true;
            return player.hasPermission(permission);
        };
    }

    public BlossomCommand(@NotNull String name, @Nullable String... aliases) {
        this(null, name, aliases);
    }

    public BlossomCommand(@NotNull String name) {
        this(null, name);
    }

    @Override
    public @NotNull Collection<CommandSyntax> addSyntax(@NotNull CommandExecutor executor, @NotNull Argument<?>... args) {
        return addConditionalSyntax(permissionCondition, executor, args);
    }

}
