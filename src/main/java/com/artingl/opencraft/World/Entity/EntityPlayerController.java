package com.artingl.opencraft.World.Entity;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.GUI.Screens.ChatScreen;
import com.artingl.opencraft.GUI.Windows.PlayerHotBar;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Packet.PacketWorldUpdate;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Spectator;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Block.BlockRegistry;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Creative;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Survival;
import com.artingl.opencraft.World.Item.ItemSlot;
import com.artingl.opencraft.World.Item.Tool;
import com.artingl.opencraft.World.RayCast;

import java.util.HashMap;

public class EntityPlayerController
{

    private class BreakingBlock {
        public float blockBreakState;
        public RayCast.RayResult blockPos;
        public Block block;

    }

    private PlayerHotBar playerHotbar;

    private HashMap<Integer, Boolean> clickedMouse;
    private HashMap<String, Boolean> clickedKeyboard;

    private final BreakingBlock breakingBlock;
    private final int keyboardEvent;

    private int cameraOrient = 3;


    public EntityPlayerController() {
        this.playerHotbar = new PlayerHotBar();
        this.clickedMouse = new HashMap<>();
        this.clickedKeyboard = new HashMap<>();
        this.breakingBlock = new BreakingBlock();

        for (int i = 0; i < 20; i++) clickedMouse.put(i, false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toLowerCase(), false);
        for(int i = 32; i <= 126; i++) clickedKeyboard.put(String.valueOf((char)i).toUpperCase(), false);

        this.keyboardEvent = Input.registerKeyboardHandler(this, this::keyEvent);
    }

    public void tick() {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();

        Vector3f pos = entityPlayer.getPosition();
        Vector3f vel = entityPlayer.getVelocity();
        Vector2f rotation = entityPlayer.getRotation();

        if (entityPlayer.getGamemode().getId() == Survival.id) {
            entityPlayer.setFlyingState(false);
        }

        if (entityPlayer.getGamemode().getId() == Spectator.id) {
            entityPlayer.setFlyingState(true);
        }

        int xa = 0;
        int ya = 0;

        entityPlayer.setAcceleration(1f);

        if (entityPlayer.canControl()) {
            if (Input.isKeyDown(Input.Keys.KEY_W)) --ya;
            if (Input.isKeyDown(Input.Keys.KEY_S)) ++ya;
            if (Input.isKeyDown(Input.Keys.KEY_A)) --xa;
            if (Input.isKeyDown(Input.Keys.KEY_D)) ++xa;
            if (Input.isKeyDown(Input.Keys.KEY_LSHIFT) && !entityPlayer.isFlying()) {
                entityPlayer.setHeightOffset(1.72F);
                entityPlayer.setAcceleration(0.1f);
            } else if (!entityPlayer.isFlying()) entityPlayer.setHeightOffset(1.82F);
            if (Input.isKeyDown(Input.Keys.KEY_SPACE) && !entityPlayer.isFlying()) {
                if (entityPlayer.isInWater()) {
                    vel.y += 0.04F;
                } else if (entityPlayer.isOnGround()) {
                    vel.y = 0.42F;
                }
            }

            if (entityPlayer.getGamemode().getId() == Creative.id || entityPlayer.getGamemode().getId() == Spectator.id) {
                if (entityPlayer.isFlying()) {
                    if (Input.isKeyDown(Input.Keys.KEY_SPACE)) {
                        vel.y = 0.42F;
                    } else if (Input.isKeyDown(Input.Keys.KEY_LSHIFT)) {
                        vel.y = -0.42F;
                    } else vel.y = 0;
                } else {
                    if (Input.isKeyDown(Input.Keys.KEY_SPACE)) {
                        if (entityPlayer.isInWater()) {
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
                if (Input.getMouseKey(0) && this.breakingBlock.block != null && this.breakingBlock.blockPos.equals(ray[0]))
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

                            Block oldBlock = Opencraft.getLevel().getBlock(ray[0]);

                            oldBlock.destroy(ray[0]);
                            oldBlock.createDrop(ray[0]);
                            Opencraft.getLevel().removeBlock(ray[0]);

                            if (Opencraft.getClientConnection().isActive()) {
                                try {
                                    new PacketWorldUpdate(null, Opencraft.getClientConnection()
                                            .getConnection()).breakBlock(ray[0], entityPlayer);
                                } catch (Exception e) {
                                    Logger.exception("Unable to send packet!", e);

                                    Opencraft.getLevel().setBlock(oldBlock, ray[0]);
                                }
                            }
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

                if (Input.getMouseKey(1) && !clickedMouse.get(1) && !entityPlayer.getAABB().intersects(Block.getAABB(ray[1]))
                        && entityPlayer.getInventoryItem(entityPlayer.getInventorySelectedSlot()) != null)
                {
                    RayCast.RayResult rayResult = ray[1];

                    if (Opencraft.getLevel().getBlock(ray[0]).equals(BlockRegistry.Blocks.grass)) {
                        rayResult = ray[0];
                    }

                    ItemSlot item = entityPlayer.getInventoryItem(entityPlayer.getInventorySelectedSlot());

                    if (item.getItemType() == ItemSlot.ItemType.BLOCK && item.getAmount() > 0) {
                        Opencraft.getLevel().setBlock(item.getBlock(), rayResult);
                        entityPlayer.decreaseSlot(entityPlayer.getInventorySelectedSlot());
                    }

                    if (Opencraft.getClientConnection().isActive()) {
                        try {
                            new PacketWorldUpdate(null, Opencraft.getClientConnection()
                                    .getConnection()).setBlock(item.getBlock(), rayResult, entityPlayer);
                        } catch (Exception e) {
                            Logger.exception("Unable to send packet!", e);

                            Opencraft.getLevel().removeBlock(rayResult);
                            entityPlayer.increaseSlot(entityPlayer.getInventorySelectedSlot());
                        }
                    }

                    clickedMouse.put(1, true);
                }
                else if(!Input.getMouseKey(1))
                {
                    clickedMouse.put(1, false);
                }
            }
        }
        else {
            if (entityPlayer.getGamemode().getId() == Creative.id || entityPlayer.getGamemode().getId() == Spectator.id) {
                if (entityPlayer.isFlying()) {
                    vel.y = 0;
                }
            }
        }

        entityPlayer.setVelocity(vel);
        EntityPlayerController.updateEntityPositionByVelocity(entityPlayer, new Vector2i(xa, ya));
    }

    public void keyEvent(Input.KeyInput keyInput) {
        EntityPlayer entityPlayer = Opencraft.getPlayerEntity();

        if (keyInput.keyCode == Input.Keys.KEY_SPACE && keyInput.clickType == Input.ClickType.DOUBLE) {
            if (entityPlayer.getGamemode().getId() == Creative.id) {
                entityPlayer.setFlyingState(!entityPlayer.isFlying());
                if (entityPlayer.isFlying())
                    entityPlayer.getVelocity().y = 0.42f;
            }
            else if (entityPlayer.getGamemode().equals(Spectator.instance)) {
                entityPlayer.setFlyingState(true);
                entityPlayer.getVelocity().y = 0.42f;
            }
        }
        else if (keyInput.keyCode == Input.Keys.KEY_T) {
            if (entityPlayer.getScreen() == null)
                entityPlayer.setScreen(new ChatScreen(entityPlayer, Opencraft.getLevel(), ""));
        }
        else if (keyInput.keyCode == Input.Keys.KEY_SLASH) {
            if (entityPlayer.getScreen() == null)
                entityPlayer.setScreen(new ChatScreen(entityPlayer, Opencraft.getLevel(), "/"));
        }
        else if (keyInput.keyCode == Input.Keys.KEY_ESCAPE) {
            if (Opencraft.isWorldLoaded() && !Opencraft.getPlayerEntity().isDead()) {
                Opencraft.setCurrentScreen(ScreenRegistry.pauseMenu);
                Opencraft.inMenu(true);
            }
        }
        else {
            String  c = keyInput.character;

            if (c.equals("1")) entityPlayer.setInventorySelectedSlot(0);
            if (c.equals("2")) entityPlayer.setInventorySelectedSlot(1);
            if (c.equals("3")) entityPlayer.setInventorySelectedSlot(2);
            if (c.equals("4")) entityPlayer.setInventorySelectedSlot(3);
            if (c.equals("5")) entityPlayer.setInventorySelectedSlot(4);
            if (c.equals("6")) entityPlayer.setInventorySelectedSlot(5);
            if (c.equals("7")) entityPlayer.setInventorySelectedSlot(6);
            if (c.equals("8")) entityPlayer.setInventorySelectedSlot(7);
            if (c.equals("9")) entityPlayer.setInventorySelectedSlot(8);
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

        if (!entityPlayer.isFlying() && entityPlayer.isOnGround()) {
            cameraRotation.y += (camRotY - cameraRotation.y) * 0.4F;
            cameraRotation.x += (camRotX - cameraRotation.x) * 0.8F;
        }

        rotation.y = (float)((double)rotation.y + (double) Input.getDX() * 0.15D);
        rotation.x = (float)((double)rotation.x - (double) Input.getDY() * 0.15D);

        motion.x = (float) (-Math.sin(rotation.y / 180.0F * (float)Math.PI) * Math.cos(rotation.x / 180.0F * (float)Math.PI) * 0.15F);
        motion.z = (float) (Math.cos(rotation.y / 180.0F * (float)Math.PI) * Math.cos(rotation.x / 180.0F * (float)Math.PI) * 0.15F);
        motion.y = (float) (-Math.sin(rotation.x / 180.0F * (float)Math.PI) * 0.15F + 0.1F);

        if (rotation.x > 90) rotation.x = 90;
        if (rotation.x < -90) rotation.x = -90;

        entityPlayer.setMotion(motion);
        entityPlayer.setCameraRotation(cameraRotation);
        entityPlayer.setRotation(rotation);
    }

    public void destroy() {
        this.clickedMouse.clear();
        this.clickedKeyboard.clear();

        this.playerHotbar = null;
        this.clickedKeyboard = null;
        this.clickedMouse = null;

        Input.unregisterKeyboardHandler(this.keyboardEvent);
    }

    public float getBlockBreakState() {
        return this.breakingBlock.blockBreakState;
    }

    public int getCameraOrient() {
        return cameraOrient;
    }

    public static void updateEntityPositionByVelocity(Entity entity, Vector2i pos) {
        float yo;
        float acceleration = entity.getAcceleration();

        if (entity.isInWater() && !entity.isFlying()) {
            yo = entity.getPosition().y;
            entity.moveRelative(pos.x, pos.y, 0.02F);
            entity.move(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z);
            entity.getVelocity().x *= 0.8F * acceleration;
            entity.getVelocity().y *= 0.8F;
            entity.getVelocity().z *= 0.8F * acceleration;
            entity.getVelocity().y = (float) ((double) entity.getVelocity().y - 0.02D);
            if (entity.hasHorizontalCollision() && entity.isFree(entity.getVelocity().x, entity.getVelocity().y + 0.6F - yo + yo, entity.getVelocity().z)) {
                entity.getVelocity().y = 0.3F;
            }
        } else if (entity.isFlying()) {
            entity.moveRelative(pos.x, pos.y, 0.1F);
            entity.move(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z);
            entity.getVelocity().x *= 0.91F * acceleration;
            entity.getVelocity().y *= 0.98F;
            entity.getVelocity().z *= 0.91F * acceleration;
            entity.getVelocity().y = (float)((double)entity.getVelocity().y - 0.08D);

            if (entity.isOnGround()) {
                entity.setFlyingState(false);
            }
        } else {
            entity.moveRelative(pos.x, pos.y, entity.isOnGround() ? 0.1F : 0.02F);
            entity.move(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z);
            entity.getVelocity().x *= 0.91F * acceleration;
            entity.getVelocity().y *= 0.98F;
            entity.getVelocity().z *= 0.91F * acceleration;
            entity.getVelocity().y = (float)((double)entity.getVelocity().y - 0.08D);
            if (entity.isOnGround()) {
                entity.getVelocity().x *= 0.6F;
                entity.getVelocity().z *= 0.6F;
            }
        }
    }
}
