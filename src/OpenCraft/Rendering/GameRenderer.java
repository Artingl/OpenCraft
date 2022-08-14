package OpenCraft.Rendering;

import OpenCraft.Logger.Logger;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.BlockDestroy;
import OpenCraft.World.Entity.EntityPlayer;
import OpenCraft.World.Level.Level;
import OpenCraft.World.RayCast;
import OpenCraft.gui.Font;
import OpenCraft.utils.StackTrace;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.util.HashMap;

import OpenCraft.Controls;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RENDERER;

public class GameRenderer {

    public static void render()
    {
        try {
            if (OpenCraft.isWorldDestroyed())
            {
                OpenCraft.updateGlContext();
                return;
            }

            if (!OpenCraft.isInMenu()) {
                OpenCraft.getPlayerController().rotate();
                Controls.update();
                Mouse.setGrabbed(true);
            }

            GL11.glClear(16640);
            GameRenderer.transformCamera();
            Frustum frustum = Frustum.getFrustum();
            OpenCraft.getLevelRenderer().render(frustum);
            OpenCraft.updateGlContext();

            OpenCraft.getLevelSaver().update();

            glEnable(GL_BLEND);

            new HashMap<>(OpenCraft.getRenderersInterfaceMap()).forEach(((id, tick) -> {
                try {
                    if (tick != null) tick.render();
                } catch (Exception e) {
                    Logger.exception("Error occurred while running GL update function", e);
                }
            }));

            OpenCraft.getParticleEngine().render(OpenCraft.getTimer().a);
            GameRenderer.blockOutline();
            glDisable(GL_BLEND);

            GL11.glPopMatrix();
            GL11.glDisable(3042);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glDisable(2912);

            drawGui();
        } catch (Exception e) {
            Logger.exception("Error occurred while rendering", e);
        }
    }

    public static void drawGui()
    {
        Font font = OpenCraft.getFont();

        int width = OpenCraft.getWidth();
        int height = OpenCraft.getHeight();
        int guiScale = OpenCraft.getGuiScale();

        try {
            int scale = 240;
            if (guiScale == 1) scale = 540;
            if (guiScale == 2) scale = 440;
            if (guiScale == 3) scale = 340;

            int screenWidth = width * scale / height;
            int screenHeight = height * scale / height;

            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, (double)screenWidth, (double)screenHeight, 0.0D, 100.0D, 300.0D);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);

            VerticesBuffer t = VerticesBuffer.instance;
            if (OpenCraft.getCurrentScreen() != null)
            {
                OpenCraft.getCurrentScreen().render(screenWidth, screenHeight, scale);
            }

            if (OpenCraft.isInMenu())
            {
                for(int i = 0; i < OpenCraft.getTimer().ticks; ++i) {
                    int finalScale = scale;
                    new HashMap<>(OpenCraft.getGuiTickInterfaceMap()).forEach(((id, tick) -> {
                        if (tick != null) tick.tick(screenWidth, screenHeight, finalScale);
                        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
                    }));
                }
            }

            if (!OpenCraft.isInMenu() && !OpenCraft.isWorldDestroyed())
            {
                Level level = OpenCraft.getLevel();
                Level.LevelType levelType = level.getLevelType();

                String gpuRenderer  = GL11.glGetString(GL_RENDERER);
                EntityPlayer entityPlayer = level.getPlayerEntity();

                EntityPlayer player = level.getPlayerEntity();
                String biome;

                int line = 0;

                if (levelType == Level.LevelType.HELL) {
                    biome = level.getGenerator().getBiome().getHellBiome((int)player.getX(), (int)player.getZ()).name();
                }
                else {
                    biome = level.getGenerator().getBiome().getWorldBiome((int)player.getX(), (int)player.getZ()).name();
                }

                font.drawShadow("OpenCraft " + OpenCraft.getVersion(), 2, 2, 0xFFFFFF);
                font.drawShadow("X Y Z: " + entityPlayer.getX() + " " +  + entityPlayer.getY() + " " +  + entityPlayer.getZ(), 2, 2 + (++line * 10), 0xFFFFFF);
                font.drawShadow("FPS: " + OpenCraft.getFPS() +
                                 ", Chunks updated: " + OpenCraft.getChunksUpdatedCount() +
                                 ", Chunks rendered: " + LevelRenderer.CHUNKS_RENDERED +
                                 ", Chunks amount: " + OpenCraft.getLevelRenderer().getChunksAmount(), 2, 2 + (++line * 10), 0xFFFFFF);
                font.drawShadow("Level: " + (levelType == Level.LevelType.WORLD ? "WORLD" : "HELL") +
                                 ", Seed: " + level.getSeed() +
                                 ", Biome: " + biome, 2, 2 + (++line * 10), 0xFFFFFF);

                ++line;
                font.drawShadow("GPU: " + gpuRenderer, 2, 2 + (++line * 10), 0xFFFFFF);
                font.drawShadow("Display: " + Display.getWidth() + "x" + Display.getHeight(), 2, 2 + (++line * 10), 0xFFFFFF);

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

                int finalScale = scale;
                new HashMap<>(OpenCraft.getGuiInterfaceMap()).forEach(((id, iGuiInterface) -> {
                    //GL11.glColor4f(1, 1, 1, 1);
                    if (iGuiInterface != null) iGuiInterface.render(screenWidth, screenHeight, finalScale);
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

    public static void blockOutline()
    {
        EntityPlayer entityPlayer = OpenCraft.getLevel().getPlayerEntity();

        RayCast.RayResult[] ray = RayCast.rayCastToBlock(6, entityPlayer.getRx(), entityPlayer.getRy(), entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ());
        if (ray[0].state)
        {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glColor3d(0, 0, 0);
            VerticesBuffer t = VerticesBuffer.instance;
            for(int i = 0; i < 6; ++i) {
                t.begin();
                BlockRenderer.renderFaceNoTexture(entityPlayer, t, ray[0].x, ray[0].y, ray[0].z, i);
                t.end();
            }

            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glColor3d(1, 1, 1);

            float value = OpenCraft.getPlayerController().getBlockBreakState();

            if (value > 0) {
                value++;
                if (value > 10) value = 10;

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());

                t.begin();
                BlockRenderer.renderBackSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderTopSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderLeftSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderRightSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderBottomSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                BlockRenderer.renderFrontSide(t, ray[0].x, ray[0].y, ray[0].z, new BlockDestroy((int) value-1));
                t.end();

                GL11.glEndList();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            }
        }
    }

    public static void transformCamera()
    {
        EntityPlayer player = OpenCraft.getLevel().getPlayerEntity();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(
                getFOV(),
                (float)OpenCraft.getWidth() / (float)OpenCraft.getHeight(),
                0.05F, (16 * OpenCraft.getRenderDistance()) * 2.0F
        );
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        float x = player.getXo() + (player.getX() - player.getXo()) * OpenCraft.getTimer().a;
        float y = player.getYo() + (player.getY() - player.getYo()) * OpenCraft.getTimer().a - 2 + player.getHeightOffset();
        float z = player.getZo() + (player.getZ() - player.getZo()) * OpenCraft.getTimer().a;

        GL11.glTranslatef(0.0F, 0.0F, -0.3F);
        GL11.glRotatef(player.getRx(), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(player.getRy(), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-x, -y, -z);

//        float tickTime = OpenCraft.getTimer().a;
//
//        float var3 = player.getDistanceWalked() - player.getPrevDistanceWalked();
//        float var4 = -(player.getDistanceWalked() + var3 * tickTime);
//        float var5 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * tickTime;
//        float var6 = player.prevCameraPitch + (player.cameraPitch - player.prevCameraPitch) * tickTime;
//        GL11.glTranslatef((float) (Math.sin(var4 * (float)Math.PI) * var5 * 0.5F), (float) -Math.abs(Math.cos(var4 * (float)Math.PI) * var5), 0.0F);
//        GL11.glRotatef((float) (Math.sin(var4 * (float)Math.PI) * var5 * 3.0F), 0.0F, 0.0F, 1.0F);
//        GL11.glRotatef((float) (Math.abs(Math.cos(var4 * (float)Math.PI - 0.2F) * var5) * 5.0F), 1.0F, 0.0F, 0.0F);
//        GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);

    }

    private static int getFOV() {
        return OpenCraft.getFOV();
    }

}
