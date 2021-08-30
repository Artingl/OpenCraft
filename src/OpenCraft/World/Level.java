package OpenCraft.World;

import OpenCraft.World.Entity.Entity;
import OpenCraft.World.generation.PerlinNoise;
import OpenCraft.phys.AABB;
import OpenCraft.Interfaces.LevelRendererListener;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Level
{

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;
    public static final int DEPTH = 256;
    public static final int WATER_LEVEL = 64;

    private Thread generationThread;
    private LevelArrayList blocks;
    //private static byte[] saveBlocks;
    private int[] lightDepths;
    private PerlinNoise noise;
    private boolean isPlayerStartPosSet = false;
    private ArrayList<LevelRendererListener> levelListeners = new ArrayList();
    private ArrayList<Entity> entities = new ArrayList<>();

    public Level()
    {
        //saveBlocks = new byte[WIDTH * HEIGHT * DEPTH];
        blocks = new LevelArrayList();
        lightDepths = new int[WIDTH * HEIGHT];
    }

    public void addEntity(Entity entity)
    {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities()
    {
        return entities;
    }

    public void generateXZ(int x0, int z0, int x1, int z1)
    {
        for (int x = x0; x <= x1; x++)
        {
            for (int z = z0; z <= z1; z++)
            {
                if (getBlock(x, 0, z) != null) continue;

                int y = getNoiseValue(16, (float) x, (float) z, .5f, .01f, -24, 24) + WATER_LEVEL;

                if (y < 0) y = 0;
                if (y > DEPTH) y = DEPTH;

                for (int i = y + 1; i < DEPTH; i++)
                    if (getBlock(x, i, z) == null)
                        setBlockWithoutRendering(x, i, z, Block.air);

                boolean wasSand = false;

                setBlockWithoutRendering(x, 0, z, Block.bedrock);
                if (y < WATER_LEVEL - 1)
                {
                    setBlockWithoutRendering(x, y, z, Block.sand);
                    wasSand = true;
                }
                else
                {
                    setBlockWithoutRendering(x, y, z, Block.grass_block);
                }

                if (!wasSand && getRandomNumber(20, 100) == getRandomNumber(20, 100))
                {
                    generateTree(x, y + 1, z);
                }
                else if (y > WATER_LEVEL - 3 && !isPlayerStartPosSet && x > WIDTH / 4 && z > HEIGHT / 4) {
                    OpenCraft.getPlayer().setSpawnPoint(x, y + 2, z);
                    OpenCraft.getPlayer().teleportToSpawnPoint();
                    isPlayerStartPosSet = true;
                }

                for (int i = 1; i < y; i++)
                {
                    if (!wasSand)
                    {
                        if (i > y - getRandomNumber(5, 10))
                        {
                            setBlockWithoutRendering(x, i, z, Block.dirt);
                        }
                        else
                        {
                            setBlockWithoutRendering(x, i, z, Block.stone);
                        }
                    }
                    else
                    {
                        if (i > y - getRandomNumber(5, 10))
                        {
                            setBlockWithoutRendering(x, i, z, Block.sandStone);
                        }
                        else
                        {
                            setBlockWithoutRendering(x, i, z, Block.stone);
                        }
                    }

                }
                if (y < WATER_LEVEL - 3)
                {
                    for (int i = y; i < WATER_LEVEL - 3; i++)
                    {
                        setBlockWithoutRendering(x, i + 1, z, Block.water);
                    }
                }
            }
        }
    }

    public void generateWorld(int seed)
    {
        noise = new PerlinNoise(3, seed);
        generateXZ(-256, -256, 256, 256);

        new Thread(() -> {
            while (true)
            {
                generateXZ((int) (OpenCraft.getPlayer().getX() - 256), (int) (OpenCraft.getPlayer().getZ() - 256),
                        (int) (OpenCraft.getPlayer().getX() + 256), (int) (OpenCraft.getPlayer().getZ() + 256));

            }
        }).start();
    }

    public void generateTree(int x, int y, int z)
    {
        int treeHeight = getRandomNumber(5, 7);
        for (int i = x + -2; i < x + 3; i++)
        {
            for (int j = z + -2; j < z + 3; j++)
            {
                for (int k = y + treeHeight - 2; k < y + treeHeight; k++)
                {
                    setBlockWithoutRendering(i, k, j, Block.leaves_oak);
                }
            }
        }
        for (int i = treeHeight; i < treeHeight + 1; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    setBlockWithoutRendering(x + j, y + i, z + k, Block.leaves_oak);
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
                        setBlockWithoutRendering(x + j, y + i, z + k, Block.leaves_oak);
                    }
                    cl++;
                }
            }
        }
        for (int i = y; i < y + treeHeight; i++)
        {
            setBlockWithoutRendering(x, i, z, Block.log_oak);
        }
        setBlockWithoutRendering(x, y + treeHeight + 1, z, Block.leaves_oak);
    }

    public void setBlockWithoutRendering(int x, int y, int z, Block block)
    {
        blocks.set((y * HEIGHT + z) * WIDTH + x, block);
    }

    public void setBlockWithoutRendering(int i, Block block)
    {
        blocks.set(i, block);
    }

    public int getRandomNumber(int min, int max)
    {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
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

    public float getGroundLevel() {
        return WATER_LEVEL;
    }

    public void addListener(LevelRendererListener levelListener) {
        this.levelListeners.add(levelListener);
    }

    public void removeListener(LevelRendererListener levelListener) {
        this.levelListeners.remove(levelListener);
    }

    public Block getBlock(int x, int y, int z)
    {
        //if (y < WATER_LEVEL) return Block.stone;
        //return Block.air;
        if (blocks.contains((y * HEIGHT + z) * WIDTH + x))
            return blocks.get((y * HEIGHT + z) * WIDTH + x);//blocks[(y * HEIGHT + z) * WIDTH + x];
        return null;
    }

    public void removeBlock(int x, int y, int z)
    {
        setBlock(x, y, z, Block.air);
    }

    public void setBlock(int x, int y, int z, Block block)
    {
        {//(x >= 0 && y >= 0 && z >= 0 && x < WIDTH && y < DEPTH && z < HEIGHT) {
            //saveBlocks[(y * HEIGHT + z) * WIDTH + x] = (byte) block.getIdInt();
            //blocks[(y * HEIGHT + z) * WIDTH + x] = block;
            blocks.set((y * HEIGHT + z) * WIDTH + x, block);
            //this.neighborChanged(x - 1, y, z);
            //this.neighborChanged(x + 1, y, z);
            //this.neighborChanged(x, y - 1, z);
            //this.neighborChanged(x, y + 1, z);
            //this.neighborChanged(x, y, z - 1);
            //this.neighborChanged(x, y, z + 1);
            //this.calcLightDepths(x, z, 1, 1);

            for(int i = 0; i < this.levelListeners.size(); ++i) {
                ((LevelRendererListener)this.levelListeners.get(i)).tileChanged(x, y, z);
            }
        }
    }

    private void neighborChanged(int x, int y, int z) {
        //if (x >= 0 && y >= 0 && z >= 0 && x < WIDTH && y < DEPTH && z < HEIGHT) {
        //    Block block = blocks.get((y * HEIGHT + z) * WIDTH + x);//blocks[(y * HEIGHT + z) * WIDTH + x];
        //    block.neighborChanged(this, x, y, z);
        //}
    }

    public ArrayList<AABB> getCubes(AABB box) {
        ArrayList<AABB> boxes = new ArrayList<>();
        int x0 = (int)Math.floor((double)box.x0);
        int x1 = (int)Math.floor((double)(box.x1 + 1.0F));
        int y0 = (int)Math.floor((double)box.y0);
        int y1 = (int)Math.floor((double)(box.y1 + 1.0F));
        int z0 = (int)Math.floor((double)box.z0);
        int z1 = (int)Math.floor((double)(box.z1 + 1.0F));

        for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
                for(int z = z0; z < z1; ++z) {
                    if (getBlock(x, y, z) == null) continue;
                    if (getBlock(x, y, z).isSolid() && !getBlock(x, y, z).isLiquid()) {
                        boxes.add(new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1)));
                    }
                }
            }
        }

        return boxes;
    }

    public boolean isLightBlocker(int x, int y, int z) {
        Block block = getBlock(x, y, z);
        return block.blocksLight();
    }

    public void calcLightDepths(int x0, int y0, int x1, int y1) {
        for(int x = x0; x < x0 + x1; ++x) {
            for(int z = y0; z < y0 + y1; ++z) {
                int oldDepth = this.lightDepths[x + z * WIDTH];

                int y;
                for(y = DEPTH - 1; y > 0 && !this.isLightBlocker(x, y, z); --y) {
                }

                this.lightDepths[x + z * WIDTH] = y;
                if (oldDepth != y) {
                    int yl0 = Math.min(oldDepth, y);
                    int yl1 = Math.max(oldDepth, y);

                    for(int i = 0; i < this.levelListeners.size(); ++i) {
                        ((LevelRendererListener)this.levelListeners.get(i)).lightColumnChanged(x, z, yl0, yl1);
                    }
                }
            }
        }

    }

    public boolean isLit(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < WIDTH && y < DEPTH && z < HEIGHT) {
            return y >= this.lightDepths[x + z * WIDTH];
        } else {
            return true;
        }
    }

    public boolean containsLiquid(AABB box) {
        int x0 = (int)Math.floor((double)box.x0);
        int x1 = (int)Math.floor((double)(box.x1 + 1.0F));
        int y0 = (int)Math.floor((double)box.y0);
        int y1 = (int)Math.floor((double)(box.y1 + 1.0F));
        int z0 = (int)Math.floor((double)box.z0);
        int z1 = (int)Math.floor((double)(box.z1 + 1.0F));
        if (x0 < 0) {
            x0 = 0;
        }

        if (y0 < 0) {
            y0 = 0;
        }

        if (z0 < 0) {
            z0 = 0;
        }

        if (x1 > WIDTH) {
            x1 = WIDTH;
        }

        if (y1 > DEPTH) {
            y1 = DEPTH;
        }

        if (z1 > HEIGHT) {
            z1 = HEIGHT;
        }

        for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
                for(int z = z0; z < z1; ++z) {
                    if (getBlock(x, y, z) == null) continue;
                    if (getBlock(x, y, z).isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //public byte[] getByteBlocks() {
    //    return saveBlocks;
    //}

    public void destroy()
    {
        //saveBlocks = new byte[WIDTH * HEIGHT * DEPTH];
        blocks = new LevelArrayList();
        lightDepths = new int[WIDTH * HEIGHT];
    }
}
