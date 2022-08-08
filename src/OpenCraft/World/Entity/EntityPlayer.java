package OpenCraft.World.Entity;

import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Entity.Models.PlayerModel;
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

    public EntityPlayer(PlayerController controller) {
        setModel(model);

        this.heightOffset = 1.62F;
        this.controller = controller;
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
        this.controller.tick();
    }

    public void destroy() {
        super.destroy();
    }
}
