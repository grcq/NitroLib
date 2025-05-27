package dev.grcq.nitrolib.core.scripting.lang.runtime.impl;

import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.ValueType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StringValue implements RuntimeValue {

    private final String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
