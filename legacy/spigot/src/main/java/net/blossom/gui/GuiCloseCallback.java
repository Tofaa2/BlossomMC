package net.blossom.gui;

import net.blossom.player.BlossomPlayer;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GuiCloseCallback {

    void onClose(@NotNull Gui gui, @NotNull BlossomPlayer player);

}
