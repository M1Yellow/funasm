package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * 消息弹窗方法修改
 */
public class MessagesShowDialogTransformer implements IMyTransformer {
    
    @Override
    public String getTargetClassName() {
        return "com." + "intel" + "lij" + ".openapi.ui.Messages";
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, byte[] classBytes, int order) throws Exception {
        byte[] newByte = null;
        try {
            newByte = getAsmTreeBytes(loader, className, classBytes);
        } catch (Throwable e) {
            System.err.println(">>>> MessagesShowDialogTransformer getAsmTreeBytes error: " + e.getMessage());
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
            // .method public static showDialog(Ljava/awt/Component;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;ILjavax/swing/Icon;)I
            if ("showDialog".equals(mn.name) && "(Ljava/awt/Component;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;ILjavax/swing/Icon;)I".equals(mn.desc)) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 2)); // 非静态方法，0-this；1-第一个参数；依次类推。静态方法，0-第一个参数；1-第二个；依次类推
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("trial has expired"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));
                LabelNode label1 = new LabelNode();
                insnList.add(new JumpInsnNode(IFNE, label1)); // IFNE 不等于0跳转，0-false; 1-true
                insnList.add(new VarInsnNode(ALOAD, 2));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("试用已到期"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));
                LabelNode label2 = new LabelNode();
                insnList.add(new JumpInsnNode(IFEQ, label2)); // IFEQ 等于0跳转，0-false; 1-true
                insnList.add(label1);
                insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new VarInsnNode(ALOAD, 2));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                insnList.add(new InsnNode(ICONST_0));
                insnList.add(new InsnNode(IRETURN));
                insnList.add(label2);

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);
            }
        }

        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        cn.accept(cw);
        return cw.toByteArray();
    }
}
