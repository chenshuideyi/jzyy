package com.csdy.jzyy.agent;

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
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                           java.security.ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        ClassNode classNode = new ClassNode();
        if (className.contains("kakiku")) {
            TransformClass(classNode);
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
