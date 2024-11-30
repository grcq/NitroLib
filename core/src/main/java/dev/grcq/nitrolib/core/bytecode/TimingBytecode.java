package dev.grcq.nitrolib.core.bytecode;

import dev.grcq.nitrolib.core.annotations.Timing;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Opcodes.ASM9;

public class TimingBytecode extends ClassVisitor {

    public TimingBytecode(ClassVisitor classVisitor) {
        super(ASM9, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new TimingMethodVisitor(ASM9, methodVisitor, access, name, descriptor);
    }

    private static class TimingMethodVisitor extends AdviceAdapter {
        private boolean hasAnnotation = false;
        private final String name;

        protected TimingMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
            this.name = name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, boolean b) {
            if (Type.getDescriptor(Timing.class).equals(s)) {
                hasAnnotation = true;
                return null;
            }
            return super.visitAnnotation(s, b);
        }

        @Override
        protected void onMethodEnter() {
            if (hasAnnotation) {
                // store start time
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                mv.visitVarInsn(LSTORE, 1);
            }
        }

        @Override
        protected void onMethodExit(int i) {
            if (hasAnnotation) {
                // store end - start time
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                mv.visitVarInsn(LSTORE, 3);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitLdcInsn("Method " + name + " executed in ");
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(LLOAD, 3);
                mv.visitVarInsn(LLOAD, 1);
                mv.visitInsn(LSUB);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn("ms");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            }
        }
    }

}
