package dev.grcq.nitrolib.core.serialization;

import dev.grcq.nitrolib.core.annotations.serialization.Serializable;
import dev.grcq.nitrolib.core.annotations.serialization.SerializeField;
import dev.grcq.nitrolib.core.serialization.adapters.AdapterContext;
import dev.grcq.nitrolib.core.serialization.elements.*;
import dev.grcq.nitrolib.core.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

public class FileSerializer {

    public <T> FileObject serialize(T object) {
        return serialize(object, AdapterContext.DEFAULT);
    }

    public <T> FileObject serialize(T object, AdapterContext context) {
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Serializable.class)) {
            LogUtil.error("Class is not serializable: " + clazz.getName());
            return null;
        }

        Serializable annotation = clazz.getAnnotation(Serializable.class);
        String key = annotation.value();

        FileObject fileObject = new FileObject();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(SerializeField.class)) continue;

            SerializeField serializeField = field.getAnnotation(SerializeField.class);
            String name = serializeField.value();
            boolean nullable = serializeField.nullable();

            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value == null && !nullable) {
                    LogUtil.error("Field is null and not nullable: " + field.getName());
                    return null;
                }

                if (value == null) continue;

                Class<?> type = value.getClass();
                if (Iterable.class.isAssignableFrom(type)) {
                    FileArray array = new FileArray();
                    for (Object obj : (Iterable<?>) value) {
                        FileElement element = parse(obj.getClass(), obj, field, serializeField, context);
                        array.add(element);
                    }
                    fileObject.add(name, array);
                } else {
                    FileElement element = parse(type, value, field, serializeField, context);
                    fileObject.add(name, element);
                }
            } catch (IllegalAccessException e) {
                LogUtil.error("Failed to access field: " + field.getName());
            }
        }

        if (!key.isEmpty()) {
            FileObject obj = new FileObject();
            obj.add(key, fileObject);
            return obj;
        }

        return fileObject;
    }

    private FileElement parse(Class<?> type, Object value, Field field, SerializeField serializeField, AdapterContext context) {
        switch (type.getSimpleName()) {
            case "String":
                return new FilePrimitive((String) value);
            case "int":
            case "Integer":
                return new FilePrimitive((int) value);
            case "long":
            case "Long":
                return new FilePrimitive((long) value);
            case "double":
            case "Double":
                return new FilePrimitive((double) value);
            case "float":
            case "Float":
                return new FilePrimitive((float) value);
            case "boolean":
            case "Boolean":
                return new FilePrimitive((boolean) value);
            default:
                FileElement obj;
                boolean ignoreRootPath = serializeField.ignoreRootPath();
                if (type.getSimpleName().equals("FileElement") || type.getSuperclass().getSimpleName().equals("FileElement")) {
                    obj = (FileElement) value;
                } else if (value.getClass().isAnnotationPresent(Serializable.class)) {
                    FileObject fileObj = serialize(value, context);
                    Serializable serializable = value.getClass().getAnnotation(Serializable.class);
                    String rootPath = serializable.value();
                    if (ignoreRootPath && !rootPath.isEmpty()) {
                        fileObj = fileObj.get(rootPath).asFileObject();
                    }

                    obj = fileObj;
                } else {
                    obj = context.serialize(value);
                }
                return obj;
        }
    }

    public <T> void serialize(T object, File file) {
        serialize(object, file, AdapterContext.DEFAULT);
    }

    public <T> void serialize(T object, File file, AdapterContext context) {
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();

                file.createNewFile();
            } catch (IOException e) {
                LogUtil.handleException("Failed to create file: " + file.getName(), e);
                return;
            }
        }
        FileObject fileObject = serialize(object, context);
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1);
        switch (extension) {
            case "json":
                String json = fileObject.toJson();
                writeToFile(json, file);
                break;
            case "yml":
            case "yaml":
                String yaml = fileObject.toYaml();
                writeToFile(yaml, file);
                break;
            default:
                break;
        }
    }

    private void writeToFile(String content, File file) {
        try {
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            LogUtil.error("Failed to write to file: " + file.getName());
        }
    }
}
