package net.blossom.core;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlossomMCPlugin extends JavaPlugin {


    private Blossom blossom;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        CommandAPIBukkitConfig commandConfig = new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true);
        CommandAPI.onLoad(commandConfig);
        CommandAPI.onEnable();
        blossom = new Blossom(this);
    }

    @Override
    public void onDisable() {
        Blossom.getScoreboardLibrary().close();
    }

    public Blossom getBlossom() {
        return blossom;
    }
}
