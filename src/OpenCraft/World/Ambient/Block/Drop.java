package OpenCraft.World.Ambient.Block;

import OpenCraft.Interfaces.IRenderHandler;
import OpenCraft.OpenCraft;
import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.World.Entity.Entity;
import OpenCraft.World.Item.ItemBlock;
import OpenCraft.math.Vector3i;
import OpenCraft.sound.Sound;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Drop extends Entity implements IRenderHandler {

    private final int renderEvent;
    private ItemBlock itemBlock;

    private float rotation = 0;
    private float position = 0;
    private boolean state;
    private boolean picked;

    public Drop(ItemBlock itemBlock, Vector3i position) {
        this.renderEvent = OpenCraft.registerRenderEvent(this);
        this.itemBlock = itemBlock;
        this.picked = false;

        reset(position.x, position.y + 1, position.z);
    }

    @Override
    public void render() {
        if (this.picked) return;

        VerticesBuffer t = VerticesBuffer.instance;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());
        GL11.glTranslatef(x, y + position, z);
        GL11.glRotatef(rotation, 0, 1, 0);

        t.begin();
        BlockRenderer.renderBackSide(t, 0, 0, 0, 0.4f, itemBlock.getBlock());
        BlockRenderer.renderTopSide(t, 0, 0, 0, 0.4f, itemBlock.getBlock());
        BlockRenderer.renderLeftSide(t, 0, 0, 0, 0.4f, itemBlock.getBlock());
        BlockRenderer.renderRightSide(t, 0, 0, 0, 0.4f, itemBlock.getBlock());
        BlockRenderer.renderBottomSide(t, 0, 0, 0, 0.4f, itemBlock.getBlock());
        BlockRenderer.renderFrontSide(t, 0, 0, 0, 0.4f, itemBlock.getBlock());
        t.end();

        GL11.glRotatef(-rotation, 0, 1, 0);
        GL11.glTranslatef(-x, -(y + position), -z);
        GL11.glEndList();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    @Override
    public void tick() {
        if (this.picked) return;

        super.tick();

        rotation += 2;

        if (!state) position += 0.3f / 20f;
        else        position -= 0.3f / 20f;

        if (position > 0.3)      state = true;
        else if (position < 0.01) state = false;

        this.yd = (float)((double)this.yd - 0.04D);
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98F;
        this.yd *= 0.98F;
        this.zd *= 0.98F;
        if (this.onGround) {
            this.xd *= 0.7F;
            this.zd *= 0.7F;
        }

        ArrayList<Entity> entities = OpenCraft.getLevel().getEntities();

        for (Entity entity: entities) {
            if (entity.hasInventory() && entity.aabb.intersects(this.aabb)) {
                Sound.loadAndPlay("resources/sounds/player/pick.wav");
                entity.pick(itemBlock);
                this.picked();
                break;
            }
        }
    }

    public void picked() {
        // todo: do it able to completely remove from the world
        OpenCraft.unregisterRenderEvent(this.renderEvent);
        this.destroy();
        this.picked = true;

    }

}
