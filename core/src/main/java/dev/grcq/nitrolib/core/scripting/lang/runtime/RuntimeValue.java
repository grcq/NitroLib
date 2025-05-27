package dev.grcq.nitrolib.core.scripting.lang.runtime;

public interface RuntimeValue {

    ValueType getType();

    Object getValue();
}
