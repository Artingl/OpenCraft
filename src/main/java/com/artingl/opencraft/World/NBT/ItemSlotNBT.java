package com.artingl.opencraft.World.NBT;

import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Item.Item;
import com.artingl.opencraft.World.Item.ItemSlot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ItemSlotNBT extends NBT {

    private Item item;
    private Block block;

    private int maxAmount;
    private int amount;
    private ItemSlot.ItemType itemType;

    public ItemSlotNBT() {
        super(NBT.NBTFieldId.ITEM_SLOT);
    }

    public ItemSlot.ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemSlot.ItemType itemType) {
        this.itemType = itemType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public void writeToStream(DataOutputStream dos) throws IOException {
        dos.writeInt(maxAmount);
        dos.writeInt(amount);
        dos.writeInt(itemType.ordinal());

        if (itemType == ItemSlot.ItemType.BLOCK) {
            dos.writeShort(block.packToShort());
        }
    }

    @Override
    public void readFromStream(DataInputStream inputStream) throws IOException {
        maxAmount = inputStream.readInt();
        amount = inputStream.readInt();
        itemType = inputStream.readInt() == ItemSlot.ItemType.BLOCK.ordinal() ? ItemSlot.ItemType.BLOCK : ItemSlot.ItemType.ITEM;

        if (itemType == ItemSlot.ItemType.BLOCK) {
            block = Block.unpackFromShort(inputStream.readShort());
        }
    }
}
