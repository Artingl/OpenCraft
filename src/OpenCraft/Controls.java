package OpenCraft;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.HashMap;

public class Controls
{

    public static double MouseX; // Mouse x position
    public static double MouseY; // Mouse y position

    public static HashMap<Integer /* id */, Boolean /* state */> PressedKeyboardKeys; // All keyboard pressed keys
    public static HashMap<Integer /* id */, Boolean /* state */> PressedMouseKeys; // All mouse pressed keys

    public static void init() throws LWJGLException {
        Mouse.create();
        PressedKeyboardKeys = new HashMap<Integer, Boolean>();
        PressedMouseKeys = new HashMap<Integer, Boolean>();
        for (int i = Keyboard.KEY_1; i < Keyboard.KEY_SLEEP; i++)
        {
            PressedKeyboardKeys.put(i, false);
        }
        for (int i = 0; i < 20; i++) { PressedMouseKeys.put(i, false); }
    }

    public static void destroy() {
        Mouse.destroy();
    }

    public static void update()
    {
        MouseX = Mouse.getX();
        MouseY = Mouse.getY();

        for (int i = Keyboard.KEY_1; i < Keyboard.KEY_SLEEP; i++)
        {
            if (Keyboard.isKeyDown(i))
            {
                PressedKeyboardKeys.put(i, true);
            }
            else {
                PressedKeyboardKeys.put(i, false);
            }
        }

        while (Mouse.next())
        {
            PressedMouseKeys.put(Mouse.getEventButton(), Mouse.getEventButtonState());
        }
    }

    public static boolean getKeyboardKey(int id)
    {
        return PressedKeyboardKeys.get(id);
    }

    public static boolean getMouseKey(int id)
    {
        return PressedMouseKeys.get(id);
    }

    public static int getMouseX()
    {
        return Mouse.getX();
    }

    public static int getMouseY()
    {
        return Mouse.getY();
    }

}
