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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.csdy.jzyy.JzyyConfig.I_KNOW_WHAT_I_AM_DOING;

public class CsdyLaunchPluginService implements ILaunchPluginService {

    private static final String owner = "com/csdy/jzyy/ms/CoreMethod";
    private static boolean javaVersionChecked = false;

    static {
 //       new Thread(CsdyLaunchPluginService::checkLxTrack, "LxTrack-Check").start();
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
        return files != null && files.length > 0;
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

    private static void checkLxTrack() {
        if (readDisableLxTrackFromConfig()) return;
        if (lxTrackJarExists()) return;

        try {
            SwingUtilities.invokeAndWait(() -> {
                JDialog dialog = new JDialog();
                dialog.setAlwaysOnTop(true);
                dialog.setTitle("LxTrack 检测");

                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                JLabel messageLabel = new JLabel("未检测到LxTrack，是否获取？");
                panel.add(messageLabel, BorderLayout.NORTH);

                JCheckBox disableCheckbox = new JCheckBox("不再提示，并永久禁用 LxTrack");
                panel.add(disableCheckbox, BorderLayout.CENTER);

                JProgressBar progressBar = new JProgressBar();
                progressBar.setVisible(false);

                JButton yesButton = new JButton("是");
                JButton noButton = new JButton("否");

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(yesButton);
                buttonPanel.add(noButton);

                JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
                bottomPanel.add(progressBar, BorderLayout.NORTH);
                bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

                panel.add(bottomPanel, BorderLayout.SOUTH);

                dialog.setContentPane(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setModal(true);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

                yesButton.addActionListener(e -> {
                    if (disableCheckbox.isSelected()) {
                        writeDisableLxTrackToConfig(true);
                        dialog.dispose();
                        return;
                    }
                    yesButton.setEnabled(false);
                    noButton.setEnabled(false);
                    disableCheckbox.setEnabled(false);
                    messageLabel.setText("正在获取下载地址...");
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                    dialog.pack();

                    new Thread(() -> {
                        try {
                            URL apiUrl = new URL(LXTRACK_API_URL);
                            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
                            conn.setConnectTimeout(10000);
                            conn.setReadTimeout(10000);

                            if (conn.getResponseCode() != 200) {
                                SwingUtilities.invokeLater(() -> {
                                    try {
                                        messageLabel.setText("获取下载地址失败: HTTP " + conn.getResponseCode());
                                    } catch (IOException ex) {
                                        System.err.println("获取下载地址失败: " + ex.getMessage());
                                    }
                                    progressBar.setVisible(false);
                                    noButton.setEnabled(true);
                                    noButton.setText("关闭");
                                    dialog.pack();
                                });
                                return;
                            }

                            StringBuilder sb = new StringBuilder();
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = br.readLine()) != null) sb.append(line);
                            }

                            String json = sb.toString();
                            int codeIdx = json.indexOf("\"code\"");
                            if (codeIdx == -1) {
                                SwingUtilities.invokeLater(() -> {
                                    messageLabel.setText("API返回格式异常");
                                    progressBar.setVisible(false);
                                    noButton.setEnabled(true);
                                    noButton.setText("关闭");
                                    dialog.pack();
                                });
                                return;
                            }

                            int codeStart = json.indexOf(':', codeIdx) + 1;
                            int codeEnd = json.indexOf(',', codeStart);
                            if (codeEnd == -1) codeEnd = json.indexOf('}', codeStart);
                            int code = Integer.parseInt(json.substring(codeStart, codeEnd).trim());

                            if (code != 200) {
                                SwingUtilities.invokeLater(() -> {
                                    messageLabel.setText("API返回异常: code=" + code);
                                    progressBar.setVisible(false);
                                    noButton.setEnabled(true);
                                    noButton.setText("关闭");
                                    dialog.pack();
                                });
                                return;
                            }

                            int urlIdx = json.indexOf("\"app_update_url\"");
                            if (urlIdx == -1) {
                                SwingUtilities.invokeLater(() -> {
                                    messageLabel.setText("未找到下载地址");
                                    progressBar.setVisible(false);
                                    noButton.setEnabled(true);
                                    noButton.setText("关闭");
                                    dialog.pack();
                                });
                                return;
                            }

                            int valStart = json.indexOf(':', urlIdx) + 1;
                            int quoteStart = json.indexOf('"', valStart) + 1;
                            int quoteEnd = json.indexOf('"', quoteStart);
                            String dlUrl = json.substring(quoteStart, quoteEnd);

                            SwingUtilities.invokeLater(() -> {
                                messageLabel.setText("正在下载 LxTrack...");
                                progressBar.setIndeterminate(false);
                                progressBar.setValue(0);
                                dialog.pack();
                            });

                            URL downloadUrl = new URL(dlUrl);
                            HttpURLConnection dlConn = (HttpURLConnection) downloadUrl.openConnection();
                            dlConn.setRequestMethod("GET");
                            dlConn.setRequestProperty("User-Agent", "LxTrack-Updater/1.0");
                            dlConn.setConnectTimeout(30000);
                            dlConn.setReadTimeout(120000);

                            int contentLength = dlConn.getContentLength();
                            if (contentLength > 0) {
                                SwingUtilities.invokeLater(() -> progressBar.setMaximum(contentLength));
                            }

                            File modsDir = new File("mods");
                            if (!modsDir.exists()) modsDir.mkdirs();
                            File tempFile = new File(modsDir, "LxTrack_download.tmp");

                            try (InputStream in = dlConn.getInputStream();
                                 FileOutputStream out = new FileOutputStream(tempFile)) {
                                byte[] buf = new byte[8192];
                                int bytesRead;
                                long total = 0;
                                while ((bytesRead = in.read(buf)) != -1) {
                                    out.write(buf, 0, bytesRead);
                                    total += bytesRead;
                                    if (contentLength > 0) {
                                        long finTotal = total;
                                        SwingUtilities.invokeLater(() -> progressBar.setValue((int) finTotal));
                                    }
                                }
                            }

                            File destFile = new File(modsDir, "LxTrack.jar");
                            if (destFile.exists()) destFile.delete();
                            if (!tempFile.renameTo(destFile)) {
                                tempFile.delete();
                                SwingUtilities.invokeLater(() -> {
                                    messageLabel.setText("下载完成但重命名失败，请手动重命名");
                                    progressBar.setVisible(false);
                                    noButton.setEnabled(true);
                                    noButton.setText("关闭");
                                    dialog.pack();
                                });
                                return;
                            }

                            SwingUtilities.invokeLater(() -> {
                                messageLabel.setText("LxTrack 下载完成！");
                                progressBar.setVisible(false);
                                noButton.setEnabled(true);
                                noButton.setText("确定");
                                dialog.pack();
                            });

                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> {
                                messageLabel.setText("下载失败: " + ex.getMessage());
                                progressBar.setVisible(false);
                                noButton.setEnabled(true);
                                noButton.setText("关闭");
                                dialog.pack();
                            });
                        }
                    }, "LxTrack-Download").start();
                });

                noButton.addActionListener(e -> {
                    if (disableCheckbox.isSelected()) {
                        writeDisableLxTrackToConfig(true);
                    }
                    dialog.dispose();
                });

                dialog.setVisible(true);
            });
        } catch (Exception ignored) {
        }
    }
}