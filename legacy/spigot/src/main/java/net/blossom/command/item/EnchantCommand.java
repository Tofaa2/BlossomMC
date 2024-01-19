package net.blossom.command.item;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EnchantmentArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.blossom.command.BlossomCommand;
import net.blossom.core.Blossom;
import net.blossom.player.Rank;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class EnchantCommand implements BlossomCommand {
    @Override
    public @NotNull CommandAPICommand create() {
        return new CommandAPICommand("enchant")
                .withRequirement(Rank.OWNER)
                .withArguments(
                        new EnchantmentArgument("enchantment"),
                        new IntegerArgument("level", 1, Short.MAX_VALUE)
                )
                .executesPlayer((sender, context) -> {
                    Object enchantment = context.get("enchantment");
                    if (enchantment == null) {
                        Blossom.getChatManager().sendMessage(sender, "<red>Invalid enchantment.");
                        return;
                    }
                    ItemStack held = sender.getInventory().getItemInMainHand();
                    if (held.getType().isAir()) {
                        Blossom.getChatManager().sendMessage(sender, "<red>You need to be holding an item in your hand.");
                        return;
                    }
                    ItemStack newItem = held.clone();
                    newItem.addUnsafeEnchantment((Enchantment) enchantment, context.getOrDefaultUnchecked("level", 1));
                    sender.getInventory().setItemInMainHand(newItem);
                    Blossom.getChatManager().sendMessage(sender, "<green>Successfully enchanted item with <yellow>" + ((Enchantment) enchantment).getKey().getKey() + "<green>.");
                });
    }
}
