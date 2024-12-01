package dev.grcq.nitrolib.core.cli.options.types.impl;

import dev.grcq.nitrolib.core.cli.options.Option;
import dev.grcq.nitrolib.core.cli.options.types.IOptionParseType;
import org.jetbrains.annotations.Nullable;

public class FloatType implements IOptionParseType<Float> {

    @Override
    public @Nullable Float parse(String value, Option option) {
        if (value.contains("e") || value.contains("E")) {
            return null;
        }

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
