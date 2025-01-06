package dev.grcq.nitrolib.core.serialization.elements;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

@Getter
public class FileArray extends FileElement implements Iterable<FileElement> {

    private final List<FileElement> elements;

    public FileArray() {
        this.elements = Lists.newArrayList();
    }

    public FileArray(FileElement[] elements) {
        this.elements = Lists.newArrayList(elements);
    }

    public FileArray(List<FileElement> elements) {
        this.elements = elements;
    }

    public void add(FileElement element) {
        elements.add(element);
    }

    public void add(String value) {
        elements.add(new FilePrimitive(value));
    }

    public void add(Number value) {
        elements.add(new FilePrimitive(value));
    }

    public void add(Boolean value) {
        elements.add(new FilePrimitive(value));
    }

    public void add(Character value) {
        elements.add(new FilePrimitive(value));
    }

    public void addAll(FileElement... elements) {
        addAll(Lists.newArrayList(elements));
    }

    public void addAll(List<FileElement> elements) {
        this.elements.addAll(elements);
    }

    public void remove(FileElement element) {
        elements.remove(element);
    }

    public FileElement get(int index) {
        return elements.get(index);
    }

    @Override
    public FileElement copy() {
        List<FileElement> elements = Lists.newArrayList();
        for (FileElement element : this.elements) {
            elements.add(element.copy());
        }
        return new FileArray(elements);
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    @Override
    public @NotNull Iterator<FileElement> iterator() {
        return elements.iterator();
    }

    @Override
    public String toJson() {
        return toJson(1);
    }

    @Override
    public String toJson(int indentLevel) {
        StringBuilder builder = new StringBuilder("[\n");
        String indent = StringUtils.repeat("\t", indentLevel);
        String endIndent = StringUtils.repeat("\t", indentLevel - 1);

        for (FileElement element : elements) {
            builder.append(indent)
                    .append(element.toJson(indentLevel + 1))
                    .append(",\n");
        }

        if (!elements.isEmpty()) {
            builder.delete(builder.length() - 2, builder.length());
        }

        builder.append("\n")
                .append(endIndent)
                .append("]");
        return builder.toString();
    }

    @Override
    public String toYaml() {
        return toYaml(1);
    }

    @Override
    public String toYaml(int indentLevel) {
        StringBuilder builder = new StringBuilder("\n");
        String indent = StringUtils.repeat("\t", indentLevel);

        int index = 0;
        for (FileElement element : elements) {
            if (element instanceof FileArray || element instanceof FileObject) {
                builder.append(indent)
                        .append(index)
                        .append(":");
                if (element instanceof FileObject) builder.append("\n");
                builder.append(element.toYaml(indentLevel + 1))
                        .append("\n");
                index++;
                continue;
            }
            builder.append(indent)
                    .append("- ")
                    .append(element.toYaml(indentLevel + 1))
                    .append("\n");
        }

        if (!elements.isEmpty()) {
            builder.delete(builder.length() - 1, builder.length());
        }

        return builder.toString();
    }
}
