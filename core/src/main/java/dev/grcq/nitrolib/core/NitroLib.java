package dev.grcq.nitrolib.core;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import dev.grcq.nitrolib.core.annotations.Validate;
import dev.grcq.nitrolib.core.cli.options.OptionParser;
import dev.grcq.nitrolib.core.cli.options.def.NitroOptions;
import dev.grcq.nitrolib.core.config.ConfigurationHandler;
import dev.grcq.nitrolib.core.config.TestConfig;
import dev.grcq.nitrolib.core.database.Condition;
import dev.grcq.nitrolib.core.database.RelationalDatabase;
import dev.grcq.nitrolib.core.serialization.FileDeserializer;
import dev.grcq.nitrolib.core.serialization.FileSerializer;
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

    // TODO
    @Validate(regex = "[a-zA-Z]+")
    private static String test;

    /**
     * Initializes NitroLib, no need to call this in your Minecraft plugin unless you want to use our options.
     * @param mainClass The main class of your project
     */
    public static void init(Class<?> mainClass) {
        init(mainClass, new String[0]);
    }

    /**
     * Initializes NitroLib, no need to call this in your Minecraft plugin unless you want to use our options.
     * @param mainClass The main class of your project
     * @param args The arguments passed to the main method, or your custom arguments
     */
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
    }

    public static void main(String[] args) {
        init(NitroLib.class, new String[] { "-v", "-d" });
    }

}
