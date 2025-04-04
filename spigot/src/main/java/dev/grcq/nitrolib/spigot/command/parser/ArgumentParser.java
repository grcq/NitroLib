package dev.grcq.nitrolib.spigot.command.parser;

import com.google.common.collect.Lists;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.command.NitroCommandHandler;
import dev.grcq.nitrolib.spigot.command.annotations.Arg;
import dev.grcq.nitrolib.spigot.command.annotations.Flag;
import dev.grcq.nitrolib.spigot.command.annotations.FlagValue;
import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import dev.grcq.nitrolib.spigot.command.parser.data.ArgData;
import dev.grcq.nitrolib.spigot.command.parser.data.FlagData;
import dev.grcq.nitrolib.spigot.command.parser.data.FlagValueData;
import dev.grcq.nitrolib.spigot.command.parser.data.IData;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ArgumentParser {

    private final Parameter[] parameters;

    public List<IData> parse(CommandSender sender, String[] args) {
        List<IData> data = new ArrayList<>();

        List<String> flags = Lists.newArrayList();
        List<String> arguments = Lists.newArrayList(args);
        LogUtil.debug("Parsing %s %b", arguments, arguments.isEmpty());
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(Flag.class)) {
                Flag flag = param.getAnnotation(Flag.class);
                String flagName = "-" + flag.value();

                boolean found = arguments.removeIf(arg -> arg.equalsIgnoreCase(flagName));
                if (found) flags.add(flag.value());
                data.add(new FlagData(flagName, found));
                continue;
            }

            if (param.isAnnotationPresent(FlagValue.class)) {
                FlagValue flagValue = param.getAnnotation(FlagValue.class);
                String flagName = "-" + flagValue.name();
                String argName = flagValue.arg();

                int index = arguments.indexOf(flagName);
                boolean found = arguments.removeIf(arg -> arg.equalsIgnoreCase(flagName));
                if (found) {
                    if (index >= arguments.size()) {
                        if (flagValue.required() && flagValue.def().isEmpty()) return new ArrayList<>();
                        data.add(new FlagValueData(flagName, argName, flagValue.def().isEmpty() ? null : flagValue.def()));
                        continue;
                    }

                    String value = arguments.remove(index);
                    if (value.startsWith("\"")) {
                        if (value.endsWith("\"")) value = value.substring(1, value.length() - 1);
                        else {
                            StringBuilder builder = new StringBuilder(value);
                            while (!builder.toString().endsWith("\"")) {
                                if (arguments.isEmpty()) {
                                    LogUtil.error("Invalid parameter. Ensure all parameters are annotated with either @Arg, @Flag, or @FlagValue.");
                                    return new ArrayList<>();
                                }

                                builder.append(" ").append(arguments.remove(0));
                            }

                            value = builder.substring(1, builder.length() - 1);
                        }

                        value = value.trim();
                    }

                    if (!flagValue.pattern().isEmpty() && !value.matches(flagValue.pattern())) {
                        return null;
                    }

                    Object parsed = parse(sender, flags.toArray(new String[0]), value, param.getType());
                    if (parsed == null) return null;

                    data.add(new FlagValueData(flagName, argName, parsed));
                    continue;
                }

                data.add(new FlagValueData(flagName, argName, null));
                continue;
            }

            if (param.isAnnotationPresent(Arg.class)) {
                Arg arg = param.getAnnotation(Arg.class);
                String argName = arg.value();

                String argValue;
                boolean required = arg.required();
                if (arguments.isEmpty()) {
                    if (arg.required() && arg.def().isEmpty()) return new ArrayList<>();

                    required = false;
                    argValue = arg.def();
                } else {
                    argValue = arguments.remove(0);
                    if (argValue.startsWith("\"")) {
                        if (argValue.endsWith("\"")) argValue = argValue.substring(1, argValue.length() - 1);
                        else {
                            StringBuilder builder = new StringBuilder(argValue);
                            while (!builder.toString().endsWith("\"")) {
                                if (arguments.isEmpty()) {
                                    LogUtil.error("Invalid parameter. Ensure all parameters are annotated with either @Arg, @Flag, or @FlagValue.");
                                    return new ArrayList<>();
                                }

                                builder.append(" ").append(arguments.remove(0));
                            }

                            argValue = builder.substring(1, builder.length() - 1);
                        }

                        argValue = argValue.trim();
                    } else {
                        if (arg.wildcard()) {
                            StringBuilder builder = new StringBuilder(argValue);
                            while (!arguments.isEmpty()) {
                                builder.append(" ").append(arguments.remove(0));
                            }

                            argValue = builder.toString();
                        }
                    }
                }

                if (!arg.pattern().isEmpty() && !argValue.matches(arg.pattern())) {
                    return null;
                }

                Object parsed = parse(sender, flags.toArray(new String[0]), argValue, param.getType());
                if (parsed == null) return null;

                data.add(new ArgData(argName, parsed, required));
                continue;
            }

            LogUtil.error("Invalid parameter. Ensure all parameters are annotated with either @Arg, @Flag, or @FlagValue.");
        }

        if (!arguments.isEmpty()) data.add(null);
        return data;
    }

    private Object parse(CommandSender sender, String[] flags, String arg, Class<?> type) {
        TypeParameter<?> parameter = NitroCommandHandler.getTypeParameters().get(type);
        if (parameter == null) {
            LogUtil.error("No parameter found for type " + type.getName());
            return null;
        }

        return parameter.parse(sender, flags, arg);
    }

    private enum ParseValue {
        NONE
    }

}
