package com.artingl.opencraft.Control.Game;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Display {

    private int width;
    private int height;
    private String title;
    private long display;
    private boolean isFullscreen;
    private boolean isResized;
    private boolean focus;
    private GLFWWindowSizeCallbackI sizeCallback;
    private GLFWWindowFocusCallbackI focusCallback;

    // is used for fullscreen function
    private final IntBuffer lastWindowX = BufferUtils.createIntBuffer(1);
    private final IntBuffer lastWindowY = BufferUtils.createIntBuffer(1);
    private final IntBuffer lastWindowW = BufferUtils.createIntBuffer(1);
    private final IntBuffer lastWindowH = BufferUtils.createIntBuffer(1);

    public Display(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.focus = true;
    }

    public void create() {
        if (!glfwInit())
            throw new IllegalStateException("Cannot initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_FALSE);

        this.display = glfwCreateWindow(this.width, this.height, this.title, 0, 0);
        if (this.display == 0L) {
            throw new RuntimeException("Cannot create GLFW window");

        }

        try (MemoryStack stack = stackPush()) {

            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(this.display, pWidth, pHeight);
            GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(this.display,
                    (mode.width()-pWidth.get(0))/2,
                    (mode.height()-pHeight.get(0))/2
            );

        } catch (Exception e) {
            throw e;
        }

        glfwMakeContextCurrent(this.display);
        glfwSwapInterval(1);
        glfwShowWindow(this.display);

        this.registerCallbacks();
    }

    private void registerCallbacks() {
        sizeCallback = (window, w, h) -> {
            isResized = true;
            if (w != 0)
                width = w;
            if (h != 0)
                height = h;
        };

        focusCallback = (window, focused) ->
                focus = focused;

        GLFW.glfwSetKeyCallback(this.display, Input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(this.display, Input.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(this.display, Input.getMouseButtonsCallback());
        GLFW.glfwSetCharCallback(this.display, Input.getUnicodeCharsCallback());
//        GLFW.glfwSetScrollCallback(this.display, Controls.getMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(this.display, sizeCallback);
        GLFW.glfwSetWindowFocusCallback(this.display, focusCallback);
    }

    public void createFrame() {
        GLFW.glfwPollEvents();
    }

    public void swapBuffers() {
//        System.out.println("aa");
        glfwSwapBuffers(this.display);
//        System.out.println("dd");
        GL11.glFlush();
    }

    public void destroy() {
        glfwFreeCallbacks(this.display);
        glfwDestroyWindow(this.display);
        glfwTerminate();
    }

    public boolean isClosed() {
        return glfwWindowShouldClose(this.display);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean isFullscreen) {
        isResized = true;
        if (isFullscreen) {
            GLFW.glfwGetWindowPos(this.display, lastWindowX, lastWindowY);
            GLFW.glfwGetWindowSize(this.display, lastWindowW, lastWindowH);
            GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowMonitor(this.display, GLFW.glfwGetPrimaryMonitor(), 0, 0, mode.width(), mode.height(), 0);
        } else {
            GLFW.glfwSetWindowMonitor(this.display, 0, lastWindowX.get(0), lastWindowY.get(0), width, height, 0);
            this.width = lastWindowW.get(0);
            this.height = lastWindowH.get(0);
            GLFW.glfwSetWindowSize(this.display, this.width, this.height);
        }

        this.isFullscreen = isFullscreen;
    }

    public boolean isResized() {
        boolean v = isResized;
        isResized = false;
        return v;
    }

    public long getDisplayId() {
        return display;
    }

    public boolean inFocus() {
        return focus;
    }
}
