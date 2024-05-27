package com.doidea.core.transformers;

/**
 * obtainAnonTrial.action/validateKey.action 请求参数 hostName 修改
 * 原方法是对 machineId 加密一次，再进行 base64 加密返回，所以目前只要改了 machineId，就暂时不用管这个 hostName
 */
public class ParamHostNameTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return IMyTransformer.super.transform(className, classBytes, order);
    }
}
