package com.artingl.opencraft.World;

import com.artingl.opencraft.GL.Controls;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.GUI.Screens.ChatScreen;
import com.artingl.opencraft.GUI.Windows.PlayerGUI;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Entity.Gamemode.Creative;
import com.artingl.opencraft.World.Entity.Gamemode.Survival;
import com.artingl.opencraft.World.Item.Item;
import com.artingl.opencraft.World.Item.ItemBlock;
import com.artingl.opencraft.World.Item.Tool;

import java.util.HashMap;

public class PlayerController
{

    private class BreakingBlock {
        public float blockBreakState;
        public RayCast.RayResult blockPos;
        public Block block;

    }

    private ItemBlock[] inventory;
    private PlayerGUI playerGUI;

    private HashMap<Integer, Boolean> clickedMouse;
    private HashMap<String, Boolean> clickedKeyboard;

    private boolean isFlying = true;

    private final BreakingBlock breakingBlock;
    private final int keyboardEvent;

    private int cameraOrient = 3;


    public PlayerController() {
        this.playerGUI = new PlayerGUI();
        this.clickedMouse = new HashMap<>();
        this.clickedKeyboard = new HashMap<>();
        this.inventory = new ItemBlock[36];
        this.breakingBlock = new BreakingBlock();

        this.inventory[0] = new ItemBlock(BlockRegistry.Blocks.stone, 64);

        for (int i = 0; i < 20; i++) clickedMouse.put(i, false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toLowerCase(), false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toUpperCase(), false);

        this.keyboardEvent = Controls.registerKeyboardHandler(this, this::keyEvent);
    }

    public void tick() {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();

        Vector3f pos = entityPlayer.getPosition();
        Vector3f vel = entityPlayer.getVelocity();
        Vector2f rotation = entityPlayer.getRotation();

        if (entityPlayer.getGamemode().getId() == Survival.id) {
            isFlying = false;
        }

        float xa = 0;
        float ya = 0;
        float acceleration = 1;
        boolean inWater = entityPlayer.isInWater();

        if (entityPlayer.canControl()) {
            if (Controls.isKeyDown(Controls.Keys.KEY_W)) --ya;
            if (Controls.isKeyDown(Controls.Keys.KEY_S)) ++ya;
            if (Controls.isKeyDown(Controls.Keys.KEY_A)) --xa;
            if (Controls.isKeyDown(Controls.Keys.KEY_D)) ++xa;
            if (Controls.isKeyDown(Controls.Keys.KEY_LSHIFT) && !isFlying) {
                entityPlayer.setHeightOffset(1.72F);
                acceleration = 0.1f;
            } else if (!isFlying) entityPlayer.setHeightOffset(1.82F);
            if (Controls.isKeyDown(Controls.Keys.KEY_SPACE) && !isFlying) {
                if (inWater) {
                    vel.y += 0.04F;
                } else if (entityPlayer.isOnGround()) {
                    vel.y = 0.42F;
                }
            }

            if (entityPlayer.getGamemode().getId() == Creative.id) {
                if (isFlying) {
                    if (Controls.isKeyDown(Controls.Keys.KEY_SPACE)) {
                        vel.y = 0.42F;
                    } else if (Controls.isKeyDown(Controls.Keys.KEY_LSHIFT)) {
                        vel.y = -0.42F;
                    } else vel.y = 0;
                } else {
                    if (Controls.isKeyDown(Controls.Keys.KEY_SPACE)) {
                        if (inWater) {
                            vel.y += 0.04F;
                        } else if (entityPlayer.isOnGround()) {
                            vel.y = 0.42F;
                        }
                    }
                }
            }

            RayCast.RayResult[] ray = RayCast.rayCastToBlock(6, rotation.x, rotation.y, pos.x, pos.y, pos.z);
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

                            Opencraft.getLevel().getBlock(ray[0]).destroy(ray[0]);
                            Opencraft.getLevel().getBlock(ray[0]).createDrop(ray[0]);
                            Opencraft.getLevel().removeBlock(ray[0]);
                        }
                    }
                    else {
                        this.breakingBlock.blockBreakState = 0;
                    }
                }
                else {
                    this.breakingBlock.blockBreakState = 0;
                    this.breakingBlock.blockPos = ray[0];
                    this.breakingBlock.block = Opencraft.getLevel().getBlock(ray[0]);
                }

                if (Controls.getMouseKey(1) && !clickedMouse.get(1) && !entityPlayer.getAABB().intersects(Block.getAABB(ray[1]))
                        && getInventoryItem(playerGUI.selected) != null)
                {
                    if (getInventoryItem(playerGUI.selected) instanceof ItemBlock) {
                        RayCast.RayResult rayResult = ray[1];

                        if (Opencraft.getLevel().getBlock(ray[0]).equals(BlockRegistry.Blocks.grass)) {
                            rayResult = ray[0];
                        }

                        Opencraft.getLevel().setBlock(rayResult, ((ItemBlock)getInventoryItem(playerGUI.selected)).getBlock());
                        decreaseSlot(playerGUI.selected);
                    }

                    clickedMouse.put(1, true);
                }
                else if(!Controls.getMouseKey(1))
                {
                    clickedMouse.put(1, false);
                }
            }
        }
        else {
            if (entityPlayer.getGamemode().getId() == Creative.id) {
                if (isFlying) {
                    vel.y = 0;
                }
            }
        }

        entityPlayer.setVelocity(vel);

        float yo;
        if (inWater && !isFlying) {
            yo = entityPlayer.getPosition().y;
            entityPlayer.moveRelative(xa, ya, 0.02F);
            entityPlayer.move(entityPlayer.getVelocity().x, entityPlayer.getVelocity().y, entityPlayer.getVelocity().z);
            entityPlayer.getVelocity().x *= 0.8F * acceleration;
            entityPlayer.getVelocity().y *= 0.8F;
            entityPlayer.getVelocity().z *= 0.8F * acceleration;
            entityPlayer.getVelocity().y = (float) ((double) entityPlayer.getVelocity().y - 0.02D);
            if (entityPlayer.hasHorizontalCollision() && entityPlayer.isFree(entityPlayer.getVelocity().x, entityPlayer.getVelocity().y + 0.6F - yo + yo, entityPlayer.getVelocity().z)) {
                entityPlayer.getVelocity().y = 0.3F;
            }
        } else if (isFlying)  {
            entityPlayer.moveRelative(xa, ya, 0.1F);
            entityPlayer.move(entityPlayer.getVelocity().x, entityPlayer.getVelocity().y, entityPlayer.getVelocity().z);
            entityPlayer.getVelocity().x *= 0.91F * acceleration;
            entityPlayer.getVelocity().y *= 0.98F;
            entityPlayer.getVelocity().z *= 0.91F * acceleration;
            entityPlayer.getVelocity().y = (float)((double)entityPlayer.getVelocity().y - 0.08D);

            if (entityPlayer.isOnGround()) {
                setFlying(false);
            }
        } else {
            entityPlayer.moveRelative(xa, ya, entityPlayer.isOnGround() ? 0.1F : 0.02F);
            entityPlayer.move(entityPlayer.getVelocity().x, entityPlayer.getVelocity().y, entityPlayer.getVelocity().z);
            entityPlayer.getVelocity().x *= 0.91F * acceleration;
            entityPlayer.getVelocity().y *= 0.98F;
            entityPlayer.getVelocity().z *= 0.91F * acceleration;
            entityPlayer.getVelocity().y = (float)((double)entityPlayer.getVelocity().y - 0.08D);
            if (entityPlayer.isOnGround()) {
                entityPlayer.getVelocity().x *= 0.6F;
                entityPlayer.getVelocity().z *= 0.6F;
            }
        }

    }

    public void keyEvent(Controls.KeyInput keyInput) {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();

        if (keyInput.keyCode == Controls.Keys.KEY_SPACE && keyInput.clickType == Controls.ClickType.DOUBLE) {
            if (entityPlayer.getGamemode().getId() == Creative.id) {
                setFlying(!isFlying);
                if (isFlying)
                    entityPlayer.getVelocity().y = 0.42f;
            }
        }
        else if (keyInput.keyCode == Controls.Keys.KEY_T) {
            if (entityPlayer.getScreen() == null)
                entityPlayer.setScreen(new ChatScreen(entityPlayer, Opencraft.getLevel(), ""));
        }
        else if (keyInput.keyCode == Controls.Keys.KEY_SLASH) {
            if (entityPlayer.getScreen() == null)
                entityPlayer.setScreen(new ChatScreen(entityPlayer, Opencraft.getLevel(), "/"));
        }
        else if (keyInput.keyCode == Controls.Keys.KEY_ESCAPE) {
            if (Opencraft.isWorldLoaded() && !Opencraft.getPlayerEntity().isDead()) {
                Opencraft.setCurrentScreen(GUI.pauseMenu);
                Opencraft.inMenu(true);
            }
        }
        else {
            String  c = keyInput.character;

            if (c.equals("1")) playerGUI.selected = 0;
            if (c.equals("2")) playerGUI.selected = 1;
            if (c.equals("3")) playerGUI.selected = 2;
            if (c.equals("4")) playerGUI.selected = 3;
            if (c.equals("5")) playerGUI.selected = 4;
            if (c.equals("6")) playerGUI.selected = 5;
            if (c.equals("7")) playerGUI.selected = 6;
            if (c.equals("8")) playerGUI.selected = 7;
            if (c.equals("9")) playerGUI.selected = 8;
        }
    }

    public void rotate()
    {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();

        Vector2f cameraRotation = entityPlayer.getCameraRotation();
        Vector2f rotation = entityPlayer.getRotation();
        Vector3f motion = entityPlayer.getMotion();

        float camRotY = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        float camRotX = (float) Math.atan(-motion.y * 0.20000000298023224D) * 15.0F;

        if (!isFlying() && entityPlayer.isOnGround()) {
            cameraRotation.y += (camRotY - cameraRotation.y) * 0.4F;
            cameraRotation.x += (camRotX - cameraRotation.x) * 0.8F;
        }

        rotation.y = (float)((double)rotation.y + (double) Controls.getDX() * 0.15D);
        rotation.x = (float)((double)rotation.x - (double) Controls.getDY() * 0.15D);

        motion.x = (float) (-Math.sin(rotation.y / 180.0F * (float)Math.PI) * Math.cos(rotation.x / 180.0F * (float)Math.PI) * 0.15F);
        motion.z = (float) (Math.cos(rotation.y / 180.0F * (float)Math.PI) * Math.cos(rotation.x / 180.0F * (float)Math.PI) * 0.15F);
        motion.y = (float) (-Math.sin(rotation.x / 180.0F * (float)Math.PI) * 0.15F + 0.1F);

        if (rotation.x > 90) rotation.x = 90;
        if (rotation.x < -90) rotation.x = -90;

        entityPlayer.setMotion(motion);
        entityPlayer.setCameraRotation(cameraRotation);
        entityPlayer.setRotation(rotation);
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
        if (inventory[playerGUI.selected] == null) return null;
        return inventory[playerGUI.selected];
    }

    public void appendInventory(Item item) {
        Opencraft.getSoundEngine().loadAndPlay("opencraft", "player/pick");

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

        this.playerGUI = null;
        this.inventory = null;
        this.clickedKeyboard = null;
        this.clickedMouse = null;

        Controls.unregisterKeyboardHandler(this.keyboardEvent);
    }

    public float getBlockBreakState() {
        return this.breakingBlock.blockBreakState;
    }

    public int getCameraOrient() {
        return cameraOrient;
    }
}
