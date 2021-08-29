package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;

public class BlockLogOak extends Block
{

    public BlockLogOak(int idi)
    {
        super("log_oak", idi);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX("log_oak t");
        ty = TextureEngine.getBlockTextureY("log_oak t");
        bx = TextureEngine.getBlockTextureX("log_oak t");
        by = TextureEngine.getBlockTextureY("log_oak t");
        sx = TextureEngine.getBlockTextureX("log_oak s");
        sy = TextureEngine.getBlockTextureY("log_oak s");
        int id = TextureEngine.getBlockTextureId("log_oak s");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
