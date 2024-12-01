package dev.grcq.nitrolib.core.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import dev.grcq.nitrolib.core.Constants;
import dev.grcq.nitrolib.core.config.ConfigField;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
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
            e.printStackTrace();
        }

        return null;
    }
}
