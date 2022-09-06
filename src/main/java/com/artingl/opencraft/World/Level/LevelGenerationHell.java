package com.artingl.opencraft.World.Level;

import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Chunk.Region;

public class LevelGenerationHell extends LevelGeneration
{

    public LevelGenerationHell(ClientLevel.Generation generation)
    {
        super(generation);
    }

    @Override
    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z)
    {
        int y = getHeightValue(world_x, world_z);
        int y2 = getHeightValue(world_x+12, world_z-12);

        region.setBlock(BlockRegistry.Blocks.bedrock, region_x, 0, region_z);
        region.setBlock(BlockRegistry.Blocks.bedrock, region_x, ClientLevel.MAX_HEIGHT-1, region_z);

        for (int i = 1; i < y; i++)
        {
            region.setBlock(BlockRegistry.Blocks.hellrock, region_x, i, region_z);
        }

        for (int i = 1; i < y2; i++)
        {
            region.setBlock(BlockRegistry.Blocks.hellrock, region_x, ClientLevel.MAX_HEIGHT - 1 - i - ClientLevel.WATER_LEVEL - 20, region_z);
        }

        if (y < ClientLevel.WATER_LEVEL - 3)
        {
            for (int i = y; i < ClientLevel.WATER_LEVEL - 3; i++)
            {
                region.setBlock(BlockRegistry.Blocks.lava, region_x, i, region_z);
            }
        }
    }

    public int getHeightValue(int x, int z) {
        return this.noise.getNoiseValue(12, (float) x, (float) z, .5f, .01f, -24, 17) + ClientLevel.WATER_LEVEL;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
