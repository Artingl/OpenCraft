package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Rendering.Texture;
import com.artingl.opencraft.Rendering.TextureEngine;
import com.artingl.opencraft.World.Ambient.Block.Drop;
import com.artingl.opencraft.World.Direction;
import com.artingl.opencraft.World.Item.ItemBlock;
import com.artingl.opencraft.World.Item.Tool;
import com.artingl.opencraft.World.Ambient.Block.Particle;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Phys.AABB;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Level.ClientLevel;

public class Block
{
    private int randomization;
    private String blockId;
    private int integerId;

    protected Texture texture;

    private float strength;

    private Tool tool;

    public Block(){}

    public Block(String blockId) {
        this(blockId, true);
    }

    public Block(String blockId, boolean createTexture) {
        this.blockId = blockId;
        this.tool = Tool.HAND;
        this.strength = 1;
        this.makeRandomization();

        if (createTexture) {
            float tx, ty, bx, by, sx, sy;

            tx = TextureEngine.getBlockTextureX(this.getId());
            ty = TextureEngine.getBlockTextureY(this.getId());
            bx = TextureEngine.getBlockTextureX(this.getId());
            by = TextureEngine.getBlockTextureY(this.getId());
            sx = TextureEngine.getBlockTextureX(this.getId());
            sy = TextureEngine.getBlockTextureY(this.getId());
            int id = TextureEngine.getBlockTextureId(this.getId());
            this.texture = new Texture(id, tx, ty, bx, by, sx, sy);
        }

        this.integerId = -1;
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
        return this.blockId;
    }

    public int getIntegerId() {
        if (this.integerId == -1) {
            this.integerId = 0;

            try {
                for (Class<?> blockClass : BlockRegistry.blocks) {
                    Block block = (Block) blockClass.getConstructor().newInstance();

                    if (block.getId().equals(blockId))
                        break;

                    ++this.integerId;
                }
            } catch (Exception e) {
                Logger.exception("Error while getting block id", e);
            }
        }

        return this.integerId;
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
        return !this.equals(BlockRegistry.Blocks.air);
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
                    Opencraft.getParticleEngine().add(
                            new Particle(
                                    Opencraft.getLevel(), xp, yp, zp, xp - (float)x - 0.5F, yp - (float)y - 0.5F, zp - (float)z - 0.5F, texture)
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
        new Drop(new ItemBlock(this, 1), new Vector3i(x, y, z));
    }

    public int getStackAmount() {
        return 64;
    }

    @Override
    public boolean equals(Object obj) {
        return getId().equals(((Block)obj).getId());
    }

    @Override
    public String toString() {
        return "Block{id=" + getId() + "}";
    }

    public void neighborChanged(ClientLevel level, Vector3i blockPos, Direction.Values direction, Block newBlock) {
    }

}
