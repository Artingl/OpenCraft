package com.artingl.opencraft.World.Chunk;

import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Rendering.BlockRenderer;
import com.artingl.opencraft.Rendering.LevelRenderer;
import com.artingl.opencraft.Rendering.VerticesBuffer;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Phys.AABB;
import com.artingl.opencraft.Phys.SimpleAABB;
import com.artingl.opencraft.World.Level.ClientLevel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Chunk
{
    public static final int CHUNK_LAYERS = 64;

    private VerticesBuffer verticesBuffer = VerticesBuffer.getGlobalInstance();
    private Vector2i chunkListPosition;
    private final Region chunkRegion;
    private AABB aabb;
    private ArrayList<Boolean> layersState;
    private ArrayList<Boolean> layersVisible;
    private int chunkList;
    private boolean initialized;

    public Chunk(Vector2i chunkListPosition)
    {
        this.chunkListPosition = chunkListPosition;
        this.layersState = new ArrayList<>();
        this.layersVisible = new ArrayList<>();
        this.aabb = new AABB(
                chunkListPosition.x * 16, 0, chunkListPosition.y * 16,
                chunkListPosition.x * 16 + 16, ClientLevel.MAX_HEIGHT, chunkListPosition.y * 16 + 16);
        this.chunkRegion = new Region(this);
        this.chunkList = GL11.glGenLists(CHUNK_LAYERS);

        for (int i = 0; i < CHUNK_LAYERS; i++) {
            layersState.add(false);
            layersVisible.add(false);
        }

        Opencraft.getThreadsManager().execute(() -> {
            this.chunkRegion.generate();
            this.initialized = true;
        });
    }

    public boolean buildLayer(int layer) {
        if (!this.initialized)
            return false;

        ++LevelRenderer.CHUNK_UPDATES;

        layersState.set(layer, false);
        layersVisible.set(layer, false);

        boolean isVisible = false;

        GL11.glNewList(this.chunkList + layer, GL11.GL_COMPILE);

        Vector3i currentBlockPosition = new Vector3i(0, 0, 0);
        verticesBuffer.clear();

        for (int y = layer * (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS); y < layer * (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS) + (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS); ++y) {
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

        Opencraft.runInGLContext(() -> {
            if (this.chunkList != -1)
                GL11.glDeleteLists(this.chunkList, CHUNK_LAYERS);
        });

        this.chunkRegion.destroy();
        this.layersState.clear();
        this.layersVisible.clear();
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
        return layersState.get(layer);
    }

    public boolean getLayersVisible(int layer) {
        return layersVisible.get(layer);
    }

    public void setLayerState(int k) {
        layersState.set(k, false);
    }
}
