package OpenCraft.World.Block;

import OpenCraft.Game.Rendering.Texture;
import OpenCraft.Game.Rendering.TextureManager;

public class BlockVoid extends Block
{

    public BlockVoid(int idi)
    {
        super("void", idi);
        this.texture = new Texture(0, 0, 0, 0, 0, 0, 0);
    }

}
