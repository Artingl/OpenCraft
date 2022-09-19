package com.artingl.opencraft.World.Level.Listener;

import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.EntityData.EntityEvent;

public class LevelListenerEventEntityUpdate extends LevelListenerEvent {

    private final Entity entity;
    private final EntityEvent event;

    public LevelListenerEventEntityUpdate(Entity entity, EntityEvent event, Vector3f position) {
        super(position);
        this.entity = entity;
        this.event = event;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityEvent getEventType() {
        return event;
    }
}
