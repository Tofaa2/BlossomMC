package net.blossom.proxy.commands;

import com.velocitypowered.api.proxy.Player;
import net.blossom.proxy.BlossomPlayer;
import net.blossom.proxy.BlossomProxy;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendsCommand extends BlossomCommand {

    public FriendsCommand() {
        super("friends", "commands.friends", "friend", "f");
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player sender)) {
            return;
        }
        if (invocation.arguments().length == 0) {
            sender.sendMessage(Component.text("Usage: /friends <add|remove|list|join>", NamedTextColor.RED));
            return;
        }
        BlossomPlayer player = BlossomProxy.getInstance().getPlayer(sender);
        String subCommand = invocation.arguments()[0];
        switch (subCommand.toLowerCase()) {
            case "add" -> {
                if (invocation.arguments().length != 2) {
                    sender.sendMessage(Component.text("Usage: /friends add [player]", NamedTextColor.RED));
                    return;
                }
                Player target = BlossomProxy.getInstance().getServer().getPlayer(invocation.arguments()[1]).orElse(null);
                if (target == null) {
                    sender.sendMessage(nullPlayerMessage);
                    return;
                }
                if (target == sender) {
                    sender.sendMessage(Component.text("You can't be friends with yourself, that's a bit sad", NamedTextColor.RED));
                    return;
                }
                if (player.isFriend(target.getUniqueId())) {
                    sender.sendMessage(Component.text("You are already friends with that player.", NamedTextColor.RED));
                    return;
                }
                player.addFriend(target.getUniqueId());
                Component msg = Component.text("You are now friends with " + target.getUsername() + ".", NamedTextColor.GREEN);
                if (player.isMutualFriend(target.getUniqueId())) {
                    msg = msg.append(Component.text(" You are now mutual friends.", NamedTextColor.GOLD));
                }
                sender.sendMessage(msg);
            }
            case "remove" -> {
                if (invocation.arguments().length != 2) {
                    sender.sendMessage(Component.text("Usage: /friends remove [player]", NamedTextColor.RED));
                    return;
                }
                Player target = BlossomProxy.getInstance().getServer().getPlayer(invocation.arguments()[1]).orElse(null);
                if (target == null) {
                    sender.sendMessage(nullPlayerMessage);
                    return;
                }
                if (target == sender) {
                    sender.sendMessage(Component.text("You can't un-friend yourself, that's a bit sad", NamedTextColor.RED));
                    return;
                }
                if (!player.isFriend(target.getUniqueId())) {
                    sender.sendMessage(Component.text("You are not friends with that player.", NamedTextColor.RED));
                    return;
                }
                player.removeFriend(target.getUniqueId());
                sender.sendMessage(Component.text("You are no longer friends with " + target.getUsername() + ".", NamedTextColor.GREEN));
            }
            case "list" -> {
                if (invocation.arguments().length > 2) {
                    sender.sendMessage(Component.text("Usage: /friends list", NamedTextColor.RED));
                    return;
                }
                Book.Builder book = Book.builder()
                        .author(Component.text("Blossom"))
                        .title(Component.text("Friends List", NamedTextColor.GOLD));
                Collection<UUID> friends = player.getFriends();
                int perPage = 10;
                int pages = (int) Math.ceil(friends.size() / (double) perPage);
                List<Component> pageComponents = new ArrayList<>(pages);
                Component page = Component.empty();
                int i = 0;
                for (UUID friendId : friends) {
                    BlossomPlayer friend = BlossomProxy.getInstance().getPlayer(friendId);
                    page = page.append(Component.text(friend.getUsername()));
                    if (player.isMutualFriend(friendId)) {
                        page = page.append(Component.text(" (mutual)", NamedTextColor.GOLD));
                    }
                    if (player.isOnline()) {
                            page = page.append(Component.text(" - ", NamedTextColor.GRAY))
                                .append(Component.text("Click To Join", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)
                                        .clickEvent(ClickEvent.runCommand("/friends join " + friend.getUsername())));
                    }
                    page = page.append(Component.newline());
                    i++;
                    if (i == perPage) {
                        pageComponents.add(page);
                        page = Component.empty();
                        i = 0;
                    }
                }
                if (i == 0) {
                    sender.sendMessage(Component.text("You have no friends. ):", NamedTextColor.RED));
                    return;
                }
                if (i < perPage) {
                    pageComponents.add(page);
                }
                sender.openBook(book.pages(pageComponents).build());
             }
            case "join" -> {
                if (invocation.arguments().length != 2) {
                    sender.sendMessage(Component.text("Usage: /friends join [player]", NamedTextColor.RED));
                    return;
                }
                Player target = BlossomProxy.getInstance().getServer().getPlayer(invocation.arguments()[1]).orElse(null);
                if (target == null) {
                    sender.sendMessage(nullPlayerMessage);
                    return;
                }
                if (target == sender) {
                    sender.sendMessage(Component.text("You can't join yourself.", NamedTextColor.RED));
                    return;
                }
                if (!player.isFriend(target.getUniqueId())) {
                    sender.sendMessage(Component.text("You are not friends with that player.", NamedTextColor.RED));
                    return;
                }
                BlossomProxy.getInstance().getServer().getPlayer(target.getUniqueId()).ifPresent(p -> {
                    sender.sendMessage(Component.text("Sending you to " + target.getUsername() + "...", NamedTextColor.GREEN));
                    sender.createConnectionRequest(p.getCurrentServer().get().getServer()).fireAndForget();
                });
            }
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(List.of("add", "remove", "list", "join"));
    }
}
