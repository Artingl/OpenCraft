package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.LevelTypes;

public class BiomeMountains extends Biome {

    public static int id = 1;
    private int heightValue;
    private float gravelNoiseValue;


    public BiomeMountains() {
        super(LevelTypes.WORLD);
    }

    public int getId() {
        return BiomeMountains.id;
    }

    @Override
    public int getBiomeHeight() {
        return 110;
    }

    public String getName() {
        return "Mountains";
    }

    @Override
    public void prepareXZ(Region region, int height, int x, int z) {
        heightValue = region.randomInteger(-5, 5);
        gravelNoiseValue = region.getNoiseValue(x + heightValue, z - heightValue, -150, 150, 1, .002f);
    }

    @Override
    public void createBlock(Region region, int height, int x, int y, int z) {
        if (y > 100 + heightValue) {
            region.setBlock(BlockRegistry.Blocks.dirt, x, y, z);

            if (y == height - 1) {
                region.setBlock(BlockRegistry.Blocks.snow, x, y, z);
            }
        } else {
            if (gravelNoiseValue > -1 && gravelNoiseValue < 1 && y > height - heightValue - 5) {
                region.setBlock(BlockRegistry.Blocks.gravel, x, y, z);
            } else region.setBlock(BlockRegistry.Blocks.stone, x, y, z);
        }
    }

    @Override
    public boolean doTreesSpawn() {
        return false;
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 31 && value <= 45;
    }

}
