package com.doidea.core.method;

public interface DoAfter {
    /**
     * machineId 生成方法
     *
     * @param methodVisitor MethodVisitor 对象
     * @param methodAccess  方法访问权限
     * @param methodDesc    方法参数描述
     * @return 是否执行完成
     */
    boolean doAfterMachineId(Object methodVisitor, int methodAccess, String methodDesc);
}
