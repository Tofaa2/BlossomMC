package net.blossom.world.old;

import net.blossom.world.old.populator.FastNoiseLite;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class OverworldGenerator implements Generator {


    private final FastNoiseLite terrainNoise;
    private final FastNoiseLite detailNoise;
    private final FastNoiseLite biomeNoise;
    private final int averageDirtDepth = 3;
    private final Random random = new Random();
    private final Instance instance;

    public OverworldGenerator(Instance instance, int seed) {
        this.instance = instance;

        this.terrainNoise = new FastNoiseLite(seed);
        this.terrainNoise.SetFrequency(0.001f);
        this.terrainNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.terrainNoise.SetFractalOctaves(5);

        this.detailNoise = new FastNoiseLite(seed);
        this.detailNoise.SetFrequency(0.05f);

        this.biomeNoise = new FastNoiseLite(seed);
        this.biomeNoise.SetFrequency(0.01f);
        this.biomeNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.biomeNoise.SetFractalOctaves(5);
        this.biomeNoise.SetFractalLacunarity(2);
        this.biomeNoise.SetFractalGain(0.5f);
    }

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        var start = unit.absoluteStart();
        var maxX = unit.size().x();
        var maxZ = unit.size().z();
        var maxY = unit.size().y();
        var chunkX = start.chunkX();
        var chunkZ = start.chunkZ();
        for (int x = 0; x < maxX; x++) {
            for (int z = 0; z < maxZ; z++) {
                for (int y = 0; y < maxY; y++) {
                    Point point = start.add(x, y, z);
                    float alg = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 2) + (detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) / 10);
                    float alg2 = detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16));
                    float currentY = (65 + (30 * alg));
                    if (y == -63) {
                        unit.modifier().setBlock(point, Block.BEDROCK);
                        continue;
                    }
                    if (y < currentY) {
                        float distanceToSurface = Math.abs(y - currentY); // The absolute y distance to the world surface.
                        double function = .1 * Math.pow(distanceToSurface, 2) - 1; // A second grade polynomial offset to the noise max and min (1, -1).

                        if (alg2 > Math.min(function, -.3)) {
                            unit.modifier().setBlock(point, Block.STONE);
                        }
                    }
                    else if (y < 62) {
                        unit.modifier().setBlock(point, Block.WATER);
                        continue;
                    }
//
//                    if (isTop(alg, point)) {
//                        unit.modifier().setBlock(point, Block.GRASS_BLOCK);
//                        continue;
//                    }
//                    if (point.y() < 62) {
//                        unit.modifier().setBlock(point, Block.WATER);
//                        continue;
//                    }
//                    if(65 + (30 * alg) > point.y()) {
//                        unit.modifier().setBlock(point, Block.DIRT);
//                    }
                }
            }
        }
    }

    private boolean isTop(float alg, Point point) {
        return ((int) (65 + (30 * alg))) == (int) point.y();
    }

}


