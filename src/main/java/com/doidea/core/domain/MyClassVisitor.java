package com.doidea.core.domain;

import com.doidea.core.Launcher;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import java.util.List;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * 自定义类访问处理器
 */
public class MyClassVisitor extends ClassVisitor {

    private final String currClass;
    private final List<String> targetMethods;

    public MyClassVisitor(int api, ClassVisitor classVisitor, String currClass) {
        super(api, classVisitor);
        this.currClass = currClass;
        this.targetMethods = Launcher.targetClassMethodMap.get(currClass.replace("/", "."));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        // 指定方法
        if (mv != null && targetMethods != null && targetMethods.contains(name)) {
            System.out.println(">>>> targetMethod: " + name);
            if (generateNewBody(mv, name, access, descriptor)) return null;
            return new MyMethodVisitor(ASM8, mv, name, access, descriptor);
        }

        return mv;
    }


    /**
     * 清空方法体，修改返回值
     */
    private boolean generateNewBody(MethodVisitor mv, String targetMethod, int methodAccess, String methodDesc) {
        boolean result = false;
        switch (targetMethod) {
            case "j":
                // 重写 machineId 生成方法
                doMachineIdNew(mv, targetMethod, methodAccess, methodDesc);
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * 重写获取 machineId 方法
     * 只改一个 machineId 没用，还有 hostName、userName，还是多重加密，太费时费力
     * machineId 有一个生成方法
     * hostName 由 machineId base64 加密生成
     * userName 取的是系统当前用户名，【只改用户名】，可以获取新的试用许可，但为了稳妥起见，machineId 最好也一起改
     */
    private boolean doMachineIdNew(MethodVisitor mv, String targetMethod, int methodAccess, String methodDesc) {

        // 根据 methodDesc 区分重载方法，注意对象类型有“;”
        if (!methodDesc.equals("(II)Ljava/lang/String;")) return false;
        System.out.println(">>>> doMachineIdNew target methodDesc: " + methodDesc);

        // 方法参数和返回值类型
        Type t = Type.getType(methodDesc);
        Type[] argumentTypes = t.getArgumentTypes();
        Type returnType = t.getReturnType();

        // 计算 locals 和 stack 栈大小
        boolean isStaticMethod = ((methodAccess & Opcodes.ACC_STATIC) != 0);
        int localSize = isStaticMethod ? 0 : 1;
        for (Type argType : argumentTypes) {
            localSize += argType.getSize();
        }
        int stackSize = returnType.getSize();

        // 打印入参
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

        // 方法体
        mv.visitCode();
        // 目前 machineId 为 UUID
        mv.visitMethodInsn(INVOKESTATIC, "java/util/UUID", "randomUUID", "()Ljava/util/UUID;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 2);
        // 打印 UUID
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(stackSize, localSize);
        //mv.visitMaxs(1, 0);
        //mv.visitMaxs(2, 1);
        mv.visitEnd();

        return true;
    }
}
