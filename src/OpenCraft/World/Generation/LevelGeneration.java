package OpenCraft.World.Generation;

import OpenCraft.World.Block.Block;
import OpenCraft.World.Chunk.Region;
import OpenCraft.World.Generation.noise.PerlinNoise;
import OpenCraft.World.Level.Level;

public class LevelGeneration
{

    private Level level;
    private PerlinNoise noise;

    public LevelGeneration(Level level)
    {
        this.level = level;
        this.noise = new PerlinNoise(3, level.getSeed());
    }

    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z)
    {
        int y = getNoiseValue(38, (float) world_x, (float) world_z, .5f, .01f, -24, 38) + Level.WATER_LEVEL;

        boolean wasSand = false;

        region.setBlock(Block.bedrock, region_x, 0, region_z);

        if (y < Level.WATER_LEVEL - 1)
        {
            region.setBlock(Block.sand, region_x, y, region_z);
            wasSand = true;
        }
        else
        {
            region.setBlock(Block.grass_block, region_x, y, region_z);
        }

        if (!wasSand && level.getRandomNumber(20, 1500) == level.getRandomNumber(20, 1500))
        {
            int treeHeight = level.getRandomNumber(5, 7);
            generateTree(region, treeHeight, region_x, y + 1, region_z);
        }

        for (int i = 1; i < y; i++)
        {
            if (!wasSand)
            {
                if (i > y - level.getRandomNumber(5, 10))
                {
                    region.setBlock(Block.dirt, region_x, i, region_z);
                }
                else
                {
                    region.setBlock(Block.stone, region_x, i, region_z);
                }
            }
            else
            {
                if (i > y - level.getRandomNumber(5, 10))
                {
                    region.setBlock(Block.sandStone, region_x, i, region_z);
                }
                else
                {
                    region.setBlock(Block.stone, region_x, i, region_z);
                }
            }

        }
        if (y < Level.WATER_LEVEL - 3)
        {
            for (int i = y; i < Level.WATER_LEVEL - 3; i++)
            {
                region.setBlock(Block.water, region_x, i + 1, region_z);
            }
        }
    }

    public void generateTree(Region region, int treeHeight, int x, int y, int z)
    {
        for (int i = x + -2; i < x + 3; i++)
        {
            for (int j = z + -2; j < z + 3; j++)
            {
                for (int k = y + treeHeight - 2; k < y + treeHeight; k++)
                {
                    region.setBlock(Block.leaves_oak, i, k, j);
                }
            }
        }
        for (int i = treeHeight; i < treeHeight + 1; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    region.setBlock(Block.leaves_oak, x + j, y + i, z + k);
                }
            }
        }
        int cl = 2;
        for (int i = treeHeight + 1; i < treeHeight + 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    if (cl % 2 != 0)
                    {
                        region.setBlock(Block.leaves_oak, x + j, y + i, z + k);
                    }
                    cl++;
                }
            }
        }
        for (int i = y; i < y + treeHeight; i++)
        {
            region.setBlock(Block.log_oak, x, i, z);
        }

        region.setBlock(Block.leaves_oak, x, y + treeHeight + 1, z);
    }

    public int getNoiseValue(int num_iterations, float x, float y, float persistence, float scale, float low, float high)
    {
        float maxAmp = 0;
        float amp = 1;
        float freq = scale;
        float noise = 0;

        //add successively smaller, higher-frequency terms
        for(int i = 0; i < num_iterations; ++i)
        {
            noise += this.noise.getValue(x * freq, y * freq) * amp;
            maxAmp += amp;
            amp *= persistence;
            freq *= 2;
        }

        //take the average value of the iterations
        noise /= maxAmp;

        //normalize the result
        noise = noise * (high - low) / 2 + (high + low) / 2;

        return (int) noise;
    }

    public void destroy() {
        this.noise.destroy();
    }
}
