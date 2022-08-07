package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.World.Particle;
import OpenCraft.phys.AABB;
import OpenCraft.OpenCraft;
import OpenCraft.World.Level.Level;

public class Block
{

    public static Block air = new BlockAir(0);
    public static Block stone = new BlockStone(1);
    public static Block dirt = new BlockDirt(2);
    public static Block grass_block = new BlockGrassBlock(3);
    public static Block bedrock = new BlockBedrock(4);
    public static Block water = new BlockWater(5);
    public static Block sand = new BlockSand(6);
    public static Block gravel = new BlockGravel(7);
    public static Block log_oak = new BlockLogOak(8);
    public static Block leaves_oak = new BlockLeavesOak(9);
    public static Block glass = new BlockGlass(10);
    public static Block sandStone = new BlockSandStone(11);

    public static Block[] blocks = {
      air, stone, dirt, grass_block, bedrock, water, sand, gravel, log_oak, leaves_oak, glass, sandStone
    };

    private final int idi;
    private final String id;
    protected Texture texture;

    public Block(String id, int idi) {
        this.id = id;
        this.idi = idi;
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    public Texture getTexture()
    {
        return this.texture;
    }

    public String getId()
    {
        return this.id;
    }

    public int getIdInt()
    {
        return this.idi;
    }

    public static AABB getAABB(int x, int y, int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }

    public boolean isVisible()
    {
        return getIdInt() != 0;
    }

    public boolean isLiquid()
    {
        return false;
    }

    public boolean blocksLight()
    {
        return true;
    }

    public static Block getBlockByIntId(int id)
    {
        for (Block block: blocks)
        {
            if (block.idi == id) return block;
        }

        return Block.air;
    }

    public void destroy(int x, int y, int z) {
        int SD = 4;

        for(int xx = 0; xx < SD; ++xx) {
            for(int yy = 0; yy < SD; ++yy) {
                for(int zz = 0; zz < SD; ++zz) {
                    float xp = (float)x + ((float)xx + 0.5F) / (float)SD;
                    float yp = (float)y + ((float)yy + 0.5F) / (float)SD;
                    float zp = (float)z + ((float)zz + 0.5F) / (float)SD;
                    OpenCraft.getParticleEngine().add(new Particle(OpenCraft.getLevel(), xp, yp, zp, xp - (float)x - 0.5F, yp - (float)y - 0.5F, zp - (float)z - 0.5F, texture));
                }
            }
        }

    }

    public boolean haveToRenderSide(int layer, int side, float x, float y, float z)
    {
        boolean layerOk = true;
        if (layer == 2) {
            return hasTranslucent();
        } else {
            if (layer >= 0) {
                layerOk = OpenCraft.getLevel().isLit((int)x, (int)y, (int)z) ^ layer == 1;
            }

            return !OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z).isVisible() && layerOk;
        }
    }

    public boolean hasTranslucent() {
        return false;
    }

    public void neighborChanged(Level level, int x, int y, int z) {
    }

}
