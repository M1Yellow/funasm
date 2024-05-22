package com.doidea.core.domain;

import jdk.internal.org.objectweb.asm.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * 自定义方法访问处理器
 */
public class MyMethodVisitor extends MethodVisitor {

    private final String currMethod;
    private final int access;
    private final String descriptor;


    public MyMethodVisitor(int api, MethodVisitor methodVisitor, String currMethod, int access, String descriptor) {
        super(api, methodVisitor);
        this.currMethod = currMethod;
        this.access = access;
        this.descriptor = descriptor;
    }

    /**
     * 进入方法之前处理
     */
    @Override
    public void visitCode() {

        switch (currMethod) {
            case "setTitle":
                doJDialogSetTitleEnter(mv, access, descriptor);
                break;
            case "<init>": // <init> 为构造方法
                doUrlEnter(mv, access, descriptor);
                break;
            default:
                break;
        }

        // 方法原有逻辑
        mv.visitCode();
    }

    /**
     * JDialog setTitle 修改
     */
    private void doJDialogSetTitleEnter(MethodVisitor mv, int methodAccess, String methodDesc) {
        // System.out.println(title);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            /*
            // title = title.trim(); // 尽可能不改动原 title
            if (title.trim().equalsIgnoreCase("Licenses") || title.trim().equalsIgnoreCase("许可证")) {
                throw new RuntimeException("Licenses dialog abort.");
            }
            */
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
        mv.visitLdcInsn("Licenses");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
        Label label3 = new Label();
        mv.visitJumpInsn(IFNE, label3); // IFNE 不等于0跳转，0-false; 1-true
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
        mv.visitLdcInsn("许可证");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
        Label label4 = new Label();
        mv.visitJumpInsn(IFEQ, label4); // IFEQ 等于0跳转，0-false; 1-true
        mv.visitLabel(label3);
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Licenses dialog abort.");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(label4);
    }

    /**
     * java.net.URL new URL(url) 添加参数打印
     */
    private void doUrlEnter(MethodVisitor mv, int methodAccess, String methodDesc) {
        // System.out.println(url);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        // 打印调用堆栈
        // Stream.of(Thread.currentThread().getStackTrace()).forEach(System.out::println);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/stream/Stream", "of", "([Ljava/lang/Object;)Ljava/util/stream/Stream;", true);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitInsn(POP);
        mv.visitInvokeDynamicInsn("accept", "(Ljava/io/PrintStream;)Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false), Type.getType("(Ljava/lang/StackTraceElement;)V")});
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "forEach", "(Ljava/util/function/Consumer;)V", true);
    }
}
