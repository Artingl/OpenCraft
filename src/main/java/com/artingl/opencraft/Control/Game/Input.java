package com.artingl.opencraft.Control.Game;

import com.artingl.opencraft.GUI.Screens.Screen;
import com.artingl.opencraft.Math.Vector2i;
import com.artingl.opencraft.Opencraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Input
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
               KEY_ENTER = GLFW.GLFW_KEY_ENTER,
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

    public static class Buttons {
        public static int
                BUTTON_LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT,
                BUTTON_RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                BUTTON_MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
    }

    public enum ClickType {
        SINGLE, DOUBLE
    };

    public enum MouseState {
        DOWN, HOLD, UP
    };

    private static class KeyState {
        public long keyTimer;
        public long sinceLastClick;
        public boolean click = false;
        public int keyClicks;

        protected KeyState() {
        }

    }

    public static class KeyInput {
        public String character;
        public int keyCode;
        public ClickType clickType;

        protected KeyInput(String c, int m, ClickType clickType) {
            this.character = c;
            this.keyCode = m;
            this.clickType = clickType;
        }

        @Override
        public String toString() {
            return "Key{Char=" + character + ", KeyCode=" + keyCode + ", ClickType=" + clickType + "}";
        }
    }

    private static class UnicodeCallback {
        public Consumer<KeyInput> consumer;
        public Object obj;

        protected UnicodeCallback(Consumer<KeyInput> consumer, Object obj) {
            this.consumer = consumer;
            this.obj = obj;
        }
    }

    private static class MouseCallback {
        public Consumer<MouseInput> consumer;
        public Object obj;

        protected MouseCallback(Consumer<MouseInput> consumer, Object obj) {
            this.consumer = consumer;
            this.obj = obj;
        }
    }

    public static class MouseInput {
        public int button;
        public MouseState state;
        public Vector2i mousePosition;

        protected MouseInput(int button, MouseState state, Vector2i mousePosition) {
            this.button = button;
            this.state = state;
            this.mousePosition = mousePosition;
        }

        @Override
        public String toString() {
            return "Mouse{Button=" + button + ", MouseState=" + state+ ", MousePosition=" + mousePosition + "}";
        }
    }


    protected static boolean isGrabbed;

    protected static int MouseX;
    protected static int MouseY;

    protected static float MouseDX;
    protected static float MouseDY;

    protected static int lastUnicodeId = 0;
    protected static int lastMouseId = 0;

    protected static HashMap<Integer, MouseCallback> mouseCallbacks;
    protected static HashMap<Integer, UnicodeCallback> unicodeCallbacks;
    protected static HashMap<Integer, KeyState> pressedKeyboardKeysState;
    protected static ArrayList<Boolean> pressedKeyboardKeys;
    protected static ArrayList<Boolean> pressedKeyboardKeysUnicode;
    protected static ArrayList<Boolean> pressedMouseKeys;

    protected static GLFWScrollCallbackI scrollCallback;
    protected static GLFWCursorPosCallbackI cursorPosCallback;
    protected static GLFWMouseButtonCallbackI mouseButtonCallback;
    protected static GLFWCharCallbackI charCallback;
    protected static GLFWKeyCallbackI keyCallback;

    public static void init() {
        isGrabbed = false;
        mouseCallbacks = new HashMap<>();
        unicodeCallbacks = new HashMap<>();
        pressedKeyboardKeysState = new HashMap<>();
        pressedKeyboardKeysUnicode = new ArrayList<>();
        pressedKeyboardKeys = new ArrayList<>();
        pressedMouseKeys = new ArrayList<>();
        for (int i = 0; i < 1024; i++) pressedKeyboardKeysState.put(i, new KeyState());
        for (int i = 0; i < 65535; i++) pressedKeyboardKeysUnicode.add(false);
        for (int i = 0; i < 1024; i++) pressedKeyboardKeys.add(false);
        for (int i = 0; i < 128; i++) pressedMouseKeys.add(false);
    }

    public static void destroy() {
        pressedKeyboardKeysUnicode.clear();
        pressedKeyboardKeys.clear();
        pressedMouseKeys.clear();
        pressedKeyboardKeysState.clear();
        mouseCallbacks.clear();
    }

    public static boolean isKeyDown(int id)
    {
        return pressedKeyboardKeys.get(id);
    }

    public static boolean getMouseKey(int id)
    {
        return pressedMouseKeys.get(id);
    }

    public static void update() {
        if (isGrabbed && Opencraft.getDisplay().inFocus()) {
            MouseDX = getMouseX() - (int) (Opencraft.getWidth() / 2f);
            MouseDY = ((int) (Opencraft.getHeight() / 2f)) - getRealMouseY();

            setMousePos((int) (Opencraft.getWidth() / 2f), (int) (Opencraft.getHeight() / 2f));
        }
        else {
            MouseDX = 0;
            MouseDY = 0;
        }

        int i = 0;
        for (Map.Entry<Integer, KeyState> entry: new HashMap<>(pressedKeyboardKeysState).entrySet()) {
            KeyState keyState = entry.getValue();

            if (pressedKeyboardKeys.get(i) && (keyState.keyTimer + (keyState.keyClicks < 2 ? 500 : 20) < System.currentTimeMillis()))
            {
                ClickType clickType = ClickType.SINGLE;
                if (!keyState.click) {
                    clickType = (keyState.sinceLastClick + 200 > System.currentTimeMillis()) ? ClickType.DOUBLE : ClickType.SINGLE;
                    keyState.sinceLastClick = System.currentTimeMillis();
                }

                for (Map.Entry<Integer, UnicodeCallback> entry_input: new HashMap<>(unicodeCallbacks).entrySet()) {
                    if (Opencraft.isGuiOpened()) {
                        if (entry_input.getValue().obj instanceof Screen)
                            entry_input.getValue().consumer.accept(new KeyInput("", i, clickType));
                    }
                    else entry_input.getValue().consumer.accept(new KeyInput("", i, clickType));
                }

                keyState.keyTimer = System.currentTimeMillis();
                keyState.keyClicks++;
                keyState.click = true;
            }
            else if (!pressedKeyboardKeys.get(i)) {
                keyState.keyTimer = 0;
                keyState.keyClicks = 0;
                keyState.click = false;
            }

            i++;
        }

        for (Map.Entry<Integer, MouseCallback> entry: mouseCallbacks.entrySet()) {
            for (int j = 0; j < 10; j++) {
                if (getMouseKey(j)) {
                    entry.getValue().consumer.accept(new MouseInput(j, MouseState.HOLD, getMousePos()));
                }
            }
        }
    }

    public static Vector2i getMousePos() {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        GLFW.glfwGetCursorPos(Opencraft.getDisplay().getDisplayId(), x, y);
        return new Vector2i((int) x.rewind().get(), (int) (Opencraft.getDisplay().getHeight() - y.rewind().get()));
    }

    public static int getMouseX()
    {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        GLFW.glfwGetCursorPos(Opencraft.getDisplay().getDisplayId(), x, y);
        return (int) x.rewind().get();
    }

    public static int getMouseY()
    {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        GLFW.glfwGetCursorPos(Opencraft.getDisplay().getDisplayId(), x, y);
        return (int) (Opencraft.getDisplay().getHeight() - y.rewind().get());
    }

    public static int getRealMouseY()
    {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        GLFW.glfwGetCursorPos(Opencraft.getDisplay().getDisplayId(), x, y);
        return (int) y.rewind().get();
    }

    public static void setMouseX(float x) {
        GLFW.glfwSetCursorPos(Opencraft.getDisplay().getDisplayId(), x, getMouseY());
    }

    public static void setMouseY(float y) {
        GLFW.glfwSetCursorPos(Opencraft.getDisplay().getDisplayId(), getMouseX(), y);
    }

    public static void setMousePos(float x, float y) {
        GLFW.glfwSetCursorPos(Opencraft.getDisplay().getDisplayId(), x, y);
    }

    public static void setMousePos(int x, int y) {
        GLFW.glfwSetCursorPos(Opencraft.getDisplay().getDisplayId(), x, y);
    }

    public static float getDX() {
        return MouseDX;
    }

    public static float getDY() {
        return MouseDY;
    }

    public static void setMouseGrabbed(boolean b) {
        if (b && !isGrabbed) {
            setMousePos((int) (Opencraft.getWidth() / 2f), (int) (Opencraft.getHeight() / 2f));
        }

        isGrabbed = b;

        if (!isGrabbed || !Opencraft.getDisplay().inFocus()) {
            GLFW.glfwSetInputMode(Opencraft.getDisplay().getDisplayId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
        else if (isGrabbed) {
            GLFW.glfwSetInputMode(Opencraft.getDisplay().getDisplayId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        }
    }

    public static int registerKeyboardHandler(Object self, Consumer<KeyInput> r) {
        int id = lastUnicodeId++;
        unicodeCallbacks.put(id, new UnicodeCallback(r, self));
        return id;
    }

    public static void unregisterKeyboardHandler(int id) {
        unicodeCallbacks.remove(id);
    }

    public static int registerMouseHandler(Object self, Consumer<MouseInput> r) {
        int id = lastMouseId++;
        mouseCallbacks.put(id, new MouseCallback(r, self));
        return id;
    }

    public static void unregisterMouseHandler(int id) {
        mouseCallbacks.remove(id);
    }

    public static GLFWScrollCallbackI getMouseScrollCallback() {
        return null;
    }

    public static GLFWCursorPosCallbackI getMouseMoveCallback() {
        if (cursorPosCallback == null) {
            cursorPosCallback = (window, xpos, ypos) -> {
                MouseX = (int) (xpos);
                MouseY = (int) (Opencraft.getDisplay().getHeight() - ypos);
            };
        }

        return cursorPosCallback;
    }

    public static GLFWMouseButtonCallbackI getMouseButtonsCallback() {
        if (mouseButtonCallback == null) {
            mouseButtonCallback = (window, button, action, mods) -> {
                pressedMouseKeys.set(button, action == 1);

                for (Map.Entry<Integer, MouseCallback> entry: mouseCallbacks.entrySet()) {
                    entry.getValue().consumer.accept(new MouseInput(button, action == 0 ? MouseState.UP : MouseState.DOWN, getMousePos()));
                }
            };
        }

        return mouseButtonCallback;
    }

    public static GLFWKeyCallbackI getKeyboardCallback() {
        if (keyCallback == null) {
            keyCallback = (window, key, scancode, action, mods) ->
            {
                if (key > 0)
                    pressedKeyboardKeys.set(key, action == 1 || action == 2);
            };
        }

        return keyCallback;
    }

    public static GLFWCharCallbackI getUnicodeCharsCallback() {
        if (charCallback == null) {
            charCallback = (window, codepoint) -> {
                for (Map.Entry<Integer, UnicodeCallback> entry: unicodeCallbacks.entrySet()) {
                    if (Opencraft.isGuiOpened()) {
                        if (entry.getValue().obj instanceof Screen)
                            entry.getValue().consumer.accept(new KeyInput(String.valueOf(Character.toChars(codepoint)[0]), -1, ClickType.SINGLE));
                    }
                    else entry.getValue().consumer.accept(new KeyInput(String.valueOf(Character.toChars(codepoint)[0]), -1, ClickType.SINGLE));
                }
            };
        }

        return charCallback;
    }

}
