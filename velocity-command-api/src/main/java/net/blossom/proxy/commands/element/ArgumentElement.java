package net.blossom.proxy.commands.element;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ArgumentElement<T>(
        @NotNull String name,
        @NotNull ArgumentType<T> type,
        @Nullable SuggestionProvider<CommandSource> suggestionProvider
) implements CommandElement {

    @Override
    public @NotNull ArgumentBuilder<CommandSource, ?> toBuilder() {
        var builder = RequiredArgumentBuilder.<CommandSource, T>argument(this.name, this.type);
        if (this.suggestionProvider != null) builder.suggests(this.suggestionProvider);
        return builder;
    }
}
