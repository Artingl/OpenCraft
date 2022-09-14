package com.artingl.opencraft.World.NBT;

import com.artingl.opencraft.Math.Vector2f;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.World.EntityData.Nametag;
import com.artingl.opencraft.World.EntityData.UUID;
import com.artingl.opencraft.World.Item.ItemSlot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityNBT extends NBT {

    private boolean isFlying;

    private Vector3f velocity;
    private Vector3f position;
    private Vector3f prevPosition;
    private Vector3f spawnpoint;
    private Vector2f rotation;
    private Vector2f prevRotation;
    private int selectedSlot;

    private Vector2f size;
    private float heightOffset;
    private float fallValue;
    private Nametag nameTag;
    private UUID uuid;
    private int hearts;
    private int maxHearts;
    private boolean dead;
    private float acceleration;

    private ItemSlot[] inventory;

    public EntityNBT(UUID uuid) {
        super(NBTFieldId.Entity);
        this.position = new Vector3f(0, 0, 0);
        this.prevPosition = new Vector3f(0, 0, 0);
        this.spawnpoint = new Vector3f(0, 0, 0);
        this.velocity = new Vector3f(0, 0, 0);
        this.rotation = new Vector2f(0, 0);
        this.prevRotation = new Vector2f(0, 0);
        this.size = new Vector2f(0.6f, 1.8f);
        this.inventory = new ItemSlot[36];
        this.selectedSlot = 0;
        this.fallValue = 0;
        this.nameTag = new Nametag("");
        this.uuid = uuid;
        this.isFlying = false;
        this.acceleration = 1f;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlyingState(boolean flying) {
        isFlying = flying;
    }

    public Vector3f getPosition() {
        return position;
    }

    public ItemSlot[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemSlot[] inventory) {
        this.inventory = inventory;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPrevPosition() {
        return prevPosition;
    }

    public void setPrevPosition(Vector3f prevPosition) {
        this.prevPosition = prevPosition;
    }

    public float getHeightOffset() {
        return heightOffset;
    }

    public void setHeightOffset(float heightOffset) {
        this.heightOffset = heightOffset;
    }

    public Vector3f getSpawnpoint() {
        return spawnpoint;
    }

    public void setSpawnpoint(Vector3f spawnpoint) {
        this.spawnpoint = spawnpoint;
    }

    public Nametag getNameTag() {
        return nameTag;
    }

    public void setNameTag(Nametag nameTag) {
        this.nameTag = nameTag;
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public void setRotation(Vector2f rotation) {
        this.rotation = rotation;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }

    public int getMaxHearts() {
        return maxHearts;
    }

    public void setMaxHearts(int maxHearts) {
        this.maxHearts = maxHearts;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDeadState(boolean dead) {
        this.dead = dead;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getFallValue() {
        return fallValue;
    }

    public void setFallValue(float fallValue) {
        this.fallValue = fallValue;
    }

    public Vector2f getPrevRotation() {
        return prevRotation;
    }

    public void setPrevRotation(Vector2f prevRotation) {
        this.prevRotation = prevRotation;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    @Override
    public void destroy() {
        this.inventory = null;
    }

    public void writeInventoryToStream(DataOutputStream dos) throws IOException {
        dos.writeInt(this.inventory.length);
        for (ItemSlot slot: this.inventory) {
            if (slot == null) {
                dos.writeInt(0);
                continue;
            }

            dos.writeInt(1);
            slot.getNbt().writeToStream(dos);
        }
    }

    public void readInventoryFromStream(DataInputStream inputStream) throws IOException {
        int inventorySlots = inputStream.readInt();
        this.inventory = new ItemSlot[inventorySlots];

        for (int i = 0; i < inventorySlots; i++) {
            if (inputStream.readInt() == 0) {
                this.inventory[i] = null;
                continue;
            }

            ItemSlotNBT nbt = new ItemSlotNBT();
            nbt.readFromStream(inputStream);
            this.inventory[i] = new ItemSlot(nbt);
        }
    }

    public void writeToStream(DataOutputStream dos) throws IOException {
        dos.writeFloat(velocity.x); dos.writeFloat(velocity.y); dos.writeFloat(velocity.z);
        dos.writeFloat(position.x); dos.writeFloat(position.y); dos.writeFloat(position.z);
        dos.writeFloat(prevPosition.x); dos.writeFloat(prevPosition.y); dos.writeFloat(prevPosition.z);
        dos.writeFloat(spawnpoint.x); dos.writeFloat(spawnpoint.y); dos.writeFloat(spawnpoint.z);
        dos.writeFloat(rotation.x); dos.writeFloat(rotation.y);
        dos.writeFloat(prevRotation.x); dos.writeFloat(prevRotation.y);
        dos.writeFloat(size.x); dos.writeFloat(size.y);

        dos.writeFloat(acceleration);
        dos.writeFloat(fallValue);
        dos.writeFloat(heightOffset);
        dos.writeUTF(nameTag.getNametag());
        dos.writeUTF(uuid.getStringUUID());
        dos.writeInt(hearts);
        dos.writeInt(maxHearts);
        dos.writeBoolean(dead);
        dos.writeInt(selectedSlot);
        dos.writeBoolean(isFlying);

        writeInventoryToStream(dos);
    }

    public void readFromStream(DataInputStream inputStream) throws IOException {
        velocity.x = inputStream.readFloat(); velocity.y = inputStream.readFloat(); velocity.z = inputStream.readFloat();
        position.x = inputStream.readFloat(); position.y = inputStream.readFloat(); position.z = inputStream.readFloat();
        prevPosition.x = inputStream.readFloat(); prevPosition.y = inputStream.readFloat(); prevPosition.z = inputStream.readFloat();
        spawnpoint.x = inputStream.readFloat(); spawnpoint.y = inputStream.readFloat(); spawnpoint.z = inputStream.readFloat();
        rotation.x = inputStream.readFloat(); rotation.y = inputStream.readFloat();
        prevRotation.x = inputStream.readFloat(); prevRotation.y = inputStream.readFloat();
        size.x = inputStream.readFloat(); size.y = inputStream.readFloat();

        acceleration = inputStream.readFloat();
        fallValue = inputStream.readFloat();
        heightOffset = inputStream.readFloat();
        nameTag = new Nametag(inputStream.readUTF());
        uuid = new UUID(inputStream.readUTF());
        hearts = inputStream.readInt();
        maxHearts = inputStream.readInt();
        dead = inputStream.readBoolean();
        selectedSlot = inputStream.readInt();
        isFlying = inputStream.readBoolean();

        readInventoryFromStream(inputStream);
    }
}
