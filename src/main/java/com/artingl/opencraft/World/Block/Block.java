package com.artingl.opencraft.World.Block;

import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Phys.AABB;
import com.artingl.opencraft.Control.Game.Texture;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.World.Ambient.Block.Particle;
import com.artingl.opencraft.World.Direction;
import com.artingl.opencraft.World.Item.Tool;
import com.artingl.opencraft.World.Level.ClientLevel;

public class Block
{
    private String blockId;

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
        return this.blockId.hashCode();
    }

    public static AABB getAABB(Vector3i position) {
        return getAABB(position.x, position.y, position.z);
    }

    public static AABB getAABB(int x, int y, int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }
    
    public boolean isCollidable() {
        return !isLiquid() && isVisible() && !isTile();
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
//        new Drop(new ItemBlock(this, 1), new Vector3i(x, y, z));
    }

    public int getStackAmount() {
        return 64;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Block))
            return false;

        return getId().equals(((Block)obj).getId());
    }

    @Override
    public String toString() {
        return "Block{id=" + getId() + "}";
    }

    public void neighborChanged(ClientLevel level, Vector3i blockPos, Direction.Values direction, Block newBlock) {
    }

    public short packToShort() {
        return (short) getIntegerId();
    }

    public static Block unpackFromShort(short i) {
        if (!BlockRegistry.blocksHashes.containsKey(i))
            return BlockRegistry.Blocks.air;

        return BlockRegistry.blocksHashes.get(i);
    }

}
