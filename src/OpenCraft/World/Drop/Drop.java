package OpenCraft.World.Drop;

import OpenCraft.Interfaces.IRenderHandler;
import OpenCraft.Interfaces.ITick;
import OpenCraft.OpenCraft;
import OpenCraft.Rendering.BlockRenderer;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.Rendering.VerticesBuffer;
import OpenCraft.World.Block.BlockDestroy;
import OpenCraft.World.Entity.Entity;
import OpenCraft.World.Item.ItemBlock;
import OpenCraft.math.Vector3i;
import org.lwjgl.opengl.GL11;

public class Drop extends Entity implements IRenderHandler, ITick {

    private final int tickEvent;
    private final int renderEvent;
    private ItemBlock itemBlock;

    public Drop(ItemBlock itemBlock, Vector3i position) {
        this.renderEvent = OpenCraft.registerRenderEvent(this);
        this.tickEvent = OpenCraft.registerTickEvent(this);
        this.itemBlock = itemBlock;

        reset(position.x, position.y, position.z);
    }

    @Override
    public void render() {
        VerticesBuffer t = VerticesBuffer.instance;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureEngine.getTerrain());

        t.begin();
        BlockRenderer.renderBackSide(t, x, y, z, itemBlock.getBlock());
        BlockRenderer.renderTopSide(t, x, y, z, itemBlock.getBlock());
        BlockRenderer.renderLeftSide(t, x, y, z, itemBlock.getBlock());
        BlockRenderer.renderRightSide(t, x, y, z, itemBlock.getBlock());
        BlockRenderer.renderBottomSide(t, x, y, z, itemBlock.getBlock());
        BlockRenderer.renderFrontSide(t, x, y, z, itemBlock.getBlock());
        t.end();

        GL11.glEndList();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    @Override
    public void tick() {
        super.tick();

        this.moveRelative(0, 0, this.onGround ? 0.1F : 0.02F);
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.91F;
        this.yd *= 0.98F;
        this.zd *= 0.91F;
        this.yd = (float)((double)this.yd - 0.08D);
        if (this.onGround) {
            this.xd *= 0.6F;
            this.zd *= 0.6F;
        }
    }

    public void picked() {
        OpenCraft.unregisterRenderEvent(this.renderEvent);
        OpenCraft.unregisterTickEvent(this.tickEvent);
    }

}
