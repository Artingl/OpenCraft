package OpenCraft.World.Entity;

import OpenCraft.OpenCraft;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Entity.Models.ZombieModel;
import org.lwjgl.opengl.GL11;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class EntityZombie extends Entity
{

    public static int TEXTURE = TextureEngine.load("opencraft:entity/zombie.png");
    public static ZombieModel model = new ZombieModel();

    public float rot;
    public float timeOffs;
    public float speed;
    public float rotA;

    public EntityZombie(float x, float y, float z)
    {
        super(x, y, z);
        setModel(model);
        this.rotA = (float)(Math.random() + 1.0D) * 0.01F;
        this.setPosition(x, y, z);
        this.timeOffs = (float)Math.random() * 1239813.0F;
        this.rot = (float)(Math.random() * 3.141592653589793D * 2.0D);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.rot += this.rotA;
        this.rotA = (float)((double)this.rotA * 0.99D);
        this.rotA = (float)((double)this.rotA + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.06);
        float xa = (float)Math.sin((double)this.rot);
        float ya = (float)Math.cos((double)this.rot);

        if(OpenCraft.getLevel().getBlock(Math.round(this.x + xa), (int)(y - 1), (int)(this.z + ya)).isVisible() && !OpenCraft.getLevel().getBlock(Math.round(this.x + xa), (int)(y), Math.round(this.z + ya)).isVisible() && this.onGround)
        {
            this.yd = 0.43F;
        }

        this.moveRelative(xa, ya, 0.02F);
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.91F;
        this.yd *= 0.98F;
        this.zd *= 0.91F;
        this.yd = (float)((double)this.yd - 0.08D);
        if (this.onGround) {
            this.xd *= 0.6F;
            this.zd *= 0.6F;
        }
    }

    @Override
    public void render() {
        super.render();

        float a = OpenCraft.getTimer().a;
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, TEXTURE);
        GL11.glPushMatrix();
        double time = (double)System.nanoTime() / 1.0E9D * 10.0D * (double)0.3 + (double)this.timeOffs;
        float size = 0.058333334F;
        float yy = (float)(-Math.abs(Math.sin(0.6662D)) * 5.0D - 23.0D);
        GL11.glTranslatef(this.xo + (this.x - this.xo) * a, this.yo + (this.y - this.yo) * a, this.zo + (this.z - this.zo) * a);
        GL11.glScalef(1.0F, -1.0F, 1.0F);
        GL11.glScalef(size, size, size);
        GL11.glTranslatef(0.0F, yy, 0.0F);
        float c = 57.29578F;
        GL11.glRotatef(this.rot * c + 180.0F, 0.0F, 1.0F, 0.0F);
        super.model.render((float)time);
        GL11.glPopMatrix();
        GL11.glDisable(3553);
    }

}
