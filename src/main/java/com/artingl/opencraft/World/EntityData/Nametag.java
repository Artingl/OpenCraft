package com.artingl.opencraft.World.EntityData;

public class Nametag {

    private final String nametag;

    public Nametag(String nametag) {
        this.nametag = nametag;
    }

    public String getNametag() {
        return nametag;
    }

    @Override
    public String toString() {
        return nametag;
    }
}
