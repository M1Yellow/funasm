package com.doidea.core.domain;

import jdk.internal.org.objectweb.asm.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * 自定义方法访问处理器
 */
public class MyMethodVisitor extends MethodVisitor {

    private final String currMethod;
    private final int methodAccess;
    private final String methodDesc;


    public MyMethodVisitor(int api, MethodVisitor mv, String currMethod, int access, String descriptor) {
        super(api, mv);
        this.currMethod = currMethod;
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

        switch (currMethod) {
            case "setTitle":
                if (doJDialogSetTitleEnter(mv, methodAccess, methodDesc)) break;
                break;
            case "<init>": // <init> 为构造方法
                //if (doUrlEnter(mv, methodAccess, methodDesc)) break;
                if (doValidateKeyEnter(mv, methodAccess, methodDesc)) break;
                break;
            case "j":
                // base64 加密参数处理
                if (doBase64BeforeFunEnter(mv, methodAccess, methodDesc)) break;
                //if (doMachineIdEnter(mv, methodAccess, methodDesc)) break;
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
            switch (currMethod) {
                case "j":
                    //if (doMachineIdEnd(mv, methodAccess, methodDesc)) break;
                    break;
                default:
                    break;
            }
        }

        // 方法原有逻辑
        super.visitInsn(opcode);
    }


    /**
     * JDialog setTitle 修改
     */
    private boolean doJDialogSetTitleEnter(MethodVisitor mv, int methodAccess, String methodDesc) {

        if (!methodDesc.equals("(Ljava/lang/String;)V")) return false;
        System.out.println(">>>> doUrlEnter target methodDesc: " + methodDesc);

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

        return true;
    }

    /**
     * java.net.URL new URL(url) 添加参数打印
     */
    private boolean doUrlEnter(MethodVisitor mv, int methodAccess, String methodDesc) {

        if (!methodDesc.equals("(Ljava/lang/String;)V")) return false;
        System.out.println(">>>> doUrlEnter target methodDesc: " + methodDesc);

        // 打印调用堆栈
        //addPrintRuntimeExceptionStackTrace(mv);

        // System.out.println(url);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        return true;
    }

    /**
     * validateKey.action 验证许可证密钥请求
     */
    private boolean doValidateKeyEnter(MethodVisitor mv, int methodAccess, String methodDesc) {
        if (methodDesc.equals("()V")) return false;
        // (JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IJ)V
        System.out.println(">>>> doValidateKeyEnter target methodDesc: " + methodDesc);

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

    /**
     * base64 encode 前一个关键处理方法
     */
    private boolean doBase64BeforeFunEnter(MethodVisitor mv, int methodAccess, String methodDesc) {
        // 打印调用堆栈
        //addPrintRuntimeExceptionStackTrace(mv);

        //System.out.println(">>>> methodDesc: " + methodDesc);
        // 根据 methodDesc 区分重载方法
        if (!methodDesc.equals("([BJ)[B")) return false;
        System.out.println(">>>> doBase64BeforeFunEnter target methodDesc: " + methodDesc);

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

    /**
     * 生成 machineId 进入时调用
     */
    private boolean doMachineIdEnter(MethodVisitor mv, int methodAccess, String methodDesc) {
        // 根据 methodDesc 区分重载方法，注意对象类型有“;”
        if (!methodDesc.equals("(II)Ljava/lang/String;")) return false;
        System.out.println(">>>> doMachineIdEnter target methodDesc: " + methodDesc);

        // 打印入参
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

        return true;
    }

    /**
     * 生成 machineId 方法返回之前执行
     */
    private boolean doMachineIdEnd(MethodVisitor mv, int methodAccess, String methodDesc) {
        if (!methodDesc.equals("(II)Ljava/lang/String;")) return false;
        System.out.println(">>>> doMachineIdEnd target methodDesc: " + methodDesc);

        // 打印返回值
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 6); // ALOAD 的 index 从反编译的 smali 代码查看
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        // 修改返回值
        mv.visitMethodInsn(INVOKESTATIC, "java/util/UUID", "randomUUID", "()Ljava/util/UUID;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 6);

        // 打印修改后的值
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        return true;
    }
}
