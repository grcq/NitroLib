package dev.grcq.nitrolib.core.cli.options;

import dev.grcq.nitrolib.core.utils.Util;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionParser {

    @Setter
    private static boolean silent = false;

    public static void parse(IOptions instance, String[] args) {
        List<String> commands = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            for (Field field : instance.getClass().getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option == null) continue;

                String[] names = option.names();
                String[] shortNames = option.shortNames();
                if (names.length == 0 && shortNames.length == 0) throw new IllegalArgumentException("No names provided for option");

                OptionValue value = option.value();

                if (arg.startsWith("--")) {
                    String name = arg.substring(2);
                    setField(instance, args, i, arg, field, names, value, name);
                } else if (arg.startsWith("-")) {
                    String name = arg.substring(1);
                    setField(instance, args, i, arg, field, shortNames, value, name);
                } else {
                    commands.add(arg);
                }
            }
        }
    }

    private static void setField(IOptions instance, String[] args, int i, String arg, Field field, String[] shortNames, OptionValue value, String name) {
        if (Arrays.asList(shortNames).contains(name)) {
            if (i + 1 >= args.length && value != OptionValue.BOOLEAN) {
                if (!silent) System.out.println("No value provided for option: " + arg);
                return;
            }

            try {
                field.setAccessible(true);
                if (value == OptionValue.BOOLEAN) {
                    boolean bool = (boolean) field.get(instance);
                    field.set(instance, !bool);
                    return;
                }

                field.set(instance, value.getType().cast(args[i + 1]));
            } catch (IllegalAccessException e) {
                Util.handleException("Failed to set field value", e);
            }
        }
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
