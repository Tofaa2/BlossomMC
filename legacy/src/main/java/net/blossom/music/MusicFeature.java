package net.blossom.music;

import com.google.auto.service.AutoService;
import net.blossom.core.BlossomCommand;
import net.blossom.core.Feature;
import net.blossom.core.FeatureDepends;
import net.blossom.core.LoadAfter;
import net.blossom.gui.GuiFeature;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.adventure.MinestomAdventure;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.golem.SnowGolemMeta;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@AutoService(Feature.class)
@FeatureDepends(
        GuiFeature.class
)
public class MusicFeature extends Feature {

    private final Map<UUID, Task> playingTaskMap = new ConcurrentHashMap<>();
    private final HashMap<String, Song> cachedSongs = new HashMap<>();
    private MusicCommand command;

    @Override
    public void init() {
        File songsDir = new File(DATA_FOLDER, "songs");
        if (!songsDir.exists()) {
            songsDir.mkdirs();
        }
        for (File file : Objects.requireNonNull(songsDir.listFiles((dir, name) -> name.endsWith(".nbs")))) {
            if (file.isDirectory()) continue;
            try {
                //getLogger().info("Loading song " + file.getName());
                Song song = new Song(file.toPath());
                cachedSongs.put(file.getName().replace(".nbs", ""), song);
            }
            catch (IOException e) {
                getLogger().info("Failed to load song " + file.getName(), e);
            }
        }
        command = new MusicCommand(this);
        registerCommands(
                command,
                new SongCommand(this),
                BlossomCommand.fast("stopsong", (sender, context) -> {
                    if (!(sender instanceof Player player)) return;
                    stop(player);
                })
        );
    }

    public Set<String> getSongNames() {
        return cachedSongs.keySet();
    }

    public @Nullable Song getSongFromName(String name) {
        return cachedSongs.get(name);
    }

    public void openMusicInventory(Player player) {
        player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 2f));
        command.getGui().open(player);
    }

    public void play(@NotNull Song song, Player player) {
        play(song, player, player.scheduler(), player.getUuid());
    }

    public void play(@NotNull Song song, @NotNull Player player, boolean withParticles) {
        if (withParticles) {
            playWithParticles(song, Collections.singleton(player), player.scheduler(), player.getUuid());
        }
        else {
            play(song, player, player.scheduler(), player.getUuid());
        }
    }

    public void play(@NotNull Song song, @NotNull Collection<Player> players, boolean withParticles) {
        if (withParticles) {
            playWithParticles(song, players, players.iterator().next().scheduler(), players.iterator().next().getUuid());
        }
        else {
            Audience audience = Audiences.players(p -> players.contains(p));
            play(song, audience, players.iterator().next().scheduler(), players.iterator().next().getUuid());
        }
    }

    public void play(@NotNull Song song, Audience audience, Scheduler scheduler, UUID stopId) {
        playingTaskMap.put(stopId, scheduler.submitTask(new Supplier<>() {
            int tick = 0;
            @Override
            public TaskSchedule get() {
                if (tick > song.getLength() + 1) {
                    return TaskSchedule.stop();
                }
                List<Sound> sounds = song.getTicks().get(tick);
                if (sounds != null) {
                    for (Sound sound : sounds) {
                        audience.playSound(sound, Sound.Emitter.self());

                    }
                }
                tick++;
                return TaskSchedule.millis((long) (1000.0 / song.getTps()));
            }
        }));
    }

    public void playWithParticles(@NotNull Song song, @NotNull Collection<Player> audience, Scheduler scheduler, UUID stopId) {
        playingTaskMap.put(stopId, scheduler.submitTask(new Supplier<>() {

            final ThreadLocalRandom random = ThreadLocalRandom.current();
            int tick = 0;
            @Override
            public TaskSchedule get() {
                if (tick > song.getLength() + 1) {
                    return TaskSchedule.stop();
                }
                List<Sound> sounds = song.getTicks().get(tick);
                if (sounds != null) {
                    for (Sound sound : sounds) {
                        for (Player player : audience) {
                            Pos pos = player.getPosition().add(random.nextDouble(-0.65, 0.65), 1, random.nextDouble(-0.65, 0.65));
                            ParticlePacket packet = ParticleCreator.createParticlePacket(
                                    Particle.NOTE,
                                    false,
                                    pos.x(), pos.y(), pos.z(),
                                    0f, 0f, 0f,
                                    (tick / 24f), 1, null);
                            player.playSound(sound, Sound.Emitter.self());
                            player.sendPacketToViewersAndSelf(packet);
                        }
                    }
                }
                tick++;
                return TaskSchedule.millis((long) (1000.0 / song.getTps()));
            }
        }));
    }

    public void stop(Player player) {
        stop(player.getUuid());
    }
    public void stop(UUID stopId) {
        Task task = playingTaskMap.get(stopId);
        if (task != null) {
            task.cancel();
        }
        playingTaskMap.remove(stopId);
    }
}
