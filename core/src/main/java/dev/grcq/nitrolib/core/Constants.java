package dev.grcq.nitrolib.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public interface Constants {

    Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    Gson GSON_PPT = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).setPrettyPrinting().create();

    String NO_RESPONSE = "NO_RESPONSE_wy7n3iomfg83_socket";

    @Getter
    enum ConfigType {
        JSON("json") {
            @Override
            public JsonObject read(String file) {
                StringBuilder content = new StringBuilder();
                try {
                    InputStream inputStream = NitroLib.class.getClassLoader().getResourceAsStream(file);
                    if (inputStream == null) {
                        return null;
                    }
                    int data = inputStream.read();
                    while (data != -1) {
                        content.append((char) data);
                        data = inputStream.read();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return GSON.fromJson(content.toString(), JsonObject.class);
            }

            @Override
            public void write(String file, JsonObject content) {

            }
        },
        YAML("yaml", "yml") {
            @Override
            public JsonObject read(String file) {
                return null;
            }

            @Override
            public void write(String file, JsonObject content) {
            }
        };

        private final String[] extensions;

        ConfigType(String... extensions) {
            this.extensions = extensions;
        }

        public abstract JsonObject read(String file);
        public abstract void write(String file, JsonObject content);

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
