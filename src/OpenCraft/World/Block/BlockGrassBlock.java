package OpenCraft.World.Block;

import OpenCraft.Game.Rendering.Texture;
import OpenCraft.Game.Rendering.TextureEngine;

public class BlockGrassBlock extends Block
{

    public BlockGrassBlock(int idi)
    {
        super("grass_block", idi);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX("grass t");
        ty = TextureEngine.getBlockTextureY("grass t");
        bx = TextureEngine.getBlockTextureX("grass b");
        by = TextureEngine.getBlockTextureY("grass b");
        sx = TextureEngine.getBlockTextureX("grass s");
        sy = TextureEngine.getBlockTextureY("grass s");
        int id = TextureEngine.getBlockTextureId("grass s");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
