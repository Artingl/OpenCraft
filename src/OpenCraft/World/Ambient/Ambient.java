package OpenCraft.World.Ambient;

import OpenCraft.World.Level.Level;

public class Ambient {

    private Level level;
    private Sky sky;

    public Ambient(Level level) {
        this.level = level;
        this.sky = new Sky();
    }

    public void update() {
        if (this.level.getLevelType() == Level.LevelType.WORLD) {
            this.sky.update();
        }
    }

    public void tick() {
        if (this.level.getLevelType() == Level.LevelType.WORLD) {
            this.sky.tick();
        }
    }

}
