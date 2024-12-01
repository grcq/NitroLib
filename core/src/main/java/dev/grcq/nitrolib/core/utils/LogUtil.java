package dev.grcq.nitrolib.core.utils;

import dev.grcq.nitrolib.core.NitroLib;

public class LogUtil {

    public LogUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void handleException(String message, Exception e) {
        // todo: implement
        e.printStackTrace();
    }

    public static void error(String message) {
        error(message, -1);
    }

    public static void error(String message, int exit, Object... args) {
        System.out.println("\u001B[31m[ERROR] " + String.format(message, args) + "\u001B[0m");
        if (exit >= 0) System.exit(exit);
    }

    public static void info(String message, Object... args) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[34m[INFO] \u001B[37m" + String.format(message, args) + "\u001B[0m");
    }

    public static void warn(String message, Object... args) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[33m[WARN] " + String.format(message, args) + "\u001B[0m");
    }

    public static void success(String message, Object... args) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[32m[SUCCESS] " + String.format(message, args) + "\u001B[0m");
    }

    public static void debug(String message, Object... args) {
        if (NitroLib.getOptions().isDebug()) System.out.println("\u001B[36m[DEBUG] " + String.format(message, args) + "\u001B[0m");
    }

    public static void verbose(String message, Object... args) {
        if (NitroLib.getOptions().isVerbose()) System.out.println("\u001B[35m[VERBOSE] " + String.format(message, args) + "\u001B[0m");
    }

}
