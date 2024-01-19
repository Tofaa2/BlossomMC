package net.blossom.core;

import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.DimensionTypeManager;

public final class ExtraDimensionTypes {

    public static final DimensionType FULLBRIGHT = DimensionType.builder(NamespaceID.from("blossom:fullbright"))
            .ambientLight(2.0f)
            .build();

    private ExtraDimensionTypes() {}

    static void init() {
        DimensionTypeManager manager = MinecraftServer.getDimensionTypeManager();
        manager.addDimension(FULLBRIGHT);
    }


}
