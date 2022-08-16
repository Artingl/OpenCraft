package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Rendering.Texture;
import com.artingl.opencraft.Rendering.TextureEngine;
import com.artingl.opencraft.World.Ambient.Block.Drop;
import com.artingl.opencraft.World.Direction;
import com.artingl.opencraft.World.Item.ItemBlock;
import com.artingl.opencraft.World.Item.Tool;
import com.artingl.opencraft.World.Ambient.Block.Particle;
import com.artingl.opencraft.math.Vector3i;
import com.artingl.opencraft.phys.AABB;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.World.Level.Level;

public class Block
{
    public static int BLOCK_COUNT = 0;

    public static Block air = new BlockAir(getNextBlockId());
    public static Block stone = new BlockStone(getNextBlockId());
    public static Block dirt = new BlockDirt(getNextBlockId());
    public static Block grass_block = new BlockGrassBlock(getNextBlockId());
    public static Block bedrock = new BlockBedrock(getNextBlockId());
    public static Block water = new BlockWater(getNextBlockId());
    public static Block lava = new BlockLava(getNextBlockId());
    public static Block sand = new BlockSand(getNextBlockId());
    public static Block gravel = new BlockGravel(getNextBlockId());
    public static Block log_oak = new BlockLogOak(getNextBlockId());
    public static Block leaves_oak = new BlockLeavesOak(getNextBlockId());
    public static Block glass = new BlockGlass(getNextBlockId());
    public static Block sandStone = new BlockSandStone(getNextBlockId());
    public static Block hellrock = new BlockHellrock(getNextBlockId());
    public static Block grass = new BlockGrass(getNextBlockId());
    public static Block rose = new BlockRose(getNextBlockId());

    public static Block[] blocks = {
            air, stone, dirt, grass_block, bedrock, water,
            sand, gravel, log_oak, leaves_oak, glass, sandStone, hellrock, lava, grass, rose
    };

    private int randomization;
    private final int intId;
    private final String stringId;

    protected Texture texture;

    private float strength;

    private Tool tool;


    public Block(String stringId, int intId) {
        this(stringId, intId, true);
    }

    public Block(String stringId, int intId, boolean createTexture) {
        this.stringId = stringId;
        this.intId = intId;
        this.tool = Tool.HAND;
        this.strength = 1;
        this.makeRandomization();

        if (createTexture) {
            float tx, ty, bx, by, sx, sy;

            tx = TextureEngine.getBlockTextureX(this.getStringId());
            ty = TextureEngine.getBlockTextureY(this.getStringId());
            bx = TextureEngine.getBlockTextureX(this.getStringId());
            by = TextureEngine.getBlockTextureY(this.getStringId());
            sx = TextureEngine.getBlockTextureX(this.getStringId());
            sy = TextureEngine.getBlockTextureY(this.getStringId());
            int id = TextureEngine.getBlockTextureId(this.getStringId());
            this.texture = new Texture(id, tx, ty, bx, by, sx, sy);
        }
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    public Texture getTexture()
    {
        return this.texture;
    }

    public String getStringId()
    {
        return this.stringId;
    }

    public int getIntId()
    {
        return this.intId;
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
        return getIntId() != Block.air.getIntId();
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
            if (block.intId == id) return block;
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
        // todo: when next block is dropped, the last one would disappear
        OpenCraft.getLevel().addEntity(
                new Drop(new ItemBlock(this, 1), new Vector3i(x, y, z)));
    }

    public int getStackAmount() {
        return 64;
    }

    @Override
    public boolean equals(Object obj) {
        return getIntId() == ((Block)obj).getIntId();
    }

    @Override
    public String toString() {
        return "Block{id=" + getStringId() + "}";
    }

    public void neighborChanged(Level level, Vector3i blockPos, Direction.Values direction, Block newBlock) {
    }
    
    public static int getNextBlockId() {
        return Block.BLOCK_COUNT++;
    }
}
