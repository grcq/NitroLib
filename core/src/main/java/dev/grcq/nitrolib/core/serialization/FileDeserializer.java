package dev.grcq.nitrolib.core.serialization;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import dev.grcq.nitrolib.core.annotations.serialization.Serializable;
import dev.grcq.nitrolib.core.annotations.serialization.SerializeField;
import dev.grcq.nitrolib.core.serialization.adapters.AdapterContext;
import dev.grcq.nitrolib.core.serialization.elements.*;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Objects;

public class FileDeserializer {

    private Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    @Setter
    @Getter
    private AdapterContext context = new AdapterContext();

    public <T> T deserialize(File file, Class<T> clazz) throws Exception {
        if (!clazz.isAnnotationPresent(Serializable.class))
            throw new Exception("Class is not serializable");

        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1);
        switch (extension) {
            case "json":
                return deserializeJson(file, clazz);
            case "yml":
            case "yaml":
                return deserializeYaml(file, clazz);
            default:
                throw new Exception("Unsupported file extension");
        }
    }

    private <T> T deserializeJson(File file, Class<T> clazz) {
        try (FileReader fileReader = new FileReader(file)) {
            JsonReader reader = new JsonReader(fileReader);
            JsonObject object = GSON.fromJson(reader, JsonObject.class);
            FileObject fileObject = Objects.requireNonNull(parseJson(object)).asFileObject();

            Serializable annotation = clazz.getAnnotation(Serializable.class);
            String key = annotation.value();

            return deserializeJson((key.isEmpty() ? fileObject : fileObject.get(key).asFileObject()), clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T> T deserializeJson(FileObject fileObject, Class<T> clazz) throws Exception {
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
            switch (field.getType().getSimpleName()) {
                case "int":
                case "Integer":
                    field.set(instance, fileObject.asInt(key));
                    break;
                case "long":
                case "Long":
                    field.set(instance, fileObject.asLong(key));
                    break;
                case "float":
                case "Float":
                    field.set(instance, fileObject.asFloat(key));
                    break;
                case "double":
                case "Double":
                    field.set(instance, fileObject.asDouble(key));
                    break;
                case "boolean":
                case "Boolean":
                    field.set(instance, fileObject.asBoolean(key));
                    break;
                case "String":
                    field.set(instance, fileObject.asString(key));
                    break;
                default:
                    Object value;
                    if (field.getType().isAnnotationPresent(Serializable.class)) {
                        value = deserializeJson(fileObject.get(key).asFileObject(), field.getType());
                    } else {
                        value = context.deserialize(fileObject.get(key), field.getType());
                    }
                    field.set(instance, value);
                    break;
            }
            field.setAccessible(false);
        }

        return instance;
    }

    private static FileElement parseJson(JsonElement element) {
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

    private static <T> T deserializeYaml(File file, Class<T> clazz) {
        return null;
    }

}
