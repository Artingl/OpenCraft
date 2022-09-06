package com.artingl.opencraft.World.EntityData;


import java.io.DataInputStream;
import java.io.IOException;

public class NBT {

    public enum NBTFieldId {
        Entity,
    }

    protected final NBTFieldId fieldId;

    public NBT(NBTFieldId id) {
        this.fieldId = id;
    }

    public int getId() {
        return fieldId.hashCode();
    }

    public byte[] getBytes() throws IOException {
        return null;
    }

    public void destroy() {
    }

    public void loadFromStream(DataInputStream inputStream) throws IOException {
    }
}
