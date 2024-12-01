package dev.grcq.nitrolib.core.cli.options.types.impl;

import dev.grcq.nitrolib.core.cli.options.Option;
import dev.grcq.nitrolib.core.cli.options.types.IOptionParseType;
import org.jetbrains.annotations.Nullable;

public class IntegerType implements IOptionParseType<Integer> {

    @Override
    public @Nullable Integer parse(String value, Option option)
    {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
