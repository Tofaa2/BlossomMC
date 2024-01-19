package net.blossom.entity;

import net.blossom.chat.ChatFeature;
import net.blossom.core.Feature;
import net.blossom.utils.TickContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.scoreboard.Sidebar;

public final class PlayerSidebar {

    private static final ChatFeature CHAT_FEATURE = Feature.getFeature(ChatFeature.class);
    private static final Component TITLE = Component.text("Blossom", TextColor.color(0x00FF00));


    private final BlossomPlayer player;
    private TickContainer<Sidebar> tickContainer;




    PlayerSidebar(BlossomPlayer player) {
         this.player = player;
         this.tickContainer = new TickContainer<>(5, new Sidebar(TITLE), sidebar -> {
         });
    }

}
