package com.artingl.opencraft.World.Ambient;

import com.artingl.opencraft.World.Level.LevelType;
import com.artingl.opencraft.World.Level.ClientLevel;

public class Ambient {

    private ClientLevel level;
    private Sky sky;

    public Ambient(ClientLevel level) {
        this.level = level;
        this.sky = new Sky();
    }

    public void update() {
        if (this.level.getLevelType() == LevelType.WORLD) {
            this.sky.update();
        }
    }

    public void tick() {
        if (this.level.getLevelType() == LevelType.HELL) {
            this.sky.tick();
        }
    }

}
