package net.blossom.survival.player;

import com.google.auto.service.AutoService;
import net.blossom.core.BlossomCommand;
import net.blossom.core.Feature;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;

@AutoService(Feature.class)
public final class PlayerFeature extends Feature {

    @Override
    public void init() {
        process().connection().setPlayerProvider(SMPPlayer::new);

        registerCommands(
                BlossomCommand.fast("gm", (sender, context) -> {
                    ((SMPPlayer) sender).setGameMode(context.get("gamemode"));
                }, ArgumentType.Enum("gamemode", GameMode.class))
        );
    }
}
