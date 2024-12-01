package dev.grcq.nitrolib.core.cli.options.types.impl;

import dev.grcq.nitrolib.core.cli.options.Option;
import dev.grcq.nitrolib.core.cli.options.types.IOptionParseType;
import org.jetbrains.annotations.Nullable;

public class LongType implements IOptionParseType<Long> {

    @Override
    public @Nullable Long parse(String value, Option option) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
