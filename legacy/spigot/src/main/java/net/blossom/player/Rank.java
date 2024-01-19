package net.blossom.player;

import net.blossom.core.Blossom;
import net.kyori.adventure.text.Component;


public enum Rank {
    OWNER("<gradient:#ff0000:#ff7f00>Owner</gradient>", "<gradient:#ff0000:#ff7f00>"),
    ADMIN("<gradient:#ff7f00:#ffbf00>Admin</gradient>", "<gradient:#ff7f00:#ffbf00>"),
    MEMBER("<gradient:#00ff00:#00ff7f>Member</gradient>", "<gradient:#00ff00:#00ff7f>"),
    ;
    private final String prefix;
    private final String nameColor;

    Rank(String prefix, String nameColor) {
        this.prefix = prefix;
        this.nameColor = nameColor;
    }

    public boolean inherits(Rank other) {
        return this.ordinal() <= other.ordinal();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNameColor() {
        return nameColor;
    }

    public Component getPrefixComponent() {
        return Blossom.getChatManager().createMessage(prefix, false);
    }

    public Component getNameColorComponent() {
        return Blossom.getChatManager().createMessage(nameColor, false);
    }

}
