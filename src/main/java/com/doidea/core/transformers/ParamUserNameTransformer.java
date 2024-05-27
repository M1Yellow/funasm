package com.doidea.core.transformers;

import jdk.internal.org.objectweb.asm.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * obtainAnonTrial.action/validateKey.action 请求参数 userName 修改
 * 原本方法获取的是计算机当前用户名称，修改为自定义名称
 * 测试修改用户名后，即可再次获取到 30 的试用时间
 */
public class ParamUserNameTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "com.jet" + "brains.t.t.ji";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return getAsmBytes(className, classBytes);
    }

    private byte[] getAsmBytes(String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算max stacks、max locals和stack map frame的具体内容
        ClassVisitor cv = new UserNameClassVisitor(ASM8, cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    /**
     * 重写类访问器
     */
    private static class UserNameClassVisitor extends ClassVisitor {
        public UserNameClassVisitor(int api, ClassWriter classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (null != mv && "j".equals(name) && "([BJ)[B".equals(descriptor)) {
                System.out.println(">>>> Target method name: " + name);
                System.out.println(">>>> Target method descriptor: " + descriptor);
                return new ChangeNameBytesVisitor(ASM8, mv, name, access, descriptor);
            }
            return mv;
        }
    }

    /**
     * 重写方法访问器
     */
    private static class ChangeNameBytesVisitor extends MethodVisitor {

        private final String name;
        private final int access;
        private final String descriptor;

        public ChangeNameBytesVisitor(int api, MethodVisitor mv, String name, int access, String descriptor) {
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
            // 打印参数
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(LLOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);

            // 修改入参
            // 每月 14 号变换一次
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            Label label1 = new Label();
            mv.visitLabel(label1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(LLOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
            Label label2 = new Label();
            mv.visitLabel(label2);
            mv.visitMethodInsn(INVOKESTATIC, "java/time/LocalDateTime", "now", "()Ljava/time/LocalDateTime;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label label3 = new Label();
            mv.visitLabel(label3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/time/LocalDateTime", "getYear", "()I", false);
            mv.visitVarInsn(ISTORE, 4);
            Label label4 = new Label();
            mv.visitLabel(label4);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/time/LocalDateTime", "getMonthValue", "()I", false);
            mv.visitVarInsn(ISTORE, 5);
            Label label5 = new Label();
            mv.visitLabel(label5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/time/LocalDateTime", "getDayOfMonth", "()I", false);
            mv.visitVarInsn(ISTORE, 6);
            Label label6 = new Label();
            mv.visitLabel(label6);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(II)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), new Object[]{"MAC-\u0001\u000114"});
            mv.visitVarInsn(ASTORE, 7);
            Label label7 = new Label();
            mv.visitLabel(label7);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitIntInsn(BIPUSH, 14);
            Label label8 = new Label();
            mv.visitJumpInsn(IF_ICMPLT, label8);
            Label label9 = new Label();
            mv.visitLabel(label9);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInvokeDynamicInsn("makeConcatWithConstants", "(II)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), new Object[]{"MAC-\u0001\u000128"});
            mv.visitVarInsn(ASTORE, 7);
            mv.visitLabel(label8);
            mv.visitFrame(Opcodes.F_FULL, 7, new Object[]{"[B", Opcodes.LONG, "java/time/LocalDateTime", Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER, "java/lang/String"}, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 7);
            mv.visitFieldInsn(GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);
            mv.visitVarInsn(ASTORE, 0);
            Label label10 = new Label();
            mv.visitLabel(label10);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            // 方法原有逻辑
            super.visitCode();
        }
    }
}
