package OpenCraft.World;

import OpenCraft.OpenCraft;
import OpenCraft.World.Block.Block;
import OpenCraft.math.Vector3i;

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
                            new RayResult((int) Math.floor(x),  (int)Math.floor(y),  (int)Math.floor(z), true),
                            new RayResult((int) Math.floor(ox), (int)Math.floor(oy), (int)Math.floor(oz), true)
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

        return new RayResult[]{ new RayResult(-1, -1, -1, false) };
    }

}
