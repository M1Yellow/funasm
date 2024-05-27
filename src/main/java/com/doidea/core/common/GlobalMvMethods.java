package com.doidea.core.common;

import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * MethodVisitor 通用方法提取
 */
public class GlobalMvMethods {

    /**
     * 打印调用堆栈，RuntimeException 方式
     */
    public static void addPrintRuntimeExceptionStackTrace(MethodVisitor mv) {
        // new RuntimeException(">>>> Print stacktrace: \n").printStackTrace();
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(">>>> Print stacktrace: \n");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/RuntimeException", "printStackTrace", "()V", false);
    }

    /**
     * 打印调用堆栈，Thread.currentThread().getStackTrace() 方式
     */
    public static void addPrintThreadStackTrace(MethodVisitor mv) {
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
