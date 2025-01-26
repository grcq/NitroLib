package dev.grcq.nitrolib.core.bytecode;

import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import dev.grcq.nitrolib.core.annotations.Inject;
import dev.grcq.nitrolib.core.utils.LogUtil;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.build.JavassistBuildException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;

import java.util.Arrays;

public class InjectTransformer extends ClassTransformer {

    @Override
    public boolean shouldTransform(CtClass ctClass) throws JavassistBuildException {
        return Arrays.stream(ctClass.getDeclaredFields())
                .anyMatch(field -> field.hasAnnotation(Inject.class));
    }

    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
        for (CtField field : ctClass.getDeclaredFields()) {
            if (!field.hasAnnotation(Inject.class) || Modifier.isStatic(field.getModifiers())) continue;

            try {
                CtClass fieldType = field.getType();
                String injectable = "dev.grcq.nitrolib.core.inject.InjectHandler.get(" + fieldType.getName() + ".class)";

                CtField replacement = CtField.make(fieldType.getSimpleName() + " " + field.getName() + " = " + injectable + ";", ctClass);
                replacement.setModifiers(field.getModifiers());

                FieldInfo fieldInfo = field.getFieldInfo();
                AnnotationsAttribute visible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
                AnnotationsAttribute invisible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.invisibleTag);

                if (visible != null) {
                    AnnotationsAttribute replacementVisible = new AnnotationsAttribute(replacement.getFieldInfo().getConstPool(), AnnotationsAttribute.visibleTag);
                    for (Annotation annotation : visible.getAnnotations()) {
                        if (annotation.getTypeName().equalsIgnoreCase(Inject.class.getName())) continue;
                        replacementVisible.addAnnotation(annotation);
                    }
                    replacement.getFieldInfo().addAttribute(replacementVisible);
                }

                if (invisible != null) {
                    AnnotationsAttribute replacementInvisible = new AnnotationsAttribute(replacement.getFieldInfo().getConstPool(), AnnotationsAttribute.invisibleTag);
                    for (Annotation annotation : invisible.getAnnotations()) {
                        replacementInvisible.addAnnotation(annotation);
                    }
                    replacement.getFieldInfo().addAttribute(replacementInvisible);
                }

                ctClass.removeField(field);
                ctClass.addField(replacement);
            } catch (Exception e) {
                LogUtil.handleException("Failed to inject field " + field.getName(), e);
            }
        }
    }
}
