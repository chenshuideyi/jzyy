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
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.csdy.jzyy.JzyyConfig.I_KNOW_WHAT_I_AM_DOING;

public class CsdyLaunchPluginService implements ILaunchPluginService {

    private static final String owner = "com/csdy/jzyy/ms/CoreMethod";
    private static boolean javaVersionChecked = false;

    static {
        // 委托给 JzyyHotUpdater（独立类，不引用 Minecraft 类，避免 bootstrap 阶段 NoClassDefFoundError）
        new Thread(JzyyHotUpdater::init, "HotUpdate-GUI").start();
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
}
