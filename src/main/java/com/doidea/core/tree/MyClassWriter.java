package com.doidea.core.tree;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;

/**
 * Tree API 需要重写 ClassWriter getCommonSuperClass() 方法，避免内部类或其他引用类找不到，导致 ClassNotFoundException
 * https://github.com/alibaba/bytekit/bytekit-core/src/main/java/com/alibaba/bytekit/asm/ClassLoaderAwareClassWriter.java
 */
public class MyClassWriter extends ClassWriter {

    private final ClassLoader classLoader;

    public MyClassWriter(int flags, ClassLoader loader) {
        this(null, flags, loader);
    }

    public MyClassWriter(ClassReader classReader, int flags, ClassLoader loader) {
        super(classReader, flags);
        this.classLoader = loader;
    }

    /**
     * 注意，为了自动计算帧的大小，有时必须计算两个类共同的父类。
     * 缺省情况下，ClassWriter将会在getCommonSuperClass方法中计算这些，通过在加载这两个类进入虚拟机时，使用反射API来计算。
     * 但是，如果你将要生成的几个类相互之间引用，这将会带来问题，因为引用的类可能还不存在。
     * 在这种情况下，你可以重写getCommonSuperClass方法来解决这个问题。
     *
     * 通过重写 getCommonSuperClass() 方法，更正获取ClassLoader的方式，改成使用指定ClassLoader的方式进行。
     * 规避了原有代码采用Object.class.getClassLoader()的方式
     *
     * ClassWriter.COMPUTE_FRAMES 自动计算帧的大小，有时必须计算两个类共同的父类
     * ClassWriter.getCommonSuperClass 内部类或引用了其他还没加载的类报错 ClassNotFoundException TypeNotPresentException
     * 重写指定 classLoader，从 ClassFileTransformer 的 transform 方法传递进来
     */
    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        if (classLoader == null) {
            return super.getCommonSuperClass(type1, type2);
        }

        // TODO 下述代码为 getCommonSuperClass 的源码，后续需留意更新同步
        Class<?> class1;
        try {
            class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException(type1, e);
        }
        Class<?> class2;
        try {
            class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException(type2, e);
        }
        if (class1.isAssignableFrom(class2)) {
            return type1;
        }
        if (class2.isAssignableFrom(class1)) {
            return type2;
        }
        if (class1.isInterface() || class2.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                class1 = class1.getSuperclass();
            } while (!class1.isAssignableFrom(class2));
            return class1.getName().replace('.', '/');
        }
    }
}
