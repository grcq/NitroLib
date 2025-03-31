package dev.grcq.nitrolib.core.bytecode;

import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import dev.grcq.nitrolib.core.annotations.Singleton;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.build.JavassistBuildException;

public class SingletonTransformer extends ClassTransformer {

    @Override
    public boolean shouldTransform(CtClass ctClass) throws JavassistBuildException {
        return ctClass.hasAnnotation(Singleton.class);
    }

    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
        try {
            CtField instanceField = CtField.make("private static " + ctClass.getName() + " instance;", ctClass);
            ctClass.addField(instanceField);

            ctClass.addMethod(CtMethod.make("public static " + ctClass.getName() + " getInstance() {\n" +
                    "        if (instance == null) {\n" +
                    "            instance = new " + ctClass.getName() + "();\n" +
                    "        }\n" +
                    "        return instance;\n" +
                    "    }", ctClass));

            ctClass.addMethod(CtMethod.make("private " + ctClass.getName() + "() {}", ctClass));
        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }
}
