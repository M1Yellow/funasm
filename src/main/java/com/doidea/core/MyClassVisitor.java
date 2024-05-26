package com.doidea.core;

import com.doidea.core.bo.TargetMethod;
import com.doidea.core.method.impl.DoRewriteDispatcher;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import java.util.List;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM8;

/**
 * 自定义类访问处理器
 */
public class MyClassVisitor extends ClassVisitor {

    private final String targetClassName;
    private final List<TargetMethod> targetMethods;

    public MyClassVisitor(int api, ClassVisitor classVisitor, String targetClassName) {
        super(api, classVisitor);
        this.targetClassName = targetClassName;
        this.targetMethods = Launcher.targetClassMethodMap.get(targetClassName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        // 通过类名、方法名、方法参数类型，匹配唯一目标方法
        TargetMethod targetMethod = targetMethods.stream().filter(method ->
                method.getTargetClassName().equals(targetClassName)
                        && method.getTargetMethodName().equals(name)
                        && method.getTargetMethodParamType().toString().equals(descriptor)).findAny().orElse(null);

        if (mv != null && targetMethod != null) {
            System.out.println(">>>> targetMethodName: " + name);
            System.out.println(">>>> doMachineIdNew target methodDesc: " + descriptor);
            // TODO 当前类可能有同名的方法，如果执行重写逻辑成功，就不再执行 before、after 逻辑
            if (generateNewBody(mv, name, access, descriptor)) return null;
            return new MyMethodVisitor(ASM8, mv, name, access, descriptor);
        }

        return mv;
    }


    /**
     * 清空方法体，修改返回值
     */
    private boolean generateNewBody(MethodVisitor mv, String targetMethodName, int methodAccess, String methodDesc) {
        boolean result = false;
        switch (targetMethodName) {
            case "j":
                // 重写 machineId 生成方法
                result = DoRewriteDispatcher.INSTANCE.doRewriteMachineId(mv, methodAccess, methodDesc);
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

}
