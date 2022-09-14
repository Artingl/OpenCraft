package com.artingl.opencraft.Multiplayer;

public class InternalServer extends Server {

    public InternalServer() {
        super("192.168.0.105", 65000);
    }

    public void create(int seed) {
        ServerSettings.seed = seed;
        super.create();
    }

}
