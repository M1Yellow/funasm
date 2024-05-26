package com.doidea.core.method.impl;

import com.doidea.core.method.DoBefore;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public enum DoBeforeDispatcher implements DoBefore {

    INSTANCE;

    @Override
    public boolean doBeforeJDialogSetTitle(Object methodVisitor, int methodAccess, String methodDesc) {

        // 打印调用堆栈
        //addPrintRuntimeExceptionStackTrace(mv);

        MethodVisitor mv = (MethodVisitor) methodVisitor;

        // System.out.println(title);
        Label label1 = new Label();
        mv.visitLabel(label1);
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
        // 配置了 ClassWriter.COMPUTE_FRAMES 自动计算
        //mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        // TODO 异常信息建议不写，异常信息可能会上报。后续再研究 hook 异常堆栈打印方法，屏蔽添加的手动异常
        //mv.visitLdcInsn("Licenses dialog abort.");
        //mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(label4);

        return true;
    }

    @Override
    public boolean doBeforeValidateKey(Object methodVisitor, int methodAccess, String methodDesc) {

        MethodVisitor mv = (MethodVisitor) methodVisitor;

        // 打印调用堆栈
        //addPrintRuntimeExceptionStackTrace(mv);

        // 抛出异常直接终止请求
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("ValidateKeyRequest abort.");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);

        return true;
    }

    @Override
    public boolean doBeforeBase64Fun(Object methodVisitor, int methodAccess, String methodDesc) {

        MethodVisitor mv = (MethodVisitor) methodVisitor;

        // 打印参数
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(LLOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);

        // 修改入参
        // 每月 14 号变换一次
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(LLOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitMethodInsn(INVOKESTATIC, "java/time/LocalDateTime", "now", "()Ljava/time/LocalDateTime;", false);
        mv.visitVarInsn(ASTORE, 3);
        Label label3 = new Label();
        mv.visitLabel(label3);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/time/LocalDateTime", "getYear", "()I", false);
        mv.visitVarInsn(ISTORE, 4);
        Label label4 = new Label();
        mv.visitLabel(label4);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/time/LocalDateTime", "getMonthValue", "()I", false);
        mv.visitVarInsn(ISTORE, 5);
        Label label5 = new Label();
        mv.visitLabel(label5);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/time/LocalDateTime", "getDayOfMonth", "()I", false);
        mv.visitVarInsn(ISTORE, 6);
        Label label6 = new Label();
        mv.visitLabel(label6);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(II)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), new Object[]{"MAC-\u0001\u000114"});
        mv.visitVarInsn(ASTORE, 7);
        Label label7 = new Label();
        mv.visitLabel(label7);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitIntInsn(BIPUSH, 14);
        Label label8 = new Label();
        mv.visitJumpInsn(IF_ICMPLT, label8);
        Label label9 = new Label();
        mv.visitLabel(label9);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(II)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), new Object[]{"MAC-\u0001\u000128"});
        mv.visitVarInsn(ASTORE, 7);
        mv.visitLabel(label8);
        mv.visitFrame(Opcodes.F_FULL, 7, new Object[]{"[B", Opcodes.LONG, "java/time/LocalDateTime", Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER, "java/lang/String"}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 7);
        mv.visitFieldInsn(GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);
        mv.visitVarInsn(ASTORE, 0);
        Label label10 = new Label();
        mv.visitLabel(label10);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        return true;
    }

    @Override
    public boolean doBeforeMachineId(Object methodVisitor, int methodAccess, String methodDesc) {

        MethodVisitor mv = (MethodVisitor) methodVisitor;

        // 打印入参
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

        return true;
    }


}
