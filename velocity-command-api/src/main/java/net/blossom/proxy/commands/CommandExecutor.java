package net.blossom.proxy.commands;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;

public interface CommandExecutor {

    void execute(@NotNull CommandContext<CommandSource> context);

}
