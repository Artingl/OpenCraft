package com.artingl.opencraft.World.Entity;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.MathUtils;
import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Multiplayer.Packet.PacketEntityUpdate;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.Multiplayer.SocketVectorArraylist;
import com.artingl.opencraft.Multiplayer.World.Entity.EntityMP;
import com.artingl.opencraft.Control.RenderInterface;
import com.artingl.opencraft.Control.Render.BlockRenderer;
import com.artingl.opencraft.Multiplayer.World.Gamemode.Spectator;
import com.artingl.opencraft.World.Entity.Models.Model;
import com.artingl.opencraft.World.EntityData.EntityEvent;
import com.artingl.opencraft.World.EntityData.Nametag;
import com.artingl.opencraft.World.EntityData.UUID;
import com.artingl.opencraft.World.Item.ItemSlot;
import com.artingl.opencraft.World.Level.Listener.LevelListener;
import com.artingl.opencraft.World.Level.Listener.LevelListenerEventEntityUpdate;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.Phys.AABB;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.World.NBT.EntityNBT;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class Entity implements Tick, RenderInterface
{

    public enum Types {
        PLAYER, MOB, BASE, DROP, PARTICLE
    }

    public static Class<?>[] ENTITIES = {
            EntityPlayer.class,
    };

    public static int ENTITIES_ID = 0;


    private boolean onGround = true;
    private boolean horizontalCollision = false;
    private boolean removed = false;

    private ClientLevel level;

    private EntityNBT nbt;
    private Model model;
    private AABB aabb;

    private float distanceWalked;
    private float prevDistanceWalked;

    protected Types entityType;

    private final int entityId;
    private int tickEvent = -1;
    private int renderEvent = -1;
    protected boolean isOnlineEntity;
    protected Server.Side side;
    private SocketVectorArraylist<Vector2i> positionQueue;

    public Entity(ClientLevel level) {
        this(level, Server.Side.CLIENT);
    }

    public Entity(ClientLevel level, Server.Side side) {
        this.entityId = ENTITIES_ID++;
        this.level = level;
        this.nbt = new EntityNBT(UUID.fromNametag(new Nametag("entity")));

        this.positionQueue = new SocketVectorArraylist<>();
        this.nbt.setPosition(new Vector3f(0, 0, 0));
        this.nbt.setRotation(new Vector2f(0, 0));
        float w = 0.3F;
        float h = 0.9F;
        this.aabb = new AABB(-w, -h, -w, w, h, w);
        this.nbt.setHearts(20);
        this.nbt.setMaxHearts(20);
        this.nbt.setDeadState(false);
        this.entityType = Types.BASE;
        this.side = side;

        if (side == Server.Side.CLIENT) {
            this.tickEvent = Opencraft.registerTickEvent(this);
            this.renderEvent = Opencraft.registerRenderEvent(this);
        }

        if (entityType != Types.PARTICLE && entityType != Types.DROP) {
            Opencraft.getLevelController().sendEvent(
                    LevelListener.Events.ENTITY_UPDATE,
                    level,
                    new LevelListenerEventEntityUpdate(this, EntityEvent.SPAWN, getPosition())
            );
        }
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    public Model getModel(Model model)
    {
        return model;
    }

    protected void remove()
    {
        removed = true;
    }

    @Override
    public void render() {
        if (Opencraft.showHitBoxes() && !this.equals(Opencraft.getPlayerEntity())) {
            float width = this.aabb.x0 - this.aabb.x1;
            float height = this.aabb.y0 - this.aabb.y1;
            float depth = this.aabb.z0 - this.aabb.z1;

            GL11.glColor4f(0.5f, 0.5f, 0, 0.3f);
            GL11.glBegin(GL11.GL_QUADS);
            BlockRenderer.renderLegacyTop(this.aabb.x0, this.aabb.y0, this.aabb.z0, width, height, depth);
            BlockRenderer.renderLegacyBottom(this.aabb.x0, this.aabb.y0, this.aabb.z0, width, height, depth);
            BlockRenderer.renderLegacyFront(this.aabb.x0, this.aabb.y0, this.aabb.z0, width, height, depth);
            BlockRenderer.renderLegacyBack(this.aabb.x0, this.aabb.y0, this.aabb.z0, width, height, depth);
            BlockRenderer.renderLegacyRight(this.aabb.x0, this.aabb.y0, this.aabb.z0, width, height, depth);
            BlockRenderer.renderLegacyLeft(this.aabb.x0, this.aabb.y0, this.aabb.z0, width, height, depth);
            GL11.glEnd();
            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    public void move(float xa, float ya, float za) {
        float xaOrg = xa;
        float yaOrg = ya;
        float zaOrg = za;
        List<AABB> aABBs = Opencraft.getLevel().getCubes(this.aabb.expand(xa, ya, za));

        if (this instanceof EntityPlayer) {
            if (((EntityPlayer)this).getGamemode().equals(Spectator.instance)) {
                aABBs.clear();
            }
        }

        int i;
        for(i = 0; i < aABBs.size(); ++i) {
            ya = ((AABB)aABBs.get(i)).clipYCollide(this.aabb, ya);
        }

        this.aabb.move(0.0F, ya, 0.0F);

        for(i = 0; i < aABBs.size(); ++i) {
            xa = ((AABB)aABBs.get(i)).clipXCollide(this.aabb, xa);
        }

        this.aabb.move(xa, 0.0F, 0.0F);

        for(i = 0; i < aABBs.size(); ++i) {
            za = ((AABB)aABBs.get(i)).clipZCollide(this.aabb, za);
        }

        if (!this.onGround && (yaOrg != ya && yaOrg < 0.0F)) {
            this.nbt.setFallValue(Math.round(this.nbt.getFallValue()));
            this.hitHandler(this.nbt.getFallValue());
            this.nbt.setFallValue(0);
        }
        else {
            this.nbt.setFallValue(this.nbt.getFallValue() - ya);
        }

        this.aabb.move(0.0F, 0.0F, za);
        this.horizontalCollision = xaOrg != xa || zaOrg != za;
        this.onGround = yaOrg != ya && yaOrg < 0.0F;

        Vector3f vel = getVelocity();

        if (xaOrg != xa) {
            vel.x = 0.0F;
        }

        if (yaOrg != ya) {
            vel.y = 0.0F;
        }

        if (zaOrg != za) {
            vel.z = 0.0F;
        }

        float xx = this.getPosition().x;
        float zz = this.getPosition().z;

        this.setVelocity(vel);
        this.nbt.setPosition(new Vector3f(
                (this.aabb.x0 + this.aabb.x1) / 2.0F,
                this.aabb.y0 + this.nbt.getHeightOffset(),
                (this.aabb.z0 + this.aabb.z1) / 2.0F
        ));

        xx = this.getPosition().x - xx;
        zz = this.getPosition().z - zz;

        this.distanceWalked = (float)((double)this.distanceWalked + Math.sqrt(xx * xx + zz * zz) * 0.6D);

        if (!this.getPosition().equals(this.getPrevPosition()) && isOnlineEntity) {
            if (this.positionQueue.isEmpty())
                this.positionQueue.add(new Vector2i(0, 0));
        }
    }

    public void moveRelative(float xa, float za, float speed) {
        int initial_xa = (int) xa;
        int initial_za = (int) za;

        float dist = xa * xa + za * za;
        if (!(dist < 0.01F)) {
            Vector2f rotation = this.getRotation();
            dist = speed / (float)Math.sqrt((double)dist);
            xa *= dist;
            za *= dist;
            float sin = (float)Math.sin(rotation.y / 180 * 3.14159265359);
            float cos = (float)Math.cos(rotation.y / 180 * 3.14159265359);
            this.getVelocity().x += (xa * cos - za * sin);
            this.getVelocity().z += (za * cos + xa * sin);

            if (isOnlineEntity)
            {
                this.positionQueue.add(new Vector2i(initial_xa, initial_za));

                if (entityType != Types.PARTICLE && entityType != Types.DROP) {
                    Opencraft.getLevelController().sendEvent(
                            LevelListener.Events.ENTITY_UPDATE,
                            level,
                            new LevelListenerEventEntityUpdate(this, EntityEvent.MOVEMENT, getPosition())
                    );
                }
            }
        }
    }

    @Override
    public void tick()
    {
        this.nbt.setPrevPosition(this.nbt.getPosition());
        this.nbt.setPrevRotation(this.nbt.getRotation());
        this.prevDistanceWalked = this.distanceWalked;

        if (isOnlineEntity) {
            if (!this.positionQueue.isEmpty()) {
                if (Opencraft.getClientConnection().isActive()) {
                    try {
                        new PacketEntityUpdate(null, Opencraft.getClientConnection()
                                .getConnection()).updateEntityPosition(this);
                    } catch (Exception e) {
                        Logger.exception("Unable to send player movement", e);
                    }
                }
            }

            this.sendNbtToServer();
        }
    }

    public void sendNbtToServer() {
        if (isOnlineEntity) {
            if (Opencraft.getClientConnection().isActive()) {
                try {
                    new PacketEntityUpdate(null, Opencraft.getClientConnection()
                            .getConnection()).sendMinorEntityNbt(this);
                } catch (Exception e) {
                    Logger.exception("Unable to send player movement", e);
                }
            }
        }
    }

    public SocketVectorArraylist<Vector2i> getPositionQueue() {
        return positionQueue;
    }

    public void setPositionQueue(SocketVectorArraylist<Vector2i> queue) {
        this.positionQueue.clear();
        this.positionQueue = queue;
    }

    public void clearPositionQueue() {
        this.positionQueue.clear();
    }

    public int getHearts() {
        return this.nbt.getHearts();
    }

    public void setHearts(int h) {
        this.nbt.setHearts(h);
    }

    public int getMaxHearts() {
        return this.nbt.getMaxHearts();
    }

    public void setMaxHearts(int h) {
        this.nbt.setMaxHearts(h);

        if (this.nbt.getHearts() > this.nbt.getMaxHearts()) {
            this.nbt.setHearts(this.nbt.getMaxHearts());
        }
    }

    public boolean hitHandler(float fallHeight) {
        if (fallHeight >= 4) {
            this.nbt.setHearts(this.nbt.getHearts() - 5);

            if (fallHeight >= 47) {
                this.nbt.setHearts(0);
            }
            else if (fallHeight >= 30) {
                this.nbt.setHearts(this.nbt.getHearts() - 17);
            }
            else if (fallHeight >= 23) {
                this.nbt.setHearts(this.nbt.getHearts() - 13);
            }
            else if (fallHeight >= 16) {
                this.nbt.setHearts(this.nbt.getHearts() - 9);
            }
            else if (fallHeight >= 10) {
                this.nbt.setHearts(this.nbt.getHearts() - 7);
            }

            if (this.getHearts() < 0) {
                this.die();
            }

            return true;
        }

        return false;
    }

    public void die() {
        this.nbt.setHearts(0);
        this.nbt.setDeadState(true);

        if (entityType != Types.PARTICLE && entityType != Types.DROP) {
            Opencraft.getLevelController().sendEvent(
                    LevelListener.Events.ENTITY_UPDATE,
                    level,
                    new LevelListenerEventEntityUpdate(this, EntityEvent.DIE, getPosition())
            );
        }
    }

    public boolean isDead() {
        return this.nbt.isDead();
    }

    public void respawn() {
        this.nbt.setDeadState(false);
        this.teleportToSpawnPoint();
        this.setHearts(this.getMaxHearts());
        this.setVelocity(new Vector3f(0, 0, 0));
        this.nbt.setRotation(new Vector2f(0, 0));
        this.nbt.setFallValue(0);

        if (entityType != Types.PARTICLE && entityType != Types.DROP) {
            Opencraft.getLevelController().sendEvent(
                    LevelListener.Events.ENTITY_UPDATE,
                    level,
                    new LevelListenerEventEntityUpdate(this, EntityEvent.RESPAWN, getPosition())
            );
        }
    }

    public boolean isInWater() {
        return Opencraft.getLevel().containsLiquid(this.aabb.grow(0.0F, -0.4F, 0.0F));
    }

    public boolean isFree(float xa, float ya, float za) {
        AABB box = this.aabb.cloneMove(xa, ya, za);
        List<AABB> aABBs = Opencraft.getLevel().getCubes(box);
        if (aABBs.size() > 0) {
            return false;
        } else {
            return !Opencraft.getLevel().containsLiquid(box);
        }
    }

    public void teleportToSpawnPoint()
    {
        setPosition(this.nbt.getSpawnpoint());
    }

    public Vector3f getSpawnpoint() {
        return this.nbt.getSpawnpoint();
    }

    public void setSpawnpoint(Vector3f pos)
    {
        this.nbt.setSpawnpoint(pos);
    }

    public Vector3f getPosition() {
        return this.nbt.getPosition();
    }

    public void setPosition(Vector3f pos) {
        this.nbt.setPosition(pos);
        this.nbt.setFallValue(0);
        float w = this.nbt.getSize().x / 2.0F;
        float h = this.nbt.getSize().y / 2.0F;
        this.aabb = new AABB(pos.x - w, pos.y - h, pos.z - w, pos.x + w, pos.y + h, pos.z + w);
    }

    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x, y, z));
    }

    public void setPrevPosition(float x, float y, float z) {
        setPrevPosition(new Vector3f(x, y, z));
    }

    public void setRotation(float x, float y) {
        setRotation(new Vector2f(x, y));
    }

    public void setPrevRotation(float x, float y) {
        setPrevRotation(new Vector2f(x, y));
    }

    public Vector3f getPrevPosition() {
        return this.nbt.getPrevPosition();
    }

    public void setPrevPosition(Vector3f prevPosition) {
        this.nbt.setPrevPosition(prevPosition);
    }

    public Vector2f getRotation() {
        return this.nbt.getRotation();
    }

    public void setRotation(Vector2f rotation) {
        this.nbt.setRotation(rotation);
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entity))
            return false;

        return ((Entity)obj).getEntityId() == this.entityId;
    }

    public void destroy() {
        if (this.tickEvent != -1) Opencraft.unregisterTickEvent(this.tickEvent);
        if (this.renderEvent != -1) Opencraft.unregisterRenderEvent(this.renderEvent);

        this.tickEvent = -1;
        this.renderEvent = -1;

        this.positionQueue.clear();
    }

    public float getHeightOffset() {
        return this.nbt.getHeightOffset();
    }

    public void setHeightOffset(float heightOffset) {
        this.nbt.setHeightOffset(heightOffset);
    }

    public boolean hasInventory() {
        return false;
    }

    public void setLevel(ClientLevel level) {
        this.level = level;
    }

    public Nametag getNameTag() {
        return this.nbt.getNameTag();
    }

    public UUID getUUID() {
        return this.nbt.getUUID();
    }

    public void setNameTag(Nametag nameTag) {
        this.nbt.setNameTag(nameTag);
        this.nbt.setUUID(UUID.fromNametag(this.nbt.getNameTag()));
    }

    public EntityNBT getNbt() {
        return nbt;
    }

    public AABB getAABB() {
        return aabb;
    }

    public void setSize(float w, float h) {
        this.nbt.setSize(new Vector2f(w, h));
    }

    public Vector2f getSize() {
        return this.nbt.getSize();
    }

    public Model getModel() {
        return model;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isRemoved() {
        return removed;
    }

    public boolean hasHorizontalCollision() {
        return horizontalCollision;
    }

    public Vector3f getVelocity() {
        return this.nbt.getVelocity();
    }

    public void setVelocity(Vector3f velocity) {
        this.nbt.setVelocity(velocity);
    }

    public static Entity getDefaultEntityByType(int entityType) {
        try {
            for (Class<?> c : ENTITIES) {
                Entity e = (Entity) c.getConstructor().newInstance();
                if (e.getEntityType().ordinal() == entityType) {
                    return e;
                }
            }
        } catch (Exception e) {
            Logger.exception("Error getting entity", e);
        }

        return null;
    }

    public Types getEntityType() {
        return entityType;
    }

    public void setNbt(EntityNBT nbt) {
        this.nbt = nbt;

        setPosition(getPosition());
        setPrevPosition(getPrevPosition());
        setRotation(getRotation());
    }

    public Vector2f getPrevRotation() {
        return this.nbt.getPrevRotation();
    }

    public void setPrevRotation(Vector2f prevRotation) {
        this.nbt.setPrevRotation(prevRotation);
    }

    public float getDistanceWalked() {
        return distanceWalked;
    }

    public float getPrevDistanceWalked() {
        return prevDistanceWalked;
    }

    public void setDistanceWalked(float distanceWalked) {
        this.distanceWalked = distanceWalked;
    }

    public void setPrevDistanceWalked(float prevDistanceWalked) {
        this.prevDistanceWalked = prevDistanceWalked;
    }

    public ItemSlot[] getInventory() {
        return this.nbt.getInventory();
    }

    public void setInventory(ItemSlot[] inventory) {
        this.nbt.setInventory(inventory);
    }

    public int getInventorySelectedSlot() {
        return this.nbt.getSelectedSlot();
    }

    public void setInventorySelectedSlot(int slot) {
        this.nbt.setSelectedSlot(slot);
    }

    public ItemSlot getInventoryItem(int i)
    {
        ItemSlot[] inventory = getInventory();

        if (i < 0 || i > inventory.length) return null;
        if (inventory[i] == null) return null;
        return inventory[i];
    }

    public ItemSlot getCurrentItem()
    {
        if (getInventory()[this.nbt.getSelectedSlot()] == null) return null;
        return getInventory()[this.nbt.getSelectedSlot()];
    }

    public boolean appendInventory(ItemSlot item) {
        ItemSlot[] inventory = getInventory();

        for (ItemSlot itemInventory : inventory) {
            if (itemInventory == null) continue;
            if (itemInventory.equals(item) &&
                    itemInventory.getAmount() + item.getAmount() <= itemInventory.getMaxStackAmount()) {
                itemInventory.setAmount(itemInventory.getAmount() + item.getAmount());
                this.nbt.setInventory(inventory);
                return true;
            }
        }

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = item;
                this.nbt.setInventory(inventory);
                return true;
            }
        }

        return false;
    }

    public void decreaseSlot(int selected) {
        ItemSlot[] inventory = getInventory();

        if (inventory[selected] == null) return;
        inventory[selected].setAmount(inventory[selected].getAmount()-1);
        if (inventory[selected].getAmount() <= 0) inventory[selected] = null;

        this.nbt.setInventory(inventory);
    }

    public void increaseSlot(int selected) {
        ItemSlot[] inventory = getInventory();

        if (inventory[selected] == null) return;
        inventory[selected].setAmount(inventory[selected].getAmount()+1);

        this.nbt.setInventory(inventory);
    }


    @Override
    public String toString() {
        return "Entity{uuid=" + getUUID() + ", nametag=" + getNameTag() + ", position=" + getPosition() + ", rotation=" + getRotation() + ", type=" + entityType + "}";
    }

    public EntityMP convertToMP() {
        EntityMP mp = EntityMP.getDefaultEntityByType(entityType.ordinal());
        mp.setNbt(nbt);
        return mp;
    }

    public boolean insideRenderDistance(Entity entity) {
        return MathUtils.calculateLength(entity.getPosition(), getPosition()) < (Opencraft.getRenderDistance() * 16);
    }

    public boolean isFlying() {
        return this.nbt.isFlying();
    }

    public void setFlyingState(boolean flying) {
        this.nbt.setFlyingState(flying);
    }

    public float getAcceleration() {
        return this.nbt.getAcceleration();
    }

    public void setAcceleration(float acceleration) {
        this.nbt.setAcceleration(acceleration);
    }

    public ClientLevel getLevel() {
        return level;
    }
}
