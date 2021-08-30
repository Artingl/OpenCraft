package OpenCraft.World;

import OpenCraft.World.Block.Block;
import OpenCraft.World.Entity.Player;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.phys.AABB;
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

    public boolean haveToRerender = false;

    private int x; // Chunk x
    private int y; // Chunk y
    private int z; // Chunk z

    private int width; // Chunk width
    private int height; // Chunk height
    private int depth; // Chunk depth

    private boolean dirty; // Is chuck dirty
    private int chunksList = -1; // GL list

    private Level level; // Level where is located current chunk
    private byte[] blocks;

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
        this.blocks = new byte[16 * 16 * 16];
    }

    public void prepare(int i)
    {
        CHUNK_UPDATES++;

        GL11.glNewList(this.chunksList + i, GL_COMPILE);
        verticesBuffer.begin();

        int ii = 0;
        for(int x = this.x; x < this.width; ++x) {
            for(int y = this.y; y < this.height; ++y) {
                for(int z = this.z; z < this.depth; ++z) {
                    Block block = OpenCraft.getLevel().getBlock(x, y, z);
                    Block block1 = OpenCraft.getLevel().getBlock(x + 1, y, z);
                    Block block2 = OpenCraft.getLevel().getBlock(x, y + 1, z);
                    Block block3 = OpenCraft.getLevel().getBlock(x, y, z + 1);
                    Block block4 = OpenCraft.getLevel().getBlock(x - 1, y, z);
                    Block block5 = OpenCraft.getLevel().getBlock(x, y - 1, z);
                    Block block6 = OpenCraft.getLevel().getBlock(x, y, z - 1);
                    if (block == null || block1 == null || block2 == null || block3 == null || block4 == null
                     || block5 == null || block6 == null)
                    {
                        haveToRerender = true;
                    }
                    else {
                        if (block.isSolid())
                        {
                            BlockRenderer.render(i, x, y, z, block);
                        }

                        if (i == 0) blocks[ii] = (byte) block.getIdInt();
                        ii++;
                    }
                }
            }
        }

        verticesBuffer.end();
        GL11.glEndList();
    }

    public void prepare()
    {
        if (this.chunksList == -1) this.chunksList = GL11.glGenLists(3);
        ++CHUNK_UPDATES;
        prepare(0);
        prepare(1);
        prepare(2);
        this.dirty = false;//result0 && result1 && result2;
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
        this.blocks = new byte[16 * 16 * 16];
        this.dirty = true;

        for(int i = 0; i < 3; ++i) {
            GL11.glNewList(this.chunksList + i, 4864);
            GL11.glEndList();
        }

    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getDepth()
    {
        return depth;
    }

    public byte[] getBlocks() {
        return blocks;
    }
}
