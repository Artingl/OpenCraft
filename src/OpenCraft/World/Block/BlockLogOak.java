package OpenCraft.World.Block;

import OpenCraft.Game.Rendering.Texture;
import OpenCraft.Game.Rendering.TextureManager;

public class BlockLogOak extends Block
{

    public BlockLogOak()
    {
        super("log_oak");

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureManager.getBlockTextureX("log_oak t");
        ty = TextureManager.getBlockTextureY("log_oak t");
        bx = TextureManager.getBlockTextureX("log_oak t");
        by = TextureManager.getBlockTextureY("log_oak t");
        sx = TextureManager.getBlockTextureX("log_oak s");
        sy = TextureManager.getBlockTextureY("log_oak s");
        int id = TextureManager.getBlockTextureId("log_oak s");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
