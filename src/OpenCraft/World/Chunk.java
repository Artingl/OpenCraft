package OpenCraft.World;

import OpenCraft.World.Block.Block;
import OpenCraft.World.Entity.Player;
import OpenCraft.Game.Rendering.VerticesBuffer;
import OpenCraft.Game.Rendering.BlockRenderer;
import OpenCraft.Game.phys.AABB;
import OpenCraft.OpenCraft;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class Chunk
{

    public static int CHUNK_UPDATES = 0;
    public static VerticesBuffer verticesBuffer = VerticesBuffer.instance;

    public boolean visible;
    public long dirtiedTime = 0;
    public AABB aabb;

    private int x; // Chunk x
    private int y; // Chunk y
    private int z; // Chunk z

    private int width; // Chunk width
    private int height; // Chunk height
    private int depth; // Chunk depth

    private boolean dirty; // Is chuck dirty
    private int chunksList; // GL list

    private Level level; // Level where is located current chunk

    public Chunk(Level level, int x, int y, int z, int w, int h, int d)
    {
        this.dirty = true;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = w;
        this.height = h;
        this.depth = d;
        this.level = level;
        this.aabb = new AABB((float)x, (float)y, (float)z, (float)w, (float)h, (float)d);

        this.chunksList = GL11.glGenLists(3);
    }

    public void prepare(int i)
    {
        dirty = false;
        CHUNK_UPDATES++;

        GL11.glNewList(this.chunksList + i, GL_COMPILE);
        verticesBuffer.begin();

        for(int x = this.x; x < this.width; ++x) {
            for(int y = this.y; y < this.height; ++y) {
                for(int z = this.z; z < this.depth; ++z) {
                    Block block = OpenCraft.getLevel().getBlock(x, y, z);
                    if (block.isSolid())
                    {
                        BlockRenderer.render(i, x, y, z, block);
                    }

                }
            }
        }

        verticesBuffer.end();
        GL11.glEndList();

    }

    public void prepare()
    {
        ++CHUNK_UPDATES;
        prepare(0);
        prepare(1);
        prepare(2);
        this.dirty = false;
    }

    public void render(int i)
    {
        glCallList(this.chunksList + i);
    }


    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        if (!this.dirty) {
            this.dirtiedTime = System.currentTimeMillis();
        }

        this.dirty = true;
    }

    public float distanceToSqr(Player player) {
        float xd = player.getX() - this.x;
        float yd = player.getY() - this.y;
        float zd = player.getZ() - this.z;
        return xd * xd + yd * yd + zd * zd;
    }

    public void reset() {
        this.dirty = true;

        for(int i = 0; i < 3; ++i) {
            GL11.glNewList(this.chunksList + i, 4864);
            GL11.glEndList();
        }

    }

}
