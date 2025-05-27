package dev.grcq.nitrolib.core.scripting.lang.runtime.impl;

import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.ValueType;

public class NullValue implements RuntimeValue {

    @Override
    public ValueType getType() {
        return ValueType.NULL;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String toString() {
        return "null";
    }
}
