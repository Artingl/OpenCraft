package com.artingl.opencraft.GL;

import com.artingl.opencraft.OpenCraft;
import org.lwjgl.glfw.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Controls
{

    public static class Keys {
       public static final int
               KEY_ESCAPE = GLFW.GLFW_KEY_ESCAPE,
               KEY_1 = GLFW.GLFW_KEY_1,
               KEY_2 = GLFW.GLFW_KEY_2,
               KEY_3 = GLFW.GLFW_KEY_3,
               KEY_4 = GLFW.GLFW_KEY_4,
               KEY_5 = GLFW.GLFW_KEY_5,
               KEY_6 = GLFW.GLFW_KEY_6,
               KEY_7 = GLFW.GLFW_KEY_7,
               KEY_8 = GLFW.GLFW_KEY_8,
               KEY_9 = GLFW.GLFW_KEY_9,
               KEY_0 = GLFW.GLFW_KEY_0,
               KEY_MINUS = GLFW.GLFW_KEY_MINUS,
               KEY_EQUALS = GLFW.GLFW_KEY_EQUAL,
               KEY_TAB = GLFW.GLFW_KEY_TAB,
               KEY_Q = GLFW.GLFW_KEY_Q,
               KEY_W = GLFW.GLFW_KEY_W,
               KEY_E = GLFW.GLFW_KEY_E,
               KEY_R = GLFW.GLFW_KEY_R,
               KEY_T = GLFW.GLFW_KEY_T,
               KEY_Y = GLFW.GLFW_KEY_Y,
               KEY_U = GLFW.GLFW_KEY_U,
               KEY_I = GLFW.GLFW_KEY_I,
               KEY_O = GLFW.GLFW_KEY_O,
               KEY_P = GLFW.GLFW_KEY_P,
               KEY_LBRACKET = GLFW.GLFW_KEY_LEFT_BRACKET,
               KEY_RBRACKET = GLFW.GLFW_KEY_RIGHT_BRACKET,
               KEY_RETURN = GLFW.GLFW_KEY_ENTER,
               KEY_LCONTROL = GLFW.GLFW_KEY_LEFT_CONTROL,
               KEY_A = GLFW.GLFW_KEY_A,
               KEY_S = GLFW.GLFW_KEY_S,
               KEY_D = GLFW.GLFW_KEY_D,
               KEY_F = GLFW.GLFW_KEY_F,
               KEY_G = GLFW.GLFW_KEY_G,
               KEY_H = GLFW.GLFW_KEY_H,
               KEY_J = GLFW.GLFW_KEY_J,
               KEY_K = GLFW.GLFW_KEY_K,
               KEY_L = GLFW.GLFW_KEY_L,
               KEY_SEMICOLON = GLFW.GLFW_KEY_SEMICOLON,
               KEY_APOSTROPHE = GLFW.GLFW_KEY_APOSTROPHE,
               KEY_GRAVE = GLFW.GLFW_KEY_GRAVE_ACCENT,
               KEY_LSHIFT = GLFW.GLFW_KEY_LEFT_SHIFT,
               KEY_BACKSLASH = GLFW.GLFW_KEY_BACKSLASH,
               KEY_Z = GLFW.GLFW_KEY_Z,
               KEY_X = GLFW.GLFW_KEY_X,
               KEY_C = GLFW.GLFW_KEY_C,
               KEY_V = GLFW.GLFW_KEY_V,
               KEY_B = GLFW.GLFW_KEY_B,
               KEY_N = GLFW.GLFW_KEY_N,
               KEY_M = GLFW.GLFW_KEY_M,
               KEY_COMMA = GLFW.GLFW_KEY_COMMA,
               KEY_PERIOD = GLFW.GLFW_KEY_PERIOD,
               KEY_SLASH = GLFW.GLFW_KEY_SLASH,
               KEY_RSHIFT = GLFW.GLFW_KEY_RIGHT_SHIFT,
               KEY_MULTIPLY = GLFW.GLFW_KEY_KP_MULTIPLY,
               KEY_MENU = GLFW.GLFW_KEY_MENU,
               KEY_SPACE = GLFW.GLFW_KEY_SPACE,
               KEY_CAPSLOCK = GLFW.GLFW_KEY_CAPS_LOCK,
               KEY_F1 = GLFW.GLFW_KEY_F1,
               KEY_F2 = GLFW.GLFW_KEY_F2,
               KEY_F3 = GLFW.GLFW_KEY_F3,
               KEY_F4 = GLFW.GLFW_KEY_F4,
               KEY_F5 = GLFW.GLFW_KEY_F5,
               KEY_F6 = GLFW.GLFW_KEY_F6,
               KEY_F7 = GLFW.GLFW_KEY_F7,
               KEY_F8 = GLFW.GLFW_KEY_F8,
               KEY_F9 = GLFW.GLFW_KEY_F9,
               KEY_F10 = GLFW.GLFW_KEY_F10,
               KEY_F11 = GLFW.GLFW_KEY_F11,
               KEY_F12 = GLFW.GLFW_KEY_F12,
               KEY_F13 = GLFW.GLFW_KEY_F13,
               KEY_F14 = GLFW.GLFW_KEY_F14,
               KEY_F15 = GLFW.GLFW_KEY_F15,
               KEY_F16 = GLFW.GLFW_KEY_F16,
               KEY_F17 = GLFW.GLFW_KEY_F17,
               KEY_F18 = GLFW.GLFW_KEY_F18,
               KEY_F19 = GLFW.GLFW_KEY_F19,
               KEY_BACKSPACE = GLFW.GLFW_KEY_BACKSPACE,
               KEY_COLON = GLFW.GLFW_KEY_SEMICOLON,
               KEY_RCONTROL = GLFW.GLFW_KEY_RIGHT_CONTROL,
               KEY_DIVIDE = GLFW.GLFW_KEY_KP_DIVIDE,
               KEY_PAUSE = GLFW.GLFW_KEY_PAUSE,
               KEY_HOME = GLFW.GLFW_KEY_HOME,
               KEY_UP = GLFW.GLFW_KEY_UP,
               KEY_LEFT = GLFW.GLFW_KEY_LEFT,
               KEY_RIGHT = GLFW.GLFW_KEY_RIGHT,
               KEY_END = GLFW.GLFW_KEY_END,
               KEY_DOWN = GLFW.GLFW_KEY_DOWN,
               KEY_INSERT = GLFW.GLFW_KEY_INSERT,
               KEY_DELETE = GLFW.GLFW_KEY_DELETE;
    }

    protected static boolean isGrabbed;

    protected static int MouseX;
    protected static int MouseY;

    protected static int MouseDX;
    protected static int MouseDY;

    protected static HashMap<Integer, Consumer<Integer>> unicodeCallbacks;
    protected static ArrayList<Boolean> PressedKeyboardKeys;
    protected static ArrayList<Boolean> PressedKeyboardKeysUnicode;
    protected static ArrayList<Boolean> PressedMouseKeys;

    protected static GLFWScrollCallbackI scrollCallback;
    protected static GLFWCursorPosCallbackI cursorPosCallback;
    protected static GLFWMouseButtonCallbackI mouseButtonCallback;
    protected static GLFWCharCallbackI charCallback;
    protected static GLFWKeyCallbackI keyCallback;

    public static void init() {
        isGrabbed = false;
        unicodeCallbacks = new HashMap<>();
        PressedKeyboardKeysUnicode = new ArrayList<>();
        PressedKeyboardKeys = new ArrayList<>();
        PressedMouseKeys = new ArrayList<>();
        for (int i = 0; i < 65535; i++) PressedKeyboardKeysUnicode.add(false);
        for (int i = 0; i < 1024; i++) PressedKeyboardKeys.add(false);
        for (int i = 0; i < 128; i++) PressedMouseKeys.add(false);
    }

    public static void destroy() {
        PressedKeyboardKeys.clear();
        PressedMouseKeys.clear();
    }

    public static boolean isKeyDown(int id)
    {
        return PressedKeyboardKeys.get(id);
    }

    public static boolean getMouseKey(int id)
    {
        return PressedMouseKeys.get(id);
    }

    public static void update() {

    }

    public static int getMouseX()
    {
        return MouseX;
    }

    public static int getMouseY()
    {
        return MouseY;
    }

    public static float getDX() {
        return 0;
    }

    public static float getDY() {
        return 0;
    }

    public static void setMouseGrabbed(boolean b) {
        isGrabbed = b;
    }

    public static int registerKeyboardHandler(Consumer<Integer> r) {
        unicodeCallbacks.put(unicodeCallbacks.size(), r);
        return unicodeCallbacks.size() -1;
    }

    public static void unregisterKeyboardHandler(int id) {
        unicodeCallbacks.remove(id);
    }

    public static GLFWScrollCallbackI getMouseScrollCallback() {
        return null;
    }

    public static GLFWCursorPosCallbackI getMouseMoveCallback() {
        if (cursorPosCallback == null) {
            cursorPosCallback = (window, xpos, ypos) -> {
                MouseX = (int) (xpos);
                MouseY = (int) (OpenCraft.getDisplay().getHeight() - ypos);
            };
        }

        return cursorPosCallback;
    }

    public static GLFWMouseButtonCallbackI getMouseButtonsCallback() {
        if (mouseButtonCallback == null) {
            mouseButtonCallback = (window, button, action, mods) ->
                    PressedMouseKeys.set(button, action == 1);
        }

        return mouseButtonCallback;
    }

    public static GLFWKeyCallbackI getKeyboardCallback() {
        if (keyCallback == null) {
            keyCallback = (window, key, scancode, action, mods) -> {
                PressedKeyboardKeys.set(key, action == 1 || action == 2);
            };
        }

        return keyCallback;
    }

    public static GLFWCharCallbackI getUnicodeCharsCallback() {
        if (charCallback == null) {
            charCallback = new GLFWCharCallback() {
                @Override
                public void invoke(long window, int codepoint) {
                    for (Map.Entry<Integer, Consumer<Integer>> entry: unicodeCallbacks.entrySet()) {
                        entry.getValue().accept(codepoint);
                    }
                }
            };
        }

        return charCallback;
    }

}
