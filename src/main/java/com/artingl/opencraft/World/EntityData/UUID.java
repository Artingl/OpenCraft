package com.artingl.opencraft.World.EntityData;

import com.artingl.opencraft.Math.Vector2i;

public class UUID {

    public static final UUID empty = new UUID("");
    private final String uuid;

    public static UUID fromNametag(Nametag name) {
        return new UUID(name.getNametag());
    }

    public UUID(String uuid) {
        this.uuid = uuid;
    }

    public String getStringUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return "UUID{value=" + uuid + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UUID))
            return false;

        return ((UUID)obj).getStringUUID().equals(uuid);
    }
}
