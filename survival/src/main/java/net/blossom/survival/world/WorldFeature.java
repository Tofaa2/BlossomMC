package net.blossom.survival.world;

import com.google.auto.service.AutoService;
import net.blossom.core.BlossomCommand;
import net.blossom.core.BlossomPlayer;
import net.blossom.core.Feature;
import net.blossom.survival.world.terrain.BiomeBase;
import net.blossom.survival.world.terrain.MinestomHook;
import net.blossom.survival.world.terrain.World;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@AutoService(Feature.class)
public class WorldFeature extends Feature {

    private DimensionType fullbright;
    private Pos spawnPoint;
    private InstanceContainer hub;
    private World overworld;

    @Override
    public void init() {
        fullbright = DimensionType.builder(NamespaceID.from("blossom:fullbright_dimension"))
			.ultrawarm(true)
                .natural(true)
                .piglinSafe(false)
                .respawnAnchorSafe(false)
                .bedSafe(true)
                .raidCapable(false)
                .skylightEnabled(true)
                .ceilingEnabled(false)
                .fixedTime(null)
                .ambientLight(1.0f)
                .height(128)
                .minY(0)
                .logicalHeight(128)
                .infiniburn(NamespaceID.from("minecraft:infiniburn_overworld"))
                .build();
        process().dimension().addDimension(fullbright);
        BiomeBase.init();
        spawnPoint = new Pos(-128, 64, -8, -178, -8);
        hub = process().instance().createInstanceContainer(fullbright, new AnvilLoader("hub"));
        overworld = new World(UUID.randomUUID(), fullbright, new AnvilLoader("overworld"));
        overworld.enableAutoChunkLoad(true);
        process().instance().registerInstance(overworld);
        overworld.setGenerator(new MinestomHook(overworld, new Random().nextInt()));


        getEventNode().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(overworld);
            event.getPlayer().setRespawnPoint(spawnPoint);
        });

        registerCommands(
                BlossomCommand.fast("hub", (sender, context) -> {
                    ((Player) sender).setInstance(hub, spawnPoint);
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
