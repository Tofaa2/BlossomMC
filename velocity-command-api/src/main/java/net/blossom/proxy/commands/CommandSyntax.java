package net.blossom.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import net.blossom.proxy.commands.element.CommandElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public record CommandSyntax(
        @Nullable Predicate<CommandSource> condition,
        @NotNull CommandExecutor executor,
        @NotNull List<CommandElement> elements
) { }
