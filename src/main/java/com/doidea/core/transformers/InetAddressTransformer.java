package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * DNS 域名解析方法修改
 */
public class InetAddressTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "java.net.InetAddress";
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, byte[] classBytes, int order) throws Exception {
        byte[] newByte = null;
        try {
            newByte = getAsmTreeBytes(loader, className, classBytes);
        } catch (Throwable e) {
            System.err.println(">>>> InetAddressTransformer getAsmTreeBytes error: " + e.getMessage());
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
            if ("getAllByName".equals(mn.name) && "(Ljava/lang/String;Ljava/net/InetAddress;)[Ljava/net/InetAddress;".equals(mn.desc)) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "com/doidea/core/filters/DNSFilter", "testQuery", "(Ljava/lang/String;)Ljava/lang/String;", false));
                insnList.add(new InsnNode(POP));

                /*
                insnList.add(new InsnNode(ACONST_NULL)); // null
                insnList.add(new VarInsnNode(ALOAD, 0));
                LabelNode label0 = new LabelNode();
                insnList.add(new JumpInsnNode(IF_ACMPEQ, label0)); // 等于 null
                insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("jet" + "brains.com"));
                //insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));
                LabelNode label1 = new LabelNode();
                insnList.add(new JumpInsnNode(IFNE, label1)); // IFNE 不等于0跳转，0-false; 1-true
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("plugin.obroom.com"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));
                insnList.add(new JumpInsnNode(IFEQ, label0)); // IFEQ 等于0跳转，0-false; 1-true
                insnList.add(label1);
                insnList.add(new TypeInsnNode(NEW, "java/lang/UnknownHostException"));
                insnList.add(new InsnNode(DUP));
                insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/UnknownHostException", "<init>", "()V", false));
                insnList.add(new InsnNode(ATHROW));
                insnList.add(label0);
                */

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);
            } else if ("isReachable".equals(mn.name) && "(Ljava/net/NetworkInterface;II)Z".equals(mn.desc)) {
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "com/doidea/core/filters/DNSFilter", "testReachable", "(Ljava/net/InetAddress;)Ljava/lang/Object;", false));
                insnList.add(new VarInsnNode(ASTORE, 4));
                insnList.add(new InsnNode(ACONST_NULL));
                insnList.add(new VarInsnNode(ALOAD, 4));
                LabelNode label1 = new LabelNode();
                insnList.add(new JumpInsnNode(IF_ACMPEQ, label1));
                insnList.add(new InsnNode(ICONST_0));
                insnList.add(new InsnNode(IRETURN));
                insnList.add(label1);

                /*
                insnList.add(new InsnNode(ACONST_NULL)); // null
                insnList.add(new VarInsnNode(ALOAD, 0)); // 0-this, InetAddress 对象实例
                LabelNode label0 = new LabelNode();
                insnList.add(new JumpInsnNode(IF_ACMPEQ, label0)); // 等于 null
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/net/InetAddress", "getHostName", "()Ljava/lang/String;", false));
                insnList.add(new VarInsnNode(ASTORE, 4));
                insnList.add(new InsnNode(ACONST_NULL));
                insnList.add(new VarInsnNode(ALOAD, 4));
                insnList.add(new JumpInsnNode(IF_ACMPEQ, label0)); // 等于 null
                insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new VarInsnNode(ALOAD, 4));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                insnList.add(new VarInsnNode(ALOAD, 4));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("jet" + "brains.com"));
                //insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));
                LabelNode label1 = new LabelNode();
                insnList.add(new JumpInsnNode(IFNE, label1)); // IFNE 不等于0跳转，0-false; 1-true
                insnList.add(new VarInsnNode(ALOAD, 4));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("plugin.obroom.com"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));
                insnList.add(new JumpInsnNode(IFEQ, label0)); // IFEQ 等于0跳转，0-false; 1-true
                insnList.add(label1);
                insnList.add(new InsnNode(ICONST_0)); // 0-false
                insnList.add(new InsnNode(IRETURN));
                insnList.add(label0);
                */

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);
            }
        }

        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        cn.accept(cw);
        return cw.toByteArray();
    }
}
