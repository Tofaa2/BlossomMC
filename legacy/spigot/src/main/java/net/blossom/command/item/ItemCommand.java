package net.blossom.command.item;

import net.blossom.item.BlossomItem;
import net.blossom.player.Rank;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.blossom.command.BlossomCommand;
import net.blossom.core.Blossom;
import org.jetbrains.annotations.NotNull;

public class ItemCommand implements BlossomCommand {
    @Override
    public @NotNull CommandAPICommand create() {
        return new CommandAPICommand("item")
                .withRequirement(Rank.OWNER)
                .withAliases("i", "customitem")
                .withArguments(
                        new StringArgument("item")
                                .includeSuggestions(ArgumentSuggestions.strings(Blossom.getItemManager().getItemIds()))
                )
                .withOptionalArguments(
                        new IntegerArgument("amount", 1, 64)
                )
                .executesPlayer((sender, context) -> {
                    BlossomItem item = Blossom.getItemManager().getFromId(context.getUnchecked("item"));
                    if (item == null) {
                        Blossom.getChatManager().sendMessage(sender, "<red>Invalid item.");
                        return;
                    }
                    var optional = context.getOptional("amount");
                    if (optional.isEmpty()) {
                        item.give(sender);
                        return;
                    }
                    if (optional.get() instanceof Integer amount) {
                        item.give(sender, amount);
                        return;
                    }
                    Blossom.getChatManager().sendMessage(sender, "<red>Invalid amount.");
                });
    }
}
