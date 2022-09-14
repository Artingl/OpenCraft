package com.artingl.opencraft.World.Item;

import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.NBT.ItemSlotNBT;
import com.artingl.opencraft.World.NBT.NBT;

public class ItemSlot {

    public enum ItemType {
        ITEM, BLOCK
    }

    private final ItemSlotNBT nbt;

    public ItemSlot(Block block, int amount) {
        this.nbt = new ItemSlotNBT();

        this.nbt.setAmount(amount);
        this.nbt.setItemType(ItemType.BLOCK);
        this.nbt.setBlock(block);
        this.nbt.setItem(null);
    }

    public ItemSlot(Item item, int amount) {
        this.nbt = new ItemSlotNBT();

        this.nbt.setAmount(amount);
        this.nbt.setItemType(ItemType.ITEM);
        this.nbt.setBlock(null);
        this.nbt.setItem(item);
    }

    public ItemSlot(ItemSlotNBT nbt) {
        this.nbt = nbt;
    }

    public int getAmount() {
        return this.nbt.getAmount();
    }

    public void setAmount(int value) {

        this.nbt.setAmount(value);
    }

    public ItemType getItemType() {
        return this.nbt.getItemType();
    }

    public Block getBlock() {
        return this.nbt.getBlock();
    }

    public Item getItem() {
        return this.nbt.getItem();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemSlot))
            return false;

        if (this.nbt.getItemType() == ItemType.BLOCK) {
            return getBlock().equals(((ItemSlot)obj).getBlock());
        }

        return false;
    }

    @Override
    public String toString() {
        return "ItemSlot{type=" + getItemType() + ", amount=" + getAmount() + "}";
    }

    public ItemSlotNBT getNbt() {
        return nbt;
    }

    public int getMaxStackAmount() {
        return this.nbt.getMaxAmount();
    }

}
