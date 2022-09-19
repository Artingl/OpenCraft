package com.artingl.opencraft.World.Level.Listener;

import com.artingl.opencraft.Math.Vector3f;

public class LevelListenerEvent {

    private final Vector3f position;

    public LevelListenerEvent(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }
}
