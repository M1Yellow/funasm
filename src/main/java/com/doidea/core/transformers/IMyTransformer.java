package com.doidea.core.transformers;

import java.security.ProtectionDomain;

public interface IMyTransformer {

    String getTargetClassName();

    default byte[] transform(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes, int order) throws Exception {
        return transform(className, classBytes, order);
    }

    default byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return classBytes;
    }
}
