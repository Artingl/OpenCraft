package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.LevelTypes;

public class BiomeDesert extends Biome {

    public static int id = 2;
    private int heightValue;

    public BiomeDesert() {
        super(LevelTypes.WORLD);
    }

    public int getId() {
        return BiomeDesert.id;
    }

    @Override
    public int getBiomeHeight() {
        return 5;
    }

    public String getName() {
        return "Desert";
    }

    public void prepareXZ(Region region, int height, int x, int z) {
        heightValue = region.randomInteger(-10, -4);
    }

    @Override
    public void createBlock(Region region, int height, int x, int y, int z) {
        if (y == height - 1) {
            region.setBlock(BlockRegistry.Blocks.sand, x, y, z);
        } else {
            if (y > height + heightValue) {
                region.setBlock(BlockRegistry.Blocks.sandStone, x, y, z);
            } else {
                region.setBlock(BlockRegistry.Blocks.stone, x, y, z);
            }
        }
    }

    @Override
    public boolean checkHeight(int value) {
        return value < 15;
    }
}
