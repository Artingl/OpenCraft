package OpenCraft.World.Entity;

import OpenCraft.Game.Controls;
import OpenCraft.Game.RayCast;
import OpenCraft.Game.phys.AABB;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Player extends Entity
{

    private Block currentBlock = Block.glass;

    private boolean clickedMouse0 = false;
    private boolean clickedMouse1 = false;

    public Player(float x, float y, float z) {
        super(x, y, z);
        this.heightOffset = 1.62F;
    }

    public void rotate()
    {
        super.setRy((float)((double)super.getRy() + (double)((float) Mouse.getDX()) * 0.15D));
        super.setRx((float)((double)super.getRx() - (double)((float)Mouse.getDY()) * 0.15D));

        if (super.getRx() > 90) super.setRx(90);
        if (super.getRx() < -90) super.setRx(-90);
    }

    @Override
    public void tick()
    {
        super.tick();

        float w = 0.3F;
        float h = 0.9F;

        float xa = 0.0F;
        float ya = 0.0F;
        boolean inWater = inWater();

        if (Keyboard.isKeyDown(200) || Keyboard.isKeyDown(17)) {
            --ya;
        }

        if (Keyboard.isKeyDown(208) || Keyboard.isKeyDown(31)) {
            ++ya;
        }

        if (Keyboard.isKeyDown(203) || Keyboard.isKeyDown(30)) {
            --xa;
        }

        if (Keyboard.isKeyDown(205) || Keyboard.isKeyDown(32)) {
            ++xa;
        }

        if ((Keyboard.isKeyDown(57) || Keyboard.isKeyDown(219))) {
            if (inWater) {
                this.yd += 0.04F;
            } else if (this.onGround) {
                this.yd = 0.42F;
            }
        }

        float yo;
        if (inWater) {
            yo = this.y;
            this.moveRelative(xa, ya, 0.02F);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.8F;
            this.yd *= 0.8F;
            this.zd *= 0.8F;
            this.yd = (float)((double)this.yd - 0.02D);
            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6F - this.y + yo, this.zd)) {
                this.yd = 0.3F;
            }
        } else {
            this.moveRelative(xa, ya, this.onGround ? 0.1F : 0.02F);
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

        float[][] ray = RayCast.rayCastToBlock(6, rx, ry, x, y, z);
        if (ray[0][0] == 1)
        {
            if (Controls.getMouseKey(0) && !clickedMouse0)
            {
                OpenCraft.getLevel().getBlock((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]).destroy((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]);
                OpenCraft.getLevel().removeBlock((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]);
                clickedMouse0 = true;
            }
            else if(!Controls.getMouseKey(0))
            {
                clickedMouse0 = false;
            }

            if (Controls.getMouseKey(1) && !clickedMouse1 && !aabb.intersects(Block.getAABB((int)ray[2][0], (int)ray[2][1], (int)ray[2][2])))
            {
                OpenCraft.getLevel().setBlock((int)ray[2][0], (int)ray[2][1], (int)ray[2][2], currentBlock);
                clickedMouse1 = true;
            }
            else if(!Controls.getMouseKey(1))
            {
                clickedMouse1 = false;
            }
        }
    }

    public Block getCurrentBlock()
    {
        return currentBlock;
    }

}
