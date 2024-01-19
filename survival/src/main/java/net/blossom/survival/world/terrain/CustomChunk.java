package net.blossom.survival.world.terrain;

import net.blossom.survival.world.terrain.events.ChunkPopulatedEvent;
import net.blossom.survival.world.terrain.events.ChunkUnloadAttemptEvent;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.DynamicChunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CustomChunk extends DynamicChunk {

    private boolean populated = false;
    private long markedForPopulation = 0L;
    private long timeSinceLastPlayer = 0;

    public CustomChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
        super(instance, chunkX, chunkZ);
    }

    public boolean isPopulated() {
        return populated;
    }

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    public long getMarkedForPopulation() {
        return markedForPopulation;
    }

    public void setMarkedForPopulation(long markedForPopulation) {
        this.markedForPopulation = markedForPopulation;
    }

    public long getTimeSinceLastPlayer() {
        return timeSinceLastPlayer;
    }

    public void setTimeSinceLastPlayer(long timeSinceLastPlayer) {
        this.timeSinceLastPlayer = timeSinceLastPlayer;
    }

    public boolean isMarkedForPopulation() {
        return System.currentTimeMillis() - markedForPopulation < 1000 * 60;
    }

    @Override
    protected void onLoad() {
        this.loaded = true;
        super.onLoad();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Sets the chunk as "unloaded".
     */
    @Override
    protected void unload() {
        this.loaded = false;
    }

    @Override
    public boolean addViewer(@NotNull Player player) {
        // this should very rarely be needed. but it is :(
        if (!isPopulated() && !isMarkedForPopulation()) doPopulate("addViewer");
        return super.addViewer(player);
    }

    public void doPopulate() {
        doPopulate("no idea");
    }
    public synchronized void doPopulate(String reason) {
        if (!reason.equalsIgnoreCase("force") && (isMarkedForPopulation() || isPopulated())) return;
        setMarkedForPopulation(System.currentTimeMillis());

        var chunks = new ArrayList<CompletableFuture<Chunk>>();
        ChunkUtils.forChunksInRange(getChunkX(), getChunkZ(), 1, (x, z) ->
                chunks.add(((World)getInstance()).loadChunk(x, z, false))
        );
        CompletableFuture.supplyAsync(() -> {
                    CompletableFuture.allOf(chunks.toArray(CompletableFuture[]::new)).join();

                    ((World)instance).getChunkProviderGenerate().populateChunk(this);

                    // effectively invalidate the chunk cache
                    super.setBiome(0, 1, 0, super.getBiome(0, 1, 0));

                    setPopulated(true);
                    setMarkedForPopulation(0);
                    EventDispatcher.call(new ChunkPopulatedEvent(this.instance, this));
                    return true;
                }).completeOnTimeout(false, 60, TimeUnit.SECONDS)
                .thenAcceptAsync((v)->{
                    if (!isPopulated() && !v) {
                        // it failed... LETS RERUN!
                        setMarkedForPopulation(0);
                        this.doPopulate("retry attempt");
                        System.out.println("Population failed for Chunk. Timeout. Rerunning!");
                    }
                }).handle((res, ex)->{
                    ex.printStackTrace();// it failed... LETS RERUN!
                    setMarkedForPopulation(0);
                    this.doPopulate("retry attempt");
                    System.out.println("Population failed for Chunk. Error. Rerunning!");
                    return null;
                });
    }

    @Override
    public void tick(long time) {
        if (!getViewers().isEmpty() && !isPopulated() && !isMarkedForPopulation() && isLoaded()) {
            doPopulate();
        }
        super.tick(time);
        if (getViewers().isEmpty() && isPopulated() && isLoaded() && !isMarkedForPopulation()) {
            timeSinceLastPlayer++;
            if (timeSinceLastPlayer > 20*60) { // 30 seconds
                // fire an ChunkUnloadAttemptEvent
                EventDispatcher.callCancellable(new ChunkUnloadAttemptEvent(this.instance, this), ()->{
                    getInstance().unloadChunk(this);
                });
            }
        } else {
            timeSinceLastPlayer = 0;
        }
    }


}
