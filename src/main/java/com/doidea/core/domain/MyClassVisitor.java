package com.doidea.core.domain;

import com.doidea.core.Launcher;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import java.util.List;

import static jdk.internal.org.objectweb.asm.Opcodes.ARETURN;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM8;

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
        boolean result;
        switch (targetMethod) {
            case "n":
                doMachineId(mv, targetMethod, methodAccess, methodDesc);
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
     */
    private void doMachineId(MethodVisitor mv, String targetMethod, int methodAccess, String methodDesc) {
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

        // 方法体
        mv.visitCode();
        // 重写
        mv.visitLdcInsn("v53d4b07-g5y7-3fi9-vs34-b5t8cd21s7f4");
        mv.visitInsn(ARETURN);

        mv.visitMaxs(stackSize, localSize);
        //mv.visitMaxs(1, 0);
        //mv.visitMaxs(2, 1);
        mv.visitEnd();
    }
}
