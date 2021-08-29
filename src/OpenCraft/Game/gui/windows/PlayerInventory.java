package OpenCraft.Game.gui.windows;

import OpenCraft.Game.Rendering.BlockRenderer;
import OpenCraft.Game.Rendering.TextureEngine;
import OpenCraft.Game.Rendering.VerticesBuffer;
import OpenCraft.Game.gui.Screen;
import OpenCraft.Game.gui.Window;
import OpenCraft.OpenCraft;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class PlayerInventory extends Window
{

    public static int[] INVENTORY_TEXTURES;
    public int selected = 0;

    static {
        try {
            INVENTORY_TEXTURES = new int[]{
                    TextureEngine.load(ImageIO.read(new File("resources/gui/inventory.png"))),
                    TextureEngine.load(ImageIO.read(new File("resources/gui/inventory_selected.png")))
            };
        } catch (IOException e) { }
    }

    public PlayerInventory() {
        super(0, 0, "");
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        this.width = (182f * Math.min(scale / 100, 1));
        this.height = (22f * Math.min(scale / 100, 1));

        float sel_width = (22f * Math.min(scale / 100, 1));
        float sel_height = (22f * Math.min(scale / 100, 1));

        VerticesBuffer t = VerticesBuffer.instance;

        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glTranslatef(screenWidth / 2f - width / 2f, screenHeight - height - 1, -60);
        fillTexture(0, 0, this.width, this.height, INVENTORY_TEXTURES[0]);
        GL11.glTranslatef((sel_width - 2) * selected, 0, 50);
        fillTexture(0, 0, sel_width, sel_height, INVENTORY_TEXTURES[1]);
        GL11.glPopMatrix();
        GL11.glDisable(GL_BLEND);

        for (int i = 0; i < 9; i++)
        {
            if (OpenCraft.getPlayer().getInventoryBlock(i) == null) continue;
            BlockRenderer.renderBlockIcon(t, 9, 9, screenWidth / 2f - width / 2f + ((sel_width - 2) * i) + 11, screenHeight - 12, OpenCraft.getPlayer().getInventoryBlock(i));
        }

        super.render(screenWidth, screenHeight, scale);
    }

}
