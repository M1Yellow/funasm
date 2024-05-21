package com.doidea.core;

import jdk.internal.org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class Launcher {

    private static volatile boolean loaded = false;

    public static void main(String[] args) {
        // 程序自身的日志不会在IDEA日志中打印
        System.out.println(">>>> Launcher main >>>>");
    }

    /**
     * 在 JVM 启动前加载
     *
     * @param args
     * @param instrumentation
     */
    public static void premain(String args, Instrumentation instrumentation) {

        if (loaded) {
            System.err.println(">>>> multiple javaagent jar.");
            return;
        }

        try {
            MyClassFileTransformer transformer = new MyClassFileTransformer();
            instrumentation.addTransformer(transformer);
            //instrumentation.addTransformer(transformer, true);
            loaded = true;
        } catch (Exception e) {
            System.err.println(">>>> instrumentation addTransformer failed.");
            e.printStackTrace();
        }
    }

    public static class MyClassFileTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            //System.out.println(">>>> loading Class: " + className);
            // TODO 包名关键字简单混淆一下，稍微减缓 DMCA 的速度
            //  后续版本窗口包名可能会变化，或者加入更严格检测和校验，当下版本能用一两年也不错了，况且新版本总会有新的破-解办法出现！
            String modifyClassName = "com." + "intel" + "lij" + ".openapi.ui.DialogWrapper";
            String modifyClassMethod = "setTitle";
            String loadClassName = modifyClassName.replace(".", "/");
            // 找到了指定类
            //if (className.contains("DialogWrapper")) {
            if (className.equals(loadClassName)) {
                System.out.println(">>>> Target Class: " + className);
                try {
                    return getAsmBytes(loader, className, classfileBuffer, modifyClassMethod);
                } catch (Throwable e) {
                    System.err.println(">>>> getAsmBytes Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            return classfileBuffer;
        }

        /**
         * 获取 ASM 修改后的字节码
         *
         * @param loader          类加载器
         * @param className       类名
         * @param classfileBuffer 字节码
         * @return ASM 修改后的字节码
         */
        private byte[] getAsmBytes(ClassLoader loader, String className, byte[] classfileBuffer, String targetMethod) {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算max stacks、max locals和stack map frame的具体内容
            ClassVisitor cv = new MyClassVisitor(ASM8, cw, targetMethod);
            cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return cw.toByteArray();
        }
    }

    /**
     * 自定义类访问处理器
     */
    public static class MyClassVisitor extends ClassVisitor {

        private final String targetMethod;

        public MyClassVisitor(int api, ClassVisitor classVisitor, String targetMethod) {
            super(api, classVisitor);
            this.targetMethod = targetMethod;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            // 指定方法
            if (mv != null && name.equals(targetMethod)) {
                System.out.println(">>>> targetMethod: " + targetMethod);
                return new MyMethodVisitor(ASM8, mv);
            }
            return mv;
        }
    }

    /**
     * 自定义方法访问处理器
     */
    public static class MyMethodVisitor extends MethodVisitor {

        public MyMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        /**
         * 进入方法之前处理
         */
        @Override
        public void visitCode() {
            // System.out.println(title);
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitVarInsn(ALOAD, 1);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            /*
            if (title.trim().equalsIgnoreCase("Licenses") || title.trim().equalsIgnoreCase("许可证")) {
                throw new RuntimeException("Licenses dialog abort.");
            }
            */
            Label label2 = new Label();
            super.visitLabel(label2);
            super.visitVarInsn(ALOAD, 1);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
            super.visitLdcInsn("Licenses");
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            Label label3 = new Label();
            super.visitJumpInsn(IFNE, label3); // IFNE 不等于0跳转，0-false; 1-true
            super.visitVarInsn(ALOAD, 1);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
            super.visitLdcInsn("\u8bb8\u53ef\u8bc1");
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            Label label4 = new Label();
            super.visitJumpInsn(IFEQ, label4);
            super.visitLabel(label3);
            super.visitTypeInsn(NEW, "java/lang/RuntimeException");
            super.visitInsn(DUP);
            super.visitLdcInsn("Licenses dialog abort.");
            super.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            super.visitInsn(ATHROW);
            super.visitLabel(label4);

            // 方法原有逻辑
            super.visitCode();
        }
    }

}
