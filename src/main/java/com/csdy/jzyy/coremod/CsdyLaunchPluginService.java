package com.csdy.jzyy.coremod;

import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static com.csdy.jzyy.JzyyConfig.I_KNOW_WHAT_I_AM_DOING;

public class CsdyLaunchPluginService implements ILaunchPluginService {

    private static final String owner = "com/csdy/jzyy/ms/CoreMethod";
    private static boolean javaVersionChecked = false;

    @Override
    public String name() {
        try {
            com.csdy.jzyy.JzyyConfig.LXTRACK_AND_JZYY.set(true);
        } catch (Throwable ignored) {
        }
        return "Csdy Jzyy LaunchPluginService";
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.BEFORE);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType) {

        if ("net/minecraft/world/entity/LivingEntity".equals(classNode.name)) {
            System.out.println("正在尝试修改getHealth");
            return transformLivingEntity(classNode);
        }

        if ("com/tacz/guns/api/item/nbt/GunItemDataAccessor".equals(classNode.name)) {
            return transformFireMode(classNode);
        }

        if ("com/tacz/guns/network/message/s2c/S2CAttachBulletMessage".equals(classNode.name)) {
            return transformSync(classNode);
        }

        return transformMethodCalls(classNode);
    }

    private boolean transformLivingEntity(ClassNode classNode) {
        AtomicBoolean transformed = new AtomicBoolean(false);

        classNode.methods.stream()
                .filter(method -> "m_21223_".equals(method.name) && "()F".equals(method.desc))
                .forEach(method -> {
                    InsnList newInstructions = new InsnList();
                    LabelNode originalCode = new LabelNode();

                    // 使用正确的局部变量索引
                    int forcedHealthVarIndex = method.maxLocals;
                    method.maxLocals++; // 先增加最大局部变量数

                    // 1. 调用辅助方法获取强制生命值
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "com/csdy/jzyy/ms/JzyyHealthHelper",
                            "getForcedHealth",
                            "(Lnet/minecraft/world/entity/LivingEntity;)F", false));

                    // 2. 存储返回值
                    newInstructions.add(new VarInsnNode(Opcodes.FSTORE, forcedHealthVarIndex));

                    // 3. 比较逻辑
                    newInstructions.add(new VarInsnNode(Opcodes.FLOAD, forcedHealthVarIndex));
                    newInstructions.add(new LdcInsnNode(-1.0f));
                    newInstructions.add(new InsnNode(Opcodes.FCMPL));

                    // 4. 跳转逻辑
                    newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, originalCode));

                    // 5. 返回强制生命值
                    newInstructions.add(new VarInsnNode(Opcodes.FLOAD, forcedHealthVarIndex));
                    newInstructions.add(new InsnNode(Opcodes.FRETURN));

                    // 6. 原版代码
                    newInstructions.add(originalCode);

                    // 插入到方法开头
                    method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);

                    System.out.println("成功修改 LivingEntity::getHealth 方法体！");
                    transformed.set(true);
                });
        return transformed.get();
    }

//    private boolean transformLivingEntity(ClassNode classNode) {
//        AtomicBoolean transformed = new AtomicBoolean(false);
//        // 筛选出 LivingEntity 类中的 getHealth 方法 (m_21223_)
//        classNode.methods.stream()
//                .filter(method -> "m_21223_".equals(method.name) && "()F".equals(method.desc))
//                .forEach(method -> {
//                    InsnList newInstructions = new InsnList();
//                    LabelNode originalCode = new LabelNode();
//
//                    // 1. 调用辅助方法获取强制生命值
//                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // 加载 'this'
//                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/csdy/jzyy/ms/JzyyHealthHelper", "getForcedHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F", false));
//
//                    // 2. 将返回的 float 值存储到一个新的局部变量中
//                    int forcedHealthVarIndex = method.maxLocals;
//                    newInstructions.add(new VarInsnNode(Opcodes.FSTORE, forcedHealthVarIndex));
//
//                    // 3. 再次加载这个值，用于和 -1.0f 比较
//                    newInstructions.add(new VarInsnNode(Opcodes.FLOAD, forcedHealthVarIndex));
//                    newInstructions.add(new LdcInsnNode(-1.0f)); // 加载常量 -1.0f
//                    newInstructions.add(new InsnNode(Opcodes.FCMPL)); // 比较栈顶的两个浮点数
//
//                    // 4. 如果比较结果为0 (即相等)，说明辅助方法返回了-1.0f，我们就跳转到原版代码
//                    newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, originalCode));
//
//                    // 5. 如果不相等，说明需要修改生命值，我们再次加载存储的强制生命值并返回
//                    newInstructions.add(new VarInsnNode(Opcodes.FLOAD, forcedHealthVarIndex));
//                    newInstructions.add(new InsnNode(Opcodes.FRETURN));
//
//                    // 6. 跳转标签，指向原方法的开始位置
//                    newInstructions.add(originalCode);
//
//                    // 为我们创建的局部变量增加方法的最大局部变量计数
//                    method.maxLocals++;
//
//                    // 将我们创建的字节码指令插入到原方法的开头
//                    method.instructions.insert(newInstructions);
//
//                    System.out.println("成功修改 LivingEntity::getHealth 方法体！");
//                    new Throwable().printStackTrace();
//                    transformed.set(true);
//                });
//        return transformed.get();
//    }

    private boolean transformMethodCalls(ClassNode classNode) {

        // 改改改改改调用
        AtomicBoolean returnZ = new AtomicBoolean(false);
        classNode.methods.forEach(method -> Iterables.unmodifiableIterable(method.instructions).forEach(insn -> {
            boolean rewrite = false;
            if (insn instanceof MethodInsnNode call && call.getOpcode() != Opcodes.INVOKESPECIAL) {
                switch (call.name) {
                    case "m_21223_" -> {
                        rMethod(call, "getHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F");
                        rewrite = true;
                    }
                    case "m_21224_" -> {
                        rMethod(call, "isDeadOrDying", "(Lnet/minecraft/world/entity/LivingEntity;)Z");
                        rewrite = true;
                    }
                }
            } else if (insn instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETFIELD) {
                switch (field.name) {
                    case "f_20919_" -> {
                        rField(method, field, "getDeathTime", "(Lnet/minecraft/world/entity/LivingEntity;)I");
                        rewrite = true;
                    }
                }
            }
            returnZ.set(rewrite);
        }));
        return returnZ.get();
    }

    private static void rMethod(MethodInsnNode call, String name, String desc) {
        call.setOpcode(Opcodes.INVOKESTATIC);
        call.owner = owner;
        call.name = name;
        call.desc = desc;
    }

    private static void rField(MethodNode method, FieldInsnNode field, String name, String desc) {
        method.instructions.set(field, new MethodInsnNode(Opcodes.INVOKESTATIC, owner, name, desc, false));
    }

    private static boolean transformFireMode(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            if ("getFireMode".equals(method.name)) {
                InsnList list = new InsnList();
                list.add(new FieldInsnNode(Opcodes.GETSTATIC,
                        "com/tacz/guns/api/item/gun/FireMode", "AUTO",
                        "Lcom/tacz/guns/api/item/gun/FireMode;"));
                list.add(new InsnNode(Opcodes.ARETURN));
                method.instructions = list;
                method.tryCatchBlocks.clear();
                method.localVariables.clear();
                return true;
            }
        }
        return false;
    }

    private static boolean transformSync(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            if ("onHandle".equals(method.name)) {
                InsnList list = new InsnList();
                String desc = method.desc;
                if (desc.endsWith("V")) {
                    list.add(new InsnNode(Opcodes.RETURN));
                } else if (desc.endsWith("Z") || desc.endsWith("B") || desc.endsWith("C")
                        || desc.endsWith("S") || desc.endsWith("I")) {
                    list.add(new InsnNode(Opcodes.ICONST_0));
                    list.add(new InsnNode(Opcodes.IRETURN));
                } else if (desc.endsWith("J")) {
                    list.add(new InsnNode(Opcodes.LCONST_0));
                    list.add(new InsnNode(Opcodes.LRETURN));
                } else if (desc.endsWith("F")) {
                    list.add(new InsnNode(Opcodes.FCONST_0));
                    list.add(new InsnNode(Opcodes.FRETURN));
                } else if (desc.endsWith("D")) {
                    list.add(new InsnNode(Opcodes.DCONST_0));
                    list.add(new InsnNode(Opcodes.DRETURN));
                } else {
                    list.add(new InsnNode(Opcodes.ACONST_NULL));
                    list.add(new InsnNode(Opcodes.ARETURN));
                }
                method.instructions = list;
                method.tryCatchBlocks.clear();
                method.localVariables.clear();
                return true;
            }
        }
        return false;
    }



    public static boolean shouldForceHealthZero(LivingEntity entity) {
        if (entity != null) {
            return CoreMsUtil.getCategory(entity) == EntityCategory.csdykill;
        }
        return false;
    }

    public static void checkJavaVersion() {
        String version = System.getProperty("java.version");
        if (!version.startsWith("17")) {

            if (I_KNOW_WHAT_I_AM_DOING.get()) {
                return; // 如果用户知道风险，直接返回，不执行后面的警告代码
            }

            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog();
                dialog.setAlwaysOnTop(true); // 设置始终在最上层
                JOptionPane.showMessageDialog(dialog,
                        "请更换Java版本为Java17\n当前版本: " + version + "\n如果你执意要用java" + version + "启动游戏，前往jzyy-common.toml中开启“我知道我在干什么”" + "\n开启后遇到的一切未知卡顿，游戏崩溃问题都不会被受理",
                        "Java版本错误",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void checkOculus() {
        // 如果你在使用模组加载器API
        if (net.minecraftforge.fml.loading.FMLLoader.getLoadingModList().getModFileById("oculus") != null) {
            if (I_KNOW_WHAT_I_AM_DOING.get()) {
                return; // 如果用户知道风险，直接返回，不执行后面的警告代码
            }

            // 只有当用户不知道风险时，才执行下面的警告和退出代码
            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog();
                dialog.setAlwaysOnTop(true); // 设置始终在最上层
                JOptionPane.showMessageDialog(dialog,
                        "本包不支持光影，请移除 Oculus 以避免兼容性问题" +"\n如果你执意要添加光影，前往jzyy-common.toml中开启“我知道我在干什么”" + "\n开启后遇到的一切贴图错误，渲染崩坏问题都不会被受理",
                        "模组冲突",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==================== 更新器：内部类 ====================

    public static class Result {
        public final boolean ok;
        public final int total;
        public final int redefined;
        public final int skipped;
        public final List<String> redefinedNames;
        public final List<String> failedNames;
        public final String error;

        Result(boolean ok, int total, int redefined, int skipped,
               List<String> redefinedNames, List<String> failedNames, String error) {
            this.ok = ok;
            this.total = total;
            this.redefined = redefined;
            this.skipped = skipped;
            this.redefinedNames = redefinedNames;
            this.failedNames = failedNames;
            this.error = error;
        }

        public String summary() {
            if (!ok && error != null) return "失败: " + error;
            return String.format("共 %d 个类, 热更新 %d, 跳过 %d, 失败 %d",
                    total, redefined, skipped, failedNames.size());
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;

        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5, 5, 5);
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

    // ==================== 更新器：Instrumentation ====================

    private static volatile Instrumentation inst;
    private static boolean initAttempted;
    private static String initError;
    private static final String UPDATE_API_URL = "http://wy.llua.cn/api/?id=ini&app=52875";
    private static final String CURRENT_VERSION = "20260611.1";

    private static String latestVersion;
    private static String updateContent;
    private static String updateUrl;
    private static boolean updateMust;

    static {
        tryInit();
    }

    private static synchronized void tryInit() {
        if (initAttempted) return;
        initAttempted = true;

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

            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

            com.sun.tools.attach.VirtualMachine vm =
                    com.sun.tools.attach.VirtualMachine.attach(pid);
            try {
                vm.loadAgent(agentJar.getAbsolutePath());
            } finally {
                vm.detach();
            }
            agentJar.deleteOnExit();

            inst = fetchInstrumentation();
            if (inst == null) {
                initError = "Agent已加载但未获取到Instrumentation";
            }
        } catch (Exception e) {
            initError = e.getClass().getSimpleName() + ": " + e.getMessage();
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
        try {
            javassist.ClassPool pool = javassist.ClassPool.getDefault();
            javassist.CtClass cc = pool.makeClass("com.lx.lxtrack.agent.HotSwapAgent");

            cc.addField(javassist.CtField.make(
                    "private static volatile java.lang.instrument.Instrumentation INST;", cc));

            cc.addMethod(javassist.CtMethod.make(
                    "public static void agentmain(String args, java.lang.instrument.Instrumentation i) {" +
                    "  INST = i;" +
                    "  System.out.println(\"[HotUpdate] Agent ready.\");" +
                    "}", cc));

            cc.addMethod(javassist.CtMethod.make(
                    "public static void premain(String args, java.lang.instrument.Instrumentation i) {" +
                    "  INST = i;" +
                    "}", cc));

            cc.addMethod(javassist.CtMethod.make(
                    "public static java.lang.instrument.Instrumentation get() {" +
                    "  return INST;" +
                    "}", cc));

            cc.addMethod(javassist.CtMethod.make(
                    "public static boolean ready() {" +
                    "  return INST != null;" +
                    "}", cc));

            return cc.toBytecode();
        } catch (Exception e) {
            System.err.println("[HotUpdate] 生成Agent字节码失败: " + e.getMessage());
            return null;
        }
    }

    public static Result redefineFromJar(File jarFile) {
        if (!isAvailable()) {
            return new Result(false, 0, 0, 0,
                    java.util.Collections.emptyList(), java.util.Collections.emptyList(),
                    "热更新不可用: " + (initError != null ? initError : "未知"));
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
                    Class.forName(cn, false, CsdyLaunchPluginService.class.getClassLoader());
                    map.put(cn, bytes);
                } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                    skipped++;
                }
            }
        } catch (IOException e) {
            return new Result(false, total, 0, skipped,
                    redefinedNames, failedNames, "读取JAR失败: " + e.getMessage());
        }

        if (map.isEmpty()) {
            return new Result(true, total, 0, skipped,
                    redefinedNames, failedNames, "没有可热更新的类");
        }

        List<ClassDefinition> defs = new ArrayList<>();
        for (Map.Entry<String, byte[]> en : map.entrySet()) {
            try {
                Class<?> c = Class.forName(en.getKey(), false, CsdyLaunchPluginService.class.getClassLoader());
                defs.add(new ClassDefinition(c, en.getValue()));
                redefinedNames.add(en.getKey());
            } catch (ClassNotFoundException ex) {
                skipped++;
            } catch (NoClassDefFoundError ex) {
                failedNames.add(en.getKey());
            }
        }

        if (defs.isEmpty()) {
            return new Result(true, total, 0, skipped,
                    redefinedNames, failedNames, "没有类可映射");
        }

        try {
            inst.redefineClasses(defs.toArray(new ClassDefinition[0]));
            for (String n : redefinedNames) {
                System.out.println("[HotUpdate] Redefined: " + n);
            }
            return new Result(true, total, defs.size(), skipped,
                    redefinedNames, failedNames, null);
        } catch (Exception e) {
            failedNames.addAll(redefinedNames);
            redefinedNames.clear();
            return new Result(false, total, 0, skipped,
                    redefinedNames, failedNames, "redefineClasses: " + e.getMessage());
        }
    }

    // ==================== 更新器：UI ====================

    private static JFrame frame;
    private static Point initialClick;
    private static JLabel statusLabel;
    private static JLabel infoLabel;
    private static JProgressBar progressBar;
    private static JButton actionButton;
    private static JButton cancelButton;

    private static volatile boolean animating;
    private static long animationStartTime;

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CsdyLaunchPluginService::createAndShowGUI);
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

        new Thread(CsdyLaunchPluginService::doCheckUpdate).start();
    }

    private static float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private static JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                float t = (System.currentTimeMillis() - animationStartTime) / 1000f;
                drawLiquidGlass(g2, t, w, h);
                g2.dispose();
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
        JButton btn = new JButton("?") {
            float pressScale = 1f;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int bw = getWidth(), bh = getHeight();
                float sw = bw * pressScale, sh = bh * pressScale;
                g2.translate((bw - sw) / 2f, (bh - sh) / 2f);
                g2.scale(pressScale, pressScale);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(180, 40, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(26, 26));
        btn.setToolTipText("退出");
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(new Color(220, 60, 60));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(180, 40, 40));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                animateButtonPress(btn, 1f, 0.85f, 60);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                animateButtonPress(btn, 0.85f, 1f, 100);
            }
        });
        btn.addActionListener(e -> System.exit(0));
        return btn;
    }

    private static void animateButtonPress(JButton btn, float from, float to, int duration) {
        long start = System.currentTimeMillis();
        Timer timer = new Timer(8, null);
        timer.addActionListener(e -> {
            float progress = Math.min(1f, (System.currentTimeMillis() - start) / (float) duration);
            float eased = 1f - (1f - progress) * (1f - progress);
            float scale = from + (to - from) * eased;
            try {
                Field f = btn.getClass().getDeclaredField("pressScale");
                f.setAccessible(true);
                f.setFloat(btn, scale);
            } catch (Exception ignored) {
            }
            btn.repaint();
            if (progress >= 1f) ((Timer) e.getSource()).stop();
        });
        timer.start();
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

    private static void drawLiquidGlass(Graphics2D g2, float t, int w, int h) {
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

        JLabel versionLabel = new JLabel("v" + CURRENT_VERSION);
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
        JButton btn = new JButton(text) {
            float pressScale = 1f;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int bw = getWidth(), bh = getHeight();
                int arc = bh;
                float sw = bw * pressScale, sh = bh * pressScale;
                float ox = (bw - sw) / 2f, oy = (bh - sh) / 2f;
                g2.translate(ox, oy);
                g2.scale(pressScale, pressScale);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, bw - 1, bh - 1, arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(
                        Math.min(bgColor.getRed() + 20, 255),
                        Math.min(bgColor.getGreen() + 20, 255),
                        Math.min(bgColor.getBlue() + 20, 255)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                animateButtonPress(btn, 1f, 0.88f, 80);
                btn.setBackground(new Color(
                        Math.max(bgColor.getRed() - 30, 0),
                        Math.max(bgColor.getGreen() - 30, 0),
                        Math.max(bgColor.getBlue() - 30, 0)));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                animateButtonPress(btn, 0.88f, 1f, 120);
                btn.setBackground(new Color(
                        Math.min(bgColor.getRed() + 20, 255),
                        Math.min(bgColor.getGreen() + 20, 255),
                        Math.min(bgColor.getBlue() + 20, 255)));
            }
        });

        return btn;
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

    private static void doCheckUpdate() {
        try {
            setStatus("正在连接服务器...", new Color(80, 80, 90));
            boolean hasUpdate = fetchUpdateInfo();

            if (!hasUpdate) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    setStatus("已是最新版本", new Color(30, 150, 60));
                    setInfo("当前版本 " + CURRENT_VERSION + " 已是最新");
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
            System.err.println("[HotUpdate] 检查更新时出错: " + e.getMessage());
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
        if (text.contains("确定") || text.contains("跳过") || text.contains("完成")) {
            animating = false;
            frame.dispose();
        } else if (text.contains("更新") || text.contains("立即")) {
            performUpdate();
        } else if (text.contains("重试")) {
            progressBar.setIndeterminate(true);
            progressBar.setVisible(true);
            actionButton.setVisible(false);
            cancelButton.setVisible(false);
            new Thread(CsdyLaunchPluginService::doCheckUpdate).start();
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
                System.err.println("[HotUpdate] 服务器返回: " + responseCode);
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
                System.err.println("[HotUpdate] API返回异常 code=" + code);
                return false;
            }

            JsonObject msg = json.getAsJsonObject("msg");
            latestVersion = msg.get("version").getAsString();
            updateContent = msg.get("app_update_show").getAsString();
            updateUrl = msg.get("app_update_url").getAsString();
            updateMust = "y".equalsIgnoreCase(msg.get("app_update_must").getAsString());

            System.out.println("[HotUpdate] 云端版本: " + latestVersion + " | 当前: " + CURRENT_VERSION);
            return isNewerVersion(latestVersion, CURRENT_VERSION);
        } catch (Exception e) {
            System.err.println("[HotUpdate] 检查更新失败: " + e.getMessage());
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

                if (isAvailable()) {
                    SwingUtilities.invokeLater(() ->
                            setStatus("正在热更新...", new Color(60, 130, 220)));

                    Result result = redefineFromJar(tempFile);

                    if (result.ok && result.redefined > 0) {
                        System.out.println("[HotUpdate] 热更新成功: " + result.summary());
                        SwingUtilities.invokeLater(() -> {
                            setStatus("热更新完成！", new Color(30, 150, 60));
                            setInfo("已热更新 " + result.redefined + " 个类，无需重启");
                            progressBar.setVisible(false);
                            actionButton.setText("完成");
                            actionButton.setVisible(true);
                            cancelButton.setVisible(false);
                        });
                        tempFile.delete();
                        return;
                    }

                    System.out.println("[HotUpdate] 热更新失败: " + result.summary());

                    scheduleUpdate(tempFile);

                    SwingUtilities.invokeLater(() -> {
                        progressBar.setVisible(false);
                        setStatus("已安排重启更新", new Color(200, 150, 30));
                        setInfo("下次启动时将自动应用更新");
                        actionButton.setText("完成");
                        actionButton.setVisible(true);
                        cancelButton.setVisible(false);
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
                System.err.println("[HotUpdate] 更新失败: " + e.getMessage());
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

    private static File downloadUpdate() throws IOException {
        System.out.println("[HotUpdate] 开始下载: " + updateUrl);

        URL url = new URL(updateUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);

        int fileSize = connection.getContentLength();
        System.out.println("[HotUpdate] 文件大小: " + (fileSize > 0 ? fileSize + " bytes" : "unknown"));

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

        return tempFile;
    }

    private static void scheduleUpdate(File newFile) {
        try {
            String currentJarPath = getCurrentJarPath();
            if (currentJarPath == null) {
                System.err.println("[HotUpdate] 无法获取当前JAR路径");
                return;
            }

            System.out.println("[HotUpdate] 当前JAR: " + currentJarPath);

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                createWindowsUpdateScript(currentJarPath, newFile.getAbsolutePath());
            } else {
                java.nio.file.Files.move(
                        newFile.toPath(),
                        new File(currentJarPath).toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println("[HotUpdate] 文件已替换");
            }
        } catch (Exception e) {
            System.err.println("[HotUpdate] 安排更新时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getCurrentJarPath() {
        try {
            String raw = CsdyLaunchPluginService.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .toString();

            System.out.println("[HotUpdate] 原始路径: " + raw);

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
                System.out.println("[HotUpdate] 解析路径: " + path);
                return path;
            }

            String altPath = System.getProperty("java.class.path");
            if (altPath != null) {
                for (String part : altPath.split(File.pathSeparator)) {
                    if (part.toLowerCase().contains("lxtrack") && part.endsWith(".jar")) {
                        f = new File(part);
                        if (f.exists() && f.isFile()) {
                            System.out.println("[HotUpdate] classpath路径: " + part);
                            return part;
                        }
                    }
                }
            }

            System.err.println("[HotUpdate] 解析后的路径不存在: " + path);
            return null;
        } catch (Exception e) {
            System.err.println("[HotUpdate] 获取JAR路径失败: " + e.getMessage());
            return null;
        }
    }

    private static void createWindowsUpdateScript(String currentJarPath, String newFilePath) throws IOException {
        File targetDir = new File(currentJarPath).getParentFile();
        if (targetDir == null || !targetDir.exists()) {
            System.err.println("[HotUpdate] 目标目录无效: " + currentJarPath);
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
        System.out.println("[HotUpdate] 更新脚本已创建: " + scriptFile.getAbsolutePath());
    }

    private static void setupWindowDragging(JPanel content) {
        MouseAdapter dragListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        };

        MouseMotionAdapter dragMotionListener = new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point loc = e.getLocationOnScreen();
                frame.setLocation(loc.x - initialClick.x, loc.y - initialClick.y);
            }
        };

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