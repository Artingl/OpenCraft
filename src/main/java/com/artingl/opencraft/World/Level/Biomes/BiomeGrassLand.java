package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.LevelTypes;

public class BiomeGrassLand extends BiomeForest {

    public static int id = 3;

    public BiomeGrassLand() {
        super();
    }

    public int getId() {
        return BiomeGrassLand.id;
    }

    @Override
    public int getBiomeHeight() {
        return 5;
    }

    public String getName() {
        return "GrassLand";
    }

    @Override
    public boolean doTreesSpawn() {
        return false;
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 48 && value <= 70;
    }
}
