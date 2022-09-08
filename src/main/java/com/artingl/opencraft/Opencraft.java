package com.artingl.opencraft;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GL.Display;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Multiplayer.Client;
import com.artingl.opencraft.Multiplayer.InternalServer;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Rendering.Game.GameRenderer;
import com.artingl.opencraft.Rendering.Game.TextureEngine;
import com.artingl.opencraft.Rendering.World.LevelRenderer;
import com.artingl.opencraft.Rendering.World.ParticleEngine;
import com.artingl.opencraft.Resources.Options.OptionsListener;
import com.artingl.opencraft.World.Level.LevelType;
import com.artingl.opencraft.Rendering.*;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.Resources.Resources;
import com.artingl.opencraft.Resources.Options.OptionsRegistry;
import com.artingl.opencraft.Sound.SoundEngine;
import com.artingl.opencraft.Utils.Random;
import com.artingl.opencraft.Utils.ThreadsManager;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.EntityData.Nametag;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.Level.LevelListener;
import com.artingl.opencraft.World.PlayerController;
import com.artingl.opencraft.World.TickTimer;
import com.artingl.opencraft.GUI.Font.Font;
import com.artingl.opencraft.GUI.GuiInterface;
import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.ModAPI.ModEntry;
import com.artingl.opencraft.Utils.JavaUtils;
import jdk.jfr.SettingControl;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.*;

public class Opencraft
{

    private static String gameDir = "";
    private static String version = "Dev";
    private static final int width = 868;
    private static final int height = 568;
    private static Display display;
    private static boolean close = false;
    private static PlayerController playerController;
    private static ParticleEngine particleEngine;
    private static LevelRenderer levelRenderer;
    private static ClientLevel level;
    private static LevelType currentLevel;
    private static boolean isWorldLoaded;
    private static final HashMap<Integer, GuiInterface> guiInterface = new HashMap<>();
    private static final HashMap<Integer, Tick> ticks = new HashMap<>();
    private static final HashMap<Integer, RenderHandler> glUpdateEvent = new HashMap<>();
    private static final HashMap<Integer, LevelListener> levelListeners = new HashMap<>();
    private static final HashMap<Integer, OptionsListener> optionsListeners = new HashMap<>();
    private static final ConcurrentLinkedQueue<Runnable> glContext = new ConcurrentLinkedQueue<>();
    private static final ArrayList<ModEntry> modsList = new ArrayList<>();
    private static boolean inMenu = true;
    private static boolean renderWhenInMenu = false;
    private static int chunksUpdate = 0;
    private static int FPS = 0;
    private static TickTimer timer;
    private static Screen currentScreen;
    private static Font font;
    private static Screen playerScreen;
    private static String playerName;
    private static SoundEngine soundEngine;
    private static InternalServer internalServer;
    private static Client internalServerConnection;
    private static ThreadsManager threadsManager;
    private static EntityPlayer entityPlayer;
    private static long lastTime;
    private static int frames;

    public Opencraft(String[] args) throws Exception {
        String ver = getClass().getPackage().getImplementationVersion();
        if (ver != null) {
            version = ver;
        }

        Logger.init();
        Logger.info("Hello world");

        Opencraft.parseArguments(args);
//        JavaUtils.modifyLibraryPath(getGameDirectory() + "natives");

        String[] requiredDirectories = {
                "saves",
                "mods",
                "logs",
        };

        for (String dir: requiredDirectories) {
            File f = new File(gameDir + dir);
            if (!f.isDirectory()) {
                Logger.debug("Creating " + dir + " directory");
                f.mkdir();
            }
        }

        Logger.setupOutputFile();

        Logger.info("Welcome to OpenCraft " + version + "!");
        Logger.debug("Loading game options");
        OptionsRegistry.init();
        OptionsRegistry.loadOptions();

        Logger.debug("Creating display");

        display = new Display(width, height, "OpenCraft " + version);
        display.create();

        GL.createCapabilities();

        Logger.debug("Initializing OpenGL");
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glShadeModel(GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL_DEPTH_TEST);
//        GL11.glEnable(GL_CULL_FACE);
        GL11.glDepthFunc(GL_LEQUAL);
        GL11.glEnable(GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL_GREATER, 0.0F);
        GL11.glCullFace(GL_BACK);
        GL11.glMatrixMode(GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL_MODELVIEW);

        Logger.debug("Initializing font and timer");
        Opencraft.font = new Font("opencraft:gui/font.gif");
        Opencraft.timer = new TickTimer(20.0F);
        Opencraft.isWorldLoaded = false;

        Logger.debug("Creating loading screen");
        Opencraft.drawLoadingScreen("Loading game");

        Logger.debug("Loading game resources");

        // load game resources
        Lang.init();
        Controls.init();
        TextureEngine.init();
        BlockRegistry.registerAllBlocks();

        Resources.loadNamespaceResources(Opencraft.class, "opencraft");

        // load all mods from mods directory
        Logger.debug("Loading mods");

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

                if (!mod.isInitialized()) {
                    throw new Exception("Mod '" + mod.getModId() + "' was not initialized! You should call ModEntry::initialize at the beginning of ModEntry::onModInitialization function!");
                }

                Resources.loadNamespaceResources(mod.getClass(), mod.getModId());
            } catch (Exception e) {
                Logger.exception("Error while loading mod", e);
            }
        }

        Logger.debug("Loading screens");
        GUI.initScreens();
        setCurrentScreen(GUI.mainMenu);

        lastTime = System.currentTimeMillis();
        frames = 0;

        Logger.debug("Initializing server and client class");
        internalServer = new InternalServer();
        internalServerConnection = new Client(internalServer.getHost(), internalServer.getPort());

        Logger.debug("Initializing sound engine");
        soundEngine = new SoundEngine();

        Logger.debug("Loading threads manager");
        threadsManager = new ThreadsManager();

        Logger.debug("Initializing complete");

        while (!display.isClosed() && !close) {
            display.createFrame();

            if (display.isResized())
            {
                GL11.glViewport(0, 0, display.getWidth(), display.getHeight());
                if (currentScreen != null) currentScreen.resize(display.getWidth(), display.getHeight());
            }

            Opencraft.renderEverything();
        }

        Logger.debug("Preparing to exit");

        Controls.destroy();
        display.destroy();
        System.exit(0);
    }

    public static void drawLoadingScreen(String stage) {
        display.createFrame();

        if (display.isResized())
            GL11.glViewport(0, 0, display.getWidth(), display.getHeight());

        int screenWidth = display.getWidth() * 220 / display.getHeight();
        int screenHeight = display.getHeight() * 220 / display.getHeight();

        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        int loading_texture = TextureEngine.load("opencraft:title/loading_screen.png", GL_LINEAR);

        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)screenWidth, (double)screenHeight, 0.0D, 100.0D, 300.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);

        GL11.glEnable(3553);
        GL11.glBindTexture(3553, loading_texture);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f((float)screenWidth, (float)0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f((float)0, (float)0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f((float)0, (float)screenHeight);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f((float)screenWidth, (float)screenHeight);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glDisable(3553);

        Opencraft.getFont().draw(stage, (screenWidth - Opencraft.getFont().getTextWidth(stage)) / 2, screenHeight / 2 + 10, 0x333333);

        display.swapBuffers();
    }

    public static void renderEverything() {
        timer.advanceTime();
        Controls.update();

        Vector3f sky = new Vector3f(0, 0, 0);

        if (getLevel() != null) {
            sky = getLevel().getSkyColor();
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();

        GL11.glClearColor(sky.x, sky.y, sky.z, 1);

        if (!inMenu && OptionsRegistry.Values.getBooleanOption("enableFog")) {
            glEnable(GL_FOG);
            glFogi(GL_FOG_MODE, GL_LINEAR);
            glFogfv(GL_FOG_COLOR, new float[]{sky.x, sky.y, sky.z});
            glFogf(GL_FOG_DENSITY, 0.25f);
            glHint(GL_FOG_HINT, GL_NICEST);
            glFogf(GL_FOG_START, (16 * Opencraft.getRenderDistance()) - 32);
            glFogf(GL_FOG_END, (16 * Opencraft.getRenderDistance()) - 16);
        }

        glPushMatrix();

        if (inMenu)
        {
            if (renderWhenInMenu && level != null) {
                GameRenderer.render();
            }
            GL11.glColor4f(1F, 1F, 1F, 1.0F);

            GameRenderer.drawGui();
            Controls.setMouseGrabbed(false);
        }
        else {
            for(int i = 0; i < timer.ticks; ++i) {
                if (isWorldLoaded) {
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
//                if (currentScreen.doGameRenderingInBackground()) {
//                    GameRenderer.render();
//                }
        }

        while(System.currentTimeMillis() >= lastTime + 1000L) {
            FPS = frames;
            chunksUpdate = LevelRenderer.CHUNK_UPDATES;
            LevelRenderer.CHUNK_UPDATES = 0;
            lastTime += 1000L;
            frames = 0;
        }

        display.swapBuffers();
        ++frames;
    }

    public static void parseArguments(String[] args) {
        String prefix = "";

        gameDir = System.getenv("APPDATA") + File.separator + ".opencraft" + File.separator;
        playerName = "Player" + Random.inRange(100, 999);

        for (String arg: args) {
            if (prefix.isEmpty()) {
                prefix = arg;
            }
            else {
                if (prefix.equals("--game-dir")) {
                    gameDir = arg + "/";
                }
                else if (prefix.equals("--player-name")) {
                    playerName = arg;
                }

                prefix = "";
            }
        }

        // creating game directory if it doesn't exist
        new File(gameDir).mkdir();
    }

    public static void quitToMainMenu()
    {
        Opencraft.isWorldLoaded = true;

        Opencraft.inMenu = true;
        Opencraft.renderWhenInMenu = false;

        new Thread(() -> {
            if (ticks != null) ticks.clear();
            if (level != null) level.destroy();
            if (levelRenderer != null) levelRenderer.destroy();
            if (particleEngine != null) particleEngine.destroy();
            if (playerController != null) playerController.destroy();
            if (playerController != null) playerController = null;
            level = null;
            levelRenderer = null;
            particleEngine = null;

            isWorldLoaded = false;

            Thread.currentThread().interrupt();
        }).start();

        try {
            internalServer.destroy();
            internalServerConnection.destroy();
        } catch (Exception e) {
            Logger.exception("Unable to close internal server/connection!", e);
        }

        System.gc();

        Opencraft.inMenu = true;
        Opencraft.renderWhenInMenu = false;
        setCurrentScreen(GUI.mainMenu);
    }

    public static void startNewGame(int loadState, int seed) {
        try {
            GUI.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.loading_world"));

            levelRenderer = new LevelRenderer();
            playerController = new PlayerController();
            particleEngine = new ParticleEngine();
            level = new ClientLevel();

            entityPlayer = new EntityPlayer(Opencraft.getPlayerController());
            entityPlayer.setNameTag(new Nametag(playerName));

            if (loadState != 1)
                internalServer.create(seed);
            internalServerConnection.create();

//            if (loadState == 1) {
//                internalServerConnection.connect(internalServer.getHost(), internalServer.getPort());
//            }

            levelRenderer.prepareChunks();

            closeCurrentScreen();
            Opencraft.isWorldLoaded = true;
        } catch (Exception e) {
            Logger.exception("Error loading the world", e);
            quitToMainMenu();
//            GUI.loadingScreen.setLoadingText(Lang.getLanguageString("gui.text.unable_to_load"), true);
        }
    }

    public static void connectTo(String host, int port) {
        Opencraft.startNewGame(1, -1);
    }

    public static int getFPS() {
        return FPS;
    }

    public static boolean isInMenu() {
        return inMenu;
    }

    public static void updateGlContext() {
        for (Runnable r: new ArrayList<>(glContext)) {
            GL11.glLoadIdentity();
            r.run();
        }

        glContext.clear();
    }

    public static ClientLevel getLevel()
    {
        return level;
    }

    public static LevelType getLevelType() {
        return currentLevel;
    }

    public static int getRenderDistance() {
        return OptionsRegistry.Values.getIntOption("renderDistance");
    }

    public static int getWidth()
    {
        return display.getWidth();
    }

    public static int getHeight()
    {
        return display.getHeight();
    }

    public static int getDefaultWidth()
    {
        return width;
    }

    public static int getDefaultHeight()
    {
        return height;
    }

    public static int getFOV()
    {
        return OptionsRegistry.Values.getIntOption("FOV");
    }

    public static PlayerController getPlayerController()
    {
        return Opencraft.playerController;
    }

    public static String getVersion()
    {
        return Opencraft.version;
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
        return OptionsRegistry.Values.getIntOption("guiScale");
    }

    public static int getMaxGuiScale() {
        return 6;
    }

    public static LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public static int registerRenderEvent(RenderHandler tick)
    {
        glUpdateEvent.put(glUpdateEvent.size(), tick);
        return glUpdateEvent.size()-1;
    }

    public static void unregisterRenderEvent(int index)
    {
        glUpdateEvent.remove(index);
    }

    public static int registerTickEvent(Tick tick)
    {
        ticks.put(ticks.size(), tick);
        return ticks.size()-1;
    }

    public static void unregisterTickEvent(int index)
    {
        ticks.remove(index);
    }

    public static int registerGuiInterfaceEvent(GuiInterface iGuiInterface)
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

    public static int registerOptionsListener(OptionsListener listener)
    {
        optionsListeners.put(optionsListeners.size(), listener);
        return optionsListeners.size()-1;
    }

    public static void unregisterOptionsListener(int index)
    {
        optionsListeners.remove(index);
    }

    public static HashMap<Integer, LevelListener> getLevelListeners() {
        return levelListeners;
    }

    public static void runInGLContext(Runnable func)
    {
        glContext.add(func);
    }

    public static int getGuiScaleValue() {
        int scale = 240;
        if (getGuiScale() == 1) scale = 540;
        if (getGuiScale() == 2) scale = 440;
        if (getGuiScale() == 3) scale = 340;
        if (getGuiScale() == 4) scale = 240;
        if (getGuiScale() == 5) scale = 140;
        if (getGuiScale() == 6) scale = 40;

        return scale;
    }

    public static int getScreenScaledWidth() {
        return display.getWidth() * getGuiScaleValue() / display.getHeight();
    }

    public static int getScreenScaledHeight() {
        return display.getHeight() * getGuiScaleValue() / display.getHeight();
    }

    public static void closeCurrentScreen()
    {
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
        if (currentScreen != null)
            currentScreen.destroy();
        if (scr != null)
            scr.init();
        currentScreen = scr;
        System.gc();
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
        return System.currentTimeMillis() * 1000L;// / Sys.getTimerResolution();
    }

    public static Display getDisplay() {
        return display;
    }

    public static HashMap<Integer, GuiInterface> getGuiInterfaceMap() {
        return guiInterface;
    }

    public static HashMap<Integer, Tick> getTicksInterfaceMap() {
        return ticks;
    }

    public static boolean isWorldLoaded() {
        return Opencraft.isWorldLoaded;
    }

    public static int getChunksUpdatedCount() {
        return chunksUpdate;
    }

    public static HashMap<Integer, RenderHandler> getRenderersInterfaceMap() {
        return glUpdateEvent;
    }

    public static void registerMod(ModEntry modEntry) {
        modsList.add(modEntry);
    }

    public static String getJarLocation() throws URISyntaxException {
        return getJarLocation(Opencraft.class);
    }

    public static String getJarLocation(Class<?> c) throws URISyntaxException {
        return c.getProtectionDomain().getCodeSource().getLocation()
                .toURI().getPath();
    }

    public static String getGameDirectory() {
        return gameDir;
    }

    public static void inMenu(boolean b) {
        inMenu = b;
    }

    public static void closePlayerScreen() {
        if (Opencraft.playerScreen != null) {
            Opencraft.playerScreen.destroy();
        }
        Opencraft.playerScreen = null;
    }

    public static void setPlayerScreen(Screen playerScreen) {
        Opencraft.playerScreen = playerScreen;
        playerScreen.init();
    }

    public static Screen getPlayerScreen() {
        return playerScreen;
    }

    public static boolean isGuiOpened() {
        return inMenu || playerScreen != null;
    }

    public static SoundEngine getSoundEngine() {
        return soundEngine;
    }

    public static Client getClientConnection() {
        return internalServerConnection;
    }
    public static ThreadsManager getThreadsManager() {
        return threadsManager;
    }
    public static Server getInternalServer() {
        return internalServer;
    }

    public static EntityPlayer getPlayerEntity()
    {
        return entityPlayer;
    }

    public static HashMap<Integer, OptionsListener> getOptionsListeners() {
        return optionsListeners;
    }
}
