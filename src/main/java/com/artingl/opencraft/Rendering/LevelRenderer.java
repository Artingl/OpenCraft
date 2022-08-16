package com.artingl.opencraft.Rendering;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.World.ITick;
import com.artingl.opencraft.World.LevelListener;
import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Chunk.ChunksSorter;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.Level;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Map;

public class LevelRenderer implements ITick
{
    public static int CHUNK_UPDATES = 0;
    public static int CHUNKS_RENDERED = 0;

    private int chunksUpdate;
    private ArrayList<Chunk> chunks;

    public LevelRenderer() {
        this.chunks = new ArrayList<>();
        OpenCraft.registerTickEvent(this);
    }

    public void prepareChunks()
    {
        Logger.info("Preparing level chunks...");

        Level level = OpenCraft.getLevel();
        int renderDistance = OpenCraft.getRenderDistance();

        if (chunks.isEmpty()) {
            int j = 0;
            for (int x = -renderDistance; x < renderDistance; x++) {
                for (int z = -renderDistance; z < renderDistance; z++) {
                    Chunk chunk = new Chunk(new Vector2i(x >> 4, z >> 4));
                    for (int i = 0; i < Chunk.CHUNK_LAYERS; i++)
                        chunk.buildLayer(i);
                    chunks.add(chunk);

                    this.publishListenersEvent(LevelListener.Events.CHUNK_UPDATE, level, chunk);
                }

                Logger.info(Math.round(((j++) / (renderDistance * 2f)) * 100f) + "%");
            }

            Logger.info("100%");
            System.gc();
        }
        else {
            Logger.debug("TODO: implement LevelRenderer chunks cleaner");
        }

        this.publishListenersEvent(LevelListener.Events.LEVEL_SWITCH, level, null);
    }

    public void render(Frustum frustum) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());

        CHUNKS_RENDERED = 0;
        int updates = 0;

        chunks.sort(new ChunksSorter(OpenCraft.getLevel().getPlayerEntity()));

        for(Chunk chunk: chunks) {
            if (frustum.isVisible(chunk.getAABB())) {
                for (int layer = 0; layer < Chunk.CHUNK_LAYERS; layer++) {
                    if (frustum.isVisible(chunk.getSimpleAABB(layer))) {
                        if (!chunk.getLayerState(layer) && ++updates < 5) {
                            if (!chunk.buildLayer(layer))
                                updates--;
                        }

                        if (chunk.getLayersVisible(layer)) {
                            chunk.render(layer);
                            CHUNKS_RENDERED++;
                        }
                    }
                }
            }
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        this.publishListenersEvent(LevelListener.Events.LEVEL_DESTROY, OpenCraft.getLevel(), null);

        for(Chunk chunk: chunks) {
            chunk.destroy();
        }

        chunks.clear();
        System.gc();
    }

    public void sendEvent(LevelListener.Events event, Level level, Chunk chunk, Vector3i position) {
        if (event == LevelListener.Events.CHUNK_UPDATE) {
            if (chunk != null)
                chunk.buildLayer(position.y / (256 / Chunk.CHUNK_LAYERS));
        }

        this.publishListenersEvent(event, level, chunk);
    }

    private void publishListenersEvent(LevelListener.Events event, Level level, Chunk chunk) {
        for (Map.Entry<Integer, LevelListener> entry: OpenCraft.getLevelListeners().entrySet()) {
            LevelListener listener = entry.getValue();

            if (event == LevelListener.Events.CHUNK_UPDATE) {
                listener.chunkUpdate(level, chunk);
            }
            else if (event == LevelListener.Events.LEVEL_DESTROY) {
                listener.levelDestroy(level);
            }
            else if (event == LevelListener.Events.LEVEL_SWITCH) {
                listener.levelSwitch(level);
            }
        }
    }

    public Chunk getChunkByBlockPos(int x, int z) {
        // todo
        for (Chunk chunk: this.chunks) {
            if (chunk == null) {
                continue;
            }

            if (chunk.getPosition().x == x >> 4 && chunk.getPosition().y == z >> 4) {
                return chunk;
            }
        }

        return null;
    }

    public void switchWorld() {
        // todo: fix memory leak. it doubles the amount of used memory
        //       when the very first switch is called. after that no memory leak
        //       was found.

        this.publishListenersEvent(LevelListener.Events.LEVEL_SWITCH, OpenCraft.getLevel(), null);

        for(Chunk chunk: chunks) {
            chunk.destroy();
        }

        chunks.clear();
        System.gc();

        // this place might be in a thread,
        // so we'd like to run it inside GL context
        OpenCraft.runInGLContext(this::prepareChunks);
    }

    @Override
    public void tick() {
        // check if we're able to render new chunks
        int renderDistance = OpenCraft.getRenderDistance();

        Level level = OpenCraft.getLevel();
        EntityPlayer player = level.getPlayerEntity();

        // search for new chunks
        for (int x = -renderDistance; x < renderDistance; x++) {
            for (int z = -renderDistance; z < renderDistance; z++) {
                Vector2i chunkPosition = new Vector2i(
                        (x * 16) + (int)player.getX(),
                        (z * 16) + (int)player.getZ()
                );

                if (getChunkByBlockPos(chunkPosition.x, chunkPosition.y) == null) {
                    Chunk chunk = new Chunk(new Vector2i(chunkPosition.x >> 4, chunkPosition.y >> 4));
                    chunks.add(chunk);

                    // update chunk neighbours
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            if (i != 0 && j != 0) {
                                Chunk chunkNeighbour = getChunkByBlockPos(chunkPosition.x + i, chunkPosition.y + j);

                                if (chunkNeighbour != null) {
                                    if (chunkNeighbour.isInitialized()) {
                                        for (int k = 0; k < Chunk.CHUNK_LAYERS; k++)
                                            chunkNeighbour.setLayerState(k, false);
                                    }
                                }
                            }
                        }
                    }

                    this.publishListenersEvent(LevelListener.Events.CHUNK_UPDATE, level, chunk);
                    chunksUpdate++;
                }
            }
        }

        // remove chunks which out of our render distance
        for (int index = 0; index < chunks.size(); index++) {
            Chunk chunk = chunks.get(index);

            if (chunk == null) {
                continue;
            }

            int dist = (int) (chunk.distanceToEntity(player));

            if (dist > OpenCraft.getRenderDistance() + 2) {
                chunk.destroy();
                chunks.remove(chunk);
                chunksUpdate++;
            }
        }

        if (chunksUpdate > 4096) {
            // clear memory after some time (in case some garbage is still in memory)
            Logger.info("Clearing memory");
            new Thread(System::gc).start();
            chunksUpdate = 0;
        }
    }

    public int getChunksAmount() {
        return chunks.size();
    }
}
