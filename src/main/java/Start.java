import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.OpenCraft;

public class Start
{
    public static void main(String[] args) {
        try {
            new OpenCraft(args);
        } catch (Exception e) {
            Logger.exception("Critical error while running the game!", e);
            Logger.closeOutput();
            System.exit(-1);
        }
    }

}
