package dev.grcq.nitrolib.core.cli.options;

import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionParser {

    @Setter
    private static boolean silent = false;

    public static void parse(IOptions instance, String[] args) {
        List<String> usedOptions = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (usedOptions.contains(arg))
                LogUtil.error("Duplicated option: --" + arg, 1);

            boolean found = false;
            for (Field field : instance.getClass().getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option == null) continue;

                String[] names = option.names();
                String[] shortNames = option.shortNames();
                if (names.length == 0 && shortNames.length == 0) throw new IllegalArgumentException("No names provided for option");

                OptionValue value = option.value();
                if (arg.startsWith("--")) {
                    String name = arg.substring(2);
                    if (setField(instance, args, i, arg, field, names, value, name)) {
                        i++;
                    }
                    found = true;
                    break;
                } else if (arg.startsWith("-")) {
                    String name = arg.substring(1);
                    if (setField(instance, args, i, arg, field, shortNames, value, name)) {
                        i++;
                    }
                    found = true;
                    break;
                } else {
                    if (!silent) LogUtil.error("Invalid option: " + arg, 1);
                }
            }

            if (found) usedOptions.add(arg);
        }
    }

    private static boolean setField(IOptions instance, String[] args, int i, String arg, Field field, String[] shortNames, OptionValue value, String name) {
        if (Arrays.asList(shortNames).contains(name)) {
            if (i + 1 >= args.length && value != OptionValue.BOOLEAN) {
                if (!silent) System.out.println("No value provided for option: " + arg);
                return true;
            }

            try {
                field.setAccessible(true);
                if (value == OptionValue.BOOLEAN) {
                    boolean bool = (boolean) field.get(instance);
                    field.set(instance, !bool);
                    return false;
                }

                field.set(instance, value.cast(args[i + 1]));
                return true;
            } catch (IllegalAccessException e) {
                LogUtil.handleException("Failed to set field value", e);
            }
        }
        return true;
    }

    public static String getHelp(IOptions instance) {
        /*
        Format:

        Options:
        --name, -n              The name of the user
        --age, -a               The age of the user
         */

        StringBuilder builder = new StringBuilder();
        for (Field field : instance.getClass().getDeclaredFields()) {
            Option option = field.getAnnotation(Option.class);
            if (option != null) {
                String[] names = option.names();
                String[] shortNames = option.shortNames();
                String description = option.description();

                builder.append("Options: ");
                for (String name : names) {
                    builder.append("--").append(name).append(", ");
                }
                for (String shortName : shortNames) {
                    builder.append("-").append(shortName).append(", ");
                }
                builder.delete(builder.length() - 2, builder.length());

                builder.append("\t\t").append(description).append("\n");
            }
        }
        return builder.toString();
    }
}
