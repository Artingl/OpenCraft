package com.artingl.opencraft.Control.Render;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.MathUtils;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityMP;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityPlayerMP;
import com.artingl.opencraft.Control.World.LevelController;
import com.artingl.opencraft.Resources.Options.OptionsRegistry;
import com.artingl.opencraft.World.Level.Biomes.Biome;
import com.artingl.opencraft.World.Level.LevelTypes;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Block.BlockDestroy;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.RayCast;
import com.artingl.opencraft.GUI.Font.Font;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RENDERER;

public class GameRenderer {

    public static void render()
    {
        try {
            if (!Opencraft.isInMenu()) {
                Opencraft.getPlayerController().rotate();
                Input.setMouseGrabbed(Opencraft.getPlayerScreen() == null);
            }

            GL11.glClear(16640);
            GameRenderer.transformCamera();
            Frustum frustum = Frustum.getInstance();

            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glEnable(GL_BLEND);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            glFrontFace(GL_CW);
            Opencraft.getLevelController().render(frustum);
            glDisable(GL_CULL_FACE);

            EntityPlayer player = Opencraft.getPlayerEntity();
            ClientLevel level = Opencraft.getLevel();

            glEnable(GL_BLEND);

            for(int i = 0; i < Opencraft.getTimer().ticks; ++i) {
                Opencraft.getClientConnection().tick();
                Opencraft.getInternalServer().tick();
            }

            for (EntityMP entity : Opencraft.getClientConnection().getEntities()) {
                if (entity instanceof EntityPlayerMP && entity.getLevelMP() == level.getLevelType() &&
                        MathUtils.calculateLength(entity.getPosition(), player.getPosition()) < (Opencraft.getRenderDistance() * 16) - 16) {
                    entity.render();
                }
            }

            new HashMap<>(Opencraft.getRenderersInterfaceMap()).forEach(((id, tick) -> {
                try {
                    if (tick != null) tick.render();
                } catch (Exception e) {
                    Logger.exception("Error occurred while running GL update function", e);
                }
            }));


            Opencraft.getParticleEngine().render(Opencraft.getTimer().a);
            GameRenderer.renderBlockOutline();

            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            Camera.perspective(getFOV(), (float) Opencraft.getWidth() / (float) Opencraft.getHeight(), 0.05F, (16 * Opencraft.getRenderDistance()) * 2.0F + 48);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GameRenderer.renderViewbobbing();
            GameRenderer.renderHand();

            glDisable(GL_BLEND);

            GL11.glPopMatrix();
            GL11.glDisable(3042);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glDisable(2912);

            drawGui();

            Opencraft.updateGlContext();
        } catch (Exception e) {
            Logger.exception("Error occurred while rendering", e);
        }
    }

    public static void drawGui()
    {
        Font font = Opencraft.getFont();

        int width = Opencraft.getWidth();
        int height = Opencraft.getHeight();

        try {
            int screenWidth = width * Opencraft.getGuiScaleValue() / height;
            int screenHeight = height * Opencraft.getGuiScaleValue() / height;

            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, (double)screenWidth, (double)screenHeight, 0.0D, 100.0D, 300.0D);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);

            BufferRenderer t = BufferRenderer.getGlobalInstance();
            if (Opencraft.getCurrentScreen() != null)
            {
                Opencraft.getCurrentScreen().render(screenWidth, screenHeight, Opencraft.getGuiScaleValue());
            }

            if (Opencraft.getPlayerScreen() != null) {
                Opencraft.getPlayerScreen().render(screenWidth, screenHeight, Opencraft.getGuiScaleValue());
            }

            if (OptionsRegistry.Values.getBooleanOption("showInformation")) {
                String gpuRenderer = GL11.glGetString(GL_RENDERER);

                int line = 0;

                font.drawShadow("Opencraft " + Opencraft.getVersion(), 2, 2, 0xFFFFFF);

                if (!Opencraft.isInMenu() && Opencraft.isWorldLoaded()) {
                    ClientLevel level = Opencraft.getLevel();
                    LevelTypes levelType = level.getLevelType();

                    EntityPlayer entityPlayer = Opencraft.getPlayerEntity();
                    Vector3f playerPos = entityPlayer.getPosition();

                    Biome biome = Opencraft.getLevel().getGeneration().getLevelGeneration().getBiome((int) playerPos.x, (int) playerPos.z);

                    font.drawShadow("X Y Z: " + playerPos.x + " " + +playerPos.y + " " + +playerPos.z, 2, 2 + (++line * 10), 0xFFFFFF);
                    font.drawShadow("FPS: " + Opencraft.getFPS() +
                            ", Chunks updated: " + Opencraft.getChunksUpdatedCount() +
                            ", Chunks rendered: " + LevelController.CHUNKS_RENDERED +
                            ", Chunks amount: " + Opencraft.getLevelController().getChunksAmount(), 2, 2 + (++line * 10), 0xFFFFFF);
                    font.drawShadow("Level: " + levelType, 2, 2 + (++line * 10), 0xFFFFFF);
                    font.drawShadow("Biome: " + biome.getName(), 2, 2 + (++line * 10), 0xFFFFFF);
                }
                else {
                    font.drawShadow("FPS: " + Opencraft.getFPS(), 2, 2 + (++line * 10), 0xFFFFFF);
                }

                ++line;
                font.drawShadow("GPU: " + gpuRenderer, 2, 2 + (++line * 10), 0xFFFFFF);
                font.drawShadow("Display: " + Opencraft.getDisplay().getWidth() + "x" + Opencraft.getDisplay().getHeight(), 2, 2 + (++line * 10), 0xFFFFFF);
            }

            if (!Opencraft.isInMenu() && Opencraft.isWorldLoaded())
            {
                if (Opencraft.getPlayerEntity().headInWater()) {
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glColor4f(0.31f, 0.5f, 1, 0.6f);
                    t.begin();
                    t.vertex((float)0, (float)screenHeight, 0.0F);
                    t.vertex((float)screenWidth, (float)screenHeight, 0.0F);
                    t.vertex((float)screenWidth, (float)0, 0.0F);
                    t.vertex((float)0, (float)0, 0.0F);
                    t.end();
                    GL11.glDisable(3042);
                }

                int wc = screenWidth / 2;
                int hc = screenHeight / 2;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                t.begin();
                t.vertex((float)(wc + 1), (float)(hc - 4), 0.0F);
                t.vertex((float)(wc - 0), (float)(hc - 4), 0.0F);
                t.vertex((float)(wc - 0), (float)(hc + 5), 0.0F);
                t.vertex((float)(wc + 1), (float)(hc + 5), 0.0F);
                t.vertex((float)(wc + 5), (float)(hc - 0), 0.0F);
                t.vertex((float)(wc - 4), (float)(hc - 0), 0.0F);
                t.vertex((float)(wc - 4), (float)(hc + 1), 0.0F);
                t.vertex((float)(wc + 5), (float)(hc + 1), 0.0F);
                t.end();

                new HashMap<>(Opencraft.getGuiInterfaceMap()).forEach(((id, iGuiInterface) -> {
                    //GL11.glColor4f(1, 1, 1, 1);
                    if (iGuiInterface != null) iGuiInterface.render(screenWidth, screenHeight, Opencraft.getGuiScaleValue());
                    GL11.glLoadIdentity();
                    GL11.glTranslatef(0.0F, 0.0F, 0.0F);
                }));
            }

            GL11.glDisable(3042);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glDisable(2912);
        } catch (Exception e) {
            Logger.exception("Error occurred while drawing gui", e);
        }
    }

    public static void renderBlockOutline()
    {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();
        Vector3f pos = entityPlayer.getPosition();
        Vector2f rotation = entityPlayer.getRotation();

        RayCast.RayResult[] ray = RayCast.rayCastToBlock(6, rotation.x, rotation.y, pos.x, pos.y, pos.z);
        if (ray[0].state)
        {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glColor3d(0, 0, 0);
            BufferRenderer t = BufferRenderer.getGlobalInstance();
            for(int i = 0; i < 6; ++i) {
                t.begin();
                BlockRenderer.renderBlockOutline(entityPlayer, t, ray[0].x, ray[0].y, ray[0].z, i);
                t.end();
            }

            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glColor3d(1, 1, 1);

            float value = Opencraft.getPlayerController().getBlockBreakState();

            if (value > 0) {
                value++;
                if (value > 10) value = 10;

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                Opencraft.getShaderProgram().bindTexture(TextureEngine.getTerrain());

                t.begin();
                BlockRenderer.renderBackSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderTopSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderLeftSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderRightSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderBottomSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderFrontSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                t.end();

                GL11.glEndList();
                Opencraft.getShaderProgram().bindTexture(0);
            }
        }
    }

    public static void transformCamera()
    {
        EntityPlayer player = Opencraft.getPlayerEntity();
        if (player == null) {
            return;
        }

        Vector3f pos = player.getPosition();
        Vector3f prevPos = player.getPrevPosition();
        Vector2f rotation = player.getRotation();
        Vector2f prevRotation = player.getPrevRotation();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        Camera.perspective(
                getFOV(),
                (float) Opencraft.getWidth() / (float) Opencraft.getHeight(),
                0.05F, (OptionsRegistry.Values.getBooleanOption("enableFog") ? (16 * Opencraft.getRenderDistance()) : (16 * Opencraft.getRenderDistance()) * 2)
        );
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        GameRenderer.renderViewbobbing();

        float x = prevPos.x + (pos.x - prevPos.x) * Opencraft.getTimer().a;
        float y = prevPos.y + (pos.y - prevPos.y) * Opencraft.getTimer().a - 2 + player.getHeightOffset();
        float z = prevPos.z + (pos.z - prevPos.z) * Opencraft.getTimer().a;

        GL11.glTranslatef(0.0F, 0.0F, -0.3F);
        GL11.glRotatef(prevRotation.x + (rotation.x - prevRotation.x) * Opencraft.getTimer().a, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(prevRotation.y + (rotation.y - prevRotation.y) * Opencraft.getTimer().a, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-x, -y, -z);
    }

    public static int getFOV() {
        return Opencraft.getFOV();
    }

    public static void renderHand() {
        /*
        EntityPlayer player = Opencraft.getPlayerEntity();

        Vector2f armRotation = player.getArmRotation();
        Vector2f prevArmRotation = player.getPrevArmRotation();
        Vector2f rotation = player.getRotation();
        Vector2f prevRotation = player.getPrevRotation();

        float equippedProgress = 1;//this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * par1;
        float cameraRotX = prevRotation.x + (rotation.x - prevRotation.x) * Opencraft.getTimer().a;

        GL11.glPushMatrix();
        GL11.glRotatef(cameraRotX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(prevRotation.y + (rotation.y - prevRotation.y) * Opencraft.getTimer().a, 0.0F, 1.0F, 0.0F);
        GL11.glPopMatrix();

        float armRotX = prevArmRotation.x + (armRotation.x - prevArmRotation.x) * Opencraft.getTimer().a;
        float armRotY = prevArmRotation.y + (armRotation.y - prevArmRotation.y) * Opencraft.getTimer().a;
        GL11.glRotatef((rotation.x - armRotX) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((rotation.y - armRotY) * 0.1F, 0.0F, 1.0F, 0.0F);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();
        armRotY = 0.8F;
        float var19 = (player.getController().getBlockBreakState() / 9);//var3.getSwingProgress(par1);
        float var21 = (float) Math.sin(var19 * (float)Math.PI);
        float var10 = (float) Math.sin(Math.sqrt(var19) * (float)Math.PI);
        GL11.glTranslatef(-var10 * 0.4F, (float) (Math.sin(Math.sqrt(var19) * (float)Math.PI * 2.0F) * 0.2F), -var21 * 0.2F);
        var19 = 1.0F - cameraRotX / 45.0F + 0.1F;

        if (var19 < 0.0F) var19 = 0.0F;

        if (var19 > 1.0F) var19 = 1.0F;

        GL11.glEnable(GL_TEXTURE_2D);

        var19 = (float) (-Math.cos(var19 * (float)Math.PI) * 0.5F + 0.5F);
        GL11.glTranslatef(0.0F, 0.0F * armRotY - (1.0F - equippedProgress) * 1.2F - var19 * 0.5F + 0.04F, -0.9F * armRotY);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(var19 * -85.0F, 0.0F, 0.0F, 1.0F);
        Opencraft.getShaderProgram().bindTexture(EntityPlayer.TEXTURE);

        int var22 = -1;
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)var22);
        GL11.glRotatef((float)(-45 * var22), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef((float)(-65 * var22), 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        EntityPlayer.model.arm0.render();
        GL11.glPopMatrix();

        GL11.glPopMatrix();
        GL11.glDisable(GL_TEXTURE_2D);*/
    }

    public static void renderViewbobbing() {
        EntityPlayer player = Opencraft.getPlayerEntity();
        Vector2f rotation = player.getCameraRotation();
        Vector2f prevRotation = player.getPrevCameraRotation();

        if (!OptionsRegistry.Values.getBooleanOption("enableViewBobbing"))
            return;

        float dist = -(player.getDistanceWalked() + (player.getDistanceWalked() - player.getPrevDistanceWalked()) * Opencraft.getTimer().a);
        float cameraY = prevRotation.y + (rotation.y - prevRotation.y) * Opencraft.getTimer().a;
        float cameraX = prevRotation.x + (rotation.x - prevRotation.x) * Opencraft.getTimer().a;
        GL11.glTranslatef((float)Math.sin(dist * (float)Math.PI) * cameraY * 0.5F, -Math.abs((float)Math.cos(dist * (float)Math.PI) * cameraY), 0.0F);
        GL11.glRotatef((float)Math.sin(dist * (float)Math.PI) * cameraY * 3.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(Math.abs((float)Math.cos(dist * (float)Math.PI - 0.2F) * cameraY) * 5.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(cameraX, 1.0F, 0.0F, 0.0F);
    }

}
