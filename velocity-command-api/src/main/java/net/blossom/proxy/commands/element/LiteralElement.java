package net.blossom.proxy.commands.element;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;

public record LiteralElement(@NotNull String name) implements CommandElement{
    @Override
    public @NotNull ArgumentBuilder<CommandSource, ?> toBuilder() {
        return LiteralArgumentBuilder.literal(name);
    }
}
