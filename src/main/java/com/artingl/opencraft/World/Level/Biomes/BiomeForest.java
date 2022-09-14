package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.Level.LevelTypes;

public class BiomeForest extends Biome {

    public static int id = 0;

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
    public void createRegionXZ(Region region, int height, int x, int z) {
        int value = region.randomInteger(-10, -4);
        float grass_noise_value = region.getNoiseValue(x + value, z - value, -50, 50, 1, .02f);
        for (int y = 1; y < height; y++) {
            if (y == height-1) {
                region.setBlock((ClientLevel.WATER_LEVEL < y-2 ? BlockRegistry.Blocks.grass_block : BlockRegistry.Blocks.sand), x, y, z);

                if (ClientLevel.WATER_LEVEL < y-2 && grass_noise_value > -3 && grass_noise_value < 3) {
                    region.setBlock(BlockRegistry.Blocks.grass, x, y+1, z);
                }
            }
            else {
                if (y > height + value) {
                    region.setBlock(BlockRegistry.Blocks.dirt, x, y, z);
                }
                else {
                    region.setBlock(BlockRegistry.Blocks.stone, x, y, z);
                }
            }
        }
    }

    @Override
    public int getBiomeHeight() {
        return 9;
    }

    @Override
    public boolean canTreesSpawn() {
        return true;
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 10 && value < 31;
    }
}
