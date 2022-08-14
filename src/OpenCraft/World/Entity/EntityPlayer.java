package OpenCraft.World.Entity;

import OpenCraft.OpenCraft;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Entity.Gamemode.Creative;
import OpenCraft.World.Entity.Gamemode.Gamemode;
import OpenCraft.World.Entity.Gamemode.Survival;
import OpenCraft.World.Entity.Models.PlayerModel;
import OpenCraft.World.Item.Item;
import OpenCraft.World.PlayerController;
import OpenCraft.gui.screens.DeathScreen;
import OpenCraft.sound.Sound;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

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
