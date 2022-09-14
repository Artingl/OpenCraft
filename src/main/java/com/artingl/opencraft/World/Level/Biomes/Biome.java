package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.LevelTypes;

public class Biome {

    public static int id = -1;

    private final LevelTypes levelType;

    public Biome(LevelTypes levelType) {
        this.levelType = levelType;
    }

    public LevelTypes getLevelType() {
        return levelType;
    }

    public int getBiomeHeight() {
        return 10;
    }

    public int getId() {
        return Biome.id;
    }

    public boolean checkHeight(int value) {
        return false;
    }

    public String getName() {
        return "Biome";
    }

    public void createRegionXZ(Region region, int height, int x, int z) {
    }

    public boolean canTreesSpawn() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Integer) {
            return getId() == ((int)obj);
        }

        if (!(obj instanceof Biome)) {
            return false;
        }

        return ((Biome)obj).getId() == getId();
    }

    public int getWaterLevelModifier() {
        return 0;
    }
}
