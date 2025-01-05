package dev.grcq.nitrolib.core.serialization.elements;

import com.google.common.collect.Lists;
import lombok.Getter;
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

    public void remove(FileElement element) {
        elements.remove(element);
    }

    public FileElement get(int index) {
        return elements.get(index);
    }

    @Override
    public FileElement copy() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public @NotNull Iterator<FileElement> iterator() {
        return elements.iterator();
    }
}
