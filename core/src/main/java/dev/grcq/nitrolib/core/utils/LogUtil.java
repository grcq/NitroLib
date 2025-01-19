package dev.grcq.nitrolib.core.utils;

import dev.grcq.nitrolib.core.NitroLib;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtil {

    public static void handleException(Exception e) {
        handleException("An exception occurred", e);
    }

    public static void handleException(Object message, Exception e) {
        handleException(message, e, 5);
    }

    public static void handleException(Exception e, int length) {
        handleException("An exception occurred", e, length);
    }

    public static void handleException(Object message, Exception e, int length, Object ...args) {
        error(message, -1, args);
        error(e.getClass() + ": " + e.getMessage());
        length = Math.min(length, e.getStackTrace().length);
        for (int i = 0; i < length; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            error("    at %s.%s(%s:%d)", -1, element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
        }
    }

    public static void error(Object message) {
        error(message, -1);
    }

    public static void error(Object message, int exit, Object... args) {
        System.out.println("\u001B[31m[ERROR] " + String.format(message.toString(), args) + "\u001B[0m");
        if (exit >= 0) System.exit(exit);
    }

    public static void info(Object message, Object... args) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[34m[INFO] \u001B[37m" + String.format(message.toString(), args) + "\u001B[0m");
    }

    public static void warn(Object message, Object... args) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[33m[WARN] " + String.format(message.toString(), args) + "\u001B[0m");
    }

    public static void success(Object message, Object... args) {
        if (!NitroLib.getOptions().isSilent()) System.out.println("\u001B[32m[SUCCESS] " + String.format(message.toString(), args) + "\u001B[0m");
    }

    public static void debug(Object message, Object... args) {
        if (NitroLib.getOptions().isDebug()) System.out.println("\u001B[36m[DEBUG] " + String.format(message.toString(), args) + "\u001B[0m");
    }

    public static void verbose(Object message, Object... args) {
        if (NitroLib.getOptions().isVerbose()) System.out.println("\u001B[35m[VERBOSE] " + String.format(message.toString(), args) + "\u001B[0m");
    }

}
