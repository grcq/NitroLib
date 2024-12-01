package dev.grcq.nitrolib.core.cli.options;

import dev.grcq.nitrolib.core.cli.options.types.IOptionParseType;
import dev.grcq.nitrolib.core.cli.options.types.impl.IntegerType;
import dev.grcq.nitrolib.core.cli.options.types.impl.StringType;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.*;

public class OptionParser {

    private static final Map<Class<?>, IOptionParseType<?>> parsers = new HashMap<>();
    static {
        parsers.put(Integer.class, new IntegerType());
        parsers.put(int.class, new IntegerType());
        parsers.put(Long.class, new IntegerType());
        parsers.put(long.class, new IntegerType());
        parsers.put(Double.class, new IntegerType());
        parsers.put(double.class, new IntegerType());
        parsers.put(Float.class, new IntegerType());
        parsers.put(float.class, new IntegerType());
        parsers.put(String.class, new StringType());
    }

    private static int i;

    public static void parse(IOptions instance, String[] args) {
        List<String> usedOptions = new ArrayList<>();
        for (i = 0; i < args.length; i++) {
            String arg = args[i];

            boolean found = false;
            String primaryName = null;
            for (Field field : instance.getClass().getDeclaredFields()) {
                LogUtil.verbose("Checking field: %s", field.getName());
                Option option = field.getAnnotation(Option.class);
                if (option == null) {
                    LogUtil.verbose("Field does not have @Option annotation");
                    continue;
                }

                String[] names = option.names();
                String[] shortNames = option.shortNames();
                if (names.length == 0 && shortNames.length == 0) throw new IllegalArgumentException("No names provided for option");

                OptionValue value = option.value();
                if (arg.startsWith("--")) {
                    String name = arg.substring(2);
                    if (!Arrays.asList(names).contains(name)) continue;
                    primaryName = names[0];
                    setField(instance, option, args, arg, field, value);
                    found = true;
                    break;
                } else if (arg.startsWith("-")) {
                    String name = arg.substring(1);
                    if (!Arrays.asList(shortNames).contains(name)) continue;
                    primaryName = names.length > 0 ? names[0] : null;
                    setField(instance, option, args, arg, field, value);
                    found = true;
                    break;
                } else {
                    LogUtil.error("Invalid option: " + arg, 1);
                }
            }

            if (usedOptions.contains(primaryName)) LogUtil.error("Duplicated option: " + arg, 1);
            if (found) usedOptions.add(primaryName);
        }
    }

    private static void setField(IOptions instance, Option option, String[] args, String arg, Field field, OptionValue value) {
        boolean isBoolean = field.getType().getSimpleName().equalsIgnoreCase("boolean");
        if (i + 1 >= args.length && !isBoolean) {
            LogUtil.error("No value provided for option: " + arg, 1);
            return;
        }

        try {
            field.setAccessible(true);
            if (isBoolean) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    String[] trueValues = new String[] { "true", "yes", "on", "1" };
                    String[] falseValues = new String[] { "false", "no", "off", "0" };
                    String valueStr = args[i + 1].toLowerCase();
                    if (Arrays.asList(trueValues).contains(valueStr)) {
                        LogUtil.verbose("Setting field '%s' to true", field.getName());
                        field.set(instance, true);
                        return;
                    } else if (Arrays.asList(falseValues).contains(valueStr)) {
                        LogUtil.verbose("Setting field '%s' to false", field.getName());
                        field.set(instance, false);
                    } else {
                        LogUtil.error("Invalid value for option: " + arg, 1);
                    }
                    return;
                }

                boolean bool = (boolean) field.get(instance);
                LogUtil.verbose("Setting field '%s' from %s to %s", field.getName(), bool, !bool);
                field.set(instance, !bool);
                return;
            }

            LogUtil.verbose("Parsing value for option: %s", arg);
            IOptionParseType<?> parser = parsers.get(value.getType());
            if (parser == null) {
                LogUtil.error("No parser found for type: " + value.getType().getSimpleName(), 1);
                return;
            }

            StringBuilder builder = new StringBuilder();
            if (args[i + 1].startsWith("\"")) {
                while (!args[i + 1].endsWith("\"")) {
                    i++;
                    if (i >= args.length) {
                        LogUtil.error("Invalid value for flag " + arg + ": " + builder.toString(), 1);
                        return;
                    }
                    builder.append(" ").append(args[i]);
                }

                LogUtil.verbose("Removing quotes from value: %s", builder.toString());
                builder.deleteCharAt(0);
                builder.deleteCharAt(builder.length() - 1);
            } else {
                builder.append(args[i + 1]);
            }

            Object parsedValue = parser.parse(args[i + 1], option);
            if (parsedValue == null) {
                LogUtil.error("Invalid value for flag " + arg + ": " + args[i + 1], 1);
                return;
            }

            LogUtil.verbose("Setting field value: %s", parsedValue);
            field.set(instance, parsedValue);
        } catch (IllegalAccessException e) {
            LogUtil.handleException("Failed to set field value", e);
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
