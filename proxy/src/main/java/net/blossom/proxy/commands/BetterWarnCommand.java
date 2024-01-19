package net.blossom.proxy.commands;

import com.velocitypowered.api.proxy.Player;
import net.blossom.proxy.BlossomPlayer;
import net.blossom.proxy.BlossomProxy;

import java.util.UUID;

public final class BetterWarnCommand extends Command {

    public BetterWarnCommand() {
        super("betterwarn");
        setCondition(s -> s.hasPermission("commands.betterwarn"));
        var playerArg = Player("player", BlossomProxy.getInstance().getServer());
        var reasonArg = StringArray("reason");
        addSyntax((context) -> {
            Player player = context.getArgument("player", Player.class);
            String reason = context.getArgument("reason", String.class);
            BlossomPlayer p = BlossomProxy.getInstance().getPlayer(player.getUniqueId());
            UUID issuer;
            if (context.getSource() instanceof Player) {
                issuer = ((Player) context.getSource()).getUniqueId();
            }
            else {
                issuer = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }
            p.warn(reason, issuer);
        }, playerArg, reasonArg);
    }

}
