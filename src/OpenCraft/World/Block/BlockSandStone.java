package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;

public class BlockSandStone extends Block
{

    public BlockSandStone(int idi)
    {
        super("sandstone", idi);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX("sandstone t");
        ty = TextureEngine.getBlockTextureY("sandstone t");
        bx = TextureEngine.getBlockTextureX("sandstone b");
        by = TextureEngine.getBlockTextureY("sandstone b");
        sx = TextureEngine.getBlockTextureX("sandstone s");
        sy = TextureEngine.getBlockTextureY("sandstone s");
        int id = TextureEngine.getBlockTextureId("sandstone s");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
