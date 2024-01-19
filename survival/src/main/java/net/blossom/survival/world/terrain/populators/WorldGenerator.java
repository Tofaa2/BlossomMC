package net.blossom.survival.world.terrain.populators;

import net.blossom.survival.world.terrain.CustomChunk;

import java.util.Random;

public abstract class WorldGenerator {


    public boolean populate(CustomChunk chunk, Random random, int centerX, int centerY, int centerZ) {
        return false;
    }


    public void scale(double scaleX, double scaleY, double scaleZ) {}
}
