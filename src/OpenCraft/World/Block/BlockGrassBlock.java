package OpenCraft.World.Block;

import OpenCraft.Game.Rendering.Texture;
import OpenCraft.Game.Rendering.TextureManager;

public class BlockGrassBlock extends Block
{

    public BlockGrassBlock(int idi)
    {
        super("grass_block", idi);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureManager.getBlockTextureX("grass t");
        ty = TextureManager.getBlockTextureY("grass t");
        bx = TextureManager.getBlockTextureX("grass b");
        by = TextureManager.getBlockTextureY("grass b");
        sx = TextureManager.getBlockTextureX("grass s");
        sy = TextureManager.getBlockTextureY("grass s");
        int id = TextureManager.getBlockTextureId("grass s");

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
