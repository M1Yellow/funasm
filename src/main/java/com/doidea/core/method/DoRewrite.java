package com.doidea.core.method;

import jdk.internal.org.objectweb.asm.MethodVisitor;

public interface DoRewrite {
    /**
     * 重写 machineId 生成方法
     * @param mv MethodVisitor 对象
     * @param methodAccess 方法访问权限
     * @param methodDesc 方法参数描述
     * @return 是否执行完成
     */
    boolean doRewriteMachineId(MethodVisitor mv, int methodAccess, String methodDesc);
}
