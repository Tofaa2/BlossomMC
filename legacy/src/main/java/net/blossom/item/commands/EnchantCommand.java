package net.blossom.item.commands;

import net.blossom.core.BlossomCommand;
import net.blossom.core.Rank;
import net.blossom.item.Item;
import net.blossom.item.enchantment.ItemEnchant;
import net.blossom.item.enchantment.ItemEnchantHeader;
import net.blossom.item.types.EnchantableItem;
import net.blossom.entity.BlossomPlayer;
import net.minestom.server.command.builder.arguments.Argument;

import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class EnchantCommand extends BlossomCommand {

    public EnchantCommand() {
        super(Rank.ADMIN, "enchant", "enchantment", "ench");

        Argument<String> enchArg = ArgumentType.String("enchantment")
                .setSuggestionCallback((sender, context, suggestion) -> {
                    for (ItemEnchantHeader header : ItemEnchantHeader.values()) {
                        suggestion.addEntry(new SuggestionEntry(header.name()));
                    }
                });

        ArgumentInteger levelArg = ArgumentType.Integer("level");

        addSyntax((sender, context) -> {
            if (!(sender instanceof BlossomPlayer player)) return;
            String enchName = context.get(enchArg);
            ItemEnchantHeader header = ItemEnchantHeader.get(enchName);
            if (header == null) {
                sender.sendMessage("<red>Invalid enchantment: " + enchName);
                return;
            }
            int level = context.get(levelArg);
            ItemEnchant enchant = new ItemEnchant(level, header);
            Item i = Item.fromItemStack(player.getInventory().getItemInMainHand());
            if ((!(i instanceof EnchantableItem item))) {
                sender.sendMessage("<red>Item in hand is not enchantable!");
                return;
            }
            boolean b = item.addEnchantment(enchant);
            if (!b) {
                sender.sendMessage("<red>Item in hand is not compatible with " + header.name());
                return;
            }
            player.getInventory().setItemInMainHand(item.toItemStack());
            player.sendMessage("<green>Enchanted item in hand with " + header.name() + " " + level);
        }, enchArg, levelArg);
    }

}
