package dev.grcq.nitrolib.core;

import com.google.common.base.Preconditions;
import dev.grcq.nitrolib.core.cli.options.OptionParser;
import dev.grcq.nitrolib.core.cli.options.def.NitroOptions;
import dev.grcq.nitrolib.core.config.ConfigurationHandler;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;

public class NitroLib {

    private static boolean initialized = false;
    @Getter
    private static NitroOptions options;

    public static void init(Class<?> mainClass) {
        init(mainClass, new String[0]);
    }

    public static void init(Class<?> mainClass, String[] args) {
        Preconditions.checkState(!initialized, "NitroLib is already initialized");
        initialized = true;

        options = new NitroOptions();
        OptionParser.parse(options, args);

        LogUtil.info("Initializing NitroLib");

        ConfigurationHandler configurationHandler = new ConfigurationHandler();

        configurationHandler.loadConfiguration(mainClass);
    }

    public static void main(String[] args) {
        init(NitroLib.class, new String[] { "-v", "-d" });
    }

}
