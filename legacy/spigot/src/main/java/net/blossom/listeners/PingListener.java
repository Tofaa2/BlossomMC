package net.blossom.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PingListener implements Listener {


    @EventHandler
    public void onPing(PaperServerListPingEvent event) {
        event.motd(Component.text("I forgot to change this!", NamedTextColor.RED));
    }

}
