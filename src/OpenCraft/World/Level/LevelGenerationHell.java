package OpenCraft.World.Level;

import OpenCraft.World.Block.Block;
import OpenCraft.World.Chunk.Region;

public class LevelGenerationHell extends LevelGeneration
{

    public LevelGenerationHell(Level level)
    {
        super(level);
    }

    @Override
    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z)
    {
        int y = getHeightValue(world_x, world_z);
        int y2 = getHeightValue(world_x+12, world_z-12);

        region.setBlock(Block.bedrock, region_x, 0, region_z);
        region.setBlock(Block.bedrock, region_x, 255, region_z);

        for (int i = 1; i < y; i++)
        {
            region.setBlock(Block.hellrock, region_x, i, region_z);
        }

        for (int i = 1; i < y2; i++)
        {
            region.setBlock(Block.hellrock, region_x, 255 - i - Level.WATER_LEVEL - 20, region_z);
        }

        if (y < Level.WATER_LEVEL - 3)
        {
            for (int i = y; i < Level.WATER_LEVEL - 3; i++)
            {
                region.setBlock(Block.lava, region_x, i, region_z);
            }
        }
    }

    public int getHeightValue(int x, int z) {
        return this.noise.getNoiseValue(12, (float) x, (float) z, .5f, .01f, -24, 17) + Level.WATER_LEVEL;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
