package dev.grcq.nitrolib.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;

public interface Constants {

    Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    Gson GSON_PPT = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).setPrettyPrinting().create();

    String NO_RESPONSE = "NO_RESPONSE_wy7n3iomfg83_socket";

    @Getter
    enum ConfigType {
        JSON("json") {
            @Override
            public JsonObject read(File file) {
                return null;
            }

            @Override
            public void write(File file, JsonObject content) {
            }
        },
        YAML("yaml", "yml") {
            @Override
            public JsonObject read(File file) {
                return null;
            }

            @Override
            public void write(File file, JsonObject content) {
            }
        };

        private final String[] extensions;

        ConfigType(String... extensions) {
            this.extensions = extensions;
        }

        public abstract JsonObject read(File file);
        public abstract void write(File file, JsonObject content);

        public JsonObject read(Path file) {
            return read(file.toFile());
        }

        public JsonObject read(String file) {
            return read(new File(file));
        }

        public void write(Path file, JsonObject content) {
            write(file.toFile(), content);
        }

        public void write(String file, JsonObject content) {
            write(new File(file), content);
        }

        public static ConfigType fromExtension(String extension) {
            for (ConfigType configType : values()) {
                for (String ext : configType.extensions) {
                    if (ext.equals(extension)) {
                        return configType;
                    }
                }
            }
            return null;
        }
    }
}
