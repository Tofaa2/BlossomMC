package net.blossom.chat;

import java.util.List;
import java.util.Map;

public record ChatSettings(
        Map<String, String> placeholders,
        String joinMessage,
        String quitMessage,
        int broadcastDelay,
        List<String> broadcastMessages,
        List<String> patchNotes
) {

    static final ChatSettings DEFAULTS = new ChatSettings(
            Map.of("prefix", "<gradient:#00ffe1:#ff00a2>Blossom</gradient><gray> > "),
            "{player} <gray>has joined the server.",
            "{player} <gray>has left the server.",
            300,
            List.of(
                    "<red>Don't forget to join our nonexistent discord server!",
                    "<rainbow>I have no idea what to put here."
            ),
            List.of("We are finally released?")
    );
}