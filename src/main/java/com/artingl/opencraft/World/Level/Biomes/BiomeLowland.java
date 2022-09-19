package com.artingl.opencraft.World.Level.Biomes;

public class BiomeLowland extends BiomeGrassLand {

    public static int id = 4;

    public BiomeLowland() {
        super();
    }

    public int getId() {
        return BiomeLowland.id;
    }

    @Override
    public int getBiomeHeight() {
        return -25;
    }

    public String getName() {
        return "Lowland";
    }

    @Override
    public boolean doTreesSpawn() {
        return false;
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 90 && value <= 110;
    }
}
