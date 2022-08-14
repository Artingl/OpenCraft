package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;

public class BlockGrassBlock extends Block
{

    public BlockGrassBlock(int idi)
    {
        super("opencraft:grass_block", idi, false);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX("opencraft:grass_top");
        ty = TextureEngine.getBlockTextureY("opencraft:grass_top");
        bx = TextureEngine.getBlockTextureX("opencraft:grass_bottom");
        by = TextureEngine.getBlockTextureY("opencraft:grass_bottom");
        sx = TextureEngine.getBlockTextureX("opencraft:grass_side");
        sy = TextureEngine.getBlockTextureY("opencraft:grass_side");
        int id = TextureEngine.getBlockTextureId("opencraft:grass_side");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
