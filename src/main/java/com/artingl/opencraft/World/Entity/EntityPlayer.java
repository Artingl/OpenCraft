package com.artingl.opencraft.World.Entity;

import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.Game.TextureEngine;
import com.artingl.opencraft.World.Entity.Gamemode.Creative;
import com.artingl.opencraft.World.Entity.Gamemode.Gamemode;
import com.artingl.opencraft.World.Entity.Models.PlayerModel;
import com.artingl.opencraft.World.Item.Item;
import com.artingl.opencraft.World.PlayerController;
import com.artingl.opencraft.GUI.Screens.DeathScreen;

import java.util.ArrayList;

public class EntityPlayer extends Entity {

    public static int TEXTURE = TextureEngine.load("opencraft:entity/steve.png");
    public static PlayerModel model = new PlayerModel();

    private PlayerController controller;
    private Gamemode gamemode;
    private ArrayList<String> chatHistory;

    private Vector3f motion;
    private Vector2f cameraRotation;
    private Vector2f prevCameraRotation;
    private Vector2f armRotation;
    private Vector2f prevArmRotation;

    public EntityPlayer(PlayerController controller) {
        super(Opencraft.getLevel());
        setModel(model);

        this.setHeightOffset(1.82F);
        this.controller = controller;
        this.gamemode = Creative.instance;
        this.chatHistory = new ArrayList<>();
        this.motion = new Vector3f(0, 0, 0);
        this.cameraRotation = new Vector2f(0, 0);
        this.prevCameraRotation = new Vector2f(0, 0);
        this.armRotation = new Vector2f(0, 0);
        this.prevArmRotation = new Vector2f(0, 0);
        this.entityType = Types.PLAYER;
    }

    public void tick() {
        super.tick();
        this.prevCameraRotation = this.cameraRotation;
        this.prevArmRotation = this.armRotation;
        this.armRotation.x = (float)((double)this.armRotation.x + (double)(this.getRotation().x - this.armRotation.x) * 0.5D);
        this.armRotation.y = (float)((double)this.armRotation.y + (double)(this.getRotation().y - this.armRotation.y) * 0.5D);
        this.controller.tick();
    }

    public void destroy() {
        super.destroy();
        this.chatHistory.clear();
    }

    public void tellInChat(String msg) {
        chatHistory.add(msg);

        if (chatHistory.size() > 100)
            chatHistory.subList(chatHistory.size()-100, chatHistory.size());
    }

    public ArrayList<String> getChatHistory() {
        return chatHistory;
    }

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public void setGamemode(Gamemode gm) {
        this.gamemode = gm;
    }

    @Override
    public boolean hitHandler(float fallHeight) {
        if (getGamemode().getId() == Creative.id)
            return false;

        boolean result = super.hitHandler(fallHeight);

        if (result)
            Opencraft.getSoundEngine().loadAndPlay("opencraft", "player/damage");

        if (this.getHearts() == 0)
        {
            Opencraft.setCurrentScreen(new DeathScreen());
            Opencraft.changeMenuStatus(true);
        }

        return result;
    }

    public boolean hasInventory() {
        return true;
    }

    public boolean pick(Item item) {
        Opencraft.getPlayerController().appendInventory(item);
        return true;
    }

    public void closeScreen() {
        Opencraft.closePlayerScreen();
    }

    public void setScreen(Screen playerScreen) {
        Opencraft.setPlayerScreen(playerScreen);
    }

    public Screen getScreen() {
        return Opencraft.getPlayerScreen();
    }

    public boolean canControl() {
        return getScreen() == null;
    }

    public PlayerController getController() {
        return controller;
    }

    public Vector3f getMotion() {
        return motion;
    }

    public void setMotion(Vector3f motion) {
        this.motion = motion;
    }

    public void setCameraRotation(Vector2f cameraRotation) {
        this.cameraRotation = cameraRotation;
    }

    public Vector2f getCameraRotation() {
        return cameraRotation;
    }

    public Vector2f getPrevCameraRotation() {
        return prevCameraRotation;
    }

    public void setPrevCameraRotation(Vector2f prevCameraRotation) {
        this.prevCameraRotation = prevCameraRotation;
    }


    public Vector2f getArmRotation() {
        return armRotation;
    }

    public Vector2f getPrevArmRotation() {
        return prevArmRotation;
    }

    public void setArmRotation(Vector2f armRotation) {
        this.armRotation = armRotation;
    }

    public void setPrevArmRotation(Vector2f prevArmRotation) {
        this.prevArmRotation = prevArmRotation;
    }

}
