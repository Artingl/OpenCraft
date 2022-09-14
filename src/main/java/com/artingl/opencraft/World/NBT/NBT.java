package com.artingl.opencraft.World.NBT;


import com.artingl.opencraft.World.EntityData.UUID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NBT {

    public enum NBTFieldId {
        Entity, ENTITY_PLAYER, ITEM_SLOT
    }

    protected final NBTFieldId fieldId;

    public NBT(NBTFieldId id) {
        this.fieldId = id;
    }

    public int getId() {
        return fieldId.ordinal();
    }

    public byte[] getBytes() throws IOException {
        return null;
    }

    public void destroy() {
    }

    public void writeToStream(DataOutputStream dos) throws IOException {

    }

    public void readFromStream(DataInputStream inputStream) throws IOException {
    }

}
