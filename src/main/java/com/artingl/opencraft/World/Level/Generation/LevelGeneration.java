package com.artingl.opencraft.World.Level.Generation;

import com.artingl.opencraft.World.Level.Generation.Noise.PerlinNoise;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.Biomes.Biome;
import com.artingl.opencraft.World.Level.Biomes.BiomesRegistry;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.Level.LevelTypes;

public class LevelGeneration {

    protected ClientLevel level;
    protected PerlinNoise noise;
    protected PerlinNoise caveNoise;
    protected PerlinNoise biomeNoise;

    public LevelGeneration() {
    }

    public void initialize(ClientLevel.Generation generation) {
        this.level = generation.getLevel();

        this.noise = new PerlinNoise(3, generation.getSeed());
        this.caveNoise = new PerlinNoise(3, generation.getSeed()*4 << 5);
        this.biomeNoise = new PerlinNoise(1, generation.getSeed()*2 << 4);
    }


    public void generateRegion(Region region) {}

    public void destroy() {
    }

    public Biome getBiomeByNoise(int x, int z) {
        return BiomesRegistry.getBiome(LevelTypes.WORLD, (int) (this.biomeNoise.getNoiseValue(1, (float) (int) x, (float) (int) z, .5f, .005f, 0, 12) * 15));
    }

    public Biome getBiome(int x, int z) {
        Biome biome = null;
        Biome[] biomes = new Biome[]{getBiomeByNoise(x, z), getBiomeByNoise(x + 1, z), getBiomeByNoise(x, z + 1), getBiomeByNoise(x - 1, z), getBiomeByNoise(x, z - 1)};
        int[] values = new int[BiomesRegistry.totalBiomes];
        int value = 0;

        for (Biome item : biomes) {
            values[item.getId()]++;

            if (value < values[item.getId()]) {
                value = values[item.getId()];
                biome = item;
            }
        }

        return biome;
    }

    public ClientLevel getLevel() {
        return level;
    }
}
