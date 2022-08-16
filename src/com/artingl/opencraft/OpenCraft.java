package com.artingl.opencraft;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Rendering.*;
import com.artingl.opencraft.Resources.Lang;
import com.artingl.opencraft.Resources.Resources;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.ITick;
import com.artingl.opencraft.World.Level.Level;
import com.artingl.opencraft.World.Level.LevelSaver;
import com.artingl.opencraft.World.LevelListener;
import com.artingl.opencraft.World.PlayerController;
import com.artingl.opencraft.World.TickTimer;
import com.artingl.opencraft.gui.Font;
import com.artingl.opencraft.gui.IGuiInterface;
import com.artingl.opencraft.gui.IGuiTick;
import com.artingl.opencraft.gui.Screen;
import com.artingl.opencraft.gui.screens.*;
import com.artingl.opencraft.math.Vector3f;
import com.artingl.opencraft.modapi.ModEntry;
import com.artingl.opencraft.utils.JavaUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class OpenCraft
{

    private static String gameDir = "";

    // Game version
    private static final String version = "0.8.0";

    // Window
    private static int width = 868;
    private static int height = 568;
    private static boolean escapeClick;
    private static boolean close = false;

    // Player
    private static PlayerController playerController;

    private static ParticleEngine particleEngine; // Particle engine
    private static LevelRenderer levelRenderer; // World renderer
    private static Level world;
    private static Level hell;
    private static LevelSaver levelSaver;
    private static Level.LevelType currentLevel; // current loaded world
    private static boolean isWorldDestroyed; // Is world destroyed

    // Rendering and other game stuff
    private static final HashMap<Integer, IGuiInterface> guiInterface = new HashMap<>();
    private static HashMap<Integer, IGuiTick> guiTicks = new HashMap<>();
    private static final HashMap<Integer, ITick> ticks = new HashMap<>();
    private static final HashMap<Integer, IRenderHandler> glUpdateEvent = new HashMap<>();
    private static final HashMap<Integer, LevelListener> levelListeners = new HashMap<>();
    private static final ArrayList<Runnable> glContext = new ArrayList<>();
    private static final ArrayList<ModEntry> modsList = new ArrayList<>();
    private static final int renderDistance = 3; // Render distance
    private static boolean inMenu = true;
    private static boolean renderWhenInMenu = false;
    private static int chunksUpdate = 0;
    private static final int FOV = 90; // Camera FOV
    private static int FPS = 0;
    private static TickTimer timer;
    private static Screen currentScreen;

    // Gui
    private static NewWorldConfiguratorScreen newWorldConfigurator;
    private static PauseMenuScreen pauseMenu;
    private static WorldListScreen worldList;
    private static MainMenuScreen mainMenu;
    private static Font font;
    private static int guiScale = 3;

    public OpenCraft(String[] args) throws Exception {
        Logger.init();
        Logger.info("Hello from OpenCraft");

        OpenCraft.parseArguments(args);
        JavaUtils.modifyLibraryPath(getGameDirectory() + "natives");

        String[] requiredDirectories = {
                "saves",
                "mods",
                "logs",
        };

        for (String dir: requiredDirectories) {
            File f = new File(gameDir + dir);
            if (!f.isDirectory()) {
                Logger.info("Creating " + dir + " directory");
                f.mkdir();
            }
        }

        Logger.setupOutputFile();

        /* Window initializing */
        Display.setDisplayMode(new DisplayMode(width, height)); // Set window size
        Display.setTitle("com/artingl/opencraft " + version); // Set window title
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

        Logger.info("Loading game resources");

        // load game resources
        Lang.init();
        Controls.init();
        TextureEngine.init();

        Resources.loadNamespaceResources(OpenCraft.class, "opencraft");

        Logger.info("Initializing font and timer");

        OpenCraft.font = new Font("opencraft:gui/font.gif");
        OpenCraft.timer = new TickTimer(20.0F);
        OpenCraft.isWorldDestroyed = true;

        // load all mods from mods directory
        Logger.info("Loading mods");

        File folder = new File(gameDir + "mods");
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles) {
            String name = listOfFile.getName();
            if (listOfFile.isFile() && name.endsWith(".jar")) {
                JavaUtils.loadJar(gameDir + "mods" + File.separator + name);
            }
        }

        // initialize all loaded mods
        for (ModEntry mod: modsList) {
            try {
                mod.onModInitialization();
            } catch (Exception e) {
                Logger.exception("Error while loading mod", e);
            }
        }

        Logger.info("Loading screens");
        initScreens();
        setCurrentScreen(mainMenu);

        long lastTime = System.currentTimeMillis();
        int frames = 0;

        Logger.info("Initializing complete");

        while (!Display.isCloseRequested() && !close) {

            if (Display.wasResized())
            {
                GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
                width = Display.getWidth(); // Set new width
                height = Display.getHeight(); // Set new height

                if (currentScreen != null) currentScreen.resize(width, height);
            }

            timer.advanceTime();

            Vector3f sky = new Vector3f(0, 0, 0);

            if (getLevel() != null) {
                sky = getLevel().getSkyColor();
            }

            GL11.glClearColor(sky.x, sky.y, sky.z, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glLoadIdentity();

            glPushMatrix();

            if (inMenu)
            {
                if (renderWhenInMenu && world != null) {
                    GameRenderer.render();
                }
                GL11.glColor4f(1F, 1F, 1F, 1.0F);

                GameRenderer.drawGui();
                Controls.update();
                Mouse.setGrabbed(false);
            }
            else {
                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
                {
                    escapeClick = true;
                }
                else if (escapeClick && !isWorldDestroyed && !getLevel().getPlayerEntity().isDead())
                {
                    escapeClick = false;
                    inMenu = true;
                    setCurrentScreen(pauseMenu);
                }

                for(int i = 0; i < timer.ticks; ++i) {
                    if (!isWorldDestroyed) {
                        new HashMap<>(ticks).forEach(((id, tick) -> {
                            try {
                                if (tick != null) tick.tick();
                            } catch (Exception e) {
                                Logger.exception("Error occurred while running tick function", e);
                            }
                        }));
                    }
                }

                GameRenderer.render();
            }

            if (currentScreen != null) {
                if (currentScreen.equals(pauseMenu) || currentScreen.equals(new DeathScreen())) {
                    GameRenderer.render();
                }
            }

            while(System.currentTimeMillis() >= lastTime + 1000L) {
                FPS = frames;
                chunksUpdate = LevelRenderer.CHUNK_UPDATES;
                LevelRenderer.CHUNK_UPDATES = 0;
                lastTime += 1000L;
                frames = 0;
            }

            Display.update();
            ++frames;
        }

        Controls.destroy();
        Display.destroy();
        System.exit(0);
    }

    public static void parseArguments(String[] args) {
        String prefix = "";

        for (String arg: args) {
            if (prefix.isEmpty()) {
                prefix = arg;
            }
            else {
                if (prefix.equals("--game-dir")) {
                    gameDir = arg;
                }

                prefix = "";
            }
        }
    }

    public static void initScreens() throws IOException {
        mainMenu = new MainMenuScreen();
        worldList = new WorldListScreen();
        pauseMenu = new PauseMenuScreen();
        newWorldConfigurator = new NewWorldConfiguratorScreen();
    }

    public static void quitToMainMenu()
    {
        OpenCraft.isWorldDestroyed = false;
        OpenCraft.escapeClick = false;

        OpenCraft.inMenu = true;
        OpenCraft.renderWhenInMenu = false;

        new Thread(() -> {
            levelSaver.destroy();
            ticks.clear();
            world.destroy();
            hell.destroy();
            levelRenderer.destroy();
            particleEngine.destroy();
            playerController.destroy();
            playerController = null;
            world = null;
            hell = null;
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

    public static void startNewGame(boolean load, int seed)
    {
        closeCurrentScreen();

        OpenCraft.isWorldDestroyed = false;
        OpenCraft.currentLevel = Level.LevelType.WORLD;

        levelRenderer = new LevelRenderer();
        world = new Level(Level.LevelType.WORLD, seed);
        hell = new Level(Level.LevelType.HELL, seed+1);
        playerController = new PlayerController();
        particleEngine = new ParticleEngine();
        levelSaver = new LevelSaver(OpenCraft.getWorldListScreen().levelName);
        if (!load) levelSaver.create();
        else       levelSaver.load();

        EntityPlayer entityPlayer = new EntityPlayer(playerController);

        world.setPlayerEntity(entityPlayer);
        world.getGenerator().setPlayerSpawnPoint();

        levelRenderer.prepareChunks();
    }

    public static int getFPS() {
        return FPS;
    }

    public static boolean isInMenu() {
        return inMenu;
    }

    public static void updateGlContext() {
        for (Runnable r: new ArrayList<>(glContext)) {
            r.run();
        }

        glContext.clear();
    }

    public static Level getLevel()
    {
        return currentLevel == Level.LevelType.WORLD ? world : hell;
    }

    public static void switchWorld(Level.LevelType level) {
        if (OpenCraft.isWorldDestroyed)
            return;

        Level oldLevel = getLevel();
        currentLevel = level;

        new Thread(() -> {
            oldLevel.movePlayerEntity(getLevel());
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

    public static PlayerController getPlayerController()
    {
        return OpenCraft.playerController;
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

    public static WorldListScreen getWorldListScreen()
    {
        return worldList;
    }

    public static MainMenuScreen getMainMenuScreen()
    {
        return mainMenu;
    }

    public static NewWorldConfiguratorScreen getNewWorldConfigurator()
    {
        return newWorldConfigurator;
    }

    public static int registerRenderEvent(IRenderHandler tick)
    {
        glUpdateEvent.put(glUpdateEvent.size(), tick);
        return glUpdateEvent.size()-1;
    }

    public static void unregisterRenderEvent(int index)
    {
        glUpdateEvent.remove(index);
    }

    public static int registerTickEvent(ITick tick)
    {
        ticks.put(ticks.size(), tick);
        return ticks.size()-1;
    }

    public static void unregisterTickEvent(int index)
    {
        ticks.remove(index);
    }

    public static int registerGuiTickEvent(IGuiTick tick)
    {
        guiTicks.put(guiTicks.size(), tick);
        return guiTicks.size()-1;
    }

    public static void unregisterGuiTickEvent(int index)
    {
        guiTicks.remove(index);
    }

    public static int registerGuiInterfaceEvent(IGuiInterface iGuiInterface)
    {
        guiInterface.put(guiInterface.size(), iGuiInterface);
        return guiInterface.size()-1;
    }

    public static void unregisterGuiInterfaceEvent(int index) {
        guiInterface.remove(index);
    }

    public static int registerLevelListener(LevelListener listener)
    {
        levelListeners.put(levelListeners.size(), listener);
        return levelListeners.size()-1;
    }

    public static void unregisterLevelListener(int index)
    {
        levelListeners.remove(index);
    }

    public static HashMap<Integer, LevelListener> getLevelListeners() {
        return levelListeners;
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
        if (currentScreen != null) {
            currentScreen.isOpened(false);
        }
        guiTicks = new HashMap<>();
        setCurrentScreen(null);
        inMenu = false;
        renderWhenInMenu = false;
    }

    public static void setWorldSavingState(boolean b) {
        // World
        long worldSavingElapsed = System.currentTimeMillis();
    }

    public static void setCurrentScreen(Screen scr)
    {
        if (currentScreen != null) {
            currentScreen.isOpened(false);
        }

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

    public static LevelSaver getLevelSaver() {
        return levelSaver;
    }

    public static Screen getCurrentScreen()
    {
        return currentScreen;
    }

    public static void close() {
        close = true;
    }

    public static void changeMenuStatus(boolean b) {
        inMenu = b;
    }

    public static boolean showHitBoxes() {
        // todo
        return false;
    }

    public static long getSystemTime()
    {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    public static HashMap<Integer, IGuiInterface> getGuiInterfaceMap() {
        return guiInterface;
    }

    public static HashMap<Integer, ITick> getTicksInterfaceMap() {
        return ticks;
    }

    public static HashMap<Integer, IGuiTick> getGuiTickInterfaceMap() {
        return guiTicks;
    }

    public static boolean isWorldDestroyed() {
        return OpenCraft.isWorldDestroyed;
    }

    public static int getChunksUpdatedCount() {
        return chunksUpdate;
    }

    public static HashMap<Integer, IRenderHandler> getRenderersInterfaceMap() {
        return glUpdateEvent;
    }

    public static void registerMod(ModEntry modEntry) {
        modsList.add(modEntry);
    }

    public static String getJarLocation() throws URISyntaxException {
        return getJarLocation(OpenCraft.class);
    }

    public static String getJarLocation(Class<?> c) throws URISyntaxException {
        return c.getProtectionDomain().getCodeSource().getLocation()
                .toURI().getPath();
    }

    public static String getGameDirectory() {
        return gameDir;
    }
}
