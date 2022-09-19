package com.artingl.opencraft.Control.World;

import com.artingl.opencraft.GUI.ScreenRegistry;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Control.Render.Frustum;
import com.artingl.opencraft.Control.Game.TextureEngine;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.World.Chunk.ChunksSorter;
import com.artingl.opencraft.World.Level.Listener.LevelListenerEventChunkUpdate;
import com.artingl.opencraft.World.Level.Listener.LevelListenerEvent;
import com.artingl.opencraft.World.Level.Listener.LevelListenerEventEntityUpdate;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.World.Level.Listener.LevelListener;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.Math.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LevelController implements Tick {
    public static int CHUNK_UPDATES = 0;
    public static int CHUNKS_RENDERED = 0;


    private boolean loadingWorld;
    private long updateTimer;
    private int chunksUpdate;
    private final ConcurrentHashMap<Vector2i, Chunk> chunks;

    public LevelController() {
        this.chunks = new ConcurrentHashMap<>();
        Opencraft.registerTickEvent(this);
    }

    public void prepareChunks() {
        while (!Opencraft.getClientConnection().isActive())
            ScreenRegistry.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.joining_world"));
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
        Opencraft.getShaderProgram().bindTexture(TextureEngine.getTerrain());

        EntityPlayer player = Opencraft.getPlayerEntity();
        Vector3f pos = player.getPosition();

        CHUNKS_RENDERED = 0;
        int updates = 2;

        ArrayList<Chunk> sortedChunks = new ArrayList<>(chunks.values());
        sortedChunks.sort(new ChunksSorter(player));

        for (Chunk chunk : sortedChunks) {

            if (!frustum.isVisible(chunk.getAABB()) || !chunk.isInitialized())
                continue;

            if (chunk.shouldRenderBuffersQueue() && updates >= 0) {
                updates -= chunk.renderBuffersQueue(1);
            }

            for (int layer = 0; layer < Chunk.CHUNK_LAYERS; layer++) {
                if (!frustum.isVisible(chunk.getSimpleAABB(layer)))
                    continue;

                if (!chunk.isLayerRendering(layer) && !chunk.getLayerState(layer))
                    chunk.buildLayer(layer);

                int y = layer * (ClientLevel.MAX_HEIGHT / Chunk.CHUNK_LAYERS);

                if (Math.abs(y - pos.y) < 256) {
                    if (chunk.getLayersVisible(layer)) {
                        chunk.render(layer);
                        CHUNKS_RENDERED++;
                    }
                }
            }
        }

        Opencraft.getShaderProgram().bindTexture(0);
    }

    public void destroy() {
        this.publishListenersEvent(LevelListener.Events.LEVEL_DESTROY, Opencraft.getLevel());

        for (Map.Entry<Vector2i, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            chunk.destroy();
        }

        chunks.clear();
        System.gc();
    }

    public void sendEvent(LevelListener.Events eventType, ClientLevel level, LevelListenerEvent event) {
        if (eventType == LevelListener.Events.CHUNK_UPDATE) {
            Chunk chunk = ((LevelListenerEventChunkUpdate)event).getChunk();

            if (chunk != null)
                chunk.setLayerState((int) (event.getPosition().y / (ClientLevel.MAX_HEIGHT / Chunk.CHUNK_LAYERS)));
        }

        this.publishListenersEvent(eventType, level, event);
    }

    private void publishListenersEvent(LevelListener.Events eventType, ClientLevel level) {
        publishListenersEvent(eventType, level, null);
    }

    private void publishListenersEvent(LevelListener.Events eventType, ClientLevel level, LevelListenerEvent event) {
        for (Map.Entry<Integer, LevelListener> entry : Opencraft.getLevelListeners().entrySet()) {
            LevelListener listener = entry.getValue();

            if (eventType == LevelListener.Events.CHUNK_UPDATE) {
                listener.chunkUpdate(level, ((LevelListenerEventChunkUpdate)event).getChunk());
            }
            else if (eventType == LevelListener.Events.LEVEL_DESTROY) {
                listener.levelDestroy(level);
            }
            else if (eventType == LevelListener.Events.LEVEL_SWITCH) {
                listener.levelSwitch(level);
            }
            else if (eventType == LevelListener.Events.ENTITY_UPDATE) {
                listener.entityUpdate(level, ((LevelListenerEventEntityUpdate)event).getEntity(), ((LevelListenerEventEntityUpdate)event).getEventType());
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
                            ScreenRegistry.loadingScreen.setLoadingText(Lang.getTranslatedString("opencraft:gui.text.loading_region") + " " + counter + " " + Lang.getTranslatedString("opencraft:gui.text.out_of") + " " + maxRegionsCounter);
                        } while (!chunk.isInitialized());
                    }

                    // update chunk neighbours
                    for (int i = -2; i <= 2; i++) {
                        for (int j = -2; j <= 2; j++) {
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

                    this.publishListenersEvent(LevelListener.Events.CHUNK_UPDATE, level, new LevelListenerEventChunkUpdate(chunk, new Vector3f(chunk_x, 0, chunk_z)));
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

            int dist = (int) (chunk.distanceToEntity(player)) - 8;

            if (dist > Opencraft.getRenderDistance()) {
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
