package OpenCraft.World;

import OpenCraft.OpenCraft;
import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.LevelRenderer;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.World.Block.Block;
import OpenCraft.math.Vector2i;
import OpenCraft.math.Vector3i;
import OpenCraft.phys.AABB;
import OpenCraft.phys.SimpleAABB;
import org.lwjgl.opengl.GL11;

public class Chunk
{
    public static int CHUNK_LAYERS = 4;

    private VerticesBuffer verticesBuffer = VerticesBuffer.instance;

    private Vector2i chunkListPosition;
    private Region chunkRegion;
    private AABB aabb;

    private boolean dirty;
    private int chunksList = -1;

    public Chunk(Vector2i chunkListPosition)
    {
        this.chunkListPosition = chunkListPosition;
        this.chunkRegion = new Region(this);
        this.aabb = new AABB(
                chunkListPosition.x * 16, 0, chunkListPosition.y * 16,
                chunkListPosition.x * 16 + 16, 256, chunkListPosition.y * 16 + 16);

        this.dirty = true;
        this.prepare();
    }

    public void prepare() {
        ++LevelRenderer.CHUNK_UPDATES;

        if (this.chunksList != -1)
            GL11.glDeleteLists(this.chunksList, CHUNK_LAYERS);

        this.chunksList = GL11.glGenLists(CHUNK_LAYERS);

        Vector3i currentBlockPosition = new Vector3i(0, 0, 0);

        for (int state = 0; state < CHUNK_LAYERS; state++) {
            GL11.glNewList(this.chunksList + state, GL11.GL_COMPILE);
            verticesBuffer.begin();

            for (int y = state * (256 / CHUNK_LAYERS); y < state * (256 / CHUNK_LAYERS) + (256 / CHUNK_LAYERS); ++y) {
                currentBlockPosition.y = y;
                for (int x = chunkListPosition.x * 16; x < chunkListPosition.x * 16 + 16; ++x) {
                    currentBlockPosition.x = x;
                    for (int z = chunkListPosition.y * 16; z < chunkListPosition.y * 16 + 16; ++z) {
                        currentBlockPosition.z = z;
                        Block block = OpenCraft.getLevel().getBlock(currentBlockPosition);

                        if (block.isVisible()) {
                            BlockRenderer.render(verticesBuffer, x, y, z, block);
                        }
                    }
                }
            }

            verticesBuffer.end();
            GL11.glEndList();
            verticesBuffer.clear();
        }

        this.dirty = false;
    }

    public Region getRegion() {
        return chunkRegion;
    }

    public Vector2i getPosition() {
        // todo: return position according to current player position.
        //       update aabb when position changed
        return chunkListPosition;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void render(int i) {
        GL11.glCallList(this.chunksList + i);
    }

    public void setDirty(boolean b) {
        this.dirty = b;
    }

    public AABB getAABB() {
        return this.aabb;
    }

    public SimpleAABB getSimpleAABB(int layer) {
        return new SimpleAABB(
                this.chunkListPosition.x * 16, layer * (256f / CHUNK_LAYERS), this.chunkListPosition.y * 16,
                this.chunkListPosition.x * 16 + 16, layer * (256f / CHUNK_LAYERS) + (256f / CHUNK_LAYERS), this.chunkListPosition.y * 16 + 16);
    }

    public void destroy() {
        OpenCraft.runInGLContext(() -> {
            if (this.chunksList != -1)
                GL11.glDeleteLists(this.chunksList, CHUNK_LAYERS);
        });

        this.verticesBuffer.clear();

        this.verticesBuffer = null;
        this.aabb = null;
        this.chunkRegion = null;
        this.chunkListPosition = null;
    }
}
