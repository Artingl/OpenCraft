package OpenCraft.World;

import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;

public class RayCast
{

    public static float[][] rayCastToBlock(int dist, float rx, float ry, float x, float y, float z)
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
                if (block.isSolid() && !block.isLiquid())
                {
                    return new float[][]{
                            {1},
                            {(int)(x),  (int)(y),  (int)(z)},
                            {ox, oy, oz}
                    };
                }
            }

            ox = (int)(x);
            oy = (int)(y);
            oz = (int)(z);
            x += dx;
            y += dy;
            z += dz;
        }

        return new float[][]{{0}};
    }

}
