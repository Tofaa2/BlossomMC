package net.blossom.item;

import com.google.auto.service.AutoService;
import net.blossom.chat.ChatFeature;
import net.blossom.core.Feature;
import net.blossom.core.FeatureDepends;
import net.blossom.core.LoadAfter;
import net.blossom.item.commands.EnchantCommand;
import net.blossom.item.commands.ItemCommand;
import net.blossom.item.commands.NBTCommand;
import net.blossom.item.enchantment.ItemEnchant;
import net.blossom.item.enchantment.ItemEnchantHeader;
import net.blossom.item.types.EnchantableItem;
import net.blossom.entity.BlossomPlayer;

import java.util.Set;

@AutoService(Feature.class)
@FeatureDepends(ChatFeature.class)
public class ItemFeature extends Feature {

    @Override
    public void postInit() {
        Items.Registry.init(this);
        registerCommands(
                new ItemCommand(),
                new EnchantCommand(),
                new NBTCommand()
        );

        Set<ItemEnchantHeader> enchants = ItemEnchantHeader.values();
        for (ItemEnchantHeader enchant : enchants) {
            for (var eClass : enchant.trigger().events()) {
                getEventNode().addListener(eClass, event -> {
                    final BlossomPlayer p = (BlossomPlayer) event.getPlayer();
                    for (var item : p.getEquippedItems()) {
                        if (!(item instanceof EnchantableItem e)) continue;
                        ItemEnchant ench = e.getEnchantment(enchant);
                        if (ench == null) continue;
                        enchant.trigger().handle().handle(ench, p, e);
                    }
                });
            }
        }
    }
}
