package dev.grcq.nitrolib.core.utils;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import dev.grcq.nitrolib.core.Constants;
import dev.grcq.nitrolib.core.config.ConfigField;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

@UtilityClass
public class Util {

    public static boolean isSrvAddress(String url) {
        Pattern pattern = Pattern.compile("^(.*):\\/\\/(.*):(.*)$");
        return pattern.matcher(url).matches();
    }

    public static JsonObject parseClassAsJsonWithDefaults(Class<?> configurationClass) {
        try {
            Object instance = configurationClass.getDeclaredConstructor().newInstance();
            JsonObject object = new JsonObject();
            for (Field field : configurationClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(instance);
                if (value == null) continue;
                String name;
                if (field.isAnnotationPresent(ConfigField.class)) {
                    ConfigField configField = field.getAnnotation(ConfigField.class);
                    name = configField.value();
                } else {
                    name = field.getName();
                }

                JsonElement element = Constants.GSON.toJsonTree(value);
                object.add(name, element);
            }
            return object;
        } catch (Exception e) {
            LogUtil.handleException("Failed to parse class as JSON.", e);
        }

        return null;
    }

    public static Collection<Class<?>> getClassesInPackage(Class<?> mainClass) {
        return getClassesInPackage(mainClass, mainClass.getPackage().getName());
    }

    public static Collection<Class<?>> getClassesInPackage(Class<?> mainClass, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource = mainClass.getProtectionDomain().getCodeSource();
        URL location = codeSource.getLocation();
        String relativePath = packageName.replace('.', '/');
        String resourcePath = location.getPath().replace("%20", " ");
        String jarPath = resourcePath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        try {
            jarFile = new JarFile(jarPath);
        } catch (Exception e) {
            LogUtil.handleException("Failed to read JAR file", e);
            return new ArrayList<>();
        }

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relativePath) && entryName.length() > (relativePath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                Class<?> clazz;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    LogUtil.handleException("Failed to load class: " + className, e);
                    continue;
                }

                classes.add(clazz);
            }
        }

        try {
            jarFile.close();
        } catch (Exception e) {
            LogUtil.warn("Failed to close JAR file.");
        }

        return ImmutableSet.copyOf(classes);
    }
}
