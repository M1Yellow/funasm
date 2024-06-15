package com.doidea.core;

import com.doidea.core.transformers.IMyTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;


/**
 * transform 调度
 */
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
        // TODO transform 方法内的日志能在 idea.log 中打印
        //System.out.println(">>>> loading Class: " + className); // xxx/xxxx/xxx 格式
        String targetClassName = className.replace("/", "."); // targetClass 为 xxx.xxxx.xxx$xxx 格式
        List<IMyTransformer> transformers = this.transformerMap.get(targetClassName);
        if (null == transformers || transformers.isEmpty()) return classfileBuffer;
        // 命中目标类
        System.out.println(">>>> Target Class: " + className);
        // TODO may be null if the bootstrap loader 启动类加载器加载的核心类，loader 为 null。比如：java.lang.String、java.awt.Dialog
        if (null == loader) loader = ClassLoader.getSystemClassLoader().getParent();
        System.out.println(">>>> Target ClassLoader: " + loader.toString()); // toString() -> PathClassLoader；getName() -> null

        // 插件运行模式
        if (null != Launcher.propMap && !Launcher.propMap.isEmpty())
            System.out.println(">>>> Current MODE: " + Launcher.propMap.get("mode"));

        int order = 0; // 自定义 transform 执行顺序，暂未使用
        try {
            for (IMyTransformer transformer : transformers) {
                classfileBuffer = transformer.transform(loader, classBeingRedefined, protectionDomain, className, classfileBuffer, order++);
            }
        } catch (Throwable e) {
            System.err.println(">>>> Transform class error: " + e.getMessage());
            e.printStackTrace();
        }

        return classfileBuffer;
    }
}
