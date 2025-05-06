package com.csdy.jzyy.ms.util.test;

import com.csdy.jzyy.ms.util.SoundPlayer;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL; // 使用 MemoryUtil.NULL 更清晰

// 1. 重命名类以反映其目的
public class OverlayTextWindow implements Runnable { // 实现 Runnable 表明它适合在单独线程运行

    // --- Getter (可选) ---
    // --- 窗口句柄 ---
    // 2. 明确区分窗口句柄
    @Getter
    private long overlayWindowHandle = NULL; // 我们创建的覆盖窗口句柄，初始化为 NULL
    private long minecraftWindowHandle = NULL; // 缓存的 Minecraft 主窗口句柄

    // --- 字体渲染常量和字段 ---
    private static final float FONT_HEIGHT = 96.0f; // 字体渲染高度
    private static final int ASCII_START = 32;      // ASCII 字符起始点 (' ')
    private static final int NUM_CHARS = 96;        // 要烘焙的字符数量 (ASCII 32 到 127)
    private static final int BITMAP_W = 1024;       // 字体位图宽度
    private static final int BITMAP_H = 1024;       // 字体位图高度

    private final ResourceLocation fontLocation;    // TTF 字体文件的 ResourceLocation
    private int fontTextureId = 0;                  // 字体纹理的 OpenGL ID
    private STBTTBakedChar.Buffer charData = null;  // 存储烘焙后的字符度量信息
    private ByteBuffer fontBuffer = null;           // 存储从文件加载的原始 TTF 数据

    // 字体加载状态标志
    private boolean fontResourcesLoaded = false; // TTF 数据是否已加载到内存
    private boolean fontTextureCreated = false;  // OpenGL 纹理是否已创建
    private boolean fontLoadFailed = false;      // TTF 文件加载或解析是否失败
    private boolean fontBakeFailed = false;      // 字体位图烘焙或上传到纹理是否失败

    // --- 覆盖窗口状态字段 ---
    private volatile int windowWidth = 800;  // 覆盖窗口当前宽度 (volatile 保证线程可见性)
    private volatile int windowHeight = 600; // 覆盖窗口当前高度 (volatile 保证线程可见性)
    private final String windowTitle = "你死了！"; // 窗口标题（即使无装饰也可能有用）
    private final String textToRender = "You Died!"; // 要显示的文本

    /**
     * 构造函数
     * @param fontResourceLocation 指向 TTF 字体文件的 ResourceLocation
     */
    public OverlayTextWindow(ResourceLocation fontResourceLocation) {
        this.fontLocation = fontResourceLocation;
        // 不再在构造函数中获取 MC 窗口句柄，推迟到 run() 或 init() 中
        // 保证在需要时获取最新的句柄，并处理获取失败的情况
    }

    /**
     * 加载字体文件到内存缓冲区 (不依赖 OpenGL 上下文)
     */
    private void loadFontResources() {
        if (fontResourcesLoaded || fontLoadFailed) return; // 防止重复加载或失败后重试

        System.out.println("[Overlay] 开始加载字体资源: " + fontLocation);
        try {
            // 使用 Minecraft 资源管理器获取资源
            Optional<Resource> resourceOpt = Minecraft.getInstance().getResourceManager().getResource(fontLocation);
            if (resourceOpt.isEmpty()) {
                throw new IOException("字体资源未找到 (Optional为空): " + fontLocation);
            }

            // 使用 try-with-resources 确保 InputStream 被关闭
            try (InputStream is = resourceOpt.get().open();
                 ReadableByteChannel rbc = Channels.newChannel(is)) {

                // 初始分配，并根据需要重新分配以读取整个文件
                int initialBufferSize = 512 * 1024; // 初始 512KB，对于大多数字体足够
                ByteBuffer buffer = MemoryUtil.memAlloc(initialBufferSize);
                try {
                    while (true) {
                        int bytesRead = rbc.read(buffer);
                        if (bytesRead == -1) break; // 文件读取完毕
                        if (!buffer.hasRemaining()) { // 缓冲区已满，需要扩容
                            System.out.println("[Overlay] 字体缓冲区需要扩容 (当前容量: " + buffer.capacity() + ")");
                            int newCapacity = buffer.capacity() * 2;
                            ByteBuffer newBuffer = MemoryUtil.memRealloc(buffer, newCapacity);
                            if (newBuffer == null) {
                                throw new IOException("无法重新分配字体缓冲区内存 (容量: " + newCapacity + ")");
                            }
                            buffer = newBuffer;
                        }
                    }
                    buffer.flip(); // 准备从缓冲区读取数据

                    if (buffer.limit() == 0) {
                        throw new IOException("读取的字体文件为空: " + fontLocation);
                    }

                    this.fontBuffer = buffer; // 保存字体数据缓冲区
                    this.charData = STBTTBakedChar.malloc(NUM_CHARS); // 分配存储烘焙数据的结构
                    this.fontResourcesLoaded = true;
                    System.out.println("[Overlay] 字体资源加载成功. 缓冲区大小: " + buffer.limit() + " bytes.");

                } catch (IOException e) {
                    MemoryUtil.memFree(buffer); // 如果在读取或处理中出错，释放已分配的缓冲区
                    throw e; // 重新抛出异常
                }
            }
        } catch (Exception e) { // 捕获所有可能的异常 (IOException, NullPointerException等)
            System.err.println("[Overlay] 加载字体资源失败: " + fontLocation);
            e.printStackTrace();
            this.fontLoadFailed = true;
            cleanupNativeBuffers(); // 清理可能已部分分配的本地资源
        }
    }

    /**
     * 主执行方法，通常在新线程中调用
     */
    @Override
    public void run() {
        System.out.println("[Overlay run] 覆盖窗口线程启动...");
        loadFontResources(); // 1. 加载字体文件数据
        SoundPlayer.tryPlayMillenniumSnowAsync("hated_by_life_itself.wav");
        if (fontLoadFailed) {
            System.err.println("[Overlay run] 因字体加载失败，无法启动覆盖窗口。");
            cleanup(); // 确保即使启动失败也清理资源
            return;
        }

        try {
            // 2. 初始化 GLFW 并创建覆盖窗口
            System.out.println("[Overlay run] 初始化 GLFW 和覆盖窗口...");
            init();
            System.out.println("[Overlay run] 覆盖窗口初始化完成. 句柄: " + this.overlayWindowHandle);

            // 3. 进入渲染循环
            System.out.println("[Overlay run] 进入渲染循环...");
            loop();
            System.out.println("[Overlay run] 渲染循环结束.");

        } catch (Exception e) {
            System.err.println("[Overlay run] 在窗口初始化或渲染循环中发生严重错误:");
            e.printStackTrace();
        } finally {
            // 4. 无论如何都执行清理
            System.out.println("[Overlay run] 执行最终清理...");
            cleanup();
            System.out.println("[Overlay run] 覆盖窗口线程结束.");
        }
    }

    /**
     * 初始化 GLFW, 创建覆盖窗口并设置 OpenGL 上下文
     */
    private void init() {
        // 获取 Minecraft 窗口句柄，用于同步
        this.minecraftWindowHandle = Minecraft.getInstance().window.getWindow();
        if (this.minecraftWindowHandle == NULL) {
            // 如果无法获取 MC 窗口，可以决定是失败退出还是允许窗口独立存在
            System.err.println("[Overlay init] 警告: 未能获取 Minecraft 窗口句柄，将无法同步位置/大小。");
            // throw new IllegalStateException("未能获取 Minecraft 窗口句柄"); // 或者直接抛异常
        } else {
            System.out.println("[Overlay init] 获取到 Minecraft 窗口句柄: " + this.minecraftWindowHandle);
        }


        // 设置 GLFW 错误回调
        GLFWErrorCallback.createPrint(System.err).set();

        // 初始化 GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("无法初始化 GLFW");
        }

        // 配置窗口提示 (Hints)
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // 创建时不可见，配置完成后再显示
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // 禁止调整大小 (如果需要同步大小，这里可以设为TRUE，但无边框窗口调整体验不好)
        glfwWindowHint(GLFW_FLOATING, GLFW_TRUE);   // 使窗口置顶
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE); // 移除窗口边框和标题栏

        // 获取初始尺寸 (可以基于MC窗口或预设值)
        int initialWidth = 800;
        int initialHeight = 600;
        if (this.minecraftWindowHandle != NULL) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                glfwGetWindowSize(this.minecraftWindowHandle, w, h);
                if (w.get(0) > 0 && h.get(0) > 0) {
                    initialWidth = w.get(0);
                    initialHeight = h.get(0);
                } else {
                    System.err.println("[Overlay init] 获取的 MC 窗口尺寸无效 ("+w.get(0)+"x"+h.get(0)+"), 使用默认尺寸 "+initialWidth+"x"+initialHeight);
                }
            } catch (Exception e){
                System.err.println("[Overlay init] 获取 MC 窗口尺寸时出错, 使用默认尺寸. Error: " + e.getMessage());
            }
        }
        this.windowWidth = initialWidth;
        this.windowHeight = initialHeight;

        // 创建覆盖窗口
        this.overlayWindowHandle = glfwCreateWindow(this.windowWidth, this.windowHeight, this.windowTitle, NULL, NULL);
        if (this.overlayWindowHandle == NULL) {
            glfwTerminate(); // 创建失败时需要终止GLFW
            throw new RuntimeException("创建覆盖窗口失败 (GLFW)");
        }
        System.out.println("[Overlay init] 成功创建覆盖窗口. 句柄: " + this.overlayWindowHandle);

        // 设置窗口回调
        glfwSetKeyCallback(this.overlayWindowHandle, this::keyCallback); // ESC关闭
        glfwSetFramebufferSizeCallback(this.overlayWindowHandle, this::framebufferSizeCallback); // 处理窗口大小变化（如果允许）

        // 尝试将窗口居中显示
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidmode != null) {
                glfwSetWindowPos(
                        this.overlayWindowHandle,
                        (vidmode.width() - this.windowWidth) / 2,
                        (vidmode.height() - this.windowHeight) / 2
                );
                System.out.println("[Overlay init] 已将覆盖窗口居中 (" + this.windowWidth + "x" + this.windowHeight + ").");
            } else {
                System.err.println("[Overlay init] 无法获取主显示器视频模式，无法居中窗口。");
            }
        } catch (Exception e) {
            System.err.println("[Overlay init] 居中窗口时发生错误: " + e.getMessage());
        }

        // 设置 OpenGL 上下文
        glfwMakeContextCurrent(this.overlayWindowHandle);
        System.out.println("[Overlay init] 已设置覆盖窗口的 OpenGL 上下文为当前.");

        // 3. 在设置上下文之后创建 OpenGL 功能绑定
        try {
            GL.createCapabilities();
            System.out.println("[Overlay init] 成功创建 OpenGL 功能绑定.");
            System.out.println("[Overlay init] OpenGL 版本: " + GL11.glGetString(GL11.GL_VERSION));
        } catch (Exception e) {
            System.err.println("[Overlay init] 创建 OpenGL 功能绑定失败!");
            glfwDestroyWindow(this.overlayWindowHandle); // 清理已创建的窗口
            glfwTerminate();
            throw new RuntimeException("创建 OpenGL 功能绑定失败", e);
        }

        // 启用 V-Sync (垂直同步)
        glfwSwapInterval(1);

        // 使窗口可见
        glfwShowWindow(this.overlayWindowHandle);
        System.out.println("[Overlay init] 覆盖窗口已设置为可见.");
        System.out.println("[Overlay init] 初始化完成.");
    }

    /**
     * 键盘事件回调 (处理 ESC 关闭)
     */
    private void keyCallback(long windowHandle, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            System.out.println("[Overlay Callback] 检测到 ESC 键释放，请求关闭窗口。");
            glfwSetWindowShouldClose(windowHandle, true);
        }
    }

    /**
     * 窗口帧缓冲大小变化回调
     */
    private void framebufferSizeCallback(long windowHandle, int width, int height) {
        if (windowHandle == this.overlayWindowHandle) {
            if (width > 0 && height > 0) {
                this.windowWidth = width;
                this.windowHeight = height;
                // 需要确保 OpenGL 上下文是当前的才能调用 glViewport
                // 如果回调发生在非渲染线程，这里可能会出问题，但 GLFW 通常在主线程或指定线程调用
                if (glfwGetCurrentContext() == this.overlayWindowHandle) {
                    GL11.glViewport(0, 0, width, height);
                    System.out.println("[Overlay Callback] 覆盖窗口大小调整为: " + width + "x" + height + ". 视口已更新.");
                } else {
                    System.err.println("[Overlay Callback] 警告: 帧缓冲大小回调发生在非当前上下文，未更新视口。");
                }
            } else {
                System.out.println("[Overlay Callback] 接收到无效的窗口尺寸: " + width + "x" + height);
            }
        }
    }

    /**
     * 烘焙字体位图并创建/上传到 OpenGL 纹理 (需要当前 GL 上下文)
     */
    private void completeFontTextureInitialization() {
        // 检查是否需要执行或是否已失败
        if (!fontResourcesLoaded || fontTextureCreated || fontLoadFailed || fontBakeFailed) {
            return;
        }
        // 检查 OpenGL 上下文是否正确
        if (this.overlayWindowHandle == NULL || glfwGetCurrentContext() != this.overlayWindowHandle) {
            System.err.println("[Overlay Font Init] 无法完成字体纹理初始化 - 覆盖窗口的 OpenGL 上下文无效或不是当前的!");
            // 可以选择标记失败，或等待下次机会
            // this.fontBakeFailed = true;
            return;
        }

        System.out.println("[Overlay Font Init] 尝试烘焙字体位图并创建 OpenGL 纹理...");
        ByteBuffer bitmap = null; // 在 try 外部声明以便 finally 中可以访问
        try {
            bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H); // 分配用于存储位图的内存

            // 使用 STB Truetype 烘焙字体位图
            int bakeResult = STBTruetype.stbtt_BakeFontBitmap(fontBuffer, FONT_HEIGHT, bitmap, BITMAP_W, BITMAP_H, ASCII_START, charData);

            if (bakeResult <= 0) { // 检查烘焙结果
                fontBakeFailed = true;
                System.err.println("[Overlay Font Init] 字体烘焙失败 (stbtt_BakeFontBitmap 返回 " + bakeResult + "). 可能字体大小过大或位图尺寸不足。");
            } else {
                System.out.println("[Overlay Font Init] 字体烘焙成功. 最大 Y 坐标: " + bakeResult + " (负值表示失败).");

                // 创建 OpenGL 纹理
                fontTextureId = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureId);
                System.out.println("[Overlay Font Init] 生成字体纹理 ID: " + fontTextureId);

                // 设置纹理参数 (线性过滤, 边缘裁剪)
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

                // 上传位图数据到纹理 (使用 GL_ALPHA 格式，因为 STB 只生成灰度图)
                // 重要：确保像素解包对齐方式正确，对于单字节灰度图通常是 1
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, bitmap);
                // 恢复默认对齐方式 (通常是 4)
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);

                System.out.println("[Overlay Font Init] 已上传位图数据到 OpenGL 纹理.");
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); // 解绑纹理

                fontTextureCreated = true; // 标记纹理创建成功
                System.out.println("[Overlay Font Init] 字体纹理初始化成功完成.");
            }
        } catch (Exception e) {
            System.err.println("[Overlay Font Init] 字体纹理初始化期间发生异常:");
            e.printStackTrace();
            fontBakeFailed = true;
            cleanupOpenGLResources(); // 尝试清理可能部分创建的 GL 资源
        } finally {
            if (bitmap != null) {
                MemoryUtil.memFree(bitmap); // 无论成功失败，都释放临时位图缓冲区
                System.out.println("[Overlay Font Init] 已释放临时位图缓冲区.");
            }
        }
    }

    /**
     * 内部文本绘制逻辑 (使用旧版 OpenGL)
     * @param text 要绘制的字符串
     * @param centerX 绘制中心的 X 坐标 (窗口坐标系)
     * @param centerY 绘制中心的 Y 坐标 (窗口坐标系)
     */
    private void drawTextInternal(String text, float centerX, float centerY) {
        // 检查字体纹理是否准备就绪
        if (!fontTextureCreated || fontTextureId == 0 || charData == null) {
            // 可以选择在这里打印一条信息，说明为何未绘制
            // System.err.println("[Overlay Draw] 字体未就绪，无法绘制文本.");
            return;
        }

        // 保存 OpenGL 状态 (很重要，避免干扰其他渲染)
        GL11.glPushAttrib(GL11.GL_TEXTURE_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_VIEWPORT_BIT | GL11.GL_TRANSFORM_BIT);
        GL11.glPushMatrix(); // 保存模型视图矩阵

        try {
            // 为覆盖窗口设置正交投影 (左上角为 0,0)
            setupOrthoProjection(this.windowWidth, this.windowHeight);

            // 配置渲染状态
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND); // 启用混合以处理透明度
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // 标准 Alpha 混合
            GL11.glDisable(GL11.GL_DEPTH_TEST); // 2D 文本不需要深度测试
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // 设置文本颜色为白色 (RGBA)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureId); // 绑定字体纹理

            // 使用 MemoryStack 分配临时坐标和四边形数据
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer x = stack.floats(0.0f); // 当前绘制位置 X
                FloatBuffer y = stack.floats(0.0f); // 当前绘制位置 Y
                STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack); // 用于获取字符四边形信息

                // 1. 计算文本总宽度以实现居中
                float textWidth = calculateTextWidth(text, stack);

                // 2. 计算绘制起始位置
                // 注意：stbtt 返回的 Y 坐标是基线，我们需要调整使其垂直居中
                // FONT_HEIGHT 大约是字符的高度，除以 2 近似中心，但可能需要微调
                float startX = centerX - textWidth / 2.0f;
                float startY = centerY - FONT_HEIGHT / 2.0f + (FONT_HEIGHT * 0.75f); // 调整垂直位置，可能需要实验
                // 或者更简单的垂直居中：float startY = centerY - (q.y1() - q.y0()) / 2.0f; 但 q 在循环外不可用

                x.put(0, startX);
                y.put(0, startY);

                // 3. 遍历字符串并绘制每个字符的四边形
                GL11.glBegin(GL11.GL_QUADS);
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (c == '\n') { // 处理换行符
                        y.put(0, y.get(0) + FONT_HEIGHT); // Y 向下移动一行
                        x.put(0, startX); // X 回到起始列
                        continue;
                    }
                    if (c >= ASCII_START && c < ASCII_START + NUM_CHARS) { // 只处理已烘焙的字符
                        // 获取字符的屏幕坐标和纹理坐标
                        STBTruetype.stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - ASCII_START, x, y, q, true);
                        // 绘制四边形
                        GL11.glTexCoord2f(q.s0(), q.t0()); GL11.glVertex2f(q.x0(), q.y0());
                        GL11.glTexCoord2f(q.s1(), q.t0()); GL11.glVertex2f(q.x1(), q.y0());
                        GL11.glTexCoord2f(q.s1(), q.t1()); GL11.glVertex2f(q.x1(), q.y1());
                        GL11.glTexCoord2f(q.s0(), q.t1()); GL11.glVertex2f(q.x0(), q.y1());
                    }
                    // else: 可以选择忽略或替换不支持的字符
                }
                GL11.glEnd();
            }
        } finally {
            // 恢复 OpenGL 状态
            // 确保按正确的顺序弹出矩阵
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix(); // 恢复投影矩阵
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix(); // 恢复模型视图矩阵
            GL11.glPopAttrib(); // 恢复其他属性
        }
    }

    /**
     * 辅助方法：计算给定字符串的渲染宽度
     * @param text 字符串
     * @param stack 当前 MemoryStack
     * @return 文本宽度（像素）
     */
    private float calculateTextWidth(String text, MemoryStack stack) {
        float width = 0;
        FloatBuffer x = stack.floats(0.0f);
        FloatBuffer y = stack.floats(0.0f); // y 坐标在此计算中不重要
        STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

        float currentX = 0.0f; // 模拟绘制过程中的 x 位置
        float lineStartX = 0.0f; // 当前行的起始 x
        float maxLineWidth = 0.0f; // 处理多行文本时记录最宽行

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                maxLineWidth = Math.max(maxLineWidth, currentX - lineStartX);
                currentX = lineStartX; // 回到行首 x (对于单行居中，这个可以简化)
                // y 不影响宽度计算
                continue;
            }
            if (c >= ASCII_START && c < ASCII_START + NUM_CHARS) {
                // 传入模拟的 x, y 获取下一个位置
                x.put(0, currentX);
                STBTruetype.stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - ASCII_START, x, y, q, true);
                // 更新模拟的 x 位置
                currentX = x.get(0);
            }
        }
        // 记录最后一行的宽度
        maxLineWidth = Math.max(maxLineWidth, currentX - lineStartX);

        // 对于单行文本，宽度就是最后一个字符的 x1 减去第一个字符的 x0
        // 这种模拟方式更通用，处理字距调整
        return maxLineWidth; // 返回最宽行的宽度
    }


    /**
     * 设置正交投影矩阵 (左上角 0,0)
     * @param width 视口宽度
     * @param height 视口高度
     */
    private void setupOrthoProjection(float width, float height) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix(); // 保存之前的投影矩阵
        GL11.glLoadIdentity(); // 重置投影矩阵
        // 设置正交投影，Y 轴向下 (0 在顶部)
        GL11.glOrtho(0.0, width, height, 0.0, -1.0, 1.0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix(); // 保存之前的模型视图矩阵
        GL11.glLoadIdentity(); // 重置模型视图矩阵
    }

    /**
     * 覆盖窗口的主渲染循环
     */
    private void loop() {
        // OpenGL 功能已在 init() 中创建

        // 设置清屏颜色 (黑色，完全不透明)
        // 如果需要窗口透明效果，这里需要设置为 (0,0,0,0) 并且窗口创建时需要支持透明度 (GLFW_TRANSPARENT_FRAMEBUFFER)
        // 但这会增加复杂性，并且可能需要特定的驱动/系统支持。
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);

        // 尝试完成字体纹理初始化 (如果之前未完成)
        completeFontTextureInitialization();

        // 使用 MemoryStack 管理循环内分配的本地缓冲区
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer mcX = stack.mallocInt(1);
            IntBuffer mcY = stack.mallocInt(1);
            IntBuffer mcWidthBuf = stack.mallocInt(1);
            IntBuffer mcHeightBuf = stack.mallocInt(1);

            // 主循环，直到窗口被要求关闭
            while (!glfwWindowShouldClose(this.overlayWindowHandle)) {
                try {
                    // --- 同步与 Minecraft 窗口 ---
                    if (this.minecraftWindowHandle != NULL) {
                        // 检查 MC 窗口是否已关闭
                        if (glfwWindowShouldClose(this.minecraftWindowHandle)) {
                            System.out.println("[Overlay Loop] 检测到 Minecraft 窗口关闭，正在关闭覆盖窗口...");
                            glfwSetWindowShouldClose(this.overlayWindowHandle, true);
                            this.minecraftWindowHandle = NULL; // 避免后续再检查
                            continue; // 跳过本帧渲染
                        }

                        // 同步位置
                        glfwGetWindowPos(this.minecraftWindowHandle, mcX, mcY);
                        glfwSetWindowPos(this.overlayWindowHandle, mcX.get(0), mcY.get(0));
                        mcX.rewind(); mcY.rewind(); // 重置 buffer 位置以便下次读取

                        // 同步大小 (只有在覆盖窗口可调整大小时才有意义，或者确保初始同步正确)
                        // 如果覆盖窗口是不可调整大小的，可能不需要每帧同步大小
                        glfwGetWindowSize(this.minecraftWindowHandle, mcWidthBuf, mcHeightBuf);
                        int mcWidth = mcWidthBuf.get(0);
                        int mcHeight = mcHeightBuf.get(0);
                        mcWidthBuf.rewind(); mcHeightBuf.rewind();

//                         如果覆盖窗口允许调整大小，并且 MC 尺寸变化了
//                         if (glfwGetWindowAttrib(overlayWindowHandle, GLFW_RESIZABLE) == GLFW_TRUE) {
//                             if ((mcWidth != this.windowWidth || mcHeight != this.windowHeight) && mcWidth > 0 && mcHeight > 0) {
//                                 glfwSetWindowSize(this.overlayWindowHandle, mcWidth, mcHeight);
//                                 // framebufferSizeCallback 会处理视口和 this.windowWidth/Height 的更新
//                             }
//                         }
                    }
                    // -----------------------------

                    // --- 渲染覆盖窗口内容 ---
                    // 清除颜色缓冲区和深度缓冲区 (虽然可能没用深度)
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                    // 如果字体已就绪，则绘制文本
                    if (fontTextureCreated) {
                        // 使用当前窗口的宽度和高度进行居中绘制
                        drawTextInternal(this.textToRender, (float)this.windowWidth / 2.0f, (float)this.windowHeight / 2.0f);
                    } else if (fontLoadFailed || fontBakeFailed) {
                        // 可选：如果字体加载失败，绘制一条错误信息（需要一个备用字体或简单线条）
                        // drawTextInternal("Font Load Failed!", ...); // 但这会依赖 drawTextInternal 本身
                    }
                    // -------------------------

                    // 交换前后缓冲区，将绘制内容显示出来
                    glfwSwapBuffers(this.overlayWindowHandle);

                    // 处理窗口事件 (如键盘输入、关闭请求等)
                    glfwPollEvents();

                } catch (Exception e) { // 捕获单次循环内的错误
                    System.err.println("[Overlay Loop] 渲染循环中发生错误:");
                    e.printStackTrace();
                    // 发生错误时，最好关闭窗口以避免无限错误循环
                    glfwSetWindowShouldClose(this.overlayWindowHandle, true);
                }
            } // 结束 while 循环
        } // MemoryStack 会在这里自动释放 mcX, mcY 等缓冲区
        System.out.println("[Overlay Loop] 退出覆盖窗口的渲染循环.");
    }

    /**
     * 清理所有资源
     */
    private void cleanup() {
        System.out.println("[Overlay Cleanup] 开始清理资源...");

        // 清理 OpenGL 资源 (需要在正确的上下文，或者至少尝试)
        cleanupOpenGLResources();

        // 清理原生内存缓冲区
        cleanupNativeBuffers();

        // 清理 GLFW 窗口和回调
        if (this.overlayWindowHandle != NULL) {
            System.out.println("[Overlay Cleanup] 清理覆盖窗口 (句柄: " + this.overlayWindowHandle + ")...");
            try {
                // 确保在销毁窗口前移除回调，虽然 glfwDestroyWindow 通常会处理
                glfwSetKeyCallback(this.overlayWindowHandle, null);
                glfwSetFramebufferSizeCallback(this.overlayWindowHandle, null);
                Callbacks.glfwFreeCallbacks(this.overlayWindowHandle); // 释放所有回调
                System.out.println("[Overlay Cleanup] 已释放覆盖窗口的 GLFW 回调.");
                glfwDestroyWindow(this.overlayWindowHandle);
                System.out.println("[Overlay Cleanup] 已销毁覆盖窗口.");
            } catch (Exception e) {
                System.err.println("[Overlay Cleanup] 清理覆盖窗口时发生异常: " + e.getMessage());
            } finally {
                this.overlayWindowHandle = NULL; // 标记窗口已销毁
            }
        } else {
            System.out.println("[Overlay Cleanup] 没有有效的覆盖窗口句柄需要销毁.");
        }

        // 终止 GLFW (只有当不再需要 GLFW 时才调用，如果还有其他 GLFW 窗口则不应调用)
        // 假设这个类管理的是唯一的 GLFW 实例，或者它是最后一个使用者
        try {
            glfwTerminate();
            System.out.println("[Overlay Cleanup] GLFW 已终止.");
            // 释放错误回调
            GLFWErrorCallback callback = glfwSetErrorCallback(null);
            if (callback != null) {
                callback.free();
                System.out.println("[Overlay Cleanup] 已释放 GLFW 错误回调.");
            }
        } catch (Exception e) { // GLFW 可能已在其他地方终止
            System.err.println("[Overlay Cleanup] 终止 GLFW 或释放回调时发生异常: " + e.getMessage());
        }

        // 重置状态标志
        fontResourcesLoaded = false;
        fontTextureCreated = false;
        fontLoadFailed = false;
        fontBakeFailed = false;
        System.out.println("[Overlay Cleanup] 清理完成.");
    }

    /**
     * 清理原生内存缓冲区 (字体数据, 烘焙数据)
     */
    private void cleanupNativeBuffers() {
        if (charData != null) {
            charData.free();
            charData = null;
            System.out.println("[Overlay Cleanup] 已释放 STBTTBakedChar 缓冲区.");
        }
        if (fontBuffer != null) {
            MemoryUtil.memFree(fontBuffer);
            fontBuffer = null;
            System.out.println("[Overlay Cleanup] 已释放字体数据缓冲区.");
        }
    }

    /**
     * 清理 OpenGL 资源 (主要是字体纹理)
     * 需要在正确的 OpenGL 上下文是当前的情况下调用才有效
     */
    private void cleanupOpenGLResources() {
        if (fontTextureId != 0) {
            // 尝试在删除前确保上下文是当前的，但这可能无法保证
            // 更健壮的方式是在拥有上下文的线程的清理逻辑中调用 glDeleteTextures
            if (this.overlayWindowHandle != NULL && glfwGetCurrentContext() == this.overlayWindowHandle) {
                try {
                    GL11.glDeleteTextures(fontTextureId);
                    System.out.println("[Overlay Cleanup] 已删除字体纹理 (ID: " + fontTextureId + ").");
                } catch (Exception e) { // 捕获可能的 GL 错误
                    System.err.println("[Overlay Cleanup] 删除字体纹理时发生异常: " + e.getMessage());
                }
            } else {
                System.err.println("[Overlay Cleanup] 无法删除字体纹理 - 覆盖窗口上下文不是当前的或窗口无效。纹理可能泄露！");
                // 在这种情况下，纹理资源会泄露，直到程序退出
            }
            fontTextureId = 0; // 无论是否成功删除，都重置 ID
        }
        fontTextureCreated = false; // 重置纹理创建标志
    }

}
