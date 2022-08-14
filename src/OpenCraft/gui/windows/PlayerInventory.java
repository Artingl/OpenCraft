package OpenCraft.gui.windows;

import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.World.Entity.EntityPlayer;
import OpenCraft.World.Entity.Gamemode.Survival;
import OpenCraft.World.Item.ItemBlock;
import OpenCraft.World.PlayerController;
import OpenCraft.gui.Window;
import OpenCraft.OpenCraft;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class PlayerInventory extends Window
{

    public static int[] INVENTORY_TEXTURES = new int[]{
            TextureEngine.load("opencraft:gui/inventory.png"),
            TextureEngine.load("opencraft:gui/inventory_selected.png"),
            TextureEngine.load("opencraft:gui/heartbg.png"),
            TextureEngine.load("opencraft:gui/halfheart.png"),
            TextureEngine.load("opencraft:gui/fullheart.png"),
    };

    public int selected = 0;

    public PlayerInventory() {
        super(0, 0, "player_hotbar");
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        PlayerController playerController = OpenCraft.getPlayerController();
        EntityPlayer entityPlayer = OpenCraft.getLevel().getPlayerEntity();

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

        GL11.glTranslatef(-((sel_width - 2) * selected), 0, 50);

        if (entityPlayer.getGamemode().getId() == Survival.id) {
            int hearts = entityPlayer.getHearts();
            for (int i = 0; i < entityPlayer.getMaxHearts()/2; i++) {
                GL11.glTranslatef(9 * i, -15, 50);
                fillTexture(0, 0, 9, 9, INVENTORY_TEXTURES[2]);
                if (hearts >= 2) {
                    GL11.glTranslatef(1, 1, 1);
                    fillTexture(0, 0, 7, 7, INVENTORY_TEXTURES[4]);
                    GL11.glTranslatef(-1, -1, -1);
                }
                else if (hearts > 0) {
                    GL11.glTranslatef(1, 1, 1);
                    fillTexture(0, 0, 7, 7, INVENTORY_TEXTURES[3]);
                    GL11.glTranslatef(-1, -1, -1);
                }
                hearts -= 2;
                GL11.glTranslatef(-(9 * i), 15, -50);
            }
        }

        GL11.glPopMatrix();
        GL11.glDisable(GL_BLEND);

        for (int i = 0; i < 9; i++)
        {
            if (playerController.getInventoryItem(i) == null) continue;
            BlockRenderer.renderBlockIcon(t, 9, 9, screenWidth / 2f - width / 2f + ((sel_width - 2) * i) + 11, screenHeight - 12, ((ItemBlock)playerController.getInventoryItem(i)).getBlock());
            OpenCraft.getFont().drawShadow(playerController.getInventoryItem(i).getAmount()+"", (int) (screenWidth / 2f - width / 2f + ((sel_width - 2) * i) + 11), screenHeight - 12, 0xFFFFFF);
        }

        super.render(screenWidth, screenHeight, scale);
    }

}
