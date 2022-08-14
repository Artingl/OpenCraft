package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;
import OpenCraft.World.Item.Tool;

public class BlockBedrock extends Block
{

    public BlockBedrock(int idi)
    {
        super("opencraft:bedrock", idi);
        this.setDefaultTool(Tool.UNBREAKABLE);
    }

}
