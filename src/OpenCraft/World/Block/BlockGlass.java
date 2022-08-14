package OpenCraft.World.Block;

import OpenCraft.Rendering.Texture;
import OpenCraft.Rendering.TextureEngine;

public class BlockGlass extends Block
{

    public BlockGlass(int idi)
    {
        super("opencraft:glass", idi);

    }

    @Override
    public boolean hasTranslucent() {
        return true;
    }

}
