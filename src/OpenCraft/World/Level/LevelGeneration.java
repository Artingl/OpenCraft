package OpenCraft.World.Level;

import OpenCraft.World.Biomes.Biomes;
import OpenCraft.World.Chunk.Region;
import OpenCraft.World.Generation.PerlinNoise;

public class LevelGeneration {

    protected Biomes biomes;
    protected Level level;
    protected PerlinNoise noise;

    public LevelGeneration(Level level) {
        this.level = level;

        this.biomes = new Biomes(this, level.getLevelType(), level.getSeed());
        this.noise = new PerlinNoise(3, level.getSeed());
    }

    public void generateRegion(Region region, int region_x, int region_z, int world_x, int world_z) {}
    public void destroy() {
        this.noise.destroy();
    }

    public void setPlayerSpawnPoint()
    {
        int prob = 0;

        while (true)
        {
            int x = level.getRandomNumber(10, 2000);
            int z = level.getRandomNumber(10, 2000);
            int y = getHeightValue(x, z);

            if (y > Level.WATER_LEVEL - 1)
            {
                if (prob++ > 10) {
                    level.getPlayerEntity().setSpawnPoint(x, y + 2, z);
                    level.getPlayerEntity().teleportToSpawnPoint();
                    break;
                }
            }
        }
    }

    public int getHeightValue(int x, int z) {
        return this.noise.getNoiseValue(38, (float) x, (float) z, .5f, .01f, -5, 38) + Level.WATER_LEVEL;
    }

    public Biomes getBiome() {
        return biomes;
    }
}
