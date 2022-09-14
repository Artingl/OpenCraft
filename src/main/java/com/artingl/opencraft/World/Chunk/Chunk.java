package com.artingl.opencraft.World.Chunk;

import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Control.World.BlockRenderer;
import com.artingl.opencraft.Control.World.LevelRenderer;
import com.artingl.opencraft.Control.Game.VerticesBuffer;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Phys.AABB;
import com.artingl.opencraft.Phys.SimpleAABB;
import com.artingl.opencraft.World.Level.ClientLevel;
import org.lwjgl.opengl.GL11;

public class Chunk
{
    public static final int CHUNK_LAYERS = 16;

    private VerticesBuffer verticesBuffer = VerticesBuffer.getGlobalInstance();
    private Vector2i chunkListPosition;
    private Region chunkRegion;
    private AABB aabb;
    private boolean[] layersState;
    private boolean[] layersVisible;
    private boolean[] fullyFilled;
    private int chunkList;
    private boolean initialized;

    public Chunk(Vector2i chunkListPosition)
    {
        this.chunkListPosition = chunkListPosition;
        this.layersState = new boolean[CHUNK_LAYERS];
        this.layersVisible = new boolean[CHUNK_LAYERS];
        this.fullyFilled = new boolean[CHUNK_LAYERS];
        this.aabb = new AABB(
                chunkListPosition.x * 16, 0, chunkListPosition.y * 16,
                chunkListPosition.x * 16 + 16, ClientLevel.MAX_HEIGHT, chunkListPosition.y * 16 + 16);
        this.chunkRegion = new Region(this);
        this.chunkList = GL11.glGenLists(CHUNK_LAYERS);

        Opencraft.getThreadsManager().execute(() -> {
            this.chunkRegion.generate();
            this.initialized = true;
        });
    }

    public boolean buildLayer(int layer) {
        if (!this.initialized) {
            return false;
        }

        Chunk c0 = Opencraft.getLevelRenderer().getChunkByPos(chunkListPosition.x - 1, chunkListPosition.y);
        Chunk c1 = Opencraft.getLevelRenderer().getChunkByPos(chunkListPosition.x, chunkListPosition.y - 1);
        Chunk c2 = Opencraft.getLevelRenderer().getChunkByPos(chunkListPosition.x + 1, chunkListPosition.y);
        Chunk c3 = Opencraft.getLevelRenderer().getChunkByPos(chunkListPosition.x, chunkListPosition.y + 1);

        if (layer > 1 && layer < CHUNK_LAYERS-1 && c0 != null && c1 != null && c2 != null && c3 != null) {
            if (getFilledState(layer + 1) && getFilledState(layer - 1) && c0.getFilledState(layer) && c1.getFilledState(layer) && c2.getFilledState(layer) && c3.getFilledState(layer)) {
                return false;
            }
        }

        ++LevelRenderer.CHUNK_UPDATES;

        layersState[layer] = false;
        layersVisible[layer] = false;
        fullyFilled[layer] = true;

        boolean isVisible = false;

        GL11.glNewList(this.chunkList + layer, GL11.GL_COMPILE);

        verticesBuffer.clear();

        for (int y = layer * (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS); y < layer * (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS) + (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS); ++y) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    Block block = Opencraft.getLevel().getBlock(chunkListPosition.x * 16 + x, y, chunkListPosition.y * 16 + z);

                    if (block.isVisible()) {
                        BlockRenderer.render(verticesBuffer, chunkListPosition.x * 16 + x, y, chunkListPosition.y * 16 + z, block);
                        isVisible = true;
                    }
                    else {
                        fullyFilled[layer] = false;
                    }
                }
            }
        }

        layersVisible[layer] = isVisible;
        layersState[layer] = true;

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
        if (!this.initialized)
            return;

        GL11.glCallList(this.chunkList + i);
    }

    public AABB getAABB() {
        return this.aabb;
    }

    public SimpleAABB getSimpleAABB(int layer) {
        return new SimpleAABB(
                this.chunkListPosition.x * 16, layer * ((float) ClientLevel.MAX_HEIGHT / CHUNK_LAYERS), this.chunkListPosition.y * 16,
                this.chunkListPosition.x * 16 + 16, layer * ((float) ClientLevel.MAX_HEIGHT / CHUNK_LAYERS) + ((float) ClientLevel.MAX_HEIGHT / CHUNK_LAYERS), this.chunkListPosition.y * 16 + 16);
    }

    public void destroy() {
        if (!this.initialized)
            return;

        this.initialized = false;

        this.chunkRegion.destroy();
        this.layersState = null;
        this.layersVisible = null;
        this.fullyFilled = null;
    }

    public float distanceToEntity(Entity entity) {
        float xd = ((int)entity.getPosition().x >> 4) - chunkListPosition.x;
        float zd = ((int)entity.getPosition().z >> 4) - chunkListPosition.y;
        return (float) Math.sqrt(xd * xd + zd * zd);
    }

    public Vector3i translateToRealCoords(Vector3i position) {
        return translateToRealCoords(position.x, position.y, position.z);
    }

    public Vector3i translateToRealCoords(int x, int y, int z) {
        return new Vector3i(
                x + this.chunkListPosition.x * 16,
                y,
                z + this.chunkListPosition.y * 16
        );
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean getLayerState(int layer) {
        return layersState[layer];
    }

    public boolean getLayersVisible(int layer) {
        return layersVisible[layer];
    }

    public void setLayerState(int k) {
        layersState[k] = false;
    }

    public boolean getFilledState(int layer) {
        return fullyFilled[layer];
    }
}
