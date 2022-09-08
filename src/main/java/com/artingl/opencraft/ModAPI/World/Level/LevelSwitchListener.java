package com.artingl.opencraft.ModAPI.World.Level;

import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;

@FunctionalInterface
public interface LevelSwitchListener {
    void callback(ClientLevel level, EntityPlayer player);
}
