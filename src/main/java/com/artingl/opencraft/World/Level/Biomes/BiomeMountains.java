package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.LevelTypes;

public class BiomeMountains extends Biome {

    public static int id = 1;


    public BiomeMountains() {
        super(LevelTypes.WORLD);
    }

    public int getId() {
        return BiomeMountains.id;
    }

    @Override
    public int getBiomeHeight() {
        return 80;
    }

    public String getName() {
        return "Mountains";
    }

    @Override
    public void createRegionXZ(Region region, int height, int x, int z) {
        int value = region.randomInteger(-5, 5);
        float dirt_noise_value = region.getNoiseValue(x + value, z - value, -150, 150, 1, .002f);
        for (int y = 1; y < height; y++) {
            if (y > 100 + value) {
                region.setBlock(BlockRegistry.Blocks.dirt, x, y, z);

                if (y == height - 1) {
                    region.setBlock(BlockRegistry.Blocks.snow, x, y, z);
                }
            }
            else {
                if (dirt_noise_value > -1 && dirt_noise_value < 1 && y > height - value - 5) {
                    region.setBlock(BlockRegistry.Blocks.gravel, x, y, z);
                }
                else region.setBlock(BlockRegistry.Blocks.stone, x, y, z);
            }
        }
    }

    @Override
    public boolean canTreesSpawn() {
        return false;
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 31 && value <= 38;
    }

}
