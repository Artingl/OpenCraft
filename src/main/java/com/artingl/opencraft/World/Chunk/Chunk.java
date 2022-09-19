package com.artingl.opencraft.World.Chunk;

import com.artingl.opencraft.Control.Render.VericiesBuffer;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.Control.Render.BlockRenderer;
import com.artingl.opencraft.Control.World.LevelController;
import com.artingl.opencraft.Control.Render.BufferRenderer;
import com.artingl.opencraft.World.Block.Block;
import com.artingl.opencraft.World.Entity.Entity;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import com.artingl.opencraft.Phys.AABB;
import com.artingl.opencraft.Phys.SimpleAABB;
import com.artingl.opencraft.World.Level.ClientLevel;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Chunk
{
    public static final int CHUNK_LAYERS = 4;

    private BufferRenderer bufferRenderer = BufferRenderer.getGlobalInstance();
    private Vector2i chunkListPosition;
    private Region chunkRegion;
    private AABB aabb;
    private ConcurrentLinkedDeque<BufferQueueMember> buffersQueue;
    private boolean[] layersState;
    private boolean[] layerIsRendering;
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
        this.layerIsRendering = new boolean[CHUNK_LAYERS];
        this.aabb = new AABB(
                chunkListPosition.x * 16, 0, chunkListPosition.y * 16,
                chunkListPosition.x * 16 + 16, ClientLevel.MAX_HEIGHT, chunkListPosition.y * 16 + 16);
        this.chunkRegion = new Region(this);
        this.chunkList = GL11.glGenLists(CHUNK_LAYERS);
        this.buffersQueue = new ConcurrentLinkedDeque<>();

        Opencraft.getThreadsManager().execute(() -> {
            this.chunkRegion.generate();
            this.initialized = true;
        });
    }

    public void buildLayer(int layer) {
        layerIsRendering[layer] = true;

        Opencraft.getThreadsManager().execute(() -> {
            if (!this.initialized) {
                layerIsRendering[layer] = false;
                return;
            }

            Chunk c0 = Opencraft.getLevelController().getChunkByPos(chunkListPosition.x - 1, chunkListPosition.y);
            Chunk c1 = Opencraft.getLevelController().getChunkByPos(chunkListPosition.x, chunkListPosition.y - 1);
            Chunk c2 = Opencraft.getLevelController().getChunkByPos(chunkListPosition.x + 1, chunkListPosition.y);
            Chunk c3 = Opencraft.getLevelController().getChunkByPos(chunkListPosition.x, chunkListPosition.y + 1);

            if (layer > 1 && layer < CHUNK_LAYERS-1 && c0 != null && c1 != null && c2 != null && c3 != null) {
                if (getFilledState(layer + 1) && getFilledState(layer - 1) && c0.getFilledState(layer) && c1.getFilledState(layer) && c2.getFilledState(layer) && c3.getFilledState(layer)) {
                    layerIsRendering[layer] = false;
                    return;
                }
            }

            ++LevelController.CHUNK_UPDATES;

            layersState[layer] = false;
            fullyFilled[layer] = true;

            boolean isVisible = false;

            VericiesBuffer buffer = new VericiesBuffer();

            for (int y = layer * (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS); y < layer * (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS) + (ClientLevel.MAX_HEIGHT / CHUNK_LAYERS); ++y) {
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        Block block = Opencraft.getLevel().getBlock(chunkListPosition.x * 16 + x, y, chunkListPosition.y * 16 + z);

                        if (block.isVisible()) {
                            BlockRenderer.render(buffer, chunkListPosition.x * 16 + x, y, chunkListPosition.y * 16 + z, block);
                            isVisible = true;
                        }
                        else {
                            fullyFilled[layer] = false;
                        }
                    }
                }
            }

            layersState[layer] = true;
            buffersQueue.add(new BufferQueueMember(buffer, layer, isVisible));
            layerIsRendering[layer] = false;
        });
    }

    public int renderBuffersQueue(int count) {
        int total;

        for (total = 0; total <= count && buffersQueue.size() > 0; total++) {
            BufferQueueMember member = buffersQueue.getFirst();
            buffersQueue.removeFirst();

            if (member.isVisible) {
                GL11.glNewList(this.chunkList + member.getLayerId(), GL11.GL_COMPILE);
                bufferRenderer.clear();
                bufferRenderer.drawBuffer(member.getBuffer());
                member.getBuffer().clear();
                bufferRenderer.end();
                bufferRenderer.clear();
                GL11.glEndList();
                layersVisible[member.layerId] = true;
            }
        }

        return total;
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
        this.buffersQueue.clear();
//        this.layersState = null;
//        this.layersVisible = null;
//        this.fullyFilled = null;
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

    public boolean shouldRenderBuffersQueue() {
        return this.buffersQueue.size() > 0;
    }

    public boolean isLayerRendering(int layer) {
        return layerIsRendering[layer];
    }

    public static class BufferQueueMember {
        private final VericiesBuffer buffer;
        private final int layerId;
        private final boolean isVisible;

        public BufferQueueMember(VericiesBuffer buffer, int layerId, boolean isVisible) {
            this.buffer = buffer;
            this.layerId = layerId;
            this.isVisible = isVisible;
        }

        public VericiesBuffer getBuffer() {
            return buffer;
        }

        public int getLayerId() {
            return layerId;
        }

        public boolean isVisible() {
            return isVisible;
        }
    }
}
