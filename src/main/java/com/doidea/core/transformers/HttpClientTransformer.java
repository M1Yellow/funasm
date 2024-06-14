package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * HttpClient 方法修改
 */
public class HttpClientTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "sun.net.www.http.HttpClient";
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
            if ("openServer".equals(mn.name) && "()V".equals(mn.desc)) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 0)); // 0-this 对象本身
                insnList.add(new FieldInsnNode(GETFIELD, "sun/net/www/http/HttpClient", "url", "Ljava/net/URL;"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/net/URL", "toString", "()Ljava/lang/String;", false));
                insnList.add(new VarInsnNode(ASTORE, 1));
                insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new VarInsnNode(ALOAD, 1));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                insnList.add(new VarInsnNode(ALOAD, 1));
                insnList.add(new LdcInsnNode("validateKey.action"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));
                LabelNode label1 = new LabelNode();
                insnList.add(new JumpInsnNode(IFEQ, label1)); // IFEQ 等于0跳转，0-false; 1-true
                insnList.add(new TypeInsnNode(NEW, "java/net/SocketTimeoutException"));
                insnList.add(new InsnNode(DUP));
                insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false));
                insnList.add(new InsnNode(ATHROW));
                insnList.add(label1);

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);
            }
        }

        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        cn.accept(cw);
        return cw.toByteArray();
    }
}
