package com.doidea.core.method.impl;

import com.doidea.core.method.DoAfter;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public enum DoAfterDispatcher implements DoAfter {

    INSTANCE;

    @Override
    public boolean doAfterMachineId(MethodVisitor mv, int methodAccess, String methodDesc) {
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
