package dev.grcq.nitrolib.core.scripting.lang.runtime.impl;

import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.ValueType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NumberValue implements RuntimeValue {

    private final double value;

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public ValueType getType() {
        return ValueType.NUMBER;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
