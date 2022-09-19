package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.Level.LevelTypes;

public class BiomeForest extends Biome {

    public static int id = 0;
    private int heightValue;
    private float grassNoiseValue;

    public BiomeForest() {
        super(LevelTypes.WORLD);
    }

    public int getId() {
        return BiomeForest.id;
    }

    public String getName() {
        return "Forest";
    }

    @Override
    public void prepareXZ(Region region, int height, int x, int z) {
        heightValue = region.randomInteger(-10, -4);
        grassNoiseValue = region.getNoiseValue(x + heightValue, z - heightValue, -50, 50, 1, .02f);
    }

    @Override
    public void createBlock(Region region, int height, int x, int y, int z) {
        if (y == height - 1) {
            region.setBlock(BlockRegistry.Blocks.grass_block, x, y, z);

            if (ClientLevel.WATER_LEVEL < y - 2 && grassNoiseValue > -3 && grassNoiseValue < 3) {
                region.setBlock(BlockRegistry.Blocks.grass, x, y + 1, z);
            }
        } else {
            if (y > height + heightValue) {
                region.setBlock(BlockRegistry.Blocks.dirt, x, y, z);
            } else {
                region.setBlock(BlockRegistry.Blocks.stone, x, y, z);
            }
        }
    }

    @Override
    public int getBiomeHeight() {
        return 9;
    }

    @Override
    public boolean doTreesSpawn() {
        return true;
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 10 && value < 31;
    }
}
