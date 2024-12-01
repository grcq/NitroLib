package dev.grcq.nitrolib.core.config;

@Configuration("config.json")
public class TestConfig {

    @ConfigField("test")
    public static String TEST = "abc";

}
