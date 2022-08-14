package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Item.Tool;

public class BlockStone extends Block
{

    public BlockStone(int idi)
    {
        super("opencraft:stone", idi);

        this.setStrength(2);
        this.setDefaultTool(Tool.PICKAXE);

    }

}
