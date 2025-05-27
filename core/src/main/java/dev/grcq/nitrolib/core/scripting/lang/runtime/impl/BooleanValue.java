package dev.grcq.nitrolib.core.scripting.lang.runtime.impl;

import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.ValueType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BooleanValue implements RuntimeValue {

    private final boolean value;

    @Override
    public ValueType getType() {
        return ValueType.BOOLEAN;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
