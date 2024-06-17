package com.doidea.core;

import com.doidea.core.transformers.ExtendTransformer;
import com.doidea.core.transformers.IMyTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.*;


/**
 * transform 调度
 */
public class Dispatcher implements ClassFileTransformer {

    /**
     * 需要操作的 Instrumentation
     */
    private final Instrumentation inst;

    /**
     * 目标类名集合
     */
    private final Set<String> classSet = new TreeSet<>();

    /**
     * 目标类文件转换器
     */
    private final Map<String, List<IMyTransformer>> transformerMap = new HashMap<>();


    public Dispatcher(Instrumentation inst) {
        this.inst = inst;
    }

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

    /**
     * 注意！transformer 类中的日志可能有时候不打印，导致以为执行！但具体执行的 filters 类又能打印日志！以 filters 类日志为准
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (null == className || className.isBlank()) return classfileBuffer;
        try {
            // TODO transform 方法内的日志可以在 idea.log 中打印（用 everything 搜索这个日志文件）
            //System.out.println(">>>> Transform Class: " + className); // xxx/xxxx/xxx 格式
            String targetClassName = className.replace("/", "."); // targetClass 为 xxx.xxxx.xxx$xxx 格式
            List<IMyTransformer> transformers = this.transformerMap.get(targetClassName);
            if (null == transformers || transformers.isEmpty()) return classfileBuffer;

            // 命中目标类
            System.out.println(">>>> Target Class: " + className);
            System.out.println(">>>> Original ClassLoader: " + loader);
            // TODO may be null if the bootstrap loader 启动类加载器加载的核心类，loader 为 null。比如：java.lang.String、java.awt.Dialog
            if (null == loader) loader = ClassLoader.getSystemClassLoader().getParent();
            // ClassLoader.getSystemClassLoader().getParent() -> jdk.internal.loader.ClassLoaders$PlatformClassLoader
            System.out.println(">>>> Target ClassLoader: " + loader.toString()); // toString() -> PathClassLoader；getName() -> null

            // 插件运行模式
            if (null != Launcher.propMap && !Launcher.propMap.isEmpty())
                System.out.println(">>>> Current MODE: " + Launcher.propMap.get("mode"));

            int order = 0; // 自定义 transform 执行顺序，暂未使用
            for (IMyTransformer transformer : transformers) {
                classfileBuffer = transformer.transform(loader, classBeingRedefined, protectionDomain, className, classfileBuffer, order++);
                // TODO 额外处理 ASM 在 transform 阶段没有处理的类转换，依附在一个能处理的类后面执行
                if (transformer.getTargetClassName().equals("sun.management.VMManagementImpl")) {
                    System.out.println(">>>> ASM doExtendTransform >>>>");
                    for (Class<?> c : inst.getAllLoadedClasses()) { // 截至 VMManagementImpl 类加载时，IDEA 应用层面的类还没加载
                        String name = c.getName().replace("/", ".");
                        if (classSet.contains(name)) System.out.println(">>>> Instrumentation loaded class: " + name);
                    }
                    new ExtendTransformer().doExtendTransform(inst); // 【验证有效】
                }
            }
        } catch (Throwable e) {
            System.err.println(">>>> Transform class error: " + e.getMessage());
            e.printStackTrace();
        }

        return classfileBuffer;
    }
}
