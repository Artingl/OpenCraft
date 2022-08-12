package OpenCraft.World.Entity.Gamemode;

public class Creative extends Gamemode {

    public static final int id = 1;

    public static Creative instance = new Creative();

    private Creative() {}

    public int getId() {
        return Creative.id;
    }
}
