package net.blossom.item.commands;

import net.blossom.core.BlossomCommand;
import net.blossom.core.Rank;
import net.blossom.item.Item;
import net.blossom.entity.BlossomPlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public final class ItemCommand extends BlossomCommand {

    public ItemCommand() {
        super(Rank.ADMIN, "item", "i", "items", "give");
        ArgumentEntity eArg = ArgumentType.Entity("target").onlyPlayers(true).singleEntity(true);
        Argument<Integer> numArg = ArgumentType.Integer("amount").setDefaultValue(1);
        Argument<String> idArg = ArgumentType.String("id").setSuggestionCallback((sender, context, suggestion) -> {
            for (var id : Item.Registry.getIds()) {
                suggestion.addEntry(new SuggestionEntry(id));
            }
        });
        addSyntax((sender, context) -> handle(sender, context.get(idArg), context.get(numArg), context.get(eArg).findFirstPlayer(sender)), eArg, idArg, numArg);
        addSyntax((sender, context) -> handle(sender, context.get(idArg), 1,context.get(eArg).findFirstPlayer(sender)), eArg, idArg);
        addSyntax((sender, context) -> handle(sender, context.get(idArg), context.get(numArg), (Player) sender), idArg, numArg);
        addSyntax((sender, context) -> handle(sender, context.get(idArg), 1, (Player) sender), idArg);
    }

    private void handle(CommandSender sender, String id, int amount, Player player) {
        Item i = Item.Registry.get(id);
        if (i == null) {
            sender.sendMessage("<red>Invalid item id");
            return;
        }
        if (player == null) {
            sender.sendMessage("<red>Invalid player");
            return;
        }
        i = i.withAmount(amount);
        ((BlossomPlayer) player).addItem(i);
        player.sendMessage("<green>You have been given <gold>" + amount + "x " + i.getName() + "<green>!");
        sender.sendMessage("<green>You have given <gold>" + amount + "x " + i.getName() + "<green> to <gold>" + player.getUsername() + "<green>!");
    }

}
