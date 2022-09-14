package com.artingl.opencraft.Math;

import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.World.Entity.EntityPlayer;

import java.util.Comparator;

public class Vector3fEntitySorter implements Comparator<Vector3f> {

    private final Entity entity;

    public Vector3fEntitySorter(EntityPlayer player) {
        this.entity = player;
    }


    @Override
    public int compare(Vector3f c0, Vector3f c1) {
        if (c0 == null || c1 == null)
            return 0;

        if (c0.distanceToEntity(entity) == c1.distanceToEntity(entity)) {
            return 0;
        }

        return c0.distanceToEntity(entity) < c1.distanceToEntity(entity) ? -1 : 1;
    }

}
