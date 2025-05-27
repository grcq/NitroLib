package dev.grcq.nitrolib.core.addon;

import dev.grcq.nitrolib.core.utils.ClassUtil;
import dev.grcq.nitrolib.core.utils.LogUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class AddonHandler {

    /**
     * Loads addons from the default "addons" folder.
     * @param mainClass The main class of the application.
     * @param <T> The type of the main class.
     */
    public static <T> void loadAddon(T mainClass) {
        loadAddons(mainClass, "addons");
    }

    /**
     * Loads addons from the specified folder.
     *
     * @param mainClass The main class of the application.
     * @param folderName The folder containing the addon files.
     * @param <T> The type of the main class.
     */
    public static <T> void loadAddons(T mainClass, String folderName) {
        File addonsFolder = new File(folderName);
        if (!addonsFolder.exists()) {
            addonsFolder.mkdirs();
        }
        loadAddons(mainClass, addonsFolder);
    }

    /**
     * Loads addons from the specified folder.
     *
     * @param mainClass The main class of the application.
     * @param addonsFolder The folder containing the addon files.
     * @param <T> The type of the main class.
     */
    public static <T> void loadAddons(T mainClass, File addonsFolder) {
        if (!addonsFolder.exists() || !addonsFolder.isDirectory()) {
            throw new IllegalArgumentException("The specified folder does not exist or is not a directory: " + addonsFolder.getAbsolutePath());
        }

        LogUtil.info("Loading addons from: " + addonsFolder.getAbsolutePath());
        File[] files = addonsFolder.listFiles((dir, name) -> name.endsWith(".jar") || name.endsWith(".zip"));
        if (files == null || files.length == 0) {
            LogUtil.info("No addons found in: " + addonsFolder.getAbsolutePath());
            return;
        }

        LogUtil.info("Found " + files.length + " addons in: " + addonsFolder.getAbsolutePath());
        for (File file : files) {
            try {
                ClassLoader mainClassLoader = mainClass.getClass().getClassLoader();
                URL url = file.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, mainClassLoader);

                boolean isValidAddon = false;
                for (String className : ClassUtil.getClassNamesFromJar(file)) {
                    // should already be loaded by ClassUtil#getClassNamesFromJar, but we need to fetch the class
                    Class<?> clazz = classLoader.loadClass(className);
                    if (AddonEntryPoint.class.isAssignableFrom(clazz)) {
                        @SuppressWarnings("unchecked")
                        AddonEntryPoint<T> entryPoint = (AddonEntryPoint<T>) clazz.getDeclaredConstructor().newInstance();
                        entryPoint.onEnable(mainClass, classLoader);

                        NitroAddon addonAnnotation = clazz.getAnnotation(NitroAddon.class);
                        if (addonAnnotation == null) {
                            LogUtil.warn("No @NitroAddon annotation found on class: " + className);
                            continue;
                        }

                        LogUtil.info("Loaded addon: " + addonAnnotation.name());
                        isValidAddon = true;
                        break;
                    }
                }

                if (!isValidAddon) {
                    LogUtil.warn("No valid addon entry point found in: " + file.getName());
                }
            } catch (Exception e) {
                LogUtil.error("Failed to load addon: " + file.getName(), e);
            }
        }
    }

}
