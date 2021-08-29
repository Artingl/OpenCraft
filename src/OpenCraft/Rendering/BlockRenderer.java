package OpenCraft.Rendering;

import OpenCraft.World.Entity.Player;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Chunk;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class BlockRenderer
{

    public static void renderBlockIcon(VerticesBuffer t, float scale_x, float scale_y, float x, float y, Block block)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, -50.0F);
        GL11.glScalef(scale_x, scale_y, 16.0F);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-1.5F, 0.5F, 0.5F);
        GL11.glScalef(-1.0F, -1.0F, -1.0F);
        GL11.glBindTexture(3553, TextureEngine.getTerrain());
        GL11.glEnable(3553);
        t.begin();
        render(t, -2, 0, 0, block);
        t.end();
        GL11.glDisable(3553);
        GL11.glPopMatrix();
    }

    public static void render(VerticesBuffer t, float x, float y, float z, Block block)
    {
        renderTopSide(t, x, y, z, block);
        renderBottomSide(t, x, y, z, block);
        renderBackSide(t, x, y, z, block);
        renderRightSide(t, x, y, z, block);
        renderFrontSide(t, x, y, z, block);
        renderLeftSide(t, x, y, z, block);
    }

    public static void render(int layer, float x, float y, float z, Block block)
    {
        render(Chunk.verticesBuffer, layer, x, y, z, block);
    }

    public static void render(VerticesBuffer t, int layer, float x, float y, float z, Block block)
    {
        if (haveToRenderSide(layer, 0, x, y, z, block))
        {
            renderTopSide(t, x, y, z, block);
        }
        if (haveToRenderSide(layer, 1, x, y, z, block))
        {
            renderBottomSide(t, x, y, z, block);
        }
        if (haveToRenderSide(layer, 2, x, y, z, block))
        {
            renderBackSide(t, x, y, z, block);
        }
        if (haveToRenderSide(layer, 3, x, y, z, block))
        {
            renderRightSide(t, x, y, z, block);
        }
        if (haveToRenderSide(layer, 4, x, y, z, block))
        {
            renderFrontSide(t, x, y, z, block);
        }
        if (haveToRenderSide(layer, 5, x, y, z, block))
        {
            renderLeftSide(t, x, y, z, block);
        }
    }

    public static boolean haveToRenderSide(int layer, int side, float x, float y, float z, Block block)
    {
        //if (layer != 2 && block.hasTranslucent()) return false;
        //if (layer == 2 && !block.hasTranslucent()) return false;

        if (block.isLiquid())
        {
            if (side == 0) return !OpenCraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isSolid();
            if (side == 1) return !OpenCraft.getLevel().getBlock((int)x, (int)y - 1, (int)z).isSolid();
            if (side == 2) return !OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z - 1).isSolid();
            if (side == 3) return !OpenCraft.getLevel().getBlock((int)x + 1, (int)y, (int)z).isSolid();
            if (side == 4) return !OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z + 1).isSolid();
            if (side == 5) return !OpenCraft.getLevel().getBlock((int)x - 1, (int)y, (int)z).isSolid();
        }
//
        if (side == 0) return !OpenCraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isSolid() || (OpenCraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).hasTranslucent() && !block.hasTranslucent()) || OpenCraft.getLevel().getBlock((int)x, (int)y + 1, (int)z).isLiquid();
        if (side == 1) return !OpenCraft.getLevel().getBlock((int)x, (int)y - 1, (int)z).isSolid() || (OpenCraft.getLevel().getBlock((int)x, (int)y - 1, (int)z).hasTranslucent() && !block.hasTranslucent()) || OpenCraft.getLevel().getBlock((int)x, (int)y - 1, (int)z).isLiquid();
        if (side == 2) return !OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z - 1).isSolid() || (OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z - 1).hasTranslucent() && !block.hasTranslucent()) || OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z - 1).isLiquid();
        if (side == 3) return !OpenCraft.getLevel().getBlock((int)x + 1, (int)y, (int)z).isSolid() || (OpenCraft.getLevel().getBlock((int)x + 1, (int)y, (int)z).hasTranslucent() && !block.hasTranslucent()) || OpenCraft.getLevel().getBlock((int)x + 1, (int)y, (int)z).isLiquid();
        if (side == 4) return !OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z + 1).isSolid() || (OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z + 1).hasTranslucent() && !block.hasTranslucent()) || OpenCraft.getLevel().getBlock((int)x, (int)y, (int)z + 1).isLiquid();
        if (side == 5) return !OpenCraft.getLevel().getBlock((int)x - 1, (int)y, (int)z).isSolid() || (OpenCraft.getLevel().getBlock((int)x - 1, (int)y, (int)z).hasTranslucent() && !block.hasTranslucent()) || OpenCraft.getLevel().getBlock((int)x - 1, (int)y, (int)z).isLiquid();
//
        return false;//OpenCraft.getLevel().isLit((int)x, (int)y, (int)z) ^ layer == 1;
    }

    public static void renderTopSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;

        t.setColori(block_r, block_g, block_b);
        t.setTexCoord(block.getTexture().getTopTextureX() + 0.0f, block.getTexture().getTopTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, 1.f +y - down, z);
        t.setTexCoord(block.getTexture().getTopTextureX() + 0.0f, block.getTexture().getTopTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, 1.f +y - down, z);
        t.setTexCoord(block.getTexture().getTopTextureX() + TextureEngine.addTextCoord, block.getTexture().getTopTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, 1.f +y - down, 1.f +z);
        t.setTexCoord(block.getTexture().getTopTextureX() + TextureEngine.addTextCoord, block.getTexture().getTopTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, 1.f +y - down, 1.f +z);
    }

    public static void renderBottomSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;

        t.setColori(block_r, block_g, block_b);
        t.setTexCoord(block.getTexture().getBottomTextureX() + 0.0f, block.getTexture().getBottomTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, y, 1.f +z);
        t.setTexCoord(block.getTexture().getBottomTextureX() + 0.0f, block.getTexture().getBottomTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, y, 1.f +z);
        t.setTexCoord(block.getTexture().getBottomTextureX() + TextureEngine.addTextCoord, block.getTexture().getBottomTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, y, z);
        t.setTexCoord(block.getTexture().getBottomTextureX() + TextureEngine.addTextCoord, block.getTexture().getBottomTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, y, z);
    }

    public static void renderBackSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;

        t.setColori(block_r - 40, block_g - 40, block_b - 40);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(1.f +x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, 1.f +y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, 1.f +y - down, z);
    }

    public static void renderRightSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;

        t.setColori(block_r - 80, block_g - 80, block_b - 80);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(1.f +x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(1.f +x, y - down, 1.f +z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, 1.f +y - down, 1.f +z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, 1.f +y - down, z);
    }

    public static void renderFrontSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;

        t.setColori(block_r - 40, block_g - 40, block_b - 40);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(1.f +x, y - down, 1.f +z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, y - down, 1.f +z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, 1.f +y - down, 1.f +z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(1.f +x, 1.f +y - down, 1.f +z);
    }

    public static void renderLeftSide(VerticesBuffer t, float x, float y, float z, Block block)
    {
        int block_r=255;
        int block_g=255;
        int block_b=255;
        float down = 0;

        t.setColori(block_r - 80, block_g - 80, block_b - 80);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, y - down, 1.f +z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + TextureEngine.addTextCoord);
        t.setVertexCoord(x, y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + 0.0f, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, 1.f +y - down, z);
        t.setTexCoord(block.getTexture().getSideTextureX() + TextureEngine.addTextCoord, block.getTexture().getSideTextureY() + 0.0f);
        t.setVertexCoord(x, 1.f +y - down, 1.f +z);
    }

    public static void renderFaceNoTexture(Player player, VerticesBuffer t, int x, int y, int z, int face) {
        float x0 = (float)x + -0.001F;
        float x1 = (float)x +  1.001F;
        float y0 = (float)y + -0.001F;
        float y1 = (float)y +  1.001F;
        float z0 = (float)z + -0.001F;
        float z1 = (float)z +  1.001F;
        if (face == 0 && (float)y > player.getY()) {
            t.vertex(x0, y0, z1);
            t.vertex(x0, y0, z0);
            t.vertex(x1, y0, z0);
            t.vertex(x1, y0, z1);
        }

        if (face == 1 && (float)y < player.getY()) {
            t.vertex(x1, y1, z1);
            t.vertex(x1, y1, z0);
            t.vertex(x0, y1, z0);
            t.vertex(x0, y1, z1);
        }

        if (face == 2 && (float)z > player.getZ()) {
            t.vertex(x0, y1, z0);
            t.vertex(x1, y1, z0);
            t.vertex(x1, y0, z0);
            t.vertex(x0, y0, z0);
        }

        if (face == 3 && (float)z < player.getZ()) {
            t.vertex(x0, y1, z1);
            t.vertex(x0, y0, z1);
            t.vertex(x1, y0, z1);
            t.vertex(x1, y1, z1);
        }

        if (face == 4 && (float)x > player.getX()) {
            t.vertex(x0, y1, z1);
            t.vertex(x0, y1, z0);
            t.vertex(x0, y0, z0);
            t.vertex(x0, y0, z1);
        }

        if (face == 5 && (float)x < player.getX()) {
            t.vertex(x1, y0, z1);
            t.vertex(x1, y0, z0);
            t.vertex(x1, y1, z0);
            t.vertex(x1, y1, z1);
        }

    }

    public static void renderLegacyTop(int x, int y, int z)
    {
        GL11.glTranslatef(x, y, z);

        glTexCoord2f(0, 0);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glTexCoord2f(1, 0);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glTexCoord2f(1, 1);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glTexCoord2f(0, 1);
        glVertex3f(1.0f, 1.0f, 1.0f);

        GL11.glTranslatef(-x, -y, -z);
    }

    public static void renderLegacyBottom(int x, int y, int z)
    {
        GL11.glTranslatef(x, y, z);

        glTexCoord2f(0, 0);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glTexCoord2f(1, 0);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glTexCoord2f(1, 1);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glTexCoord2f(0, 1);
        glVertex3f(1.0f, -1.0f, -1.0f);

        GL11.glTranslatef(-x, -y, -z);
    }

    public static void renderLegacyFront(int x, int y, int z)
    {
        GL11.glTranslatef(x, y, z);

        glTexCoord2f(0, 0);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glTexCoord2f(1, 0);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glTexCoord2f(1, 1);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glTexCoord2f(0, 1);
        glVertex3f(1.0f, -1.0f, 1.0f);

        GL11.glTranslatef(-x, -y, -z);
    }

    public static void renderLegacyBack(int x, int y, int z)
    {
        GL11.glTranslatef(x, y, z);

        glTexCoord2f(1, 1);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glTexCoord2f(0, 1);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glTexCoord2f(0, 0);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glTexCoord2f(1, 0);
        glVertex3f(1.0f, 1.0f, -1.0f);

        GL11.glTranslatef(-x, -y, -z);
    }

    public static void renderLegacyLeft(int x, int y, int z)
    {
        GL11.glTranslatef(x, y, z);

        glTexCoord2f(0, 0);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glTexCoord2f(1, 0);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glTexCoord2f(1, 1);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glTexCoord2f(0, 1);
        glVertex3f(-1.0f, -1.0f, 1.0f);

        GL11.glTranslatef(-x, -y, -z);
    }

    public static void renderLegacyRight(int x, int y, int z)
    {
        GL11.glTranslatef(x, y, z);

        glTexCoord2f(0, 0);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glTexCoord2f(1, 0);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glTexCoord2f(1, 1);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glTexCoord2f(0, 1);
        glVertex3f(1.0f, -1.0f, -1.0f);

        GL11.glTranslatef(-x, -y, -z);
    }

}
