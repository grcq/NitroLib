package dev.grcq.nitrolib.core.scripting.lang.runtime.impl;

import dev.grcq.nitrolib.core.scripting.lang.runtime.RuntimeValue;
import dev.grcq.nitrolib.core.scripting.lang.runtime.ValueType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

@Data
@AllArgsConstructor
public class FunctionValue implements RuntimeValue {

    private final String[] parameters;
    private final Function function;

    @Override
    public ValueType getType() {
        return ValueType.FUNCTION;
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public String toString() {
        return "FunctionValue{" +
                "parameters=" + Arrays.toString(parameters) +
                '}';
    }

    @FunctionalInterface
    public interface Function {
        RuntimeValue call(RuntimeValue... args);
    }

}
