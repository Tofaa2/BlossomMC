package net.blossom.music;

import net.blossom.core.BlossomCommand;
import net.blossom.gui.Gui;
import net.blossom.gui.GuiButton;
import net.blossom.music.MusicDisc;
import net.blossom.music.MusicFeature;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
final class MusicCommand extends BlossomCommand {

    private final Map<UUID, Task> stopPlayingTaskMap = new ConcurrentHashMap<>();
    private final Tag<Integer> playingDiscTag = Tag.Integer("playingDisc");
    private final MusicFeature feature;
    private final Gui gui;

    MusicCommand(MusicFeature feature) {
        super("music");
        this.feature = feature;

        var gui = Gui.builder(InventoryType.CHEST_6_ROW, Component.text("Music Discs", NamedTextColor.BLACK));
        var i = 10;
        for (MusicDisc disc : MusicDisc.values()) {
            if ((i + 1) % 9 == 0) i += 2;
            gui.withButton(i, new GuiButton(ItemStack.builder(disc.getMaterial())
                    .displayName(
                            Component.text(disc.getDescription(), NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                    )
                    .meta((meta) -> {
                        // For some reason the disc author lore requires this hide flag
                        meta.hideFlag(ItemHideFlag.HIDE_POTION_EFFECTS);

                        if (disc == MusicDisc.MUSIC_DISC_WAIT) meta.lore(Component.text("where are we now", TextColor.color(46, 17, 46)).decoration(TextDecoration.ITALIC, false));
                    })
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        MusicDisc nowPLaying = MusicDisc.fromMaterial(event.getClickedItem().material());
                        playDisc(event.getPlayer(), nowPLaying.getShortName());
                    }
            ));
            i++;
        }
        gui.withButton(40, new GuiButton(ItemStack.builder(Material.BARRIER)
                .displayName(Component.text("Stop", NamedTextColor.RED))
                .build(),
                event -> {
                    event.setCancelled(true);
                    stop(event.getPlayer());
                }
        ));
        this.gui = gui.build();
        setDefaultExecutor((sender, ctx) -> {
            if (!(sender instanceof Player player)) return;
            feature.openMusicInventory(player);
        });

        Argument<String> stopArgument = new ArgumentLiteral("stop");
        Argument<String[]> discArgument = new ArgumentStringArray("disc").setSuggestionCallback((sender, context, suggestion) -> {
            for (MusicDisc disc : MusicDisc.values()) {
                suggestion.addEntry(new SuggestionEntry(disc.getShortName()));
            }
        });

        addSyntax((sender, ctx) -> {
            stop(sender);
        }, stopArgument);

        addSyntax((sender, ctx) -> {
            playDisc(sender, String.join(" ", ctx.get(discArgument)));
        }, discArgument);
    }

    Gui getGui() {
        return gui;
    }

    public void playDisc(CommandSender sender, String disc) {
        if (!(sender instanceof Player player)) return;

        List<MusicDisc> discValues = Arrays.asList(MusicDisc.values());

        stop(sender);

        MusicDisc nowPlayingDisc = MusicDisc.valueOf("MUSIC_DISC_" + disc.toUpperCase());

        String discName = nowPlayingDisc.getDescription();

        player.setTag(playingDiscTag, discValues.indexOf(nowPlayingDisc));
        player.playSound(Sound.sound(nowPlayingDisc.getSound(), Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());

        stopPlayingTaskMap.put(player.getUuid(), player.scheduler().buildTask(() -> {
            stop(sender);
        }).delay(Duration.ofSeconds(nowPlayingDisc.getLength())).schedule());

        player.sendActionBar(
                Component.text()
                        .append(Component.text("Playing: ", NamedTextColor.GRAY))
                        .append(Component.text(discName, NamedTextColor.AQUA))
        );
    }

    public void stop(CommandSender sender) {
        if (!(sender instanceof Player player)) return;

        MusicDisc[] discValues = MusicDisc.values();

        Integer playingDisk = player.getTag(playingDiscTag);
        if (playingDisk == null) return;
        MusicDisc playingDisc = discValues[playingDisk];

        player.stopSound(SoundStop.named(playingDisc.getSound()));
        player.removeTag(playingDiscTag);

        Task stopPlayingTask = stopPlayingTaskMap.get(player.getUuid());
        if (stopPlayingTask != null) {
            stopPlayingTask.cancel();
            stopPlayingTaskMap.remove(player.getUuid());
        }
        feature.stop(player);
    }

}
