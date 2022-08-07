package OpenCraft.math;

public class Vector2i
{

    public int x;
    public int y;

    public Vector2i(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2i mult(Vector2i v, int c)
    {
        return new Vector2i(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c);
    }


    public Vector2i addX(int i) {
        return new Vector2i(x + i, y);
    }

    public Vector2i addY(int i) {
        return new Vector2i(x, y + i);
    }
}
