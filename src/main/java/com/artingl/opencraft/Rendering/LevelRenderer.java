package com.artingl.opencraft.Rendering;

import com.artingl.opencraft.GUI.GUI;
import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Math.Vector3f;
import com.artingl.opencraft.Resources.Lang.Lang;
import com.artingl.opencraft.World.Tick;
import com.artingl.opencraft.World.LevelListener;
import com.artingl.opencraft.Opencraft;
import com.artingl.opencraft.World.Chunk.Chunk;
import com.artingl.opencraft.World.Chunk.ChunksSorter;
import com.artingl.opencraft.World.Entity.EntityPlayer;
import com.artingl.opencraft.World.Level.ClientLevel;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Math.Vector3i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Map;

public class LevelRenderer implements Tick
{
    public static int CHUNK_UPDATES = 0;
    public static int CHUNKS_RENDERED = 0;


    private int chunksUpdate;
    private ArrayList<Chunk> chunks;

    public LevelRenderer() {
        this.chunks = new ArrayList<>();
        Opencraft.registerTickEvent(this);
    }

    public void prepareChunks()
    {
        while (!Opencraft.getClientConnection().isActive())
            GUI.loadingScreen.setLoadingText(Lang.getLanguageString("opencraft:gui.text.joining_world"));
        Logger.debug("Preparing level chunks...");

        ClientLevel level = Opencraft.getLevel();
        int renderDistance = Opencraft.getRenderDistance();

        if (chunks.isEmpty()) {
            int j = 0;
            for (int x = -renderDistance; x < renderDistance; x++) {
                for (int z = -renderDistance; z < renderDistance; z++) {
                    GUI.loadingScreen.updateDisplay();

                    Chunk chunk = new Chunk(new Vector2i(x >> 4, z >> 4));
                    chunks.add(chunk);

                    this.publishListenersEvent(LevelListener.Events.CHUNK_UPDATE, level, chunk);
                }

                Logger.debug(Math.round(((j++) / (renderDistance * 2f)) * 100f) + "%");
            }

            Logger.debug("100%");
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

        EntityPlayer player = Opencraft.getPlayerEntity();
        Vector3f pos = player.getPosition();

        CHUNKS_RENDERED = 0;
        int updates = 0;

        chunks.sort(new ChunksSorter(Opencraft.getPlayerEntity()));

        for (Chunk chunk: chunks) {
            boolean isVisible = frustum.isVisible(chunk.getAABB());
            int chunk_updates = 0;

            for (int layer = 0; layer < Chunk.CHUNK_LAYERS; layer++) {
                if (!frustum.isVisible(chunk.getSimpleAABB(layer)))
                    continue;

                int y = layer * (ClientLevel.MAX_HEIGHT / Chunk.CHUNK_LAYERS);

                if (Math.abs(y - pos.y) < 100) {
                    if (!chunk.getLayerState(layer) && ++updates < 5 && ++chunk_updates < Chunk.CHUNK_LAYERS/2) {
                        if (!chunk.buildLayer(layer))
                            --updates;
                        }

                    if (chunk.getLayersVisible(layer) && isVisible) {
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

        for (Chunk chunk: chunks) {
            chunk.destroy();
        }

        chunks.clear();
        System.gc();
    }

    public void sendEvent(LevelListener.Events event, ClientLevel level, Chunk chunk, Vector3i position) {
        if (event == LevelListener.Events.CHUNK_UPDATE) {
            if (chunk != null)
                chunk.buildLayer(position.y / (ClientLevel.MAX_HEIGHT / Chunk.CHUNK_LAYERS));
        }

        this.publishListenersEvent(event, level, chunk);
    }

    private void publishListenersEvent(LevelListener.Events event, ClientLevel level, Chunk chunk) {
        for (Map.Entry<Integer, LevelListener> entry: Opencraft.getLevelListeners().entrySet()) {
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
        for (Chunk chunk: new ArrayList<>(this.chunks)) {
            if (chunk == null) {
                continue;
            }

            if (chunk.getPosition().x == x >> 4 && chunk.getPosition().y == z >> 4) {
                return chunk;
            }
        }

        return null;
    }

    public Chunk getChunkByPos(int x, int z) {
        // todo
        for (Chunk chunk: new ArrayList<>(this.chunks)) {
            if (chunk == null) {
                continue;
            }

            if (chunk.getPosition().x == x && chunk.getPosition().y == z) {
                return chunk;
            }
        }

        return null;
    }

    @Override
    public void tick() {
        // check if we're able to render new chunks
        int renderDistance = Opencraft.getRenderDistance();

        ClientLevel level = Opencraft.getLevel();
        EntityPlayer player = Opencraft.getPlayerEntity();

        Vector3f playerPos = player.getPosition();

        // search for new chunks
        for (int x = -renderDistance; x < renderDistance; x++) {
            for (int z = -renderDistance; z < renderDistance; z++) {
                int chunk_x = (x * 16) + (int)playerPos.x;
                int chunk_z = (z * 16) + (int)playerPos.z;

                if (getChunkByBlockPos(chunk_x, chunk_z) == null) {
                    Chunk chunk = new Chunk(new Vector2i(chunk_x >> 4, chunk_z >> 4));
                    chunks.add(chunk);

                    // update chunk neighbours
                    for (int i = -2; i < 3; i++) {
                        for (int j = -2; j < 3; j++) {
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

            if (dist > Opencraft.getRenderDistance() + 2) {
                chunk.destroy();
                chunks.remove(chunk);
                chunksUpdate++;
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
