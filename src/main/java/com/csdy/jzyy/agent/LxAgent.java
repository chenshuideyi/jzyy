package com.csdy.jzyy.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getReturnType;

public class LxAgent implements ClassFileTransformer {
    private static final List<String> INTERCEPTED_PACKAGE_NAMES = Arrays.asList(
          "kakiku"
    );
    private static LxAgent INSTANCE;
    public LxAgent() {

    }
    private static class HiddenClassLoader extends ClassLoader {
        private final byte[] classBytes;

        public HiddenClassLoader(ClassLoader parent, byte[] classBytes) {
            super(parent);
            this.classBytes = classBytes;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(LxAgent.class.getName())) {
                byte[] copy = Arrays.copyOf(classBytes, classBytes.length);
                Arrays.fill(classBytes, (byte)0);
                return defineClass(name, copy, 0, copy.length);
            }
            return super.findClass(name);
        }
    }

    public static LxAgent getInstance(byte[] classBytes) {
        if (INSTANCE == null) {
            try {
                ClassLoader cl = new HiddenClassLoader(
                        LxAgent.class.getClassLoader().getParent(),
                        classBytes
                );
                Class<?> clazz = cl.loadClass(LxAgent.class.getName());
                INSTANCE = (LxAgent) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Agent init failed", e);
            }
        }
        return INSTANCE;
    }


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            java.security.ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className != null && className.contains("kakiku")) {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            TransformClass(cn);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        }
        return classfileBuffer;
    }

    private void TransformClass(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            NullMethod(method);
            System.out.println("Transform Method: "+ classNode.name + method.name + method.desc);
        }
        classNode.fields.clear();
        classNode.interfaces.clear();
    }

    private void NullMethod(MethodNode method) {
        if ("init".contains(method.name) || "clinit".contains(method.name)) return;

        InsnList newBody = new InsnList();
        Type returnType = getReturnType(method.desc);

        switch (returnType.getSort()) {
            case Type.VOID:
                newBody.add(new InsnNode(RETURN));
                break;
            case Type.BOOLEAN:
            case Type.BYTE:
            case Type.CHAR:
            case Type.SHORT:
            case Type.INT:
                newBody.add(new InsnNode(ICONST_0));
                newBody.add(new InsnNode(IRETURN));
                break;
            case Type.LONG:
                newBody.add(new LdcInsnNode(0L));
                newBody.add(new InsnNode(LRETURN));
                break;
            case Type.FLOAT:
                newBody.add(new InsnNode(FCONST_0));
                newBody.add(new InsnNode(FRETURN));
                break;
            case Type.DOUBLE:
                newBody.add(new InsnNode(DCONST_0));
                newBody.add(new InsnNode(DRETURN));
                break;
            case Type.OBJECT:
            case Type.ARRAY:
                newBody.add(new InsnNode(ACONST_NULL));
                newBody.add(new InsnNode(ARETURN));
                break;
            default:
                newBody.add(new InsnNode(RETURN));
        }

        method.instructions = newBody;
        method.tryCatchBlocks = null;
        method.localVariables = null;
        method.maxStack = -1;
        method.maxLocals = -1;
    }
}
