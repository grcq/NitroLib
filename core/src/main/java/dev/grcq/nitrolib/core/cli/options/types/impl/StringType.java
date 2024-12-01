package dev.grcq.nitrolib.core.cli.options.types.impl;

import dev.grcq.nitrolib.core.cli.options.Option;
import dev.grcq.nitrolib.core.cli.options.types.IOptionParseType;
import org.jetbrains.annotations.Nullable;

public class StringType implements IOptionParseType<String> {

    @Override
    public @Nullable String parse(String value, Option option) {
        return value;
    }
}
