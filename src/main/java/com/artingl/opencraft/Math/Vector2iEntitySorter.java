package com.artingl.opencraft.Math;

import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Entity.EntityPlayer;

import java.util.Comparator;

public class Vector2iEntitySorter implements Comparator<Vector2i> {

    private final Entity entity;

    public Vector2iEntitySorter(EntityPlayer player) {
        this.entity = player;
    }

    @Override
    public int compare(Vector2i c0, Vector2i c1) {
        if (c0 == null || c1 == null)
            return 0;

        if (c0.distanceToEntity(entity) == c1.distanceToEntity(entity)) {
            return 0;
        }

        return c0.distanceToEntity(entity) < c1.distanceToEntity(entity) ? -1 : 1;
    }

}
