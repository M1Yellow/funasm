package com.doidea.core.transformers;

import jdk.internal.org.objectweb.asm.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * obtainAnonTrial.action/validateKey.action 请求参数 machineId 修改
 * 跟修改用户名一起搭配，避免一个机器有无数个用户名
 */
public class MachineIdTransformer implements MyTransformer {


    @Override
    public String getTargetClassName() {
        return "com.jet" + "brains.t.t.p";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return getAsmBytes(className, classBytes);
    }

    private byte[] getAsmBytes(String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算max stacks、max locals和stack map frame的具体内容
        ClassVisitor cv = new MachineIdClassVisitor(ASM8, cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    /**
     * 重写类访问器
     */
    private static class MachineIdClassVisitor extends ClassVisitor {
        public MachineIdClassVisitor(int api, ClassWriter classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (null != mv && "j".equals(name) && "(II)Ljava/lang/String;".equals(descriptor)) {
                System.out.println(">>>> Target method name: " + name);
                System.out.println(">>>> Target method descriptor: " + descriptor);
                rewriteMachineIdVisitor(ASM8, mv, name, access, descriptor);
            }
            return mv;
        }

        private void rewriteMachineIdVisitor(int api, MethodVisitor mv, String name, int access, String descriptor) {
            // 方法参数和返回值类型
            Type t = Type.getType(descriptor);
            Type[] argumentTypes = t.getArgumentTypes();
            Type returnType = t.getReturnType();

            // 计算 locals 和 stack 栈大小
            boolean isStaticMethod = ((access & Opcodes.ACC_STATIC) != 0);
            int localSize = isStaticMethod ? 0 : 1;
            for (Type argType : argumentTypes) {
                localSize += argType.getSize();
            }
            int stackSize = returnType.getSize();

            // 打印入参
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

            // 方法体
            mv.visitCode();
            // 目前 machineId 为 UUID
            mv.visitMethodInsn(INVOKESTATIC, "java/util/UUID", "randomUUID", "()Ljava/util/UUID;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 2);
            // 打印 UUID
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARETURN);

            mv.visitMaxs(stackSize, localSize);
            //mv.visitMaxs(1, 0);
            //mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
    }

    /**
     * 重写方法访问器
     */
    private static class MachineIdVisitor extends MethodVisitor {

        private final String name;
        private final int access;
        private final String descriptor;

        public MachineIdVisitor(int api, MethodVisitor mv, String name, int access, String descriptor) {
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
            // 方法原有逻辑
            super.visitCode();
        }
    }
}
