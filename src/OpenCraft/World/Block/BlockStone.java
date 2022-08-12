package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Item.Tool;

public class BlockStone extends Block
{

    public BlockStone(int idi)
    {
        super("stone", idi);

        float tx, ty, bx, by, sx, sy;
        tx = 0; ty = 0; bx = 0; by = 0; sx = 0; sy = 0;

        tx = TextureEngine.getBlockTextureX(this.getId());
        ty = TextureEngine.getBlockTextureY(this.getId());
        bx = TextureEngine.getBlockTextureX(this.getId());
        by = TextureEngine.getBlockTextureY(this.getId());
        sx = TextureEngine.getBlockTextureX(this.getId());
        sy = TextureEngine.getBlockTextureY(this.getId());
        int id = TextureEngine.getBlockTextureId(this.getId());
        this.setStrength(0.4f);
        this.setDefaultTool(Tool.PICKAXE);

        this.texture = new Texture(id, tx, ty, bx, by, sx, sy);

    }

}
