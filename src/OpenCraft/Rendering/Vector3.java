package OpenCraft.Rendering;

public class Vector3
{

    public float x;
    public float y;
    public float z;

    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 mult(Vector3 v, float c)
    {
        return new Vector3(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c, this.z + (v.z - this.z) * c);
    }

}
