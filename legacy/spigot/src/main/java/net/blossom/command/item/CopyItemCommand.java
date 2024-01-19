package net.blossom.command.item;

import dev.jorel.commandapi.CommandAPICommand;
import net.blossom.command.BlossomCommand;
import net.blossom.core.Blossom;
import net.blossom.player.Rank;
import net.blossom.utils.HasteBin;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class CopyItemCommand implements BlossomCommand {
    @Override
    public @NotNull CommandAPICommand create() {
        return new CommandAPICommand("copyitem")
                .withRequirement(Rank.OWNER)
                .executesPlayer((sender, context) -> {
                    ItemStack held = sender.getInventory().getItemInMainHand();
                    if (held.getType().isAir()) return;
                    CompoundTag tag = CraftItemStack.asNMSCopy(held).getOrCreateTag();
                    String tagString = tag.getAsString();
                    String itemMetaJson = held.getItemMeta().getAsString();
                    StringBuilder sb = new StringBuilder();
                    sb.append("COMPOUND TAG JSON:\n");
                    sb.append(tagString);
                    sb.append("\n\nITEM META JSON:\n");
                    sb.append(itemMetaJson);
                    String hasteUrl = HasteBin.createPaste(sb.toString());
                    Blossom.getChatManager().sendMessage(sender, "<green>Pasted to <click:open_url:" + hasteUrl + ">HasteBin</click>");
                });
    }
}
