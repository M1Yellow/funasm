package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * BigInteger oddModPow 方法修改
 */
public class BigIntegerTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "java.math.BigInteger";
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, byte[] classBytes, int order) throws Exception {
        byte[] newByte = null;
        try {
            newByte = getAsmTreeBytes(loader, className, classBytes);
        } catch (Throwable e) {
            System.err.println(">>>> BigIntegerTransformer getAsmTreeBytes error: " + e.getMessage());
            e.printStackTrace();
        }
        return newByte;
    }

    /**
     * ASM Tree API 方式获取字节码
     */
    private byte[] getAsmTreeBytes(ClassLoader loader, String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode(ASM8);
        cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES); // 跟 COMPUTE_FRAMES 搭配
        for (MethodNode mn : cn.methods) {
            if ("oddModPow".equals(mn.name) && "(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;".equals(mn.desc)) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new VarInsnNode(ALOAD, 1));
                insnList.add(new VarInsnNode(ALOAD, 2));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "com/doidea/core/filters/BigIntegerFilter", "testFilter", "(Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;", false));
                insnList.add(new VarInsnNode(ASTORE, 3));
                insnList.add(new InsnNode(ACONST_NULL));
                insnList.add(new VarInsnNode(ALOAD, 3));
                LabelNode label0 = new LabelNode();
                insnList.add(new JumpInsnNode(IF_ACMPEQ, label0));
                insnList.add(new VarInsnNode(ALOAD, 3));
                insnList.add(new InsnNode(ARETURN));
                insnList.add(label0);

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);
            }
        }

        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        cn.accept(cw);
        return cw.toByteArray();
    }
}
