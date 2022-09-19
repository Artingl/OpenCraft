package com.artingl.opencraft.ModAPI.World.Level;

import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.EntityData.EntityEvent;
import com.artingl.opencraft.World.Level.ClientLevel;

@FunctionalInterface
public interface LevelEntityUpdateListener {

    void callback(ClientLevel level, Entity entity, EntityEvent event);

}
