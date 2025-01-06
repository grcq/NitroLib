package dev.grcq.nitrolib.core.serialization.elements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileObject extends FileElement implements Iterable<Map.Entry<String, FileElement>> {

    private final Map<String, FileElement> elements;

    public FileObject() {
        this.elements = new LinkedHashMap<>();
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
        Map<String, FileElement> elements = new HashMap<>();
        for (Map.Entry<String, FileElement> entry : this.elements.entrySet()) {
            elements.put(entry.getKey(), entry.getValue().copy());
        }
        return new FileObject(elements);
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    @Override
    public @NotNull Iterator<Map.Entry<String, FileElement>> iterator() {
        return elements.entrySet().iterator();
    }

    @Override
    public String toJson() {
        return toJson(1);
    }

    @Override
    public String toJson(int indentLevel) {
        StringBuilder builder = new StringBuilder("{\n");
        String indent = StringUtils.repeat("\t", indentLevel);
        String endIndent = StringUtils.repeat("\t", indentLevel - 1);

        for (Map.Entry<String, FileElement> entry : elements.entrySet()) {
            FileElement element = entry.getValue();
            builder.append(indent)
                    .append("\"").append(entry.getKey()).append("\": ")
                    .append(element.toJson(indentLevel + 1))
                    .append(",\n");
        }

        if (!elements.isEmpty()) {
            builder.delete(builder.length() - 2, builder.length());
        }

        builder.append("\n").append(endIndent).append("}");
        return builder.toString();
    }


    @Override
    public String toYaml() {
        return toYaml(0);
    }

    @Override
    public String toYaml(int indentLevel) {
        StringBuilder builder = new StringBuilder();
        String indent = StringUtils.repeat("\t", indentLevel);

        for (Map.Entry<String, FileElement> entry : elements.entrySet()) {
            FileElement element = entry.getValue();
            builder.append(indent)
                    .append(entry.getKey())
                    .append(": ");
            if (element instanceof FileObject) builder.append("\n");
            builder.append(element.toYaml(indentLevel + 1))
                    .append("\n");
        }

        if (!elements.isEmpty()) {
            builder.delete(builder.length() - 1, builder.length());
        }

        return builder.toString();
    }
}
