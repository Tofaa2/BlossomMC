package net.blossom.listeners;

import net.blossom.core.Blossom;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class BedtimeListener implements Listener {

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Blossom.getChatManager().sendMessage("<green>" + event.getPlayer().getName() + " has entered sleep, skipping the night", true);
        for (World world : Bukkit.getWorlds()) {
            world.setTime(0);
        }
    }

}
