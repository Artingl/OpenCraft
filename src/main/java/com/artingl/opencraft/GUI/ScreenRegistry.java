package com.artingl.opencraft.GUI;

import com.artingl.opencraft.GUI.Screens.*;

public class ScreenRegistry {

    public static NewWorldConfiguratorScreen newWorldConfigurator;
    public static PauseMenuScreen pauseMenu;
    public static WorldListScreen worldList;
    public static ServersListScreen serversList;
    public static ModsListScreen modsList;
    public static SettingsMenuScreen settingsScreen;
    public static MainMenuScreen mainMenu;
    public static VideoSettingsMenuScreen videoSettings;
    public static LoadingScreen loadingScreen;


    public static void initScreens() {
        mainMenu = new MainMenuScreen();
        worldList = new WorldListScreen();
        serversList = new ServersListScreen();
        pauseMenu = new PauseMenuScreen();
        modsList = new ModsListScreen();
        newWorldConfigurator = new NewWorldConfiguratorScreen();
        settingsScreen = new SettingsMenuScreen();
        videoSettings = new VideoSettingsMenuScreen();
        loadingScreen = new LoadingScreen();
    }

}
