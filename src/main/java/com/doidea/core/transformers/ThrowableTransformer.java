package com.doidea.core.transformers;

import com.doidea.core.tree.MyClassWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Throwable setStackTrace 方法修改，隐藏自定义类
 */
public class ThrowableTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "java.lang.Throwable";
    }

    @Override
    public byte[] transform(Instrumentation inst, ClassLoader loader, String className, byte[] classBytes, int order) throws Exception {
        byte[] newByte = null;
        try {
            newByte = getAsmTreeBytes(inst, loader, className, classBytes);
            //newByte = getAsmBytes(loader, className, classBytes);
        } catch (Throwable e) {
            System.err.println(">>>> ThrowableTransformer getAsmTreeBytes error: " + e.getMessage());
            e.printStackTrace();
        }
        return newByte;
    }

    /**
     * ASM Tree API 方式获取字节码
     */
    private byte[] getAsmTreeBytes(Instrumentation inst, ClassLoader loader, String className, byte[] classBytes) throws Exception {
        ClassReader cr;
        if (null != classBytes) cr = new ClassReader(classBytes);
        else cr = new ClassReader(className);
        ClassNode cn = new ClassNode(ASM8);
        cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES); // 跟 COMPUTE_FRAMES 搭配
        for (MethodNode mn : cn.methods) {
            //if ("fillInStackTrace".equals(mn.name) && "()Ljava/lang/Throwable;".equals(mn.desc)) {
            if ("setStackTrace".equals(mn.name) && "([Ljava/lang/StackTraceElement;)V".equals(mn.desc)) {
                System.out.println(">>>> Target method name: " + mn.name);
                System.out.println(">>>> Target method descriptor: " + mn.desc);

                InsnList insnList = new InsnList();
                // setStackTrace
                //insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                //insnList.add(new LdcInsnNode(">>>> Rewrite setStackTrace >>>>"));
                //insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                insnList.add(new VarInsnNode(ALOAD, 1));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "com/doidea/core/filters/StackTraceFilter", "testStackTrace", "([Ljava/lang/StackTraceElement;)[Ljava/lang/StackTraceElement;", false));
                insnList.add(new VarInsnNode(ASTORE, 1));

                /*
                // fillInStackTrace
                insnList.add(new VarInsnNode(ALOAD, 0)); // GETFIELD 操作
                insnList.add(new VarInsnNode(ALOAD, 0)); // PUTFIELD 操作
                insnList.add(new FieldInsnNode(GETFIELD, "java/lang/Throwable", "stackTrace", "[Ljava/lang/StackTraceElement;"));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "com/doidea/core/filters/StackTraceFilter", "testStackTrace", "([Ljava/lang/StackTraceElement;)[Ljava/lang/StackTraceElement;", false));
                insnList.add(new FieldInsnNode(PUTFIELD, "java/lang/Throwable", "stackTrace", "[Ljava/lang/StackTraceElement;"));
                */

                // 在方法开始前加入指令集
                mn.instructions.insert(insnList);

                /*
                // 在方法返回前加入指令集，返回前被置空了！stackTrace = UNASSIGNED_STACK;
                for (AbstractInsnNode in : mn.instructions) {
                    if (AbstractInsnNode.INSN == in.getType() && ARETURN == in.getOpcode())
                        mn.instructions.insert(in.getPrevious(), insnList); // ARETURN 前一个节点
                }
                */
            }
        }

        ClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_FRAMES, loader); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        cn.accept(cw);
        byte[] newBytes = cw.toByteArray();

        // 重写 class，classBytes 为 null，说明不是从 transform 进来，需要替换已加载的类【会导致不能正常启动】
        if (null == classBytes && null != className) {
            Class<?> targetClass = Class.forName(className.replace("/", "."));
            if (inst.isRedefineClassesSupported() && inst.isModifiableClass(targetClass)) {
                System.out.println(">>>> redefineClasses: " + className);
                inst.redefineClasses(new ClassDefinition(targetClass, newBytes));
            }
        }

        return newBytes;
    }


    /**
     * ASM Core API 方式获取字节码
     * 需要创建 ClassVisitor、MethodVisitor，代码较多，性能效率高
     */
    private byte[] getAsmBytes(ClassLoader loader, String className, byte[] classBytes) throws Exception {
        ClassReader cr;
        if (null != classBytes) cr = new ClassReader(classBytes);
        else cr = new ClassReader(className);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES); // COMPUTE_FRAMES 自动计算 max stacks、max locals 和 stack map frame
        ClassVisitor cv = new ThrowableClassVisitor(ASM8, cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    /**
     * 重写类访问器
     */
    private static class ThrowableClassVisitor extends ClassVisitor {
        public ThrowableClassVisitor(int api, ClassWriter classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (null != mv && "fillInStackTrace".equals(name) && "()Ljava/lang/Throwable;".equals(descriptor)) {
                System.out.println(">>>> Target method name: " + name);
                System.out.println(">>>> Target method descriptor: " + descriptor);
                return new FillInStackTraceMethodVisitor(ASM8, mv, name, access, descriptor);
            }
            return mv;
        }
    }

    /**
     * 重写方法访问器
     */
    private static class FillInStackTraceMethodVisitor extends MethodVisitor {

        private final String name;
        private final int access;
        private final String descriptor;

        public FillInStackTraceMethodVisitor(int api, MethodVisitor mv, String name, int access, String descriptor) {
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

            mv.visitVarInsn(ALOAD, 0); // GETFIELD 操作
            mv.visitVarInsn(ALOAD, 0); // PUTFIELD 操作
            mv.visitFieldInsn(GETFIELD, "java/lang/Throwable", "stackTrace", "[Ljava/lang/StackTraceElement;");
            mv.visitMethodInsn(INVOKESTATIC, "com/doidea/core/filters/StackTraceFilter", "testStackTrace", "([Ljava/lang/StackTraceElement;)[Ljava/lang/StackTraceElement;", false);
            mv.visitFieldInsn(PUTFIELD, "java/lang/Throwable", "stackTrace", "[Ljava/lang/StackTraceElement;");

            // 方法原有逻辑
            super.visitCode();
        }
    }
}
