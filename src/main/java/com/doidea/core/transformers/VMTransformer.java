package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * JVM 获取启动参数方法修改，隐藏 javaagent 插件参数
 */
public class VMTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "sun.management.VMManagementImpl";
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, byte[] classBytes, int order) throws Exception {
        byte[] newByte = null;
        try {
            newByte = getAsmTreeBytes(loader, className, classBytes);
        } catch (Throwable e) {
            System.err.println(">>>> VMTransformer getAsmTreeBytes error: " + e.getMessage());
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
            if ("getVmArguments".equals(mn.name) && "()Ljava/util/List;".equals(mn.desc)) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 0)); // GETFIELD 操作
                insnList.add(new VarInsnNode(ALOAD, 0)); // PUTFIELD 操作
                insnList.add(new FieldInsnNode(GETFIELD, "sun/management/VMManagementImpl", "vmArgs", "Ljava/util/List;"));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "com/doidea/core/filters/VmArgumentFilter", "testArgs", "(Ljava/util/List;)Ljava/util/List;", false));
                insnList.add(new FieldInsnNode(PUTFIELD, "sun/management/VMManagementImpl", "vmArgs", "Ljava/util/List;"));

                // 在方法返回前加入指令集
                for (AbstractInsnNode in : mn.instructions) {
                    if (AbstractInsnNode.INSN == in.getType() && ARETURN == in.getOpcode())
                        mn.instructions.insert(in.getPrevious().getPrevious(), insnList); // ARETURN 前两个节点，可以用 ASMPlugin 查看
                }
            }
        }

        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        cn.accept(cw);
        return cw.toByteArray();
    }
}
