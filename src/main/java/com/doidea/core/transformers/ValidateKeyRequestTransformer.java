package com.doidea.core.transformers;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * validateKey.action 验证许可证密钥请求方法修改
 */
public class ValidateKeyRequestTransformer implements MyTransformer {


    @Override
    public String getTargetClassName() {
        return "com.jet" + "brains.ls.requests.ValidateKeyRequest";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return getAsmBytes(className, classBytes);
    }

    private byte[] getAsmBytes(String className, byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算max stacks、max locals和stack map frame的具体内容
        ClassVisitor cv = new ValidateKeyReqClassVisitor(ASM8, cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    /**
     * 重写类访问器
     */
    private static class ValidateKeyReqClassVisitor extends ClassVisitor {
        public ValidateKeyReqClassVisitor(int api, ClassWriter classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (null != mv && "<init>".equals(name) && "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IJ)V".equals(descriptor)) {
                System.out.println(">>>> Target method name: " + name);
                System.out.println(">>>> Target method descriptor: " + descriptor);
                return new ReqConstructsVisitor(ASM8, mv, name, access, descriptor);
            }
            return mv;
        }
    }

    /**
     * 重写方法访问器
     */
    private static class ReqConstructsVisitor extends MethodVisitor {

        private final String name;
        private final int access;
        private final String descriptor;

        public ReqConstructsVisitor(int api, MethodVisitor mv, String name, int access, String descriptor) {
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
            // 抛出异常直接终止请求
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("ValidateKeyRequest abort.");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);

            // 方法原有逻辑
            super.visitCode();
        }
    }
}
