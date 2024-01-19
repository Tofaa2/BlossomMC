package net.blossom.proxy.punishments;

import java.util.UUID;

public record Punishment(
        UUID id,
        long issuedAt,
        long time,
        String reason,
        UUID issuer,
        PunishmentType type
) {
}
