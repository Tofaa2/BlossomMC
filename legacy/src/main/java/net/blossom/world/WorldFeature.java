package net.blossom.world;

import com.google.auto.service.AutoService;
import net.blossom.core.BlossomCommand;
import net.blossom.core.Feature;
import net.blossom.entity.BlossomPlayer;
import net.blossom.world.old.OverworldGenerator;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@AutoService(Feature.class)
public class WorldFeature extends Feature {

    private DimensionType fullbright;
    private Pos spawnPoint;
    private InstanceContainer hub;
    private InstanceContainer overworld;

    @Override
    public void init() {

        fullbright = DimensionType.builder(NamespaceID.from("blossom:fullbright"))
                .ambientLight(2.0f)
                .build();
        process().dimension().addDimension(fullbright);
        spawnPoint = new Pos(-128, 64, -8, -178, -8);
        hub = process().instance().createInstanceContainer(fullbright, new AnvilLoader("hub"));
        overworld = process().instance().createInstanceContainer(fullbright, new AnvilLoader("overworld"));
        overworld.setGenerator(new OverworldGenerator(overworld,1234567));
        getEventNode().addListener(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(hub);
            event.getPlayer().setRespawnPoint(spawnPoint);
        });

        registerCommands(
                BlossomCommand.fast("hub", (sender, context) -> {
                    ((BlossomPlayer) sender).setInstance(hub, spawnPoint);
                }),
                BlossomCommand.fast("randomteleport", List.of("rtp"), (sender, context) -> {
                    final AtomicInteger y = new AtomicInteger();
                    int x = (int) (Math.random() * 1000 - 500);
                    int z = (int) (Math.random() * 1000 - 500);
                    overworld.loadChunk(new Vec(x, 0, z)).thenRun(() -> {
                        for (int currentY = overworld.getDimensionType().getMaxY(); currentY > 0; currentY--) {
                            if (overworld.getBlock(x, currentY, z).isAir() && !overworld.getBlock(x, currentY - 1, z).isAir()) {
                                y.set(currentY);
                                ((BlossomPlayer) sender).setInstance(overworld, new Pos(x, y.get(), z));
                                return;
                            }
                        }
                        throw new RuntimeException("Could not find a suitable location to teleport to");
                    });
                })
        );
    }

    public Pos getSpawnPoint() {
        return spawnPoint;
    }

    public InstanceContainer getHub() {
        return hub;
    }

    public InstanceContainer getOverworld() {
        return overworld;
    }
}
