package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Class.forName 方法修改，隐藏自定义类
 */
public class ClassNameTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "java.lang.Class";
    }

    @Override
    public byte[] transform(Instrumentation inst, ClassLoader loader, String className, byte[] classBytes, int order) throws Exception {
        byte[] newByte = null;
        try {
            newByte = getAsmTreeBytes(inst, loader, className, classBytes);
        } catch (Throwable e) {
            System.err.println(">>>> ClassNameTransformer getAsmTreeBytes error: " + e.getMessage());
            e.printStackTrace();
        }
        return newByte;
    }

    /**
     * ASM Tree API 方式获取字节码
     */
    private byte[] getAsmTreeBytes(Instrumentation inst, ClassLoader loader, String className, byte[] classBytes) throws Exception {
        ClassReader cr;
        if (null != classBytes) cr = new ClassReader(classBytes);
        else cr = new ClassReader(className);
        ClassNode cn = new ClassNode(ASM8);
        cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES); // 跟 COMPUTE_FRAMES 搭配
        for (MethodNode mn : cn.methods) {
            if ("forName".equals(mn.name) && mn.desc.startsWith("(Ljava/lang/String;")) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                /*
                insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new LdcInsnNode(">>>> Rewrite Class.forName >>>>"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                */
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "com/doidea/core/filters/ClassNameFilter", "testClass", "(Ljava/lang/String;)V", false));

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);
            }
        }

        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        cn.accept(cw);
        byte[] newBytes = cw.toByteArray();

        // 重写 class，classBytes 为 null，说明不是从 transform 进来，需要替换已加载的类
        if (null == classBytes && null != className) {
            Class<?> targetClass = Class.forName(className.replace("/", "."));
            if (inst.isRedefineClassesSupported() && inst.isModifiableClass(targetClass)) {
                System.out.println(">>>> redefineClasses: " + className);
                inst.redefineClasses(new ClassDefinition(targetClass, newBytes));
            }
        }

        return newBytes;
    }
}
