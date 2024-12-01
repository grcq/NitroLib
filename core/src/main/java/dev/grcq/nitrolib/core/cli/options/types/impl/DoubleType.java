package dev.grcq.nitrolib.core.cli.options.types.impl;

import dev.grcq.nitrolib.core.cli.options.Option;
import dev.grcq.nitrolib.core.cli.options.types.IOptionParseType;
import org.jetbrains.annotations.Nullable;

public class DoubleType implements IOptionParseType<Double> {

    @Override
    public @Nullable Double parse(String value, Option option) {
        if (value.contains("e") || value.contains("E")) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
