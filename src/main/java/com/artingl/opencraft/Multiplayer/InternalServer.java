package com.artingl.opencraft.Multiplayer;

public class InternalServer extends Server {

    public InternalServer() {
        super("127.0.0.1", 12342);
    }

    public void create(int seed) {
        ServerSettings.seed = seed;
        super.create();
    }

}
