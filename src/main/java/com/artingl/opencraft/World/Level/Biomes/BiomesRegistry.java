package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.World.Level.LevelTypes;

import java.util.ArrayList;
import java.util.Arrays;

public class BiomesRegistry {

    public static class Biomes {
        public static BiomeForest forest = new BiomeForest();
        public static BiomeMountains mountains = new BiomeMountains();
        public static BiomeDesert desert = new BiomeDesert();
        public static BiomeGrassLand grassLand = new BiomeGrassLand();
        public static BiomeFlowers flowers = new BiomeFlowers();
    }

    public static int totalBiomes;
    public static ArrayList<Class<?>> biomes = new ArrayList<>();

    public static void init() {
        Logger.info("Initializing biomes");
        Class<?>[] vanilla_biomes = {
                BiomeForest.class,
                BiomeMountains.class,
                BiomeDesert.class,
                BiomeGrassLand.class,
                BiomeFlowers.class,
        };

        biomes.addAll(Arrays.asList(vanilla_biomes));
        totalBiomes = biomes.size();
    }

    public static Biome registerBiome(Biome biome) {
        biomes.add(biome.getClass());
        totalBiomes++;
        return biome;
    }

    public static Biome getBiome(LevelTypes type, int level) {
        try {
            for (Class<?> biomeClass : biomes) {
                Biome biome = (Biome) biomeClass.getConstructor().newInstance();

                if (biome.checkHeight(level) && biome.getLevelType().equals(type))
                    return biome;
            }
        } catch (Exception e) {
            Logger.exception("Error while getting biome", e);
        }

        return Biomes.forest;
    }

}
