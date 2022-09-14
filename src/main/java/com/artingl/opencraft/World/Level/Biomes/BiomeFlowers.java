package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;

public class BiomeFlowers extends BiomeGrassLand {

    public static int id = 4;

    public BiomeFlowers() {
        super();
    }

    public int getId() {
        return BiomeFlowers.id;
    }

    @Override
    public int getBiomeHeight() {
        return 5;
    }

    public String getName() {
        return "Flowers";
    }

    @Override
    public boolean canTreesSpawn() {
        return true;
    }

    @Override
    public void createRegionXZ(Region region, int height, int x, int z) {
        super.createRegionXZ(region, height, x, z);
        int value = region.randomInteger(-10, -4);
        float flower_noise_value = region.getNoiseValue(x + value, z - value, -50, 50, 1, .02f);
        if (region.getBlock(x, height, z).equals(BlockRegistry.Blocks.air) && flower_noise_value > -3 && flower_noise_value < 3) {
            region.setBlock(BlockRegistry.Blocks.rose, x, height, z);
        }
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 70 && value <= 90;
    }
}
