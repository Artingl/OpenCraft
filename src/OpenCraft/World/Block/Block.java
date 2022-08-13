package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.World.Ambient.Block.Drop;
import OpenCraft.World.Direction;
import OpenCraft.World.Item.ItemBlock;
import OpenCraft.World.Item.Tool;
import OpenCraft.World.Ambient.Block.Particle;
import OpenCraft.math.Vector3i;
import OpenCraft.phys.AABB;
import OpenCraft.OpenCraft;
import OpenCraft.World.Level.Level;

public class Block
{
    public static int BLOCK_COUNT = 0;

    public static Block air = new BlockAir(BLOCK_COUNT++);
    public static Block stone = new BlockStone(BLOCK_COUNT++);
    public static Block dirt = new BlockDirt(BLOCK_COUNT++);
    public static Block grass_block = new BlockGrassBlock(BLOCK_COUNT++);
    public static Block bedrock = new BlockBedrock(BLOCK_COUNT++);
    public static Block water = new BlockWater(BLOCK_COUNT++);
    public static Block lava = new BlockLava(BLOCK_COUNT++);
    public static Block sand = new BlockSand(BLOCK_COUNT++);
    public static Block gravel = new BlockGravel(BLOCK_COUNT++);
    public static Block log_oak = new BlockLogOak(BLOCK_COUNT++);
    public static Block leaves_oak = new BlockLeavesOak(BLOCK_COUNT++);
    public static Block glass = new BlockGlass(BLOCK_COUNT++);
    public static Block sandStone = new BlockSandStone(BLOCK_COUNT++);
    public static Block hellrock = new BlockHellrock(BLOCK_COUNT++);
    public static Block grass = new BlockGrass(BLOCK_COUNT++);
    public static Block rose = new BlockRose(BLOCK_COUNT++);

    public static Block[] blocks = {
            air, stone, dirt, grass_block, bedrock, water,
            sand, gravel, log_oak, leaves_oak, glass, sandStone, hellrock, lava, grass, rose
    };

    private int randomization;
    private final int idi;
    private final String id;

    protected Texture texture;

    private float strength;

    private Tool tool;

    public Block(String id, int idi) {
        this.id = id;
        this.idi = idi;
        this.tool = Tool.HAND;
        this.strength = 1;
        this.makeRandomization();
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

    public int getRandomization() { return this.randomization; }

    public static AABB getAABB(Vector3i position) {
        return getAABB(position.x, position.y, position.z);
    }

    public static AABB getAABB(int x, int y, int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }

    public boolean isVisible()
    {
        return getIdInt() != Block.air.getIdInt();
    }

    public boolean isLiquid()
    {
        return false;
    }

    public boolean isTile()
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

    public void destroy(Vector3i position) {
        this.destroy(position.x, position.y, position.z);
    }

    public void destroy(int x, int y, int z) {
        int SD = 4;

        for(int xx = 0; xx < SD; ++xx) {
            for(int yy = 0; yy < SD; ++yy) {
                for(int zz = 0; zz < SD; ++zz) {
                    float xp = (float)x + ((float)xx + 0.5F) / (float)SD;
                    float yp = (float)y + ((float)yy + 0.5F) / (float)SD;
                    float zp = (float)z + ((float)zz + 0.5F) / (float)SD;
                    OpenCraft.getParticleEngine().add(
                            new Particle(
                                    OpenCraft.getLevel(), xp, yp, zp, xp - (float)x - 0.5F, yp - (float)y - 0.5F, zp - (float)z - 0.5F, texture)
                    );
                }
            }
        }

    }

    public boolean hasTranslucent() {
        return false;
    }

    protected void makeRandomization() {
        // todo: fix it. right now it may change world generation
        //       because it uses level seed
        // this.randomization = OpenCraft.getLevel().getRandomNumber(-100, 100);
    }

    public float getStrength() {
        return strength;
    }

    public Tool getTool() {
        return tool;
    }

    public void setStrength(float strength) {
        if (strength == 0) {
            strength = 1;
        }

        this.strength = strength;
    }

    public void setDefaultTool(Tool tool) {
        this.tool = tool;
    }

    public void createDrop(Vector3i position) {
        createDrop(position.x, position.y, position.z);
    }

    public void createDrop(int x, int y, int z) {
        new Drop(new ItemBlock(this, 1), new Vector3i(x, y, z));
    }

    public int getStackAmount() {
        return 64;
    }

    @Override
    public boolean equals(Object obj) {
        return getIdInt() == ((Block)obj).getIdInt();
    }

    @Override
    public String toString() {
        return "Block{id=" + getId() + "}";
    }

    public void neighborChanged(Level level, Vector3i blockPos, Direction.Values direction, Block newBlock) {
    }
}
