package com.csdy.jzyy.coremod;

import com.csdy.jzyy.agent.LxAgent;
import com.csdy.jzyy.ms.CoreMsUtil;
import com.csdy.jzyy.ms.enums.EntityCategory;
import com.google.common.collect.Iterables;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class CsdyLaunchPluginService implements ILaunchPluginService {

    private static final String owner = "com/csdy/jzyy/ms/CoreMethod";
    private static boolean javaVersionChecked = false;

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

        if (!javaVersionChecked) {
            checkJavaVersion();
            javaVersionChecked = true;
        }

        if ("net/minecraft/world/entity/LivingEntity".equals(classNode.name)) {
            System.out.println("正在尝试修改getHealth");
            return transformLivingEntity(classNode);
        }

        return transformMethodCalls(classNode);
    }

    private boolean transformLivingEntity(ClassNode classNode) {
        AtomicBoolean transformed = new AtomicBoolean(false);
        // 筛选出 LivingEntity 类中的 getHealth 方法 (m_21223_)
        classNode.methods.stream()
                .filter(method -> "m_21223_".equals(method.name) && "()F".equals(method.desc))
                .forEach(method -> {
                    InsnList newInstructions = new InsnList();
                    LabelNode originalCode = new LabelNode();

                    // 1. 调用辅助方法获取强制生命值
                    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // 加载 'this'
                    newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/csdy/jzyy/ms/JzyyHealthHelper", "getForcedHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F", false));

                    // 2. 将返回的 float 值存储到一个新的局部变量中
                    int forcedHealthVarIndex = method.maxLocals;
                    newInstructions.add(new VarInsnNode(Opcodes.FSTORE, forcedHealthVarIndex));

                    // 3. 再次加载这个值，用于和 -1.0f 比较
                    newInstructions.add(new VarInsnNode(Opcodes.FLOAD, forcedHealthVarIndex));
                    newInstructions.add(new LdcInsnNode(-1.0f)); // 加载常量 -1.0f
                    newInstructions.add(new InsnNode(Opcodes.FCMPL)); // 比较栈顶的两个浮点数

                    // 4. 如果比较结果为0 (即相等)，说明辅助方法返回了-1.0f，我们就跳转到原版代码
                    newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, originalCode));

                    // 5. 如果不相等，说明需要修改生命值，我们再次加载存储的强制生命值并返回
                    newInstructions.add(new VarInsnNode(Opcodes.FLOAD, forcedHealthVarIndex));
                    newInstructions.add(new InsnNode(Opcodes.FRETURN));

                    // 6. 跳转标签，指向原方法的开始位置
                    newInstructions.add(originalCode);

                    // 为我们创建的局部变量增加方法的最大局部变量计数
                    method.maxLocals++;

                    // 将我们创建的字节码指令插入到原方法的开头
                    method.instructions.insert(newInstructions);

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
            if (classNode.name.contains("kakiku")) {
                addPremain();
                byte[] AgentByte = readLxAgentClassBytes();
                LxAgent.getInstance(AgentByte);
                System.out.println("kakiku针对神全家已无 正在启动premain神力");
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

    public void addPremain() {
        try {
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + "/bin/java";
            String classpath = System.getProperty("java.class.path");
            String className = getClass().getCanonicalName();
            String agentJarPath = "mods\\" + getAgentJarName();
            String newJvmArgs = "-javaagent:" + agentJarPath;
            ProcessBuilder processBuilder = new ProcessBuilder(javaBin, newJvmArgs, "-cp", classpath, className);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readLxAgentClassBytes() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("com/csdy/jzyy/agent/LxAgent.class")) {
            if (is == null) {
                throw new IOException("LxAgent.class 未找到，请检查类路径");
            }
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("读取 LxAgent 字节码失败", e);
        }
    }

    public String getAgentJarName() {
        java.security.CodeSource codeSource = LxAgent.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            java.net.URL location = codeSource.getLocation();
            if (location != null && "file".equals(location.getProtocol())) {
                try {
                    File jarFile = new File(location.toURI());
                    if (jarFile.isFile()) {
                        return jarFile.getName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
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
            JOptionPane.showMessageDialog(null,
                    "请更换Java版本为17\n当前版本: " + version,
                    "Java版本错误",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

}
