package com.artingl.opencraft.World.Entity;

import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.Rendering.TextureEngine;
import com.artingl.opencraft.World.Entity.Gamemode.Creative;
import com.artingl.opencraft.World.Entity.Gamemode.Gamemode;
import com.artingl.opencraft.World.Entity.Gamemode.Survival;
import com.artingl.opencraft.World.Entity.Models.PlayerModel;
import com.artingl.opencraft.World.Item.Item;
import com.artingl.opencraft.World.PlayerController;
import com.artingl.opencraft.GUI.screens.DeathScreen;
import com.artingl.opencraft.Sound.Sound;

public class EntityPlayer extends Entity {

    public static int TEXTURE = TextureEngine.load("opencraft:entity/steve.png");
    public static PlayerModel model = new PlayerModel();

    private PlayerController controller;
    private Gamemode gamemode;

    public EntityPlayer(PlayerController controller) {
        setModel(model);

        this.heightOffset = 1.82F;
        this.controller = controller;
        this.gamemode = Survival.instance;
    }

    public void tick() {
        super.tick();

        // In case we might play on some sort of server, there might be more
        // than one player entity (for future reference)
        if (this.equals(OpenCraft.getLevel().getPlayerEntity()))
        {
            this.controller.tick();
        }
    }

    public void destroy() {
        super.destroy();
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
            Sound.loadAndPlay("opencraft:sounds/player/damage.wav");

        if (this.getHearts() == 0)
        {
            OpenCraft.setCurrentScreen(new DeathScreen());
            OpenCraft.changeMenuStatus(true);
        }

        return result;
    }

    public boolean hasInventory() {
        return true;
    }

    public boolean pick(Item item) {
        OpenCraft.getPlayerController().appendInventory(item);
        return true;
    }

}
