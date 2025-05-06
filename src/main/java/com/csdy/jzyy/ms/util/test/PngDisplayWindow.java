package com.csdy.jzyy.ms.util.test;

import com.csdy.jzyy.ms.util.SoundPlayer;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import org.lwjgl.system.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*; // 使用 GL30 常量以支持 VAO 等
import static org.lwjgl.opengl.GL11.*; // 仍然需要 GL11 的一些常量和函数
import static org.lwjgl.opengl.GL13.*; // For glActiveTexture
import static org.lwjgl.opengl.GL15.*; // For VBO functions
import static org.lwjgl.opengl.GL20.*; // For shader functions
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * 创建一个独立的、置顶的 GLFW 窗口，用于显示指定的 PNG 图片。
 * 该窗口的大小和位置会尝试与 Minecraft 主窗口同步。
 * 实现了 Runnable 接口，应在单独线程中运行。
 */
public class PngDisplayWindow implements Runnable {

    ///直接拿去用吧，底下是实现
    private static final ResourceLocation DEATH_SCREEN_LOCATION = new ResourceLocation("jzyy", "textures/gui/propose.png");
    private static PngDisplayWindow deathWindowInstance = null;
    private static Thread deathWindowThread = null;


    public static void showDeathScreen() {
        //播放音乐的线程，可以去掉
        SoundPlayer.tryPlayMillenniumSnowAsync("propose.wav");

        // --- Enhanced Logging ---
        long threadId = Thread.currentThread().getId();

        // --- Check if a window thread is already active ---
        // This is the primary guard against multiple windows
        Thread currentThread = deathWindowThread; // Read volatile variable once
        if (currentThread != null && currentThread.isAlive()) {
            return;
        }

        // --- Resource Loading and Window Creation Logic ---
        Minecraft mc = Minecraft.getInstance();
        ResourceManager resourceManager = mc.getResourceManager();
        InputStream imageStream = null;
        PngDisplayWindow newInstance = null; // Temporary variable for the new instance
        Thread newThread = null; // Temporary variable for the new thread

        try {
            System.out.println("[" + threadId + "] showDeathScreen: Loading resource: " + DEATH_SCREEN_LOCATION);
            Optional<Resource> resourceOpt = resourceManager.getResource(DEATH_SCREEN_LOCATION);
            if (resourceOpt.isEmpty()) {
                System.err.println("[" + threadId + "] ERROR: Resource not found: " + DEATH_SCREEN_LOCATION);
                // if (isCreatingDeathWindow.get()) isCreatingDeathWindow.set(false); // Release lock if used
                return;
            }

            imageStream = resourceOpt.get().open(); // Don't close here

            int width = 800; // 默认，后面会根据mc窗口大小改
            int height = 600;

            newInstance = new PngDisplayWindow(
                    imageStream,
                    DEATH_SCREEN_LOCATION.toString(),
                    width, height, "游戏结束", true);
            //其实标题没什么意义，后面会隐藏


            newThread = new Thread(newInstance, "离屏线程" + System.currentTimeMillis()); // Unique name
            deathWindowInstance = newInstance;
            deathWindowThread = newThread;
            newThread.start();
            imageStream = null;

        } catch (IOException e) {
            System.err.println("[" + threadId + "] ERROR: IOException during resource loading/window creation for " + DEATH_SCREEN_LOCATION + ": " + e.getMessage());
            e.printStackTrace();
            if (imageStream != null) { try { imageStream.close(); } catch (IOException ignored) {} }
            // Reset static fields if creation failed mid-way
            deathWindowInstance = null;
            deathWindowThread = null;
        } catch (Exception e) {
            System.err.println("[" + threadId + "] ERROR: Unexpected exception during window creation for " + DEATH_SCREEN_LOCATION + ": " + e.getMessage());
            e.printStackTrace();
            if (imageStream != null) { try { imageStream.close(); } catch (IOException ignored) {} }
            deathWindowInstance = null;
            deathWindowThread = null;
        }

    }

    // --- 窗口句柄 ---
    private long window = NULL; // 我们创建的覆盖窗口句柄
    private long minecraftWindowHandle = NULL; // Minecraft 主窗口句柄

    // --- 窗口属性 ---
    private volatile int width;  // 窗口当前宽度 (volatile 保证线程可见性)
    private volatile int height; // 窗口当前高度 (volatile 保证线程可见性)
    private final String title;
    private final boolean alwaysOnTop;

    // --- 图像资源 ---
    private final InputStream imageStream;    // 从构造函数传入的图像流
    private final String imageIdentifier; // 用于日志记录的图像标识符

    // --- OpenGL 对象 ID ---
    private int textureId = 0;
    private int vaoId = 0;
    private int vboId = 0;
    private int shaderProgramId = 0;
    private int projectionMatrixLocation = -1; //投影矩阵 uniform 位置

    // --- 顶点着色器 (带投影矩阵) ---
    private final String vertexShaderSource = """
            #version 330 core
            layout (location = 0) in vec2 aPos;        // 顶点位置 (-1 to 1)
            layout (location = 1) in vec2 aTexCoord;   // 纹理坐标 (0 to 1)

            out vec2 TexCoord;

            uniform mat4 projection; // 投影矩阵

            void main()
            {
                // 直接使用标准化坐标乘以投影矩阵 (通常是单位矩阵)
                gl_Position = projection * vec4(aPos.x, aPos.y, 0.0, 1.0);
                TexCoord = aTexCoord; // 传递纹理坐标给片段着色器
            }
            """;

    // --- 片段着色器 ---
    private final String fragmentShaderSource = """
            #version 330 core
            out vec4 FragColor; // 输出的像素颜色

            in vec2 TexCoord; // 从顶点着色器接收的纹理坐标

            uniform sampler2D textureSampler; // 纹理采样器

            void main()
            {
                // 从纹理中采样颜色
                FragColor = texture(textureSampler, TexCoord);
            }
            """;

    /**
     * 构造函数
     * @param imageStream PNG 图像的输入流。该类将负责关闭此流。
     * @param imageIdentifier 图像的标识符，用于日志记录 (例如 ResourceLocation.toString())
     * @param initialWidth 窗口的初始宽度 (如果无法获取MC窗口尺寸，则使用此值)
     * @param initialHeight 窗口的初始高度 (如果无法获取MC窗口尺寸，则使用此值)
     * @param title 窗口标题
     * @param alwaysOnTop 是否将窗口设置为总在最前
     */
    public PngDisplayWindow(InputStream imageStream, String imageIdentifier, int initialWidth, int initialHeight, String title, boolean alwaysOnTop) {
        if (imageStream == null) {
            throw new IllegalArgumentException("Image InputStream cannot be null");
        }
        this.imageStream = imageStream;
        this.imageIdentifier = imageIdentifier;
        this.width = initialWidth;
        this.height = initialHeight;
        this.title = title;
        this.alwaysOnTop = alwaysOnTop;
    }

    /**
     * 线程的入口点。初始化、运行渲染循环并进行清理。
     */
    @Override
    public void run() {
        try {
            init(); // 初始化 GLFW, 窗口, OpenGL 上下文和资源
            loop(); // 进入渲染循环
        } catch (IOException e) {
            System.err.println("[PngDisplayWindow run] IO Error during execution for [" + imageIdentifier + "]: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) { // 捕获所有可能的错误，包括 RuntimeException
            System.err.println("[PngDisplayWindow run] Unexpected error during execution: " + t.getMessage());
            t.printStackTrace();
        } finally {
            cleanup(); // 确保资源被清理
            // 尝试关闭流，以防 loadTexture 未能关闭它
            if (this.imageStream != null) {
                try {
                    this.imageStream.close();
                } catch (IOException ignored) {}
            }
            System.out.println("[PngDisplayWindow run] Thread finished.");
        }
    }

    /**
     * 初始化 GLFW, 创建窗口, 设置 OpenGL 上下文, 加载资源。
     * @throws IOException 如果加载纹理失败。
     * @throws RuntimeException 如果 GLFW 或 OpenGL 初始化失败。
     */
    private void init() throws IOException {
        System.out.println("[PngDisplayWindow init] Starting initialization...");

        // 1. 获取 Minecraft 窗口句柄并同步初始尺寸
        this.minecraftWindowHandle = Minecraft.getInstance().window.getWindow();
        // ... (rest of the handle acquisition and initial size sync logic remains the same) ...

        // 2. 初始化 GLFW
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // 3. 配置窗口提示 (Hints)
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);   // 创建时隐藏
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);  // 允许程序化同步调整大小
        glfwWindowHint(GLFW_FLOATING, GLFW_TRUE);   // 窗口置顶
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE); // 移除窗口边框和标题栏

        // 4. 创建窗口 (using width, height determined earlier)
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // 5. 设置窗口回调
        glfwSetKeyCallback(window, this::keyCallback);
        glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback);

        // 6. 设置 OpenGL 上下文和 VSync
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(1);

        // 7. 加载和设置 OpenGL 资源
        textureId = loadTexture();
        setupShaders();
        setupQuad();

        // 8. 设置初始 OpenGL 视口
        glViewport(0, 0, width, height);

        // 9. 显示窗口
        glfwShowWindow(window);
    }

    /**
     * 从成员变量 imageStream 加载 PNG 数据并创建 OpenGL 纹理。
     * @return 创建的 OpenGL 纹理 ID。
     * @throws IOException 如果读取图像或 ImageIO 操作失败。
     */
    private int loadTexture() throws IOException {
        BufferedImage image;
        int generatedTextureId = 0; // 局部变量存储生成的 ID
        try {
            System.out.println("[PngDisplayWindow loadTexture] Loading image from stream: " + imageIdentifier);
            image = ImageIO.read(this.imageStream); // 从流读取
            if (image == null) {
                throw new IOException("Could not load image (ImageIO.read returned null) for: " + imageIdentifier);
            }
            System.out.println("[PngDisplayWindow loadTexture] Image loaded: " + image.getWidth() + "x" + image.getHeight());
        } catch (IOException e) {
            System.err.println("[PngDisplayWindow loadTexture] Failed to read image stream for: " + imageIdentifier);
            throw e; // 重新抛出
        } finally {
            // 确保流被关闭，无论成功与否
            if (this.imageStream != null) {
                try {
                    this.imageStream.close();
                } catch (IOException ignored) {}
                // this.imageStream = null; // 清除引用，表示已处理
            }
        }

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        // 获取像素数据 (ARGB -> RGBA for OpenGL)
        int[] pixels = new int[imgWidth * imgHeight];
        image.getRGB(0, 0, imgWidth, imgHeight, pixels, 0, imgWidth);

        ByteBuffer buffer = MemoryUtil.memAlloc(imgWidth * imgHeight * 4); // 4 bytes per pixel (RGBA)
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int pixel = pixels[y * imgWidth + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                buffer.put((byte) (pixel & 0xFF));         // Blue
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
            }
        }
        buffer.flip(); // 准备让 OpenGL 读取

        try {
            // 创建 OpenGL 纹理
            generatedTextureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, generatedTextureId);

            // 设置纹理参数 (拉伸边缘, 线性过滤)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // 设置像素解包对齐方式为1，因为我们是逐字节填充的RGBA数据
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            // 上传纹理数据
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imgWidth, imgHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            // 恢复默认对齐方式 (通常是 4)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 4);

            // 可选: 生成 Mipmap
            // glGenerateMipmap(GL_TEXTURE_2D);

            glBindTexture(GL_TEXTURE_2D, 0); // 解绑

            System.out.println("[PngDisplayWindow loadTexture] Texture created successfully. ID: " + generatedTextureId);

        } finally {
            MemoryUtil.memFree(buffer); // 释放内存中的像素缓冲区
            System.out.println("[PngDisplayWindow loadTexture] Freed pixel buffer.");
        }

        return generatedTextureId;
    }

    /**
     * 编译和链接顶点和片段着色器。
     */
    private void setupShaders() {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkShaderCompilation(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkShaderCompilation(fragmentShader, "FRAGMENT");

        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexShader);
        glAttachShader(shaderProgramId, fragmentShader);
        glLinkProgram(shaderProgramId);
        checkProgramLinking(shaderProgramId);

        // 链接后删除着色器对象
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        // 获取 Uniform 变量的位置
        projectionMatrixLocation = glGetUniformLocation(shaderProgramId, "projection");
        if (projectionMatrixLocation == -1) {
            System.err.println("WARNING: Uniform 'projection' not found in vertex shader!");
        }

        int textureSamplerLocation = glGetUniformLocation(shaderProgramId, "textureSampler");
        if (textureSamplerLocation == -1) {
            System.err.println("WARNING: Uniform 'textureSampler' not found in fragment shader!");
        } else {
            // 设置纹理采样器使用的纹理单元 (只需设置一次)
            glUseProgram(shaderProgramId); // 必须先使用程序
            glUniform1i(textureSamplerLocation, 0); // 0 对应 GL_TEXTURE0
            glUseProgram(0); // 解绑程序
        }
        System.out.println("[PngDisplayWindow setupShaders] Shaders compiled and linked. Program ID: " + shaderProgramId);
    }

    /**
     * 检查着色器编译状态。
     */
    private void checkShaderCompilation(int shader, String type) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, success);
            if (success.get(0) == GL_FALSE) {
                String infoLog = glGetShaderInfoLog(shader, 512); // 获取错误信息
                glDeleteShader(shader); // 删除失败的着色器
                throw new RuntimeException("ERROR::SHADER::" + type + "::COMPILATION_FAILED\n" + infoLog);
            }
        }
    }

    /**
     * 检查着色器程序链接状态。
     */
    private void checkProgramLinking(int program) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetProgramiv(program, GL_LINK_STATUS, success);
            if (success.get(0) == GL_FALSE) {
                String infoLog = glGetProgramInfoLog(program, 512); // 获取错误信息
                glDeleteProgram(program); // 删除失败的程序
                throw new RuntimeException("ERROR::PROGRAM::LINKING_FAILED\n" + infoLog);
            }
        }
    }

    /**
     * 创建并配置用于绘制全屏四边形的 VAO 和 VBO。
     */
    private void setupQuad() {
        // 顶点数据: 位置 (x, y) in NDC [-1, 1], 纹理坐标 (s, t) [0, 1]
        float[] vertices = {
                // 位置          纹理坐标
                -1.0f, -1.0f,  0.0f, 1.0f, // 左下角 (纹理左下)
                1.0f, -1.0f,  1.0f, 1.0f, // 右下角 (纹理右下)
                1.0f,  1.0f,  1.0f, 0.0f, // 右上角 (纹理右上)
                -1.0f,  1.0f,  0.0f, 0.0f  // 左上角 (纹理左上)
        };
        // 注意：使用 GL_TRIANGLE_FAN 的顶点顺序: 左下, 右下, 右上, 左上

        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId); // 绑定 VAO

        glBindBuffer(GL_ARRAY_BUFFER, vboId); // 绑定 VBO
        // 将顶点数据上传到 VBO
        try (MemoryStack stack = stackPush()) {
            FloatBuffer vertexBuffer = stack.floats(vertices);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

        // 配置顶点属性指针
        int stride = 4 * Float.BYTES; // 每个顶点包含 4 个 float (pos.x, pos.y, tex.s, tex.t)
        // 位置属性 (location = 0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0); // 2个 float, 步长, 偏移量 0
        glEnableVertexAttribArray(0);
        // 纹理坐标属性 (location = 1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2 * Float.BYTES); // 2个 float, 步长, 偏移量 2*float
        glEnableVertexAttribArray(1);

        // 解绑 VBO 和 VAO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        System.out.println("[PngDisplayWindow setupQuad] VAO and VBO for quad created. VAO ID: " + vaoId + ", VBO ID: " + vboId);
    }

    /**
     * 窗口的主渲染循环。负责同步窗口状态和绘制内容。
     */
    private void loop() {
        // 设置清屏颜色 (可以根据需要调整，例如设为透明)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // 黑色背景

        // 使用 MemoryStack 管理循环内所需的本地缓冲区
        try (MemoryStack stack = stackPush()) {
            IntBuffer mcX = stack.mallocInt(1);
            IntBuffer mcY = stack.mallocInt(1);
            IntBuffer mcWidthBuf = stack.mallocInt(1);
            IntBuffer mcHeightBuf = stack.mallocInt(1);
            FloatBuffer projectionMatrixBuffer = stack.mallocFloat(16); // 4x4 矩阵

            while (!glfwWindowShouldClose(window)) {
                try {
                    // --- 1. 同步窗口状态 ---
                    if (this.minecraftWindowHandle != NULL) {
                        if (glfwWindowShouldClose(this.minecraftWindowHandle)) {
                            System.out.println("[PngDisplayWindow Loop] Detected Minecraft window closing, closing overlay...");
                            glfwSetWindowShouldClose(this.window, true);
                            this.minecraftWindowHandle = NULL; // Stop checking
                            continue;
                        }

                        // Sync Position
                        glfwGetWindowPos(this.minecraftWindowHandle, mcX, mcY);
                        glfwSetWindowPos(this.window, mcX.get(0), mcY.get(0));
                        mcX.rewind(); mcY.rewind();

                        // Sync Size
                        glfwGetWindowSize(this.minecraftWindowHandle, mcWidthBuf, mcHeightBuf);
                        int currentMcWidth = mcWidthBuf.get(0);
                        int currentMcHeight = mcHeightBuf.get(0);
                        mcWidthBuf.rewind(); mcHeightBuf.rewind();

                        if (currentMcWidth > 0 && currentMcHeight > 0 &&
                                (currentMcWidth != this.width || currentMcHeight != this.height)) {
                            // Size changed, request GLFW to set our window size
                            // This will trigger framebufferSizeCallback which updates viewport and internal width/height
                            glfwSetWindowSize(this.window, currentMcWidth, currentMcHeight);
                            // Log that we requested a size change
                            // System.out.println("[PngDisplayWindow Loop] Requesting size sync to " + currentMcWidth + "x" + currentMcHeight);
                        }
                    }

                    // --- 2. 渲染 ---
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // 清屏

                    glUseProgram(shaderProgramId); // 使用着色器

                    // 设置投影矩阵 (单位矩阵，因为我们用的是标准化坐标)
                    projectionMatrixBuffer.put(new float[]{1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1}).flip();
                    if (projectionMatrixLocation != -1) {
                        glUniformMatrix4fv(projectionMatrixLocation, false, projectionMatrixBuffer);
                    }

                    glActiveTexture(GL_TEXTURE0); // 激活纹理单元
                    glBindTexture(GL_TEXTURE_2D, textureId); // 绑定纹理

                    glBindVertexArray(vaoId); // 绑定 VAO

                    glDrawArrays(GL_TRIANGLE_FAN, 0, 4); // 绘制四边形

                    // 解绑 (可选但推荐)
                    glBindVertexArray(0);
                    glBindTexture(GL_TEXTURE_2D, 0);
                    glUseProgram(0);

                    // --- 3. 交换缓冲区 ---
                    glfwSwapBuffers(window);

                    // --- 4. 处理事件 ---
                    glfwPollEvents();

                } catch (Exception e) {
                    System.err.println("[PngDisplayWindow Loop] Error in render loop:");
                    e.printStackTrace();
                    glfwSetWindowShouldClose(window, true); // Close on error
                }
            }
        } // MemoryStack frees buffers here
        System.out.println("[PngDisplayWindow Loop] Exiting render loop.");
    }

    /**
     * 键盘回调方法。
     */
    private void keyCallback(long windowHandle, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            System.out.println("[PngDisplayWindow Callback] ESC key pressed, closing window.");
            glfwSetWindowShouldClose(windowHandle, true);
        }
    }

    /**
     * 帧缓冲大小变化回调方法。
     */
    private void framebufferSizeCallback(long windowHandle, int newWidth, int newHeight) {
        if (windowHandle == this.window) {
            if (newWidth > 0 && newHeight > 0) {
                // 检查尺寸是否真的改变了，避免不必要的更新和日志
                if (newWidth != this.width || newHeight != this.height) {
                    this.width = newWidth;
                    this.height = newHeight;
                    // 确保在正确的上下文调用 glViewport
                    if (glfwGetCurrentContext() == this.window) {
                        glViewport(0, 0, newWidth, newHeight);
                        System.out.println("[PngDisplayWindow Callback] Framebuffer resized to: " + newWidth + "x" + newHeight + ". Viewport updated.");
                    } else {
                        System.err.println("[PngDisplayWindow Callback] WARNING: Framebuffer resize callback called on wrong context! Viewport not updated.");
                    }
                }
            } else {
                System.out.println("[PngDisplayWindow Callback] Received invalid resize dimensions: " + newWidth + "x" + newHeight);
            }
        }
    }

    /**
     * 清理所有分配的资源 (OpenGL 对象, GLFW 窗口和回调, 终止 GLFW)。
     */
    private void cleanup() {
        System.out.println("[PngDisplayWindow Cleanup] Starting cleanup...");

        // 1. 删除 OpenGL 对象
        // 这些调用应该在窗口上下文仍然有效时进行，但在 finally 块中尽力而为
        try {
            if (shaderProgramId != 0) { glDeleteProgram(shaderProgramId); shaderProgramId = 0; }
            if (textureId != 0) { glDeleteTextures(textureId); textureId = 0; }
            if (vboId != 0) { glDeleteBuffers(vboId); vboId = 0; }
            if (vaoId != 0) { glDeleteVertexArrays(vaoId); vaoId = 0; }
            System.out.println("[PngDisplayWindow Cleanup] OpenGL objects deleted.");
        } catch (Exception e) {
            System.err.println("[PngDisplayWindow Cleanup] Error deleting OpenGL objects: " + e.getMessage());
        }


        // 2. 清理 GLFW 窗口和回调
        if (window != NULL) {
            System.out.println("[PngDisplayWindow Cleanup] Cleaning up window callbacks and destroying window...");
            try {
                // 先移除回调，再释放它们，最后销毁窗口
                glfwSetKeyCallback(window, null);
                glfwSetFramebufferSizeCallback(window, null);
                glfwFreeCallbacks(window);
                glfwDestroyWindow(window);
                System.out.println("[PngDisplayWindow Cleanup] Window destroyed.");
            } catch (Exception e) {
                System.err.println("[PngDisplayWindow Cleanup] Error during window cleanup: " + e.getMessage());
            } finally {
                window = NULL; // 标记窗口句柄无效
            }
        }

        // 3. 终止 GLFW (如果合适)
        // 注意: 只有当确定没有其他部分在使用 GLFW 时才调用 glfwTerminate
        System.out.println("[PngDisplayWindow Cleanup] Terminating GLFW...");
        try {
            glfwTerminate();
            // 释放之前设置的错误回调
            GLFWErrorCallback callback = glfwSetErrorCallback(null);
            if (callback != null) {
                callback.free();
            }
            System.out.println("[PngDisplayWindow Cleanup] GLFW terminated and error callback freed.");
        } catch (Exception e) { // 可能已在别处终止
            System.err.println("[PngDisplayWindow Cleanup] Error during GLFW termination: " + e.getMessage());
        }

        System.out.println("[PngDisplayWindow Cleanup] Cleanup finished.");
    }

    /**
     * 获取当前覆盖窗口的 GLFW 句柄。
     * @return 窗口句柄，如果窗口未创建或已销毁，则返回 NULL。
     */
    public long getWindowHandle() {
        return window;
    }

}