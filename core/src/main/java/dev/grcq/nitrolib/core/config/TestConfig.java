package dev.grcq.nitrolib.core.config;

@Configuration(value = "config.json", createIfNotExists = true)
public class TestConfig {

    @ConfigField("test")
    public static String TEST = "abc";

}
