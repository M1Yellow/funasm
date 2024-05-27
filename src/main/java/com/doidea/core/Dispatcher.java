package com.doidea.core;

import com.doidea.core.transformers.IMyTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

public class Dispatcher implements ClassFileTransformer {
    /**
     * 目标类名集合
     */
    private final Set<String> classSet = new TreeSet<>();

    /**
     * 目标类文件转换器
     */
    private final Map<String, List<IMyTransformer>> transformerMap = new HashMap<>();


    public void addTransformer(IMyTransformer transformer) {
        if (null == transformer) return;
        synchronized (this) {
            String className = transformer.getTargetClassName().replace('/', '.');
            this.classSet.add(className);
            List<IMyTransformer> transformers = this.transformerMap.computeIfAbsent(className, k -> new ArrayList<>());
            transformers.add(transformer);
        }
    }

    public void addTransformers(List<IMyTransformer> transformers) {
        if (null == transformers) return;
        for (IMyTransformer transformer : transformers)
            addTransformer(transformer);
    }

    public void addTransformers(IMyTransformer[] transformers) {
        if (null == transformers) return;
        addTransformers(Arrays.asList(transformers));
    }

    public Set<String> getTargetClassNames() {
        return this.classSet;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (null == className || className.isBlank()) return classfileBuffer;
        //System.out.println(">>>> loading Class: " + className); // xxx/xxxx/xxx 格式
        String targetClassName = className.replace("/", "."); // targetClass 为 xxx.xxxx.xxx$xxx 格式
        List<IMyTransformer> transformers = this.transformerMap.get(targetClassName);
        if (null == transformers || transformers.isEmpty()) return classfileBuffer;
        // 命中目标类
        System.out.println(">>>> Target Class: " + targetClassName);

        int order = 0;
        try {
            for (IMyTransformer transformer : transformers) {
                classfileBuffer = transformer.transform(loader, classBeingRedefined, protectionDomain, targetClassName, classfileBuffer, order++);
            }
        } catch (Throwable e) {
            System.err.println(">>>> Transform class error: " + e.getMessage());
            //e.printStackTrace();
        }

        return classfileBuffer;
    }
}
