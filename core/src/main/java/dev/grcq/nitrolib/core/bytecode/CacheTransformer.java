package dev.grcq.nitrolib.core.bytecode;

import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import dev.grcq.nitrolib.core.annotations.Cached;
import dev.grcq.nitrolib.core.utils.JavassistUtil;
import dev.grcq.nitrolib.core.utils.LogUtil;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.build.JavassistBuildException;

import java.util.Arrays;

public class CacheTransformer extends ClassTransformer {

    @Override
    public boolean shouldTransform(CtClass ctClass) throws JavassistBuildException {
        return Arrays.stream(ctClass.getMethods()).anyMatch(method -> {
            try {
                return method.hasAnnotation(Cached.class) && method.getReturnType() != CtClass.voidType;
            } catch (NotFoundException e) {
                LogUtil.handleException("Failed to check if method has @Cached annotation", e);
                return false;
            }
        });
    }

    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
        try {
            for (CtMethod method : ctClass.getMethods()) {
                if (!method.hasAnnotation(Cached.class) || method.getReturnType() == CtClass.voidType) continue;
                JavassistUtil.removeAnnotation(method, Cached.class);

                Cached cached = (Cached) method.getAnnotation(Cached.class);
                String methodName = method.getName();
                StringBuilder key = new StringBuilder(String.format("%s#%s(\"", ctClass.getName(), methodName));
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    key.append(" + $").append(i + 1).append(" + \",\"");
                }
                key.append(" + \")");

                method.insertBefore("if (dev.grcq.nitrolib.core.NitroLib.getCacheManager().exists(\"" + key + "\")) { return ($r) dev.grcq.nitrolib.core.NitroLib.getCacheManager().get(\"" + key + "\"); }");
                method.insertAfter("dev.grcq.nitrolib.core.NitroLib.getCacheManager().set(\"" + key + "\", (Object) $_, " + cached.ttl() + "L);");
            }
        } catch (Exception e) {
            LogUtil.handleException("Failed to apply cache transformation", e);
        }
    }
}
