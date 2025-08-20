package com.csdy.jzyy.coremod;

import com.csdy.jzyy.agent.LxAgent;
import com.google.common.collect.Iterables;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class CsdyLaunchPluginService implements ILaunchPluginService {
    private static final String owner = "com/csdy/jzyy/ms/CoreMethod";

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
        return transform(classNode);
    }

    private boolean transform(ClassNode classNode) {

        checkJavaVersion();

        if (!classNode.name.startsWith("net/minecraft/") && !classNode.name.startsWith("net/minecraftforge/"))
            return false;
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
//                    case "m_6084_" -> {
//                        rMethod(call, "isAlive", "(Lnet/minecraft/world/entity/Entity;)Z");
//                        rewrite = true;
//                    }
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

    public static void checkJavaVersion() {
        String version = System.getProperty("java.version");
        if (!version.startsWith("17")) {
            JOptionPane.showMessageDialog(null,
                    "请更换Java版本为17\n当前版本: " + version,
                    "Java版本错误",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1); // 结束进程
        }
    }

}
