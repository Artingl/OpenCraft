package com.artingl.opencraft.Multiplayer.World.Entity;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Multiplayer.Server;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Level.ClientLevel;

public class EntityMP extends Entity {

    public static Class<?>[] ENTITIES = {
            EntityPlayerMP.class,
    };

    public EntityMP(ClientLevel level) {
        super(level, Server.Side.SERVER);
    }

    public static EntityMP getDefaultEntityByType(int entityType) {
        try {
            for (Class<?> c : ENTITIES) {
                EntityMP e = (EntityMP) c.getConstructor().newInstance();
                if (e.getEntityType().hashCode() == entityType) {
                    return e;
                }
            }
        } catch (Exception e) {
            Logger.exception("Error getting entity", e);
        }

        return null;
    }

    @Override
    public void render() {
        super.render();
    }
}
