package com.csdy.jzyy.ms.util.test;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import net.minecraft.server.packs.resources.Resource; // Required for font loading
// For GL_CLAMP_TO_EDGE

// Required for font loading
import javax.imageio.ImageIO;
import java.nio.channels.Channels; // Required for font loading
import java.nio.channels.ReadableByteChannel; // Required for font loading

import static org.lwjgl.glfw.GLFW.*;

// Required for font loading alternative
// Required for font loading alternative
// Required for font loading alternative
// Required for font loading alternative


public class OffScreenGl {
    // 这个 window 变量会在 init() 中被新创建的窗口句柄覆盖
    private long window;
    private static final float FONT_HEIGHT = 96.0f;
    private static final int ASCII_START = 32;
    private static final int NUM_CHARS = 96;



    //这里写ttf的位置
    ResourceLocation fontLocation = new ResourceLocation("jzyy", "font.ttf");

    // --- 字体渲染字段 ---
    private static final int BITMAP_W = 1024;
    private static final int BITMAP_H = 1024;
    private int fontTextureId = 0;
    private STBTTBakedChar.Buffer charData = null;
    private ByteBuffer fontBuffer = null;
    private boolean fontResourcesLoaded = false;
    private boolean fontTextureCreated = false;
    private boolean fontLoadFailed = false;
    private boolean fontBakeFailed = false;

    // --- 新窗口的状态字段 ---
    // 需要默认值，因为init()中获取实际尺寸前可能被访问
    private int windowWidth = 800;  // 默认宽度
    private int windowHeight = 600; // 默认高度

    // 构造函数：获取MC窗口句柄，但会被init()覆盖
    private long minecraftWindowHandle;
    public OffScreenGl() {
        this.minecraftWindowHandle = Minecraft.getInstance().window.getWindow();
        long mcWindowHandle = Minecraft.getInstance().window.getWindow();
        System.out.println("[Gl 构造函数] 获取MC窗口句柄: " + mcWindowHandle + " (将在init中被替换)");
        this.window = mcWindowHandle;
    }

    // --- 加载字体资源的方法 ---
    private void loadFontResources() {
        if (fontResourcesLoaded || fontLoadFailed) return;
        fontLoadFailed = false;
        System.out.println("[Gl] 加载字体资源: " + fontLocation);
        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(fontLocation)
                    .orElseThrow(() -> new IOException("字体资源未找到: " + fontLocation));
            try (InputStream is = resource.open()) {
                ByteBuffer buffer = MemoryUtil.memAlloc(1024 * 512);
                ReadableByteChannel rbc = Channels.newChannel(is);
                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) break;
                    if (buffer.remaining() == 0) {
                        ByteBuffer newBuffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 2);
                        if (newBuffer == null) { MemoryUtil.memFree(buffer); throw new IOException("无法重新分配字体缓冲区"); }
                        buffer = newBuffer; System.out.println("[Gl] 调整字体缓冲区大小: " + buffer.capacity());
                    }
                }
                buffer.flip();
                if (buffer.limit() == 0) { MemoryUtil.memFree(buffer); throw new IOException("字体文件为空: " + fontLocation); }
                this.fontBuffer = buffer; System.out.println("[Gl] 字体加载到缓冲区. 大小: " + buffer.limit() + " bytes.");
                this.charData = STBTTBakedChar.malloc(NUM_CHARS); System.out.println("[Gl] 已分配 STBTTBakedChar 缓冲区.");
                this.fontResourcesLoaded = true;
            }
        } catch (IOException e) {
            System.err.println("[Gl] 加载字体资源失败: " + fontLocation); e.printStackTrace();
            this.fontLoadFailed = true; cleanupNativeBuffers();
        }
    }

    // --- run() 方法 ---
    public void run() {
        System.out.println("[Gl run] 开始执行...");
        this.loadFontResources(); // 加载字体
        if (this.fontLoadFailed) {
            System.err.println("[Gl run] 因字体加载失败无法继续.");
            cleanup(); return;
        }

        try {
            System.out.println("[Gl run] 调用 init()...");
            this.init(); // *** 调用init，创建新窗口 ***
            System.out.println("[Gl run] init() 完成. 新窗口句柄: " + this.window);

            System.out.println("[Gl run] 调用 loop()...");
            this.loop(); // *** 调用loop，针对新窗口 ***
            System.out.println("[Gl run] loop() 完成.");
        } catch (Exception e) {
            System.err.println("[Gl run] 在 init() 或 loop() 中发生异常:"); e.printStackTrace();
        } finally {
            System.out.println("[Gl run] 进入 finally 块进行清理...");
            cleanup(); // 清理资源
        }
        System.out.println("[Gl run] 执行完毕.");
    }

    // --- 创建新窗口并且包含有问题的线程 ---
    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) { throw new IllegalStateException("无法初始化GLFW"); }
        else {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // 创建时不可见
            // glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // 保持可调整大小

            // +++ 让窗口置顶 (Always-on-top) +++
            glfwWindowHint(GLFW_FLOATING, GLFW_TRUE);

            // +++ 移除窗口装饰 (标题栏、按钮等)，使其无法通过按钮最小化 +++
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
            // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++

            // --- 移除后台线程 ---
            System.out.println("[Gl init] 后台窗口创建线程已被移除.");
            // ---------------------

            // *** 主线程创建新窗口并覆盖 this.window ***
            int initialWidth = Minecraft.getInstance().window.getWidth();
            int initialHeight = Minecraft.getInstance().window.getHeight();
            this.window = glfwCreateWindow(initialWidth, initialHeight, "你死了！", 0L, 0L); // 标题仍然设置，但不可见
            this.windowWidth = initialWidth; this.windowHeight = initialHeight;

            if (this.window == 0L) { glfwTerminate(); throw new RuntimeException("创建GLFW窗口失败"); }
            else {
                System.out.println("[Gl init] 成功创建新的置顶、无装饰GLFW窗口. 句柄: " + this.window);
                // 设置键盘回调 (按 ESC 关闭)
                glfwSetKeyCallback(this.window, (windowHandle, key, scancode, action, mods) -> {
                    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                        glfwSetWindowShouldClose(windowHandle, true);
                    }
                });
                // 设置大小调整回调
                glfwSetFramebufferSizeCallback(this.window, this::framebufferSizeCallback);
                // 居中窗口
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer pWidth = stack.mallocInt(1); IntBuffer pHeight = stack.mallocInt(1);
                    glfwGetWindowSize(this.window, pWidth, pHeight);
                    this.windowWidth = pWidth.get(0); this.windowHeight = pHeight.get(0);
                    GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                    if (vidmode != null) {
                        glfwSetWindowPos(this.window, (vidmode.width() - this.windowWidth) / 2, (vidmode.height() - this.windowHeight) / 2);
                        System.out.println("[Gl init] 已居中新窗口 ("+this.windowWidth+"x"+this.windowHeight+").");
                    } else { System.err.println("[Gl init] 无法获取主显示器视频模式用于居中."); }
                } catch (Throwable e) { System.err.println("[Gl init] 居中窗口时发生错误: " + e.getMessage()); }
                // 设置上下文
                glfwMakeContextCurrent(this.window);
                System.out.println("[Gl init] 已设置新窗口的上下文为当前.");
                // V-Sync
                glfwSwapInterval(1);
                // 显示窗口
                glfwShowWindow(this.window);
                System.out.println("[Gl init] 新的置顶、无装饰窗口已可见.");
            }
        }
        System.out.println("[Gl init] Init 完成.");
    }

    // --- 新窗口的 Framebuffer resize callback ---
    private void framebufferSizeCallback(long windowHandle, int width, int height) {
        if (windowHandle == this.window) { // 确认是我们的新窗口
            if (width > 0 && height > 0) {
                this.windowWidth = width; this.windowHeight = height;
                GL11.glViewport(0, 0, width, height);
                System.out.println("[Gl FBCallback] 新窗口调整大小: " + width + "x" + height + ". 视口已更新.");
            }
        }
    }

    // --- 烘焙字体并创建纹理 (需要GL上下文) ---
    private void completeFontTextureInitialization() {
        if (!fontResourcesLoaded || fontTextureCreated || fontLoadFailed || fontBakeFailed) return;
        if (this.window == 0 || GLFW.glfwGetCurrentContext() != this.window) {
            System.err.println("[Gl Font Init] 无法创建纹理 - 新窗口的GL上下文不是当前的!");
            fontBakeFailed = true; return;
        }
        System.out.println("[Gl Font Init] 完成字体初始化 (为新窗口烘焙位图和创建纹理)...");
        try {
            ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);
            int bakeResult = STBTruetype.stbtt_BakeFontBitmap(fontBuffer, FONT_HEIGHT, bitmap, BITMAP_W, BITMAP_H, ASCII_START, charData);
            if (bakeResult <= 0) { MemoryUtil.memFree(bitmap); fontBakeFailed = true; System.err.println("[Gl Font Init] 字体烘焙失败. Result: " + bakeResult); }
            else {
                System.out.println("[Gl Font Init] 字体烘焙成功. Max Y: " + bakeResult);
                fontTextureId = GL11.glGenTextures(); GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureId);
                System.out.println("[Gl Font Init] 生成字体纹理 ID: " + fontTextureId);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, bitmap);
                System.out.println("[Gl Font Init] 已上传位图为 GL_ALPHA 纹理.");
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE); GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); MemoryUtil.memFree(bitmap); System.out.println("[Gl Font Init] 已释放临时位图缓冲区.");
                fontTextureCreated = true; System.out.println("[Gl Font Init] 字体纹理初始化成功完成.");
            }
        } catch (Exception e) {
            System.err.println("[Gl Font Init] 字体纹理初始化期间发生异常:"); e.printStackTrace();
            fontBakeFailed = true; cleanupOpenGLResources();
        }
    }

    // --- 文本绘制实现 (在新窗口内绘制) ---
    private void drawTextInternal(String text, float centerX, float centerY) {
        if (!fontTextureCreated || fontTextureId == 0 || charData == null || fontBakeFailed || fontLoadFailed) return;

        GL11.glPushAttrib(GL11.GL_TEXTURE_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_VIEWPORT_BIT | GL11.GL_TRANSFORM_BIT);
        GL11.glPushMatrix(); // 保存模型视图矩阵

        try {
            // 为新窗口设置正交投影
            setupOrthoProjection(this.windowWidth, this.windowHeight);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); //这里设置文字颜色
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureId);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer x = stack.floats(0.0f); FloatBuffer y = stack.floats(0.0f); STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
                // 计算文本宽度
                float textWidth = 0; float firstX0 = 0; float lastX1 = 0; boolean firstChar = true;
                for (int i = 0; i < text.length(); i++) { char c = text.charAt(i);
                    if (c >= ASCII_START && c < ASCII_START + NUM_CHARS) { FloatBuffer tempX = stack.floats(x.get(0)); FloatBuffer tempY = stack.floats(y.get(0));
                        STBTruetype.stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - ASCII_START, tempX, tempY, q, true);
                        if (firstChar) { firstX0 = q.x0(); firstChar = false; } lastX1 = q.x1(); x.put(0, tempX.get(0));
                    } else if (c == '\n') break;
                }
                if (!firstChar) textWidth = lastX1 - firstX0; else textWidth = 0;
                // 计算起始位置
                float startX = centerX - textWidth / 2.0f; float startY = centerY + FONT_HEIGHT / 4.0f;
                x.put(0, startX); y.put(0, startY);
                // 实际绘制
                GL11.glBegin(GL11.GL_QUADS);
                for (int i = 0; i < text.length(); i++) { char c = text.charAt(i);
                    if (c >= ASCII_START && c < ASCII_START + NUM_CHARS) { STBTruetype.stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - ASCII_START, x, y, q, true);
                        GL11.glTexCoord2f(q.s0(), q.t0()); GL11.glVertex2f(q.x0(), q.y0()); GL11.glTexCoord2f(q.s1(), q.t0()); GL11.glVertex2f(q.x1(), q.y0());
                        GL11.glTexCoord2f(q.s1(), q.t1()); GL11.glVertex2f(q.x1(), q.y1()); GL11.glTexCoord2f(q.s0(), q.t1()); GL11.glVertex2f(q.x0(), q.y1());
                    } else if (c == '\n') { y.put(0, y.get(0) + FONT_HEIGHT); x.put(0, startX); }
                } GL11.glEnd();
            }
        } finally {
            // 恢复GL状态
            GL11.glMatrixMode(GL11.GL_MODELVIEW); GL11.glPopMatrix(); // 恢复模型视图矩阵
            GL11.glMatrixMode(GL11.GL_PROJECTION); GL11.glPopMatrix(); // 恢复投影矩阵
            GL11.glMatrixMode(GL11.GL_MODELVIEW); GL11.glPopAttrib(); // 恢复属性
        }
    }

    // --- 为新窗口设置正交投影 ---
    private void setupOrthoProjection(float width, float height) {
        GL11.glMatrixMode(GL11.GL_PROJECTION); GL11.glPushMatrix(); GL11.glLoadIdentity();
        GL11.glOrtho(0.0, width, height, 0.0, -1.0, 1.0); // 左上角原点
        GL11.glMatrixMode(GL11.GL_MODELVIEW); GL11.glLoadIdentity(); // 重置模型视图矩阵
    }

    // --- 这个循环操作的是 init() 创建的新窗口 ---
    private void loop() {
        try {
            GL.createCapabilities(); // 创建 OpenGL 功能绑定
            System.out.println("[Gl Loop] 为置顶窗口上下文创建了OpenGL功能.");
            System.out.println("[Gl Loop] OpenGL 版本: " + GL11.glGetString(GL11.GL_VERSION));
        } catch (Exception e) { System.err.println("[Gl Loop] 创建OpenGL功能失败! loop 无法继续."); e.printStackTrace(); return; }

        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F); // 设置清屏颜色为黑色

        completeFontTextureInitialization(); // 尝试初始化字体纹理

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer mcX = stack.mallocInt(1); // MC X 坐标
            IntBuffer mcY = stack.mallocInt(1); // MC Y 坐标
            IntBuffer mcWidthBuf = stack.mallocInt(1); // MC 宽度
            IntBuffer mcHeightBuf = stack.mallocInt(1); // MC 高度

            while (!glfwWindowShouldClose(this.window)) { // 循环直到置顶窗口关闭
                try {
                    // --- 检查并同步 MC 窗口状态 ---
                    if (this.minecraftWindowHandle != 0) {
                        if (glfwWindowShouldClose(this.minecraftWindowHandle)) {
                            System.out.println("[Gl Loop] 检测到 Minecraft 窗口关闭请求，正在关闭置顶窗口...");
                            glfwSetWindowShouldClose(this.window, true);
                            this.minecraftWindowHandle = 0L;
                            continue; // 跳过本帧剩余部分
                        }

                        // --- 同步位置 ---
                        glfwGetWindowPos(this.minecraftWindowHandle, mcX, mcY);
                        glfwSetWindowPos(this.window, mcX.get(0), mcY.get(0));
                        mcX.rewind(); mcY.rewind();
                        // -----------------

                        // +++ 同步大小 +++
                        glfwGetWindowSize(this.minecraftWindowHandle, mcWidthBuf, mcHeightBuf);
                        int mcWidth = mcWidthBuf.get(0);
                        int mcHeight = mcHeightBuf.get(0);
                        mcWidthBuf.rewind(); mcHeightBuf.rewind();

                        // 如果尺寸发生变化且有效，则设置置顶窗口大小
                        if ((mcWidth != this.windowWidth || mcHeight != this.windowHeight) && mcWidth > 0 && mcHeight > 0) {
                            glfwSetWindowSize(this.window, mcWidth, mcHeight);
                            // 注意：glfwSetWindowSize 可能不会立即更新 this.windowWidth/Height
                            // 但 framebufferSizeCallback 会处理视口更新。
                            // 为了比较准确，我们可以在这里手动更新，但回调优先。
                            // this.windowWidth = mcWidth; // 手动更新可能导致与回调冲突
                            // this.windowHeight = mcHeight;
                        }

                        // ++++++++++++++++
                    }
                    // -----------------------------

                    // --- 渲染逻辑 ---
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                    if (fontTextureCreated && !fontBakeFailed && !fontLoadFailed) {
                        // 使用 this.windowWidth/Height 进行居中绘制，它们会被回调更新
                        drawTextInternal("You Died!", (float)this.windowWidth / 2.0f, (float)this.windowHeight / 2.0f);
                    }
                    // ---------------

                    glfwSwapBuffers(this.window); // 交换缓冲区
                    glfwPollEvents();             // 处理事件

                } catch (Exception e) { // 捕获循环内的异常
                    System.err.println("[Gl Loop] 渲染循环中发生错误:"); e.printStackTrace();
                    glfwSetWindowShouldClose(this.window, true); // 发生错误时尝试关闭窗口退出循环
                }
            }
        } // MemoryStack mcX, mcY, mcWidthBuf, mcHeightBuf 会在此自动释放
        System.out.println("[Gl Loop] 退出置顶窗口的渲染循环.");
    }

    // --- 清理方法 ---
    private void cleanup() { /* ... 清理逻辑和之前一样，针对新窗口和字体资源 ... */
        System.out.println("[Gl Cleanup] 开始清理...");
        cleanupOpenGLResources(); // 清理字体纹理 (需要上下文)
        cleanupNativeBuffers();   // 清理字体数据缓冲区

        if (this.window != 0L) { // 清理新窗口相关的GLFW资源
            System.out.println("[Gl Cleanup] 清理GLFW回调并销毁新窗口 (句柄: " + this.window + ")...");
            try { Callbacks.glfwFreeCallbacks(this.window); System.out.println("[Gl Cleanup] 已释放GLFW回调.");
                GLFW.glfwDestroyWindow(this.window); System.out.println("[Gl Cleanup] 已销毁新GLFW窗口."); }
            catch (Exception e) { System.err.println("[Gl Cleanup] GLFW窗口清理期间发生异常: " + e.getMessage()); }
            this.window = 0L; // 标记窗口已销毁
        } else { System.out.println("[Gl Cleanup] 没有有效的新窗口句柄需要销毁."); }

        try { // 终止GLFW
            GLFW.glfwTerminate(); System.out.println("[Gl Cleanup] GLFW已终止.");
            GLFWErrorCallback callback = GLFW.glfwSetErrorCallback(null);
            if (callback != null) { callback.free(); System.out.println("[Gl Cleanup] 已释放GLFW错误回调."); }
        } catch(Exception e) { System.err.println("[Gl Cleanup] GLFW终止期间发生异常: " + e.getMessage()); }

        // 重置标志位
        fontResourcesLoaded = false; fontTextureCreated = false; fontLoadFailed = false; fontBakeFailed = false;
        System.out.println("[Gl Cleanup] 清理完成.");
    }
    private void cleanupNativeBuffers() { /* ... 和之前一样 ... */
        if (charData != null) { charData.free(); charData = null; System.out.println("[Gl Cleanup] Freed STBTTBakedChar buffer."); }
        if (fontBuffer != null) { MemoryUtil.memFree(fontBuffer); fontBuffer = null; System.out.println("[Gl Cleanup] Freed font data buffer."); }
    }
    private void cleanupOpenGLResources() { /* ... 和之前一样, 尝试在当前上下文删除纹理 ... */
        if (fontTextureId != 0) {
            if (this.window != 0 && GLFW.glfwGetCurrentContext() == this.window) {
                try { GL11.glDeleteTextures(fontTextureId); System.out.println("[Gl Cleanup] Deleted font texture (ID: " + fontTextureId + ")."); }
                catch (Exception e) { System.err.println("[Gl Cleanup] Exception deleting texture: " + e.getMessage()); }
            } else { System.err.println("[Gl Cleanup] Skipping texture deletion - context not current or window invalid."); }
            fontTextureId = 0;
        }
        fontTextureCreated = false;
    }
}