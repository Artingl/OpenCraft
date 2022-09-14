package com.artingl.opencraft.GUI.Windows;

import com.artingl.opencraft.Control.World.BlockRenderer;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Control.Game.VerticesBuffer;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Survival;
import com.artingl.opencraft.World.Item.ItemSlot;
import com.artingl.opencraft.Opencraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class PlayerHotBar extends Window
{

    public static int[] INVENTORY_TEXTURES = new int[]{
            TextureEngine.load("opencraft:gui/inventory.png"),
            TextureEngine.load("opencraft:gui/inventory_selected.png"),
            TextureEngine.load("opencraft:gui/heartbg.png"),
            TextureEngine.load("opencraft:gui/halfheart.png"),
            TextureEngine.load("opencraft:gui/fullheart.png"),
    };

    public long[] chatLineTimeout = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public PlayerHotBar() {
        super(0, 0, "player_hotbar");
    }

    @Override
    public void render(int screenWidth, int screenHeight, int scale)
    {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();

        this.width = (182f * Math.min(scale / 100, 1));
        this.height = (22f * Math.min(scale / 100, 1));

        float sel_width = (22f * Math.min(scale / 100, 1));
        float sel_height = (22f * Math.min(scale / 100, 1));

        VerticesBuffer t = VerticesBuffer.getGlobalInstance();

        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glTranslatef(screenWidth / 2f - width / 2f, screenHeight - height - 1, -60);
        fillTexture(0, 0, this.width, this.height, INVENTORY_TEXTURES[0]);
        GL11.glTranslatef((sel_width - 2) * entityPlayer.getInventorySelectedSlot(), 0, 49);
        fillTexture(0, 0, sel_width, sel_height, INVENTORY_TEXTURES[1]);

        GL11.glTranslatef(-((sel_width - 2) * entityPlayer.getInventorySelectedSlot()), 0, 49);

        if (entityPlayer.getGamemode().getId() == Survival.id) {
            int hearts = entityPlayer.getHearts();
            for (int i = 0; i < entityPlayer.getMaxHearts()/2; i++) {
                GL11.glTranslatef(9 * i, -15, 49);
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
                GL11.glTranslatef(-(9 * i), 15, -49);
            }
        }

        GL11.glPopMatrix();
        GL11.glDisable(GL_BLEND);

        for (int i = 0; i < 9; i++)
        {
            ItemSlot item = entityPlayer.getInventoryItem(i);

            if (item == null)
                continue;

            if (item.getItemType() == ItemSlot.ItemType.BLOCK) {
                BlockRenderer.renderBlockIcon(t, 9, 9, screenWidth / 2f - width / 2f + ((sel_width - 2) * i) + 11, screenHeight - 12, item.getBlock());
            }
            else {
                // todo
            }

            Opencraft.getFont().drawShadow(entityPlayer.getInventoryItem(i).getAmount()+"", (int) (screenWidth / 2f - width / 2f + ((sel_width - 2) * i) + 11), screenHeight - 12, 0xFFFFFF);
        }

        ArrayList<String> chat = Opencraft.getPlayerEntity().getChatHistory();
        int y = 0;
        int start_val = (Math.min(chat.size() - 1, 10));
        for (int i = start_val; i >= 0; i--) {
            String line = chat.get(i);
            int ix = 2;
            int iy = screenHeight - 40 - (y * Opencraft.getFont().getCharHeight())+2;
            int w = (int)(screenWidth / 2f - (10 * (Opencraft.getMaxGuiScale() - Opencraft.getGuiScale()) * 5))+ix;

            fill(0, iy-2, w, Opencraft.getFont().getCharHeight()+iy+(i == start_val ? 2 : -2), 0x55000000);
            Opencraft.getFont().drawShadow(line, ix, iy, 0xFFFFFF);
            ++y;
        }

        super.render(screenWidth, screenHeight, scale);
    }

}
