package com.artingl.opencraft.World.EntityData;

public class UUID {

    private final String uuid;

    public static UUID fromNametag(Nametag name) {
        return new UUID(String.valueOf(name.getNametag()));
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
        return ((UUID)obj).getStringUUID().equals(uuid);
    }
}
