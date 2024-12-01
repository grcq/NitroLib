package dev.grcq.nitrolib.core.config;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.grcq.nitrolib.core.Constants;
import dev.grcq.nitrolib.core.utils.LogUtil;
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
            LogUtil.verbose("Found class with @Configuration annotation: %s", configurationClass.getName());
            LogUtil.verbose("Loading configuration file: %s", fileName);
            LogUtil.verbose("Configuration file extension: %s", extension);

            Constants.ConfigType configType = Constants.ConfigType.fromExtension(extension);
            if (configType == null) {
                throw new IllegalArgumentException("Unsupported configuration file extension: " + extension);
            }

            JsonObject configurationObject = configType.read(fileName);
            if (configurationObject == null) {
                throw new IllegalArgumentException("Failed to read configuration file: " + fileName);
            }

            LogUtil.verbose("Configuration file loaded and file parsed to a JsonObject.");
            LogUtil.verbose("Configuration file contents: %s", configurationObject);
            for (var field : configurationClass.getDeclaredFields()) {
                field.setAccessible(true);

                LogUtil.verbose("Checking field: %s", field.getName());
                ConfigField configField = field.getAnnotation(ConfigField.class);
                if (configField == null) {
                    LogUtil.verbose("Field does not have @ConfigField annotation");
                    continue;
                }

                Class<?> fieldType = field.getType();
                String fullFieldName = configField.value();
                String[] fieldNameParts = fullFieldName.split("\\.");
                LogUtil.verbose("Checking for path '%s' in %s", fullFieldName, fileName);

                JsonObject currentObject = configurationObject;
                if (fieldNameParts.length > 1) {
                    for (int i = 0; i < fieldNameParts.length - 1; i++) {
                        currentObject = currentObject.getAsJsonObject(fieldNameParts[i]);
                    }
                }

                String fieldName = fieldNameParts[fieldNameParts.length - 1];
                if (!currentObject.has(fieldName)) {
                    LogUtil.verbose("Could not find specified path '%s' in %s, skipping..." , fullFieldName, fileName);
                    continue;
                }

                JsonElement fieldValue = currentObject.get(fieldName);
                Object value = Constants.GSON.fromJson(fieldValue, fieldType);
                LogUtil.verbose("Setting field '%s' to value: %s", field.getName(), value);
                try {
                    field.set(null, value);
                } catch (IllegalAccessException e) {
                    LogUtil.handleException("Failed to set configuration field: " + field.getName(), e);
                }
            }
        }
    }

}
