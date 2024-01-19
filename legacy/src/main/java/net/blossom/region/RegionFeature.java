package net.blossom.region;

import com.google.auto.service.AutoService;
import net.blossom.core.BlossomCommand;
import net.blossom.core.Feature;
import net.blossom.core.FeatureDepends;
import net.blossom.core.Rank;
import net.blossom.entity.BlossomPlayer;
import net.blossom.utils.JsonUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;


@AutoService(Feature.class)
public final class RegionFeature extends Feature {

    private final Map<UUID, SimpleRegion> regions = new HashMap<>();
    private File regionsDir;

    @Override
    public void init() {
        regionsDir = new File(DATA_FOLDER, "regions");
        if (!regionsDir.exists()) {
            regionsDir.mkdirs();
        }
        File[] files = regionsDir.listFiles();
        if (files != null) {
            for (File file : files) {
                SimpleRegion region = JsonUtils.castJson(file, SimpleRegion.class);
                if (region == null) {
                    getLogger().error("Could not load region from file " + file.getName());
                    continue;
                }
                regions.put(region.getUuid(), region);
            }
        }
        registerCommands(BlossomCommand.fast(
                Rank.ADMIN, "regionstest", (sender, context) -> {
                    BlossomPlayer p = (BlossomPlayer) sender;
                    boolean b = createRegion(new SimpleRegion(UUID.randomUUID(), p.getUuid(), "Test Region", new HashSet<>(), p.getPosition(), p.getPosition().asVec(), p.getPosition().asVec()));
                    p.sendMessage("Region created: " + b);
                    saveRegionToStorage(getRegion("Test Region"));
                }
        ));
    }

    @Override
    public void terminate() {
        saveRegionsToStorage();
    }

    public @Nullable SimpleRegion getRegion(String name) {
        return regions.values().stream()
                .filter(region -> region.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public @Nullable SimpleRegion getRegion(UUID uuid) {
        return regions.get(uuid);
    }

    public boolean createRegion(SimpleRegion region) {
        if (getRegion(region.getName()) != null) {
            return false;
        }
        regions.put(region.getUuid(), region);
        return true;
    }

    public void saveRegionToStorage(SimpleRegion region) {
        JsonUtils.writeJson(new File(regionsDir, region.getName().toLowerCase() + ".json"), region, SimpleRegion.class);
    }

    public void saveRegionsToStorage() {
        for (SimpleRegion region : regions.values()) {
            saveRegionToStorage(region);
        }
    }

}
