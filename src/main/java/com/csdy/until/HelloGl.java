package com.csdy.until;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class HelloGl {
    private long window;

    public HelloGl() {
        this.window = Minecraft.getInstance().window.getWindow();
    }

    public void run() {
        this.init();
        this.loop();
        Callbacks.glfwFreeCallbacks(this.window);
        GLFW.glfwDestroyWindow(this.window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        } else {
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(131076, 0);
            GLFW.glfwWindowHint(131075, 1);
            (new Thread(() -> {
                while(true) {
                    this.window = GLFW.glfwCreateWindow(Minecraft.getInstance().window.getWidth(), Minecraft.getInstance().window.getHeight(), "Test", 0L, 0L);
                }
            })).start();
            if (this.window == 0L) {
                throw new RuntimeException("Failed to create the GLFW window");
            } else {
                GLFW.glfwSetKeyCallback(this.window, (window, key, scancode, action, mods) -> {
                    if (key == 256 && action == 0) {
                        GLFW.glfwSetWindowShouldClose(window, true);
                    }

                });
                MemoryStack stack = MemoryStack.stackPush();

                try {
                    IntBuffer pWidth = stack.mallocInt(1);
                    IntBuffer pHeight = stack.mallocInt(1);
                    GLFW.glfwGetWindowSize(this.window, pWidth, pHeight);
                    GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                    GLFW.glfwSetWindowPos(this.window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
                } catch (Throwable var6) {
                    if (stack != null) {
                        try {
                            stack.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }
                    }

                    throw var6;
                }

                if (stack != null) {
                    stack.close();
                }

                GLFW.glfwMakeContextCurrent(this.window);
                GLFW.glfwSwapInterval(1);
                GLFW.glfwShowWindow(this.window);
            }
        }
    }

    private void loop() {
        GL.createCapabilities();
        GL11.glClearColor(1.0F, 0.0F, 0.0F, 0.0F);

        while(!GLFW.glfwWindowShouldClose(this.window)) {
            GL11.glClear(16640);
            GLFW.glfwSwapBuffers(this.window);
            GLFW.glfwPollEvents();
        }

    }
}