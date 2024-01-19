package net.blossom.proxy.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.blossom.proxy.commands.element.ArgumentElement;
import net.blossom.proxy.commands.element.CommandElement;
import net.blossom.proxy.commands.element.LiteralElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class Command {

    public static void register(ProxyServer server, Command... commands) {
        for (var command : commands) {
            server.getCommandManager().register(new BrigadierCommand(command.build()));
        }
    }

    private final String name;
    private final String[] aliases;

    private @Nullable Predicate<CommandSource> condition;
    private @Nullable CommandExecutor defaultExecutor;

    private final List<CommandSyntax> syntaxes = new ArrayList<>();
    private final List<Command> subCommands = new ArrayList<>();

    public Command(@NotNull String name, @NotNull String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public Command(@NotNull String name) {
        this(name, new String[0]);
    }

    protected final void setCondition(@NotNull Predicate<CommandSource> condition) {
        this.condition = condition;
    }

    protected final void setDefaultExecutor(@NotNull CommandExecutor executor) {
        this.defaultExecutor = executor;
    }

    @SafeVarargs
    protected final void addConditionalSyntax(@Nullable Predicate<CommandSource> condition, @NotNull CommandExecutor executor,
                                              @NotNull CommandElement... elements) {
        var syntax = new CommandSyntax(condition, executor, List.of(elements));
        this.syntaxes.add(syntax);
    }

    @SafeVarargs
    protected final void addSyntax(@NotNull CommandExecutor executor, @NotNull CommandElement... elements) {
        this.addConditionalSyntax(null, executor, elements);
    }

    protected final void addSubCommand(@NotNull Command command) {
        this.subCommands.add(command);
    }

    public final @NotNull LiteralCommandNode<CommandSource> build() {
        return Graph.create(this).build();
    }

    public final @NotNull String getName() {
        return this.name;
    }

    public final @NotNull String[] getAliases() {
        return this.aliases;
    }

    final @Nullable Predicate<CommandSource> getCondition() {
        return this.condition;
    }

    final @Nullable CommandExecutor getDefaultExecutor() {
        return this.defaultExecutor;
    }

    final @NotNull Collection<CommandSyntax> getSyntaxes() {
        return this.syntaxes;
    }

    final @NotNull Collection<Command> getSubCommands() {
        return this.subCommands;
    }

    protected static  @NotNull LiteralElement Literal(@NotNull String name) {
        return new LiteralElement(name);
    }

    protected static <T> @NotNull ArgumentElement<T> Argument(@NotNull String name, @NotNull ArgumentType<T> type, @Nullable SuggestionProvider<CommandSource> suggestions) {
        return new ArgumentElement<>(name, type, suggestions);
    }

    protected static @NotNull ArgumentElement<Integer> Integer(@NotNull String name, int min, int max) {
        return Argument(name, IntegerArgumentType.integer(min, max), null);
    }

    protected static @NotNull ArgumentElement<Integer> Integer(@NotNull String name) {
        return Argument(name, IntegerArgumentType.integer(), null);
    }

    protected static @NotNull ArgumentElement<Double> Double(@NotNull String name, double min, double max) {
        return Argument(name, DoubleArgumentType.doubleArg(min, max), null);
    }

    protected static @NotNull ArgumentElement<Double> Double(@NotNull String name) {
        return Argument(name, DoubleArgumentType.doubleArg(), null);
    }

    protected static @NotNull ArgumentElement<String> Word(@NotNull String name, @Nullable SuggestionProvider<CommandSource> suggestions) {
        return Argument(name, StringArgumentType.word(), suggestions);
    }

    protected static @NotNull ArgumentElement<String> Word(@NotNull String name) {
        return Argument(name, StringArgumentType.word(), null);
    }

    protected static @NotNull ArgumentElement<String> StringArray(@NotNull String name, @Nullable SuggestionProvider<CommandSource> suggestions) {
        return Argument(name, StringArgumentType.greedyString(), suggestions);
    }

    protected static @NotNull ArgumentElement<String> StringArray(@NotNull String name) {
        return Argument(name, StringArgumentType.greedyString(), null);
    }

    protected static @NotNull ArgumentElement<String> String(@NotNull String name, @Nullable SuggestionProvider<CommandSource> suggestions) {
        return Argument(name, StringArgumentType.string(), suggestions);
    }

    protected static @NotNull ArgumentElement<String> String(@NotNull String name) {
        return Argument(name, StringArgumentType.string(), null);
    }

    protected static @NotNull ArgumentElement<Player> Player(@NotNull String name, @NotNull ProxyServer server) {
        ArgumentType<Player> type = stringReader -> server.getPlayer(stringReader.readString()).orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(stringReader));
        SuggestionProvider<CommandSource> suggestionProvider = (commandContext, suggestionsBuilder) -> {
            for (Player player : server.getAllPlayers()) {
                suggestionsBuilder.add(new SuggestionsBuilder(player.getUsername(), 0));
            }
            return suggestionsBuilder.buildFuture();
        };
        return Argument(name, type, suggestionProvider);
    }

}
