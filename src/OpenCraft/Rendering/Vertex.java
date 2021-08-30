package OpenCraft.Rendering;

public class Vertex
{

    public Vector3 position;
    public float u;
    public float v;

    public Vertex(float x, float y, float z, float u, float v)
    {
        this(new Vector3(x, y, z), u, v);
    }

    public Vertex remap(float u, float v)
    {
        return new Vertex(this.position, u, v);
    }

    public Vertex(Vector3 pos, float u, float v)
    {
        this.position = pos;
        this.u = u;
        this.v = v;
    }

}
