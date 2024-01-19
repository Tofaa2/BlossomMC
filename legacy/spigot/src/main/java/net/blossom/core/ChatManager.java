package net.blossom.core;

import net.blossom.player.BlossomPlayer;
import net.blossom.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ChatManager {

    private final File chatFile;
    private MiniMessage miniMessage;
    private Config<ChatSettings> chatConfig;
    private BukkitTask task;

    ChatManager() {
        this.chatFile = new File(Blossom.getDataFolder(), "chat.json");
        reload();
    }


    public void sendQuitMessage(BlossomPlayer player) {
        String message = chatConfig.get().quitMessage;
        message = message.replace("{player}", player.getRank().getPrefix() + " " + player.getRank().getNameColor() + player.getPlayer().getName());
        sendMessage(message, true);
    }

    public void sendJoinMessage(BlossomPlayer player) {
        String message = chatConfig.get().joinMessage;
        message = message.replace("{player}", player.getRank().getPrefix() + " " + player.getRank().getNameColor() + player.getPlayer().getName());
        sendMessage(message, true);
    }


    public Component createMessage(String message) {
        return createMessage(message, true);
    }

    public List<Component> createMessages(String... messages) {
        List<Component> components = new ArrayList<>(messages.length);
        for (String s : messages) {
            components.add(createMessage(s));
        }
        return components;
    }

    public List<Component> createMessages(Collection<String> messages) {
        List<Component> components = new ArrayList<>(messages.size());
        for (String s : messages) {
            components.add(createMessage(s));
        }
        return components;
    }

    public Component createMessage(String message, boolean prefix) {
        return miniMessage.deserialize(prefix ? "<prefix>" + message : message);
    }

    public void sendMessage(CommandSender sender, String message, boolean prefix) {
        sender.sendMessage(createMessage(message, prefix));
    }

    public void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, true);
    }

    public void sendMessage(Collection<CommandSender> senders, String message) {
        senders.forEach(sender -> sendMessage(sender, message));
    }

    public void sendMessage(String message, boolean prefix) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            sendMessage(p, message, prefix);
        });
    }

    public Collection<Component> createPatchNotesList() {
        Collection<String> patchNotes = chatConfig.get().patchNotes;
        ArrayList<Component> components = new ArrayList<>(patchNotes.size());
        for (String s : patchNotes)  {
            components.add(createMessage(s, false));
        }
        return components;
    }

    public void reload() {
        this.chatConfig = Config.createAndLoad(ChatSettings.class, chatFile.toPath(), ChatSettings.DEFAULTS);

        TagResolver.Builder tagBuilder = TagResolver.builder();
        tagBuilder = tagBuilder.resolver(TagResolver.standard());
        for (var entry : chatConfig.get().placeholders.entrySet()) {
            tagBuilder = tagBuilder.resolver(Placeholder.parsed(entry.getKey(), entry.getValue()));
        }
        this.miniMessage = MiniMessage.builder().tags(tagBuilder.build()).build();

        if (task != null) {
            task.cancel();
        }
        long broadcastDelay = chatConfig.get().broadcastDelay * 20L;
        task = Blossom.async(() -> {
            List<String> messages = chatConfig.get().broadcastMessages;
            String message = messages.get(RandomUtils.nextInt(messages.size()));
            sendMessage(message, true);
        }, broadcastDelay, broadcastDelay);
    }

    public Component pureWhite(String input) {
        return Component.text(input).decoration(TextDecoration.ITALIC, false);
    }

    public Component pure(String input) {
        return createMessage(input, false).decoration(TextDecoration.ITALIC, false);
    }

    public record ChatSettings(
            Map<String, String> placeholders,
            String joinMessage,
            String quitMessage,
            int broadcastDelay,
            List<String> broadcastMessages,
            List<String> patchNotes
    ) {

        private static final ChatSettings DEFAULTS = new ChatSettings(
                Map.of("prefix", "<gradient:#00ffe1:#ff00a2>Blossom</gradient><gray> > "),
                "{player} <gray>has joined the server.",
                "{player} <gray>has left the server.",
                300,
                List.of(
                        "<red>Don't forget to join our nonexistent discord server!",
                        "<rainbow>I have no idea what to put here."
                ),
                List.of("We are finally released?")
        );
    }
}
