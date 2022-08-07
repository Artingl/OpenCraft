package OpenCraft.math;

public class Vector3i
{

    public int x;
    public int y;
    public int z;

    public Vector3i(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i mult(Vector3i v, int c)
    {
        return new Vector3i(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c, this.z + (v.z - this.z) * c);
    }

    public Vector3i addX(int i) {
        return new Vector3i(x + i, y, z);
    }

    public Vector3i addY(int i) {
        return new Vector3i(x, y + i, z);
    }

    public Vector3i addZ(int i) {
        return new Vector3i(x, y, z + i);
    }
}