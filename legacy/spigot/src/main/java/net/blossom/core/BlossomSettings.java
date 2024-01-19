package net.blossom.core;

public record BlossomSettings(
        String hastebinToken,
        double globalXpMultiplier
) {

    public static final BlossomSettings DEFAULT = new BlossomSettings(
            "net/blossom",
            1.0
    );
}
