package OpenCraft.World.Level;

import OpenCraft.World.Biomes.Biomes;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Chunk.Region;

import java.util.ArrayList;

public class LevelGenerationWorld extends LevelGeneration
{

    private float heightLevel = 12;
    private ArrayList<Float> heightLevels;

    public LevelGenerationWorld(Level level)
    {
        super(level);
        this.heightLevels = new ArrayList<>();
    }

    @Override
    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z)
    {
        Biomes.World biome = this.biomes.getWorldBiome(world_x, world_z);

        int y = getHeightValue(world_x, world_z, biome);

        region.setBlock(Block.bedrock, region_x, 0, region_z);

        Block mainBlock = Block.grass_block;

        if (biome == Biomes.World.Desert) {
            mainBlock = Block.sand;
        }

        if (biome == Biomes.World.Ocean) {
            mainBlock = Block.water;
        }

        region.setBlock(mainBlock, region_x, y, region_z);

        if (!(y < Level.WATER_LEVEL - 3)) {
            if (biome == Biomes.World.Forest && level.getRandomNumber(20, 1500) == level.getRandomNumber(20, 1500)) {
                int treeHeight = level.getRandomNumber(5, 7);
                generateTree(region, treeHeight, region_x, y + 1, region_z);
            }
        }

        for (int i = 1; i < y; i++)
        {
            if (biome == Biomes.World.Desert)
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
            else {
                if (i > y - level.getRandomNumber(5, 10))
                {
                    region.setBlock(Block.dirt, region_x, i, region_z);
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

    public int getHeightValue(int x, int z, Biomes.World biome) {
//        if (biome == Biomes.World.Mountains) {
//            this.setBiomeHeightValue(40);
//        }
//        else if (biome == Biomes.World.Forest) {
//            this.setBiomeHeightValue(20);
//        }
//        else if (biome == Biomes.World.Desert) {
//            this.setBiomeHeightValue(18);
//        }
//        else {
//            this.setBiomeHeightValue(12);
//        }

        return this.noise.getNoiseValue(38, (float) x, (float) z, .5f, .01f, -10, 10) + Level.WATER_LEVEL;
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

    @Override
    public void destroy() {
        super.destroy();
        this.heightLevels.clear();
    }

    private void setBiomeHeightValue(float val) {
        if (heightLevel < val) {
            heightLevel += 0.01f;
        }
        if (heightLevel > val) {
            heightLevel -= 0.01f;
        }

        this.heightLevels.add(heightLevel);
        if (this.heightLevels.size() > 125) {
            this.heightLevels.subList(0, this.heightLevels.size() - 125).clear();
        }
    }

    private float getBiomeHeightLevel() {
        float val = 0;

        for (Float aFloat : this.heightLevels) {
            val += aFloat;
        }

        return val / this.heightLevels.size();
    }

}
