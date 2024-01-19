package net.blossom.proxy.commands;

import com.velocitypowered.api.proxy.Player;
import net.blossom.proxy.BlossomPlayer;
import net.blossom.proxy.BlossomProxy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WarnCommand extends BlossomCommand {

    public WarnCommand() {
        super("warn", "commands.warn");
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("Usage: /warn <player> <reason>", NamedTextColor.RED));
            return;
        }
        Player player = BlossomProxy.getInstance().getServer().getPlayer(args[0]).orElse(null);
        if (player == null) {
            invocation.source().sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return;
        }
        BlossomPlayer p = BlossomProxy.getInstance().getPlayer(player.getUniqueId());
        UUID issuer;
        if (invocation.source() instanceof Player) {
            issuer = ((Player) invocation.source()).getUniqueId();
        }
        else {
            issuer = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
        p.warn(String.join(" ", args).substring(args[0].length() + 1), issuer);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if (invocation.arguments().length == 1) {
            return CompletableFuture.completedFuture(BlossomProxy.getInstance().getServer().getAllPlayers().stream().map(Player::getUsername).toList());
        }
        return CompletableFuture.completedFuture(List.of());
    }

}
