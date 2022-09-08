package com.artingl.opencraft.World.Ambient;

import com.artingl.opencraft.World.Level.ClientLevel;

public class Ambient {

    private ClientLevel level;
    private Sky sky;

    public Ambient(ClientLevel level) {
        this.level = level;
        this.sky = new Sky();
    }

    public void update() {
        this.sky.update();
    }

    public void tick() {
    }

}
