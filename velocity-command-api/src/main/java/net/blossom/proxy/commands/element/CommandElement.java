package net.blossom.proxy.commands.element;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;

public interface CommandElement {

    @NotNull ArgumentBuilder<CommandSource, ?> toBuilder();

}
