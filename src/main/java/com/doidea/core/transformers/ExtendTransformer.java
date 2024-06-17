package com.doidea.core.transformers;

import java.lang.instrument.Instrumentation;

/**
 * 扩展处理 ASM transform 没有转换的类
 */
public class ExtendTransformer {

    /**
     * 额外处理 transform
     */
    public void doExtendTransform(Instrumentation inst) throws Exception {
        redoClassNameTransformer(inst);
        //redoThrowableTransformer(inst); // 效果不对，没有去掉异常堆栈中的自定义类信息

        /*
        String[] retransformClasses = {
                "java.lang.Throwable",
                "java.lang.Class"
        };
        for (String className : retransformClasses) {
            Class<?> clazz = Class.forName(className); // xxx.xxxx.xxx 格式
            boolean isModifiable = inst.isModifiableClass(clazz);
            System.out.println(">>>> " + className + " isModifiable: " + isModifiable);
            if (isModifiable) {
                inst.retransformClasses(clazz); // TODO 没有生效
            }
        }
        */
    }

    /**
     * ASM transform 没有加载 java.lang.Class <br>
     * 需要手动重新 transform
     */
    public void redoClassNameTransformer(Instrumentation inst) throws Exception {
        String className = "java/lang/Class";
        String targetClassName = className.replace("/", ".");
        System.out.println(">>>> Target Class: " + className);
        Class<?> aClass = Class.forName(targetClassName);
        ClassLoader classLoader = aClass.getClassLoader();
        if (null == classLoader)
            classLoader = ClassLoader.getSystemClassLoader().getParent(); // jdk.internal.loader.ClassLoaders$PlatformClassLoader
        new ClassNameTransformer().transform(inst, classLoader, targetClassName, null, 0);
    }

    /**
     * ASM transform 没有加载 java.lang.Throwable <br>
     * 需要手动重新 transform
     */
    public void redoThrowableTransformer(Instrumentation inst) throws Exception {
        String className = "java/lang/Throwable";
        String targetClassName = className.replace("/", ".");
        System.out.println(">>>> Target Class: " + className);
        Class<?> aClass = Class.forName(targetClassName);
        ClassLoader classLoader = aClass.getClassLoader();
        if (null == classLoader)
            classLoader = ClassLoader.getSystemClassLoader().getParent(); // jdk.internal.loader.ClassLoaders$PlatformClassLoader
        new ThrowableTransformer().transform(inst, classLoader, targetClassName, null, 0);
    }
}
