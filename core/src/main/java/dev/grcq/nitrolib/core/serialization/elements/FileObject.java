package dev.grcq.nitrolib.core.serialization.elements;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileObject extends FileElement implements Iterable<Map.Entry<String, FileElement>> {

    private final Map<String, FileElement> elements;

    public FileObject() {
        this.elements = new HashMap<>();
    }

    public void add(String key, FileElement element) {
        elements.put(key, element);
    }

    public void add(String key, String value) {
        elements.put(key, new FilePrimitive(value));
    }

    public void add(String key, Number value) {
        elements.put(key, new FilePrimitive(value));
    }

    public void add(String key, Boolean value) {
        elements.put(key, new FilePrimitive(value));
    }

    public void add(String key, Character value) {
        elements.put(key, new FilePrimitive(value));
    }

    public FileElement get(String key) {
        return elements.get(key);
    }

    public boolean has(String key) {
        return elements.containsKey(key);
    }

    public String asString(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FilePrimitive)) {
                throw new IllegalStateException("Element is not a FilePrimitive");
            }

            return ((FilePrimitive) element).asString();
        }

        return null;
    }

    public int asInt(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FilePrimitive)) {
                throw new IllegalStateException("Element is not a FilePrimitive");
            }

            return ((FilePrimitive) element).asInt();
        }

        return 0;
    }

    public long asLong(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FilePrimitive)) {
                throw new IllegalStateException("Element is not a FilePrimitive");
            }

            return ((FilePrimitive) element).asLong();
        }

        return 0;
    }

    public float asFloat(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FilePrimitive)) {
                throw new IllegalStateException("Element is not a FilePrimitive");
            }

            return ((FilePrimitive) element).asFloat();
        }

        return 0;
    }

    public double asDouble(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FilePrimitive)) {
                throw new IllegalStateException("Element is not a FilePrimitive");
            }

            return ((FilePrimitive) element).asDouble();
        }

        return 0;
    }

    public Boolean asBoolean(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FilePrimitive)) {
                throw new IllegalStateException("Element is not a FilePrimitive");
            }

            return ((FilePrimitive) element).asBoolean();
        }

        return null;
    }

    public Character asChar(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FilePrimitive)) {
                throw new IllegalStateException("Element is not a FilePrimitive");
            }

            return ((FilePrimitive) element).asChar();
        }

        return null;
    }

    public FileObject asObject(String key) {
        FileElement element = get(key);
        if (element != null) {
            if (!(element instanceof FileObject)) {
                throw new IllegalStateException("Element is not a FileObject");
            }

            return (FileObject) element;
        }

        return null;
    }

    @Override
    public FileElement copy() {
        return null;
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    @Override
    public @NotNull Iterator<Map.Entry<String, FileElement>> iterator() {
        return elements.entrySet().iterator();
    }
}
