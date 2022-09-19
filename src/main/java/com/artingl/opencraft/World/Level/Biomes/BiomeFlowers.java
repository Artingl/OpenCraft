package com.artingl.opencraft.World.Level.Biomes;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;

public class BiomeFlowers extends BiomeGrassLand {

    public static int id = 4;
    private int heightValue;
    private float flowerNoiseValue;

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
    public boolean doTreesSpawn() {
        return true;
    }

    @Override
    public void prepareXZ(Region region, int height, int x, int z) {
        super.prepareXZ(region, height, x, z);
        heightValue = region.randomInteger(-10, -4);
        flowerNoiseValue = region.getNoiseValue(x + heightValue, z - heightValue, -50, 50, 1, .02f);
    }

    @Override
    public void createBlock(Region region, int height, int x, int y, int z) {
        super.createBlock(region, height, x, y, z);
        if (region.getBlock(x, height, z).equals(BlockRegistry.Blocks.air) && flowerNoiseValue > -3 && flowerNoiseValue < 3) {
            region.setBlock(BlockRegistry.Blocks.rose, x, height, z);
        }
    }

    @Override
    public boolean checkHeight(int value) {
        return value >= 70 && value <= 90;
    }
}
