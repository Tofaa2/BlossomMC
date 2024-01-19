package net.blossom.hub.features.parkour;

import com.google.auto.service.AutoService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import net.blossom.core.Feature;
import net.blossom.core.FeatureDepends;
import net.blossom.dbm.BlossomDatabase;
import net.blossom.hub.Hub;
import net.blossom.hub.features.HubFeature;
import net.blossom.hub.features.HubPlayer;
import net.minestom.server.entity.Player;
import org.bson.Document;

import java.util.*;

@AutoService(Feature.class)
@FeatureDepends(HubFeature.class)
public class ParkourFeature extends Feature {

    private Map<String, ParkourMap> maps = new HashMap<>();

    @Override
    public void init() {
        reloadMaps();
    }

    public void reloadMaps() {
        maps.clear();
        BlossomDatabase db = Hub.getDatabase();
        FindIterable<Document> results = db.find("parkour-maps", new Document());
        for (Document result : results) {
            String name = Objects.requireNonNull(result.getString("name"));
            List<ParkourCheckpoint> checkpoints = result.get("checkpoints", List.class);
            Map<UUID, Float> bestTimes = dbGetBestTimes(name);
            maps.put(name, new ParkourMap(name, bestTimes, checkpoints));
        }
    }

    public void saveMaps() {
        MongoCollection<Document> collection = Hub.getDatabase().getCollection("parkour-maps");
        for (ParkourMap map : maps.values()) {
            Document document = new Document("name", map.getName());
            document.append("checkpoints", map.getCheckpoints());
            collection.replaceOne(new Document("name", map.getName()), document, BlossomDatabase.UPSERT);
        }
    }

    private Map<UUID, Float> dbGetBestTimes(String map) {
        BlossomDatabase db = Hub.getDatabase();
        FindIterable<Document> results = db.find("players", new Document());
        Map<UUID, Float> bestTimes = new HashMap<>();
        for (Document result : results) {
            UUID uuid = UUID.fromString(result.getString("uuid"));
            Player online = process().connection().getOnlinePlayerByUuid(uuid);
            if (online != null) {
                HubPlayer player = (HubPlayer) online;
                if (player.getParkourTimes().containsKey(map)) {
                    bestTimes.put(uuid, player.getParkourTimes().get(map));
                }
                continue;
            }
            Map<String, Float> parkourTimes = result.get("parkour-times", Map.class);
            if (parkourTimes == null || !parkourTimes.containsKey(map)) {
                continue;
            }
            float time = parkourTimes.get(map);
            bestTimes.put(uuid, time);
        }
        return bestTimes;
    }


}
