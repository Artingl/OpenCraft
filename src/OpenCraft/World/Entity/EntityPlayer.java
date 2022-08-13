package OpenCraft.World.Entity;

import OpenCraft.OpenCraft;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Entity.Gamemode.Gamemode;
import OpenCraft.World.Entity.Gamemode.Survival;
import OpenCraft.World.Entity.Models.PlayerModel;
import OpenCraft.World.Item.Item;
import OpenCraft.World.PlayerController;
import OpenCraft.gui.screens.DeadScreen;
import OpenCraft.sound.Sound;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class EntityPlayer extends Entity {

    public static int TEXTURE;
    public static PlayerModel model = new PlayerModel();

    static {
        try {
            TEXTURE = TextureEngine.load(ImageIO.read(new File("resources/entity/steve.png")));
        } catch (IOException e) { }
    }

    private PlayerController controller;
    private Gamemode gamemode;

    public EntityPlayer(PlayerController controller) {
        setModel(model);

        this.heightOffset = 1.82F;
        this.controller = controller;
        this.gamemode = Survival.instance;
    }

    public void rotate()
    {
        super.setRy((float)((double)super.getRy() + (double)((float) Mouse.getDX()) * 0.15D));
        super.setRx((float)((double)super.getRx() - (double)((float) Mouse.getDY()) * 0.15D));

        if (super.getRx() > 90) super.setRx(90);
        if (super.getRx() < -90) super.setRx(-90);
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
        boolean result = super.hitHandler(fallHeight);

        if (result)
            Sound.loadAndPlay("resources/sounds/player/damage.wav");

        if (this.getHearts() == 0)
        {
            OpenCraft.setCurrentScreen(new DeadScreen());
            OpenCraft.changeMenuStatus(true);
        }

        return result;
    }

    public boolean hasInventory() {
        return true;
    }

    public void pick(Item item) {
        OpenCraft.getPlayerController().appendInventory(item);
    }

}
