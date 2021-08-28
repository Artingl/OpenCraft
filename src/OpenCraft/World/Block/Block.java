package OpenCraft.World.Block;

import OpenCraft.Game.Rendering.Texture;
import OpenCraft.Game.Particle;
import OpenCraft.Game.phys.AABB;
import OpenCraft.OpenCraft;
import OpenCraft.World.Level;

public class Block
{

    public static Block air = new BlockAir();
    public static Block stone = new BlockStone();
    public static Block dirt = new BlockDirt();
    public static Block grass_block = new BlockGrassBlock();
    public static Block bedrock = new BlockBedrock();
    public static Block water = new BlockWater();
    public static Block sand = new BlockSand();
    public static Block gravel = new BlockGravel();
    public static Block log_oak = new BlockLogOak();
    public static Block leaves_oak = new BlockLeavesOak();
    public static Block glass = new BlockGlass();
    public static Block block_void = new BlockVoid();

    private String id;
    protected Texture texture;

    public Block(String id) {
        this.id = id;
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

    public AABB getAABB(int x, int y, int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }

    public boolean isSolid()
    {
        return !getId().equals("air");
    }

    public boolean isLiquid()
    {
        return false;
    }

    public boolean blocksLight()
    {
        return true;
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

            return !OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z).isSolid() && layerOk;
        }
    }

    public boolean hasTranslucent() {
        return false;
    }

    public void neighborChanged(Level level, int x, int y, int z) {
    }

}
