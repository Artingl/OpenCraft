package OpenCraft.World.Biomes;

import OpenCraft.World.Generation.PerlinNoise;
import OpenCraft.World.Level.Level;
import OpenCraft.World.Level.LevelGeneration;

public class Biomes {

    public enum World {
        Forest, Desert, Ocean, Mountains
    };

    public enum Hell {
        Hell
    };

    private int seed;
    private int initialSeed;
    private LevelGeneration generation;
    private Level.LevelType levelType;
    private PerlinNoise noise;

    private World previousBiome;

    public Biomes(LevelGeneration generation, Level.LevelType levelType, int seed) {
        this.seed = seed;
        this.initialSeed = seed;
        this.generation = generation;
        this.levelType = levelType;
        this.noise = new PerlinNoise(3, seed*10);
    }

    public int getRandomNumber(int min, int max)
    {
        seed = seed * 1664525 + 1013904223;
        return (seed >> 24) % (max + 1 - min) + min;
    }

    public World getWorldBiome(int x, int z) {
        double value = this.noise.getNoiseValue(12, (float) x, (float) z, .5f, .01f, 0, 50);
        World biome;

        if (value > 10 && value <= 30) {
            biome = World.Desert;
        }
        else if (value > 30 && value <= 46) {
            if (previousBiome == World.Forest || previousBiome == World.Mountains)
            {
                biome = World.Mountains;
            }
            else biome = World.Forest;
        }
        else {
            biome = World.Forest;
        }

        previousBiome = biome;
        return biome;
    }

    public Hell getHellBiome(int x, int z) {
        return Hell.Hell;
    }

}
