package com.artingl.opencraft.GL;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
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

    // callbacks
    private GLFWWindowSizeCallback sizeCallback;

    public Display(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void create() {
        if (!glfwInit())
            throw new IllegalStateException("Cannot initialize GLFW");

        glfwDefaultWindowHints();

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
        sizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int w, int h) {
                isResized = true;
                width = w;
                height = h;
            }
        };

        GLFW.glfwSetKeyCallback(this.display, Controls.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(this.display, Controls.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(this.display, Controls.getMouseButtonsCallback());
        GLFW.glfwSetCharCallback(this.display, Controls.getUnicodeCharsCallback());
//        GLFW.glfwSetScrollCallback(this.display, Controls.getMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(this.display, sizeCallback);
    }

    public void createFrame() {
        GLFW.glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(this.display);
    }

    public void destroy() {
        glfwFreeCallbacks(this.display);
        glfwDestroyWindow(this.display);
        glfwTerminate();

        this.sizeCallback.free();
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
//            org.lwjgl.glfw.GLFW.glfwGetWindowPos(this.display, windowPosX, windowPosY);
            GLFW.glfwSetWindowMonitor(this.display, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, 0);
        } else {
            GLFW.glfwSetWindowMonitor(this.display, 0, 0, 0, width, height, 0);
        }
    }

    public boolean isResized() {
        boolean v = isResized;
        isResized = false;
        return v;
    }

    public long getDisplayId() {
        return display;
    }
}
