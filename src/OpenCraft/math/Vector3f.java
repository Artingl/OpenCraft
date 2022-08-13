package OpenCraft.math;

public class Vector3f
{

    public float x;
    public float y;
    public float z;

    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f mult(Vector3f v, float c)
    {
        return new Vector3f(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c, this.z + (v.z - this.z) * c);
    }

    public Vector3f addX(float i) {
        return new Vector3f(x + i, y, z);
    }

    public Vector3f addY(float i) {
        return new Vector3f(x, y + i, z);
    }

    public Vector3f addZ(float i) {
        return new Vector3f(x, y, z + i);
    }

    @Override
    public boolean equals(Object obj) {
        Vector3f v3 = (Vector3f) obj;
        return x ==  v3.x && y == v3.y && z == v3.z;
    }

    @Override
    public String toString() {
        return "Vector3f{x=" + x + ", y=" + y + ", z=" + z + "}";
    }
}
