package com.artingl.opencraft.World.Level;

import com.artingl.opencraft.World.Generation.NoiseGenerator3D;
import com.artingl.opencraft.World.Generation.PerlinNoise;
import com.artingl.opencraft.World.Chunk.Region;

public class LevelGeneration {

    protected ClientLevel level;
    protected PerlinNoise noise;
    protected NoiseGenerator3D noise3d;

    public LevelGeneration(ClientLevel.Generation generation) {
        this.level = generation.getLevel();

        this.noise = new PerlinNoise(3, generation.getSeed());
        this.noise3d = new NoiseGenerator3D(generation.getSeed());
    }

    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z) {}
    public void destroy() {
        this.noise.destroy();
    }

    public int getHeightValue(int x, int z) {
        return this.noise.getNoiseValue(38, (float) x, (float) z, .5f, .01f, -5, 38) + ClientLevel.WATER_LEVEL;
    }

}
