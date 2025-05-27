package dev.grcq.nitrolib.discord.test;

import dev.grcq.nitrolib.core.serialization.FileDeserializer;

import java.io.File;

// TEST CLASS
public class Main {

    public static void main(String[] args) throws Exception {
        FileDeserializer deserializer = new FileDeserializer();
        File file = new File("discord.json");
        TestConfig config = deserializer.deserialize(file, TestConfig.class).get(0);

        System.out.println(config.getToken());
    }

}
