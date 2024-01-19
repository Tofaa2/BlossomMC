package net.blossom.gui;

import com.google.auto.service.AutoService;
import net.blossom.chat.ChatFeature;
import net.blossom.core.Feature;
import net.blossom.core.FeatureDepends;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

@AutoService(Feature.class)
@FeatureDepends(ChatFeature.class)
public class GuiFeature extends Feature {


    @Override
    public void init() {
        getEventNode().addListener(InventoryPreClickEvent.class, event -> {
            Inventory inventory = event.getInventory();

            if(!(inventory instanceof GuiImpl gui)) return;
            event.setCancelled(true);

            GuiButton button = gui.getButtonAt(event.getSlot());
            if(button != null && button.clickHandler() != null) {
                button.clickHandler().accept(event);
            }

            if(gui.getClickHandler() != null) gui.getClickHandler().accept(event);
        });
        getEventNode().addListener(InventoryCloseEvent.class, event -> {
            Inventory inventory = event.getInventory();

            if(!(inventory instanceof GuiImpl gui)) return;

            if(gui.getCloseHandler() != null)
                gui.getCloseHandler().accept(event);
        });
    }



    public @Nullable Gui getGui(Player player) {
        if (player.getOpenInventory() == null) return null;
        if (!(player.getOpenInventory() instanceof GuiImpl gui)) return null;
        return gui;
    }

}
