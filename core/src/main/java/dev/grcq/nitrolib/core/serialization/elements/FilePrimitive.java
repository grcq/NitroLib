package dev.grcq.nitrolib.core.serialization.elements;

import lombok.Getter;

@Getter
public class FilePrimitive extends FileElement {

    private final Object value;

    public FilePrimitive(String value) {
        this.value = value;
    }

    public FilePrimitive(Boolean value) {
        this.value = value;
    }

    public FilePrimitive(Number value) {
        this.value = value;
    }

    public FilePrimitive(Character value) {
        this.value = value;
    }

    private FilePrimitive(Object value) {
        this.value = value;
    }

    public String asString() {
        if (isString()) {
            return (String) value;
        }

        throw new IllegalStateException("Value is not a string");
    }

    public boolean asBoolean() {
        if (isBoolean()) {
            return (boolean) value;
        }

        throw new IllegalStateException("Value is not a boolean");
    }

    public int asInt() {
        if (isInt()) {
            return (int) value;
        }

        throw new IllegalStateException("Value is not a number");
    }

    public long asLong() {
        if (isLong()) {
            return (long) value;
        }

        throw new IllegalStateException("Value is not a number");
    }

    public float asFloat() {
        if (isFloat()) {
            return (float) value;
        }

        if (isDouble()) {
            return (float) (double) value;
        }

        throw new IllegalStateException("Value is not a number");
    }

    public double asDouble() {
        if (isNumber()) {
            return (double) value;
        }

        throw new IllegalStateException("Value is not a number");
    }

    public char asChar() {
        if (isChar()) {
            return (char) value;
        }

        throw new IllegalStateException("Value is not a character");
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isInt() {
        return value instanceof Integer;
    }

    public boolean isLong() {
        return value instanceof Long;
    }

    public boolean isFloat() {
        return value instanceof Float;
    }

    public boolean isDouble() {
        return value instanceof Double;
    }

    public boolean isChar() {
        return value instanceof Character;
    }

    @Override
    public FileElement copy() {
        return new FilePrimitive(value);
    }

    @Override
    public String toString() {
        if (isString()) return String.format("\"%s\"", value);
        if (isChar()) return String.format("'%s'", value);
        return value.toString();
    }
}
