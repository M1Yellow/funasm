package com.doidea.core.transformers;

import jdk.internal.org.objectweb.asm.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Licenses（许可证）弹窗设置标题方法修改
 */
public class JDialogSetTitleTransformer implements MyTransformer {


    @Override
    public String getTargetClassName() {
        return "com." + "intel" + "lij" + ".openapi.ui.DialogWrapper";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return getAsmBytes(className, classBytes);
    }

    private byte[] getAsmBytes(String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算max stacks、max locals和stack map frame的具体内容
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
            // System.out.println(title);
            Label label1 = new Label();
            mv.visitLabel(label1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            /*
            // title = title.trim(); // 尽可能不改动原 title
            if (title.trim().equalsIgnoreCase("Licenses") || title.trim().equalsIgnoreCase("许可证")) {
                throw new RuntimeException("Licenses dialog abort.");
            }
            */
            Label label2 = new Label();
            mv.visitLabel(label2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
            mv.visitLdcInsn("Licenses");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            Label label3 = new Label();
            mv.visitJumpInsn(IFNE, label3); // IFNE 不等于0跳转，0-false; 1-true
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false);
            mv.visitLdcInsn("许可证");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);
            Label label4 = new Label();
            mv.visitJumpInsn(IFEQ, label4); // IFEQ 等于0跳转，0-false; 1-true
            mv.visitLabel(label3);
            // 配置了 ClassWriter.COMPUTE_FRAMES 自动计算
            //mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            // TODO 异常信息建议不写，异常信息可能会上报。后续再研究 hook 异常堆栈打印方法，屏蔽添加的手动异常
            //mv.visitLdcInsn("Licenses dialog abort.");
            //mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(label4);

            // 方法原有逻辑
            super.visitCode();
        }
    }
}
