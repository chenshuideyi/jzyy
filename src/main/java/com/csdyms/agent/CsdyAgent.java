package com.csdyms.agent;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

public class CsdyAgent implements ClassFileTransformer {
    public static final List<String> InterPackage = Arrays.asList("com/csdy","net/minecraft","net/minecraftforge","com/mojang","mezz/jei","de/keksuccino","java","jdk");
    public static final List<String> InterMethod = Arrays.asList(
            "klassptr","classptr","replaceClass", "m_88315_", "m_6883_", "inventoryTick", "onInventoryTick"
            ,"clearContent","dropAll","m_36071_","m_6211_","drop","m_104455_","setStackInSlot"
    );
    public static final List<String> BlackMe = Arrays.asList("MobTrait");
    public CsdyAgent() {
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        for(String interpackage : InterPackage){
            if(className.startsWith(interpackage)) return classfileBuffer;
        }
        return this.transformClass(className, classfileBuffer);
    }

    private byte[] transformClass(final String className, byte[] classfileBuffer) {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, 3);
        ClassVisitor visitor = new ClassVisitor(458752, writer) {
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

                for(String intermethod : InterMethod){
                    if(name.equals(intermethod)) {
                        System.out.println("CSDY在游戏启动前删除了一个方法: " + className + "." + name);
                        return new NullMethod(this.api);
                    }
                }


                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };reader.accept(visitor, 0);
        return writer.toByteArray();
    }
//    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
//        Iterator packagename = InterPackage.iterator();
//
//        String interpackage;
//        do {
//            if (!packagename.hasNext()) {
//                return classfileBuffer;
//            }
//
//            interpackage = (String)packagename.next();
//        } while(!className.startsWith(interpackage));
//
//        return this.transformClass(className, classfileBuffer);
//    }

//    private byte[] transformClass1(final String className, byte[] classfileBuffer) {
//        ClassReader cr = new ClassReader(classfileBuffer);
//        ClassWriter cw = new ClassWriter(cr, 3);
//        ClassVisitor cv = new ClassVisitor(458752, cw) {
//            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//                Iterator methodname = CsdyAgent.InterMethod.iterator();
//
//                String intermethod;
//                do {
//                    if (!methodname.hasNext()) {
//                        return super.visitMethod(access, name, descriptor, signature, exceptions);
//                    }
//
//                    intermethod = (String)methodname.next();
//                } while(!name.equals(intermethod));
//
//                System.out.println("CSDY在游戏启动前删除了一个其他mod的方法: " + className + "." + name);
//                return new NullMethod(this.api);
//            }
//        };
//        cr.accept(cv, 0);
//        return cw.toByteArray();
//    }

    private static class NullMethod extends MethodVisitor {
        public NullMethod(int api) {
            super(api);
        }

        public void visitCode() {
        }
    }
}
