package dev.grcq.nitrolib.core.cli.options.types;

import dev.grcq.nitrolib.core.cli.options.Option;
import org.jetbrains.annotations.Nullable;

public interface IOptionParseType<T> {
    @Nullable
    T parse(String value, Option option);
}
