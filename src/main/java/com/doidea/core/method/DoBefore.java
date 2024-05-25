package com.doidea.core.method;

import jdk.internal.org.objectweb.asm.MethodVisitor;

/**
 * 在目标方法执行之前处理
 */
public interface DoBefore {
    /**
     * JDialog SetTitle
     * @param mv MethodVisitor 对象
     * @param methodAccess 方法访问权限
     * @param methodDesc 方法参数描述
     * @return 是否执行完成
     */
    boolean doBeforeJDialogSetTitle(MethodVisitor mv, int methodAccess, String methodDesc);

    /**
     * validateKey.action 许可证密钥验证请求
     */
    boolean doBeforeValidateKey(MethodVisitor mv, int methodAccess, String methodDesc);

    /**
     * base64 encode 前一个关键处理方法
     */
    boolean doBeforeBase64Fun(MethodVisitor mv, int methodAccess, String methodDesc);

    /**
     * machineId 生成方法
     */
    boolean doBeforeMachineId(MethodVisitor mv, int methodAccess, String methodDesc);
}
