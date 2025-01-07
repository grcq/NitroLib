package dev.grcq.nitrolib.core.serialization;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dev.grcq.nitrolib.core.annotations.serialization.Serializable;
import dev.grcq.nitrolib.core.annotations.serialization.SerializeField;
import dev.grcq.nitrolib.core.serialization.adapters.AdapterContext;
import dev.grcq.nitrolib.core.serialization.elements.*;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import sun.security.pkcs11.wrapper.CK_LOCKMUTEX;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileDeserializer {

    private Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();

    public <T> List<T> deserialize(File file, Class<T> clazz) throws Exception {
        return deserialize(file, clazz, AdapterContext.DEFAULT);
    }

    public <T> List<T> deserialize(File file, Class<T> clazz, AdapterContext context) throws Exception {
        if (!clazz.isAnnotationPresent(Serializable.class))
            throw new Exception("Class is not serializable");

        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1);
        switch (extension) {
            case "json":
                return deserializeJson(file, clazz, context);
            case "yml":
            case "yaml":
                return deserializeYaml(file, clazz, context);
            default:
                throw new Exception("Unsupported file extension");
        }
    }

    private <T> List<T> deserializeJson(File file, Class<T> clazz, AdapterContext context) {
        try (FileReader fileReader = new FileReader(file)) {
            JsonReader reader = new JsonReader(fileReader);
            JsonObject object = GSON.fromJson(reader, JsonObject.class);
            FileObject fileObject = Objects.requireNonNull(parseJson(object)).asFileObject();

            Serializable annotation = clazz.getAnnotation(Serializable.class);
            String key = annotation.value();
            if (!key.isEmpty()) {
                if (!fileObject.has(key)) return null;

                FileElement fileElement = fileObject.get(key);
                if (fileElement.isFileArray()) {
                    List<T> list = Lists.newArrayList();
                    for (FileElement element : (FileArray) fileElement) {
                        if (!element.isFileObject()) throw new Exception("Element is not a file object");
                        list.add(deserializeContent(element.asFileObject(), clazz, context));
                    }
                    return list;
                }
            }

            return Lists.newArrayList(deserializeContent((key.isEmpty() ? fileObject : fileObject.get(key).asFileObject()), clazz, context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T> T deserializeContent(FileObject fileObject, Class<T> clazz, AdapterContext context) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        T instance = clazz.newInstance();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(SerializeField.class)) continue;
            SerializeField annotation = field.getAnnotation(SerializeField.class);
            String key = annotation.value();
            if (!fileObject.has(key)) {
                if (!annotation.nullable())
                    throw new Exception("Field " + field.getName() + " is not nullable");
                continue;
            }

            field.setAccessible(true);
            Class<?> type = null;
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                if (types.length > 0) type = (Class<?>) types[0];
            }

            field.set(instance, deserializeContent(fileObject.get(key), field.getType(), type, context));
            field.setAccessible(false);
        }

        return instance;
    }

    private Object deserializeContent(FileElement element, Class<?> type, Class<?> genericType, AdapterContext context) throws Exception {
        switch (type.getSimpleName()) {
            case "int":
            case "Integer":
                return element.asFilePrimitive().asInt();
            case "long":
            case "Long":
                return element.asFilePrimitive().asLong();
            case "float":
            case "Float":
                return element.asFilePrimitive().asFloat();
            case "double":
            case "Double":
                return element.asFilePrimitive().asDouble();
            case "boolean":
            case "Boolean":
                return element.asFilePrimitive().asBoolean();
            case "String":
                return element.asFilePrimitive().asString();
            default:
                if (Iterable.class.isAssignableFrom(type) && genericType != null) {
                    FileArray array = element.asFileArray();
                    Collection<Object> collection = (type == List.class || type == Collection.class ? Lists.newArrayList() :
                            (type == Set.class ? new HashSet<>() : (Collection<Object>) type.getDeclaredConstructor().newInstance()));

                    for (FileElement fileElement : array) {
                        Object obj = deserializeContent(fileElement, genericType, null, context);
                        collection.add(obj);
                    }
                    return collection;
                } else if (type.getSimpleName().equals("FileElement") || type.getSuperclass().getSimpleName().equals("FileElement")) {
                    return element;
                } else if (type.isAnnotationPresent(Serializable.class)) {
                    return deserializeContent(element.asFileObject(), type, context);
                } else {
                    return context.deserialize(element, type);
                }
        }
    }

    private FileElement parseJson(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            FileObject fileObject = new FileObject();
            for (String key : object.keySet()) {
                JsonElement value = object.get(key);
                fileObject.add(key, parseJson(value));
            }

            return fileObject;
        }

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            FileArray fileArray = new FileArray();
            for (JsonElement value : array) {
                fileArray.add(parseJson(value));
            }
            return fileArray;
        }

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) return new FilePrimitive(primitive.getAsBoolean());
            if (primitive.isNumber()) return new FilePrimitive(primitive.getAsNumber());
            if (primitive.isString()) return new FilePrimitive(primitive.getAsString());
            if (primitive.isJsonNull()) return new FileNull();
        }

        if (element.isJsonNull()) return new FileNull();
        return null;
    }

    private <T> List<T> deserializeYaml(File file, Class<T> clazz, AdapterContext context) {
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(file.toPath())) {
            Map<String, Object> object = yaml.load(in);
            FileObject fileObject = new FileObject();
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                fileObject.add(entry.getKey(), parseYaml(entry.getValue()));
            }

            Serializable annotation = clazz.getAnnotation(Serializable.class);
            String key = annotation.value();
            if (!key.isEmpty()) {
                if (!fileObject.has(key)) return null;

                FileElement fileElement = fileObject.get(key);
                if (fileElement.isFileArray()) {
                    List<T> list = Lists.newArrayList();
                    for (FileElement element : (FileArray) fileElement) {
                        if (!element.isFileObject()) throw new Exception("Element is not a file object");
                        list.add(deserializeContent(element.asFileObject(), clazz, context));
                    }
                    return list;
                }
            }

            return Lists.newArrayList(deserializeContent((key.isEmpty() ? fileObject : fileObject.get(key).asFileObject()), clazz, context));
        } catch (Exception e) {
            LogUtil.handleException("Failed to deserialize yaml file", e);
        }
        return null;
    }

    private FileElement parseYaml(Object object) {
        if (object instanceof Map) {
            FileObject fileObject = new FileObject();
            Map<String, Object> map = (Map<String, Object>) object;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                fileObject.add(entry.getKey(), parseYaml(entry.getValue()));
            }
            return fileObject;
        }

        if (object instanceof Iterable) {
            FileArray fileArray = new FileArray();
            for (Object value : (Iterable<?>) object) {
                fileArray.add(parseYaml(value));
            }
            return fileArray;
        }

        if (object instanceof Boolean) return new FilePrimitive((Boolean) object);
        if (object instanceof Number) return new FilePrimitive((Number) object);
        if (object instanceof String) return new FilePrimitive((String) object);
        if (object instanceof Character) return new FilePrimitive((Character) object);
        return new FileNull();
    }

}
