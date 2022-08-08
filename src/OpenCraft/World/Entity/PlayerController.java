package OpenCraft.World.Entity;

import OpenCraft.Controls;
import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Level.Level;
import OpenCraft.World.RayCast;
import OpenCraft.gui.windows.PlayerInventory;
import org.lwjgl.input.Keyboard;
import java.util.HashMap;

public class PlayerController
{
    private Block[] inventory;
    private PlayerInventory playerInventory;

    private HashMap<Integer, Boolean> clickedMouse;
    private HashMap<String, Boolean> clickedKeyboard;

    private boolean isFlying;

    public PlayerController() {
        this.playerInventory = new PlayerInventory();
        this.clickedMouse = new HashMap<>();
        this.clickedKeyboard = new HashMap<>();
        this.inventory = new Block[36];

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

    public void tick()
    {
        EntityPlayer entityPlayer = OpenCraft.getLevel().getPlayerEntity();

        float w = 0.3F;
        float h = 0.9F;

        float xa = 0.0F;
        float ya = 0.0F;
        boolean inWater = entityPlayer.inWater();

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) --ya;
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) ++ya;
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) --xa;
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) ++xa;

        if (isFlying)
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                entityPlayer.yd = 0.42F;
            }
            else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                entityPlayer.yd = -0.42F;
            }
            else entityPlayer.yd = 0;
        }
        else {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                if (inWater) {
                    entityPlayer.yd += 0.04F;
                } else if (entityPlayer.onGround) {
                    entityPlayer.yd = 0.42F;
                }
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_F) && !clickedKeyboard.get("f")) {
            setFlying(!isFlying);
            if (isFlying)
                entityPlayer.yd = 0.42f;
            clickedKeyboard.put("f", true);
        }
        else if(!Keyboard.isKeyDown(Keyboard.KEY_F))
        {
            clickedKeyboard.put("f", false);
        }

        float yo;
        float acceleration = 0.0f;
        if (inWater) {
            yo = entityPlayer.y;
            entityPlayer.moveRelative(xa, ya, 0.02F);
            entityPlayer.move(entityPlayer.xd, entityPlayer.yd, entityPlayer.zd);
            entityPlayer.xd *= 0.8F + acceleration;
            entityPlayer.yd *= 0.8F;
            entityPlayer.zd *= 0.8F + acceleration;
            entityPlayer.yd = (float) ((double) entityPlayer.yd - 0.02D);
            if (entityPlayer.horizontalCollision && entityPlayer.isFree(entityPlayer.xd, entityPlayer.yd + 0.6F - entityPlayer.y + yo, entityPlayer.zd)) {
                entityPlayer.yd = 0.3F;
            }
        } else if (isFlying)  {
            entityPlayer.moveRelative(xa, ya, 0.1F);
            entityPlayer.move(entityPlayer.xd, entityPlayer.yd, entityPlayer.zd);
            entityPlayer.xd *= 0.91F + acceleration;
            entityPlayer.yd *= 0.98F + acceleration;
            entityPlayer.zd *= 0.91F + acceleration;
            entityPlayer.yd = (float)((double)entityPlayer.yd - 0.08D);

            if (entityPlayer.onGround) {
                setFlying(false);
            }
        } else {
            entityPlayer.moveRelative(xa, ya, entityPlayer.onGround ? 0.1F : 0.02F);
            entityPlayer.move(entityPlayer.xd, entityPlayer.yd, entityPlayer.zd);
            entityPlayer.xd *= 0.91F + acceleration;
            entityPlayer.yd *= 0.98F;
            entityPlayer.zd *= 0.91F + acceleration;
            entityPlayer.yd = (float)((double)entityPlayer.yd - 0.08D);
            if (entityPlayer.onGround) {
                entityPlayer.xd *= 0.6F;
                entityPlayer.zd *= 0.6F;
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

        float[][] ray = RayCast.rayCastToBlock(6, entityPlayer.rx, entityPlayer.ry, entityPlayer.x, entityPlayer.y, entityPlayer.z);
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

            if (Controls.getMouseKey(1) && !clickedMouse.get(1) && !entityPlayer.aabb.intersects(Block.getAABB((int)ray[2][0], (int)ray[2][1], (int)ray[2][2])) && getInventoryBlock(playerInventory.selected) != null)
            {
                OpenCraft.getLevel().setBlock((int)ray[2][0], (int)ray[2][1], (int)ray[2][2], getInventoryBlock(playerInventory.selected));
                clickedMouse.put(1, true);
            }
            else if(!Controls.getMouseKey(1))
            {
                clickedMouse.put(1, false);
            }
        }

        // todo: remove it in the future (is used to be able to change current dimension)
        if (Keyboard.isKeyDown(Keyboard.KEY_C) && !clickedKeyboard.get("c")) {
            OpenCraft.switchWorld(OpenCraft.getLevelType() == Level.LevelType.WORLD ? Level.LevelType.HELL : Level.LevelType.WORLD);
            clickedKeyboard.put("c", true);
        }
        else if(!Keyboard.isKeyDown(Keyboard.KEY_F))
        {
            clickedKeyboard.put("c", false);
        }
    }

    public void setFlying(boolean i)
    {
        isFlying = i;
    }

    public boolean isFlying() {
        return isFlying;
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

    public Block getCurrentBlock()
    {
        return inventory[playerInventory.selected];
    }

    public void destroy() {
        this.clickedMouse.clear();
        this.clickedKeyboard.clear();

        this.playerInventory = null;
        this.inventory = null;
        this.clickedKeyboard = null;
        this.clickedMouse = null;
    }
}
