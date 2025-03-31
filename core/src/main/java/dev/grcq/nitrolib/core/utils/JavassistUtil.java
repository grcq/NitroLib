package dev.grcq.nitrolib.core.utils;

import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JavassistUtil {

    public static void removeAnnotation(CtMethod method, Class<?> annotation) {
        MethodInfo info = method.getMethodInfo();
        AnnotationsAttribute visible = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.invisibleTag);

        if (visible != null) {
            AnnotationsAttribute replacementVisible = new AnnotationsAttribute(info.getConstPool(), AnnotationsAttribute.visibleTag);
            for (Annotation ann : visible.getAnnotations()) {
                LogUtil.info(ann.getTypeName());
                if (ann.getTypeName().equalsIgnoreCase(annotation.getName())) continue;
                replacementVisible.addAnnotation(ann);
            }
            info.removeAttribute(AnnotationsAttribute.visibleTag);
            info.addAttribute(replacementVisible);
        }

        if (invisible != null) {
            AnnotationsAttribute replacementInvisible = new AnnotationsAttribute(info.getConstPool(), AnnotationsAttribute.invisibleTag);
            for (Annotation ann : invisible.getAnnotations()) {
                if (ann.getTypeName().equalsIgnoreCase(annotation.getName())) continue;
                replacementInvisible.addAnnotation(ann);
            }
            info.removeAttribute(AnnotationsAttribute.invisibleTag);
            info.addAttribute(replacementInvisible);
        }
    }

}
