package dev.grcq.nitrolib.core;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import dev.grcq.nitrolib.core.annotations.Validate;
import dev.grcq.nitrolib.core.cli.options.OptionParser;
import dev.grcq.nitrolib.core.cli.options.def.NitroOptions;
import dev.grcq.nitrolib.core.config.ConfigurationHandler;
import dev.grcq.nitrolib.core.config.TestConfig;
import dev.grcq.nitrolib.core.database.Condition;
import dev.grcq.nitrolib.core.database.RelationalDatabase;
import dev.grcq.nitrolib.core.serialization.FileDeserializer;
import dev.grcq.nitrolib.core.serialization.elements.FileArray;
import dev.grcq.nitrolib.core.serialization.elements.FileElement;
import dev.grcq.nitrolib.core.serialization.elements.FileObject;
import dev.grcq.nitrolib.core.serialization.test.TestClass;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NitroLib {

    private static boolean initialized = false;
    @Getter
    private static NitroOptions options;

    @Validate(regex = "[a-zA-Z]+")
    private static String test;

    public static void init(Class<?> mainClass) {
        init(mainClass, new String[0]);
    }

    public static void init(Class<?> mainClass, String[] args) {
        Preconditions.checkState(!initialized, "NitroLib is already initialized");
        initialized = true;

        test = "6";

        options = new NitroOptions();
        OptionParser.parse(options, args);

        if (options.isVerbose() && options.isSilent()) {
            LogUtil.error("Cannot have both verbose and silent mode enabled", 1);
        }

        LogUtil.info("Initializing NitroLib");

        LogUtil.info("Loading handlers...");
        ConfigurationHandler configurationHandler = new ConfigurationHandler();

        configurationHandler.loadConfiguration(mainClass);
        LogUtil.info("Handlers loaded!");

        LogUtil.info("NitroLib initialized!");

        LogUtil.debug(TestConfig.TEST);
        LogUtil.debug(RelationalDatabase.QueryBuilder.builder()
                .select("*")
                .from("test")
                .where(new Condition[]{
                        new Condition("id", Condition.Operators.EQUALS, 1),
                        new Condition("name", Condition.Operators.EQUALS, "test")
                })
                .orderBy("id", true)
                .limit(1)
                .build());
        URL url = NitroLib.class.getResource("/config.yml");
        try {
            File file = new File(url.toURI());
            FileDeserializer fileDeserializer = new FileDeserializer();
            TestClass testClass = fileDeserializer.deserialize(file, TestClass.class);
            System.out.println(testClass);

            FileObject fileObject = new FileObject();
            fileObject.add("test", "test");
            fileObject.add("test2", true);

            FileObject fileObject2 = new FileObject();
            fileObject2.add("acb", "test");
            fileObject2.add("hgfg", "fghf");

            FileElement copy = fileObject2.copy();

            FileArray fileArray = new FileArray();

            FileArray fileArray2 = new FileArray();
            fileArray2.add("test");
            fileArray2.add("test2");

            fileArray.add(fileArray2);
            fileArray.add(fileArray2);

            fileObject2.add("array", fileArray);
            fileObject.add("test3", fileObject2);
            //System.out.println(fileObject.toYaml());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init(NitroLib.class, new String[] { "-v", "-d" });
    }

}
