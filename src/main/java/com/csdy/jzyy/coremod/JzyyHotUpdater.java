package com.csdy.jzyy.coremod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * 独立的热更新器，不引用任何 Minecraft 类。
 * 这样在 bootstrap 阶段通过 Class.forName 加载时不会因 NoClassDefFoundError 失败。
 */
public class JzyyHotUpdater {

    // ==================== 结果键 ====================
    private static final String RK_OK = "ok";
    private static final String RK_TOTAL = "total";
    private static final String RK_REDEFINED = "redefined";
    private static final String RK_SKIPPED = "skipped";
    private static final String RK_SUMMARY = "summary";

    // ==================== Instrumentation ====================
    private static volatile Instrumentation inst;
    private static boolean initAttempted;
    private static String initError;
    private static final String UPDATE_API_URL = "http://wy.llua.cn/api/?id=ini&app=52875";

    private static String latestVersion;
    private static String updateContent;
    private static String updateUrl;
    private static boolean updateMust;
    private static volatile File pendingUpdateJar;
    private static volatile String pendingUpdateVersion;
    private static final CountDownLatch guiLatch = new CountDownLatch(1);
    private static volatile boolean guiShown; // 防止 GUI 重复弹出

    // ==================== UI 字段 ====================
    public static JFrame frame;
    public static Point initialClick;
    private static JLabel statusLabel;
    private static JLabel infoLabel;
    private static JProgressBar progressBar;
    private static JButton actionButton;
    private static JButton cancelButton;

    private static volatile boolean animating;
    public static long animationStartTime;

    private static final Path2D.Float reusablePath = new Path2D.Float();
    private static final CubicCurve2D.Float reusableCurve = new CubicCurve2D.Float();
    private static final BasicStroke ribbonStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke ribbonStrokeGlow = new BasicStroke(0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final java.util.Random FLOW_RNG = new java.util.Random(1337);
    private static final float[][] flowSeeds = new float[8][3];

    static {
        for (int i = 0; i < flowSeeds.length; i++) {
            flowSeeds[i][0] = FLOW_RNG.nextFloat() * 100f;
            flowSeeds[i][1] = FLOW_RNG.nextFloat() * 100f;
            flowSeeds[i][2] = 0.3f + FLOW_RNG.nextFloat() * 1.5f;
        }
    }

    private static class FlowRibbon {
        float[][] controlPoints;
        float speed, phase, width, alpha;
        int hue;

        FlowRibbon(float s, float ph, float w, float a, int h) {
            this.speed = s;
            this.phase = ph;
            this.width = w;
            this.alpha = a;
            this.hue = h;
            this.controlPoints = new float[4][2];
            java.util.Random r = new java.util.Random((long) (ph * 10000));
            for (int i = 0; i < 4; i++) {
                controlPoints[i][0] = r.nextFloat();
                controlPoints[i][1] = r.nextFloat();
            }
        }
    }

    private static final List<FlowRibbon> ribbons = new ArrayList<>();

    static {
        java.util.Random rr = new java.util.Random(77);
        for (int i = 0; i < 6; i++) {
            ribbons.add(new FlowRibbon(
                    0.4f + rr.nextFloat() * 0.7f,
                    rr.nextFloat() * 6.28f,
                    1.5f + rr.nextFloat() * 3f,
                    25f + rr.nextFloat() * 35f,
                    190 + rr.nextInt(65)));
        }
    }

    // ==================== 入口 ====================

    private static volatile boolean inited; // 防止 init 重复调用

    public static void init() {
        if (inited) {
            System.out.println("[JzyyHotUpdate] 已初始化，跳过重复调用");
            return;
        }
        inited = true;
        if (!isLxTrackDisabled() && !lxTrackJarExists()) {
            System.out.println("[JzyyHotUpdate] mods目录未找到LxTrack.jar，提前下载以便Forge加载...");
            downloadLxTrackToModsSync();
        }
        tryInit();
        if (isLxTrackDisabled()) {
            System.out.println("[JzyyHotUpdate] LxTrack已禁用，跳过更新检查");
        } else {
            checkUpdateWithGUI();
        }
    }

    // ==================== 配置读取 ====================

    private static boolean isLxTrackDisabled() {
        File configFile = new File("config/jzyy-common.toml");
        if (!configFile.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("disable_lxtrack")) {
                    int eq = line.indexOf('=');
                    if (eq != -1) {
                        String val = line.substring(eq + 1).trim().toLowerCase();
                        return val.equals("true");
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean lxTrackJarExists() {
        File modsDir = new File("mods");
        if (!modsDir.exists() || !modsDir.isDirectory()) return false;
        File[] files = modsDir.listFiles((dir, name) ->
                name.toLowerCase().contains("lxtrack") && name.endsWith(".jar"));
        return files != null && files.length > 0 && isValidJar(files[0]);
    }

    private static boolean isValidJar(File file) {
        if (file == null || !file.exists() || file.length() < 22) return false;
        try (JarFile jar = new JarFile(file)) {
            return jar.entries().hasMoreElements();
        } catch (IOException e) {
            return false;
        }
    }

    static String getCurrentLxTrackVersion() {
        File modsDir = new File("mods");
        File[] files = modsDir.listFiles((dir, name) ->
                name.toLowerCase().contains("lxtrack") && name.endsWith(".jar"));
        if (files == null || files.length == 0) return "0";

        try (JarFile jar = new JarFile(files[0])) {
            Manifest mf = jar.getManifest();
            if (mf != null) {
                String v = mf.getMainAttributes().getValue("Implementation-Version");
                if (v != null && !v.isEmpty()) return v.trim();
            }
        } catch (IOException ignored) {}
        return "0";
    }

    // ==================== 提前下载 ====================

    private static void downloadLxTrackToModsSync() {
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(UPDATE_API_URL);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            if (connection.getResponseCode() != 200) {
                System.err.println("[JzyyHotUpdate] 提前下载失败: HTTP " + connection.getResponseCode());
                return;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            if (json.get("code").getAsInt() != 200) {
                System.err.println("[JzyyHotUpdate] 提前下载: API返回异常");
                return;
            }

            String dlUrl = json.getAsJsonObject("msg").get("app_update_url").getAsString();
            if (dlUrl == null || dlUrl.isEmpty()) {
                System.err.println("[JzyyHotUpdate] 提前下载: 下载地址为空");
                return;
            }

            System.out.println("[JzyyHotUpdate] 开始提前下载 LxTrack...");
            File tempFile = File.createTempFile("lxtrack_dl_", ".jar");
            tempFile.deleteOnExit();

            URL dlUrlObj = new URL(dlUrl);
            HttpURLConnection dlConn = (HttpURLConnection) dlUrlObj.openConnection();
            dlConn.setRequestMethod("GET");
            dlConn.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
            dlConn.setConnectTimeout(30000);
            dlConn.setReadTimeout(60000);

            try (InputStream is = dlConn.getInputStream();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) != -1) fos.write(buf, 0, n);
            }
            dlConn.disconnect();

            if (!isValidJar(tempFile)) {
                System.err.println("[JzyyHotUpdate] 提前下载: 文件校验失败，已删除");
                tempFile.delete();
                return;
            }

            File modsDir = new File("mods");
            modsDir.mkdirs();
            File target = new File(modsDir, "LxTrack.jar");
            java.nio.file.Files.copy(tempFile.toPath(), target.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            tempFile.delete();
            System.out.println("[JzyyHotUpdate] LxTrack.jar 已提前保存到 mods 目录，Forge加载时生效");

        } catch (Exception e) {
            System.err.println("[JzyyHotUpdate] 提前下载失败: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    // ==================== Instrumentation 初始化 ====================

    private static synchronized void tryInit() {
        if (initAttempted) return;
        initAttempted = true;

        forceAllowAttachSelf();

        try {
            Class.forName("com.sun.tools.attach.VirtualMachine");
        } catch (ClassNotFoundException e) {
            initError = "JRE环境，不支持热更新（需要JDK）";
            return;
        }

        try {
            File agentJar = buildAgentJar();
            if (agentJar == null) {
                initError = "构建Agent失败";
                return;
            }

            String pidStr = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            long pid = Long.parseLong(pidStr);
            System.out.println("[JzyyHotUpdate] Detected PID: " + pid);

            boolean attached = false;

            com.sun.tools.attach.VirtualMachine vm = null;
            try {
                vm = com.sun.tools.attach.VirtualMachine.attach(String.valueOf(pid));
                attached = true;
            } catch (IOException attachEx) {
                long altPid = ProcessHandle.current().pid();
                if (altPid != pid && altPid > 0) {
                    try {
                        System.out.println("[JzyyHotUpdate] Retry with ProcessHandle PID: " + altPid);
                        vm = com.sun.tools.attach.VirtualMachine.attach(String.valueOf(altPid));
                        attached = true;
                        pid = altPid;
                    } catch (IOException altEx) {
                        System.out.println("[JzyyHotUpdate] 自attach失败，尝试子进程attach");
                        if (!trySpawnChildAttach(altPid > 0 ? altPid : pid, agentJar)) {
                            throw attachEx;
                        } else {
                            System.out.println("[JzyyHotUpdate] 子进程attach成功");
                            attached = true;
                        }
                    }
                } else {
                    System.out.println("[JzyyHotUpdate] 自attach失败，尝试子进程attach");
                    if (!trySpawnChildAttach(pid, agentJar)) {
                        throw attachEx;
                    } else {
                        System.out.println("[JzyyHotUpdate] 子进程attach成功");
                        attached = true;
                    }
                }
            }

            if (attached) {
                if (vm != null) {
                    try {
                        vm.loadAgent(agentJar.getAbsolutePath());
                    } finally {
                        vm.detach();
                    }
                    agentJar.deleteOnExit();
                }

                int retries = 10;
                while (inst == null && retries-- > 0) {
                    Thread.sleep(150);
                    inst = fetchInstrumentation();
                }

                if (inst == null) {
                    initError = "Agent已加载但未获取到Instrumentation";
                }
            }
        } catch (Exception e) {
            initError = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    private static boolean trySpawnChildAttach(long targetPid, File agentJar) throws IOException {
        File attacherJar = buildAttacherJar();
        if (attacherJar == null) {
            System.err.println("[JzyyHotUpdate] 构建attacher失败");
            return false;
        }
        attacherJar.deleteOnExit();

        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            javaBin += ".exe";
        }

        ProcessBuilder pb = new ProcessBuilder(
                javaBin,
                "--add-modules=jdk.attach",
                "-cp",
                attacherJar.getAbsolutePath(),
                "com.lx.lxtrack.agent.Attacher",
                String.valueOf(targetPid),
                agentJar.getAbsolutePath()
        );
        pb.inheritIO();
        Process p = pb.start();
        try {
            int exitCode = p.waitFor();
            return exitCode == 0;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static File buildAttacherJar() throws IOException {
        byte[] agentBytes = generateAttacherClassBytes();
        if (agentBytes == null) return null;

        Manifest mf = new Manifest();
        mf.getMainAttributes().putValue("Manifest-Version", "1.0");
        mf.getMainAttributes().putValue("Main-Class", "com.lx.lxtrack.agent.Attacher");

        File jar = File.createTempFile("lxtrack_attacher_", ".jar");
        jar.deleteOnExit();
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar), mf)) {
            jos.putNextEntry(new JarEntry("com/lx/lxtrack/agent/Attacher.class"));
            jos.write(agentBytes);
            jos.closeEntry();
        }
        return jar;
    }

    private static byte[] generateAttacherClassBytes() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, "com/lx/lxtrack/agent/Attacher", null, "java/lang/Object", null);

        MethodVisitor mv;

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        Label okLabel = new Label();

        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("[Attacher] Starting attach...");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARRAYLENGTH);
        mv.visitLdcInsn(2);
        mv.visitJumpInsn(Opcodes.IF_ICMPGE, okLabel);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("[Attacher] Usage: attacher <pid> <agent-jar>");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "exit", "(I)V", false);
        mv.visitLabel(okLabel);

        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitInsn(Opcodes.AALOAD);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "parseLong", "(Ljava/lang/String;)J", false);
        mv.visitVarInsn(Opcodes.LSTORE, 1);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.AALOAD);
        mv.visitVarInsn(Opcodes.ASTORE, 3);

        mv.visitVarInsn(Opcodes.LLOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/sun/tools/attach/VirtualMachine", "attach", "(Ljava/lang/String;)Lcom/sun/tools/attach/VirtualMachine;", false);
        mv.visitVarInsn(Opcodes.ASTORE, 4);

        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/sun/tools/attach/VirtualMachine", "loadAgent", "(Ljava/lang/String;)V", false);

        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("[Attacher] Agent loaded.");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/sun/tools/attach/VirtualMachine", "detach", "()V", false);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(4, 5);
        mv.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static void forceAllowAttachSelf() {
        System.setProperty("jdk.attach.allowAttachSelf", "true");

        try {
            Class<?> vmClass = Class.forName("jdk.internal.misc.VM");
            Field savedPropsField = vmClass.getDeclaredField("savedProps");
            savedPropsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> savedProps = (Map<String, String>) savedPropsField.get(null);
            if (savedProps != null) {
                savedProps.put("jdk.attach.allowAttachSelf", "true");
                System.out.println("[JzyyHotUpdate] 已通过内部Properties启用自attach");
            }
        } catch (Exception e) {
            System.out.println("[JzyyHotUpdate] 内部Properties写入失败，尝试其他方式: " + e.getMessage());
        }
    }

    public static boolean isAvailable() {
        return inst != null && inst.isRedefineClassesSupported();
    }

    public static String getInitError() {
        return initError;
    }

    public static Instrumentation getInstrumentation() {
        return inst;
    }

    private static Instrumentation fetchInstrumentation() {
        try {
            Class<?> agentCls = Class.forName("com.lx.lxtrack.agent.HotSwapAgent", true,
                    ClassLoader.getSystemClassLoader());
            return (Instrumentation) agentCls.getMethod("get").invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static File buildAgentJar() throws Exception {
        byte[] agentBytes = generateAgentClassBytes();
        if (agentBytes == null) return null;

        Manifest mf = new Manifest();
        mf.getMainAttributes().putValue("Manifest-Version", "1.0");
        mf.getMainAttributes().putValue("Agent-Class", "com.lx.lxtrack.agent.HotSwapAgent");
        mf.getMainAttributes().putValue("Can-Redefine-Classes", "true");
        mf.getMainAttributes().putValue("Can-Retransform-Classes", "true");

        File jar = File.createTempFile("lxtrack_hotswap_", ".jar");
        jar.deleteOnExit();
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar), mf)) {
            jos.putNextEntry(new JarEntry("com/lx/lxtrack/agent/HotSwapAgent.class"));
            jos.write(agentBytes);
            jos.closeEntry();
        }
        return jar;
    }

    private static byte[] generateAgentClassBytes() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, "com/lx/lxtrack/agent/HotSwapAgent", null, "java/lang/Object", null);

        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_VOLATILE, "INST", "Ljava/lang/instrument/Instrumentation;", null, null).visitEnd();

        MethodVisitor mv;

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "agentmain", "(Ljava/lang/String;Ljava/lang/instrument/Instrumentation;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/lx/lxtrack/agent/HotSwapAgent", "INST", "Ljava/lang/instrument/Instrumentation;");
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("[JzyyHotUpdate] Agent ready.");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "premain", "(Ljava/lang/String;Ljava/lang/instrument/Instrumentation;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/lx/lxtrack/agent/HotSwapAgent", "INST", "Ljava/lang/instrument/Instrumentation;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "get", "()Ljava/lang/instrument/Instrumentation;", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/lx/lxtrack/agent/HotSwapAgent", "INST", "Ljava/lang/instrument/Instrumentation;");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 0);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "ready", "()Z", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/lx/lxtrack/agent/HotSwapAgent", "INST", "Ljava/lang/instrument/Instrumentation;");
        Label nonNull = new Label();
        mv.visitJumpInsn(Opcodes.IFNONNULL, nonNull);
        mv.visitInsn(Opcodes.ICONST_0);
        Label end = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, end);
        mv.visitLabel(nonNull);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLabel(end);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 0);
        mv.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    // ==================== 热更新核心 ====================

    public static Map<String, Object> redefineFromJar(File jarFile) {
        Map<String, Object> result = new HashMap<>();
        if (!isAvailable()) {
            result.put(RK_OK, false);
            result.put(RK_TOTAL, 0);
            result.put(RK_REDEFINED, 0);
            result.put(RK_SKIPPED, 0);
            result.put(RK_SUMMARY, "热更新不可用: " + (initError != null ? initError : "未知"));
            return result;
        }

        List<String> redefinedNames = new ArrayList<>();
        List<String> failedNames = new ArrayList<>();
        int total = 0, skipped = 0;

        Map<String, byte[]> map = new LinkedHashMap<>();
        try (JarFile jf = new JarFile(jarFile)) {
            java.util.Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                String name = e.getName();
                if (!name.endsWith(".class") || name.contains("META-INF")) continue;

                String cn = name.substring(0, name.length() - 6).replace('/', '.');
                if (!cn.startsWith("com.lx.lxtrack.")) continue;

                total++;
                byte[] bytes = readAll(jf.getInputStream(e));
                try {
                    Class.forName(cn, false, JzyyHotUpdater.class.getClassLoader());
                    map.put(cn, bytes);
                } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                    skipped++;
                }
            }
        } catch (IOException e) {
            result.put(RK_OK, false);
            result.put(RK_TOTAL, total);
            result.put(RK_REDEFINED, 0);
            result.put(RK_SKIPPED, skipped);
            result.put(RK_SUMMARY, "读取JAR失败: " + e.getMessage());
            return result;
        }

        if (map.isEmpty()) {
            result.put(RK_OK, true);
            result.put(RK_TOTAL, total);
            result.put(RK_REDEFINED, 0);
            result.put(RK_SKIPPED, skipped);
            result.put(RK_SUMMARY, "没有可热更新的类");
            return result;
        }

        List<ClassDefinition> defs = new ArrayList<>();
        for (Map.Entry<String, byte[]> en : map.entrySet()) {
            try {
                Class<?> c = Class.forName(en.getKey(), false, JzyyHotUpdater.class.getClassLoader());
                defs.add(new ClassDefinition(c, en.getValue()));
                redefinedNames.add(en.getKey());
            } catch (ClassNotFoundException ex) {
                skipped++;
            } catch (NoClassDefFoundError ex) {
                failedNames.add(en.getKey());
            }
        }

        if (defs.isEmpty()) {
            result.put(RK_OK, true);
            result.put(RK_TOTAL, total);
            result.put(RK_REDEFINED, 0);
            result.put(RK_SKIPPED, skipped);
            result.put(RK_SUMMARY, "没有类可映射");
            return result;
        }

        try {
            inst.redefineClasses(defs.toArray(new ClassDefinition[0]));
            for (String n : redefinedNames) {
                System.out.println("[JzyyHotUpdate] Loaded " + n + " class");
            }
            result.put(RK_OK, true);
            result.put(RK_TOTAL, total);
            result.put(RK_REDEFINED, redefinedNames.size());
            result.put(RK_SKIPPED, skipped);
            result.put(RK_SUMMARY, String.format("共 %d 个类, 热更新 %d, 跳过 %d, 失败 %d",
                    total, redefinedNames.size(), skipped, failedNames.size()));
            return result;
        } catch (Exception e) {
            failedNames.addAll(redefinedNames);
            redefinedNames.clear();
            result.put(RK_OK, false);
            result.put(RK_TOTAL, total);
            result.put(RK_REDEFINED, 0);
            result.put(RK_SKIPPED, skipped);
            result.put(RK_SUMMARY, "redefineClasses: " + e.getMessage());
            return result;
        }
    }

    // ==================== GUI ====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JzyyHotUpdater::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        frame = new JFrame("LxTrack - Updater");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(0, 0);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setAlwaysOnTop(true);

        JPanel content = createContentPanel();
        frame.setContentPane(content);
        frame.setVisible(true);

        setupWindowDragging(content);

        animationStartTime = System.currentTimeMillis();
        animating = true;

        int refreshRate = 60;
        try {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            DisplayMode dm = gd.getDisplayMode();
            if (dm != null && dm.getRefreshRate() > 0) {
                refreshRate = dm.getRefreshRate();
            }
        } catch (Exception ignored) {}
        int delay = 1000 / refreshRate;

        final int targetWidth = 440;
        final int targetHeight = 320;
        final int expandDuration = 500;
        final long expandStart = System.currentTimeMillis();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int cx = (screenSize.width - targetWidth) / 2;
        int cy = (screenSize.height - targetHeight) / 2;

        Timer mainTimer = new Timer(delay, e -> {
            long now = System.currentTimeMillis();
            float expandProgress = Math.min(1f, (now - expandStart) / (float) expandDuration);
            if (expandProgress < 1f) {
                float eased = easeOutCubic(expandProgress);
                int cw = Math.max(1, (int) (targetWidth * eased));
                int ch = Math.max(1, (int) (targetHeight * eased));
                int x = cx + (targetWidth - cw) / 2;
                int y = cy + (targetHeight - ch) / 2;
                frame.setSize(cw, ch);
                frame.setLocation(x, y);
            }
            content.repaint();
        });
        mainTimer.start();

        new Thread(JzyyHotUpdater::doCheckUpdate).start();
    }

    private static float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private static Border createGlassBorder() {
        return (Border) Proxy.newProxyInstance(
                JzyyHotUpdater.class.getClassLoader(),
                new Class<?>[]{Border.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "paintBorder":
                            Component c = (Component) args[0];
                            Graphics g = (Graphics) args[1];
                            int x = (int) args[2];
                            int y = (int) args[3];
                            int w = (int) args[4];
                            int h = (int) args[5];
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.translate(x, y);
                            drawLiquidGlass(g2, (System.currentTimeMillis() - animationStartTime) / 1000f, w, h);
                            g2.dispose();
                            return null;
                        case "getBorderInsets":
                            return new Insets(0, 0, 0, 0);
                        case "isBorderOpaque":
                            return false;
                    }
                    return null;
                });
    }

    private static JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                drawLiquidGlass(g2, (System.currentTimeMillis() - animationStartTime) / 1000f, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        content.setOpaque(false);

        JPanel barPanel = new JPanel();
        barPanel.setOpaque(false);
        barPanel.setLayout(new BoxLayout(barPanel, BoxLayout.X_AXIS));
        barPanel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 10));

        JLabel titleLabel = new JLabel("LxTrack");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        titleLabel.setForeground(new Color(30, 30, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        barPanel.add(titleLabel);

        barPanel.add(createDot());
        barPanel.add(Box.createHorizontalStrut(6));

        JLabel subtitleLabel = new JLabel("Updater");
        subtitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(100, 100, 110));
        barPanel.add(subtitleLabel);

        barPanel.add(Box.createHorizontalGlue());

        JButton exitBtn = createExitButton();
        barPanel.add(exitBtn);

        content.add(barPanel, BorderLayout.NORTH);

        JPanel mainPanel = createMainPanel();
        content.add(mainPanel, BorderLayout.CENTER);

        return content;
    }

    private static JComponent createDot() {
        JLabel dot = new JLabel("·");
        dot.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        dot.setForeground(new Color(60, 60, 70));
        return dot;
    }

    private static JButton createExitButton() {
        JButton btn = new JButton("\u00D7");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(new Color(180, 40, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(26, 26));
        btn.setToolTipText("\u9000\u51FA");
        btn.addMouseListener(createExitButtonMouseListener(btn));
        btn.addActionListener(e -> System.exit(0));
        return btn;
    }

    private static MouseListener createExitButtonMouseListener(JButton btn) {
        return (MouseListener) Proxy.newProxyInstance(
                JzyyHotUpdater.class.getClassLoader(),
                new Class<?>[]{MouseListener.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "mouseEntered":
                            btn.setForeground(new Color(220, 60, 60));
                            break;
                        case "mouseExited":
                            btn.setForeground(new Color(180, 40, 40));
                            break;
                    }
                    return null;
                });
    }

    // ==================== 绘图 ====================

    private static float flowNoise(float x, float y, float t) {
        float n = 0;
        for (int i = 0; i < flowSeeds.length; i++) {
            float sx = flowSeeds[i][0];
            float sy = flowSeeds[i][1];
            float sp = flowSeeds[i][2];
            n += (float) Math.sin(x * 0.7f + sx + t * sp) * Math.cos(y * 0.9f + sy - t * sp * 0.8f);
            n += (float) Math.sin(x * 1.3f - sy + t * sp * 0.6f) * Math.cos(y * 0.5f + sx + t * sp * 1.1f);
        }
        return n / (flowSeeds.length * 2f);
    }

    private static void drawFlowRibbons(Graphics2D g2, float t, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Composite orig = g2.getComposite();

        for (FlowRibbon r : ribbons) {
            float phase = t * r.speed + r.phase;

            float cx1 = r.controlPoints[0][0] * w + flowNoise(r.controlPoints[0][0] * 5f, phase, t * 0.3f) * w * 0.4f;
            float cy1 = r.controlPoints[0][1] * h + flowNoise(phase, r.controlPoints[0][1] * 5f, t * 0.35f) * h * 0.4f;
            float cx2 = r.controlPoints[1][0] * w + flowNoise(r.controlPoints[1][0] * 5f, phase + 1f, t * 0.4f) * w * 0.35f;
            float cy2 = r.controlPoints[1][1] * h + flowNoise(phase + 1f, r.controlPoints[1][1] * 5f, t * 0.3f) * h * 0.35f;
            float cx3 = r.controlPoints[2][0] * w + flowNoise(r.controlPoints[2][0] * 5f, phase + 2f, t * 0.45f) * w * 0.3f;
            float cy3 = r.controlPoints[2][1] * h + flowNoise(phase + 2f, r.controlPoints[2][1] * 5f, t * 0.33f) * h * 0.3f;
            float cx4 = r.controlPoints[3][0] * w + flowNoise(r.controlPoints[3][0] * 5f, phase + 3f, t * 0.38f) * w * 0.35f;
            float cy4 = r.controlPoints[3][1] * h + flowNoise(phase + 3f, r.controlPoints[3][1] * 5f, t * 0.36f) * h * 0.35f;

            float alpha = r.alpha + (float) Math.sin(t * 1.3f + r.phase) * 10f;
            int ai = Math.max(5, Math.min(60, (int) alpha));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ai / 255f));
            g2.setStroke(ribbonStroke);

            g2.setColor(new Color(255, 255, 255, ai));

            reusableCurve.setCurve(cx1, cy1, cx2, cy2, cx3, cy3, cx4, cy4);
            g2.draw(reusableCurve);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (ai * 0.6f) / 255f));
            g2.setStroke(ribbonStrokeGlow);
            g2.setColor(new Color(255, 255, 255, (int) (ai * 0.8f)));
            g2.draw(reusableCurve);
        }
        g2.setComposite(orig);
    }

    private static void drawGlassHighlights(Graphics2D g2, float t, int w, int h) {
        float highlightH = h * 0.38f;

        float[] dist1 = {0.0f, 0.5f, 1.0f};
        Color[] colors1 = {
                new Color(255, 255, 255, 70 + (int) (Math.sin(t * 0.5f) * 15)),
                new Color(255, 255, 255, 30),
                new Color(255, 255, 255, 0)
        };
        LinearGradientPaint lgp1 = new LinearGradientPaint(
                0, 0, 0, highlightH, dist1, colors1);
        g2.setPaint(lgp1);
        g2.fillRoundRect(0, 0, w, (int) highlightH, 48, 48);
        g2.fillRect(0, (int) (highlightH * 0.6f), w, (int) (highlightH * 0.4f));

        float[] dist2 = {0.0f, 1.0f};
        Color[] colors2 = {
                new Color(255, 255, 255, 30 + (int) (Math.sin(t * 0.4f + 1f) * 10)),
                new Color(255, 255, 255, 0)
        };
        LinearGradientPaint lgp2 = new LinearGradientPaint(
                0, h, 0, h * 0.65f, dist2, colors2);
        g2.setPaint(lgp2);
        g2.fillRoundRect(0, (int) (h * 0.6f), w, (int) (h * 0.4f), 48, 48);
    }

    public static void drawLiquidGlass(Graphics2D g2, float t, int w, int h) {
        int arc = 48;

        Shape prevClip = g2.getClip();
        RoundRectangle2D.Float roundClip = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
        g2.setClip(roundClip);

        g2.setColor(new Color(240, 242, 248, 120));
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        g2.setColor(new Color(245, 245, 250, 80));
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        drawFlowRibbons(g2, t, w, h);

        g2.setColor(new Color(255, 255, 255, 25));
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        drawGlassHighlights(g2, t, w, h);

        g2.setClip(prevClip);

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(255, 255, 255, 80));
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        float breath = 0.5f + 0.5f * (float) Math.sin(t * 0.5f);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(255, 255, 255, (int) (40 + breath * 20)));
        g2.drawRoundRect(1, 1, w - 3, h - 3, arc - 1, arc - 1);
    }

    private static JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(null);

        JLabel iconLabel = new JLabel("LxTrack");
        iconLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
        iconLabel.setForeground(new Color(30, 30, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBounds(0, 10, 440, 36);

        statusLabel = new JLabel("正在连接服务器...");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(80, 80, 90));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBounds(0, 50, 440, 22);

        infoLabel = new JLabel("");
        infoLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 110));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBounds(0, 75, 440, 50);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(230, 232, 240));
        progressBar.setForeground(new Color(60, 130, 220));
        progressBar.setBorderPainted(false);
        progressBar.setBounds(50, 130, 340, 6);
        progressBar.setVisible(true);

        actionButton = createStyledButton("请稍候", new Color(60, 130, 220));
        actionButton.setBounds(70, 180, 130, 38);
        actionButton.setVisible(false);
        actionButton.addActionListener(e -> handleActionButton());

        cancelButton = createStyledButton("退出", new Color(120, 120, 135));
        cancelButton.setBounds(240, 180, 130, 38);
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> System.exit(0));

        JLabel versionLabel = new JLabel("v" + getCurrentLxTrackVersion());
        versionLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(150, 150, 160));
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        versionLabel.setBounds(0, 240, 440, 20);

        panel.add(iconLabel);
        panel.add(statusLabel);
        panel.add(infoLabel);
        panel.add(progressBar);
        panel.add(actionButton);
        panel.add(cancelButton);
        panel.add(versionLabel);

        return panel;
    }

    private static JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(createStyledButtonMouseListener(btn, bgColor));

        return btn;
    }

    private static MouseListener createStyledButtonMouseListener(JButton btn, Color bgColor) {
        return (MouseListener) Proxy.newProxyInstance(
                JzyyHotUpdater.class.getClassLoader(),
                new Class<?>[]{MouseListener.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "mouseEntered":
                            btn.setBackground(new Color(
                                    Math.min(bgColor.getRed() + 20, 255),
                                    Math.min(bgColor.getGreen() + 20, 255),
                                    Math.min(bgColor.getBlue() + 20, 255)));
                            break;
                        case "mouseExited":
                            btn.setBackground(bgColor);
                            break;
                        case "mousePressed":
                            btn.setBackground(new Color(
                                    Math.max(bgColor.getRed() - 30, 0),
                                    Math.max(bgColor.getGreen() - 30, 0),
                                    Math.max(bgColor.getBlue() - 30, 0)));
                            break;
                        case "mouseReleased":
                            btn.setBackground(new Color(
                                    Math.min(bgColor.getRed() + 20, 255),
                                    Math.min(bgColor.getGreen() + 20, 255),
                                    Math.min(bgColor.getBlue() + 20, 255)));
                            break;
                    }
                    return null;
                });
    }

    private static void setStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            statusLabel.setForeground(color);
        });
    }

    private static void setInfo(String info) {
        SwingUtilities.invokeLater(() -> {
            infoLabel.setText("<html><div style='text-align:center;'>"
                    + info.replace("\n", "<br>") + "</div></html>");
        });
    }

    // ==================== 阻塞式更新检查 & 热更新 ====================

    public static void checkUpdateWithGUI() {
        if (guiShown) {
            System.out.println("[JzyyHotUpdate] GUI已显示，跳过重复调用");
            return;
        }
        guiShown = true;
        try {
            createAndShowGUI();
        } catch (Exception e) {
            System.err.println("[JzyyHotUpdate] GUI创建失败: " + e.getMessage());
            guiLatch.countDown();
        }
        try {
            guiLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void notifyGUIDone() {
        guiLatch.countDown();
    }

    public static void tryHotUpdate() {
        File jar = pendingUpdateJar;
        String ver = pendingUpdateVersion;
        if (jar == null || !jar.exists()) return;

        String loadedVersion = JzyyHotUpdater.class.getPackage().getImplementationVersion();
        if (loadedVersion == null || loadedVersion.isEmpty()) loadedVersion = getCurrentLxTrackVersion();

        if (ver != null && ver.equals(loadedVersion)) {
            System.out.println("[JzyyHotUpdate] 加载的已是新版本，无需热更新");
            jar.delete();
            pendingUpdateJar = null;
            return;
        }

        if (!isAvailable()) {
            System.out.println("[JzyyHotUpdate] 热更新不可用: " + getInitError() + "，安排重启替换");
            scheduleUpdate(jar);
            if (updateMust) {
                System.out.println("[JzyyHotUpdate] 强制更新，3秒后退出");
                new Thread(() -> {
                    try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                    System.exit(0);
                }).start();
            }
            return;
        }

        System.out.println("[JzyyHotUpdate] 当前版本 " + loadedVersion + "，热更新到 " + ver);
        Map<String, Object> result = redefineFromJar(jar);
        if (Boolean.TRUE.equals(result.get(RK_OK)) && (Integer) result.get(RK_REDEFINED) > 0) {
            System.out.println("[JzyyHotUpdate] 热更新成功: " + result.get(RK_SUMMARY));
            jar.delete();
            pendingUpdateJar = null;
        } else {
            System.err.println("[JzyyHotUpdate] 热更新失败: " + result.get(RK_SUMMARY));
            scheduleUpdate(jar);
            if (updateMust) {
                System.out.println("[JzyyHotUpdate] 强制更新，3秒后退出");
                new Thread(() -> {
                    try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                    System.exit(0);
                }).start();
            }
        }
    }

    private static void doCheckUpdate() {
        try {
            if (isLxTrackDisabled()) {
                System.out.println("[JzyyHotUpdate] LxTrack已禁用");
                return;
            }

            if (!lxTrackJarExists()) {
                System.out.println("[JzyyHotUpdate] mods目录未找到LxTrack.jar，自动下载...");
                setStatus("mods目录未找到LxTrack，正在自动下载...", new Color(80, 80, 90));
                downloadAndLoadLatestLxTrack();
                return;
            }

            setStatus("正在连接服务器...", new Color(80, 80, 90));
            boolean hasUpdate = fetchUpdateInfo();

            if (!hasUpdate) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    setStatus("已是最新版本", new Color(30, 150, 60));
                    setInfo("当前版本 " + getCurrentLxTrackVersion() + " 已是最新");
                    actionButton.setText("确定");
                    actionButton.setVisible(true);
                    cancelButton.setVisible(false);
                });
                return;
            }

            SwingUtilities.invokeLater(() -> {
                progressBar.setIndeterminate(false);
                progressBar.setValue(0);
                progressBar.setVisible(false);

                if (updateMust) {
                    setStatus("检测到强制更新！", new Color(200, 50, 50));
                    actionButton.setText("立即更新");
                    cancelButton.setVisible(false);
                } else {
                    setStatus("发现新版本 " + latestVersion, new Color(200, 150, 30));
                    actionButton.setText("更新");
                    cancelButton.setText("跳过");
                    cancelButton.setVisible(true);
                }

                setInfo("更新内容:\n" + updateContent);
                actionButton.setVisible(true);
            });

        } catch (Exception e) {
            System.err.println("[JzyyHotUpdate] 检查更新时出错: " + e.getMessage());
            e.printStackTrace();

            SwingUtilities.invokeLater(() -> {
                progressBar.setVisible(false);
                setStatus("更新检查失败", new Color(200, 50, 50));
                setInfo("错误: " + e.getMessage());
                actionButton.setText("重试");
                actionButton.setVisible(true);
                cancelButton.setText("退出");
                cancelButton.setVisible(true);
            });
        }
    }

    private static void handleActionButton() {
        String text = actionButton.getText();
        if (text.contains("确定") || text.contains("跳过") || text.contains("完成") || text.contains("启动")) {
            animating = false;
            frame.dispose();
            notifyGUIDone();
        } else if (text.contains("更新") || text.contains("立即")) {
            performUpdate();
        } else if (text.contains("重试")) {
            progressBar.setIndeterminate(true);
            progressBar.setVisible(true);
            actionButton.setVisible(false);
            cancelButton.setVisible(false);
            new Thread(JzyyHotUpdater::doCheckUpdate).start();
        }
    }

    private static boolean fetchUpdateInfo() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(UPDATE_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("[JzyyHotUpdate] 服务器返回: " + responseCode);
                return false;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            int code = json.get("code").getAsInt();
            if (code != 200) {
                System.err.println("[JzyyHotUpdate] API返回异常 code=" + code);
                return false;
            }

            JsonObject msg = json.getAsJsonObject("msg");
            latestVersion = msg.get("version").getAsString();
            updateContent = msg.get("app_update_show").getAsString();
            updateUrl = msg.get("app_update_url").getAsString();
            updateMust = "y".equalsIgnoreCase(msg.get("app_update_must").getAsString());

            String currentVer = getCurrentLxTrackVersion();
            System.out.println("[JzyyHotUpdate] 云端版本: " + latestVersion + " | 当前: " + currentVer);
            return isNewerVersion(latestVersion, currentVer);
        } catch (Exception e) {
            System.err.println("[JzyyHotUpdate] 检查更新失败: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static boolean isNewerVersion(String remoteVer, String localVer) {
        try {
            return Long.parseLong(remoteVer.trim()) > Long.parseLong(localVer.trim());
        } catch (NumberFormatException e) {
            return !remoteVer.equals(localVer);
        }
    }

    private static void performUpdate() {
        SwingUtilities.invokeLater(() -> {
            actionButton.setVisible(false);
            cancelButton.setVisible(false);
            progressBar.setIndeterminate(true);
            progressBar.setVisible(true);

            String hotStatus = isAvailable() ? " (热更新可用)" : " (需重启更新)";
            setStatus("正在下载更新..." + hotStatus, new Color(80, 80, 90));
            setInfo("");
        });

        new Thread(() -> {
            try {
                File tempFile = downloadUpdate();

                if (tempFile == null || !tempFile.exists()) {
                    SwingUtilities.invokeLater(() -> {
                        setStatus("下载失败", new Color(200, 50, 50));
                        setInfo("请检查网络连接后重试");
                        progressBar.setVisible(false);
                        actionButton.setText("重试");
                        actionButton.setVisible(true);
                        cancelButton.setVisible(true);
                    });
                    return;
                }

                pendingUpdateJar = tempFile;
                pendingUpdateVersion = latestVersion;

                if (isAvailable()) {
                    SwingUtilities.invokeLater(() ->
                            setStatus("正在热更新...", new Color(60, 130, 220)));

                    Map<String, Object> result = redefineFromJar(tempFile);

                    if (Boolean.TRUE.equals(result.get(RK_OK)) && (Integer) result.get(RK_REDEFINED) > 0) {
                        System.out.println("[JzyyHotUpdate] 热更新成功: " + result.get(RK_SUMMARY));
                        SwingUtilities.invokeLater(() -> {
                            setStatus("热更新完成！", new Color(30, 150, 60));
                            setInfo("已热更新 " + result.get(RK_REDEFINED) + " 个类，无需重启");
                            progressBar.setVisible(false);
                            actionButton.setText("启动追踪");
                            actionButton.setVisible(true);
                            cancelButton.setText("退出");
                            cancelButton.setVisible(true);
                        });
                        pendingUpdateJar = null;
                        tempFile.delete();
                        return;
                    }

                    System.out.println("[JzyyHotUpdate] 热更新失败: " + result.get(RK_SUMMARY));

                    scheduleUpdate(tempFile);

                    SwingUtilities.invokeLater(() -> {
                        progressBar.setVisible(false);
                        setStatus("已安排重启更新", new Color(200, 150, 30));
                        setInfo("下次启动时将自动应用更新");
                        actionButton.setText("启动追踪");
                        actionButton.setVisible(true);
                        cancelButton.setText("退出");
                        cancelButton.setVisible(true);
                    });
                    return;
                }

                scheduleUpdate(tempFile);
                SwingUtilities.invokeLater(() -> {
                    setStatus("下载完成！", new Color(30, 150, 60));
                    setInfo("程序即将重启以应用更新");
                    progressBar.setVisible(false);
                    Timer timer = new Timer(2000, evt -> System.exit(0));
                    timer.setRepeats(false);
                    timer.start();
                });

            } catch (Exception e) {
                System.err.println("[JzyyHotUpdate] 更新失败: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStatus("更新失败", new Color(200, 50, 50));
                    setInfo("错误: " + e.getMessage());
                    progressBar.setVisible(false);
                    actionButton.setText("重试");
                    actionButton.setVisible(true);
                    cancelButton.setVisible(true);
                });
            }
        }).start();
    }

    private static void downloadAndLoadLatestLxTrack() {
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(UPDATE_API_URL);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int code = connection.getResponseCode();
            if (code != 200) {
                SwingUtilities.invokeLater(() -> {
                    setStatus("获取下载地址失败", new Color(200, 50, 50));
                    setInfo("HTTP " + code);
                    actionButton.setText("重试");
                    actionButton.setVisible(true);
                });
                return;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            if (json.get("code").getAsInt() != 200) {
                SwingUtilities.invokeLater(() -> {
                    setStatus("API返回异常", new Color(200, 50, 50));
                    actionButton.setText("重试");
                    actionButton.setVisible(true);
                });
                return;
            }

            String dlUrl = json.getAsJsonObject("msg").get("app_update_url").getAsString();
            if (dlUrl == null || dlUrl.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    setStatus("下载地址为空", new Color(200, 50, 50));
                    actionButton.setText("重试");
                    actionButton.setVisible(true);
                });
                return;
            }

            SwingUtilities.invokeLater(() -> {
                progressBar.setIndeterminate(true);
                setStatus("正在下载 LxTrack...", new Color(60, 130, 220));
                setInfo("首次运行，正在下载必要组件");
            });

            File tempFile = File.createTempFile("lxtrack_dl_", ".jar");
            tempFile.deleteOnExit();

            URL dlUrlObj = new URL(dlUrl);
            HttpURLConnection dlConn = (HttpURLConnection) dlUrlObj.openConnection();
            dlConn.setRequestMethod("GET");
            dlConn.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
            dlConn.setConnectTimeout(30000);
            dlConn.setReadTimeout(60000);

            try (InputStream is = dlConn.getInputStream();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) != -1) fos.write(buf, 0, n);
            }
            dlConn.disconnect();

            if (!isValidJar(tempFile)) {
                System.err.println("[JzyyHotUpdate] 下载文件校验失败");
                tempFile.delete();
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    setStatus("下载失败，文件损坏", new Color(200, 50, 50));
                    setInfo("请检查网络后重试");
                    actionButton.setText("重试");
                    actionButton.setVisible(true);
                });
                return;
            }

            File modsDir = new File("mods");
            modsDir.mkdirs();
            File target = new File(modsDir, "LxTrack.jar");
            java.nio.file.Files.copy(tempFile.toPath(), target.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[JzyyHotUpdate] LxTrack.jar 已保存到 mods 目录");

            tempFile.delete();

            SwingUtilities.invokeLater(() -> {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                progressBar.setVisible(false);
                setStatus("LxTrack 下载完成", new Color(30, 150, 60));
                setInfo("已保存到mods目录，Forge加载时生效");
                new Thread(JzyyHotUpdater::doCheckUpdate).start();
            });

        } catch (Exception e) {
            System.err.println("[JzyyHotUpdate] LxTrack 下载/加载失败: " + e.getMessage());
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                progressBar.setVisible(false);
                setStatus("LxTrack 下载失败", new Color(200, 50, 50));
                setInfo("错误: " + e.getMessage());
                actionButton.setText("重试");
                actionButton.setVisible(true);
            });
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static File downloadUpdate() throws IOException {
        System.out.println("[JzyyHotUpdate] 开始下载: " + updateUrl);

        URL url = new URL(updateUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);

        int fileSize = connection.getContentLength();
        System.out.println("[JzyyHotUpdate] 文件大小: " + (fileSize > 0 ? fileSize + " bytes" : "unknown"));

        File tempFile = File.createTempFile("lxtrack_update_", ".jar");
        tempFile.deleteOnExit();

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[8192];
            long downloaded = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                downloaded += bytesRead;
                if (fileSize > 0) {
                    final int progress = (int) ((downloaded * 100) / fileSize);
                    final long dled = downloaded;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(progress);
                        setInfo("下载中... " + dled / 1024 + "KB / " + fileSize / 1024 + "KB");
                    });
                }
            }
        } finally {
            connection.disconnect();
        }

        if (!isValidJar(tempFile)) {
            System.err.println("[JzyyHotUpdate] 更新文件校验失败");
            tempFile.delete();
            return null;
        }

        return tempFile;
    }

    private static void scheduleUpdate(File newFile) {
        try {
            String currentJarPath = getCurrentJarPath();
            if (currentJarPath == null) {
                System.err.println("[JzyyHotUpdate] 无法获取当前JAR路径");
                return;
            }

            System.out.println("[JzyyHotUpdate] 当前JAR: " + currentJarPath);

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                createWindowsUpdateScript(currentJarPath, newFile.getAbsolutePath());
            } else {
                java.nio.file.Files.move(
                        newFile.toPath(),
                        new File(currentJarPath).toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println("[JzyyHotUpdate] 文件已替换");
            }
        } catch (Exception e) {
            System.err.println("[JzyyHotUpdate] 安排更新时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getCurrentJarPath() {
        try {
            String raw = JzyyHotUpdater.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .toString();

            System.out.println("[JzyyHotUpdate] 原始路径: " + raw);

            String path = raw;
            if (path.startsWith("jar:") || path.startsWith("union:")) {
                int schemeEnd = path.indexOf(':') + 1;
                path = path.substring(schemeEnd);
            }

            int bangIdx = path.indexOf('!');
            if (bangIdx > 0) {
                path = path.substring(0, bangIdx);
            }

            if (path.startsWith("file:/")) {
                if (path.startsWith("file:///")) {
                    path = path.substring(8);
                } else {
                    path = path.substring(5);
                }
            }

            try {
                path = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }

            int fragment = path.indexOf('#');
            if (fragment > 0) {
                path = path.substring(0, fragment);
            }

            path = path.replace('/', File.separatorChar);

            if (path.startsWith(File.separator) && path.length() > 2 && path.charAt(2) == ':') {
                path = path.substring(1);
            }

            File f = new File(path);
            if (f.exists() && f.isFile()) {
                System.out.println("[JzyyHotUpdate] 解析路径: " + path);
                return path;
            }

            String altPath = System.getProperty("java.class.path");
            if (altPath != null) {
                for (String part : altPath.split(File.pathSeparator)) {
                    if (part.toLowerCase().contains("lxtrack") && part.endsWith(".jar")) {
                        f = new File(part);
                        if (f.exists() && f.isFile()) {
                            System.out.println("[JzyyHotUpdate] classpath路径: " + part);
                            return part;
                        }
                    }
                }
            }

            System.err.println("[JzyyHotUpdate] 解析后的路径不存在: " + path);
            return null;
        } catch (Exception e) {
            System.err.println("[JzyyHotUpdate] 获取JAR路径失败: " + e.getMessage());
            return null;
        }
    }

    private static void createWindowsUpdateScript(String currentJarPath, String newFilePath) throws IOException {
        File targetDir = new File(currentJarPath).getParentFile();
        if (targetDir == null || !targetDir.exists()) {
            System.err.println("[JzyyHotUpdate] 目标目录无效: " + currentJarPath);
            return;
        }

        File scriptFile = new File(
                System.getProperty("java.io.tmpdir"),
                "lxtrack_update_" + System.currentTimeMillis() + ".bat");

        try (PrintWriter writer = new PrintWriter(scriptFile, "GBK")) {
            writer.println("@echo off");
            writer.println("chcp 65001 > nul");
            writer.println("cd /d \"" + targetDir.getAbsolutePath() + "\"");
            writer.println("echo 正在更新 LxTrack...");
            writer.println("");
            writer.println(":retry");
            writer.println("timeout /t 2 /nobreak > nul");
            writer.println("move /Y \"" + newFilePath + "\" \"" + currentJarPath + "\"");
            writer.println("if exist \"" + newFilePath + "\" (");
            writer.println("  echo 文件被占用，重试中...");
            writer.println("  goto retry");
            writer.println(")");
            writer.println("echo 更新完成");
            writer.println("del \"%~f0\" & exit");
        }

        Runtime.getRuntime().exec("cmd /c start \"LxTrackUpdater\" \"" + scriptFile.getAbsolutePath() + "\"");
        System.out.println("[JzyyHotUpdate] 更新脚本已创建: " + scriptFile.getAbsolutePath());
    }

    private static void setupWindowDragging(JPanel content) {
        MouseListener dragListener = (MouseListener) Proxy.newProxyInstance(
                JzyyHotUpdater.class.getClassLoader(),
                new Class<?>[]{MouseListener.class},
                (proxy, method, args) -> {
                    if ("mousePressed".equals(method.getName())) {
                        MouseEvent me = (MouseEvent) args[0];
                        initialClick = me.getPoint();
                    }
                    return null;
                });

        MouseMotionListener dragMotionListener = (MouseMotionListener) Proxy.newProxyInstance(
                JzyyHotUpdater.class.getClassLoader(),
                new Class<?>[]{MouseMotionListener.class},
                (proxy, method, args) -> {
                    if ("mouseDragged".equals(method.getName())) {
                        MouseEvent me = (MouseEvent) args[0];
                        Point loc = me.getLocationOnScreen();
                        frame.setLocation(loc.x - initialClick.x, loc.y - initialClick.y);
                    }
                    return null;
                });

        frame.addMouseListener(dragListener);
        frame.addMouseMotionListener(dragMotionListener);

        Component[] components = frame.getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.addMouseListener(dragListener);
                comp.addMouseMotionListener(dragMotionListener);
            }
        }

        for (Component comp : content.getComponents()) {
            comp.addMouseListener(dragListener);
            comp.addMouseMotionListener(dragMotionListener);
        }
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] tmp = new byte[8192];
        int n;
        while ((n = in.read(tmp)) != -1) buf.write(tmp, 0, n);
        return buf.toByteArray();
    }
}
