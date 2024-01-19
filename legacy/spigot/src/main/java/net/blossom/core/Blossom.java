package net.blossom.core;

import net.blossom.command.item.CopyItemCommand;
import net.blossom.command.item.EnchantCommand;
import net.blossom.command.item.ItemCommand;
import net.blossom.command.item.ToggleItemFlagsCommand;
import net.blossom.command.player.HomeCommand;
import net.blossom.command.player.PatchNotesCommand;
import net.blossom.item.ItemManager;
import net.blossom.listeners.BedtimeListener;
import net.blossom.listeners.PingListener;
import net.blossom.listeners.PlayerDamageListener;
import net.blossom.player.PlayerManager;
import net.blossom.recipe.RecipeProvider;
import net.blossom.recipe.vanilla.VanillaToolRecipes;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import net.blossom.command.BlossomCommand;
import net.blossom.utils.config.Config;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Blossom {

    private static BlossomMCPlugin plugin;
    private static ChatManager chatManager;
    private static PlayerManager playerManager;
    private static ScoreboardLibrary scoreboardLibrary;
    private static ItemManager itemManager;
    private static Config<BlossomSettings> blossomSettings;

    Blossom(BlossomMCPlugin plugin) {
        Blossom.plugin = plugin;
        blossomSettings = Config.createAndLoad(BlossomSettings.class, new File(plugin.getDataFolder(), "settings.json").toPath(), BlossomSettings.DEFAULT);
        chatManager = new ChatManager();
        playerManager = new PlayerManager();
        itemManager = new ItemManager();
        itemManager.init();
        registerBukkitListeners(
                new PingListener(),
                new BedtimeListener(),
                new PlayerDamageListener()
        );
        registerCommands(
                new PatchNotesCommand(),
                new EnchantCommand(),
                new ItemCommand(),
                new CopyItemCommand(),
                new ToggleItemFlagsCommand(),
                new HomeCommand()
        );
        registerRecipes(VanillaToolRecipes.ALL);
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLibrary = new NoopScoreboardLibrary();
        }
    }

    public static Config<BlossomSettings> getBlossomSettings() {
        return blossomSettings;
    }

    public static void registerRecipe(Recipe recipe) {
        plugin.getServer().addRecipe(recipe);
    }

    public static void registerRecipe(RecipeProvider provider) {
        registerRecipe(provider.provide());
    }

    public static void registerRecipes(RecipeProvider... providers) {
        for (RecipeProvider provider : providers) {
            registerRecipe(provider);
        }
    }

    public static NamespacedKey newKey(String key) {
        return new NamespacedKey(plugin, key);
    }

    public static ItemManager getItemManager() {
        return itemManager;
    }

    public static void registerCommands(BlossomCommand... commands) {
        if (!CommandAPI.isLoaded()) {
            plugin.getLogger().warning("CommandAPI is not loaded!");
            return;
        }
        for (BlossomCommand command : commands) {
            CommandAPICommand c = command.create();
            CommandAPICommand[] many = command.createMany();
            if (c != null) {
                c.register();
            }
            if (many != null) {
                for (CommandAPICommand commandAPICommand : many) {
                    commandAPICommand.register();
                }
            }
        }
    }

    public static String getServerTime(World world) {
        long time = world.getTime();
        long hours = (time / 1000 + 6) % 24;
        long minutes = (time % 1000) * 60 / 1000;
        return String.format("%02d:%02d", hours, minutes);
    }

    public static ScoreboardLibrary getScoreboardLibrary() {
        return scoreboardLibrary;
    }

    public static BlossomMCPlugin getPlugin() {
        return plugin;
    }

    public static void registerBukkitListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static void registerBukkitListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            registerBukkitListener(listener);
        }
    }

    public static void registerPacketListener(PacketListenerCommon listener) {
        PacketEvents.getAPI().getEventManager().registerListener(listener);
    }

    public static @NotNull ChatManager getChatManager() {
        return chatManager;
    }

    public static @NotNull File getDataFolder() {
        return plugin.getDataFolder();
    }

    public static @NotNull PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static BukkitTask async(Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static BukkitTask async(Runnable runnable, long delay) {
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public static BukkitTask async(Runnable runnable, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    public static BukkitTask sync(Runnable runnable) {
        return plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public static BukkitTask sync(Runnable runnable, long delay) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static BukkitTask sync(Runnable runnable, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

}

