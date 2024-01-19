package net.blossom.command.player;

import dev.jorel.commandapi.CommandAPICommand;
import net.blossom.command.BlossomCommand;
import net.blossom.core.Blossom;
import net.kyori.adventure.inventory.Book;
import org.jetbrains.annotations.NotNull;

public class PatchNotesCommand implements BlossomCommand {

    public static final Book PATCH_BOOK = Book.builder()
            .title(Blossom.getChatManager().createMessage("<gold>Patch Notes", false))
            .pages(Blossom.getChatManager().createPatchNotesList()).build();

    @Override
    public @NotNull CommandAPICommand create() {
        return new CommandAPICommand("patchnotes")
                .executesPlayer((player, context) -> {
                    player.openBook(PATCH_BOOK);
                });
    }
}
