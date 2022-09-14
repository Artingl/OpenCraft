package com.artingl.opencraft.Control.World;

import com.artingl.opencraft.Control.Game.Input;
import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Control.Game.Frustum;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.World.Chunk.ChunksSorter;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.World.Level.LevelListener;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LevelRenderer implements Tick {
    public static int CHUNK_UPDATES = 0;
    public static int CHUNKS_RENDERED = 0;


    private boolean loadingWorld;
    private long updateTimer;
    private int chunksUpdate;
    private final ConcurrentHashMap<Vector2i, Chunk> chunks;

    public LevelRenderer() {
        this.chunks = new ConcurrentHashMap<>();
        Opencraft.registerTickEvent(this);
    }

    public void prepareChunks() {
        while (!Opencraft.getClientConnection().isActive())
            GUI.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.joining_world"));
        Logger.debug("Preparing level chunks...");

        ClientLevel level = Opencraft.getLevel();

        if (chunks.isEmpty()) {
            this.loadingWorld = true;
            this.tick();
            System.gc();

            this.loadingWorld = false;
        }

        this.publishListenersEvent(LevelListener.Events.LEVEL_SWITCH, level, null);
    }

    public void render(Frustum frustum) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());

        EntityPlayer player = Opencraft.getPlayerEntity();
        Vector3f pos = player.getPosition();

        CHUNKS_RENDERED = 0;
        int updates = 0;

        ArrayList<Chunk> sortedChunks = new ArrayList<>(chunks.values());
        sortedChunks.sort(new ChunksSorter(player));

        for (Chunk chunk : sortedChunks) {

            if (!frustum.isVisible(chunk.getAABB()))
                continue;

            int chunk_updates = 0;

            for (int layer = 0; layer < Chunk.CHUNK_LAYERS; layer++) {
                if (!frustum.isVisible(chunk.getSimpleAABB(layer)))
                    continue;

                int y = layer * (ClientLevel.MAX_HEIGHT / Chunk.CHUNK_LAYERS);

                if (Math.abs(y - pos.y) < 256) {
                    if (!chunk.getLayerState(layer) && ++updates <= 2 && ++chunk_updates < Chunk.CHUNK_LAYERS / 2) {
                        if (!chunk.buildLayer(layer)) {
                            --updates;
                            --chunk_updates;
                        }
                    }

                    if (chunk.getLayersVisible(layer)) {
                        chunk.render(layer);
                        CHUNKS_RENDERED++;
                    }
                }
            }
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        this.publishListenersEvent(LevelListener.Events.LEVEL_DESTROY, Opencraft.getLevel(), null);

        for (Map.Entry<Vector2i, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            chunk.destroy();
        }

        chunks.clear();
        System.gc();
    }

    public void sendEvent(LevelListener.Events event, ClientLevel level, Chunk chunk, Vector3i position) {
        if (event == LevelListener.Events.CHUNK_UPDATE) {
            if (chunk != null)
                chunk.setLayerState(position.y / (ClientLevel.MAX_HEIGHT / Chunk.CHUNK_LAYERS));
        }

        this.publishListenersEvent(event, level, chunk);
    }

    private void publishListenersEvent(LevelListener.Events event, ClientLevel level, Chunk chunk) {
        for (Map.Entry<Integer, LevelListener> entry : Opencraft.getLevelListeners().entrySet()) {
            LevelListener listener = entry.getValue();

            if (event == LevelListener.Events.CHUNK_UPDATE) {
                listener.chunkUpdate(level, chunk);
            } else if (event == LevelListener.Events.LEVEL_DESTROY) {
                listener.levelDestroy(level);
            } else if (event == LevelListener.Events.LEVEL_SWITCH) {
                listener.levelSwitch(level);
            }
        }
    }

    public Chunk getChunkByBlockPos(int x, int z) {
        return chunks.get(new Vector2i(x >> 4, z >> 4));
    }

    public Chunk getChunkByPos(int x, int z) {
        return chunks.get(new Vector2i(x, z));
    }

    @Override
    public void tick() {
        if (updateTimer + 200 > System.currentTimeMillis())
            return;

        updateTimer = System.currentTimeMillis();

        // check if we're able to render new chunks
        int renderDistance = Opencraft.getRenderDistance();

        ClientLevel level = Opencraft.getLevel();
        EntityPlayer player = Opencraft.getPlayerEntity();

        Vector3f playerPos = player.getPosition();

        int counter = 0;
        int maxRegionsCounter = (((renderDistance * 2) + 2) * (renderDistance * 2));

        if (maxRegionsCounter > 64)
            maxRegionsCounter = 64;

        // search for new chunks
        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                int chunk_x = (x * 16) + (int) playerPos.x;
                int chunk_z = (z * 16) + (int) playerPos.z;

                if (getChunkByPos(chunk_x >> 4, chunk_z >> 4) == null) {
                    Chunk chunk = new Chunk(new Vector2i(chunk_x >> 4, chunk_z >> 4));
                    chunks.put(new Vector2i(chunk_x >> 4, chunk_z >> 4), chunk);

                    if (this.loadingWorld && counter < maxRegionsCounter) {
                        do {
                            GUI.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.loading_region") + " " + counter + " " + Lang.getTranslatedString("opencraft:gui.text.out_of") + " " + maxRegionsCounter);
                        } while (!chunk.isInitialized());
                    }

                    // update chunk neighbours
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            if (i != 0 && j != 0) {
                                Chunk chunkNeighbour = getChunkByPos((chunk_x >> 4) + i, (chunk_z >> 4) + j);

                                if (chunkNeighbour != null) {
                                    if (chunkNeighbour.isInitialized()) {
                                        for (int k = 0; k < Chunk.CHUNK_LAYERS; k++)
                                            chunkNeighbour.setLayerState(k);
                                    }
                                }
                            }
                        }
                    }

                    this.publishListenersEvent(LevelListener.Events.CHUNK_UPDATE, level, chunk);
                    chunksUpdate++;
                    counter++;
                }
            }
        }

        // remove chunks which out of our render distance
        for (Map.Entry<Vector2i, Chunk> entry: chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            chunksUpdate++;

            if (chunk == null) {
                chunks.remove(entry.getKey());
                continue;
            }

            int dist = (int) (chunk.distanceToEntity(player));

            if (dist > Opencraft.getRenderDistance() + 4) {
                chunk.destroy();
                chunk = null;
                chunks.remove(entry.getKey());
            }
        }

        if (chunksUpdate > 4096) {
            // clear memory after some time (in case some garbage is still in memory)
            Logger.debug("Clearing memory");
            Opencraft.getThreadsManager().execute(System::gc);
            chunksUpdate = 0;
        }
    }

    public int getChunksAmount() {
        return chunks.size();
    }
}
