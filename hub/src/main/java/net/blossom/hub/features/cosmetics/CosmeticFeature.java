package net.blossom.hub.features.cosmetics;

import com.google.auto.service.AutoService;
import net.blossom.core.Feature;
import net.blossom.hub.features.HubPlayer;
import net.minestom.server.event.trait.PlayerEvent;

@AutoService(Feature.class)
public class CosmeticFeature extends Feature {

    @Override
    public void init() {
        for (Cosmetic<? extends PlayerEvent> cosmetic : Cosmetic.getAll()) {
            registerCosmetic(cosmetic);
        }
        registerCommands(new CosmeticsCommand());
    }

    private <T extends PlayerEvent> void registerCosmetic(Cosmetic<T> cosmetic) {
        getEventNode().addListener(cosmetic.eventClass(), event -> {
            Cosmetic<?> active = ((HubPlayer) event.getPlayer()).getActiveCosmetic();
            if (active == null || !active.eventClass().equals(cosmetic.eventClass())) {
                return;
            }
            cosmetic.eventConsumer().accept(event);
        });
    }
}
