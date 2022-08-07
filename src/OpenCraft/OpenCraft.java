package OpenCraft;

import OpenCraft.Interfaces.IGuiInterface;
import OpenCraft.Interfaces.IGuiTick;
import OpenCraft.Interfaces.ITick;
import OpenCraft.Rendering.*;
import OpenCraft.World.Entity.PlayerController;
import OpenCraft.World.Level;
import OpenCraft.World.RayCast;
import OpenCraft.World.TickTimer;
import OpenCraft.gui.Font;
import OpenCraft.gui.Screen;
import OpenCraft.gui.screens.MainMenu;
import OpenCraft.gui.screens.NewWorldConfigurator;
import OpenCraft.gui.screens.PauseMenu;
import OpenCraft.gui.screens.WorldList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.opengl.GL11.*;

public class OpenCraft
{

    // Game version
    private static final String version = "0.6.57iD";

    // Window
    private static int width = 868;
    private static int height = 568;
    private static boolean escapeClick;
    private static boolean close = false;

    // Player
    private static PlayerController player;

    // World
    private static boolean worldIsSaving = false;
    private static long worldSavingElapsed;
    private static ParticleEngine particleEngine; // Particle engine
    private static LevelRenderer levelRenderer; // World renderer
    private static Level overworld;
    private static Level nether;
    private static Level.LevelType currentLevel; // current loaded world
    private static boolean isWorldDestroyed; // Is world destroyed

    // Rendering and other game stuff
    private static HashMap<Integer, IGuiInterface> guiInterface = new HashMap<>();
    private static HashMap<Integer, IGuiTick> guiTicks = new HashMap<>();
    private static HashMap<Integer, ITick> ticks = new HashMap<>();
    private static ArrayList<Runnable> glContext = new ArrayList<>();
    private static int renderDistance = 6; // Render distance
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
    private static int guiScale = 3;

    public OpenCraft() throws Exception {
        /* Window initializing */
        Display.setDisplayMode(new DisplayMode(width, height)); // Set window size
        Display.setTitle("OpenCraft " + version); // Set window title
        Display.setResizable(true); // Set window resizable
        Display.create(); // Create window
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

        OpenCraft.font = new Font("resources/gui/font.gif");
        OpenCraft.timer = new TickTimer(20.0F);
        OpenCraft.isWorldDestroyed = true;

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

            updateGlContext();

            GL11.glClearColor(0.5f, 0.7f, 1, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glLoadIdentity();

            glPushMatrix();

            if (inMenu)
            {
                if (renderWhenInMenu && overworld != null) {
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
                else if (escapeClick && !isWorldDestroyed)
                {
                    escapeClick = false;
                    inMenu = true;
                    setCurrentScreen(pauseMenu);
                }

                for(int i = 0; i < timer.ticks; ++i) {
                    if (!isWorldDestroyed) {
                        new HashMap<>(ticks).forEach(((id, tick) -> {
                            if (tick != null) tick.tick();
                        }));
                    }
                }

                render();
                ++frames;
            }


            while(System.currentTimeMillis() >= lastTime + 10000L) {
                FPS = frames;
                chunksUpdate = LevelRenderer.CHUNK_UPDATES;
                LevelRenderer.CHUNK_UPDATES = 0;
                lastTime += 1000L;
                frames = 0;
            }

            Display.update();
        }

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
        OpenCraft.isWorldDestroyed = false;
        OpenCraft.escapeClick = false;

        OpenCraft.inMenu = true;
        OpenCraft.renderWhenInMenu = false;

        new Thread(() -> {
            ticks.clear();
            overworld.destroy();
            nether.destroy();
            levelRenderer.destroy();
            particleEngine.destroy();
            player.destroy();
            player = null;
            overworld = null;
            nether = null;
            levelRenderer = null;
            particleEngine = null;

            isWorldDestroyed = true;

            Thread.currentThread().interrupt();
        }).start();

        closeCurrentScreen();
        setCurrentScreen(mainMenu);

        // todo: fix screen flicking on quiting

        do
        {
            updateGlContext();
            mainMenu.setLoadingScreen("Quitting the world...");
            Display.update();
        }
        while (!isWorldDestroyed || !glContext.isEmpty());

        System.gc();

        OpenCraft.inMenu = true;
        OpenCraft.renderWhenInMenu = false;
        setCurrentScreen(mainMenu);
    }

    public static void startNewGame(boolean load)
    {
        closeCurrentScreen();

        OpenCraft.isWorldDestroyed = false;
        OpenCraft.currentLevel = Level.LevelType.OVERWORLD;

        player = new PlayerController(0, 70, 0);
        overworld = new Level(Level.LevelType.OVERWORLD, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        nether = new Level(Level.LevelType.NETHER, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        levelRenderer = new LevelRenderer();
        particleEngine = new ParticleEngine();
    }

    private void render()
    {
        try {
            if (isWorldDestroyed)
                return;

            if (!inMenu) {
                player.rotate();
                Controls.update();
                Mouse.setGrabbed(true);
            }

            GL11.glClear(16640);
            this.setPerspective();
            Frustum frustum = Frustum.getFrustum();
            levelRenderer.render(frustum);

            glEnable(GL_BLEND);
            particleEngine.render(player, timer.a, 0);
            glDisable(GL_BLEND);

            pick();
            GL11.glPopMatrix();
            GL11.glDisable(3042);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glDisable(2912);

            drawGui();
        } catch (Exception e) {
            System.out.println("Error occurred while rendering: " + e.getLocalizedMessage());
        }
    }

    private void drawGui()
    {
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

            if (!inMenu && !isWorldDestroyed)
            {
                String gpuRenderer  = GL11.glGetString(GL_RENDERER);

                int line = 0;

                font.drawShadow("OpenCraft " + version, 2, 2, 0xFFFFFF);
                font.drawShadow("X Y Z: " + player.getX() + " " +  + player.getY() + " " +  + player.getZ(), 2, 2 + (++line * 10), 0xFFFFFF);
                font.drawShadow("FPS: " + FPS + ", Chunks updated: " + chunksUpdate + ", Chunks rendered: " + LevelRenderer.CHUNK_RENDERED, 2, 2 + (++line * 10), 0xFFFFFF);
                font.drawShadow("Level: " + (currentLevel == Level.LevelType.OVERWORLD ? "OVERWORLD" : "NETHER") + ", Seed: " + getLevel().getSeed(), 2, 2 + (++line * 10), 0xFFFFFF);

                ++line;
                font.drawShadow("GPU: " + gpuRenderer, 2, 2 + (++line * 10), 0xFFFFFF);


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
        } catch (Exception e) {
            System.out.println("Error occurred while drawing gui: " + e.getLocalizedMessage());
        }
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
        float x = player.getXo() + (player.getX() - player.getXo()) * timer.a;
        float y = player.getYo() + (player.getY() - player.getYo()) * timer.a;
        float z = player.getZo() + (player.getZ() - player.getZo()) * timer.a;

        GL11.glTranslatef(-x, -y, -z);
    }

    private static void updateGlContext() {
        for (Runnable r: glContext) {
            r.run();
        }

        glContext.clear();
    }

    public static Level getLevel()
    {
        return currentLevel == Level.LevelType.OVERWORLD ? overworld : nether;
    }

    public static void switchWorld(Level.LevelType level) {
        if (OpenCraft.isWorldDestroyed)
            return;

        currentLevel = level;

        new Thread(() -> {
            levelRenderer.switchWorld();

            OpenCraft.isWorldDestroyed = true;
            Thread.currentThread().interrupt();
        }).start();

        do
        {
            mainMenu.setLoadingScreen("Loading the world...");
            Display.update();
            updateGlContext();
        }
        while (!isWorldDestroyed || !glContext.isEmpty());

        System.gc();

        OpenCraft.isWorldDestroyed = false;
    }

    public static Level.LevelType getLevelType() {
        return currentLevel;
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

    public static PlayerController getPlayer()
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

    public static void runInGLContext(Runnable func)
    {
        glContext.add(func);
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
