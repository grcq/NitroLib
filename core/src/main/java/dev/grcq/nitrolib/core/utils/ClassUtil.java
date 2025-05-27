package dev.grcq.nitrolib.core.utils;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClassUtil {

    public static Collection<String> getClassNamesFromJar(File file) {
        List<String> classes = new ArrayList<>();
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                     String className = entry.getName().replace('/', '.').replace(".class", "");
                     try {
                         Class.forName(className);
                         classes.add(className);
                     } catch (ClassNotFoundException e) {
                         LogUtil.warn("Class not found in jar: " + className + " in file: " + file.getAbsolutePath(), e);
                     }
                }
            }
        } catch (Exception e) {
            LogUtil.error("Error reading jar file: " + file.getAbsolutePath(), e);
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(classes);
    }

}
