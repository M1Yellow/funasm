package com.doidea.core;

import com.doidea.core.method.impl.DoBeforeDispatcher;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * 自定义方法访问处理器
 */
public class MyMethodVisitor extends MethodVisitor {

    private final String targetMethod;
    private final int methodAccess;
    private final String methodDesc;


    public MyMethodVisitor(int api, MethodVisitor mv, String targetMethod, int access, String descriptor) {
        super(api, mv);
        this.targetMethod = targetMethod;
        this.methodAccess = access;
        this.methodDesc = descriptor;
    }


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

    /**
     * 进入方法之前执行
     */
    @Override
    public void visitCode() {

        switch (targetMethod) {
            case "setTitle":
                DoBeforeDispatcher.INSTANCE.doBeforeJDialogSetTitle(mv, methodAccess, methodDesc);
                break;
            case "<init>": // <init> 为构造方法
                DoBeforeDispatcher.INSTANCE.doBeforeValidateKey(mv, methodAccess, methodDesc);
                break;
            case "j":
                // base64 加密参数处理
                DoBeforeDispatcher.INSTANCE.doBeforeBase64Fun(mv, methodAccess, methodDesc);
                break;
            default:
                break;
        }

        // 方法原有逻辑
        super.visitCode();
    }


    /**
     * 方法返回之前执行
     */
    @Override
    public void visitInsn(int opcode) {
        // TODO 方法退出时处理
        if (opcode == Opcodes.ATHROW || (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
            switch (targetMethod) {
                case "j":
                    //DoAfterDispatcher.INSTANCE.doAfterMachineId(mv, methodAccess, methodDesc);
                    break;
                default:
                    break;
            }
        }

        // 方法原有逻辑
        super.visitInsn(opcode);
    }

}
