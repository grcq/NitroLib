package dev.grcq.nitrolib.core.config;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.Constants;
import lombok.var;
import org.reflections.Reflections;

import java.util.Set;

public class ConfigurationHandler {

    private static boolean initialized = false;

    public void loadConfiguration(Class<?> mainClass) {
        Preconditions.checkState(!initialized, "Configuration is already loaded");
        initialized = true;

        Reflections reflections = new Reflections(mainClass.getPackage().getName());
        Set<Class<?>> configurationClasses = reflections.getTypesAnnotatedWith(Configuration.class);

        for (Class<?> configurationClass : configurationClasses) {
            Configuration configuration = configurationClass.getAnnotation(Configuration.class);
            String fileName = configuration.value();
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

            Constants.ConfigType configType = Constants.ConfigType.fromExtension(extension);
            if (configType == null) {
                throw new IllegalArgumentException("Unsupported configuration file extension: " + extension);
            }

            JsonObject configurationObject = configType.read(fileName);
            if (configurationObject == null) {
                throw new IllegalArgumentException("Failed to read configuration file: " + fileName);
            }

            for (var field : configurationClass.getDeclaredFields()) {
                field.setAccessible(true);
                ConfigField configField = field.getAnnotation(ConfigField.class);
                if (configField == null) continue;

                Class<?> fieldType = field.getType();
                String[] fieldNameParts = configField.value().split("\\.");

                JsonObject currentObject = configurationObject;
                if (fieldNameParts.length > 1) {
                    for (int i = 0; i < fieldNameParts.length - 1; i++) {
                        currentObject = currentObject.getAsJsonObject(fieldNameParts[i]);
                    }
                }

                String fieldName = fieldNameParts[fieldNameParts.length - 1];
                if (!currentObject.has(fieldName)) continue;

                JsonElement fieldValue = currentObject.get(fieldName);
                
            }
        }
    }

}
