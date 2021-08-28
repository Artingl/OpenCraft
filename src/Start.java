import OpenCraft.OpenCraft;
import org.lwjgl.LWJGLException;

public class Start
{
    public static void main(String[] args)
    {
        Thread mainThread = new Thread(() -> {
            try {
                new OpenCraft();
            } catch (Exception e) { e.printStackTrace(); }
        });
        mainThread.start();

    }

}
