package OpenCraft.World.Chunk;

import OpenCraft.OpenCraft;
import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.LevelRenderer;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.World.Block.Block;
import OpenCraft.World.Entity.Entity;
import OpenCraft.math.Vector2i;
import OpenCraft.math.Vector3i;
import OpenCraft.phys.AABB;
import OpenCraft.phys.SimpleAABB;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Chunk
{
    public static int CHUNK_LAYERS = 64;

    private VerticesBuffer verticesBuffer = VerticesBuffer.instance;

    private Vector2i chunkListPosition;
    private Region chunkRegion;
    private AABB aabb;
    private ArrayList<Boolean> layersState;
    private ArrayList<Boolean> layersVisible;
    private int chunksList;

    public Chunk(Vector2i chunkListPosition)
    {
        this.chunkListPosition = chunkListPosition;
        this.layersState = new ArrayList<>();
        this.layersVisible = new ArrayList<>();
        this.chunkRegion = new Region(this);
        this.aabb = new AABB(
                chunkListPosition.x * 16, 0, chunkListPosition.y * 16,
                chunkListPosition.x * 16 + 16, 256, chunkListPosition.y * 16 + 16);

        for (int i = 0; i < CHUNK_LAYERS; i++)
            layersState.add(false);

        for (int i = 0; i < CHUNK_LAYERS; i++)
            layersVisible.add(false);

        this.chunksList = GL11.glGenLists(CHUNK_LAYERS);
    }

    public boolean buildLayer(int layer) {
        ++LevelRenderer.CHUNK_UPDATES;

        if (this.chunksList != -1)
            GL11.glDeleteLists(this.chunksList + layer, 1);

        layersState.set(layer, false);

        boolean isVisible = false;

        GL11.glNewList(this.chunksList + layer, GL11.GL_COMPILE);
        verticesBuffer.begin();

        Vector3i currentBlockPosition = new Vector3i(0, 0, 0);

        for (int y = layer * (256 / CHUNK_LAYERS); y < layer * (256 / CHUNK_LAYERS) + (256 / CHUNK_LAYERS); ++y) {
            currentBlockPosition.y = y;
            for (int x = chunkListPosition.x * 16; x < chunkListPosition.x * 16 + 16; ++x) {
                currentBlockPosition.x = x - chunkListPosition.x * 16;
                for (int z = chunkListPosition.y * 16; z < chunkListPosition.y * 16 + 16; ++z) {
                    currentBlockPosition.z = z - chunkListPosition.y * 16;
                    Block block = chunkRegion.getBlock(currentBlockPosition.x, currentBlockPosition.y, currentBlockPosition.z);

                    if (block.isVisible()) {
                        BlockRenderer.render(verticesBuffer, x, y, z, block);
                        isVisible = true;
                    }
                }
            }
        }

        layersVisible.set(layer, isVisible);
        layersState.set(layer, true);
        verticesBuffer.end();

        verticesBuffer.clear();
        GL11.glEndList();

        return isVisible;
    }

    public Region getRegion() {
        return chunkRegion;
    }

    public Vector2i getPosition() {
        return chunkListPosition;
    }

    public void render(int i) {
        GL11.glCallList(this.chunksList + i);
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

        this.chunkRegion.destroy();
        this.layersState.clear();
        this.layersVisible.clear();
        this.verticesBuffer.clear();

        this.verticesBuffer = null;
        this.chunkListPosition = null;
        this.chunkRegion = null;
        this.aabb = null;
        this.layersState = null;
    }

    public float distanceToEntity(Entity entity) {
        float xd = ((int)entity.getX() >> 4) - chunkListPosition.x;
        float zd = ((int)entity.getZ() >> 4) - chunkListPosition.y;
        return (float) Math.sqrt(xd * xd + zd * zd);
    }

    public boolean layerState(int layer) {
        return this.layersState.get(layer);
    }

    public boolean getLayersVisible(int layer) {
        return this.layersVisible.get(layer);
    }
}
