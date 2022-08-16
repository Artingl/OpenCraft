package com.artingl.opencraft.World;

import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.Math.Vector3i;

public class RayCast
{
    public static class RayResult extends Vector3i {

        public final boolean state;
        public RayResult(int x, int y, int z, boolean state) {
            super(x, y, z);
            this.state = state;
        }
    }

    public static RayResult[] rayCastToBlock(int dist, float rx, float ry, float x, float y, float z)
    {
        float rotX = -rx / 180 * 3.14159265359f;
        float rotY = ry / 180 * 3.14159265359f;
        float dx = (float)Math.sin(rotY);
        float dz = (float)-Math.cos(rotY);
        float dy =(float) Math.sin(rotX);
        float m = (float)Math.cos(rotX);
        float ox = x;
        float oy = y;
        float oz = z;
        dx *= m;
        dz *= m;

        dx /= 150;
        dy /= 150;
        dz /= 150;

        for (int i = 0; i < dist * 150; i++)
        {
            Block block = OpenCraft.getLevel().getBlock((int)(x), (int)(y), (int)(z));

            if (block != null)
            {
                if (block.isVisible() && !block.isLiquid())
                {
                    return new RayResult[]{
                            new RayResult((int)x,  (int)y,  (int)z, true),
                            new RayResult((int)ox, (int)oy, (int)oz, true)
                    };
                }
            }

            ox = x;
            oy = y;
            oz = z;
            x += dx;
            y += dy;
            z += dz;
        }

        return new RayResult[]{ new RayResult(-1, -1, -1, false) };
    }

}
