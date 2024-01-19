package net.blossom.music;

import net.blossom.chat.ChatFeature;
import net.blossom.core.BlossomCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

import java.util.Collection;
import java.util.Collections;

import static net.blossom.core.Rank.ADMIN;

final class SongCommand extends BlossomCommand {

    SongCommand(MusicFeature feature) {
        super(ADMIN, "song");
        var songArgNew = ArgumentType.StringArray("song")
                .setSuggestionCallback((sender, context, suggestion) -> {
                    for (var name: feature.getSongNames()) {
                        suggestion.addEntry(new SuggestionEntry(name));
                    }
                });
        var particlesArg = ArgumentType.Boolean("particles");
        var playersArg = ArgumentType.Entity("players").onlyPlayers(true);
        addSyntax(((sender, context) -> execute(feature, sender, context.get(songArgNew), context.get(particlesArg), Collections.singleton((Player) sender))), particlesArg, songArgNew);
        addSyntax((sender, context) -> execute(feature, sender, context.get(songArgNew), true, Collections.singleton((Player) sender)), songArgNew);
        addSyntax((sender, context) -> {
            Collection<Player> players = context.get(playersArg).find(sender)
                    .stream().map(e -> (Player) e).toList();
            execute(feature, sender, context.get(songArgNew), context.get(particlesArg), players);
        }, particlesArg, playersArg, songArgNew);
    }

    private void execute(MusicFeature feature, CommandSender sender, String[] songName, boolean particles, Collection<Player> players) {
        if (!(sender instanceof Player player)) return;
        var song = feature.getSongFromName(String.join(" ", songName));
        if (song == null) {
            player.sendMessage("<red>No song found with that name.");
            return;
        }
        feature.play(song, players, particles);
        Component msg = ChatFeature.getFeature(ChatFeature.class).createMessage("<green>Playing song <white>" + song.getSongName() + "<green> by <white>" + song.getOriginalAuthor() + "<green>.");
        player.sendMessage(msg);
        players.forEach(p -> p.sendMessage(msg));
    }


}
