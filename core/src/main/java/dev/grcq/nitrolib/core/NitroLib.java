package dev.grcq.nitrolib.core;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import dev.grcq.nitrolib.core.cache.CacheManager;
import dev.grcq.nitrolib.core.cli.options.OptionParser;
import dev.grcq.nitrolib.core.cli.options.def.NitroOptions;
import dev.grcq.nitrolib.core.config.ConfigurationHandler;
import dev.grcq.nitrolib.core.database.Condition;
import dev.grcq.nitrolib.core.database.RelationalDatabase;
import dev.grcq.nitrolib.core.database.impl.relational.MySQL;
import dev.grcq.nitrolib.core.inject.InjectHandler;
import dev.grcq.nitrolib.core.tests.database.TestEntity;
import dev.grcq.nitrolib.core.utils.KeyValue;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class NitroLib {

    public static final Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();

    private static boolean initialized = false;
    @Getter
    private static final NitroOptions options = new NitroOptions();

    @Getter
    private static CacheManager cacheManager;

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
    @SneakyThrows
    public static void init(Class<?> mainClass, String[] args) {
        Preconditions.checkState(!initialized, "NitroLib is already initialized");
        initialized = true;

        OptionParser.parse(options, args);

        if (options.isVerbose() && options.isSilent()) {
            LogUtil.error("Cannot have both verbose and silent mode enabled", 1);
        }

        LogUtil.info("Initializing NitroLib");

        LogUtil.info("Loading handlers...");
        ConfigurationHandler configurationHandler = new ConfigurationHandler();
        cacheManager = new CacheManager();

        InjectHandler.register(NitroOptions.class, options);
        InjectHandler injectHandler = new InjectHandler(mainClass);
        injectHandler.inject();

        configurationHandler.loadConfiguration(mainClass);
        LogUtil.info("Handlers loaded!");

        //TestClass testClass = n ew TestClass();
        //System.out.println(testClass.getOptions());

        /*RelationalDatabase database = new MySQL("a", "a", "a", "a");
        database.connect();
        database.createTableORM(TestEntity.class);

        TestEntity entity = database.create(TestEntity.class, Arrays.asList(
                KeyValue.of("name", "Test"),
                KeyValue.of("age", 20),
                KeyValue.of("active", true),
                KeyValue.of("balance", 100.0)
        ));
        System.out.println(entity);*/

        TestClass testClass = new TestClass();
        LogUtil.info(testClass.test(5));
        LogUtil.info(testClass.test(6));
        LogUtil.info(testClass.test(5));

        LogUtil.info("NitroLib initialized!");
    }

    public static void main(String[] args) {
        init(NitroLib.class, new String[] { "-v", "-d" });
    }

}
