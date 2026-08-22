package com.csdy.jzyy.coremod;

import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import com.google.common.collect.Iterables;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.csdy.jzyy.JzyyConfig.I_KNOW_WHAT_I_AM_DOING;

public class CsdyLaunchPluginService implements ILaunchPluginService {

    private static final String owner = "com/csdy/jzyy/ms/CoreMethod";
    private static boolean javaVersionChecked = false;

    // ===== 下载窗口渲染状态（视觉风格） =====
    private static final Color[] WHITE_ALPHA = new Color[256];
    private static final float[][] flowSeeds = new float[8][3];
    private static final List<float[]> ribbons = new ArrayList<>();
    private static final CubicCurve2D.Float reusableCurve = new CubicCurve2D.Float();
    private static final BasicStroke ribbonStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke ribbonStrokeGlow = new BasicStroke(0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Random FLOW_RNG = new Random(1337);
    private static final int RI_SPEED = 0, RI_PHASE = 1, RI_WIDTH = 2, RI_ALPHA = 3, RI_HUE = 4;
    private static final int RI_CP00 = 5, RI_CP01 = 6, RI_CP10 = 7, RI_CP11 = 8;
    private static final int RI_CP20 = 9, RI_CP21 = 10, RI_CP30 = 11, RI_CP31 = 12;

    static {
        System.setProperty("java.awt.headless", "false");
        for (int i = 0; i < 256; i++) WHITE_ALPHA[i] = new Color(255, 255, 255, i);
        for (int i = 0; i < flowSeeds.length; i++) {
            flowSeeds[i][0] = FLOW_RNG.nextFloat() * 100f;
            flowSeeds[i][1] = FLOW_RNG.nextFloat() * 100f;
            flowSeeds[i][2] = 0.3f + FLOW_RNG.nextFloat() * 1.5f;
        }
        Random rr = new Random(77);
        for (int i = 0; i < 6; i++) {
            float[] r = new float[13];
            r[RI_SPEED] = 0.4f + rr.nextFloat() * 0.7f;
            r[RI_PHASE] = rr.nextFloat() * 6.28f;
            r[RI_WIDTH] = 1.5f + rr.nextFloat() * 3f;
            r[RI_ALPHA] = 25f + rr.nextFloat() * 35f;
            r[RI_HUE]   = 190 + rr.nextInt(65);
            Random r2 = new Random((long)(r[RI_PHASE] * 10000));
            for (int j = 0; j < 4; j++) {
                r[RI_CP00 + j * 2]     = r2.nextFloat();
                r[RI_CP00 + j * 2 + 1] = r2.nextFloat();
            }
            ribbons.add(r);
        }
    }

    @Override
    public String name() {
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
                return;
            }

            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog();
                dialog.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(dialog,
                        "请更换Java版本为Java17\n当前版本: " + version + "\n如果你执意要用java" + version + "启动游戏，前往jzyy-common.toml中开启\u201c我知道我在干什么\u201d" + "\n开启后遇到的一切未知卡顿，游戏崩溃问题都不会被受理",
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
        if (net.minecraftforge.fml.loading.FMLLoader.getLoadingModList().getModFileById("oculus") != null) {
            if (I_KNOW_WHAT_I_AM_DOING.get()) {
                return;
            }

            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog();
                dialog.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(dialog,
                        "本包不支持光影，请移除 Oculus 以避免兼容性问题" +"\n如果你执意要添加光影，前往jzyy-common.toml中开启\u201c我知道我在干什么\u201d" + "\n开启后遇到的一切贴图错误，渲染崩坏问题都不会被受理",
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

    private static boolean readDisableLxTrackFromConfig() {
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
        if (files == null) return false;
        for (File f : files) {
            if (isValidJar(f)) return true;
        }
        return false;
    }

    private static boolean isValidJar(File f) {
        try (InputStream in = new FileInputStream(f)) {
            return in.read() == 'P' && in.read() == 'K';
        } catch (Exception e) {
            return false;
        }
    }

    private static String readHead(File f, int maxBytes) {
        try (InputStream in = new FileInputStream(f)) {
            byte[] buf = new byte[maxBytes];
            int n = in.read(buf);
            if (n <= 0) return "(空)";
            return new String(buf, 0, n, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "(读取失败: " + e.getMessage() + ")";
        }
    }

    private static void writeDisableLxTrackToConfig(boolean value) {
        File configFile = new File("config/jzyy-common.toml");
        if (!configFile.exists()) return;
        try {
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith("disable_lxtrack")) {
                        sb.append("    disable_lxtrack = ").append(value).append("\n");
                        found = true;
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
            if (!found) {
                sb.append("    disable_lxtrack = ").append(value).append("\n");
            }
            try (FileWriter fw = new FileWriter(configFile)) {
                fw.write(sb.toString());
            }
        } catch (Exception ignored) {
        }
    }

    private static final String LXTRACK_API_URL = "http://wy.llua.cn/api/?id=ini&app=52875";

    private static boolean hasKotlinForForge() {
        File modsDir = new File("mods");
        if (!modsDir.exists() || !modsDir.isDirectory()) return false;
        File[] files = modsDir.listFiles((dir, name) ->
                name.toLowerCase().contains("kotlinforforge") && name.endsWith(".jar"));
        return files != null && files.length > 0;
    }

    private static String extractJsonStringValue(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyIdx = json.indexOf(marker);
        if (keyIdx == -1) return null;
        int valueStart = json.indexOf(':', keyIdx + marker.length());
        if (valueStart == -1) return null;
        int quoteStart = json.indexOf('"', valueStart + 1);
        if (quoteStart == -1) return null;
        int i = quoteStart + 1;
        StringBuilder sb = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char n = json.charAt(i + 1);
                switch (n) {
                    case 'n': sb.append('\n'); i += 2; continue;
                    case 'r': sb.append('\r'); i += 2; continue;
                    case 't': sb.append('\t'); i += 2; continue;
                    case '"': sb.append('"'); i += 2; continue;
                    case '\\': sb.append('\\'); i += 2; continue;
                    default: sb.append(n); i += 2; continue;
                }
            }
            if (c == '"') break;
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    private static String extractUrl(String content, String marker) {
        if (content == null) return null;
        int markerIdx = content.indexOf(marker);
        if (markerIdx < 0) return null;
        int start = content.lastIndexOf("http", markerIdx);
        if (start < 0) return null;
        int end = start;
        while (end < content.length() && isUrlChar(content.charAt(end))) {
            end++;
        }
        return content.substring(start, end);
    }

    private static boolean isUrlChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                || c == ':' || c == '/' || c == '.' || c == '?' || c == '='
                || c == '&' || c == '%' || c == '_' || c == '~' || c == '-';
    }

    private static String resolveLxTrackDownloadUrl() {
        try {
            System.out.println("[LxTrack] resolve-v4-char");
            URL apiUrl = new URL(LXTRACK_API_URL);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int code = conn.getResponseCode();
            System.out.println("[LxTrack] API响应码=" + code);
            if (code != 200) return null;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            String json = sb.toString();
            System.out.println("[LxTrack] API返回内容: " + json);

            String show = extractJsonStringValue(json, "app_update_show");
            System.out.println("[LxTrack] app_update_show=" + show);

            String allUrl = extractUrl(show, "LxTrack-all.jar");
            String kffUrl = extractUrl(show, "LxTrack-kff.jar");
            System.out.println("[LxTrack] allUrl=" + allUrl + " | kffUrl=" + kffUrl + " | hasKFF=" + hasKotlinForForge());
            if (allUrl != null && !allUrl.isEmpty()) {
                int s = show.indexOf(allUrl);
                int before = s > 0 ? show.codePointAt(s - 1) : -1;
                int after = s + allUrl.length() < show.length() ? show.codePointAt(s + allUrl.length()) : -1;
                System.out.println("[LxTrack] allUrl包裹字符codePoint 前=" + before + " 后=" + after);
            }

            String selected = hasKotlinForForge() ? kffUrl : allUrl;
            if (selected == null || selected.isEmpty()) {
                selected = extractJsonStringValue(json, "app_update_url");
                System.out.println("[LxTrack] 回退到 app_update_url=" + selected);
            }
            System.out.println("[LxTrack] 最终下载地址=" + selected);
            return (selected == null || selected.isEmpty()) ? null : selected;
        } catch (Exception e) {
            System.err.println("[LxTrack] 获取下载地址异常: " + e);
            e.printStackTrace();
            return null;
        }
    }

    public static void checkLxTrack() {
        if (readDisableLxTrackFromConfig()) return;
        if (lxTrackJarExists()) return;

        CountDownLatch doneLatch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            try {
                showLxTrackDownloadDialog();
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                doneLatch.countDown();
            }
        });
        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void showLxTrackDownloadDialog() {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(440, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 440, 200, 28, 28));

        GlassPanel panel = new GlassPanel();
        panel.setLayout(new BorderLayout(16, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel titleLabel = new JLabel("LxTrack · Updater", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        titleLabel.setForeground(new Color(240, 240, 240));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel statusLabel = new JLabel("正在获取下载地址...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(170, 170, 170));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("");
        progressBar.setBackground(new Color(30, 30, 30));
        progressBar.setForeground(new Color(201, 100, 66));
        progressBar.setBorderPainted(false);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setOpaque(false);
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(progressBar, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        JButton disableButton = createStyledButton("禁用 LxTrack 并跳过", new Color(201, 100, 66));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(disableButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);

        AtomicBoolean cancelled = new AtomicBoolean(false);
        AtomicBoolean finished = new AtomicBoolean(false);

        disableButton.addActionListener(e -> {
            if (finished.get()) {
                dialog.dispose();
                return;
            }
            writeDisableLxTrackToConfig(true);
            cancelled.set(true);
            dialog.dispose();
        });

        new Thread(() -> downloadLxTrack(dialog, statusLabel, progressBar, disableButton, cancelled, finished), "LxTrack-Download").start();

        dialog.setVisible(true);
    }

    // ==================== 视觉风格渲染 ====================

    private static final class GlassPanel extends JPanel {
        GlassPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawIslandBackground(g2, getWidth(), getHeight());
            drawFlowRibbons(g2, 0f, getWidth(), getHeight());
            drawGlassHighlights(g2, 0f, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static Color whiteAlpha(int a) {
        if (a < 0) return WHITE_ALPHA[0];
        if (a > 255) return WHITE_ALPHA[255];
        return WHITE_ALPHA[a];
    }

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
        for (float[] r : ribbons) {
            float phase = t * r[RI_SPEED] + r[RI_PHASE];

            float cx1 = r[RI_CP00] * w + flowNoise(r[RI_CP00] * 5f, phase, t * 0.3f) * w * 0.4f;
            float cy1 = r[RI_CP01] * h + flowNoise(phase, r[RI_CP01] * 5f, t * 0.35f) * h * 0.4f;
            float cx2 = r[RI_CP10] * w + flowNoise(r[RI_CP10] * 5f, phase + 1f, t * 0.4f) * w * 0.35f;
            float cy2 = r[RI_CP11] * h + flowNoise(phase + 1f, r[RI_CP11] * 5f, t * 0.3f) * h * 0.35f;
            float cx3 = r[RI_CP20] * w + flowNoise(r[RI_CP20] * 5f, phase + 2f, t * 0.45f) * w * 0.3f;
            float cy3 = r[RI_CP21] * h + flowNoise(phase + 2f, r[RI_CP21] * 5f, t * 0.33f) * h * 0.3f;
            float cx4 = r[RI_CP30] * w + flowNoise(r[RI_CP30] * 5f, phase + 3f, t * 0.38f) * w * 0.35f;
            float cy4 = r[RI_CP31] * h + flowNoise(phase + 3f, r[RI_CP31] * 5f, t * 0.36f) * h * 0.35f;

            float alpha = r[RI_ALPHA] + (float) Math.sin(t * 1.3f + r[RI_PHASE]) * 10f;
            int ai = Math.max(5, Math.min(60, (int) alpha));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ai / 255f));
            g2.setStroke(ribbonStroke);
            g2.setColor(whiteAlpha(ai));
            reusableCurve.setCurve(cx1, cy1, cx2, cy2, cx3, cy3, cx4, cy4);
            g2.draw(reusableCurve);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (ai * 0.6f) / 255f));
            g2.setStroke(ribbonStrokeGlow);
            g2.setColor(whiteAlpha((int) (ai * 0.8f)));
            g2.draw(reusableCurve);
        }
        g2.setComposite(orig);
    }

    private static void drawGlassHighlights(Graphics2D g2, float t, int w, int h) {
        float highlightH = h * 0.38f;

        float[] dist1 = {0.0f, 0.5f, 1.0f};
        Color[] colors1 = {
                whiteAlpha(70 + (int) (Math.sin(t * 0.5f) * 15)),
                whiteAlpha(30),
                whiteAlpha(0)
        };
        LinearGradientPaint lgp1 = new LinearGradientPaint(0, 0, 0, highlightH, dist1, colors1);
        g2.setPaint(lgp1);
        g2.fillRoundRect(0, 0, w, (int) highlightH, 48, 48);
        g2.fillRect(0, (int) (highlightH * 0.6f), w, (int) (highlightH * 0.4f));

        float[] dist2 = {0.0f, 1.0f};
        Color[] colors2 = {
                whiteAlpha(30 + (int) (Math.sin(t * 0.4f + 1f) * 10)),
                whiteAlpha(0)
        };
        LinearGradientPaint lgp2 = new LinearGradientPaint(0, h, 0, h * 0.65f, dist2, colors2);
        g2.setPaint(lgp2);
        g2.fillRoundRect(0, (int) (h * 0.6f), w, (int) (h * 0.4f), 48, 48);
    }

    private static void drawIslandBackground(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRoundRect(0, 0, w, h, 28, 28);
        g2.setColor(new Color(255, 255, 255, 15));
        g2.drawLine(w / 6, 1, w * 5 / 6, 1);
    }

    private static JButton createStyledButton(String text, Color bg) {
        JButton btn = new StyledButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        btn.setForeground(new Color(250, 249, 245));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHover(btn, bg);
        return btn;
    }

    private static final class StyledButton extends JButton {
        StyledButton(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static void addHover(JButton btn, Color bg) {
        MouseListener listener = (MouseListener) Proxy.newProxyInstance(
                MouseListener.class.getClassLoader(),
                new Class[]{MouseListener.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "mouseEntered": btn.setBackground(bg.brighter()); break;
                        case "mouseExited": btn.setBackground(bg); break;
                        case "mousePressed": btn.setBackground(bg.darker()); break;
                        case "mouseReleased": btn.setBackground(bg.brighter()); break;
                    }
                    return null;
                });
        btn.addMouseListener(listener);
    }

    private static void downloadLxTrack(JDialog dialog, JLabel statusLabel, JProgressBar progressBar, JButton disableButton, AtomicBoolean cancelled, AtomicBoolean finished) {
        File tempFile = null;
        try {
            String downloadUrl = resolveLxTrackDownloadUrl();
            if (downloadUrl == null) {
                finished.set(true);
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("获取下载地址失败");
                    disableButton.setText("关闭");
                });
                return;
            }
            System.out.println("[LxTrack] 下载URL=" + downloadUrl);

            HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "LxTrack-Downloader/1.0");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);
            conn.setInstanceFollowRedirects(true);

            int code = conn.getResponseCode();
            System.out.println("[LxTrack] 下载响应码=" + code + " ContentType=" + conn.getContentType() + " ContentLength=" + conn.getContentLength());
            if (code != 200) {
                finished.set(true);
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("下载失败: HTTP " + code);
                    disableButton.setText("关闭");
                });
                return;
            }

            int contentLength = conn.getContentLength();

            File modsDir = new File("mods");
            if (!modsDir.exists()) modsDir.mkdirs();
            tempFile = new File(modsDir, "LxTrack_download.tmp");

            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("正在下载 LxTrack...");
                progressBar.setIndeterminate(contentLength <= 0);
                progressBar.setValue(0);
                progressBar.setString(contentLength > 0 ? "0%" : "");
            });

            long total = 0;
            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buf = new byte[8192];
                int read;
                while ((read = in.read(buf)) != -1) {
                    if (cancelled.get()) break;
                    out.write(buf, 0, read);
                    total += read;
                    if (contentLength > 0) {
                        int pct = (int) (total * 100 / contentLength);
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(pct);
                            progressBar.setString(pct + "%");
                        });
                    }
                }
            }

            if (cancelled.get()) {
                if (tempFile.exists()) tempFile.delete();
                return;
            }

            if (!isValidJar(tempFile)) {
                System.out.println("[LxTrack] 下载内容无效，前200字节: " + readHead(tempFile, 200));
                if (tempFile.exists()) tempFile.delete();
                finished.set(true);
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("下载内容不是有效的 Jar 文件");
                    disableButton.setText("关闭");
                });
                return;
            }

            File destFile = new File(modsDir, "LxTrack.jar");
            if (destFile.exists()) destFile.delete();
            if (!tempFile.renameTo(destFile)) {
                if (tempFile.exists()) tempFile.delete();
                finished.set(true);
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("下载完成但保存失败，请检查 mods 目录权限");
                    disableButton.setText("关闭");
                });
                return;
            }

            finished.set(true);
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("LxTrack 下载完成");
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                progressBar.setString("100%");
            });
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            SwingUtilities.invokeLater(dialog::dispose);
        } catch (Exception ex) {
            finished.set(true);
            if (tempFile != null && tempFile.exists()) tempFile.delete();
            String msg = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("下载失败: " + msg);
                disableButton.setText("关闭");
            });
        }
    }
}