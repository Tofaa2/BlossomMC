package net.blossom.proxy.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import net.blossom.proxy.commands.element.CommandElement;
import net.blossom.proxy.commands.element.LiteralElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

record Graph(@NotNull Node root) {

    static  @NotNull Graph create(@NotNull Command command) {
        return new Graph(Node.command(command));
    }

    private static @NotNull CommandElement commandToElement(@NotNull Command command) {
        return new LiteralElement(command.getName());
    }

    @NotNull LiteralCommandNode<CommandSource> build() {
        CommandNode<CommandSource> node = this.root.build();
        if (!(node instanceof LiteralCommandNode<CommandSource> literalNode)) {
            throw new IllegalStateException("Root node is somehow not a literal node. This should be impossible.");
        }
        return literalNode;
    }

    record Node(@NotNull CommandElement element, @Nullable Execution execution, @NotNull List<Node> children) {

        static @NotNull Node command(@NotNull Command command) {
            return ConversionNode.fromCommand(command).toNode();
        }

        @NotNull CommandNode<CommandSource> build() {
            ArgumentBuilder<CommandSource, ?> builder = this.element.toBuilder();
            if (this.execution != null) this.execution.addToBuilder(builder);

            for (Node child : this.children) {
                builder.then(child.build());
            }

            return builder.build();
        }
    }

    record Execution(@NotNull Predicate<CommandSource> predicate, @Nullable CommandExecutor defaultExecutor, @Nullable CommandExecutor executor,
                        @Nullable Predicate<CommandSource> condition) implements Predicate<CommandSource> {

        static @NotNull Execution fromCommand(@NotNull Command command) {
            CommandExecutor defaultExecutor = command.getDefaultExecutor();
            Predicate<CommandSource> defaultCondition = command.getCondition();

            CommandExecutor executor = defaultExecutor;
            Predicate<CommandSource> condition = defaultCondition;
            for (CommandSyntax syntax : command.getSyntaxes()) {
                if (!syntax.elements().isEmpty()) continue;
                executor = syntax.executor();
                condition = syntax.condition();
                break;
            }

            return new Execution(source -> defaultCondition == null || defaultCondition.test(source), defaultExecutor, executor, condition);
        }

        static @NotNull Execution fromSyntax(@NotNull CommandSyntax syntax) {
            CommandExecutor executor = syntax.executor();
            Predicate<CommandSource> condition = syntax.condition();
            return new Execution(source -> condition == null || condition.test(source), null, executor, condition);
        }

        @Override
        public boolean test(@NotNull CommandSource source) {
            return this.predicate.test(source);
        }

        void addToBuilder(@NotNull ArgumentBuilder<CommandSource, ?> builder) {
            if (this.condition != null) builder.requires(this.condition);
            if (this.executor != null) {
                builder.executes(convertExecutor(this.executor));
            } else if (this.defaultExecutor != null) {
                builder.executes(convertExecutor(this.defaultExecutor));
            }
        }

        private static com.mojang.brigadier.@NotNull Command<CommandSource> convertExecutor(@NotNull CommandExecutor executor) {
            return context -> {
                Thread.startVirtualThread(() -> executor.execute(context));
                return 1;
            };
        }
    }

    private record ConversionNode(@NotNull CommandElement element, @Nullable Execution execution,
                                     @NotNull Map<CommandElement, ConversionNode> nextMap) {

        static @NotNull ConversionNode fromCommand(@NotNull Command command) {
            ConversionNode root = new ConversionNode(commandToElement(command), Execution.fromCommand(command));

            for (CommandSyntax syntax : command.getSyntaxes()) {
                ConversionNode syntaxNode = root;

                for (CommandElement element : syntax.elements()) {
                    boolean last = element == syntax.elements().get(syntax.elements().size() - 1);
                    syntaxNode = syntaxNode.nextMap.computeIfAbsent(element, e -> {
                        Execution execution = last ? Execution.fromSyntax(syntax) : null;
                        return new ConversionNode(e, execution);
                    });
                }
            }

            for (Command subCommand : command.getSubCommands()) {
                root.nextMap.put(commandToElement(subCommand), fromCommand(subCommand));
            }

            return root;
        }

        ConversionNode(@NotNull CommandElement element, @Nullable Execution execution) {
            this(element, execution, new LinkedHashMap<>());
        }

        private Node toNode() {
            @SuppressWarnings("unchecked") // this is fine - we only put Node<S> in to this array
            Node[] nodes = new Node[this.nextMap.size()];

            int i = 0;
            for (ConversionNode entry : this.nextMap.values()) {
                nodes[i++] = entry.toNode();
            }

            return new Node(this.element, this.execution, List.of(nodes));
        }
    }
}