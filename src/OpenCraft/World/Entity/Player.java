package OpenCraft.World.Entity;

import OpenCraft.Game.Controls;
import OpenCraft.Game.RayCast;
import OpenCraft.Game.gui.windows.PlayerInventory;
import OpenCraft.Game.phys.AABB;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Locale;

public class Player extends Entity
{

    private Block[] inventory;
    private PlayerInventory playerInventory;

    private HashMap<Integer, Boolean> clickedMouse;
    private HashMap<String, Boolean> clickedKeyboard;

    public Player(float x, float y, float z) {
        super(x, y, z);
        this.heightOffset = 1.62F;
        this.playerInventory = new PlayerInventory();
        clickedMouse = new HashMap<>();
        clickedKeyboard = new HashMap<>();
        inventory = new Block[36];

        inventory[0] = Block.dirt;
        inventory[1] = Block.log_oak;
        inventory[2] = Block.stone;
        inventory[3] = Block.grass_block;
        inventory[4] = Block.leaves_oak;
        inventory[5] = Block.glass;

        for (int i = 0; i < 20; i++) clickedMouse.put(i, false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toLowerCase(), false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toUpperCase(), false);
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
            if (Controls.getMouseKey(0) && !clickedMouse.get(0))
            {
                OpenCraft.getLevel().getBlock((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]).destroy((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]);
                OpenCraft.getLevel().removeBlock((int)ray[1][0], (int)ray[1][1], (int)ray[1][2]);
                clickedMouse.put(0, true);
            }
            else if(!Controls.getMouseKey(0))
            {
                clickedMouse.put(0, false);
            }

            if (Controls.getMouseKey(1) && !clickedMouse.get(1) && !aabb.intersects(Block.getAABB((int)ray[2][0], (int)ray[2][1], (int)ray[2][2])) && getInventoryBlock(playerInventory.selected) != null)
            {
                OpenCraft.getLevel().setBlock((int)ray[2][0], (int)ray[2][1], (int)ray[2][2], getInventoryBlock(playerInventory.selected));
                clickedMouse.put(1, true);
            }
            else if(!Controls.getMouseKey(1))
            {
                clickedMouse.put(1, false);
            }
        }

        if (checkClickedKey("1")) playerInventory.selected = 0;
        if (checkClickedKey("2")) playerInventory.selected = 1;
        if (checkClickedKey("3")) playerInventory.selected = 2;
        if (checkClickedKey("4")) playerInventory.selected = 3;
        if (checkClickedKey("5")) playerInventory.selected = 4;
        if (checkClickedKey("6")) playerInventory.selected = 5;
        if (checkClickedKey("7")) playerInventory.selected = 6;
        if (checkClickedKey("8")) playerInventory.selected = 7;
        if (checkClickedKey("9")) playerInventory.selected = 8;
    }

    private boolean checkClickedKey(String key)
    {
        if (!clickedKeyboard.containsKey(key) || (Keyboard.isKeyDown(Keyboard.getKeyIndex(key)) && !clickedKeyboard.get(key)))
        {
            clickedKeyboard.put(key, true);
            return true;
        }
        else if (!Keyboard.isKeyDown(Keyboard.getKeyIndex(key)))
            clickedKeyboard.put(key, false);
        return false;
    }

    public Block getInventoryBlock(int i)
    {
        if (i < 0 || i >= 38) return null;
        return inventory[i];
    }

}
