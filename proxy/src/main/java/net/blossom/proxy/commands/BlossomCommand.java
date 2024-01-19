package net.blossom.proxy.commands;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public abstract class BlossomCommand implements SimpleCommand {

    protected static final Component nullPlayerMessage = Component.text("That player is not online.", NamedTextColor.RED);

    private final String name;
    private final String[] aliases;
    private final String permission;

    public BlossomCommand(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    public BlossomCommand(String name, String permission) {
        this(name, permission, new String[0]);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(permission);
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }
}
