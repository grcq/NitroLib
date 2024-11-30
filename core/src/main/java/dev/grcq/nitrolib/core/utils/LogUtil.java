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

    public static void error(String message, int exit) {
        System.out.println("\u001B[31m[ERROR] " + message + "\u001B[0m");
        if (exit >= 0) System.exit(exit);
    }

    public static void info(String message) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[34m[INFO] \u001B[37m" + message + "\u001B[0m");
    }

    public static void warn(String message) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[33m[WARN] " + message + "\u001B[0m");
    }

    public static void success(String message) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[32m[SUCCESS] " + message + "\u001B[0m");
    }

    public static void debug(String message) {
        if (NitroLib.getOptions().isDebug()) System.out.println("\u001B[36m[DEBUG] " + message + "\u001B[0m");
    }

}
