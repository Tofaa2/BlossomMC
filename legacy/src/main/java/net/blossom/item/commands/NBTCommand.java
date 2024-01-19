package net.blossom.item.commands;

import net.blossom.core.BlossomCommand;
import net.blossom.utils.HasteBin;
import net.blossom.utils.JsonUtils;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import static net.blossom.core.Feature.async;

public class NBTCommand extends BlossomCommand {

    public NBTCommand() {
        super("nbtdata");
        addSyntax(((sender, context) -> {
            if (!(sender instanceof Player player)) return;
            ItemStack held = player.getInventory().getItemInMainHand();
            if (held.isSimilar(ItemStack.AIR)) {
                player.sendMessage("You must be holding an item!");
                return;
            }
            async(() -> {
                String metaJson = held.meta().toSNBT();
                String haste = HasteBin.createPaste(JsonUtils.prettyString(metaJson));
                player.sendMessage("<green>Item NBT data: <yellow><click:open_url:" + haste + ">Click here</click>");
            });
        }));
    }

}
