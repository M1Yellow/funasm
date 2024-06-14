package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Licenses（许可证）弹窗设置标题方法修改
 */
public class JDialogSetTitleTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "com." + "intel" + "lij" + ".openapi.ui.DialogWrapper"; // Dialog 的封装类
        //return "java.awt.Dialog"; // 直接修改 Dialog 本尊，更具有通用性。ASM Tree API 会出现循环依赖问题：java.lang.ClassCircularityError: java/awt/Dialog
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, byte[] classBytes, int order) throws Exception {
        byte[] newByte;
        try {
            // 先使用 tree API 修改字节码
            newByte = getAsmTreeBytes(loader, className, classBytes);
        } catch (Throwable e) {
            System.err.println(">>>> JDialogSetTitleTransformer getAsmTreeBytes error: " + e.getMessage());
            e.printStackTrace();
            // tree API 修改失败，再用 core API
            newByte = getAsmBytes(className, classBytes);
        }
        return newByte;
    }

    /**
     * ASM Tree API 方式获取字节码
     * 不用额外创建 ClassVisitor、MethodVisitor，操作简洁，性能稍降一点
     */
    private byte[] getAsmTreeBytes(ClassLoader loader, String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode(ASM8);
        //cr.accept(cn, 0); // 0-默认
        cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES); // 跟 COMPUTE_FRAMES 搭配
        for (MethodNode mn : cn.methods) {
            // .method public setTitle(Ljava/lang/String;)V
            if ("setTitle".equals(mn.name) && "(Ljava/lang/String;)V".equals(mn.desc)) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 1)); // 非静态方法，0-this；1-第一个参数；依次类推。静态方法，0-第一个参数；1-第二个；依次类推
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("Licenses"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false));
                LabelNode label0 = new LabelNode();
                insnList.add(new JumpInsnNode(IFNE, label0)); // IFNE 不等于0跳转，0-false; 1-true
                insnList.add(new VarInsnNode(ALOAD, 1));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false));
                insnList.add(new LdcInsnNode("许可证"));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false));
                LabelNode label1 = new LabelNode();
                insnList.add(new JumpInsnNode(IFEQ, label1)); // IFEQ 等于0跳转，0-false; 1-true
                insnList.add(label0);
                insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new VarInsnNode(ALOAD, 1));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                insnList.add(new TypeInsnNode(NEW, "java/lang/RuntimeException"));
                insnList.add(new InsnNode(DUP));
                insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false));
                insnList.add(new InsnNode(ATHROW));
                insnList.add(label1);

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);
            }
        }

        //ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        // TODO MyClassWriter 重写了 ClassWriter.getCommonSuperClass()，避免 ClassNotFoundException TypeNotPresentException
        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        //ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }

    /**
     * ASM Core API 方式获取字节码
     * 需要创建 ClassVisitor、MethodVisitor，代码较多，性能效率高
     */
    private byte[] getAsmBytes(String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        ClassVisitor cv = new DialogWrapperClassVisitor(ASM8, cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    /**
     * 重写类访问器
     */
    private static class DialogWrapperClassVisitor extends ClassVisitor {
        public DialogWrapperClassVisitor(int api, ClassWriter classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (null != mv && "setTitle".equals(name) && "(Ljava/lang/String;)V".equals(descriptor)) {
                System.out.println(">>>> Target method name: " + name);
                System.out.println(">>>> Target method descriptor: " + descriptor);
                return new SetTitleVisitor(ASM8, mv, name, access, descriptor);
            }
            return mv;
        }
    }

    /**
     * 重写方法访问器
     */
    private static class SetTitleVisitor extends MethodVisitor {

        private final String name;
        private final int access;
        private final String descriptor;

        public SetTitleVisitor(int api, MethodVisitor mv, String name, int access, String descriptor) {
            super(api, mv);
            this.name = name;
            this.access = access;
            this.descriptor = descriptor;
        }

        /**
         * 原有方法执行之前处理
         */
        @Override
        public void visitCode() {
            /*
            // title = title.trim(); // 尽可能不改动原 title
            if (title.trim().equalsIgnoreCase("Licenses") || title.trim().equalsIgnoreCase("许可证")) {
                System.out.println(title);
                throw new RuntimeException();
            }
            */
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
            mv.visitLdcInsn("Licenses");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            Label label0 = new Label();
            mv.visitJumpInsn(IFNE, label0); // IFNE 不等于0跳转，0-false; 1-true
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
            mv.visitLdcInsn("许可证");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            Label label1 = new Label();
            mv.visitJumpInsn(IFEQ, label1); // IFEQ 等于0跳转，0-false; 1-true
            mv.visitLabel(label0);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(label1);

            // 方法原有逻辑
            super.visitCode();
        }
    }
}
