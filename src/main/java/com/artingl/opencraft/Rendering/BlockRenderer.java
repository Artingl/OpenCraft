package com.artingl.opencraft.Rendering;

import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Block.Block;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class BlockRenderer
{

    public static void renderBlockIcon(VerticesBuffer t, float scale_x, float scale_y, float x, float y, Block block)
    {
        renderBlockIcon(t, 0, 0, scale_x, scale_y, x, y, block);
    }

    public static void renderBlockIcon(VerticesBuffer t, float rot, float rot2, float scale_x, float scale_y, float x, float y, Block block)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, -50.0F);
        GL11.glScalef(scale_x, scale_y, 16.0F);
        GL11.glRotatef(-35.0F + rot2, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F + rot, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-1.5F, 0.5F, 0.5F);
        GL11.glScalef(-1.0F, -1.0F, -1.0F);
        GL11.glBindTexture(3553, TextureEngine.getTerrain());
        GL11.glEnable(3553);
        t.begin();
        if (block.isTile()) {
            renderTile0(t, -2, 0, 0, block);
            renderTile1(t, -2, 0, 0, block);
        }
        else {
            renderTopSide(t, -2, 0, 0, block);
            if (rot == 0) renderBottomSide(t, -2, 0, 0, block);
            if (rot == 0) renderBackSide(t, -2, 0, 0, block);
            renderRightSide(t, -2, 0, 0, block);
            if (rot == 0) renderFrontSide(t, -2, 0, 0, block);
            if (rot == 0) renderLeftSide(t, -2, 0, 0, block);
        }
        t.end();
        GL11.glDisable(3553);
        GL11.glPopMatrix();
    }

    public static void render(VerticesBuffer t, float x, float y, float z, Block block)
    {
        if (block.isTile()) {
            renderTile0(t, x, y, z, block);
            renderTile1(t, x, y, z, block);
        }
        else {
            if (haveToRenderSide(0, x, y, z, block))
            {
                renderTopSide(t, x, y, z, block);
            }
            if (haveToRenderSide(1, x, y, z, block))
            {
                renderBottomSide(t, x, y, z, block);
            }
            if (haveToRenderSide(2, x, y, z, block))
            {
                renderBackSide(t, x, y, z, block);
            }
            if (haveToRenderSide(3, x, y, z, block))
            {
                renderRightSide(t, x, y, z, block);
            }
            if (haveToRenderSide(4, x, y, z, block))
            {
                renderFrontSide(t, x, y, z, block);
            }
            if (haveToRenderSide(5, x, y, z, block))
            {
                renderLeftSide(t, x, y, z, block);
            }
        }
    }

    public static boolean haveToRenderSide(int side, float x, float y, float z, Block block)
    {
        Block block0 = null;

        if (side == 0) block0 = Opencraft.getLevel().getBlock((int)x, (int)y + 1, (int)z);
        if (side == 1) block0 = Opencraft.getLevel().getBlock((int)x, (int)y - 1, (int)z);
        if (side == 2) block0 = Opencraft.getLevel().getBlock((int)x, (int)y, (int)z - 1);
        if (side == 3) block0 = Opencraft.getLevel().getBlock((int)x + 1, (int)y, (int)z);
        if (side == 4) block0 = Opencraft.getLevel().getBlock((int)x, (int)y, (int)z + 1);
        if (side == 5) block0 = Opencraft.getLevel().getBlock((int)x - 1, (int)y, (int)z);

        if (block.isLiquid())
        {
            if (side == 0) return !block0.isVisible();
            if (side == 1) return !block0.isVisible();
            if (side == 2) return !block0.isVisible();
            if (side == 3) return !block0.isVisible();
            if (side == 4) return !block0.isVisible();
            if (side == 5) return !block0.isVisible();
        }

        if (side == 0) return !block0.isVisible() || block0.hasTranslucent() || block0.isLiquid() || block0.isTile();
        if (side == 1) return !block0.isVisible() || block0.hasTranslucent() || block0.isLiquid() || block0.isTile();
        if (side == 2) return !block0.isVisible() || block0.hasTranslucent() || block0.isLiquid() || block0.isTile();
        if (side == 3) return !block0.isVisible() || block0.hasTranslucent() || block0.isLiquid() || block0.isTile();
        if (side == 4) return !block0.isVisible() || block0.hasTranslucent() || block0.isLiquid() || block0.isTile();
        if (side == 5) return !block0.isVisible() || block0.hasTranslucent() || block0.isLiquid() || block0.isTile();

        return false;
    }

    public static void renderTopSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        renderTopSide(t, x, y, z, 1, block);
    }

    public static void renderBottomSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        renderBottomSide(t, x, y, z, 1, block);
    }

    public static void renderBackSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        renderBackSide(t, x, y, z, 1, block);
    }

    public static void renderRightSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        renderRightSide(t, x, y, z, 1, block);
    }

    public static void renderFrontSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        renderFrontSide(t, x, y, z, 1, block);
    }

    public static void renderLeftSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        renderLeftSide(t, x, y, z, 1, block);
    }
    
    public static void renderTopSide(VerticesBuffer t, float x, float y, float z, float blockSize, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;
        if (block.isLiquid() && !Opencraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isLiquid())
            down = 0.1f;

        t.setColori(block_r, block_g, block_b);
        t.setTexCoord(block.getTexture().getTopTextureX() + 0.0f, block.getTexture().getTopTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, blockSize + y - down, z);
        t.setTexCoord(block.getTexture().getTopTextureX() + 0.0f, block.getTexture().getTopTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, blockSize + y - down, z);
        t.setTexCoord(block.getTexture().getTopTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getTopTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, blockSize + y - down, blockSize + z);
        t.setTexCoord(block.getTexture().getTopTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getTopTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, blockSize + y - down, blockSize + z);
    }

    public static void renderBottomSide(VerticesBuffer t, float x, float y, float z, float blockSize, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;
        if (block.isLiquid() && !Opencraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isLiquid())
            down = 0.1f;

        t.setColori(block_r, block_g, block_b);
        t.setTexCoord(block.getTexture().getBottomTextureX() + 0.0f, block.getTexture().getBottomTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, y, blockSize + z);
        t.setTexCoord(block.getTexture().getBottomTextureX() + 0.0f, block.getTexture().getBottomTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, y, blockSize + z);
        t.setTexCoord(block.getTexture().getBottomTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getBottomTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, y, z);
        t.setTexCoord(block.getTexture().getBottomTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getBottomTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, y, z);
    }

    public static void renderBackSide(VerticesBuffer t, float x, float y, float z, float blockSize, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;
        if (block.isLiquid() && !Opencraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isLiquid())
            down = 0.1f;

        t.setColori(block_r - 40, block_g - 40, block_b - 40);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(blockSize + x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, blockSize + y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, blockSize + y - down, z);
    }

    public static void renderRightSide(VerticesBuffer t, float x, float y, float z, float blockSize, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;
        if (block.isLiquid() && !Opencraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isLiquid())
            down = 0.1f;

        t.setColori(block_r - 80, block_g - 80, block_b - 80);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(blockSize + x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(blockSize + x, y - down, blockSize + z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, blockSize + y - down, blockSize + z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, blockSize + y - down, z);
    }

    public static void renderFrontSide(VerticesBuffer t, float x, float y, float z, float blockSize, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;
        if (block.isLiquid() && !Opencraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isLiquid())
            down = 0.1f;

        t.setColori(block_r - 40, block_g - 40, block_b - 40);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(blockSize + x, y - down, blockSize + z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, y - down, blockSize + z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, blockSize + y - down, blockSize + z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(blockSize + x, blockSize + y - down, blockSize + z);
    }

    public static void renderLeftSide(VerticesBuffer t, float x, float y, float z, float blockSize, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;
        if (block.isLiquid() && !Opencraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isLiquid())
            down = 0.1f;

        t.setColori(block_r - 80, block_g - 80, block_b - 80);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, y - down, blockSize + z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, blockSize + y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, blockSize + y - down, blockSize + z);
    }


    public static void renderTile0(VerticesBuffer t, float x, float y, float z, Block block) {
        renderTile0(t, x, y, z, 0, block);
    }


    public static void renderTile1(VerticesBuffer t, float x, float y, float z, Block block) {
        renderTile1(t, x, y, z, 0, block);

    }

    public static void renderTile1(VerticesBuffer t, float x, float y, float z, float tileSize, Block block) {
        int block_r=255;
        int block_g=255;
        int block_b=255;

        t.setColori(block_r - 80, block_g - 80, block_b - 80);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x+(0.1f + tileSize), y, z+(0.1f + tileSize));
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x+(0.9f - tileSize), y, z+(0.9f - tileSize));
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x+(0.9f - tileSize), y+(0.9f - tileSize), z+(0.9f - tileSize));
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x+(0.1f + tileSize), y+0.9f, z+(0.1f + tileSize));
    }

    public static void renderTile0(VerticesBuffer t, float x, float y, float z, float tileSize, Block block) {
        int block_r=255;
        int block_g=255;
        int block_b=255;

        t.setColori(block_r - 80, block_g - 80, block_b - 80);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x+(0.9f - tileSize), y, z+(0.1f + tileSize));
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.getTextureAtlasSize());
        t.setVertexCoord(x+(0.1f + tileSize), y, z+(0.9f - tileSize));
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x+(0.1f + tileSize), y+(0.9f - tileSize), z+(0.9f - tileSize));
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.getTextureAtlasSize(), block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x+(0.9f - tileSize), y+(0.9f - tileSize), z+(0.1f + tileSize));
    }

    public static void renderBlockOutline(EntityPlayer player, VerticesBuffer t, int x, int y, int z, int face) {
        float x0 = (float)x + -0.001F;
        float x1 = (float)x +  1.001F;
        float y0 = (float)y + -0.001F;
        float y1 = (float)y +  1.001F;
        float z0 = (float)z + -0.001F;
        float z1 = (float)z +  1.001F;

        Vector3f playerPos = player.getPosition();

        if (face == 0 && (float)y > playerPos.y) {
            t.vertex(x0, y0, z1);
            t.vertex(x0, y0, z0);
            t.vertex(x1, y0, z0);
            t.vertex(x1, y0, z1);
        }

        if (face == 1 && (float)y < playerPos.y) {
            t.vertex(x1, y1, z1);
            t.vertex(x1, y1, z0);
            t.vertex(x0, y1, z0);
            t.vertex(x0, y1, z1);
        }

        if (face == 2 && (float)z > playerPos.z) {
            t.vertex(x0, y1, z0);
            t.vertex(x1, y1, z0);
            t.vertex(x1, y0, z0);
            t.vertex(x0, y0, z0);
        }

        if (face == 3 && (float)z < playerPos.z) {
            t.vertex(x0, y1, z1);
            t.vertex(x0, y0, z1);
            t.vertex(x1, y0, z1);
            t.vertex(x1, y1, z1);
        }

        if (face == 4 && (float)x > playerPos.x) {
            t.vertex(x0, y1, z1);
            t.vertex(x0, y1, z0);
            t.vertex(x0, y0, z0);
            t.vertex(x0, y0, z1);
        }

        if (face == 5 && (float)x < playerPos.x) {
            t.vertex(x1, y0, z1);
            t.vertex(x1, y0, z0);
            t.vertex(x1, y1, z0);
            t.vertex(x1, y1, z1);
        }

    }

    public static void renderLegacyTop(float x, float y, float z, float width, float height, float depth)
    {
        glTexCoord2f(0, 0);
        glVertex3f(x + width, y + height, z - depth);
        glTexCoord2f(1, 0);
        glVertex3f(x - width, y + height, z - depth);
        glTexCoord2f(1, 1);
        glVertex3f(x - width, y + height, z + depth);
        glTexCoord2f(0, 1);
        glVertex3f(x + width, y + height, z + depth);
    }

    public static void renderLegacyBottom(float x, float y, float z, float width, float height, float depth)
    {
        glTexCoord2f(0, 0);
        glVertex3f(x + width, y - height, z + depth);
        glTexCoord2f(1, 0);
        glVertex3f(x - width, y - height, z + depth);
        glTexCoord2f(1, 1);
        glVertex3f(x - width, y - height, z - depth);
        glTexCoord2f(0, 1);
        glVertex3f(x + width, y - height, z - depth);
    }

    public static void renderLegacyFront(float x, float y, float z, float width, float height, float depth)
    {
        glTexCoord2f(0, 0);
        glVertex3f(x + width, y + height, z + depth);
        glTexCoord2f(1, 0);
        glVertex3f(x - width, y + height, z + depth);
        glTexCoord2f(1, 1);
        glVertex3f(x - width, y - height, z + depth);
        glTexCoord2f(0, 1);
        glVertex3f(x + width, y - height, z + depth);
    }

    public static void renderLegacyBack(float x, float y, float z, float width, float height, float depth)
    {
        glTexCoord2f(1, 1);
        glVertex3f(x + width, y - height, z - depth);
        glTexCoord2f(0, 1);
        glVertex3f(x - width, y - height, z - depth);
        glTexCoord2f(0, 0);
        glVertex3f(x - width, y + height, z - depth);
        glTexCoord2f(1, 0);
        glVertex3f(x + width, y + height, z - depth);
    }

    public static void renderLegacyLeft(float x, float y, float z, float width, float height, float depth)
    {
        glTexCoord2f(0, 0);
        glVertex3f(x - width, y + height, z + depth);
        glTexCoord2f(1, 0);
        glVertex3f(x - width, y + height, z - depth);
        glTexCoord2f(1, 1);
        glVertex3f(x - width, y - height, z - depth);
        glTexCoord2f(0, 1);
        glVertex3f(x - width, y - height, z + depth);
    }

    public static void renderLegacyRight(float x, float y, float z, float width, float height, float depth)
    {
        glTexCoord2f(0, 0);
        glVertex3f(x + width, y + height, z - depth);
        glTexCoord2f(1, 0);
        glVertex3f(x + width, y + height, z + depth);
        glTexCoord2f(1, 1);
        glVertex3f(x + width,  - height, z + depth);
        glTexCoord2f(0, 1);
        glVertex3f(x + width, y - height, z - depth);
    }

}
