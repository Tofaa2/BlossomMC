package net.blossom.chat;

import com.google.auto.service.AutoService;
import net.blossom.core.Blossom;
import net.blossom.core.Feature;
import net.blossom.entity.BlossomPlayer;
import net.blossom.utils.RandomUtils;
import net.blossom.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.timer.Task;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@AutoService(Feature.class)
public final class ChatFeature extends Feature {

    private File chatFile;
    private MiniMessage miniMessage;
    private Config<ChatSettings> chatConfig;
    private Task task;


    @Override
    public void init() {
        this.chatFile = new File(DATA_FOLDER, "chat.json");
        reload();
        getEventNode().addListener(PlayerSpawnEvent.class, event -> {
            sendJoinMessage((BlossomPlayer) event.getPlayer());
        });
        getEventNode().addListener(PlayerDisconnectEvent.class, event -> {
            sendQuitMessage((BlossomPlayer) event.getPlayer());
        });

        Component splitter = Component.text(": ", NamedTextColor.DARK_GRAY);
        Function<PlayerChatEvent, Component> chatRenderer = (event) -> {
            BlossomPlayer p  = (BlossomPlayer) event.getPlayer();
            return createMessage(p.getRank().getPrefix() + " " + p.getRank().getNameColor() + p.getUsername(), false).append(splitter).append(Component.text(event.getMessage()));
        };
        getEventNode().addListener(PlayerChatEvent.class, event -> event.setChatFormat(chatRenderer));
    }


    public Component pureWhite(String input) {
        return Component.text(input).decoration(TextDecoration.ITALIC, false);
    }

    public Component pure(String input) {
        return createMessage(input, false).decoration(TextDecoration.ITALIC, false);
    }


    public void sendQuitMessage(BlossomPlayer player) {
        String message = chatConfig.get().quitMessage();
        message = message.replace("{player}", player.getUsername());
        sendMessage(message, true);
    }

    public void sendJoinMessage(BlossomPlayer player) {
        String message = chatConfig.get().joinMessage();
        message = message.replace("{player}", player.getUsername());
        sendMessage(message, true);
    }

    public Collection<Component> createPatchNotesList() {
        Collection<String> patchNotes = chatConfig.get().patchNotes();
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
        for (var entry : chatConfig.get().placeholders().entrySet()) {
            tagBuilder = tagBuilder.resolver(Placeholder.parsed(entry.getKey(), entry.getValue()));
        }
        this.miniMessage = MiniMessage.builder().tags(tagBuilder.build()).build();

        if (task != null) {
            task.cancel();
        }
        Duration broadcastDelay = Duration.ofSeconds(chatConfig.get().broadcastDelay());
        task = async(() -> {
            List<String> messages = chatConfig.get().broadcastMessages();
            String message = messages.get(RandomUtils.nextInt(messages.size()));
            sendMessage(message, true);
        }, broadcastDelay, broadcastDelay);
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
        Component msg = createMessage(message, prefix);
        process().connection().getOnlinePlayers().forEach(p -> {
            p.sendMessage(msg);
        });
        Blossom.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(msg));
    }
}
