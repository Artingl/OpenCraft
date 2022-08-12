package OpenCraft.math;

public class Vector2f
{

    public float x;
    public float y;

    public Vector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2f mult(Vector2f v, float c)
    {
        return new Vector2f(this.x + (v.x - this.x) * c, this.y + (v.y - this.y) * c);
    }

    public Vector2f addX(float i) {
        return new Vector2f(x + i, y);
    }

    public Vector2f addY(float i) {
        return new Vector2f(x, y + i);
    }

    @Override
    public boolean equals(Object obj) {
        Vector2f v3 = (Vector2f) obj;
        return x ==  v3.x && y == v3.y;
    }
}
