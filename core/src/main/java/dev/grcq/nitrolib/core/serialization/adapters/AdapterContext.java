package dev.grcq.nitrolib.core.serialization.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import dev.grcq.nitrolib.core.annotations.serialization.Serializable;
import dev.grcq.nitrolib.core.serialization.elements.FileElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterContext {

    public static final AdapterContext DEFAULT = new AdapterContext();
    private final Map<Class<?>, Deserializer<?>> DESERIALIZERS = new HashMap<>();
    private final Map<Class<?>, Serializer<?>> SERIALIZERS = new HashMap<>();

    public void registerTypeAdapter(Class<?> clazz, Serializer<?> serializer) {
        SERIALIZERS.put(clazz, serializer);
    }

    public void registerTypeAdapter(Class<?> clazz, Deserializer<?> deserializer) {
        DESERIALIZERS.put(clazz, deserializer);
    }

    public void registerTypeAdapter(Class<?> clazz, TypeAdapter<?> typeAdapter) {
        SERIALIZERS.put(clazz, typeAdapter);
        DESERIALIZERS.put(clazz, typeAdapter);
    }

    public <T> T deserialize(FileElement element, Class<T> clazz) {
        Deserializer<T> deserializer = (Deserializer<T>) DESERIALIZERS.get(clazz);
        if (deserializer == null) {
            throw new IllegalArgumentException("No deserializer found for class " + clazz.getName());
        }

        return deserializer.deserialize(element, this);
    }

    public <T> FileElement serialize(T object) {
        Serializer<T> serializer = (Serializer<T>) SERIALIZERS.get(object.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for class " + object.getClass().getName());
        }

        return serializer.serialize(object, this);
    }

}
