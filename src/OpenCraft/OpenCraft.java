package OpenCraft;

import OpenCraft.World.Entity.Entity;
import OpenCraft.World.Entity.Zombie;
import OpenCraft.gui.Font;
import OpenCraft.gui.Screen;
import OpenCraft.gui.screens.MainMenu;
import OpenCraft.gui.screens.NewWorldConfigurator;
import OpenCraft.gui.screens.PauseMenu;
import OpenCraft.gui.screens.WorldList;
import OpenCraft.Rendering.Frustum;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.Rendering.ParticleEngine;
import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.LevelRenderer;
import OpenCraft.Interfaces.IGuiInterface;
import OpenCraft.Interfaces.IGuiTick;
import OpenCraft.Interfaces.ITick;
import OpenCraft.World.Entity.Player;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class OpenCraft
{

    // Game version
    private static final String version = "0.3.5i";

    // Window
    private static int width = 868;
    private static int height = 468;
    private static boolean escapeClick;
    private static boolean close = false;

    // Player
    private static Player player;

    // World
    private static boolean worldIsSaving = false;
    private static long worldSavingElapsed;
    private static ParticleEngine particleEngine; // Particle engine
    private static LevelRenderer levelRenderer; // World
    private static LevelSaver levelSaver; // World
    private static Level level; // World

    // Rendering and other game stuff
    private static HashMap<Integer, IGuiInterface> guiInterface = new HashMap<>();
    private static HashMap<Integer, IGuiTick> guiTicks = new HashMap<>();
    private static HashMap<Integer, ITick> ticks = new HashMap<>();
    private static int renderDistance = 8; // Render distance
    private static int blockCastList = -1;
    private static boolean inMenu = true;
    private static long chunksUpdateTime = -1;
    private static boolean renderWhenInMenu = false;
    private static int chunksUpdate = 0;
    private static int FOV = 90; // Camera FOV
    private static int FPS = 0;
    private static TickTimer timer;
    private static Screen currentScreen;


    // Gui
    private static NewWorldConfigurator newWorldConfigurator;
    private static PauseMenu pauseMenu;
    private static WorldList worldList;
    private static MainMenu mainMenu;
    private static Font font;
    private static int guiScale = 2;

    public OpenCraft() throws Exception {
        /* Window initializing */
        Display.setDisplayMode(new DisplayMode(width, height)); // Set window size
        Display.setTitle("OpenCraft " + version); // Set window title
        Display.setResizable(true); // Set window resizable
        Display.create(); // Create win// dow
        /**/


        /* Draw loading screen */
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        /**/

        /* OpenGL initializing */
        GL11.glEnable(3553);
        GL11.glShadeModel(7425);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
        GL11.glCullFace(1029);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        /**/

        Controls.init();
        TextureEngine.init();

        font = new Font("resources/gui/font.gif");
        timer = new TickTimer(20.0F);

        initScreens();
        setCurrentScreen(mainMenu);

        long lastTime = System.currentTimeMillis();
        int frames = 0;

        while (!Display.isCloseRequested() && !close) {

            if (Display.wasResized()) // Was window resized?
            {
                GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
                width = Display.getWidth(); // Set new width
                height = Display.getHeight(); // Set new height

                if (currentScreen != null) currentScreen.resize(width, height);
            }

            timer.advanceTime();

            GL11.glClearColor(0.5f, 0.7f, 1, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glLoadIdentity();

            glPushMatrix();

            if (inMenu)
            {
                if (renderWhenInMenu && level != null) {
                    render();
                }
                GL11.glColor4f(1F, 1F, 1F, 1.0F);

                drawGui();
                Controls.update();
                Mouse.setGrabbed(false);
            }
            else {
                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
                {
                    escapeClick = true;
                }
                else if (escapeClick)
                {
                    //renderWhenInMenu = true;
                    escapeClick = false;
                    inMenu = true;
                    setCurrentScreen(pauseMenu);
                }

                for(int i = 0; i < timer.ticks; ++i) {
                    new HashMap<>(ticks).forEach(((id, tick) -> {
                        if (tick != null) tick.tick();
                    }));
                }

                render();
                ++frames;
            }


            while(System.currentTimeMillis() >= lastTime + 1000L) {
                FPS = frames;
                chunksUpdate = Chunk.CHUNK_UPDATES;
                Chunk.CHUNK_UPDATES = 0;
                lastTime += 1000L;
                frames = 0;
            }

            Display.update();
        }

        if (levelSaver != null) levelSaver.destroy();
        Controls.destroy();
        Display.destroy();
        System.exit(0);
    }

    public static void initScreens()
    {
        try {
            mainMenu = new MainMenu();
        } catch (IOException e) { }
        worldList = new WorldList();
        pauseMenu = new PauseMenu();
        newWorldConfigurator = new NewWorldConfigurator();
    }

    public static void quitToMainMenu()
    {
        closeCurrentScreen();
        setCurrentScreen(mainMenu);
        mainMenu.setLoadingScreen("Quitting the world...");
        for (int i = 0; i < 4; i++)
            Display.update();

        inMenu = true;
        renderWhenInMenu = false;
        levelSaver.save();

        level.destroy();
        levelRenderer.destroy();
        player = null;
        level = null;
        levelSaver = null;
        levelRenderer = null;
        particleEngine = null;

        System.gc();
    }

    public static void startNewGame(boolean load)
    {
        closeCurrentScreen();

        player = new Player(128, 70, 128);
        level = new Level();
        levelSaver = new LevelSaver(worldList.levelName, 525874385, load);
        levelRenderer = new LevelRenderer();
        particleEngine = new ParticleEngine();
    }

    private void render()
    {
        if (!inMenu)
        {
            player.rotate();
            Controls.update();
            Mouse.setGrabbed(true);
        }

        ArrayList<Entity> entities = level.getEntities();

        GL11.glClear(16640);
        this.setPerspective();
        Frustum frustum = Frustum.getFrustum();
        levelRenderer.cull(frustum);
        if (Chunk.CHUNK_UPDATES < 100) levelRenderer.updateDirtyChunks(player);
        fog();
        levelRenderer.render(player, 0);

        for (int i = 0; i < entities.size(); i++)
        {
            Entity entity = entities.get(i);
            if (frustum.isVisible(entity.aabb))
            {
                entity.render();
            }
        }

        glEnable(GL_BLEND);
        particleEngine.render(player, timer.a, 0);
        glDisable(GL_BLEND);
        fog();
        levelRenderer.render(player, 1);

        for (int i = 0; i < entities.size(); i++)
        {
            Entity entity = entities.get(i);
            if (frustum.isVisible(entity.aabb))
            {
                entity.render();
            }
        }

        fog();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        particleEngine.render(player, timer.a, 1);
        GL11.glColorMask(false, false, false, false);
        levelRenderer.render(player, 2);
        GL11.glColorMask(true, true, true, true);
        levelRenderer.render(player, 2);

        if (chunksUpdateTime + 1500 < System.currentTimeMillis())
        {
            chunksUpdateTime = System.currentTimeMillis();
            new Thread(() -> {
                levelRenderer.updateChunks();
                Thread.currentThread().interrupt();
            }).start();
        }
        levelRenderer.checkCopyState();

        pick();
        GL11.glPopMatrix();
        GL11.glDisable(3042);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glDisable(2912);

        drawGui();

    }

    private void drawGui()
    {
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
        if (currentScreen != null)
        {
            currentScreen.render(screenWidth, screenHeight, scale);
        }

        if (inMenu)
        {
            for(int i = 0; i < timer.ticks; ++i) {
                int finalScale = scale;
                new HashMap<>(guiTicks).forEach(((id, tick) -> {
                    if (tick != null) tick.tick(screenWidth, screenHeight, finalScale);
                    GL11.glTranslatef(0.0F, 0.0F, -200.0F);
                }));
            }
        }

        if (!inMenu)
        {
            font.drawShadow("OpenCraft " + version, 2, 2, 0xFFFFFF);
            font.drawShadow("FPS: " + FPS + ", Chunks updated: " + chunksUpdate + ", X Y Z: " + player.getX() + " " +  + player.getY() + " " +  + player.getZ(), 2, 12, 0xFFFFFF);

            if (worldIsSaving)
            {
                if (worldSavingElapsed + 2000 < System.currentTimeMillis())
                {
                    worldIsSaving = false;
                }

                font.drawShadow("Saving world...", screenWidth - font.getTextWidth("Saving world...") - 20, screenHeight - 20, 0xFDFDFD);
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

            int finalScale = scale;
            new HashMap<>(guiInterface).forEach(((id, iGuiInterface) -> {
                //GL11.glColor4f(1, 1, 1, 1);
                if (iGuiInterface != null) iGuiInterface.render(screenWidth, screenHeight, finalScale);
                GL11.glTranslatef(0.0F, 0.0F, 0.0F);
            }));

            if (player.getCurrentBlock() != null)
            {
                //BlockRenderer.renderBlockIcon(t, 20, 0, 128, 128, screenWidth - 10, screenHeight + 78, player.getCurrentBlock());
            }
        }

        GL11.glDisable(3042);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glDisable(2912);
    }

    private void fog()
    {
        FloatBuffer b = BufferUtils.createFloatBuffer(4);

        //if (player.headInWater())
        //{
        //    b.put(0.16f).put(0.35f).put(0.92f).put(1).flip();
        //    GL11.glEnable(2912);
        //    glFog(GL_FOG_COLOR, b);
        //    glFogf(GL_FOG_START, 10);
        //    glFogf(GL_FOG_END, 35);
        //}
        //else {
        //    b.put(0.5f).put(0.7f).put(1).put(1).flip();
        //    glFog(GL_FOG_COLOR, b);
        //    glFogf(GL_FOG_START, 10);
        //    glFogf(GL_FOG_END, 80);
        //}
    }

    private void pick()
    {
        float[][] ray = RayCast.rayCastToBlock(6, player.getRx(), player.getRy(), player.getX(), player.getY(), player.getZ());
        if (ray[0][0] == 1)
        {
            if (blockCastList != -1)
            {
                GL11.glDeleteLists(blockCastList, 1);
            }
            blockCastList = GL11.glGenLists(1);
            GL11.glNewList(blockCastList, GL_COMPILE);
            VerticesBuffer t = VerticesBuffer.instance;
            for(int i = 0; i < 6; ++i) {
                t.begin();
                BlockRenderer.renderFaceNoTexture(player, t, (int)ray[1][0], (int)ray[1][1], (int)ray[1][2], i);
                t.end();
            }
            GL11.glEndList();

            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glColor3d(0, 0, 0);
            GL11.glCallList(blockCastList);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glColor3d(1, 1, 1);
        }
    }

    private void setPerspective()
    {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GLU.gluPerspective(FOV, (float)width / (float)height, 0.05F, 1000.0F);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();

        GL11.glTranslatef(0.0F, 0.0F, -0.3F);
        GL11.glRotatef(player.getRx(), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(player.getRy(), 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef(-(player.getRx()), 1.0f, 0.0f, 0.0f);
        //GL11.glRotatef(player.getRy(), 0.0f, 1.0f, 0.0f);
        float x = player.getXo() + (player.getX() - player.getXo()) * timer.a;
        float y = player.getYo() + (player.getY() - player.getYo()) * timer.a;
        float z = player.getZo() + (player.getZ() - player.getZo()) * timer.a;

        //double dst = 2 - ((player.getRx() / 180));
        //System.out.println(dst);

        //double camerax = 2 * Math.cos((player.getRy() + 270.0f) * 3.14159265358979323846264338327950288f / 180) + player.getX();
        //double cameray = dst * Math.cos((-player.getRx() + 270.0f) * 3.14159265358979323846264338327950288f / 180) + player.getY();
        //double cameraz = 2 * Math.sin((player.getRy() - 270.0f) * 3.14159265358979323846264338327950288f / 180) + player.getZ();

        //GL11.glTranslated(-camerax, -y, -cameraz);

        GL11.glTranslatef(-x, -y, -z);
    }

    public static Level getLevel()
    {
        return level;
    }

    public static int getRenderDistance() {
        return renderDistance;
    }

    public static int getWidth()
    {
        return OpenCraft.width;
    }

    public static int getHeight()
    {
        return OpenCraft.height;
    }

    public static int getFOV()
    {
        return OpenCraft.FOV;
    }

    public static Player getPlayer()
    {
        return OpenCraft.player;
    }

    public static String getVersion()
    {
        return OpenCraft.version;
    }

    public static TickTimer getTimer()
    {
        return timer;
    }

    public static ParticleEngine getParticleEngine()
    {
        return particleEngine;
    }

    public static LevelSaver getLevelSaver() {
        return levelSaver;
    }

    public static Font getFont()
    {
        return font;
    }

    public static int getGuiScale() {
        return guiScale;
    }

    public static LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public static WorldList getWorldListScreen()
    {
        return worldList;
    }

    public static MainMenu getMainMenuScreen()
    {
        return mainMenu;
    }

    public static NewWorldConfigurator getNewWorldConfigurator()
    {
        return newWorldConfigurator;
    }

    public static void registerTickEvent(ITick tick)
    {
        ticks.put(ticks.size(), tick);
    }

    public static void registerGuiTickEvent(IGuiTick tick)
    {
        guiTicks.put(guiTicks.size(), tick);
    }

    public static void registerGuiInterfaceEvent(IGuiInterface iGuiInterface)
    {
        guiInterface.put(guiInterface.size(), iGuiInterface);
    }

    public static int getScreenScaledWidth() {
        int scale = 240;
        if (guiScale == 1) scale = 540;
        if (guiScale == 2) scale = 440;
        if (guiScale == 3) scale = 340;
        return width * scale / height;
    }

    public static int getScreenScaledHeight() {
        int scale = 240;
        if (guiScale == 1) scale = 540;
        if (guiScale == 2) scale = 440;
        if (guiScale == 3) scale = 340;

        return height * scale / height;
    }

    public static void closeCurrentScreen()
    {
        guiTicks = new HashMap<>();
        setCurrentScreen(null);
        inMenu = false;
        renderWhenInMenu = false;
    }

    public static void setWorldSavingState(boolean b) {
        worldIsSaving = b;
        worldSavingElapsed = System.currentTimeMillis();
    }

    public static void setCurrentScreen(Screen scr)
    {
        if (scr != null)
        {
            int scale = 240;
            if (guiScale == 1) scale = 540;
            if (guiScale == 2) scale = 440;
            if (guiScale == 3) scale = 340;

            scr.init();
        }
        currentScreen = scr;
    }

    public static Screen getCurrentScreen()
    {
        return currentScreen;
    }

    public static void close() {
        close = true;
    }

}
