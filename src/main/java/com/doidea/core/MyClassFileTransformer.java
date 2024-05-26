package com.doidea.core;

import com.doidea.core.bo.TargetMethod;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM8;

/**
 * 类文件转换处理
 */
public class MyClassFileTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        //System.out.println(">>>> loading Class: " + className); // xxx/xxxx/xxx 格式
        String targetClassName = className.replace("/", "."); // targetClass 为 xxx.xxxx.xxx$xxx 格式
        List<TargetMethod> targetMethods = Launcher.targetClassMethodMap.get(targetClassName);
        if (targetMethods == null || targetMethods.isEmpty())
            return classfileBuffer;

        // 命中目标类和方法
        System.out.println(">>>> Target Class: " + targetClassName);
        try {
            return getAsmBytes(loader, targetClassName, classfileBuffer);
        } catch (Throwable e) {
            System.err.println(">>>> getAsmBytes Error: " + e.getMessage());
            e.printStackTrace();
        }

        return classfileBuffer;
    }

    /**
     * 获取 ASM 修改后的字节码
     *
     * @param loader          类加载器
     * @param targetClassName 类名
     * @param classfileBuffer 字节码
     * @return ASM 修改后的字节码
     */
    private byte[] getAsmBytes(ClassLoader loader, String targetClassName, byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算max stacks、max locals和stack map frame的具体内容
        ClassVisitor cv = new MyClassVisitor(ASM8, cw, targetClassName);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }
}
