package com.artingl.opencraft.World;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.windows.PlayerInventory;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Entity.Gamemode.Creative;
import com.artingl.opencraft.World.Entity.Gamemode.Survival;
import com.artingl.opencraft.World.Item.Item;
import com.artingl.opencraft.World.Item.ItemBlock;
import com.artingl.opencraft.World.Item.Tool;
import com.artingl.opencraft.World.Level.Level;

import java.util.HashMap;

public class PlayerController
{
    private class BreakingBlock {
        public float blockBreakState;
        public RayCast.RayResult blockPos;
        public Block block;

    }

    private ItemBlock[] inventory;
    private PlayerInventory playerInventory;

    private HashMap<Integer, Boolean> clickedMouse;
    private HashMap<String, Boolean> clickedKeyboard;

    private boolean isFlying;

    private final BreakingBlock breakingBlock;
    private final int keyboardEvent;

    public PlayerController() {
        this.playerInventory = new PlayerInventory();
        this.clickedMouse = new HashMap<>();
        this.clickedKeyboard = new HashMap<>();
        this.inventory = new ItemBlock[36];
        this.breakingBlock = new BreakingBlock();

        this.inventory[0] = new ItemBlock(Block.stone, 64);

        for (int i = 0; i < 20; i++) clickedMouse.put(i, false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toLowerCase(), false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toUpperCase(), false);

        this.keyboardEvent = Controls.registerKeyboardHandler((key) -> {
            EntityPlayer entityPlayer = OpenCraft.getLevel().getPlayerEntity();

            if (key.mod == Controls.Keys.KEY_SPACE && key.clickType == Controls.ClickType.DOUBLE) {
                if (entityPlayer.getGamemode().getId() == Creative.id) {
                    setFlying(!isFlying);
                    if (isFlying)
                        entityPlayer.yd = 0.42f;
                }
            }
            else if (key.mod == Controls.Keys.KEY_T) {

            }
            else {
                String  c = key.character;

                if (c.equals("1")) playerInventory.selected = 0;
                if (c.equals("2")) playerInventory.selected = 1;
                if (c.equals("3")) playerInventory.selected = 2;
                if (c.equals("4")) playerInventory.selected = 3;
                if (c.equals("5")) playerInventory.selected = 4;
                if (c.equals("6")) playerInventory.selected = 5;
                if (c.equals("7")) playerInventory.selected = 6;
                if (c.equals("8")) playerInventory.selected = 7;
                if (c.equals("9")) playerInventory.selected = 8;
            }
        });
    }

    public void tick() {
        EntityPlayer entityPlayer = OpenCraft.getLevel().getPlayerEntity();

        if (entityPlayer.getGamemode().getId() == Survival.id) {
            isFlying = false;
        }

        float xa = 0.0F;
        float ya = 0.0F;
        float acceleration = 1;

        boolean inWater = entityPlayer.inWater();

        if (Controls.isKeyDown(Controls.Keys.KEY_W))     --ya;
        if (Controls.isKeyDown(Controls.Keys.KEY_S))     ++ya;
        if (Controls.isKeyDown(Controls.Keys.KEY_A))     --xa;
        if (Controls.isKeyDown(Controls.Keys.KEY_D))     ++xa;
        if (Controls.isKeyDown(Controls.Keys.KEY_LSHIFT) && !isFlying) { entityPlayer.setHeightOffset(1.72F); acceleration = 0.1f; }
        else if (!isFlying)                                         entityPlayer.setHeightOffset(1.82F);
        if (Controls.isKeyDown(Controls.Keys.KEY_SPACE) && !isFlying) {
            if (inWater) {
                entityPlayer.yd += 0.04F;
            } else if (entityPlayer.onGround) {
                entityPlayer.yd = 0.42F;
            }
        }

        if (entityPlayer.getGamemode().getId() == Creative.id) {
            if (isFlying) {
                if (Controls.isKeyDown(Controls.Keys.KEY_SPACE)) {
                    entityPlayer.yd = 0.42F;
                } else if (Controls.isKeyDown(Controls.Keys.KEY_LSHIFT)) {
                    entityPlayer.yd = -0.42F;
                } else entityPlayer.yd = 0;
            } else {
                if (Controls.isKeyDown(Controls.Keys.KEY_SPACE)) {
                    if (inWater) {
                        entityPlayer.yd += 0.04F;
                    } else if (entityPlayer.onGround) {
                        entityPlayer.yd = 0.42F;
                    }
                }
            }
        }

        float yo;
        if (inWater && !isFlying) {
            yo = entityPlayer.getY();
            entityPlayer.moveRelative(xa, ya, 0.02F);
            entityPlayer.move(entityPlayer.xd, entityPlayer.yd, entityPlayer.zd);
            entityPlayer.xd *= 0.8F * acceleration;
            entityPlayer.yd *= 0.8F;
            entityPlayer.zd *= 0.8F * acceleration;
            entityPlayer.yd = (float) ((double) entityPlayer.yd - 0.02D);
            if (entityPlayer.horizontalCollision && entityPlayer.isFree(entityPlayer.xd, entityPlayer.yd + 0.6F - entityPlayer.getY() + yo, entityPlayer.zd)) {
                entityPlayer.yd = 0.3F;
            }
        } else if (isFlying)  {
            entityPlayer.moveRelative(xa, ya, 0.1F);
            entityPlayer.move(entityPlayer.xd, entityPlayer.yd, entityPlayer.zd);
            entityPlayer.xd *= 0.91F * acceleration;
            entityPlayer.yd *= 0.98F;
            entityPlayer.zd *= 0.91F * acceleration;
            entityPlayer.yd = (float)((double)entityPlayer.yd - 0.08D);

            if (entityPlayer.onGround) {
                setFlying(false);
            }
        } else {
            entityPlayer.moveRelative(xa, ya, entityPlayer.onGround ? 0.1F : 0.02F);
            entityPlayer.move(entityPlayer.xd, entityPlayer.yd, entityPlayer.zd);
            entityPlayer.xd *= 0.91F * acceleration;
            entityPlayer.yd *= 0.98F;
            entityPlayer.zd *= 0.91F * acceleration;
            entityPlayer.yd = (float)((double)entityPlayer.yd - 0.08D);
            if (entityPlayer.onGround) {
                entityPlayer.xd *= 0.6F;
                entityPlayer.zd *= 0.6F;
            }
        }

        RayCast.RayResult[] ray = RayCast.rayCastToBlock(6, entityPlayer.getRx(), entityPlayer.getRy(), entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ());

        if (ray[0].state)
        {
            if (Controls.getMouseKey(0) && this.breakingBlock.block != null && this.breakingBlock.blockPos.equals(ray[0]))
            {
                Block block = this.breakingBlock.block;

                if (block.getTool() != Tool.UNBREAKABLE && !block.isLiquid()) {
                    this.breakingBlock.blockBreakState += 0.5 / this.breakingBlock.block.getStrength();
                    this.breakingBlock.blockPos = ray[0];

                    if (this.breakingBlock.block.getStrength() == 0
                            || this.breakingBlock.blockBreakState >= 9
                            || block.getTool() == Tool.IMMEDIATELY
                            || entityPlayer.getGamemode().getId() == Creative.id) {
                        this.breakingBlock.blockBreakState = 0;

                        OpenCraft.getLevel().getBlock(ray[0]).destroy(ray[0]);
                        OpenCraft.getLevel().getBlock(ray[0]).createDrop(ray[0]);
                        OpenCraft.getLevel().removeBlock(ray[0]);
                    }
                }
                else {
                    this.breakingBlock.blockBreakState = 0;
                }
            }
            else {
                this.breakingBlock.blockBreakState = 0;
                this.breakingBlock.blockPos = ray[0];
                this.breakingBlock.block = OpenCraft.getLevel().getBlock(ray[0]);
            }

            if (Controls.getMouseKey(1) && !clickedMouse.get(1) && !entityPlayer.aabb.intersects(Block.getAABB(ray[1]))
                    && getInventoryItem(playerInventory.selected) != null)
            {
                if (getInventoryItem(playerInventory.selected) instanceof ItemBlock) {
                    RayCast.RayResult rayResult = ray[1];

                    if (OpenCraft.getLevel().getBlock(ray[0]).getIntId() == Block.grass.getIntId()) {
                        rayResult = ray[0];
                    }

                    OpenCraft.getLevel().setBlock(rayResult, ((ItemBlock)getInventoryItem(playerInventory.selected)).getBlock());
                    decreaseSlot(playerInventory.selected);
                }

                clickedMouse.put(1, true);
            }
            else if(!Controls.getMouseKey(1))
            {
                clickedMouse.put(1, false);
            }
        }

        // todo: remove it in the future (is used to be able to change current dimension)
        if (Controls.isKeyDown(Controls.Keys.KEY_C) && !clickedKeyboard.get("c")) {
            OpenCraft.switchWorld(OpenCraft.getLevelType() == Level.LevelType.WORLD ? Level.LevelType.HELL : Level.LevelType.WORLD);
            clickedKeyboard.put("c", true);
        }
        else if(!Controls.isKeyDown(Controls.Keys.KEY_C))
        {
            clickedKeyboard.put("c", false);
        }
    }

    public void rotate()
    {
        EntityPlayer entityPlayer = OpenCraft.getLevel().getPlayerEntity();

        entityPlayer.setRy((float)((double)entityPlayer.getRy() + (double)((float) Controls.getDX()) * 0.15D));
        entityPlayer.setRx((float)((double)entityPlayer.getRx() - (double)((float) Controls.getDY()) * 0.15D));

        if (entityPlayer.getRx() > 90) entityPlayer.setRx(90);
        if (entityPlayer.getRx() < -90) entityPlayer.setRx(-90);
    }

    public void setFlying(boolean i)
    {
        isFlying = i;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public Item getInventoryItem(int i)
    {
        if (i < 0 || i >= 38) return null;
        if (inventory[i] == null) return null;
        return inventory[i];
    }

    public Item getCurrentItem()
    {
        if (inventory[playerInventory.selected] == null) return null;
        return inventory[playerInventory.selected];
    }

    public void appendInventory(Item item) {
        if (item instanceof ItemBlock) {
            appendInventory((ItemBlock) item);
            return;
        }
    }

    public void appendInventory(ItemBlock item) {
        boolean found = false;
        for (ItemBlock itemBlock : inventory) {
            if (itemBlock == null) continue;
            if (itemBlock.getBlock().equals(item.getBlock())) {
                itemBlock.setAmount(itemBlock.getAmount() + item.getAmount());
                found = true;
                break;
            }
        }

        if (!found) {
            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] == null) {
                    inventory[i] = item;
                    break;
                }
            }
        }
    }

    public void decreaseSlot(int selected) {
        if (inventory[selected] == null) return;
        inventory[selected].setAmount(inventory[selected].getAmount()-1);
        if (inventory[selected].getAmount() <= 0) inventory[selected] = null;
    }

    public void destroy() {
        this.clickedMouse.clear();
        this.clickedKeyboard.clear();

        this.playerInventory = null;
        this.inventory = null;
        this.clickedKeyboard = null;
        this.clickedMouse = null;

        Controls.unregisterKeyboardHandler(this.keyboardEvent);
    }

    public float getBlockBreakState() {
        return this.breakingBlock.blockBreakState;
    }
}
