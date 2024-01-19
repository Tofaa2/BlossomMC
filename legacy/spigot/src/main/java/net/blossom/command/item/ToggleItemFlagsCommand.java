package net.blossom.command.item;

import net.blossom.utils.EnumUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import net.blossom.command.BlossomCommand;
import net.blossom.core.Blossom;
import net.blossom.player.Rank;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ToggleItemFlagsCommand implements BlossomCommand {
    @Override
    public @NotNull CommandAPICommand create() {

        Argument<String> flagg = new StringArgument("flag").includeSuggestions(ArgumentSuggestions.strings(EnumUtils.getNames(ItemFlag.class)));

        return new CommandAPICommand("toggleitemflags")
                .withArguments(flagg)
                .withRequirement(Rank.OWNER)
                .executesPlayer((sender, context) -> {
                    Optional<Object> flag = context.getOptional("flag");
                    if (flag.isEmpty()) {
                        Blossom.getChatManager().sendMessage(sender, "<red>Invalid flag");
                        return;
                    }
                    ItemFlag itemFlag = ItemFlag.valueOf(flag.get().toString().toUpperCase());
                    Blossom.getChatManager().sendMessage(sender, "<green>Flag " + itemFlag.name() + " toggled");
                    ItemStack held = sender.getInventory().getItemInMainHand().clone();
                    ItemMeta meta = held.getItemMeta();
                    if (meta.hasItemFlag(itemFlag)) {
                        meta.removeItemFlags(itemFlag);
                    } else {
                        meta.addItemFlags(itemFlag);
                    }
                    held.setItemMeta(meta);
                    sender.getInventory().setItemInMainHand(held);
                });
    }
}
